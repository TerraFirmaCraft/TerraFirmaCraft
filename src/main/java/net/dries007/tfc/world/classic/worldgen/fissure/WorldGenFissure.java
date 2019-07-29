/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.fissure;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC.getRock3;
import static net.dries007.tfc.world.classic.worldgen.fissure.CollapseData.Direction.*;

/**
 * todo: fix cascading lag. Priority: medium - low.
 * See <a href="https://github.com/TerraFirmaCraft/TerraFirmaCraft/issues/40">issue</a> here.
 */
public class WorldGenFissure implements IWorldGenerator
{
    private final IBlockState fillBlock;
    private final boolean checkStability;
    private final int minTunnel;
    private final int depth;

    public WorldGenFissure(boolean lava, int depth)
    {
        fillBlock = lava ? ChunkGenTFC.LAVA : ChunkGenTFC.FRESH_WATER;
        checkStability = lava;
        minTunnel = 5;
        this.depth = depth;
    }

    WorldGenFissure(IBlockState state)
    {
        fillBlock = state;
        checkStability = false;
        depth = -1;
        minTunnel = 1;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        BlockPos start = new ChunkPos(chunkX, chunkZ).getBlock(random.nextInt(16) + 8, 0, random.nextInt(16) + 8);
        Biome biome = world.getBiome(start);

        if (biome == BiomesTFC.BEACH || biome == BiomesTFC.OCEAN || biome == BiomesTFC.GRAVEL_BEACH || biome == BiomesTFC.LAKE || biome == BiomesTFC.RIVER || biome == BiomesTFC.DEEP_OCEAN)
            return;

        start = world.getTopSolidOrLiquidBlock(start).add(0, -1, 0);
        if (depth > 0)
            start = start.add(0, -depth - random.nextInt(60 /* todo: setting? */), 0);

        generate(world, random, start);
    }

    protected void generate(World world, Random rng, BlockPos start)
    {
        IBlockState block = world.getBlockState(start);
        if (BlocksTFC.isWater(block) && !BlocksTFC.isGround(block)) return;
        final boolean stable = ChunkDataTFC.isStable(world, start);
        if (checkStability && stable)
            return;

        int creviceDepth = 3 + rng.nextInt(5);

        int poolDepth = 1 + rng.nextInt(Math.max(creviceDepth - 1, 1));

        for (int d = 1; d <= poolDepth; d++) if (!world.getBlockState(start.add(0, -d, 0)).isNormalCube()) return;

        if (depth > 0)
            start = start.add(0, -20 - rng.nextInt(depth), 0);

        final IBlockState rock = BlockRockVariant.get(getRock3(world, start), Rock.Type.RAW).getDefaultState();
        final IBlockState localFillBlock = (!stable && BlocksTFC.isWater(fillBlock)) ? ChunkGenTFC.HOT_WATER : fillBlock;

        List<BlockPos> list = getCollapseMap(world, start, fillBlock, poolDepth);

        for (BlockPos pos : list)
        {
            if (pos.getY() < 10 && world.getBlockState(pos).getBlock() != Blocks.BEDROCK)
            {
                world.setBlockToAir(pos);
            }
            for (int d = 1; d <= poolDepth; d++)
                fill(world, pos.add(0, -d, 0), rock, localFillBlock);

            for (int d = 0; d <= creviceDepth; d++)
            {
                carve(world, pos.add(0, d, 0), rock);
                if (rng.nextInt(3) == 0) carve(world, pos.add(-1 + rng.nextInt(3), d, -1 + rng.nextInt(3)), rock);
            }
            if (localFillBlock == ChunkGenTFC.LAVA)
                world.setBlockState(pos.add(0, -poolDepth - 1, 0), rock, 2);
        }

        if (list.size() > 10) makeTunnel(rng, world, start.add(0, -poolDepth - 1, 0), rock, localFillBlock);
    }

