/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.common.TFCDamageTypes;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.NutritionData;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.PlayerInfoPacket;
import net.dries007.tfc.util.advancements.TFCAdvancements;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;

/**
 * This is a central spot for all player-specific information that TFC adds or modifies about the vanilla player. It replaces the default
 * {@code Player#foodData} as we want to override most functionality in vanilla there, and also adds various other functionality.
 * <p>
 * Accessing a {@link PlayerInfo} can be done via {@link IPlayerInfo#get(Player)}, which must always be present. Note that this introduces a conflict
 * with any other mods that desire to override {@code Player#foodData}, however we are likely incompatible with them regardless of how much
 * we split out from the player's food data itself.
 * <p>
 * Most non-implementation detail can be accessed through the {@code IPlayerInfo} interface, which provides direct access to player information
 * without including implementation details or other {@code FoodData} related methods.
 */
public final class PlayerInfo extends net.minecraft.world.food.FoodData implements IPlayerInfo
{
    // Vanilla constants
    public static final int MAX_HUNGER = 20;
    public static final float EXHAUSTION_PER_HUNGER = 4;

    public static final float MAX_THIRST = 100f;

    public static final float EXHAUSTION_MULTIPLIER = 0.4f; // Multiplier for all sources of exhaustion. Vanilla sources get reduced, while passive exhaustion factors in this multiplier.

    public static final float PASSIVE_HEALING_PER_TEN_TICKS = 20 * 0.0002f; // On the display: 1 HP / 5 seconds
    public static final float PASSIVE_EXHAUSTION_PER_TICK = MAX_HUNGER * EXHAUSTION_PER_HUNGER / (2.5f * ICalendar.TICKS_IN_DAY * EXHAUSTION_MULTIPLIER); // Passive exhaustion will deplete your food bar once every 2.5 days. Food bar holds ~5 "meals", this requires two per day

    public static final float MAX_TEMPERATURE_THIRST_DECAY = 0.4f;

    public static final long MAX_INTOXICATED_TICKS = 36 * ICalendar.TICKS_IN_HOUR; // A day and a half. Each drink gives you 4 hours of time


    private final Player player; // The player associated with this object
    private final net.minecraft.world.food.FoodData food; // The original player's food data

    private float thirst = MAX_THIRST; // The current thirst of the player
    private long lastDrinkTick = Long.MIN_VALUE;
    private long intoxicationTick = Long.MIN_VALUE; // A future tick that the player is intoxicated until
    private ChiselMode chiselMode = ChiselMode.SMOOTH.value();
    private NutritionData nutrition = new NutritionData(0.5f, 0f); // Nutrition information

    private boolean modified = false;

    public PlayerInfo(Player player)
    {
        this.player = player;
        this.food = player.getFoodData(); // This must be the original food data, we replace it after the player info is created
    }

    // ===== IPlayerInfo ===== //

    @Override
    public void onDrink()
    {
        lastDrinkTick = calendar().getTicks();
        modified = true;
    }

    @Override
    public boolean canDrink()
    {
        return lastDrinkTick + 10 < calendar().getTicks();
    }

    @Override
    public ChiselMode chiselMode()
    {
        return chiselMode;
    }

    @Override
    public void cycleChiselMode()
    {
        chiselMode = chiselMode.next();
        modified = true;
    }

    @Override
    public long getIntoxication()
    {
        return Math.max(0, intoxicationTick - calendar().getTicks());
    }

    @Override
    public void addIntoxication(long ticks)
    {
        long currentTick = Calendars.SERVER.getTicks();
        if (intoxicationTick < currentTick)
        {
            intoxicationTick = currentTick;
        }
        intoxicationTick += ticks;
        if (intoxicationTick > currentTick + MAX_INTOXICATED_TICKS)
        {
            intoxicationTick = currentTick + MAX_INTOXICATED_TICKS;
        }
        modified = true;
    }

    @Override
    public float getThirst()
    {
        return thirst;
    }

    @Override
    public void setThirst(float value)
    {
        thirst = Mth.clamp(value, 0, MAX_THIRST);
        modified = true;
    }

