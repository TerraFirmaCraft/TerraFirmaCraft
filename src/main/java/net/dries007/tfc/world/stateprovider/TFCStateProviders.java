/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.stateprovider;

import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.mojang.serialization.Codec;
import net.dries007.tfc.TerraFirmaCraft;

public class TFCStateProviders
{
    public static final DeferredRegister<BlockStateProviderType<?>> BLOCK_STATE_PROVIDERS = DeferredRegister.create(ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES, TerraFirmaCraft.MOD_ID);

    public static final RegistryObject<BlockStateProviderType<RandomPropertyProvider>> RANDOM_PROPERTY = register("random_property", RandomPropertyProvider.CODEC);

    private static <T extends BlockStateProvider> RegistryObject<BlockStateProviderType<T>> register(String name, Codec<T> codec)
    {
        return BLOCK_STATE_PROVIDERS.register(name, () -> new BlockStateProviderType<>(codec));
    }
}
