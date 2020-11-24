/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.carver;

import java.util.function.Function;

import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCCarvers
{
    public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, MOD_ID);

    public static final RegistryObject<TFCCaveCarver> CAVE = register("cave", TFCCaveCarver::new, ProbabilityConfig.CODEC);
    public static final RegistryObject<TFCRavineCarver> CANYON = register("canyon", TFCRavineCarver::new, ProbabilityConfig.CODEC);
    public static final RegistryObject<TFCUnderwaterCaveCarver> UNDERWATER_CAVE = register("underwater_cave", TFCUnderwaterCaveCarver::new, ProbabilityConfig.CODEC);
    public static final RegistryObject<TFCUnderwaterRavineCarver> UNDERWATER_CANYON = register("underwater_canyon", TFCUnderwaterRavineCarver::new, ProbabilityConfig.CODEC);

    public static final RegistryObject<WorleyCaveCarver> WORLEY_CAVE = register("worley_cave", WorleyCaveCarver::new, WorleyCaveConfig.CODEC);

    private static <C extends ICarverConfig, WC extends WorldCarver<C>> RegistryObject<WC> register(String name, Function<Codec<C>, WC> factory, Codec<C> codec)
    {
        return CARVERS.register(name, () -> factory.apply(codec));
    }
}