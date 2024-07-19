/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ChanceModifier(float chance) implements ItemStackModifier
{
    public static final MapCodec<ChanceModifier> CHANCE = Codec.FLOAT.fieldOf("chance").xmap(ChanceModifier::new, ChanceModifier::chance);
    public static final StreamCodec<ByteBuf, ChanceModifier> STREAM_CODEC = ByteBufCodecs.FLOAT.map(ChanceModifier::new, ChanceModifier::chance);

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        if (context != Context.DEFAULT)
        {
            return stack;
        }
        int count = 0;
        for (int i = 0; i < stack.getCount(); i++)
        {
            if (Math.random() < chance) count++;
        }
        if (count > 0)
        {
            return stack.copyWithCount(count);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.CHANCE.get();
    }
}
