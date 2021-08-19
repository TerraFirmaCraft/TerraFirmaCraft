package net.dries007.tfc.mixin.client.accessor;

import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ColorResolver;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientLevel.class)
public interface ClientLevelAccessor
{
    @Accessor("tintCaches")
    Object2ObjectArrayMap<ColorResolver, BlockTintCache> accessor$getTintCaches();
}
