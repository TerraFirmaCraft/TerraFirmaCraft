/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rotation;

import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

public class CrankshaftBlock extends ExtendedBlock implements IForgeBlockExtension, EntityBlockExtension
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Part> PART = TFCBlockStateProperties.CRANKSHAFT_PART;

    private static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Helpers.rotateShape(dir, 1, 0, 4, 14, 2, 12),
        Helpers.rotateShape(dir, 1, 2, 5, 4, 9, 11),
        Helpers.rotateShape(dir, 11, 0, 1, 15, 9, 3)
    ));

    public CrankshaftBlock(ExtendedProperties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(PART, Part.BASE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (state.getValue(PART) == Part.BASE && held.getItem() == TFCItems.METAL_ITEMS.get(Metal.Default.STEEL).get(Metal.ItemType.ROD).get())
        {
            final BlockPos partnerPos = getPartnerPos(pos, state);
            final BlockState stateAt = level.getBlockState(partnerPos);
            if (stateAt.canBeReplaced() && stateAt.getFluidState().isEmpty())
            {
                level.setBlockAndUpdate(partnerPos, state.setValue(PART, Part.SHAFT));
                if (!player.isCreative())
                    held.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(PART, Part.BASE);
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockPos partnerPos = getPartnerPos(pos, state);
        final BlockState stateAt = level.getBlockState(pos);
        final BlockState stateAtPartner = level.getBlockState(partnerPos);
        if (stateAtPartner.canBeReplaced() && stateAtPartner.getFluidState().isEmpty() && stateAt.getFluidState().isEmpty())
        {
            return state;
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (state.getValue(PART) == Part.SHAFT && facingPos.equals(getPartnerPos(currentPos, state)))
        {
            return facingState.getBlock() == this && facingState.getValue(FACING) == state.getValue(FACING) ? state : Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(PART) == Part.BASE ? SHAPES[state.getValue(FACING).get2DDataValue()] : state.getValue(FACING).getAxis() == Direction.Axis.X ? AxleBlock.SHAPE_X : AxleBlock.SHAPE_Z;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, PART);
    }

    private BlockPos getPartnerPos(BlockPos pos, BlockState state)
    {
        final Direction facing = state.getValue(FACING);
        return state.getValue(PART) == Part.BASE ? pos.relative(facing) : pos.relative(facing.getOpposite());
    }

    public enum Part implements StringRepresentable
    {
        BASE, SHAFT;

        @Override
        public String getSerializedName()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
