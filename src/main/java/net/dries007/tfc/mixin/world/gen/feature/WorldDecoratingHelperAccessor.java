package net.dries007.tfc.mixin.world.gen.feature;

import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Adds accessors for the internal fields on {@link WorldDecoratingHelper} as that class is needlessly limiting, and our climate
 */
@Mixin(WorldDecoratingHelper.class)
public interface WorldDecoratingHelperAccessor
{
    @Accessor(value = "level")
    ISeedReader accessor$getLevel();

    @Accessor(value = "generator")
    ChunkGenerator accessor$getGenerator();
}
