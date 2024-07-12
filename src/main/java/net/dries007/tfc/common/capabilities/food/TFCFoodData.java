/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCDamageSources;
import net.dries007.tfc.common.capabilities.player.PlayerData;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.accessor.PlayerAccessor;
import net.dries007.tfc.network.FoodDataReplacePacket;
import net.dries007.tfc.network.FoodDataUpdatePacket;
import net.dries007.tfc.util.advancements.TFCAdvancements;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;

/**
 * A note on the reason why {@link TFCFoodData} serializes to an external capability (player data):
 *
 * We don't use the vanilla read/add save data methods.
 * Why? Because we replace the {@link net.minecraft.world.food.FoodData} instance way after it has been already deserialized in vanilla.
 * - {@link net.minecraftforge.event.entity.EntityEvent.EntityConstructing} is too early as {@link Player} constructor would overwrite our food data
 * - {@link net.minecraftforge.event.AttachCapabilitiesEvent} fires just after, and is where we attach the player data capability
 * - {@link net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile} fires later, and has access to the save data, but is too early as the player's connection is not yet set, so we can't properly sync that change to client.
 * - {@link net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent} is where we can reliably update the {@link net.minecraft.world.food.FoodData} instance on server, and then sync that change to client.
 *
 * Saving is then just written to the capability NBT at point of writing
 * The capability is deserialized in  {@link net.minecraft.world.entity.Entity#load(CompoundTag)}, but the food stats doesn't exist yet, so the saved data is cached on the capability and copied over in the food data constructor
 * Now, at this point, we can actually read directly from our capability, as it has been deserialized much earlier in Entity#load.
 * Reading is a bit different: we read from the capability data early, store the NBT, and then copy it to the food stats on the instantiation of the custom food stats.
 */
public class TFCFoodData extends net.minecraft.world.food.FoodData
{
    // Vanilla constants
    public static final int MAX_HUNGER = 20;
    public static final float MAX_SATURATION = 20;
    public static final float MAX_EXHAUSTION = 40;
    public static final float EXHAUSTION_PER_HUNGER = 4;

    public static final float MAX_THIRST = 100f;

    public static final float PASSIVE_HEALING_PER_TEN_TICKS = 20 * 0.0002f; // On the display: 1 HP / 5 seconds
    public static final float EXHAUSTION_MULTIPLIER = 0.4f; // Multiplier for all sources of exhaustion. Vanilla sources get reduced, while passive exhaustion factors in this multiplier.
    public static final float PASSIVE_EXHAUSTION_PER_TICK = MAX_HUNGER * EXHAUSTION_PER_HUNGER / (2.5f * ICalendar.TICKS_IN_DAY * EXHAUSTION_MULTIPLIER); // Passive exhaustion will deplete your food bar once every 2.5 days. Food bar holds ~5 "meals", this requires two per day
    public static final float PASSIVE_EXHAUSTION_PER_SECOND = 20 * PASSIVE_EXHAUSTION_PER_TICK;

    public static final float MAX_TEMPERATURE_THIRST_DECAY = 0.4f;

    public static final float DEFAULT_AVERAGE_NUTRITION = 0.4f; // 1/2 of 4 bars = 0.5 x 4 / 5

    public static void replaceFoodStats(Player player)
    {
        // Only replace the server player's stats if they aren't already
        final net.minecraft.world.food.FoodData foodStats = player.getFoodData();
        if (!(foodStats instanceof TFCFoodData))
        {
            // Replace, and then read from the cached data on the player capability (will be present if this is initial log-in / read from disk)
            final TFCFoodData newStats = new TFCFoodData(player, foodStats);
            ((PlayerAccessor) player).accessor$setFoodData(newStats);
            PlayerData.get(player).writeTo(newStats);
        }
        // Send the update regardless so the client can perform the same logic
        if (player instanceof ServerPlayer serverPlayer)
        {
            PacketDistributor.sendToPlayer(serverPlayer, FoodDataReplacePacket.PACKET);
        }
    }

    public static void restoreFoodStatsAfterDeath(Player oldPlayer, Player newPlayer)
    {
        if (oldPlayer.getFoodData() instanceof TFCFoodData oldStats)
        {
            final TFCFoodData newStats = new TFCFoodData(newPlayer, newPlayer.getFoodData(), oldStats.getNutrition());
            ((PlayerAccessor) newPlayer).accessor$setFoodData(newStats);
        }
    }

    private final Player sourcePlayer;
    private final net.minecraft.world.food.FoodData delegate; // We keep this here to do normal vanilla tracking (rather than using super). This is also friendlier to other mods if they replace this
    private final NutritionData nutritionData; // Separate handler for nutrition, because it's a bit complex
    private long lastDrinkTick;
    private float thirst;

