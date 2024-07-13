/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.network.StreamCodecs;
import net.dries007.tfc.util.data.Pannable;
import net.dries007.tfc.world.Codecs;

public record PannableComponent(BlockState state)
{
    public static final Codec<PannableComponent> CODEC = Codecs.BLOCK_STATE.xmap(PannableComponent::new, PannableComponent::state);
    public static final StreamCodec<RegistryFriendlyByteBuf, PannableComponent> STREAM_CODEC = StreamCodecs.BLOCK_STATE.map(PannableComponent::new, PannableComponent::state);

    @Nullable
    public Pannable get()
    {
        return Pannable.get(state);
    }
}
