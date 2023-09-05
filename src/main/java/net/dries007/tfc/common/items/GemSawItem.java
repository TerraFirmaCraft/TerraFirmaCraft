/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.glass.GlassOperation;
import net.dries007.tfc.util.Helpers;

public class GemSawItem extends GlassworkingItem
{
    public GemSawItem(Properties properties)
    {
        super(properties, GlassOperation.SAW);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Player player = context.getPlayer();
        final Level level = context.getLevel();
        final Direction dir = context.getHorizontalDirection();
        final ItemStack held = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        int destroyed = 0;
        while (Helpers.isBlock(state, TFCTags.Blocks.DESTROYED_BY_GEM_SAW) && destroyed < 4)
        {
            destroyed++;
            if (level instanceof ServerLevel server)
                Helpers.dropWithContext(server, state, pos, ctx -> ctx.withParameter(LootContextParams.TOOL, held), false);
//            Block.dropResources(state, level, pos, state.hasBlockEntity() ? level.getBlockEntity(pos) : null, player, held);
            level.destroyBlock(pos, false, player); // we have to drop resources manually as breaking from the level means the tool is ignored
            pos = pos.relative(dir);
            state = level.getBlockState(pos);
        }
        if (destroyed > 0)
        {
            if (player != null && !player.isCreative())
                held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
