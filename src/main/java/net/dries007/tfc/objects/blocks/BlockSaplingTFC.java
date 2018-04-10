package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.objects.Wood;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.EnumMap;
import java.util.Random;

public class BlockSaplingTFC extends BlockBush implements IGrowable
{
    private static final EnumMap<Wood, BlockSaplingTFC> MAP = new EnumMap<>(Wood.class);
    public final Wood wood;
    public static BlockSaplingTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public BlockSaplingTFC(Wood wood)
    {
        super();
        this.wood = wood;
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{});
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote) {
            super.updateTick(world, pos, state, random);
            if (world.getLightFromNeighbors(pos.up()) >= 9 && random.nextInt(7) == 0) {
                this.grow(world, pos, state, random);
            }
        }

    }

    public void grow(World world, BlockPos blockPos, IBlockState blockState, Random random) {
        /*if ((Integer)state.getValue(STAGE) == 0) {
            world.setBlockState(pos, state.cycleProperty(STAGE), 4);
        } else {*/
            this.generateTree(world, blockPos, blockState, random);
        //}

    }

    public void generateTree(World world, BlockPos blockPos, IBlockState blockState, Random random) {
        /*if (TerrainGen.saplingGrowTree(world, random, blockPos)) {
            WorldGenerator worldgenerator = random.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
            int i = 0;
            int j = 0;
            boolean flag = false;
            IBlockState iblockstate;
            switch((BlockPlanks.EnumType)blockState.getValue(TYPE)) {
                case SPRUCE:
                    label70:
                    for(i = 0; i >= -1; --i) {
                        for(j = 0; j >= -1; --j) {
                            if (this.isTwoByTwoOfType(world, blockPos, i, j, BlockPlanks.EnumType.SPRUCE)) {
                                worldgenerator = new WorldGenMegaPineTree(false, random.nextBoolean());
                                flag = true;
                                break label70;
                            }
                        }
                    }

                    if (!flag) {
                        i = 0;
                        j = 0;
                        worldgenerator = new WorldGenTaiga2(true);
                    }
                    break;
                case BIRCH:
                    worldgenerator = new WorldGenBirchTree(true, false);
                    break;
                case JUNGLE:
                    iblockstate = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
                    IBlockState iblockstate1 = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, false);

                    label84:
                    for(i = 0; i >= -1; --i) {
                        for(j = 0; j >= -1; --j) {
                            if (this.isTwoByTwoOfType(world, blockPos, i, j, BlockPlanks.EnumType.JUNGLE)) {
                                worldgenerator = new WorldGenMegaJungle(true, 10, 20, iblockstate, iblockstate1);
                                flag = true;
                                break label84;
                            }
                        }
                    }

                    if (!flag) {
                        i = 0;
                        j = 0;
                        worldgenerator = new WorldGenTrees(true, 4 + random.nextInt(7), iblockstate, iblockstate1, false);
                    }
                    break;
                case ACACIA:
                    worldgenerator = new WorldGenSavannaTree(true);
                    break;
                case DARK_OAK:
                    label98:
                    for(i = 0; i >= -1; --i) {
                        for(j = 0; j >= -1; --j) {
                            if (this.isTwoByTwoOfType(world, blockPos, i, j, BlockPlanks.EnumType.DARK_OAK)) {
                                worldgenerator = new WorldGenCanopyTree(true);
                                flag = true;
                                break label98;
                            }
                        }
                    }

                    if (!flag) {
                        return;
                    }
                case OAK:
            }

            iblockstate = Blocks.AIR.getDefaultState();
            if (flag) {
                world.setBlockState(blockPos.add(i, 0, j), iblockstate, 4);
                world.setBlockState(blockPos.add(i + 1, 0, j), iblockstate, 4);
                world.setBlockState(blockPos.add(i, 0, j + 1), iblockstate, 4);
                world.setBlockState(blockPos.add(i + 1, 0, j + 1), iblockstate, 4);
            } else {
                world.setBlockState(blockPos, iblockstate, 4);
            }

            if (!((WorldGenerator)worldgenerator).generate(world, random, blockPos.add(i, 0, j))) {
                if (flag) {
                    world.setBlockState(blockPos.add(i, 0, j), blockState, 4);
                    world.setBlockState(blockPos.add(i + 1, 0, j), blockState, 4);
                    world.setBlockState(blockPos.add(i, 0, j + 1), blockState, 4);
                    world.setBlockState(blockPos.add(i + 1, 0, j + 1), blockState, 4);
                } else {
                    world.setBlockState(blockPos, blockState, 4);
                }
            }

        }*/
    }

    @Override
    public boolean canGrow(World world, BlockPos blockPos, IBlockState iBlockState, boolean b) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random random, BlockPos blockPos, IBlockState iBlockState) {
        return true;
    }

    @Override
    public void grow(World world, Random random, BlockPos blockPos, IBlockState iBlockState) {

    }
}
