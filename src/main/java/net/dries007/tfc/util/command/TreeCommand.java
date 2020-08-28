/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.command;

import net.minecraft.block.LogBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dries007.tfc.api.Wood;
import net.dries007.tfc.objects.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.feature.TFCFeatures;
import net.dries007.tfc.world.feature.trees.NormalTreeConfig;

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
        NormalTreeConfig config = new NormalTreeConfig(Helpers.identifier("ash/base"), Helpers.identifier("ash/overlay"), 3, 5, TFCBlocks.WOODS.get(Wood.Default.ASH).get(Wood.BlockType.LOG).get().getDefaultState().with(LogBlock.AXIS, Direction.Axis.Y));
        TFCFeatures.NORMAL_TREE.get().place(world, world.getChunkProvider().getChunkGenerator(), world.getRandom(), pos, config);
        return Command.SINGLE_SUCCESS;
    }
}
