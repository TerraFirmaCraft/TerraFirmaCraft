/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

public final class PlayerCommand
{
    private static final String QUERY_HUNGER = "tfc.commands.player.query_hunger";
    private static final String QUERY_SATURATION = "tfc.commands.player.query_saturation";

    public static LiteralArgumentBuilder<CommandSource> create()
    {
        return Commands.literal("player")
            .requires(source -> source.hasPermissionLevel(2))
            .then(Commands.argument("target", EntityArgument.player())
                .then(Commands.literal("query")
                    .then(Commands.literal("hunger")
                        .executes(context -> queryHunger(context, EntityArgument.getPlayer(context, "target")))
                    )
                    .then(Commands.literal("saturation")
                        .executes(context -> querySaturation(context, EntityArgument.getPlayer(context, "target")))
                    )
                    .then(Commands.literal("water")
                        .executes(context -> queryWater(context, EntityArgument.getPlayer(context, "target")))
                    )
                    .then(Commands.literal("nutrition")
                        .executes(context -> queryNutrition(context, EntityArgument.getPlayer(context, "target")))
                    )
                )
                .then(Commands.literal("set")
                    .then(Commands.literal("hunger")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 20))
                            .executes(context -> setHunger(EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), false))
                        )
                    )
                    .then(Commands.literal("saturation")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 20))
                            .executes(context -> setSaturation(EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), false))
                        )
                    )
                )
                .then(Commands.literal("reset")
                    .then(Commands.literal("hunger")
                        .executes(context -> setHunger(EntityArgument.getPlayer(context, "target"), 20, false))
                    )
                    .then(Commands.literal("saturation")
                        .executes(context -> setSaturation(EntityArgument.getPlayer(context, "target"), 5, false))
                    )
                    .then(Commands.literal("water")
                        .executes(context -> setWater(EntityArgument.getPlayer(context, "target"), 100, false))
                    )
                )
                .then(Commands.literal("add")
                    .then(Commands.literal("hunger")
                        .then(Commands.argument("value", IntegerArgumentType.integer(-20, 20))
                            .executes(context -> setHunger(EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), true))
                        )
                    )
                    .then(Commands.literal("saturation")
                        .then(Commands.argument("value", IntegerArgumentType.integer(-20, 20))
                            .executes(context -> setSaturation(EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), true))
                        )
                    )
                    .then(Commands.literal("water")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                            .executes(context -> setWater(EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), true))
                        )
                    )
                )
            );
    }

    private static int queryHunger(CommandContext<CommandSource> context, PlayerEntity player)
    {
        int hunger = player.getFoodStats().getFoodLevel();
        context.getSource().sendFeedback(new TranslationTextComponent(QUERY_HUNGER, hunger), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int querySaturation(CommandContext<CommandSource> context, PlayerEntity player)
    {
        float saturation = player.getFoodStats().getSaturationLevel();
        context.getSource().sendFeedback(new TranslationTextComponent(QUERY_SATURATION, saturation), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int queryWater(CommandContext<CommandSource> context, PlayerEntity player)
    {
        // todo
        throw new UnsupportedOperationException("Not implemented");
    }

    private static int queryNutrition(CommandContext<CommandSource> context, PlayerEntity player)
    {
        // todo
        throw new UnsupportedOperationException("Not implemented");
    }

    private static int setHunger(PlayerEntity player, int hunger, boolean add)
    {
        if (add)
        {
            hunger += player.getFoodStats().getFoodLevel();
        }
        player.getFoodStats().setFoodLevel(hunger);
        return Command.SINGLE_SUCCESS;
    }

    private static int setSaturation(PlayerEntity player, int saturation, boolean add)
    {
        if (add)
        {
            saturation += player.getFoodStats().getSaturationLevel();
        }
        player.getFoodStats().setFoodSaturationLevel(saturation);
        return Command.SINGLE_SUCCESS;
    }

    private static int setWater(PlayerEntity player, int water, boolean add)
    {
        // todo
        throw new UnsupportedOperationException("Not implemented");
    }
}
