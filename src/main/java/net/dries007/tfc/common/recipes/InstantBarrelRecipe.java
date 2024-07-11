/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.function.Function;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.recipes.input.BarrelInventory;

public class InstantBarrelRecipe extends BarrelRecipe
{
    public static final MapCodec<InstantBarrelRecipe> CODEC = BarrelRecipe.CODEC.xmap(InstantBarrelRecipe::new, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, InstantBarrelRecipe> STREAM_CODEC = BarrelRecipe.STREAM_CODEC.map(InstantBarrelRecipe::new, Function.identity());

    public InstantBarrelRecipe(BarrelRecipe parent)
    {
        super(parent);
    }

    @Override
    public boolean matches(BarrelInventory container)
    {
        return super.matches(container) && moreItemsThanFluid(container);
    }

    /**
     * Instant recipes must have more item inputs than fluid in order to be valid. If one of input fluid or input item doesn't exist, this
     * measure is always true. If there is no output fluid, we can always complete the recipe as the fluid content won't change.
     */
    private boolean moreItemsThanFluid(BarrelInventory input)
    {
        return inputItem.isEmpty()
            || outputFluid.isEmpty()
            || input.getFluidInTank(0).getAmount() / inputFluid.amount() <= input.getStackInSlot(BarrelBlockEntity.SLOT_ITEM).getCount() / inputItem.get().count();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.INSTANT_BARREL.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.BARREL_INSTANT.get();
    }
}
