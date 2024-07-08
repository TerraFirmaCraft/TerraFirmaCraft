/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.density;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.Helpers;

@SuppressWarnings("unused")
public final class TFCDensityFunctions
{
    public static final DeferredRegister<Codec<? extends DensityFunction>> TYPES = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, TerraFirmaCraft.MOD_ID);

    public static final RegistryObject<Codec<Spaghetti2D>> SPAGHETTI2D = TYPES.register("spaghetti2d", Spaghetti2D.CODEC::codec);
    public static final RegistryObject<Codec<Spaghetti3D>> SPAGHETTI3D = TYPES.register("spaghetti3d", Spaghetti3D.CODEC::codec);

    public static final ResourceKey<DensityFunction> NOISE_CAVES = key("noise_caves");
    public static final ResourceKey<DensityFunction> NOODLE_TOGGLE = key("noodle_caves");
    public static final ResourceKey<DensityFunction> NOODLE_THICKNESS = key("noodle_thickness");
    public static final ResourceKey<DensityFunction> NOODLE_RIDGE_A = key("noodle_ridge_a");
    public static final ResourceKey<DensityFunction> NOODLE_RIDGE_B = key("noodle_ridge_b");

    private static ResourceKey<DensityFunction> key(String id)
    {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, Helpers.identifier(id));
    }
}
