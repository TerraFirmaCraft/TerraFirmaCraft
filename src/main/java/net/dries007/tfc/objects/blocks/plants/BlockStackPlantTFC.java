/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.world.classic.CalenderTFC;

@ParametersAreNonnullByDefault
public class BlockStackPlantTFC extends BlockPlantTFC
{
    protected static final PropertyEnum<BlockStackPlantTFC.EnumBlockPart> PART = PropertyEnum.create("part", BlockStackPlantTFC.EnumBlockPart.class);
    private static final Map<Plant, BlockStackPlantTFC> MAP = new HashMap<>();

    public static BlockStackPlantTFC get(Plant plant)
    {
        return MAP.get(plant);
    }

    public BlockStackPlantTFC(Plant plant)
    {
        super(plant);

        if (MAP.put(plant, this) != null) throw new IllegalStateException("There can only be one.");

        this.setDefaultState(this.blockState.getBaseState().withProperty(DAYPERIOD, getDayPeriod()).withProperty(GROWTHSTAGE, CalenderTFC.Month.MARCH.id()).withProperty(PART, EnumBlockPart.SINGLE));
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable)
    {
        IBlockState plant = plantable.getPlant(world, pos.offset(direction));

        if (plant.getBlock() == this)
        {
            return true;
        }
        return super.canSustainPlant(state, world, pos, direction, plantable);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    @Nonnull
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(DAYPERIOD, getDayPeriod()).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(PART, getPlantPart(worldIn, pos));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, GROWTHSTAGE, PART, DAYPERIOD);
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FULL_BLOCK_AABB.offset(state.getOffset(source, pos));
    }

    protected EnumBlockPart getPlantPart(IBlockAccess world, BlockPos pos)
    {
        if (world.getBlockState(pos.down()).getBlock() != this && world.getBlockState(pos.up()).getBlock() == this)
        {
            return EnumBlockPart.LOWER;
        }
        if (world.getBlockState(pos.down()).getBlock() == this && world.getBlockState(pos.up()).getBlock() == this)
        {
            return EnumBlockPart.MIDDLE;
        }
        if (world.getBlockState(pos.down()).getBlock() == this && world.getBlockState(pos.up()).getBlock() != this)
        {
            return EnumBlockPart.UPPER;
        }
        return EnumBlockPart.SINGLE;
    }

    public enum EnumBlockPart implements IStringSerializable
    {
        UPPER,
        MIDDLE,
        LOWER,
        SINGLE;

        public String toString()
        {
            return this.getName();
        }

        public String getName()
        {
            return name().toLowerCase();
        }
    }
}