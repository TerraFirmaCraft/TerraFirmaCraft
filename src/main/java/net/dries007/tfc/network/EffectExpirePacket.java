/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.effect.TFCEffects;

public record EffectExpirePacket(Holder<MobEffect> effect) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<EffectExpirePacket> TYPE = PacketHandler.type("effect_expire");
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectExpirePacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT), c -> c.effect,
        EffectExpirePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle()
    {
        if (effect == TFCEffects.PINNED.get())
        {
            final Player player = ClientHelpers.getPlayer();
            if (player != null && player.hasEffect(TFCEffects.PINNED.holder()))
            {
                player.setForcedPose(null);
            }
        }
    }
}
