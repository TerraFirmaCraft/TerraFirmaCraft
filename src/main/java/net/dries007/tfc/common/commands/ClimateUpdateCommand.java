/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class ClimateUpdateCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("climateChunkUpdate")
            .requires(source -> source.hasPermission(2))
            .executes(stack -> climateChunkUpdate(stack.getSource()));
    }

    private static int climateChunkUpdate(CommandSourceStack source)
    {
        final ServerLevel level = source.getLevel();
        final BlockPos pos = new BlockPos(source.getPosition());
        final LevelChunk chunk = level.getChunkAt(pos);
        final ChunkData chunkData = ChunkData.get(level, pos);
        Climate.onChunkLoad(level, new ImposterProtoChunk(chunk, true), chunkData);
        return Command.SINGLE_SUCCESS;
    }
}
