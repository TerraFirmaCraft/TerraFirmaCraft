/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.stateprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.registry.RegistryHolder;

public class TFCStateProviders
{
    public static final DeferredRegister<BlockStateProviderType<?>> BLOCK_STATE_PROVIDERS = DeferredRegister.create(Registries.BLOCK_STATE_PROVIDER_TYPE, TerraFirmaCraft.MOD_ID);

    public static final Id<RandomPropertyProvider> RANDOM_PROPERTY = register("random_property", RandomPropertyProvider.CODEC);

    private static <T extends BlockStateProvider> Id<T> register(String name, MapCodec<T> codec)
    {
        return new Id<>(BLOCK_STATE_PROVIDERS.register(name, () -> new BlockStateProviderType<>(codec)));
    }

    public record Id<T extends BlockStateProvider>(DeferredHolder<BlockStateProviderType<?>, BlockStateProviderType<T>> holder)
        implements RegistryHolder<BlockStateProviderType<?>, BlockStateProviderType<T>> {}
}
