/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.entities.Seat;
import net.dries007.tfc.util.Helpers;

public class NestBoxBlock extends BottomSupportedDeviceBlock
{
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 5, 14);

    public NestBoxBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP, SHAPE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (!player.isShiftKeyDown() && player instanceof ServerPlayer serverPlayer)
        {
            level.getBlockEntity(pos, TFCBlockEntities.NEST_BOX.get()).ifPresent(nest -> Helpers.openScreen(serverPlayer, nest, pos));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        super.onRemove(state, level, pos, newState, isMoving);
        final Entity sitter = Seat.getSittingEntity(level, pos);
        if (sitter != null)
        {
            sitter.stopRiding();
        }
    }
}
