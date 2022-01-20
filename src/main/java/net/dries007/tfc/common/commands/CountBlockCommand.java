package net.dries007.tfc.common.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.Heightmap;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public final class CountBlockCommand
{
    private static final String DONE = "tfc.commands.countblock.done";

    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("countblock")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("radius", IntegerArgumentType.integer(1, 250))
                .then(Commands.argument("state", BlockStateArgument.block())
                    .executes(cmd -> countBlock(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "radius"), BlockStateArgument.getBlock(cmd, "state"))
                    )
                )
            );
    }

    private static int countBlock(CommandSourceStack source, int radius, BlockInput block)
    {
        final Level level = source.getLevel();
        final BlockPos center = new BlockPos(source.getPosition());

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        int found = 0;
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++)
                {
                    mutablePos.set(center).move(x, 0, z).setY(y);
                    if (block.test(new BlockInWorld(level, mutablePos, false)))
                    {
                        found++;
                    }
                }
            }
        }
        source.sendSuccess(new TranslatableComponent(DONE, found, block.getState().getBlock().getRegistryName()), true);
        return Command.SINGLE_SUCCESS;
    }
}