    private List<BlockPos> getCollapseMap(World world, BlockPos pos, IBlockState fillBlock, int poolDepth)
    {
        final ImmutableList.Builder<BlockPos> b = ImmutableList.builder();

        final IBlockState rock = fillBlock == ChunkGenTFC.LAVA ?
            BlockRockVariant.get(getRock3(world, pos), Rock.Type.RAW).getDefaultState() :
            BlockRockVariant.get(ChunkDataTFC.getRockHeight(world, pos), Rock.Type.RAW).getDefaultState();

        // todo this must be also used somewhere else probably move it.
        // todo this must be optimizable also

        CollapseList collapseList = new CollapseList();

        for (CollapseData.Direction d : CollapseData.Direction.values())
            collapseList.add(new CollapseData(d.offset(pos.add(0, -1, 0)), 0.55f - d.decrement, d));

        while (!collapseList.isEmpty())
        {
            CollapseData data = collapseList.pop();

            if (!world.isBlockLoaded(data.pos))
                continue; // todo: non-successful attempt at stopping cascading world gen

            IBlockState block = world.getBlockState(data.pos);

            if (BlocksTFC.isGround(block) && world.rand.nextFloat() < data.chance)
            {
                b.add(data.pos);

                switch (data.direction)
                {
                    case NORTH:
                        collapseList.add(new CollapseData(NORTH.offset(pos), data.chance - NORTH.decrement, NORTH));
                        collapseList.add(new CollapseData(EAST.offset(pos), data.chance - EAST.decrement, EAST));
                        collapseList.add(new CollapseData(WEST.offset(pos), data.chance - WEST.decrement, WEST));
                        break;
                    case SOUTH:
                        collapseList.add(new CollapseData(SOUTH.offset(pos), data.chance - SOUTH.decrement, SOUTH));
                        collapseList.add(new CollapseData(EAST.offset(pos), data.chance - EAST.decrement, EAST));
                        collapseList.add(new CollapseData(WEST.offset(pos), data.chance - WEST.decrement, WEST));
                        break;
                    case EAST:
                        collapseList.add(new CollapseData(SOUTH.offset(pos), data.chance - SOUTH.decrement, SOUTH));
                        collapseList.add(new CollapseData(EAST.offset(pos), data.chance - EAST.decrement, EAST));
                        collapseList.add(new CollapseData(NORTH.offset(pos), data.chance - NORTH.decrement, NORTH));
                        break;
                    case WEST:
                        collapseList.add(new CollapseData(SOUTH.offset(pos), data.chance - SOUTH.decrement, SOUTH));
                        collapseList.add(new CollapseData(WEST.offset(pos), data.chance - WEST.decrement, WEST));
                        collapseList.add(new CollapseData(NORTH.offset(pos), data.chance - NORTH.decrement, NORTH));
                        break;
                    case NORTHEAST:
                        collapseList.add(new CollapseData(NORTHEAST.offset(pos), data.chance - NORTHEAST.decrement, NORTHEAST));
                        collapseList.add(new CollapseData(EAST.offset(pos), data.chance - EAST.decrement, EAST));
                        collapseList.add(new CollapseData(NORTH.offset(pos), data.chance - NORTH.decrement, NORTH));
                        break;
                    case SOUTHEAST:
                        collapseList.add(new CollapseData(SOUTHEAST.offset(pos), data.chance - SOUTHEAST.decrement, SOUTHEAST));
                        collapseList.add(new CollapseData(SOUTH.offset(pos), data.chance - SOUTH.decrement, SOUTH));
                        collapseList.add(new CollapseData(EAST.offset(pos), data.chance - EAST.decrement, EAST));
                        break;
                    case NORTHWEST:
                        collapseList.add(new CollapseData(NORTHWEST.offset(pos), data.chance - NORTHWEST.decrement, NORTHWEST));
                        collapseList.add(new CollapseData(WEST.offset(pos), data.chance - WEST.decrement, WEST));
                        collapseList.add(new CollapseData(NORTH.offset(pos), data.chance - NORTH.decrement, NORTH));
                        break;
                    case SOUTHWEST:
                        collapseList.add(new CollapseData(SOUTHWEST.offset(pos), data.chance - SOUTHWEST.decrement, SOUTHWEST));
                        collapseList.add(new CollapseData(SOUTH.offset(pos), data.chance - SOUTH.decrement, SOUTH));
                        collapseList.add(new CollapseData(WEST.offset(pos), data.chance - WEST.decrement, WEST));
                        break;
                }
            }
            else if (data.chance < 1)
            {
                for (int i = 0; i <= poolDepth; i++)
                {
                    if (BlocksTFC.isGround(world.getBlockState(pos.add(0, -i, 0))))
                        world.setBlockState(pos.add(0, -i, 0), rock, 2);
                }
            }
        }
        return b.build();
    }

