/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LargeVesselBlock extends Block
{
    public static final BooleanProperty SEALED = TFCBlockStateProperties.SEALED;

    public static final VoxelShape OPENED_SHAPE = box(3D, 0D, 3D, 13D, 10D, 13D);
    public static final VoxelShape CLOSED_SHAPE = Shapes.or(
        OPENED_SHAPE,
        box(2.5D, 9.5D, 2.5D, 13.5D, 11D, 13.5D),
        box(7D, 11D, 7D, 9D, 12D, 9D)
    );

    public LargeVesselBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any().setValue(SEALED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(SEALED) ? CLOSED_SHAPE : OPENED_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(SEALED));
    }


    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        level.setBlockAndUpdate(pos, state.setValue(SEALED, !state.getValue(SEALED)));
        return InteractionResult.SUCCESS;
    }
}
