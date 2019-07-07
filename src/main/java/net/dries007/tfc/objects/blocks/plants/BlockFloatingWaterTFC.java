/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
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

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.BlocksTFC;

@ParametersAreNonnullByDefault
public class BlockFloatingWaterTFC extends BlockPlantTFC
{
    private static final AxisAlignedBB LILY_PAD_AABB = new AxisAlignedBB(0.0D, -0.125D, 0.0D, 1.0D, 0.0625D, 1.0D);
    private static final Map<Plant, BlockFloatingWaterTFC> MAP = new HashMap<>();

    public static BlockFloatingWaterTFC get(Plant plant)
    {
        return BlockFloatingWaterTFC.MAP.get(plant);
    }

    public BlockFloatingWaterTFC(Plant plant)
    {
        super(plant);
        if (MAP.put(plant, this) != null) throw new IllegalStateException("There can only be one.");
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        world.setBlockState(pos, state.withProperty(DAYPERIOD, getDayPeriod()).withProperty(growthStageProperty, plant.getStageForMonth()));
        this.checkAndDropBlock(world, pos, state);
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
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.NONE;
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        return (BlocksTFC.isWater(state) || state.getMaterial() == Material.ICE && state == plant.getWaterType()) || (state.getMaterial() == Material.CORAL && !(state.getBlock() instanceof BlockEmergentTallWaterPlantTFC));
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            IBlockState stateDown = worldIn.getBlockState(pos.down());
            Material material = stateDown.getMaterial();
            return (material == Material.WATER && stateDown.getValue(BlockLiquid.LEVEL) == 0 && stateDown == plant.getWaterType()) || material == Material.ICE || (material == Material.CORAL && !(state.getBlock() instanceof BlockEmergentTallWaterPlantTFC));
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
    protected BlockStateContainer createPlantBlockState()
    {
        return new BlockStateContainer(this, growthStageProperty, DAYPERIOD, AGE);
    }
}
