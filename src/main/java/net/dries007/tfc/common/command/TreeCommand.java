/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.command;

import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.command.EnumArgument;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dries007.tfc.common.types.Wood;

public final class TreeCommand
{
    public static LiteralArgumentBuilder<CommandSource> create()
    {
        return Commands.literal("tree")
            .requires(source -> source.hasPermissionLevel(2))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .then(Commands.argument("wood", EnumArgument.enumArgument(Wood.Default.class))
                    .executes(context -> placeTree(context.getSource().getWorld(), BlockPosArgument.getBlockPos(context, "pos"), context.getArgument("wood", Wood.Default.class)))
                )
            );
    }

    private static int placeTree(ServerWorld world, BlockPos pos, Wood.Default wood)
    {
        BlockState stateIn = world.getBlockState(pos);
        wood.getTree().place(world, world.getChunkProvider().getChunkGenerator(), pos, stateIn, world.getRandom());
        return Command.SINGLE_SUCCESS;
    }
}
