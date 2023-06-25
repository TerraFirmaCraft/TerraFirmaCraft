/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class TFCTreeGrower extends AbstractTreeGrower
{
    private final ResourceLocation normalTree;
    private final ResourceLocation oldGrowthTree;

    public TFCTreeGrower(ResourceLocation normalTree, ResourceLocation oldGrowthFeatureFactory)
    {
        this.normalTree = normalTree;
        this.oldGrowthTree = oldGrowthFeatureFactory;
    }

    public ConfiguredFeature<?, ?> getNormalFeature(Registry<ConfiguredFeature<?, ?>> registry)
    {
        return registry.getOptional(normalTree).orElseThrow(() -> new IllegalStateException("Missing tree feature: " + normalTree));
    }

    public ConfiguredFeature<?, ?> getOldGrowthFeature(Registry<ConfiguredFeature<?, ?>> registry)
    {
        return registry.getOptional(oldGrowthTree).orElseGet(() -> getNormalFeature(registry));
    }

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers)
    {
        return null; // Not using vanilla's feature config
    }

    @Override
    public boolean growTree(ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, RandomSource random)
    {
        final ConfiguredFeature<?, ?> feature = getNormalFeature(level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE));
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
        if (feature.place(level, generator, random, pos))
        {
            return true;
        }
        else
        {
            level.setBlock(pos, state, 4);
            return false;
        }
    }
}