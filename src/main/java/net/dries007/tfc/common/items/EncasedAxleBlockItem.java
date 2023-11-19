/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.util.Helpers;

/**
 * Allows encased axle block items to be placed both naturally (where the axle inside is made from the crafting recipe)
 */
public class EncasedAxleBlockItem extends BlockItem
{
    public EncasedAxleBlockItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = level.getBlockState(pos);
        final Player player = context.getPlayer();

        if (state.getBlock() instanceof AxleBlock)
        {
            final BlockState toPlace = Helpers.copyProperties(getBlock().defaultBlockState(), state);

            level.setBlockAndUpdate(pos, toPlace);

            if (player != null && !player.isCreative())
            {
                context.getItemInHand().shrink(1);
            }

            Helpers.playPlaceSound(level, pos, toPlace);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
