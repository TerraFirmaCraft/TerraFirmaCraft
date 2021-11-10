/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.common.capabilities.player.PlayerDataCapability;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class Drinkable extends FluidDefinition
{
    public static final DataManager<Drinkable> MANAGER = new DataManager<>("drinkables", "drinkable", Drinkable::new, Drinkable::reload);
    public static final IndirectHashCollection<Fluid, Drinkable> CACHE = new IndirectHashCollection<>(Drinkable::getFluids);

    @Nullable
    public static Drinkable get(Fluid fluid)
    {
        for (Drinkable drinkable : CACHE.getAll(fluid))
        {
            if (drinkable.matches(fluid))
            {
                return drinkable;
            }
        }
        return null;
    }

    private static void reload()
    {
        CACHE.reload(MANAGER.getValues());
    }

    private final float consumeChance;
    private final int thirst;
    private final int intoxication;
    private final List<Effect> effects;

    protected Drinkable(ResourceLocation id, JsonObject json)
    {
        super(id, json);

        this.consumeChance = JsonHelpers.getAsFloat(json, "consume_chance", 0);
        this.thirst = JsonHelpers.getAsInt(json, "thirst", 0);
        this.intoxication = JsonHelpers.getAsInt(json, "intoxication", 0);

        final ImmutableList.Builder<Effect> builder = new ImmutableList.Builder<>();
        if (json.has("effects"))
        {
            JsonArray array = JsonHelpers.getAsJsonArray(json, "effects");
            for (JsonElement e : array)
            {
                final JsonObject effectJson = JsonHelpers.convertToJsonObject(e, "effect");
                final MobEffect type = JsonHelpers.getRegistryEntry(effectJson, "type", ForgeRegistries.MOB_EFFECTS);
                final int duration = JsonHelpers.getAsInt(effectJson, "duration", 20);
                final int amplifier = JsonHelpers.getAsInt(effectJson, "amplifier", 1);
                final float chance = (float) JsonHelpers.getAsDouble(effectJson, "chance", 1);

                builder.add(new Effect(type, duration, amplifier, chance));
            }
        }
        this.effects = builder.build();
    }

    public void onDrink(Player player)
    {
        final Random random = player.getRandom();

        if (thirst > 0 && player.getFoodData() instanceof TFCFoodData foodData)
        {
            foodData.addThirst(thirst);
        }

        if (intoxication > 0)
        {
            player.getCapability(PlayerDataCapability.CAPABILITY).ifPresent(p -> p.addIntoxicatedTicks(intoxication));
        }

        for (Drinkable.Effect effect : effects)
        {
            if (effect.chance() > random.nextFloat())
            {
                player.addEffect(new MobEffectInstance(effect.type(), effect.duration(), effect.amplifier(), false, false, true));
            }
        }
    }

    public float getConsumeChance()
    {
        return consumeChance;
    }

    public int getThirst()
    {
        return thirst;
    }

    public int getIntoxication()
    {
        return intoxication;
    }

    public Collection<Effect> getEffects()
    {
        return effects;
    }

    public record Effect(MobEffect type, int duration, int amplifier, float chance) {}
}
