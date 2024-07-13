/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.ISlowEntities;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.TFCConfig;

public class FallenLeavesBlock extends GroundcoverBlock implements ISlowEntities
{
    public static final int MAX_LAYERS = 8;
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    private final Supplier<? extends Block> leaves;

    public FallenLeavesBlock(ExtendedProperties properties, Supplier<? extends Block> leaves)
    {
        super(properties, Shapes.block());
        this.leaves = leaves;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final ItemStack item = player.getItemInHand(hand);
        final int layers = state.getValue(LAYERS);
        if (item.getItem() == asItem() && layers < MAX_LAYERS)
        {
            if (!player.isCreative())
                item.shrink(1);
            final BlockState toPlace = layers + 1 == MAX_LAYERS ? leaves.get().defaultBlockState().setValue(TFCLeavesBlock.PERSISTENT, true) : state.setValue(LAYERS, layers + 1);
            level.setBlockAndUpdate(pos, toPlace);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        else if (layers == 1 && item.getItem() != asItem())
        {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (state.getValue(LAYERS) == MAX_LAYERS)
        {
            final FluidState fluid = state.getFluidState();
            BlockState newState = leaves.get().defaultBlockState().setValue(TFCLeavesBlock.PERSISTENT, true);
            newState = FluidHelpers.fillWithFluid(newState, fluid.getType());
            if (newState != null)
            {
                return newState;
            }
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return true; // Not for the purposes of leaf decay, but for the purposes of seasonal updates
    }

    @Override
    public float slowEntityFactor(BlockState state)
    {
        return state.getValue(LAYERS) == 1 || !state.getFluidState().isEmpty() ? NO_SLOW : (float) (state.getValue(LAYERS) / (float) MAX_LAYERS * TFCConfig.SERVER.leavesMovementModifier.get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LAYERS));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return CharcoalPileBlock.SHAPE_BY_LAYER[state.getValue(LAYERS)];
    }

    public Supplier<? extends Block> getLeaves()
    {
        return leaves;
    }
}
