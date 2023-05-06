/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.mechanical;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
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
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.util.Helpers;

public class GearBoxBlock extends DeviceBlock implements DirectionPropertyBlock
{
    public static final int PORTS_MAX = 2;

    public GearBoxBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.NOOP);
        registerDefaultState(getStateDefinition().any().setValue(UP, false).setValue(DOWN, false).setValue(EAST, false).setValue(WEST, false).setValue(NORTH, false).setValue(SOUTH, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (Helpers.isItem(player.getItemInHand(hand), TFCTags.Items.HAMMERS))
        {
            final BooleanProperty property = PROPERTY_BY_DIRECTION.get(result.getDirection());
            if (isMaxedOut(state) && !state.getValue(property))
            {
                Helpers.playSound(level, pos, SoundEvents.ITEM_BREAK);
                return InteractionResult.FAIL;
            }
            else
            {
                level.setBlockAndUpdate(pos, state.cycle(property));
                Helpers.playPlaceSound(level, pos, state);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    private boolean isMaxedOut(BlockState state)
    {
        int ports = 0;
        for (BooleanProperty prop : PROPERTY_BY_DIRECTION.values())
        {
            if (state.getValue(prop))
            {
                ports++;
                if (ports == PORTS_MAX)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST));
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
