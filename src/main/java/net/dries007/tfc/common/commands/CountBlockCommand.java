/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import java.util.function.Predicate;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public final class CountBlockCommand
{
    private static final String DONE = "tfc.commands.count_block.done";

    public static LiteralArgumentBuilder<CommandSourceStack> create(CommandBuildContext cmdContext)
    {
        return Commands.literal("count")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("radius", IntegerArgumentType.integer(1, 250))
                .then(Commands.argument("block", BlockPredicateArgument.blockPredicate(cmdContext))
                    .executes(context -> countBlock(
                        context.getSource(),
                        IntegerArgumentType.getInteger(context, "radius"),
                        BlockPredicateArgument.getBlockPredicate(context, "block")
                    ))
                )
            );
    }

    private static int countBlock(CommandSourceStack source, int radius, Predicate<BlockInWorld> block)
    {
        final Level level = source.getLevel();
        final BlockPos center = BlockPos.containing(source.getPosition());

        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        BlockPos example = null;

        int found = 0;
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++)
                {
                    cursor.set(center).move(x, 0, z).setY(y);
                    if (block.test(new BlockInWorld(level, cursor, true)))
                    {
                        if (example == null)
                            example = cursor.immutable();
                        found++;
                    }
                }
            }
        }
        int finalFound = found;
        final BlockPos fExample = example;
        source.sendSuccess(() -> {
            final MutableComponent component = Component.translatable(DONE, finalFound);
            if (fExample != null)
            {
                component.append(" ");
                component.append(ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", fExample.getX(), fExample.getY(), fExample.getZ()))
                    .withStyle(
                        p_214489_ -> p_214489_.withColor(ChatFormatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + fExample.getX() + " " + fExample.getY() + " " + fExample.getZ()))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
                    ));
            }

            return component;
        }, true);
        return Command.SINGLE_SUCCESS;
    }
}
