/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public abstract class TFCTallGrassBlock extends ShortGrassBlock implements ITallPlant
{
    protected static final EnumProperty<Part> PART = TFCBlockStateProperties.TALL_PLANT_PART;
    protected static final VoxelShape PLANT_SHAPE = Block.makeCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape SHORTER_PLANT_SHAPE = Block.makeCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);

    public static TFCTallGrassBlock create(IPlant plant, Properties properties)
    {
        return new TFCTallGrassBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected TFCTallGrassBlock(Properties properties)
    {
        super(properties);

        setDefaultState(getDefaultState().with(PART, Part.LOWER));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        Part part = stateIn.get(PART);
        if (facing.getAxis() != Direction.Axis.Y || part == Part.LOWER != (facing == Direction.UP) || facingState.getBlock() == this && facingState.get(PART) != part)
        {
            return part == Part.LOWER && facing == Direction.DOWN && !stateIn.blockNeedsPostProcessing(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        else
        {
            return Blocks.AIR.getDefaultState();
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        if (state.get(PART) == Part.LOWER)
        {
            return super.isValidPosition(state, worldIn, pos);
        }
        else
        {
            BlockState blockstate = worldIn.getBlockState(pos.down());
            if (state.getBlock() != this)
            {
                return super.isValidPosition(state, worldIn, pos); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            }
            return blockstate.getBlock() == this && blockstate.get(PART) == Part.LOWER;
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos pos = context.getPos();
        return pos.getY() < 255 && context.getWorld().getBlockState(pos.up()).isReplaceable(context) ? super.getStateForPlacement(context) : null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        worldIn.setBlockState(pos.up(), getDefaultState().with(PART, Part.UPPER));
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (worldIn.isRemote)
        {
            if (player.isCreative())
            {
                if (state.get(PART) == Part.UPPER)
                {
                    BlockPos blockpos = pos.down();
                    BlockState blockstate = worldIn.getBlockState(blockpos);
                    if (blockstate.getBlock() == state.getBlock() && blockstate.get(PART) == Part.LOWER)
                    {
                        worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                        worldIn.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
                    }
                }
            }
            else
            {
                spawnDrops(state, worldIn, pos, null, player, player.getHeldItemMainhand());
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        Part part = state.get(PART);
        if (part == Part.LOWER)
            return PLANT_SHAPE;
        return SHORTER_PLANT_SHAPE;
    }

    @Override
    public OffsetType getOffsetType()
    {
        return OffsetType.XYZ;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(PART);
    }

    public void placeTwoHalves(IWorld world, BlockPos pos, int flags, Random random)
    {
        int age = random.nextInt(3) + 1;
        world.setBlockState(pos, updateStateWithCurrentMonth(getDefaultState().with(TFCBlockStateProperties.TALL_PLANT_PART, Part.LOWER).with(TFCBlockStateProperties.AGE_3, age)), flags);
        world.setBlockState(pos.up(), updateStateWithCurrentMonth(getDefaultState().with(TFCBlockStateProperties.TALL_PLANT_PART, Part.UPPER).with(TFCBlockStateProperties.AGE_3, age)), flags);
    }
}
