/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.CalenderTFC;

import static net.dries007.tfc.world.classic.ChunkGenTFC.FRESH_WATER;

@ParametersAreNonnullByDefault
public class BlockFloatingWaterTFC extends BlockPlantTFC
{
    protected static final AxisAlignedBB LILY_PAD_AABB = new AxisAlignedBB(0.125D, -0.125D, 0.125D, 0.875D, 0.0625D, 0.875D);
    private static final Map<Plant, BlockFloatingWaterTFC> MAP = new HashMap<>();

    public static BlockFloatingWaterTFC get(Plant plant)
    {
        return BlockFloatingWaterTFC.MAP.get(plant);
    }

    public BlockFloatingWaterTFC(Plant plant)
    {
        super(plant);
        if (MAP.put(plant, this) != null) throw new IllegalStateException("There can only be one.");

        this.setDefaultState(this.blockState.getBaseState().withProperty(DAYPERIOD, getDayPeriod()).withProperty(GROWTHSTAGE, CalenderTFC.Month.MARCH.id()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        super.onEntityCollision(worldIn, pos, state, entityIn);

        if (entityIn instanceof EntityBoat)
        {
            worldIn.destroyBlock(new BlockPos(pos), true);
        }
    }

    @Override
    @Nonnull
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id());
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        world.setBlockState(pos, state.withProperty(DAYPERIOD, getDayPeriod()).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()));
        this.checkAndDropBlock(world, pos, state);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, GROWTHSTAGE, DAYPERIOD);
    }

    @Override
    @Nonnull
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.NONE;
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        return BlocksTFC.isWater(state) || state.getMaterial() == Material.ICE && state == plant.getWaterType();
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            IBlockState stateDown = worldIn.getBlockState(pos.down());
            Material material = stateDown.getMaterial();
            return (material == Material.WATER && stateDown.getValue(BlockLiquid.LEVEL) == 0 && stateDown == plant.getWaterType()) || material == Material.ICE;
        }
        else
        {
            return false;
        }
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return LILY_PAD_AABB.offset(state.getOffset(source, pos));
    }

    @Override
    @Nonnull
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Water;
    }
}
