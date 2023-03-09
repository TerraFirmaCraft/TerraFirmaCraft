/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import java.util.List;
import java.util.function.Function;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.dries007.tfc.TerraFirmaCraft.*;

public final class TFCParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);

    public static final RegistryObject<SimpleParticleType> BUBBLE = register("bubble");
    public static final RegistryObject<SimpleParticleType> STEAM = register("steam");
    public static final RegistryObject<SimpleParticleType> NITROGEN = register("nitrogen");
    public static final RegistryObject<SimpleParticleType> PHOSPHORUS = register("phosphorus");
    public static final RegistryObject<SimpleParticleType> POTASSIUM = register("potassium");
    public static final RegistryObject<SimpleParticleType> COMPOST_READY = register("compost_ready");
    public static final RegistryObject<SimpleParticleType> COMPOST_ROTTEN = register("compost_rotten");
    public static final RegistryObject<SimpleParticleType> SLEEP = register("sleep");
    public static final RegistryObject<SimpleParticleType> LEAF = register("leaf");
    public static final RegistryObject<SimpleParticleType> FEATHER = register("feather");
    public static final RegistryObject<SimpleParticleType> SPARK = register("spark");
    public static final RegistryObject<SimpleParticleType> BUTTERFLY = register("butterfly");
    public static final RegistryObject<SimpleParticleType> SMOKE_0 = register("smoke_0");
    public static final RegistryObject<SimpleParticleType> SMOKE_1 = register("smoke_1");
    public static final RegistryObject<SimpleParticleType> SMOKE_2 = register("smoke_2");
    public static final RegistryObject<SimpleParticleType> SMOKE_3 = register("smoke_3");
    public static final RegistryObject<SimpleParticleType> SMOKE_4 = register("smoke_4");
    public static final RegistryObject<ParticleType<FluidParticleOption>> FLUID_DRIP = register("fluid_drip", FluidParticleOption.DESERIALIZER, FluidParticleOption::getCodec);
    public static final RegistryObject<ParticleType<FluidParticleOption>> FLUID_FALL = register("fluid_fall", FluidParticleOption.DESERIALIZER, FluidParticleOption::getCodec);
    public static final RegistryObject<ParticleType<FluidParticleOption>> FLUID_LAND = register("fluid_land", FluidParticleOption.DESERIALIZER, FluidParticleOption::getCodec);
    public static final RegistryObject<ParticleType<FluidParticleOption>> BARREL_DRIP = register("barrel_drip", FluidParticleOption.DESERIALIZER, FluidParticleOption::getCodec);

    public static final List<RegistryObject<SimpleParticleType>> SMOKES = List.of(SMOKE_0, SMOKE_1, SMOKE_2, SMOKE_3, SMOKE_4);

    @SuppressWarnings("deprecation")
    private static <T extends ParticleOptions> RegistryObject<ParticleType<T>> register(String name, ParticleOptions.Deserializer<T> deserializer, final Function<ParticleType<T>, Codec<T>> codec)
    {
        return PARTICLE_TYPES.register(name, () -> new ParticleType<>(false, deserializer) {
            @Override
            public Codec<T> codec()
            {
                return codec.apply(this);
            }
        });
    }

    private static RegistryObject<SimpleParticleType> register(String name)
    {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(false));
    }
}