    public TFCFoodData(Player sourcePlayer, net.minecraft.world.food.FoodData delegate)
    {
        this(sourcePlayer, delegate, new NutritionData(0.5f, 0.0f));
    }

    public TFCFoodData(Player sourcePlayer, net.minecraft.world.food.FoodData delegate, NutritionData oldNutritionData)
    {
        this.sourcePlayer = sourcePlayer;
        this.delegate = delegate;
        this.nutritionData = oldNutritionData;
        this.thirst = MAX_THIRST;
    }

    @Override
    public void eat(int foodLevelIn, float foodSaturationModifier)
    {
        // This should never be called directly - when it is we assume it's direct stat modifications (saturation potion, eating cake)
        // We make modifications to vanilla logic, as saturation needs to be unaffected by hunger
    }

    @Override
    public void eat(Item maybeFood, ItemStack stack, @Nullable LivingEntity entity)
    {
        final @Nullable IFood food = FoodCapability.get(stack);
        if (food != null)
        {
            eat(food);
        }
    }

    /**
     * Called from {@link Player#tick()} on server side only
     *
     * @param player the player whose food stats this is
     */
    @Override
    public void tick(Player player)
    {
        final Difficulty difficulty = player.level().getDifficulty();
        if (difficulty == Difficulty.PEACEFUL && TFCConfig.SERVER.enablePeacefulDifficultyPassiveRegeneration.get())
        {
            // Extra-Peaceful Difficulty
            // Health regeneration modified from PlayerEntity#aiStep()
            if (sourcePlayer.getHealth() < sourcePlayer.getMaxHealth() && sourcePlayer.tickCount % 20 == 0)
            {
                sourcePlayer.heal(1.0F);
            }
            if (sourcePlayer.tickCount % 10 == 0)
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
            if (delegate.getExhaustionLevel() >= 4.0F)
            {
                addThirst(-getThirstModifier(player));

                // Vanilla will consume exhaustion and saturation in peaceful, but won't modify hunger.
                // We mimic the same checks that are about to happen in tick(), and if needed, consume hunger in advance
                if (difficulty == Difficulty.PEACEFUL && getSaturationLevel() <= 0)
                {
                    setFoodLevel(Math.max(getFoodLevel() - 1, 0));
                }
            }

            if (difficulty == Difficulty.PEACEFUL)
            {
                // Copied from vanilla's food stats, so we consume food in peaceful mode (would normally be part of the super.onUpdate call
                if (delegate.getExhaustionLevel() > 4.0F)
                {
                    setFoodLevel(Math.max(getFoodLevel() - 1, 0));
                }
            }
        }

        // Next, tick the original food stats
        delegate.tick(player);

        // Apply custom TFC regeneration
        if (player.tickCount % 10 == 0)
        {
            if (player.isHurt() && getFoodLevel() >= 4.0f && getThirst() > 20f)
            {
                final float foodBonus = Mth.inverseLerp(getFoodLevel(), 4, MAX_HUNGER);
                final float thirstBonus = Mth.inverseLerp(getThirst(), 20, MAX_THIRST);
                final float multiplier = 1 + foodBonus + thirstBonus; // Range: [1, 4] depending on total thirst and hunger

                player.heal(multiplier * PASSIVE_HEALING_PER_TEN_TICKS * TFCConfig.SERVER.naturalRegenerationModifier.get().floatValue());
            }
        }

        // Last, apply negative effects due to thirst
        if (player.tickCount % 100 == 0 && difficulty != Difficulty.PEACEFUL && !player.getAbilities().invulnerable)
        {
            if (thirst < 10f)
            {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 160, 1, false, false));
                if (thirst <= 0f)
                {
                    // Hurt the player, same as starvation
                    TFCDamageSources.dehydration(player, 1f);
                }
            }
            else if (thirst < 20f)
            {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 160, 0, false, false));
            }
        }

        // Since this is only called server side, and vanilla has a custom packet for this stuff, we need our own
        if (player instanceof ServerPlayer serverPlayer)
        {
            PacketDistributor.sendToPlayer(serverPlayer, new FoodDataUpdatePacket(nutritionData.getNutrients(), thirst));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag vanillaNbt)
    {
        delegate.readAdditionalSaveData(vanillaNbt);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag vanillaNbt)
    {
        delegate.addAdditionalSaveData(vanillaNbt);
    }

    @Override
    public int getFoodLevel()
    {
        return delegate.getFoodLevel();
    }

    @Override
    public boolean needsFood()
    {
        return delegate.needsFood();
    }

    @Override
    public void addExhaustion(float exhaustion)
    {
        // Exhaustion from all vanilla sources is reduced
        delegate.addExhaustion(EXHAUSTION_MULTIPLIER * exhaustion);
    }

    @Override
    public float getSaturationLevel()
    {
        return delegate.getSaturationLevel();
    }

    @Override
    public void setFoodLevel(int food)
    {
        delegate.setFoodLevel(food);
    }

    @Override
    public void setSaturation(float saturation)
    {
        delegate.setSaturation(saturation);
    }

    public void eat(IFood food)
    {
        // Eating items has nutritional benefits
        final FoodData data = food.getData();
        if (!food.isRotten())
        {
            eat(data);
        }
        else if (this.sourcePlayer instanceof ServerPlayer player) // Check for server side first
        {
            // Minor effects from eating rotten food
            final RandomSource random = sourcePlayer.getRandom();
            if (random.nextFloat() < 0.6)
            {
                sourcePlayer.addEffect(new MobEffectInstance(MobEffects.HUNGER, 1800, 1));
                if (random.nextFloat() < 0.15)
                {
                    sourcePlayer.addEffect(new MobEffectInstance(MobEffects.POISON, 1800, 0));
                }
            }
            TFCAdvancements.EAT_ROTTEN_FOOD.trigger(player);
        }
    }

    /**
     * Eat a piece of food with the data given by {@code data}. This applies the following effects:
     * <ul>
     *     <li>Thirst and intoxication are always used</li>
     *     <li>If hunger is {@code > 0}, then both hunger and additional saturation is applied</li>
     *     <li>Nutrition is added if either {@code hunger > 0}, or the last food eaten was non-zero hunger</li>
     * </ul>
     * @param data The food data to eat
     */
    public void eat(FoodData data)
    {
        addThirst(data.water());
        nutritionData.addNutrients(data);
        PlayerData.get(sourcePlayer).addIntoxicatedTicks(data.intoxication());

        if (this.sourcePlayer instanceof ServerPlayer serverPlayer && nutritionData.getAverageNutrition() >= 0.999)
        {
            TFCAdvancements.FULL_NUTRITION.trigger(serverPlayer);
        }

        if (data.hunger() > 0)
        {
            // In order to get the exact saturation we want, apply this scaling factor here
            delegate.eat(data.hunger(), data.saturation() / (2f * data.hunger()));
        }
    }

    public CompoundTag serializeToPlayerData()
    {
        CompoundTag nbt = new CompoundTag();

        nbt.putFloat("thirst", thirst);
        nbt.putFloat("lastDrinkTick", lastDrinkTick);
        nbt.put("nutrients", nutritionData.writeToNbt());

        return nbt;
    }

    public void deserializeFromPlayerData(CompoundTag nbt)
    {
        thirst = nbt.getFloat("thirst");
        lastDrinkTick = nbt.getLong("lastDrinkTick");
        nutritionData.readFromNbt(nbt.getCompound("nutrients"));
    }

    /**
     * Sets data from a packet, received on client side. Does not contain the full data only the important information
     */
    public void onClientUpdate(float[] nutrients, float thirst)
    {
        this.nutritionData.onClientUpdate(nutrients);
        this.thirst = thirst;
    }

    public float getHealthModifier()
    {
        final float averageNutrition = nutritionData.getAverageNutrition(); // In [0, 1]
        return averageNutrition < DEFAULT_AVERAGE_NUTRITION ?
            // Lerp [0, default] -> [min, default] modifier
            Mth.map(averageNutrition, 0.0f, DEFAULT_AVERAGE_NUTRITION, TFCConfig.SERVER.nutritionMinimumHealthModifier.get().floatValue(), TFCConfig.SERVER.nutritionDefaultHealthModifier.get().floatValue()) :
            // Lerp [default, 1] -> [default, max] modifier
            Mth.map(averageNutrition, DEFAULT_AVERAGE_NUTRITION, 1.0f, TFCConfig.SERVER.nutritionDefaultHealthModifier.get().floatValue(), TFCConfig.SERVER.nutritionMaximumHealthModifier.get().floatValue());
    }

    /**
     * @return The total thirst loss per tick, on a scale of [0, 100], 100 being the entire thirst bar
     */
    public float getThirstModifier(Player player)
    {
        return TFCConfig.SERVER.thirstModifier.get().floatValue() * (1 + getThirstContributionFromTemperature(player));
    }

    /**
     * @return The thirst loss from the ambient temperature on top of regular loss
     */
    public float getThirstContributionFromTemperature(Player player)
    {
        if (TFCConfig.SERVER.enableThirstOverheating.get())
        {
            final float temp = Climate.getTemperature(player.level(), player.blockPosition());
            return Mth.clampedMap(temp, 22f, 34f, 0f, MAX_TEMPERATURE_THIRST_DECAY);
        }
        return 0;
    }


    public float getThirst()
    {
        return thirst;
    }

    public void setThirst(float thirst)
    {
        this.thirst = Mth.clamp(thirst, 0, MAX_THIRST);
    }

    public void addThirst(float toAdd)
    {
        setThirst(thirst + toAdd);
    }

    public NutritionData getNutrition()
    {
        return nutritionData;
    }
}
