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
    @Accessor("tintCaches")
    Object2ObjectArrayMap<ColorResolver, ColorCache> getTintCaches();
}
