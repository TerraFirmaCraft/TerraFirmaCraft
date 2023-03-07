/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blocks.crop.DoubleCropBlock;
import net.dries007.tfc.common.blocks.crop.ICropBlock;
import net.dries007.tfc.util.Helpers;
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
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.hasProperty(DoubleCropBlock.PART) && state.getValue(DoubleCropBlock.PART) == DoubleCropBlock.Part.TOP)
        {
            pos = pos.below();
            state = level.getBlockState(pos);
        }

        if (state.getBlock() instanceof ICropBlock cropBlock)
        {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof CropBlockEntity cropBlockEntity)
            {
                final boolean mature = cropBlockEntity.getGrowth() >= 1f;
                cropBlock.die(level, pos, state, mature);
                Helpers.playSound(level, pos, TFCSounds.FERTILIZER_USE.get());
                if (context.getPlayer() instanceof ServerPlayer player)
                {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(player, pos, context.getItemInHand());
                    TFCAdvancements.ROTTEN_COMPOST_KILL.trigger(player);
                }
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }
}
