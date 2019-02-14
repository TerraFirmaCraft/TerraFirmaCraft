/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.recipes.AnvilRecipe;
import net.dries007.tfc.api.recipes.WeldingRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.network.PacketAnvilUpdate;
import net.dries007.tfc.util.ITileFields;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.forge.ForgeStep;
import net.dries007.tfc.util.forge.ForgeSteps;

@ParametersAreNonnullByDefault
public class TEAnvilTFC extends TEInventory implements ITileFields
{
    public static final int WORK_MAX = 145;

    public static final int SLOT_INPUT_1 = 0;
    public static final int SLOT_INPUT_2 = 1;
    public static final int SLOT_HAMMER = 2;
    public static final int SLOT_FLUX = 3;

    public static final int FIELD_PROGRESS = 0;
    public static final int FIELD_TARGET = 1;

    /*
     Properties of the anvil
     */
    private final Metal.Tier tier;
    private final boolean isStone;

    /*
     Instance variables
     */
    private AnvilRecipe recipe;
    private ForgeSteps steps;
    private int workingProgress = 0; // Min = 0, Max = 145
    private int workingTarget = 0;

    public TEAnvilTFC()
    {
        this(Metal.Tier.TIER_I, false);
    }

    public TEAnvilTFC(Metal.Tier tier, boolean isStone)
    {
        super(4);

        steps = new ForgeSteps();
        recipe = null;
        this.tier = tier;
        this.isStone = isStone;
    }

    public boolean isStone()
    {
        return isStone;
    }

    @Nonnull
    public Metal.Tier getTier()
    {
        return tier;
    }

    @Nullable
    public AnvilRecipe getRecipe()
    {
        return recipe;
    }

    @Nonnull
    public ForgeSteps getSteps()
    {
        return steps;
    }

    /**
     * Sets the current steps on client side after recieving a {@link PacketAnvilUpdate}
     *
     * @param steps the new steps
     */
    public void setSteps(ForgeSteps steps)
    {
        this.steps = steps;
    }

    /**
     * Sets the anvil TE recipe, called after pressing the recipe button
     * This is the ONLY place that should write to {@link this#recipe}
     *
     * @param recipe       the recipe to set to
     * @return true if a packet needs to be sent to the client for a recipe update
     */
    public boolean setRecipe(@Nullable AnvilRecipe recipe)
    {
        ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_1);
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        boolean recipeChanged;

        if (cap != null && recipe != null)
        {
            // Update recipe in both
            recipeChanged = this.recipe != recipe;
            cap.setRecipe(recipe);
            this.recipe = recipe;

            // Update server side fields
            workingProgress = cap.getWork();
            steps = cap.getSteps().copy();

            workingTarget = recipe.getTarget(world.getSeed());
        }
        else
        {
            // Set recipe to null because either item is missing, or it was requested
            recipeChanged = this.recipe != null;
            this.recipe = null;
            if (cap != null)
            {
                cap.reset();
            }
            resetFields();
        }

