package net.dries007.tfc.world.feature;

import java.util.*;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.noise.Metaballs2D;

public class HotSpringFeature extends Feature<HotSpringConfig>
{
    public HotSpringFeature(Codec<HotSpringConfig> codec)
    {
        super(codec);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, HotSpringConfig config)
    {
        final Metaballs2D noise = new Metaballs2D(config.radius, rand);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(generator);
        final ChunkData data = provider.get(pos, ChunkData.Status.ROCKS);
        final Rock rock = data.getRockData().getRock(pos.getX(), 0, pos.getZ());
        final BlockState rockState = rock.getBlock(Rock.BlockType.RAW).defaultBlockState();
        final BlockState gravelState = rock.getBlock(Rock.BlockType.RAW).defaultBlockState();
        final Fluid fluid = config.fluidState.getFluidState().getType();

        final boolean useFilledEmptyCheck = config.fluidState.isAir();
        final Set<BlockPos> filledEmptyPositions = new HashSet<>(); // Only used if the fill state is air - prevents positions from being 'filled' with air and affecting the shape of the spring
        final List<BlockPos> fissureStartPositions = new ArrayList<>();

        for (int x = -config.radius; x <= config.radius; x++)
        {
            for (int z = -config.radius; z <= config.radius; z++)
            {
                final int localX = pos.getX() + x;
                final int localZ = pos.getZ() + z;
                final int y = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, localX, localZ) - 1;

                if (noise.noise(x, z) == 0)
                {
                    continue;
                }

                mutablePos.set(localX, y + 1, localZ);
                final BlockState stateAbove = world.getBlockState(mutablePos);
                if (stateAbove.getMaterial().isReplaceable())
                {
                    setBlock(world, mutablePos, Blocks.AIR.defaultBlockState());
                }
                else
                {
                    continue;
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

                final int surfaceDepth = 8 + rand.nextInt(3);
                if (edge)
                {
                    final int startY = rand.nextInt(12) == 0 ? -1 : 0; // Creates holes which allow the water to flow, rarely
                    if (startY == -1)
                    {
                        mutablePos.set(localX, y, localZ);
                        setBlock(world, mutablePos, Blocks.AIR.defaultBlockState());
                    }
                    for (int dy = startY; dy >= -surfaceDepth; dy--)
                    {
                        mutablePos.set(localX, y + dy, localZ);
                        final BlockState state = world.getBlockState(mutablePos);
                        if (state == rockState)
                        {
                            break;
                        }
                        setBlock(world, mutablePos, rockState);
                    }
                }
                else
                {
                    mutablePos.set(localX, y, localZ);
                    setBlock(world, mutablePos, config.fluidState);
                    if (fluid != Fluids.EMPTY)
                    {
                        world.getLiquidTicks().scheduleTick(mutablePos, fluid, 0);
                    }
                    if (useFilledEmptyCheck)
                    {
                        filledEmptyPositions.add(mutablePos.immutable());
                    }
                    fissureStartPositions.add(mutablePos.immutable());
                    for (int dy = -1; dy >= -surfaceDepth; dy--)
                    {
                        mutablePos.set(localX, y + dy, localZ);
                        final BlockState state = world.getBlockState(mutablePos);
                        setBlock(world, mutablePos, gravelState); // set block first so we always hit at least one layer of gravel
                        if (state == rockState || state == gravelState)
                        {
                            break;
                        }
                    }
                }
            }
        }

        if (fissureStartPositions.isEmpty())
        {
            return false;
        }

        final int fissureStarts = 1 + rand.nextInt(1 + rand.nextInt(MathHelper.clamp(fissureStartPositions.size(), 1, 7)));
        final List<BlockPos> selected = Helpers.uniqueRandomSample(fissureStartPositions, fissureStarts, rand);
        for (BlockPos start : selected)
        {
            FissureFeature.placeFissure(world, start, pos, mutablePos, rand, config.fluidState, rockState, 10, 22, 6, 16, 12, config.decoration.orElse(null));
        }

        return true;
    }
}
