/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.function.Supplier;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryHolder;

public final class ClimateModels
{
    public static final ResourceKey<Registry<ClimateModelType<?>>> KEY = ResourceKey.createRegistryKey(Helpers.identifier("climate_model"));
    public static final Registry<ClimateModelType<?>> REGISTRY = new RegistryBuilder<>(KEY).sync(true).create();
    public static final DeferredRegister<ClimateModelType<?>> TYPES = DeferredRegister.create(KEY, TerraFirmaCraft.MOD_ID);

    public static final Id<OverworldClimateModel> OVERWORLD = register("overworld", OverworldClimateModel.STREAM_CODEC);
    public static final Id<BiomeBasedClimateModel> BIOME_BASED = register("biome_based", BiomeBasedClimateModel.STREAM_CODEC);

    private static <T extends ClimateModel> Id<T> register(String id, StreamCodec<ByteBuf, T> codec)
    {
        return new Id<>(TYPES.register(id, () -> new ClimateModelType<>(codec)));
    }

    public record Id<T extends ClimateModel>(DeferredHolder<ClimateModelType<?>, ClimateModelType<T>> holder)
        implements RegistryHolder<ClimateModelType<?>, ClimateModelType<T>> {}
}
