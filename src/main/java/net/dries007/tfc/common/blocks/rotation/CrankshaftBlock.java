/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rotation;

import java.util.Locale;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

public class CrankshaftBlock extends HorizontalDirectionalBlock implements IForgeBlockExtension, EntityBlockExtension
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Part> PART = TFCBlockStateProperties.CRANKSHAFT_PART;

    private static final VoxelShape[] BASE_SHAPES = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Helpers.rotateShape(dir, 0, 0, 4, 16, 2, 12),
        Helpers.rotateShape(dir, 0, 2, 4, 4, 12, 12)
    ));

    private static final VoxelShape[] SHAFT_SHAPES = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 0, 7, 8, 16, 9, 10));
    private static final TagKey<Item> STEEL_RODS = TagKey.create(Registries.ITEM, Helpers.resourceLocation("forge", "rods/steel"));

    private final ExtendedProperties properties;

    public CrankshaftBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;

        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(PART, Part.BASE));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (state.getValue(PART) == Part.BASE && Helpers.isItem(held.getItem(), STEEL_RODS))
        {
            final BlockPos partnerPos = getPartnerPos(pos, state);
            final BlockState stateAt = level.getBlockState(partnerPos);
            if (stateAt.canBeReplaced() && stateAt.getFluidState().isEmpty())
            {
                level.setBlockAndUpdate(partnerPos, state.setValue(PART, Part.SHAFT));
                if (!player.isCreative())
                    held.shrink(1);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (state.getValue(PART) == Part.SHAFT && facingPos.equals(getPartnerPos(currentPos, state)))
        {
            return facingState.getBlock() == this && facingState.getValue(FACING) == state.getValue(FACING) ? state : Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        final int index = state.getValue(FACING).get2DDataValue();
        return state.getValue(PART) == Part.BASE ? BASE_SHAPES[index] : SHAFT_SHAPES[(index + 3) % 4]; // Ultimately the +3 means I messed up making the bounding box, but I cannot be arsed to fix it.
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, PART);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec()
    {
        return fakeBlockCodec();
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
