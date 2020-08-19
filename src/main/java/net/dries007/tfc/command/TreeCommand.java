/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public final class TreeCommand
{
    public static LiteralArgumentBuilder<CommandSource> create()
    {
        return Commands.literal("tree")
            .requires(source -> source.hasPermissionLevel(2))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(context -> placeTree(context.getSource().getWorld(), BlockPosArgument.getBlockPos(context, "pos")))
            );
    }

    private static int placeTree(ServerWorld world, BlockPos pos)
    {
        throw new UnsupportedOperationException("Not implemented");
    }
}
