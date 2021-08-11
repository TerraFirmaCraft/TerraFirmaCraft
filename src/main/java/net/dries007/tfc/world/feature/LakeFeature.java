/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.soil.IDirtBlock;

/**
 * This is a big copy pasta from {@link net.minecraft.world.gen.feature.LakesFeature} with the following changes:
 * - It only works with water, since this is going to be used primarily for surface lakes
 * - It handles TFC dirt / grass transformations correctly
 */
public class LakeFeature extends Feature<NoneFeatureConfiguration>
{
    public LakeFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    @SuppressWarnings("ALL")
    public boolean place(WorldGenLevel worldIn, ChunkGenerator chunkGenerator, Random random, BlockPos pos, NoneFeatureConfiguration config)
    {
        while (pos.getY() > 5 && worldIn.isEmptyBlock(pos))
        {
            pos = pos.below();
        }

        if (pos.getY() <= 4)
        {
            return false;
        }
        else
        {
            pos = pos.below(4);
            if (worldIn.startsForFeature(SectionPos.of(pos), StructureFeature.VILLAGE).findAny().isPresent())
            {
                return false;
            }
            else
            {
                boolean[] noise = new boolean[2048];
                int points = random.nextInt(4) + 4;

                for (int i = 0; i < points; ++i)
                {
                    double x0 = random.nextDouble() * 6.0D + 3.0D;
                    double y0 = random.nextDouble() * 4.0D + 2.0D;
                    double z0 = random.nextDouble() * 6.0D + 3.0D;
                    double x1 = random.nextDouble() * (16.0D - x0 - 2.0D) + 1.0D + x0 / 2.0D;
                    double y1 = random.nextDouble() * (8.0D - y0 - 4.0D) + 2.0D + y0 / 2.0D;
                    double z1 = random.nextDouble() * (16.0D - z0 - 2.0D) + 1.0D + z0 / 2.0D;

                    for (int x = 1; x < 15; ++x)
                    {
                        for (int z = 1; z < 15; ++z)
                        {
                            for (int y = 1; y < 7; ++y)
                            {
                                double deltaX = (x - x1) / (x0 / 2.0D);
                                double deltaY = (y - y1) / (y0 / 2.0D);
                                double deltaZ = (z - z1) / (z0 / 2.0D);
                                double distance = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
                                if (distance < 1.0D)
                                {
                                    noise[(x * 16 + z) * 8 + y] = true;
                                }
                            }
                        }
                    }
                }

                for (int x = 0; x < 16; ++x)
                {
                    for (int z = 0; z < 16; ++z)
                    {
                        for (int y = 0; y < 8; ++y)
                        {
                            boolean flag = !noise[(x * 16 + z) * 8 + y] && (x < 15 && noise[((x + 1) * 16 + z) * 8 + y] || x > 0 && noise[((x - 1) * 16 + z) * 8 + y] || z < 15 && noise[(x * 16 + z + 1) * 8 + y] || z > 0 && noise[(x * 16 + (z - 1)) * 8 + y] || y < 7 && noise[(x * 16 + z) * 8 + y + 1] || y > 0 && noise[(x * 16 + z) * 8 + (y - 1)]);
                            if (flag)
                            {
                                Material material = worldIn.getBlockState(pos.offset(x, y, z)).getMaterial();
                                if (y >= 4 && material.isLiquid())
                                {
                                    return false;
                                }

                                if (y < 4 && !material.isSolid() && !worldIn.getBlockState(pos.offset(x, y, z)).is(Blocks.WATER))
                                {
                                    return false;
                                }
                            }
                        }
                    }
                }

                for (int x = 0; x < 16; ++x)
                {
                    for (int z = 0; z < 16; ++z)
                    {
                        for (int y = 0; y < 8; ++y)
                        {
                            if (noise[(x * 16 + z) * 8 + y])
                            {
                                worldIn.setBlock(pos.offset(x, y, z), y >= 4 ? Blocks.AIR.defaultBlockState() : Blocks.WATER.defaultBlockState(), 2);
                            }
                        }
                    }
                }

                for (int x = 0; x < 16; ++x)
                {
                    for (int z = 0; z < 16; ++z)
                    {
                        for (int y = 4; y < 8; ++y)
                        {
                            if (noise[(x * 16 + z) * 8 + y])
                            {
                                BlockPos dirtPos = pos.offset(x, y - 1, z);
                                BlockState dirtState = worldIn.getBlockState(dirtPos);
                                if (dirtState.getBlock() instanceof IDirtBlock && worldIn.getBrightness(LightLayer.SKY, pos.offset(x, y, z)) > 0)
                                {
                                    BlockState grassState = ((IDirtBlock) dirtState.getBlock()).getGrass();
                                    worldIn.setBlock(dirtPos, grassState, 2);
                                    worldIn.getBlockTicks().scheduleTick(dirtPos, grassState.getBlock(), 0);
                                }
                            }
                        }
                    }
                }
                return true;
            }
        }
    }
}