    private void carve(World world, BlockPos pos, IBlockState state)
    {
        // todo: check if this should even update the blocks (flags = 3 means update) I think this only causes lag. (if not also replace `setBlockToAir`)
        if (!world.isAirBlock(pos) && BlocksTFC.isGround(world.getBlockState(pos))) world.setBlockToAir(pos);
        BlockPos p = pos.add(-1, 0, 0);
        if (!world.isAirBlock(p) && BlocksTFC.isGround(world.getBlockState(p))) world.setBlockState(p, state, 3);
        p = pos.add(1, 0, 0);
        if (!world.isAirBlock(p) && BlocksTFC.isGround(world.getBlockState(p))) world.setBlockState(p, state, 3);
        p = pos.add(0, 0, -1);
        if (!world.isAirBlock(p) && BlocksTFC.isGround(world.getBlockState(p))) world.setBlockState(p, state, 3);
        p = pos.add(0, 0, 1);
        if (!world.isAirBlock(p) && BlocksTFC.isGround(world.getBlockState(p))) world.setBlockState(p, state, 3);
    }

    private void fill(World world, BlockPos pos, IBlockState rock, IBlockState fillBlock)
    {
        world.setBlockState(pos, fillBlock);
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (facing == EnumFacing.UP || world.getBlockState(pos.offset(facing)) == fillBlock) continue;
            world.setBlockState(pos.offset(facing), rock);
        }
    }


    private void makeTunnel(Random random, World world, BlockPos pos, IBlockState rock, IBlockState fillBlock)
    {
        float downChance = 75;
        while (pos.getZ() > minTunnel)
        {
            if (random.nextFloat() < downChance / 100f)
            {
                pos = pos.add(0, -1, 0);
            }
            else
            {
                int dir = random.nextInt(3);
                switch (dir)
                {
                    case 0:
                        pos = pos.add(-1, 0, 0);
                        break;
                    case 1:
                        pos = pos.add(1, 0, 0);
                        break;
                    case 2:
                        pos = pos.add(0, 0, -1);
                        break;
                    case 3:
                        pos = pos.add(0, 0, 1);
                        break;
                }
            }

            world.setBlockState(pos, fillBlock, 2);

            BlockPos p = pos.add(1, 0, 0);
            if (world.getBlockState(p).getMaterial() != fillBlock.getMaterial()) world.setBlockState(p, rock, 2);
            p = pos.add(-1, 0, 0);
            if (world.getBlockState(p).getMaterial() != fillBlock.getMaterial()) world.setBlockState(p, rock, 2);
            p = pos.add(0, 0, 1);
            if (world.getBlockState(p).getMaterial() != fillBlock.getMaterial()) world.setBlockState(p, rock, 2);
            p = pos.add(0, 0, -1);
            if (world.getBlockState(p).getMaterial() != fillBlock.getMaterial()) world.setBlockState(p, rock, 2);
        }
    }
}
