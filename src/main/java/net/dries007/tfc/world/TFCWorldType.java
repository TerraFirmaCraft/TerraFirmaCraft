/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.config.TFCConfig;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCWorldType
{
    public static final DeferredRegister<ForgeWorldType> WORLD_TYPES = DeferredRegister.create(ForgeRegistries.WORLD_TYPES, MOD_ID);

    public static final RegistryObject<ForgeWorldType> WORLD_TYPE = WORLD_TYPES.register("tng", () -> new ForgeWorldType((biomeRegistry, dimensionSettingsRegistry, seed) -> TFCChunkGenerator.defaultChunkGenerator(() -> dimensionSettingsRegistry.getOrThrow(NoiseGeneratorSettings.OVERWORLD), biomeRegistry, seed)));

    /**
     * Override the default world type, in a safe, mixin free, and API providing manner :D
     * Thank you gigahertz!
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setup()
    {
        if (TFCConfig.COMMON.setTFCWorldTypeAsDefault.get() && ForgeConfig.COMMON.defaultWorldType.get().equals("default"))
        {
            ((ForgeConfigSpec.ConfigValue) ForgeConfig.COMMON.defaultWorldType).set(TFCWorldType.WORLD_TYPE.getId().toString());
        }
    }
}
