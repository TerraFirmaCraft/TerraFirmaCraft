/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.crop.ICropBlock;
import net.dries007.tfc.util.advancements.TFCAdvancements;

public class RottenCompostItem extends Item
{
    public RottenCompostItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof ICropBlock crop)
        {
            if (!level.isClientSide)
            {
                final boolean mature = level.getBlockEntity(pos, TFCBlockEntities.CROP.get()).map(be -> be.getGrowth() >= 1f).orElse(false);
                crop.die(level, pos, state, mature);
                if (context.getPlayer() instanceof ServerPlayer player)
                {
                    TFCAdvancements.ROTTEN_COMPOST_KILL.trigger(player);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }
}
