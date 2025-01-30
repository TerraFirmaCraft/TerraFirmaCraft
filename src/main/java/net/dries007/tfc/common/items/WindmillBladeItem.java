/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.rotation.WindmillBlockEntity;
import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.common.blocks.rotation.WindmillBlock;
import net.dries007.tfc.util.Helpers;

public class WindmillBladeItem extends Item
{
    public WindmillBladeItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final Player player = context.getPlayer();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof AxleBlock axle && state.getValue(AxleBlock.AXIS) != Direction.Axis.Y)
        {
            final Direction.Axis axis = state.getValue(AxleBlock.AXIS); // Don't move this outside the if() statement! It needs to check for an axle block first!
            if (WindmillBlockEntity.isObstructedBySolidBlocks(level, pos, axis))
            {
                if (player != null)
                {
                    player.displayClientMessage(Component.translatable("tfc.tooltip.windmill_not_enough_space"), true);
                }
                return InteractionResult.FAIL;
            }


            final BlockState windmillState = axle.getWindmill()
                .defaultBlockState()
                .setValue(WindmillBlock.AXIS, axis)
                .setValue(WindmillBlock.COUNT, 1);

            level.setBlockAndUpdate(pos, windmillState);
            Helpers.insertOne(level, pos, TFCBlockEntities.WINDMILL, context.getItemInHand().copyWithCount(1));

            if (player == null || !player.isCreative())
            {
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
