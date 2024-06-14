/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.TFCEffects;

// Tracking Issue: https://github.com/MinecraftForge/MinecraftForge/issues/8506
// Update: Forge does not believe that this is an issue.
public class EffectExpirePacket
{
    private final MobEffect effect;

    public EffectExpirePacket(MobEffect effect)
    {
        this.effect = effect;
    }

    EffectExpirePacket(FriendlyByteBuf buffer)
    {
        this.effect = BuiltInRegistries.MOB_EFFECT.byIdOrThrow(buffer.readVarInt());
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(BuiltInRegistries.MOB_EFFECT.getId(effect));
    }

    void handle()
    {
        if (effect == TFCEffects.PINNED.get())
        {
            final Player player = ClientHelpers.getPlayer();
            if (player != null && player.hasEffect(TFCEffects.PINNED.get()))
            {
                player.setForcedPose(null);
            }
        }
    }
}
