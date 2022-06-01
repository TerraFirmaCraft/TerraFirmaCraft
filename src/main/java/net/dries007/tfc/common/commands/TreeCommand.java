/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.server.command.EnumArgument;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.world.feature.tree.TFCTreeGrower;

public final class TreeCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("tree")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .then(Commands.argument("wood", EnumArgument.enumArgument(Wood.class))
                    .then(Commands.argument("variant", EnumArgument.enumArgument(Variant.class))
                        .executes(context -> placeTree(context.getSource().getLevel(), BlockPosArgument.getLoadedBlockPos(context, "pos"), context.getArgument("wood", Wood.class), context.getArgument("variant", Variant.class)))
                    )
                    .executes(context -> placeTree(context.getSource().getLevel(), BlockPosArgument.getLoadedBlockPos(context, "pos"), context.getArgument("wood", Wood.class), Variant.NORMAL))
                )
            );
    }

    private static int placeTree(ServerLevel world, BlockPos pos, Wood wood, Variant variant)
    {
        TFCTreeGrower tree = wood.tree();
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