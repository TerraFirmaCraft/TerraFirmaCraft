/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.capabilities.MoldLike;
import org.jetbrains.annotations.Nullable;

public class CastingCraftingRecipe extends CustomRecipe implements ISimpleRecipe<CraftingContainer>
{
    public CastingCraftingRecipe(ResourceLocation id)
    {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer inventory, @Nullable Level level)
    {
        final MoldLike mold = getMold(inventory);
        return mold != null && !mold.isMolten() && CastingRecipe.get(mold) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory)
    {
        final MoldLike mold = getMold(inventory);
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
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack item = inv.getItem(i);
            final MoldLike mold = MoldLike.get(item);
            if (!item.isEmpty() && mold != null)
            {
                final CastingRecipe recipe = CastingRecipe.get(mold);
                if (recipe != null)
                {
                    final Player player = ForgeHooks.getCraftingPlayer();
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
    private MoldLike getMold(CraftingContainer inventory)
    {
        MoldLike mold = null;
        for (int i = 0; i < inventory.getContainerSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty())
            {
                if (mold == null)
                {
                    mold = MoldLike.get(stack);
                    if (mold == null)
                    {
                        return null; // stack that's not a mold
                    }
                }
                else
                {
                    return null; // more than one non-empty stack
                }
            }
        }
        return mold;
    }
}
