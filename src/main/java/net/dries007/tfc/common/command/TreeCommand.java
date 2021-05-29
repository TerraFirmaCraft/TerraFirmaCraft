/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.command.EnumArgument;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.world.feature.tree.TFCTree;

public final class TreeCommand
{
    public static LiteralArgumentBuilder<CommandSource> create()
    {
        return Commands.literal("tree")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .then(Commands.argument("wood", EnumArgument.enumArgument(Wood.class))
                    .then(Commands.argument("variant", EnumArgument.enumArgument(Variant.class))
                        .executes(context -> placeTree(context.getSource().getLevel(), BlockPosArgument.getOrLoadBlockPos(context, "pos"), context.getArgument("wood", Wood.class), context.getArgument("variant", Variant.class)))
                    )
                    .executes(context -> placeTree(context.getSource().getLevel(), BlockPosArgument.getOrLoadBlockPos(context, "pos"), context.getArgument("wood", Wood.class), Variant.NORMAL))
                )
            );
    }

    private static int placeTree(ServerWorld world, BlockPos pos, Wood wood, Variant variant)
    {
        TFCTree tree = wood.getTree();
        Registry<ConfiguredFeature<?, ?>> registry = world.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
        ConfiguredFeature<?, ?> feature = variant == Variant.NORMAL ? tree.getNormalFeature(registry) : tree.getOldGrowthFeature(registry);
        feature.place(world, world.getChunkSource().getGenerator(), world.getRandom(), pos);
        return Command.SINGLE_SUCCESS;
    }

    private enum Variant
    {
        NORMAL, LARGE
    }
}