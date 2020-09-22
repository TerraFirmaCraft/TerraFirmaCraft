package net.dries007.tfc.mixin.world.gen;

import net.minecraft.world.gen.Heightmap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Heightmap.class)
public interface HeightmapAccessor
{
    @Invoker("setHeight")
    void call$setHeight(int x, int z, int value);
}
