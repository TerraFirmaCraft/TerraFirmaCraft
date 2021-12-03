/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.function.Function;

import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCCarvers
{
    public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, MOD_ID);

    public static final RegistryObject<TFCCaveCarver> CAVE = register("cave", TFCCaveCarver::new, CaveCarverConfiguration.CODEC);
    public static final RegistryObject<TFCCanyonCarver> CANYON = register("canyon", TFCCanyonCarver::new, CanyonCarverConfiguration.CODEC);

    private static <C extends CarverConfiguration, WC extends WorldCarver<C>> RegistryObject<WC> register(String name, Function<Codec<C>, WC> factory, Codec<C> codec)
    {
        return CARVERS.register(name, () -> factory.apply(codec));
    }
}