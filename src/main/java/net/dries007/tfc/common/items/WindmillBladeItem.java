/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.common.blocks.rotation.WindmillBlock;

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
            if (player == null || !player.isCreative())
            {
                context.getItemInHand().shrink(1);
            }

            final BlockState windmillState = axle.getWindmill()
                .defaultBlockState()
                .setValue(WindmillBlock.AXIS, state.getValue(AxleBlock.AXIS))
                .setValue(WindmillBlock.COUNT, 1);

            level.setBlockAndUpdate(pos, windmillState);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
