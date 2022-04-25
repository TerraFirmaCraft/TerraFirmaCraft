/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.common.items.VesselItem;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgingHandler implements IForging
{
    private final LazyOptional<IForging> capability;
    private final ItemStack stack;

    private final ForgeSteps steps;

    private int work;
    @Nullable private AnvilRecipe recipe;
    @Nullable private ResourceLocation uninitializedRecipe;

    private boolean initialized;

    public ForgingHandler(ItemStack stack)
    {
        this.capability = LazyOptional.of(() -> this);
        this.stack = stack;

        this.work = 0;
        this.recipe = null;
        this.steps = new ForgeSteps();
    }

    public ItemStack getContainer()
    {
        return stack;
    }

    @Override
    public int getWork()
    {
        return work;
    }

    @Override
    public void setWork(int work)
    {
        this.work = work;
        save();
    }

    @Nullable
    @Override
    public AnvilRecipe getRecipe(Level level)
    {
        if (uninitializedRecipe != null)
        {
            uninitializedRecipe = null;
            recipe = Helpers.getRecipes(level, TFCRecipeTypes.ANVIL).get(uninitializedRecipe);
        }
        return recipe;
    }

    @Override
    public void setRecipe(@Nullable AnvilRecipe recipe)
    {
        this.recipe = recipe;
        save();
    }

    @Override
    public ForgeSteps getSteps()
    {
        return steps;
    }

    @Override
    public boolean matches(ForgeRule rule)
    {
        return rule.matches(steps);
    }

    @Override
    public void addStep(@Nullable ForgeStep step)
    {
        steps.addStep(step);
        save();
    }

    @Override
    public void reset()
    {
        save();
    }

    /**
     * @see VesselItem.VesselCapability#load()
     */
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == ForgingCapability.CAPABILITY)
        {
            load();
            return capability.cast();
        }
        return LazyOptional.empty();
    }

    private void load()
    {
        if (!initialized)
        {
            initialized = true;

            final CompoundTag tag = stack.getTagElement("tfc:forging");
            if (tag != null)
            {
                work = tag.getInt("work");
                steps.read(tag);
                uninitializedRecipe = tag.contains("recipe") ? new ResourceLocation(tag.getString("recipe")) : null;
                recipe = null;
            }
        }
    }

    private void save()
    {
        if (work == 0 && !steps.any() && recipe == null)
        {
            // No defining data, so don't save anything
            stack.removeTagKey("tfc:forging");
        }
        else
        {
            final CompoundTag tag = stack.getOrCreateTagElement("tfc:forging");
            tag.putInt("work", work);
            steps.write(tag);

            if (recipe != null)
            {
                tag.putString("recipe", recipe.getId().toString());
            }
        }
    }
}