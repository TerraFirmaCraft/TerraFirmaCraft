/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.Helpers;

/**
 * A capability instance which is attached to all items, in order to store (cached) and manipulate anvil working/forging data.
 * This instance is lazily initialized upon first getCapability() query, and saves all data directly to the stack tag.
 */
public final class Forging implements ICapabilityProvider
{
    private static final String KEY = "tfc:forging";

    private final LazyOptional<Forging> capability = LazyOptional.of(() -> this);;
    private final ItemStack stack;

    private final ForgeSteps steps;

    private int work, target;
    @Nullable private AnvilRecipe recipe;
    @Nullable private ResourceLocation uninitializedRecipe;

    public Forging(ItemStack stack)
    {
        this.stack = stack;

        this.work = 0;
        this.recipe = null;
        this.steps = new ForgeSteps();

        load();
    }

    /**
     * @return the current work value, in the range [0, {@link ForgeStep#LIMIT}]
     */
    public int getWork()
    {
        return work;
    }

    /**
     * @return the work target for the current selected recipe, if present, or -1 if there is no recipe.
     */
    public int getWorkTarget()
    {
        return target == -1 ? 0 : target;
    }

    /**
     * Gets the current recipe.
     * This requires a level as it possibly makes a recipe query, in the first load from saved data.
     */
    @Nullable
    public AnvilRecipe getRecipe(Level level)
    {
        if (uninitializedRecipe != null)
        {
            recipe = Helpers.getRecipes(level, TFCRecipeTypes.ANVIL).get(uninitializedRecipe);
            uninitializedRecipe = null;
        }
        return recipe;
    }

    /**
     * Sets the current recipe and work target based on the provided anvil recipe and anvil inventory.
     * <strong>Important:</strong> should generally only be called on server, where the inventory seed is known.
     */
    public void setRecipe(@Nullable AnvilRecipe recipe, AnvilRecipe.Inventory inventory)
    {
        setRecipe(recipe, recipe == null ? -1 : recipe.computeTarget(inventory));
    }

    /**
     * Sets the current recipe and work target directly.
     */
    public void setRecipe(@Nullable AnvilRecipe recipe, int target)
    {
        this.recipe = recipe;
        this.target = target;
        save();
    }

    public ForgeSteps getSteps()
    {
        return steps;
    }

    public boolean matches(ForgeRule[] rules)
    {
        for (ForgeRule rule : rules)
        {
            if (!matches(rule))
            {
                return false;
            }
        }
        return true;
    }

    public boolean matches(ForgeRule rule)
    {
        return rule.matches(steps);
    }

    public void addStep(@Nullable ForgeStep step)
    {
        steps.addStep(step);
        if (step != null)
        {
            work += step.step();
        }
        save();
    }

    public void addStep(@Nullable ForgeStep step, int amount)
    {
        steps.addStep(step);
        work += amount;
        save();
    }

    /**
     * This will clear the current recipe, if the item has not been additionally worked.
     * Used when removing an item from an anvil, as it makes the item stackable again - despite the fact we <strong>must</strong> persist the recipe on the item stack, even if it has not been worked.
     */
    public void clearRecipeIfNotWorked()
    {
        if (!steps.any())
        {
            recipe = null;
            uninitializedRecipe = null;
        }
        save();
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == ForgingCapability.CAPABILITY)
        {
            return capability.cast();
        }
        return LazyOptional.empty();
    }

    private void load()
    {
        final CompoundTag tag = stack.getTagElement(KEY);
        if (tag != null)
        {
            work = tag.getInt("work");
            target = tag.getInt("target");

            steps.read(tag);

            uninitializedRecipe = tag.contains("recipe", Tag.TAG_STRING) ? Helpers.resourceLocation(tag.getString("recipe")) : null;
            recipe = null;
        }
    }

    private void save()
    {
        if (!steps.any() && recipe == null && uninitializedRecipe == null)
        {
            // No defining data, so don't save anything
            stack.removeTagKey(KEY);
        }
        else
        {
            final CompoundTag tag = stack.getOrCreateTagElement(KEY);

            tag.putInt("work", work);
            tag.putInt("target", target);

            steps.write(tag);

            if (recipe != null)
            {
                tag.putString("recipe", recipe.getId().toString());
            }
            else if (uninitializedRecipe != null)
            {
                tag.putString("recipe", uninitializedRecipe.toString());
            }
        }
    }
}