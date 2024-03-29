/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.PropickItem;
import net.dries007.tfc.common.items.ProspectResult;
import net.dries007.tfc.util.Helpers;

public class PropickCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("propick")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("scan")
                .executes(cmd -> scan(cmd.getSource()))
            )
            .then(Commands.literal("clearworld")
                .executes(cmd -> clearWorld(cmd.getSource()))
            );
    }

    public static int scan(CommandSourceStack source)
    {
        final Object2IntMap<Block> found = PropickItem.scanAreaFor(source.getLevel(), BlockPos.containing(source.getPosition()), PropickItem.RADIUS, TFCTags.Blocks.PROSPECTABLE);
        if (found.isEmpty())
        {
            source.sendSuccess(() -> ProspectResult.NOTHING.getText(Blocks.AIR), true);
        }
        else
        {
            for (Object2IntMap.Entry<Block> entry : found.object2IntEntrySet())
            {
                source.sendSuccess(() -> Component.translatable("tfc.commands.propick.found_blocks", entry.getIntValue(), entry.getKey().getName()), true);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int clearWorld(CommandSourceStack source)
    {
        final ServerLevel level = source.getLevel();
        final BlockPos center = BlockPos.containing(source.getPosition());
        final int radius = PropickItem.RADIUS;
        final BlockState air = Blocks.AIR.defaultBlockState();;
        int found = 0;
        int cleared = 0;
        for (BlockPos cursor : BlockPos.betweenClosed(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius))
        {
            final BlockState state = level.getBlockState(cursor);
            if (!Helpers.isBlock(state, TFCTags.Blocks.PROSPECTABLE))
            {
                level.setBlock(cursor, air, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                cleared++;
            }
            else
            {
                found++;
            }
        }
        final int finalFound = found;
        final int finalCleared = cleared;
        source.sendSuccess(() -> Component.translatable("tfc.commands.propick.cleared", finalFound, finalCleared), true);
        return Command.SINGLE_SUCCESS;
    }
}
