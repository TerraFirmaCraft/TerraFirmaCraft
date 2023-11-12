/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.rotation.EncasedAxleBlockEntity;
import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.util.Helpers;

public class AxleCasingBlockItem extends BlockItem
{
    private final Supplier<? extends Block> encasedAxle;

    public AxleCasingBlockItem(Block block, Properties properties, Supplier<? extends Block> encasedAxle)
    {
        super(block, properties);
        this.encasedAxle = encasedAxle;
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
            final BlockState toPlace = Helpers.copyProperties(encasedAxle.get().defaultBlockState(), state);
            level.setBlockAndUpdate(pos, toPlace);
            final ItemStack internalStack = context.getItemInHand().copyWithCount(1);
            if (player != null && player.isCreative())
                context.getItemInHand().shrink(1);
            if (level.getBlockEntity(pos) instanceof EncasedAxleBlockEntity cased)
            {
                cased.setInternalItem(internalStack);
            }
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }
}
