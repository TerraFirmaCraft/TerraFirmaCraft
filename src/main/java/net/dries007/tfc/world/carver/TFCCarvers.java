/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.function.Function;

import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCCarvers
{
    public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, MOD_ID);

    public static final RegistryObject<TFCCaveCarver> CAVE = register("cave", TFCCaveCarver::new, ProbabilityFeatureConfiguration.CODEC);
    public static final RegistryObject<TFCRavineCarver> CANYON = register("canyon", TFCRavineCarver::new, ProbabilityFeatureConfiguration.CODEC);
    public static final RegistryObject<TFCUnderwaterCaveCarver> UNDERWATER_CAVE = register("underwater_cave", TFCUnderwaterCaveCarver::new, ProbabilityFeatureConfiguration.CODEC);
    public static final RegistryObject<TFCUnderwaterRavineCarver> UNDERWATER_CANYON = register("underwater_canyon", TFCUnderwaterRavineCarver::new, ProbabilityFeatureConfiguration.CODEC);

    public static final RegistryObject<WorleyCaveCarver> WORLEY_CAVE = register("worley_cave", WorleyCaveCarver::new, WorleyCaveConfig.CODEC);

    private static <C extends CarverConfiguration, WC extends WorldCarver<C>> RegistryObject<WC> register(String name, Function<Codec<C>, WC> factory, Codec<C> codec)
    {
        return CARVERS.register(name, () -> factory.apply(codec));
    }
}