        return recipeChanged;
    }

    /**
     * Slot updates only happen on server side, so update recipe when change is made
     * @param slot a slot id, or -1 if triggered by other methods
     */
    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        if (!world.isRemote)
        {
            if (checkRecipeUpdate())
            {
                TerraFirmaCraft.getNetwork().sendToDimension(new PacketAnvilUpdate(this), world.provider.getDimension());
            }
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return slot == SLOT_FLUX ? super.getSlotLimit(slot) : 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        switch (slot)
        {
            case SLOT_INPUT_1:
            case SLOT_INPUT_2:
                return stack.hasCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null) && stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            case SLOT_FLUX:
                return OreDictionaryHelper.doesStackMatchOre(stack, "dustFlux");
            case SLOT_HAMMER:
                return OreDictionaryHelper.doesStackMatchOre(stack, "hammer");
            default:
                return false;
        }
    }

    /**
     * Only occurs on server side
     */
    public void addStep(@Nullable ForgeStep step)
    {
        ItemStack input = inventory.getStackInSlot(SLOT_INPUT_1);
        IForgeable cap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);

        if (cap != null)
        {
            TerraFirmaCraft.getLog().info("Adding step: cap {}, recipe {}", cap.serializeNBT(), recipe);
            // Add step to stack + tile
            cap.addStep(step);
            steps = cap.getSteps().copy();
            if (step != null)
            {
                workingProgress += step.getStepAmount();
            }

            // Handle possible recipe completion
            if (recipe != null)
            {
                if (workingProgress == workingTarget && recipe.matches(steps))
                {
                    // Consume input + produce output / throw it in the world
                    ItemStack stack = recipe.getOutput();
                    inventory.setStackInSlot(SLOT_INPUT_1, stack);
                    world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);

                    // Reset forge stuff
                    resetFields();
                    setRecipe(null);
                }
                else if (workingProgress < 0 || workingProgress >= 150)
                {
                    // Consume input, produce no output
                    inventory.setStackInSlot(SLOT_INPUT_1, ItemStack.EMPTY);
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                }
            }

            // Step was added, so send update regardless
            TerraFirmaCraft.getNetwork().sendToDimension(new PacketAnvilUpdate(this), world.provider.getDimension());
        }
    }

    @Override
    public int getFieldCount()
    {
        return 2;
    }

    @Override
    public void setField(int index, int value)
    {
        switch (index)
        {
            case FIELD_PROGRESS:
                workingProgress = value;
                break;
            case FIELD_TARGET:
                workingTarget = value;
                break;
            default:
                TerraFirmaCraft.getLog().warn("Invalid field id {}", index);
        }
    }

    @Override
    public int getField(int index)
    {
        switch (index)
        {
            case FIELD_PROGRESS:
                return workingProgress;
            case FIELD_TARGET:
                return workingTarget;
            default:
                TerraFirmaCraft.getLog().warn("Invalid field id {} in TEAnvilTFC#getField", index);
                return 0;
        }
    }

    /**
     * Attempts to weld the two items in the input slots.
     *
     * @return true if the weld was successful
     */
    public boolean attemptWelding()
    {
        ItemStack input1 = inventory.getStackInSlot(SLOT_INPUT_1), input2 = inventory.getStackInSlot(SLOT_INPUT_2);
        if (input1.isEmpty() || input2.isEmpty())
        {
            return false;
        }

        // Find a matching welding recipe
        WeldingRecipe recipe = TFCRegistries.WELDING.getValuesCollection().stream().filter(x -> x.matches(input1, input2)).findFirst().orElse(null);
        if (recipe != null)
        {
            // Execute recipe!
            IForgeable heat1 = input1.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
            IForgeable heat2 = input2.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
            if (heat1 == null || heat2 == null || !heat1.isWeldable() || !heat2.isWeldable())
            {
                // No heat capability or not hot enough
                return false;
            }
            ItemStack result = recipe.getOutput();
            IItemHeat heatResult = result.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            float resultTemperature = Math.min(heat1.getTemperature(), heat2.getTemperature());
            if (heatResult != null)
            {
                // Every welding result should have this capability, but don't fail if it doesn't
                heatResult.setTemperature(resultTemperature);
            }

            // Set stacks in slots
            inventory.setStackInSlot(SLOT_INPUT_1, result);
            inventory.setStackInSlot(SLOT_INPUT_2, ItemStack.EMPTY);

            return true;
        }
        return false;
    }

    private boolean checkRecipeUpdate()
    {
        ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_1);
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        boolean shouldSendUpdate = false;
        if (cap == null && recipe != null)
        {
            // Check for item removed / broken
            shouldSendUpdate = setRecipe(null);
        }
        else if (cap != null)
        {
            // Check for mismatched recipe
            AnvilRecipe capRecipe = TFCRegistries.ANVIL.getValue(cap.getRecipeName());
            if (capRecipe != recipe)
            {
                shouldSendUpdate = setRecipe(capRecipe);
            }
        }
        return shouldSendUpdate;
    }

    private void resetFields()
    {
        workingProgress = 0;
        workingTarget = 0;
        steps.reset();
    }
}
