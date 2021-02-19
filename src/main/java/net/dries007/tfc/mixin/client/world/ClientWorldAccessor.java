/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.world;

import net.minecraft.client.renderer.color.ColorCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.level.ColorResolver;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientWorld.class)
public interface ClientWorldAccessor
{
    @Accessor("colorCaches")
    Object2ObjectArrayMap<ColorResolver, ColorCache> getTintCaches();
}
