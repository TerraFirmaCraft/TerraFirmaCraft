package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.collections.IndirectHashCollection;

public class Drinkable extends FluidDefinition
{
    public static final DataManager<Drinkable> MANAGER = new DataManager.Instance<>(Drinkable::new, "drinkables", "drinkable");
    public static final IndirectHashCollection<Fluid, Drinkable> CACHE = new IndirectHashCollection<>(Drinkable::getFluids);

    @Nullable
    public static Drinkable get(FluidStack fluid)
    {
        for (Drinkable drinkable : CACHE.getAll(fluid.getFluid()))
        {
            if (drinkable.matches(fluid))
            {
                return drinkable;
            }
        }
        return null;
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
        this.effects = new ArrayList<>();

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
                this.effects.add(new Effect(type, duration, amplifier, chance));
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

    public boolean hasEffects()
    {
        return !effects.isEmpty();
    }

    public record Effect(MobEffect type, int duration, int amplifier, float chance) {}
}
