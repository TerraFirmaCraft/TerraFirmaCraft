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
import net.dries007.tfc.api.recipes.AnvilRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.network.PacketAnvilRecipe;
import net.dries007.tfc.util.ITileFields;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.forge.ForgeRule;
import net.dries007.tfc.util.forge.ForgeStep;
import net.dries007.tfc.util.forge.ForgeSteps;

import static net.dries007.tfc.util.forge.ForgeRule.*;

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
    public static final int FIELD_LAST_STEP = 2;
    public static final int FIELD_SECOND_STEP = 3;
    public static final int FIELD_THIRD_STEP = 4;
    public static final int FIELD_FIRST_RULE = 5;
    public static final int FIELD_SECOND_RULE = 6;
    public static final int FIELD_THIRD_RULE = 7;

    private AnvilRecipe recipe;
    private ForgeSteps steps;
    private ForgeRule[] rules;
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
        rules = new ForgeRule[] {HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST};
        recipe = null;
        this.tier = tier;
    }

    @Nonnull
    public Metal.Tier getTier()
    {
        return tier;
    }

    @Nonnull
    public ForgeSteps getSteps()
    {
        return steps;
    }

    @Nullable
    public AnvilRecipe getRecipe()
    {
        return recipe;
    }

    public void debug()
    {
        TerraFirmaCraft.getLog().debug("Anvil Status: Work: {}, Target: {}", workingProgress, workingTarget);
    }

    public void setRecipe(@Nullable AnvilRecipe recipe)
    {
        this.recipe = recipe;
        ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_1);
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (cap != null)
        {
            cap.setRecipe(recipe);
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);

        if (world.isRemote)
            return;

        ItemStack stack = inventory.getStackInSlot(SLOT_INPUT_1);
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);

        if (cap != null)
        {
            if (recipe == null || !recipe.matches(stack))
            {
                // no current recipe or recipe exists but doesn't match input
                // in both cases, reset the recipe based off the stack info
                updateRecipe(TFCRegistries.ANVIL.getValue(cap.getRecipeName()));
                if (recipe == null)
                {
                    // for some reason the stack has an invalid recipe name
                    updateRecipe(AnvilRecipe.getFirstFor(stack));
                    if (recipe != null)
                    {
                        cap.setRecipe(recipe);
                    }
                }
                if (recipe == null)
                {
                    // no current recipe
                    resetFields();
                    updateRecipe(null);

                    cap.reset();
                    return;
                }
            }

            // at this point, the recipe is valid, but may have changed
            // update server side fields
            workingProgress = cap.getWork();
            steps = cap.getSteps().copy();

            workingTarget = recipe.getTarget(world.getSeed());
            rules = recipe.getRules();

            cap.setRecipe(recipe);
        }
        else
        {
            // cap was null, most likely if the slot was empty
            resetFields();
            updateRecipe(null);
        }
    }

    public void addStep(@Nullable ForgeStep step)
    {
        // This is only called on server
        ItemStack input = inventory.getStackInSlot(SLOT_INPUT_1);
        IForgeable cap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);

        if (cap != null)
        {
            TerraFirmaCraft.getLog().info("Adding step: cap {}", cap.serializeNBT());
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

            // update recipe
            setAndUpdateSlots(0);
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

    @Override
    public int getFieldCount()
    {
        return 8;
    }

    @Override
    public void setField(int index, int value)
    {
        TerraFirmaCraft.getLog().info("Received a field, {} {}", index, value);
        switch (index)
        {
            case FIELD_PROGRESS:
                TerraFirmaCraft.getLog().info("Setting progress field {}", value);
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
            case FIELD_FIRST_RULE:
            case FIELD_SECOND_RULE:
            case FIELD_THIRD_RULE:
                rules[index - FIELD_FIRST_RULE] = ForgeRule.valueOf(value);
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
            case FIELD_FIRST_RULE:
            case FIELD_SECOND_RULE:
            case FIELD_THIRD_RULE:
                if (index - FIELD_FIRST_RULE >= rules.length)
                    return -1;
                return ForgeRule.getID(rules[index - FIELD_FIRST_RULE]);
            default:
                TerraFirmaCraft.getLog().warn("Invalid field id {} in TEAnvilTFC#getField", index);
                return 0;
        }
    }

    private void updateRecipe(@Nullable AnvilRecipe recipe)
    {
        // Called on server
        setRecipe(recipe);
        TerraFirmaCraft.getNetwork().sendToDimension(new PacketAnvilRecipe(this), world.provider.getDimension());
    }

    private void resetFields()
    {
        if (!world.isRemote)
        {
            workingProgress = 0;
            workingTarget = 0;
            steps.reset();
            rules = new ForgeRule[3];
        }
    }
}