    @Override
    public float getThirstContributionFromTemperature()
    {
        if (TFCConfig.SERVER.enableThirstOverheating.get())
        {
            final float temp = Climate.getTemperature(player.level(), player.blockPosition());
            return Mth.clampedMap(temp, 22f, 34f, 0f, MAX_TEMPERATURE_THIRST_DECAY);
        }
        return 0;
    }

    @Override
    public NutritionData nutrition()
    {
        return nutrition;
    }

    @Override
    public void eat(IFood food)
    {
        final FoodData data = food.getData();
        if (!food.isRotten())
        {
            eat(data);
        }
        else if (player instanceof ServerPlayer serverPlayer) // Check for server side first
        {
            // Minor effects from eating rotten food
            final RandomSource random = serverPlayer.getRandom();
            if (random.nextFloat() < 0.6)
            {
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.HUNGER, 1800, 1));
                if (random.nextFloat() < 0.15)
                {
                    serverPlayer.addEffect(new MobEffectInstance(MobEffects.POISON, 1800, 0));
                }
            }
            TFCAdvancements.EAT_ROTTEN_FOOD.trigger(serverPlayer);
        }
    }

    @Override
    public void eat(FoodData food)
    {
        addThirst(food.water());
        addIntoxication(food.intoxication());

        nutrition.addNutrients(food);

        if (player instanceof ServerPlayer serverPlayer && nutrition.getAverageNutrition() >= 0.999)
        {
            TFCAdvancements.FULL_NUTRITION.trigger(serverPlayer);
        }

        if (food.hunger() > 0)
        {
            // In order to get the exact saturation we want, apply this scaling factor here
            this.food.eat(food.hunger(), food.saturation() / (2f * food.hunger()));
        }

        modified = true;
    }

    /**
     * Sets data from a packet, received on client side. Does not contain the full data only the important information
     */
    @Override
    public void onClientUpdate(PlayerInfoPacket packet)
    {
        this.lastDrinkTick = packet.lastDrinkTick();
        this.chiselMode = packet.chiselMode();
        this.thirst = packet.thirst();
        this.intoxicationTick = packet.intoxication();
        this.nutrition.onClientUpdate(packet.nutrients());
    }

    @Override
    public void copyOnDeath(IPlayerInfo info)
    {
        this.nutrition = info.nutrition();
    }

    /**
     * Called from player tick, on server side only. This is responsible for ticking both the vanilla food and our effects.
     * @param player The server player
     */
    @Override
    public void tick(Player player)
    {
        final Difficulty difficulty = player.level().getDifficulty();
        if (difficulty == Difficulty.PEACEFUL && TFCConfig.SERVER.enablePeacefulDifficultyPassiveRegeneration.get())
        {
            // Extra-Peaceful Difficulty
            // Health regeneration modified from PlayerEntity#aiStep()
            if (player.getHealth() < player.getMaxHealth() && player.tickCount % 20 == 0)
            {
                player.heal(1.0F);
            }
            if (player.tickCount % 10 == 0)
            {
                if (needsFood())
                {
                    setFoodLevel(getFoodLevel() + 1);
                }
                if (thirst < MAX_THIRST)
                {
                    addThirst(5f);
                }
            }
        }
        else
        {
            // Passive exhaustion - call the source player instead of the local method
            player.causeFoodExhaustion(PASSIVE_EXHAUSTION_PER_TICK * TFCConfig.SERVER.passiveExhaustionModifier.get().floatValue());

            // Same check as the original food stats, so hunger and thirst loss are synced
            if (food.getExhaustionLevel() >= 4.0F)
            {
                addThirst(-(TFCConfig.SERVER.thirstModifier.get().floatValue() * (1 + getThirstContributionFromTemperature())));

                // Vanilla will consume exhaustion and saturation in peaceful, but won't modify hunger.
                // We mimic the same checks that are about to happen in tick(), and if needed, consume hunger in advance
                if (difficulty == Difficulty.PEACEFUL && getSaturationLevel() <= 0)
                {
                    setFoodLevel(Math.max(getFoodLevel() - 1, 0));
                }
            }

            if (difficulty == Difficulty.PEACEFUL)
            {
                // Copied from vanilla's food stats, so we consume food in peaceful mode (would normally be part of the super.tick call)
                if (food.getExhaustionLevel() > 4.0F)
                {
                    setFoodLevel(Math.max(getFoodLevel() - 1, 0));
                }
            }
        }

        // Next, tick the original food stats
        food.tick(player);

        // Apply custom TFC regeneration
        if (player.tickCount % 10 == 0)
        {
            if (player.isHurt() && getFoodLevel() >= 4.0f && getThirst() > 20f)
            {
                final float foodBonus = Mth.inverseLerp(getFoodLevel(), 4, MAX_HUNGER);
                final float thirstBonus = Mth.inverseLerp(getThirst(), 20, MAX_THIRST);
                final float multiplier = 1 + foodBonus + thirstBonus; // Range: [1, 3] depending on total thirst and hunger

                player.heal(multiplier * PASSIVE_HEALING_PER_TEN_TICKS * TFCConfig.SERVER.naturalRegenerationModifier.get().floatValue());
            }
        }

        // Last, apply negative effects due to lack of thirst
        if (player.tickCount % 100 == 0 && difficulty != Difficulty.PEACEFUL && !player.getAbilities().invulnerable)
        {
            if (thirst < 10f)
            {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 160, 1, false, false));
                if (thirst <= 0f)
                {
                    // Hurt the player, same as starvation
                    TFCDamageTypes.dehydration(player, 1f);
                }
            }
            else if (thirst < 20f)
            {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 160, 0, false, false));
            }
        }

        // Since this is only called server side, and vanilla has a custom packet for this stuff, we need our own
        if (modified && player instanceof ServerPlayer serverPlayer)
        {
            PacketDistributor.sendToPlayer(serverPlayer, new PlayerInfoPacket(
                lastDrinkTick,
                thirst,
                chiselMode,
                intoxicationTick,
                nutrition.getNutrients()
            ));
            modified = false;
        }
    }

    // ===== Serialization via FoodData ===== //

    @Override
    public void readAdditionalSaveData(CompoundTag root)
    {
        final CompoundTag tag = root.getCompound("tfc:food");

        food.readAdditionalSaveData(root);
        lastDrinkTick = tag.getLong("lastDrinkTick");
        thirst = tag.getFloat("thirst");
        chiselMode = ChiselMode.REGISTRY.get(ResourceLocation.tryParse(tag.getString("chiselMode")));
        nutrition.readFromNbt(tag.get("nutrition"));
        nutrition.setHunger(getFoodLevel());
        intoxicationTick = tag.getLong("intoxication");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag root)
    {
        final CompoundTag tag = new CompoundTag();

        root.put("tfc:food", tag);
        food.addAdditionalSaveData(root);
        tag.putLong("lastDrinkTick", lastDrinkTick);
        tag.putFloat("thirst", thirst);
        tag.putString("chiselMode", ChiselMode.REGISTRY.getKey(chiselMode).toString());
        tag.put("nutrition", nutrition.writeToNbt());
        tag.putLong("intoxication", intoxicationTick);
    }

    // ===== Forward FoodData to original ===== //


    @Override
    public void eat(FoodProperties foodProperties) {} // No-op

    @Override
    public void eat(int foodLevelModifier, float saturationLevelModifier) {} // No-op

    @Override
    public int getFoodLevel()
    {
        return food.getFoodLevel();
    }

    @Override
    public int getLastFoodLevel()
    {
        return food.getLastFoodLevel();
    }

    @Override
    public boolean needsFood()
    {
        return food.needsFood();
    }

    @Override
    public void addExhaustion(float exhaustion)
    {
        food.addExhaustion(exhaustion);
    }

    @Override
    public float getExhaustionLevel()
    {
        return food.getExhaustionLevel();
    }

    @Override
    public float getSaturationLevel()
    {
        return food.getSaturationLevel();
    }

    @Override
    public void setFoodLevel(int foodLevel)
    {
        modified = true;
        nutrition.setHunger(foodLevel);
        food.setFoodLevel(foodLevel);
    }

    @Override
    public void setSaturation(float saturationLevel)
    {
        food.setSaturation(saturationLevel);
    }

    @Override
    public void setExhaustion(float exhaustionLevel)
    {
        food.setExhaustion(exhaustionLevel);
    }

    // ===== Private Implementation Details ===== //

    private ICalendar calendar()
    {
        return Calendars.get(player.level());
    }
}
