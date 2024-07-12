/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.NutritionData;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.accessor.PlayerAccessor;
import net.dries007.tfc.network.FoodDataReplacePacket;
import net.dries007.tfc.network.PlayerInfoPacket;

public interface IPlayerInfo
{
    float DEFAULT_AVERAGE_NUTRITION = 0.4f; // 1/2 of 4 bars = 0.5 x 4 / 5

    /**
     * @return The player info for a given player.
     * @see PlayerInfo
     */
    static IPlayerInfo get(Player player)
    {
        return (IPlayerInfo) player.getFoodData();
    }

    static void setupForPlayer(Player player)
    {
        if (!(player.getFoodData() instanceof IPlayerInfo))
        {
            ((PlayerAccessor) player).accessor$setFoodData(new PlayerInfo(player));
        }
        if (player instanceof ServerPlayer serverPlayer)
        {
            PacketDistributor.sendToPlayer(serverPlayer, FoodDataReplacePacket.PACKET);
        }
    }

    static void copyOnDeath(Player oldPlayer, Player newPlayer)
    {
        final IPlayerInfo oldInfo = get(oldPlayer);
        final IPlayerInfo newInfo = get(newPlayer);

        newInfo.copyOnDeath(oldInfo);
    }

    /**
     * Resets the player's internal cooldown for how quickly they can drink from an in-world source.
     */
    void onDrink();

    /**
     * @return {@code true} if the player can drink, based on the cooldown since they last drank.
     */
    boolean canDrink();

    /**
     * @return The player's currently selected chisel mode
     */
    ChiselRecipe.Mode chiselMode();

    /**
     * Cycle the currently selected chisel mode between the available values
     */
    void cycleChiselMode();

    /**
     * @return The number of ticks that the player is intoxicated for.
     */
    long getIntoxication();

    /**
     * Adds an amount of ticks for the player to continue to be intoxicated for
     * @param ticks An amount of ticks to add
     */
    void addIntoxication(long ticks);

    /**
     * @return The current thirst value of the player
     */
    float getThirst();

    default void addThirst(float value)
    {
        setThirst(getThirst() + value);
    }

    void setThirst(float value);

    /**
     * @return The players nutrition information
     */
    NutritionData nutrition();

    /**
     * @return The modifier for the player's health, based on their current total nutrition
     */
    default float getHealthModifier()
    {
        final float averageNutrition = nutrition().getAverageNutrition(); // In [0, 1]
        return averageNutrition < DEFAULT_AVERAGE_NUTRITION ?
            // Lerp [0, default] -> [min, default] modifier
            Mth.map(averageNutrition, 0.0f, DEFAULT_AVERAGE_NUTRITION, TFCConfig.SERVER.nutritionMinimumHealthModifier.get().floatValue(), TFCConfig.SERVER.nutritionDefaultHealthModifier.get().floatValue()) :
            // Lerp [default, 1] -> [default, max] modifier
            Mth.map(averageNutrition, DEFAULT_AVERAGE_NUTRITION, 1.0f, TFCConfig.SERVER.nutritionDefaultHealthModifier.get().floatValue(), TFCConfig.SERVER.nutritionMaximumHealthModifier.get().floatValue());
    }

    /**
     * Eats the given food, if it is not rotten. May apply effects from eating rotten food
     * @param food The food to eat
     */
    void eat(IFood food);

    /**
     * Eats the given food data directly, when there is no concern about expiry. This applies the following effects:
     * <ul>
     *     <li>Thirst and intoxication are always added</li>
     *     <li>If hunger is {@code > 0}, then both hunger and additional saturation is applied</li>
     *     <li>Nutrition is added if either {@code hunger > 0}, or the last food eaten was non-zero hunger</li>
     * </ul>
     * @param food The food to eat
     */
    void eat(FoodData food);

    /**
     * Updates the client-side food data when received on client
     * @param packet The packed with updated data
     */
    void onClientUpdate(PlayerInfoPacket packet);

    /**
     * When a player dies, copy some (nutrition) information from the old info to the new info
     * @param info The old info for the dead player
     */
    void copyOnDeath(IPlayerInfo info);
}
