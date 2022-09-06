/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.accessor;

import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ColorResolver;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * todo 1.19: Remove and use event, see https://github.com/MinecraftForge/MinecraftForge/pull/8880
 */
@Mixin(ClientLevel.class)
public interface ClientLevelAccessor
{
    @Accessor("tintCaches")
    Object2ObjectArrayMap<ColorResolver, BlockTintCache> accessor$getTintCaches();
}
