/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.recipes.RecipeHelpers;

public record ExtraProductModifier(ItemStack stack) implements ItemStackModifier
{
    public static final MapCodec<ExtraProductModifier> CODEC = ItemStack.CODEC.fieldOf("stack").xmap(ExtraProductModifier::of, ExtraProductModifier::stack);
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtraProductModifier> STREAM_CODEC = ItemStack.STREAM_CODEC.map(ExtraProductModifier::of, ExtraProductModifier::stack);

    public static ExtraProductModifier of(ItemStack stack)
    {
        return new ExtraProductModifier(FoodCapability.setTransientNonDecaying(stack));
    }

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        final @Nullable Player player = RecipeHelpers.getCraftingPlayer();
        if (player != null && context == Context.DEFAULT)
        {
            ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
        }
        return stack;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.EXTRA_PRODUCT.get();
    }
}
