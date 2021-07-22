/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.function.Function;

import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public final class TFCDecorators
{
    public static final DeferredRegister<Placement<?>> DECORATORS = DeferredRegister.create(ForgeRegistries.DECORATORS, MOD_ID);

    public static final RegistryObject<FlatEnoughDecorator> FLAT_ENOUGH = register("flat_enough", FlatEnoughDecorator::new, FlatEnoughConfig.CODEC);
    public static final RegistryObject<ClimateDecorator> CLIMATE = register("climate", ClimateDecorator::new, ClimateConfig.CODEC);
    public static final RegistryObject<NearWaterDecorator> NEAR_WATER = register("near_water", NearWaterDecorator::new, NearWaterConfig.CODEC);
    public static final RegistryObject<BoundedCarvingMaskDecorator> BOUNDED_CARVING_MASK = register("bounded_carving_mask", BoundedCarvingMaskDecorator::new, BoundedCarvingMaskConfig.CODEC);
    public static final RegistryObject<VolcanoDecorator> VOLCANO = register("volcano", VolcanoDecorator::new, VolcanoConfig.CODEC);

    private static <C extends IPlacementConfig, D extends Placement<C>> RegistryObject<D> register(String name, Function<Codec<C>, D> factory, Codec<C> codec)
    {
        return DECORATORS.register(name, () -> factory.apply(codec));
    }
}
