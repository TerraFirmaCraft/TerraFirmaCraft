/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.noise.Metaballs2D;
import net.dries007.tfc.world.settings.RockSettings;

public class HotSpringFeature extends Feature<HotSpringConfig>
{
    public HotSpringFeature(Codec<HotSpringConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<HotSpringConfig> context)
    {
        final WorldGenLevel world = context.level();
        final BlockPos pos = context.origin();
        final Random rand = context.random();
        final HotSpringConfig config = context.config();

        final Metaballs2D noise = Metaballs2D.simple(rand, config.radius());
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final ChunkDataProvider provider = ChunkDataProvider.get(context.chunkGenerator());
        final ChunkData data = provider.get(context.level(), pos);
        final RockSettings rock = data.getRockData().getRock(pos.getX(), 0, pos.getZ());
        final BlockState rockState = rock.raw().defaultBlockState();
        final BlockState gravelState = rock.gravel().defaultBlockState();
        final Fluid fluid = config.fluidState().getFluidState().getType();

        final boolean useFilledEmptyCheck = config.fluidState().isAir();
        final Set<BlockPos> filledEmptyPositions = new HashSet<>(); // Only used if the fill state is air - prevents positions from being 'filled' with air and affecting the shape of the spring
        final List<BlockPos> fissureStartPositions = new ArrayList<>();

        for (int x = -config.radius(); x <= config.radius(); x++)
        {
            for (int z = -config.radius(); z <= config.radius(); z++)
            {
                final int localX = pos.getX() + x;
                final int localZ = pos.getZ() + z;
                final int y = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, localX, localZ) - 1;

                // Disallow underwater locations
                if (y <= TFCChunkGenerator.SEA_LEVEL_Y || !noise.inside(x, z))
                {
                    continue;
                }

                mutablePos.set(localX, y + 1, localZ);
                final BlockState stateAbove = world.getBlockState(mutablePos);
                if (!stateAbove.isAir())
                {
                    if (stateAbove.getMaterial().isReplaceable())
                    {
                        setBlock(world, mutablePos, Blocks.AIR.defaultBlockState());
                    }
                    else
                    {
                        continue; // Solid, non-replaceable block above. So don't replace with hot spring blocks
                    }
                }

                boolean edge = false;
                for (Direction direction : Direction.Plane.HORIZONTAL)
                {
                    mutablePos.set(localX, y, localZ).move(direction);
                    if (world.isEmptyBlock(mutablePos) && (!useFilledEmptyCheck || !filledEmptyPositions.contains(mutablePos)))
                    {
                        edge = true;
                        break;
                    }
                }

                // surface depth is deeper near the center of the hot spring
                // Range: [0.3, 1.0]
                final float centerFactor = 1 - 0.7f * Mth.clamp(2 * (x * x + z * z) / (float) (config.radius() * config.radius()), 0, 1);
                final int surfaceDepth = (int) ((8 + rand.nextInt(3)) * centerFactor);
                if (edge)
                {
                    final int startY = rand.nextInt(12) == 0 ? -1 : 0; // Creates holes which allow the water to flow, rarely
                    if (startY == -1)
                    {
                        mutablePos.set(localX, y, localZ);
                        setBlock(world, mutablePos, Blocks.AIR.defaultBlockState());
                    }
                }
                else
                {
                    mutablePos.set(localX, y, localZ);
                    final BlockPos posAt = mutablePos.immutable();
                    fissureStartPositions.add(posAt);

                    setBlock(world, mutablePos, config.fluidState());
                    if (fluid != Fluids.EMPTY)
                    {
                        world.getLiquidTicks().scheduleTick(mutablePos, fluid, 0);
                    }
                    if (useFilledEmptyCheck)
                    {
                        filledEmptyPositions.add(posAt);
                    }

                    mutablePos.set(localX, y - 1, localZ);
                    setFissureBaseBlock(world, mutablePos, gravelState);
                }

                for (int dy = edge ? 0 : -2; dy >= -surfaceDepth; dy--)
                {
                    mutablePos.set(localX, y + dy, localZ);
                    if (!setFissureBaseBlock(world, mutablePos, rockState))
                    {
                        break;
                    }
                }
            }
        }

        if (fissureStartPositions.isEmpty())
        {
            return false;
        }

        final int fissureStarts = 1 + rand.nextInt(1 + rand.nextInt(Mth.clamp(fissureStartPositions.size(), 1, 7)));
        final List<BlockPos> selected = Helpers.uniqueRandomSample(fissureStartPositions, fissureStarts, rand);
        for (BlockPos start : selected)
        {
            FissureFeature.placeFissure(world, start, pos, mutablePos, rand, config.fluidState(), rockState, 10, 22, 6, 16, 12, config.decoration().orElse(null));
        }

        return true;
    }

    private boolean setFissureBaseBlock(WorldGenLevel world, BlockPos pos, BlockState state)
    {
        final BlockState stateAt = world.getBlockState(pos);
        if (stateAt.isAir())
        {
            return false;
        }
        world.setBlock(pos, state, 2);
        return true;
    }
}
