/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.network.StreamCodecs;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;


/**
 * @param fluid The fluid corresponding to this fluid heat
 * @param tier The tier of this fluid
 * @param meltTemperature The temperature at which this fluid melts
 * @param specificHeatCapacity The Specific Heat Capacity of the metal. Units of Energy / (°C * mB)
 */
public record FluidHeat(
    Fluid fluid,

    int tier,
    float meltTemperature,
    float specificHeatCapacity,

    ResourceLocation textureId,
    ResourceLocation softTextureId,
    String translationKey,

    Optional<Ingredient> ingots,
    Optional<Ingredient> doubleIngots,
    Optional<Ingredient> sheets
) {
    public static final Codec<FluidHeat> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codecs.FLUID.fieldOf("fluid").forGetter(c -> c.fluid),
        Codec.INT.fieldOf("tier").forGetter(c -> c.tier),
        Codec.FLOAT.fieldOf("melt_temperature").forGetter(c -> c.meltTemperature),
        Codec.FLOAT.fieldOf("specific_heat_capacity").forGetter(c -> c.specificHeatCapacity),
        Ingredient.CODEC.optionalFieldOf("ingots").forGetter(c -> c.ingots),
        Ingredient.CODEC.optionalFieldOf("double_ingots").forGetter(c -> c.doubleIngots),
        Ingredient.CODEC.optionalFieldOf("sheets").forGetter(c -> c.sheets)
    ).apply(i, FluidHeat::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidHeat> STREAM_CODEC = StreamCodecs.composite(
        ByteBufCodecs.registry(Registries.FLUID), c -> c.fluid,
        ByteBufCodecs.VAR_INT, c -> c.tier,
        ByteBufCodecs.FLOAT, c -> c.meltTemperature,
        ByteBufCodecs.FLOAT, c -> c.specificHeatCapacity,
        ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), c -> c.ingots,
        ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), c -> c.doubleIngots,
        ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), c -> c.sheets,
        FluidHeat::new
    );

    public static final ResourceLocation UNKNOWN_ID = Helpers.identifier("unknown");

    public static final DataManager<FluidHeat> MANAGER = new DataManager<>(Helpers.identifier("fluid_heat"), "fluid heat", CODEC, STREAM_CODEC);
    private static final Map<Fluid, FluidHeat> CACHE = new HashMap<>();

    /**
     * Reverse lookup for metals attached to fluids.
     * For the other direction, see {@link FluidHeat#fluid()}.
     *
     * @param fluid The fluid, can be empty.
     * @return A metal if it exists, and null if it doesn't.
     */
    @Nullable
    public static FluidHeat get(Fluid fluid)
    {
        return CACHE.get(fluid);
    }

    /**
     * Get the 'unknown' metal. This is the only metal that any assurances are made that it exists.
     */
    public static FluidHeat unknown()
    {
        return MANAGER.getOrThrow(UNKNOWN_ID);
    }

    /**
     * @return The matching metal for a given ingot, as defined by the metal itself.
     */
    @Nullable
    public static FluidHeat getFromIngot(ItemStack stack)
    {
        for (FluidHeat metal : MANAGER.getValues())
        {
            if (metal.isIngot(stack) || metal.isDoubleIngot(stack))
            {
                return metal;
            }
        }
        return null;
    }

    @Nullable
    public static FluidHeat getFromSheet(ItemStack stack)
    {
        for (FluidHeat metal : MANAGER.getValues())
        {
            if (metal.isSheet(stack))
            {
                return metal;
            }
        }
        return null;
    }

    public static void updateMetalFluidMap()
    {
        // Ensure 'unknown' metal exists
        unknown();

        // Reload fluid -> metal map
        CACHE.clear();
        for (FluidHeat metal : MANAGER.getValues())
        {
            CACHE.put(metal.fluid(), metal);
        }
    }

    /**
     * <strong>Not for general purpose use!</strong> Explicitly creates unregistered metals outside the system, which are able to act as rendering stubs.
     */
    public FluidHeat(Fluid fluid)
    {
        this(fluid, 0, 0, 0, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public FluidHeat(Fluid fluid, int tier, float meltTemperature, float specificHeatCapacity, Optional<Ingredient> ingots, Optional<Ingredient> doubleIngots, Optional<Ingredient> sheets)
    {
        this(BuiltInRegistries.FLUID.getKey(fluid), fluid, tier, meltTemperature, specificHeatCapacity, ingots, doubleIngots, sheets);
    }

    private FluidHeat(ResourceLocation id, Fluid fluid, int tier, float meltTemperature, float specificHeatCapacity, Optional<Ingredient> ingots, Optional<Ingredient> doubleIngots, Optional<Ingredient> sheets)
    {
        this(
            fluid, tier, meltTemperature, specificHeatCapacity,
            id.withPrefix("block/metal/block/"),
            id.withPrefix("block/metal/smooth/"),
            "metal." + id.getNamespace() + "." + id.getPath(),
            ingots, doubleIngots, sheets
        );
    }

    /**
     * @return The Specific Heat Capacity of the metal. Units of Energy / °C
     * @see IHeat#getHeatCapacity()
     */
    public float heatCapacity(float mB)
    {
        return specificHeatCapacity() * mB;
    }

    public MutableComponent getDisplayName()
    {
        return Component.translatable(translationKey);
    }

    public boolean isIngot(ItemStack stack)
    {
        return ingots.isPresent() && ingots.get().test(stack);
    }

    public boolean isDoubleIngot(ItemStack stack)
    {
        return ingots.isPresent() && ingots.get().test(stack);
    }

    public boolean isSheet(ItemStack stack)
    {
        return sheets.isPresent() && sheets.get().test(stack);
    }
}