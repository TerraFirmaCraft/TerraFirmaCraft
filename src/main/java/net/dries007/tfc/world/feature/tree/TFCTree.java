/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;

public class TFCTree extends Tree
{
    private final ResourceLocation normalTree;
    private final ResourceLocation oldGrowthTree;

    public TFCTree(ResourceLocation normalTree, ResourceLocation oldGrowthFeatureFactory)
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
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random randomIn, boolean largeHive)
    {
        return null; // Not using vanilla's feature config
    }

    @Override
    public boolean growTree(ServerWorld worldIn, ChunkGenerator chunkGeneratorIn, BlockPos blockPosIn, BlockState blockStateIn, Random randomIn)
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