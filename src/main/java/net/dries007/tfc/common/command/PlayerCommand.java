/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.food.TFCFoodStats;
import net.dries007.tfc.util.Helpers;

public final class PlayerCommand
{
    private static final String QUERY_HUNGER = "tfc.commands.player.query_hunger";
    private static final String QUERY_SATURATION = "tfc.commands.player.query_saturation";
    private static final String QUERY_WATER = "tfc.commands.player.query_water";
    private static final String QUERY_NUTRITION = "tfc.commands.player.query_nutrition";
    private static final String FAIL_INVALID_FOOD_STATS = "tfc.commands.player.fail_invalid_food_stats";

    public static LiteralArgumentBuilder<CommandSource> create()
    {
        return Commands.literal("player")
            .requires(source -> source.hasPermission(2))
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
                    .then(Commands.literal("water")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                            .executes(context -> setWater(EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), false))
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
        int hunger = player.getFoodData().getFoodLevel();
        context.getSource().sendSuccess(new TranslationTextComponent(QUERY_HUNGER, hunger), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int querySaturation(CommandContext<CommandSource> context, PlayerEntity player)
    {
        float saturation = player.getFoodData().getSaturationLevel();
        context.getSource().sendSuccess(new TranslationTextComponent(QUERY_SATURATION, saturation), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int queryWater(CommandContext<CommandSource> context, PlayerEntity player)
    {
        if (player.getFoodData() instanceof TFCFoodStats)
        {
            float water = ((TFCFoodStats) player.getFoodData()).getThirst();
            context.getSource().sendSuccess(new TranslationTextComponent(QUERY_WATER, water), true);
            return Command.SINGLE_SUCCESS;
        }
        context.getSource().sendFailure(new TranslationTextComponent(FAIL_INVALID_FOOD_STATS));
        return 0;
    }

    private static int queryNutrition(CommandContext<CommandSource> context, PlayerEntity player)
    {
        if (player.getFoodData() instanceof TFCFoodStats)
        {
            float[] nutrition = ((TFCFoodStats) player.getFoodData()).getNutrition().getNutrients();
            context.getSource().sendSuccess(new TranslationTextComponent(QUERY_NUTRITION), true);
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                int percent = (int) (100 * nutrition[nutrient.ordinal()]);
                context.getSource().sendSuccess(
                    new StringTextComponent(" - ")
                        .append(Helpers.translateEnum(nutrient))
                        .append(": " + percent + "%"), true);
            }
            return Command.SINGLE_SUCCESS;
        }
        context.getSource().sendFailure(new TranslationTextComponent(FAIL_INVALID_FOOD_STATS));
        return 0;
    }

    private static int setHunger(PlayerEntity player, int hunger, boolean add)
    {
        if (add)
        {
            hunger += player.getFoodData().getFoodLevel();
        }
        player.getFoodData().setFoodLevel(hunger);
        return Command.SINGLE_SUCCESS;
    }

    private static int setSaturation(PlayerEntity player, int saturation, boolean add)
    {
        if (add)
        {
            saturation += player.getFoodData().getSaturationLevel();
        }
        player.getFoodData().setSaturation(saturation);
        return Command.SINGLE_SUCCESS;
    }

    private static int setWater(PlayerEntity player, int water, boolean add)
    {
        if (player.getFoodData() instanceof TFCFoodStats)
        {
            final TFCFoodStats stats = (TFCFoodStats) player.getFoodData();
            if (add)
            {
                water += stats.getThirst();
            }
            stats.setThirst(water);
        }
        return Command.SINGLE_SUCCESS;
    }
}