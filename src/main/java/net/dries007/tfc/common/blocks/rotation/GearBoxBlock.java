/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.rotation.RotatingBlockEntity;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;

public class GearBoxBlock extends DeviceBlock implements DirectionPropertyBlock, ConnectedAxleBlock
{
    public GearBoxBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.NOOP);
        registerDefaultState(DirectionPropertyBlock.setAllDirections(getStateDefinition().any(), false));
    }

    @Override
    public AxleBlock getAxle()
    {
        // todo: once this is split by wood type, take in an axle via ctor and return that here
        return (AxleBlock) TFCBlocks.WOODS.get(Wood.ASH).get(Wood.BlockType.AXLE).get();
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (Helpers.isItem(player.getItemInHand(hand), TFCTags.Items.HAMMERS))
        {
            final BooleanProperty property = DirectionPropertyBlock.getProperty(result.getDirection());
            final boolean prev = state.getValue(property);
            if (prev || canEnable(state, property))
            {
                level.setBlockAndUpdate(pos, state.cycle(property));
                level.getBlockEntity(pos, TFCBlockEntities.GEAR_BOX.get()).ifPresent(box -> box.updateDirection(result.getDirection(), !prev));
                Helpers.playPlaceSound(level, pos, state);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else
            {
                Helpers.playSound(level, pos, SoundEvents.ITEM_BREAK);
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * If the output face {@code direction} can be enabled.
     * We model these as four-gear internal gearboxes, which means they must have one axis not present.
     */
    private boolean canEnable(BlockState state, BooleanProperty direction)
    {
        state = state.setValue(direction, true);
        return !(state.getValue(NORTH) || state.getValue(SOUTH))
            || !(state.getValue(EAST) || state.getValue(WEST))
            || !(state.getValue(UP) || state.getValue(DOWN));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (level.getBlockEntity(pos) instanceof RotatingBlockEntity entity)
        {
            entity.destroyIfInvalid(level, pos);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return DirectionPropertyBlock.rotate(state, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return DirectionPropertyBlock.mirror(state, mirror);
    }
}
