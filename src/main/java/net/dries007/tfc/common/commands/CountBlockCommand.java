/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import java.util.function.Predicate;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.dries007.tfc.util.Helpers;

public final class CountBlockCommand
{
    private static final String DONE = "tfc.commands.countblock.done";
    private static final DynamicCommandExceptionType ERROR_INVALID_BLOCK = new DynamicCommandExceptionType((block) -> Helpers.translatable("tfc.commands.countblock.invalid_block", block));

    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("count")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("radius", IntegerArgumentType.integer(1, 250))
                .then(Commands.argument("block", ResourceOrTagLocationArgument.resourceOrTag(Registry.BLOCK_REGISTRY))
                    .executes(context -> countBlock(context.getSource(), IntegerArgumentType.getInteger(context, "radius"), getRegistryType(context, "block", Registry.BLOCK_REGISTRY, ERROR_INVALID_BLOCK))
                    )
                )
            );
    }

    /**
     * @see ResourceOrTagLocationArgument#getRegistryType(CommandContext, String, ResourceKey, DynamicCommandExceptionType)
     */
    public static <T> ResourceOrTagLocationArgument.Result<T> getRegistryType(CommandContext<CommandSourceStack> context, String name, ResourceKey<Registry<T>> key, DynamicCommandExceptionType error) throws CommandSyntaxException
    {
        final ResourceOrTagLocationArgument.Result<?> result = context.getArgument(name, ResourceOrTagLocationArgument.Result.class);
        return result.cast(key).orElseThrow(() -> error.create(result));
    }

    private static int countBlock(CommandSourceStack source, int radius, ResourceOrTagLocationArgument.Result<Block> block)
    {
        final Level level = source.getLevel();
        final BlockPos center = new BlockPos(source.getPosition());

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final Predicate<Block> predicate = block.unwrap()
            .mapLeft(lr -> ForgeRegistries.BLOCKS.getValue(lr.location()))
            .map(
                lr -> b -> b == lr,
                rr -> b -> Helpers.isBlock(b, rr)
            );

        int found = 0;
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++)
                {
                    mutablePos.set(center).move(x, 0, z).setY(y);
                    final BlockState state = level.getBlockState(mutablePos);
                    if (predicate.test(state.getBlock()))
                    {
                        found++;
                    }
                }
            }
        }
        source.sendSuccess(Helpers.translatable(DONE, found, block.asPrintable()), true);
        return Command.SINGLE_SUCCESS;
    }
}
