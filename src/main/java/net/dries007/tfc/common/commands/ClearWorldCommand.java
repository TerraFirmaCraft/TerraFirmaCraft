/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.server.command.EnumArgument;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.world.feature.vein.IVeinConfig;
import net.dries007.tfc.world.feature.vein.VeinFeature;

public final class ClearWorldCommand
{
    private static final String STARTING = "tfc.commands.clear_world.starting";
    private static final String DONE = "tfc.commands.clear_world.done";

    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("clearworld")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("radius", IntegerArgumentType.integer(1, 250))
                .then(Commands.argument("preset", EnumArgument.enumArgument(Preset.class))
                    .executes(cmd -> clearWorld(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "radius"), cmd.getArgument("preset", Preset.class)))
                )
                .executes(cmd -> clearWorld(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "radius"), Preset.ALL))
            );
    }

    private static int clearWorld(CommandSourceStack source, int radius, Preset preset)
    {
        source.sendSuccess(() -> Component.translatable(STARTING), true);

        final Level level = source.getLevel();
        final BlockPos center = BlockPos.containing(source.getPosition());
        final BlockState air = Blocks.AIR.defaultBlockState();

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final Predicate<BlockState> predicate = preset.make(source.getServer());
        final int minY = level.getMinBuildHeight();

        int blocksRemoved = 0;

        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                final int height = level.getHeight(Heightmap.Types.WORLD_SURFACE, center.getX() + x, center.getZ() + z);
                for (int y = minY; y < height; y++)
                {
                    mutablePos.set(center).move(x, 0, z).setY(y);
                    BlockState state = level.getBlockState(mutablePos);
                    if (!state.isAir() && predicate.test(state))
                    {
                        level.setBlock(mutablePos, air, 2 | 16);
                        blocksRemoved++;
                    }
                }
            }
        }
        final int finalBlocksRemoved = blocksRemoved;
        source.sendSuccess(() -> Component.translatable(DONE, finalBlocksRemoved), true);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Determines which blocks to remove
     * The provided predicate should return TRUE to blocks that should be removed
     */
    enum Preset
    {
        ALL(server -> state -> true),
        RAW_ROCK(server -> {
            final Set<Block> blocks = TFCBlocks.ROCK_BLOCKS.values().stream().map(map -> map.get(Rock.BlockType.RAW).get()).collect(Collectors.toSet());
            return state -> blocks.contains(state.getBlock());
        }),
        EARTH(server -> {
            final Set<Block> blocks = Stream.of(
                Stream.of(Rock.BlockType.RAW, Rock.BlockType.HARDENED, Rock.BlockType.GRAVEL)
                    .flatMap(t -> TFCBlocks.ROCK_BLOCKS.values().stream().map(map -> map.get(t).get())),
                TFCBlocks.SOIL.get(SoilBlockType.DIRT).values().stream().map(Supplier::get),
                TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().stream().map(Supplier::get),
                TFCBlocks.SAND.values().stream().map(Supplier::get)
            ).flatMap(t -> t).collect(Collectors.toSet());
            return state -> blocks.contains(state.getBlock());
        }),
        NOT_ORE(server -> {
            final Registry<ConfiguredFeature<?, ?>> registry = server.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
            Set<Block> blocks = registry.stream()
                .filter(feature -> feature.feature() instanceof VeinFeature<?, ?>)
                .flatMap(feature -> ((IVeinConfig) feature.config()).config()
                    .states()
                    .values()
                    .stream()
                    .flatMap(weighted -> weighted.values().stream()))
                .map(BlockBehaviour.BlockStateBase::getBlock)
                .collect(Collectors.toSet());
            return state -> !blocks.contains(state.getBlock());
        });

        private final Function<MinecraftServer, Predicate<BlockState>> factory;

        Preset(Function<MinecraftServer, Predicate<BlockState>> factory)
        {
            this.factory = factory;
        }

        Predicate<BlockState> make(MinecraftServer server)
        {
            return factory.apply(server);
        }
    }
}