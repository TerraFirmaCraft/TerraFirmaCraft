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
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.recipes.AnvilRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.network.PacketAnvilRecipe;
import net.dries007.tfc.util.ITileFields;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.forge.ForgeStep;
import net.dries007.tfc.util.forge.ForgeSteps;

@ParametersAreNonnullByDefault
public class TEAnvilTFC extends TEInventory implements ITileFields, ITickable
{
    public static final int WORK_MAX = 145;

    public static final int SLOT_INPUT_1 = 0;
    public static final int SLOT_INPUT_2 = 1;
    public static final int SLOT_HAMMER = 2;
    public static final int SLOT_FLUX = 3;

    public static final int FIELD_COUNT = 5;
    public static final int FIELD_PROGRESS = 0;
    public static final int FIELD_TARGET = 1;
    public static final int FIELD_LAST_STEP = 2;
    public static final int FIELD_SECOND_STEP = 3;
    public static final int FIELD_THIRD_STEP = 4;

    private AnvilRecipe recipe;
    private ForgeSteps steps;
    private int workingProgress = 0; // Min = 0, Max = 145
    private int workingTarget = 0;
    private final Metal.Tier tier;

    public TEAnvilTFC()
    {
        this(Metal.Tier.TIER_I);
    }

    public TEAnvilTFC(Metal.Tier tier)
    {
        super(4);

        steps = new ForgeSteps();
        recipe = null;
        this.tier = tier;
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

    @Override
    public void update()
    {
        // todo: remove
        // This is just for testing
        if (world.getTotalWorldTime() % 40 == 0)
            TerraFirmaCraft.getLog().info("Anvil Status: Recipe: {} Work: {}, Target: {}, Steps: {}", recipe == null ? "NULL" : recipe.getInput() + " -> " + recipe.getOutput(), workingProgress, workingTarget, steps == null ? "NULL" : steps.serializeNBT());
    }

    public ForgeSteps getSteps()
    {
        return steps;
    }

    /**
     * Sets the anvil TE recipe, called after pressing the recipe button
     * This is the ONLY place that should write to {@link this#recipe}
     *
     * @param recipe       the recipe to set to
     * @param sendToClient should it send an update to client?
     */
    public void setRecipe(@Nullable AnvilRecipe recipe, boolean sendToClient)
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

        if (recipeChanged && sendToClient)
        {
            // Send the update to client
            TerraFirmaCraft.getNetwork().sendToDimension(new PacketAnvilRecipe(this), world.provider.getDimension());
        }
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
            checkRecipeUpdate();
        }
    }

    /**
     * Only occurs on server side
     */
    public void addStep(@Nullable ForgeStep step)
    {
        // Always check for update first
        checkRecipeUpdate();

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
                    setRecipe(null, true);
                }
                else if (workingProgress < 0 || workingProgress >= 150)
                {
                    // Consume input, produce no output
                    inventory.setStackInSlot(SLOT_INPUT_1, ItemStack.EMPTY);
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                }
            }

            // update recipe
            setAndUpdateSlots(0);
        }
    }

    @Override
    public int getFieldCount()
    {
        return FIELD_COUNT;
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

    private void checkRecipeUpdate()
    {
        ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_1);
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (cap == null && recipe != null)
        {
            // Check for item removed / broken
            setRecipe(null, true);
        }
        else if (cap != null)
        {
            // Check for mismatched recipe
            AnvilRecipe capRecipe = TFCRegistries.ANVIL.getValue(cap.getRecipeName());
            if (capRecipe != recipe)
            {
                setRecipe(capRecipe, true);
            }
        }
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
            case FIELD_LAST_STEP:
            case FIELD_SECOND_STEP:
            case FIELD_THIRD_STEP:
                steps.setStep(index, value);
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
            case FIELD_LAST_STEP:
            case FIELD_SECOND_STEP:
            case FIELD_THIRD_STEP:
                return steps.getStepByID(index);
            default:
                TerraFirmaCraft.getLog().warn("Invalid field id {} in TEAnvilTFC#getField", index);
                return 0;
        }
    }

    private void resetFields()
    {
        workingProgress = 0;
        workingTarget = 0;
        steps.reset();
    }
}
