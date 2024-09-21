/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.mold.IMold;

public class CastingCraftingRecipe extends CustomRecipe
{
    public static final CastingCraftingRecipe INSTANCE = new CastingCraftingRecipe();

    private CastingCraftingRecipe()
    {
        super(CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingInput input, Level level)
    {
        final IMold mold = getMold(input);
        return mold != null && !mold.isMolten() && CastingRecipe.get(mold) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        final IMold mold = getMold(input);
        if (mold != null)
        {
            final CastingRecipe recipe = CastingRecipe.get(mold);
            if (recipe != null)
            {
                return recipe.assemble(mold);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < input.size(); i++)
        {
            ItemStack item = input.getItem(i);
            final IMold mold = IMold.get(item);
            if (!item.isEmpty() && mold != null)
            {
                final CastingRecipe recipe = CastingRecipe.get(mold);
                if (recipe != null)
                {
                    final @Nullable Player player = RecipeHelpers.getCraftingPlayer();

                    // greater than the break chance == we should keep the item
                    if (player != null && player.getRandom().nextFloat() > recipe.getBreakChance())
                    {
                        mold.drainIgnoringTemperature(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                        items.set(i, item.copy());
                    }
                }
            }
        }
        return items;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.CASTING_CRAFTING.get();
    }

    /**
     * @return The single mold in the crafting container, if one and only exactly one can be found, paired with its ItemStack, otherwise null.
     */
    @Nullable
    private IMold getMold(CraftingInput input)
    {
        IMold mold = null;
        for (int i = 0; i < input.size(); i++)
        {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty())
            {
                if (mold == null)
                {
                    mold = IMold.get(stack);
                    if (mold == null)
                    {
                        return null; // unsealedStack that's not a mold
                    }
                }
                else
                {
                    return null; // more than one non-empty unsealedStack
                }
            }
        }
        return mold;
    }
}
