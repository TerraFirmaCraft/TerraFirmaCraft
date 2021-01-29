/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.ForestType;

public class ForestFeature extends Feature<ForestConfig>
{
    public ForestFeature(Codec<ForestConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, ForestConfig config)
    {
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(generator);
        final ChunkData data = provider.get(pos, ChunkData.Status.FLORA);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final ForestType forestType = data.getForestType();

        int treeCount;
        int groundCount;
        boolean placedTrees = false;
        boolean placedBushes = false;
        if (forestType == ForestType.SPARSE)
        {
            if (rand.nextFloat() < 0.08f)
            {
                int trees = 1 + rand.nextInt(3);
                for (int i = 0; i < trees; i++)
                {
                    placedTrees |= placeTree(worldIn, generator, rand, pos, config, data, mutablePos, false);
                }
                placeGroundcover(worldIn, rand, pos, config, data, mutablePos, 10);
            }
            return true;
        }
        else if (forestType == ForestType.NORMAL)
        {
            treeCount = 5;
            groundCount = 35;
        }
        else if (forestType == ForestType.OLD_GROWTH)
        {
            treeCount = 7;
            groundCount = 48;
        }
        else
        {
            return false;
        }

        final float density = data.getForestDensity();
        treeCount = (int) (treeCount * (0.6f + 0.9f * density));
        for (int i = 0; i < treeCount; i++)
        {
            placedTrees |= placeTree(worldIn, generator, rand, pos, config, data, mutablePos, forestType == ForestType.OLD_GROWTH);
        }
        int bushCount = (int) (treeCount * 2 * density);
        for (int j = 0; j < bushCount; j++)
        {
            placedBushes |= placeBush(worldIn, generator, rand, pos, config, data, mutablePos);
        }
        if (placedTrees)
        {
            placeGroundcover(worldIn, rand, pos, config, data, mutablePos, groundCount);
        }
        return placedTrees || placedBushes;
    }

    private boolean placeTree(ISeedReader worldIn, ChunkGenerator generator, Random random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.Mutable mutablePos, boolean allowOldGrowth)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));

        final ForestConfig.Entry entry = getTree(data, random, config, mutablePos);
        if (entry != null)
        {
            ConfiguredFeature<?, ?> feature;
            if (allowOldGrowth && random.nextInt(6) == 0)
            {
                feature = entry.getOldGrowthFeature();
            }
            else
            {
                feature = random.nextInt(200) == 0 ? entry.getOldGrowthFeature() : entry.getFeature();
            }
            return feature.place(worldIn, generator, random, mutablePos);
        }
        return false;
    }

    private boolean placeBush(ISeedReader worldIn, ChunkGenerator generator, Random random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.Mutable mutablePos)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));

        final ForestConfig.Entry entry = getTree(data, random, config, mutablePos);
        if (entry != null && worldIn.isEmptyBlock(mutablePos) && worldIn.getBlockState(mutablePos.below()).is(TFCTags.Blocks.BUSH_PLANTABLE_ON))
        {
            setBlock(worldIn, mutablePos, entry.getLog());
            for (Direction facing : Direction.values())
            {
                if (facing != Direction.DOWN)
                {
                    BlockPos offsetPos = mutablePos.offset(facing.getStepX(), facing.getStepY(), facing.getStepZ());
                    if (worldIn.isEmptyBlock(offsetPos) || worldIn.getBlockState(offsetPos).is(TFCTags.Blocks.PLANT))
                        setBlock(worldIn, offsetPos, entry.getLeaves());
                }
            }
            return true;
        }
        return false;
    }

    private void placeGroundcover(ISeedReader worldIn, Random random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.Mutable mutablePos, int tries)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR, mutablePos.getX(), mutablePos.getZ()));

        final ForestConfig.Entry entry = getTree(data, random, config, mutablePos);
        if (entry != null)
        {
            BlockState leafState = entry.getFallenLeaves();
            BlockState twigState = entry.getTwig();
            for (int j = 0; j < tries; ++j)
            {
                BlockState setState = random.nextInt(2) == 1 ? leafState : twigState;
                mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
                mutablePos.setY(worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR, mutablePos.getX(), mutablePos.getZ()));
                if ((worldIn.isEmptyBlock(mutablePos) || worldIn.isWaterAt(mutablePos)) && worldIn.getBlockState(mutablePos.below()).isFaceSturdy(worldIn, mutablePos, Direction.UP))
                {
                    setBlock(worldIn, mutablePos, setState
                        .setValue(TFCBlockStateProperties.WATER, TFCBlockStateProperties.WATER.keyFor(worldIn.getFluidState(mutablePos).getType()))
                        .setValue(HorizontalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)));
                }
            }
        }
    }

    @Nullable
    private ForestConfig.Entry getTree(ChunkData chunkData, Random random, ForestConfig config, BlockPos pos)
    {
        List<ForestConfig.Entry> entries = new ArrayList<>(4);
        float rainfall = chunkData.getRainfall(pos);
        float averageTemperature = chunkData.getAverageTemp(pos);
        for (ForestConfig.Entry entry : config.getEntries())
        {
            // silly way to halfway guarantee that stuff is in general order of dominance
            float lastRain = entry.getAverageRain();
            float lastTemp = entry.getAverageTemp();
            if (entry.isValid(averageTemperature, rainfall))
            {
                if (entry.distanceFromMean(lastTemp, lastRain) < entry.distanceFromMean(averageTemperature, rainfall))
                {
                    entries.add(entry); // if the last one was closer to it's target, just add it normally
                }
                else
                {
                    entries.add(0, entry); // if the new one is closer, stick it in front
                }
            }
        }

        float weirdness = chunkData.getForestWeirdness();
        Collections.rotate(entries, -(int) (weirdness * (entries.size() - 1f)));
        // remove up to 3 entries from the config based on weirdness, less likely to happen each time
        if (!entries.isEmpty())
        {
            for (int i = 1; i >= -1; i--)
            {
                if (entries.size() <= 1)
                    break;
                if (random.nextFloat() > weirdness - (0.15f * i) + 0.1f)
                {
                    entries.remove(entries.size() - 1);
                }
            }
        }
        else
        {
            return null;
        }

        int index = 0;
        while (index < entries.size() - 1 && random.nextFloat() < 0.6f)
        {
            index++;
        }
        return entries.get(index);
    }
}