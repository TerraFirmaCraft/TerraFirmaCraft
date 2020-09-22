/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Lazy;

public class TFCTree extends Tree
{
    private final Lazy<ConfiguredFeature<?, ?>> featureFactory;
    private final Lazy<ConfiguredFeature<?, ?>> oldGrowthFeatureFactory;

    public TFCTree(Supplier<ConfiguredFeature<?, ?>> featureFactory)
    {
        this(featureFactory, featureFactory);
    }

    public TFCTree(Supplier<ConfiguredFeature<?, ?>> featureFactory, Supplier<ConfiguredFeature<?, ?>> oldGrowthFeatureFactory)
    {
        this.featureFactory = Lazy.of(featureFactory);
        this.oldGrowthFeatureFactory = Lazy.of(oldGrowthFeatureFactory);
    }

    public ConfiguredFeature<?, ?> getNormalFeature()
    {
        return featureFactory.get();
    }

    public ConfiguredFeature<?, ?> getOldGrowthFeature()
    {
        return oldGrowthFeatureFactory.get();
    }

    @Override
    public boolean growTree(ServerWorld worldIn, ChunkGenerator chunkGeneratorIn, BlockPos blockPosIn, BlockState blockStateIn, Random randomIn)
    {
        ConfiguredFeature<?, ?> feature = getNormalFeature();
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

    @Nullable
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random randomIn, boolean largeHive)
    {
        return null; // Not using vanilla's feature config
    }
}