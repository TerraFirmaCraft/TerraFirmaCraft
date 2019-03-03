/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.world.classic.CalenderTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class BlockDoublePlantTFC extends BlockStackPlantTFC
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
    protected static final AxisAlignedBB PLANT_AABB = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 1.0D, 0.8125D);
    private static final Map<Plant, EnumMap<Plant.PlantType, BlockDoublePlantTFC>> TABLE = new HashMap<>();

    public static BlockDoublePlantTFC get(Plant plant, Plant.PlantType type)
    {
        return BlockDoublePlantTFC.TABLE.get(plant).get(type);
    }

    public final Plant plant;
    public final Plant.PlantType type;

    public BlockDoublePlantTFC(Plant plant, Plant.PlantType type)
    {
        super(plant, type);
        if (!TABLE.containsKey(plant))
            TABLE.put(plant, new EnumMap<>(Plant.PlantType.class));
        TABLE.get(plant).put(type, this);

        this.plant = plant;
        this.type = type;
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(PART, EnumBlockPart.SINGLE));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) ? this.canBlockStay(worldIn, pos, worldIn.getBlockState(pos)) : false;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            boolean flag = state.getValue(PART) == EnumBlockPart.UPPER;
            BlockPos blockpos = flag ? pos : pos.up();
            BlockPos blockpos1 = flag ? pos.down() : pos;
            Block block = (Block) (flag ? this : worldIn.getBlockState(blockpos).getBlock());
            Block block1 = (Block) (flag ? worldIn.getBlockState(blockpos1).getBlock() : this);

            if (!flag) this.dropBlockAsItem(worldIn, pos, state, 0);

            if (block == this)
            {
                worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
            }

            if (block1 == this)
            {
                worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
            }
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(AGE, state.getValue(AGE)).withProperty(GROWTHSTAGE, state.getValue(GROWTHSTAGE)).withProperty(PART, getPlantPart(worldIn, pos));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE, GROWTHSTAGE, PART});
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return PLANT_AABB.offset(state.getOffset(source, pos));
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, meta).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id());
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;
        int currentStage = state.getValue(GROWTHSTAGE);
        int expectedStage = CalenderTFC.getMonthOfYear().id();

        if (currentStage != expectedStage && random.nextDouble() < 0.5)
        {
            worldIn.setBlockState(pos, state.withProperty(AGE, state.getValue(AGE)).withProperty(GROWTHSTAGE, expectedStage).withProperty(PART, state.getValue(PART)));
        }
        this.updateTick(worldIn, pos, state, random);
    }

    @Override
    public Block.EnumOffsetType getOffsetType()
    {
        return EnumOffsetType.XYZ;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;
        BlockPos blockPos = pos.up();

        if (ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) < 15 && worldIn.getBlockState(pos.down()).getBlock() == this)
        {
            int j = state.getValue(AGE);

            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true))
            {
                if (j <= 15)
                {
                    worldIn.setBlockToAir(pos);
                    worldIn.getBlockState(pos).neighborChanged(worldIn, pos.down(), this, pos);
                }
                else
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j + 1).withProperty(GROWTHSTAGE, state.getValue(GROWTHSTAGE)).withProperty(PART, state.getValue(PART)));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }
        else if (worldIn.isAirBlock(blockPos) && ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) > 15)
        {
            int i;

            for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i)
            {
                ;
            }

            if (i < 2)
            {
                int j = state.getValue(AGE);

                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, blockPos, state, true))
                {
                    if (j == 15)
                    {
                        worldIn.setBlockState(blockPos, this.getDefaultState());
                        IBlockState iblockstate = state.withProperty(AGE, 0).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(PART, state.getValue(PART));
                        worldIn.setBlockState(pos, iblockstate);
                        iblockstate.neighborChanged(worldIn, blockPos, this, pos);
                    }
                    else
                    {
                        worldIn.setBlockState(pos, state.withProperty(AGE, j + 1).withProperty(GROWTHSTAGE, state.getValue(GROWTHSTAGE)).withProperty(PART, state.getValue(PART)));
                    }
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                }
            }
        }

        if (!canBlockStay(worldIn, pos, state))
        {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());

        if (worldIn.getBlockState(pos.down(2)).getBlock() == this) return false;
        if (state.getBlock() == this)
        {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                IBlockState blockState = worldIn.getBlockState(pos.offset(enumfacing));
                Material material = blockState.getMaterial();

                if (material.isSolid() || material == Material.LAVA)
                {
                    return blockState.getBlock() == this;
                }
            }

            return soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this) && plant.isValidLocation(ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos), ChunkDataTFC.getRainfall(worldIn, pos));
        }
        return this.canSustainBush(soil);
    }
}