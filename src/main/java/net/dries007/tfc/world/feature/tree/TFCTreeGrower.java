/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Random;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.server.level.ServerLevel;

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
    protected Holder<ConfiguredFeature<TreeConfiguration, ?>> getConfiguredFeature(Random randomIn, boolean largeHive)
    {
        return null; // Not using vanilla's feature config
    }

    @Override
    public boolean growTree(ServerLevel worldIn, ChunkGenerator chunkGeneratorIn, BlockPos blockPosIn, BlockState blockStateIn, Random randomIn)
    {
        ConfiguredFeature<?, ?> feature = getNormalFeature(worldIn.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY));
        worldIn.setBlock(blockPosIn, Blocks.AIR.defaultBlockState(), 4);
        if (feature.place(worldIn, chunkGeneratorIn, randomIn, blockPosIn))
        {
            return true;
        }
        else
        {
            worldIn.setBlock(blockPosIn, blockStateIn, 4);
            return false;
        }
    }
}