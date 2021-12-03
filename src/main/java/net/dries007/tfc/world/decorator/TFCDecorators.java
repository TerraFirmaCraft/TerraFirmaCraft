/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.function.Function;

import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public final class TFCDecorators
{
    public static final DeferredRegister<FeatureDecorator<?>> DECORATORS = DeferredRegister.create(ForgeRegistries.DECORATORS, MOD_ID);

    public static final RegistryObject<FlatEnoughDecorator> FLAT_ENOUGH = register("flat_enough", FlatEnoughDecorator::new, FlatEnoughConfig.CODEC);
    public static final RegistryObject<ClimateDecorator> CLIMATE = register("climate", ClimateDecorator::new, ClimateConfig.CODEC);
    public static final RegistryObject<NearWaterDecorator> NEAR_WATER = register("near_water", NearWaterDecorator::new, NearWaterConfig.CODEC);
    public static final RegistryObject<TFCCarvingMaskDecorator> CARVING_MASK = register("carving_mask", TFCCarvingMaskDecorator::new, TFCCarvingMaskConfig.CODEC);
    public static final RegistryObject<VolcanoDecorator> VOLCANO = register("volcano", VolcanoDecorator::new, VolcanoConfig.CODEC);

    private static <C extends DecoratorConfiguration, D extends FeatureDecorator<C>> RegistryObject<D> register(String name, Function<Codec<C>, D> factory, Codec<C> codec)
    {
        return DECORATORS.register(name, () -> factory.apply(codec));
    }
}
