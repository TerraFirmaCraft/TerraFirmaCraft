/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.CalenderTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class BlockCactusTFC extends BlockStackPlantTFC implements IGrowable
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
    protected static final AxisAlignedBB CACTUS_COLLISION_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.9375D, 0.9375D);
    private static final Map<Plant, EnumMap<Plant.PlantType, BlockCactusTFC>> TABLE = new HashMap<>();

    public static BlockCactusTFC get(Plant plant, Plant.PlantType type)
    {
        return TABLE.get(plant).get(type);
    }

    public final Plant plant;
    public final Plant.PlantType type;

    public BlockCactusTFC(Plant plant, Plant.PlantType type)
    {
        super(plant, type);
        if (!TABLE.containsKey(plant))
            TABLE.put(plant, new EnumMap<>(Plant.PlantType.class));
        TABLE.get(plant).put(type, this);

        this.plant = plant;
        this.type = type;

        setSoundType(SoundType.GROUND);
        setHardness(0.25F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(PART, EnumBlockPart.SINGLE));
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        int i;
        for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {}
        return i < 3 && worldIn.isAirBlock(pos.up()) && canBlockStay(worldIn, pos.up(), state);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockState(pos.up(), this.getDefaultState());
        IBlockState iblockstate = state.withProperty(TIME, getCurrentTime(worldIn)).withProperty(AGE, 0).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(PART, state.getValue(PART));
        worldIn.setBlockState(pos, iblockstate);
        iblockstate.neighborChanged(worldIn, pos.up(), this, pos);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, CACTUS_COLLISION_AABB.offset(state.getOffset(worldIn, pos)));
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(TIME, state.getValue(TIME)).withProperty(AGE, state.getValue(AGE)).withProperty(GROWTHSTAGE, state.getValue(GROWTHSTAGE)).withProperty(PART, getPlantPart(worldIn, pos));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE, GROWTHSTAGE, PART, TIME});
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FULL_BLOCK_AABB.offset(state.getOffset(source, pos));
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
        int currentTime = state.getValue(TIME);
        int expectedTime = getCurrentTime(worldIn);

        if (currentTime != expectedTime)
        {
            worldIn.setBlockState(pos, state.withProperty(TIME, expectedTime).withProperty(AGE, state.getValue(AGE)).withProperty(GROWTHSTAGE, currentStage).withProperty(PART, state.getValue(PART)));
        }
        if (currentStage != expectedStage && random.nextDouble() < 0.5)
        {
            worldIn.setBlockState(pos, state.withProperty(TIME, expectedTime).withProperty(AGE, state.getValue(AGE)).withProperty(GROWTHSTAGE, expectedStage).withProperty(PART, state.getValue(PART)));
        }
        this.updateTick(worldIn, pos, state, random);
    }

    @Override
    public Block.EnumOffsetType getOffsetType()
    {
        return EnumOffsetType.XYZ;
    }

    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY;
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        return BlocksTFC.isSand(state);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;

        if (ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) > 20 && plant.isValidSunlight(worldIn.getLightFromNeighbors(pos.up())) && canGrow(worldIn, pos, state, worldIn.isRemote))
        {
            int j = state.getValue(AGE);

            if (rand.nextFloat() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos.up(), state, true))
            {
                if (j == 15)
                {
                    grow(worldIn, rand, pos, state);
                }
                else
                {
                    worldIn.setBlockState(pos, state.withProperty(TIME, getCurrentTime(worldIn)).withProperty(AGE, j + 1).withProperty(GROWTHSTAGE, state.getValue(GROWTHSTAGE)).withProperty(PART, state.getValue(PART)));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
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

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Desert;
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
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return CACTUS_COLLISION_AABB.offset(blockState.getOffset(worldIn, pos));
    }
}
