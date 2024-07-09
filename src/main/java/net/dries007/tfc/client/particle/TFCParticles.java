/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import java.util.List;
import java.util.function.Function;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;

public final class TFCParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MOD_ID);

    public static final Id<SimpleParticleType> BUBBLE = register("bubble");
    public static final Id<SimpleParticleType> WATER_FLOW = register("water_flow");
    public static final Id<SimpleParticleType> STEAM = register("steam");
    public static final Id<SimpleParticleType> NITROGEN = register("nitrogen");
    public static final Id<SimpleParticleType> PHOSPHORUS = register("phosphorus");
    public static final Id<SimpleParticleType> POTASSIUM = register("potassium");
    public static final Id<SimpleParticleType> COMPOST_READY = register("compost_ready");
    public static final Id<SimpleParticleType> COMPOST_ROTTEN = register("compost_rotten");
    public static final Id<SimpleParticleType> SLEEP = register("sleep");
    public static final Id<SimpleParticleType> LEAF = register("leaf");
    public static final Id<SimpleParticleType> WIND = register("wind");
    public static final Id<SimpleParticleType> SNOWFLAKE = register("snowflake");
    public static final Id<SimpleParticleType> FLYING_SNOWFLAKE = register("flying_snowflake");
    public static final Id<BlockParticleOption> FALLING_LEAF = register("falling_leaf", BlockParticleOption.DESERIALIZER, BlockParticleOption::codec);
    public static final Id<SimpleParticleType> FEATHER = register("feather");
    public static final Id<SimpleParticleType> SPARK = register("spark");
    public static final Id<SimpleParticleType> BUTTERFLY = register("butterfly");
    public static final Id<SimpleParticleType> SMOKE_0 = register("smoke_0");
    public static final Id<SimpleParticleType> SMOKE_1 = register("smoke_1");
    public static final Id<SimpleParticleType> SMOKE_2 = register("smoke_2");
    public static final Id<SimpleParticleType> SMOKE_3 = register("smoke_3");
    public static final Id<SimpleParticleType> SMOKE_4 = register("smoke_4");
    public static final Id<FluidParticleOption> FLUID_DRIP = register("fluid_drip", FluidParticleOption.DESERIALIZER, FluidParticleOption::getCodec);
    public static final Id<FluidParticleOption> FLUID_FALL = register("fluid_fall", FluidParticleOption.DESERIALIZER, FluidParticleOption::getCodec);
    public static final Id<FluidParticleOption> FLUID_LAND = register("fluid_land", FluidParticleOption.DESERIALIZER, FluidParticleOption::getCodec);
    public static final Id<FluidParticleOption> BARREL_DRIP = register("barrel_drip", FluidParticleOption.DESERIALIZER, FluidParticleOption::getCodec);

    public static final List<Id<SimpleParticleType>> SMOKES = List.of(SMOKE_0, SMOKE_1, SMOKE_2, SMOKE_3, SMOKE_4);

    private static <T extends ParticleOptions> Id<T> register(String name, ParticleOptions.Deserializer<T> deserializer, final Function<ParticleType<T>, Codec<T>> codec)
    {
        return PARTICLE_TYPES.register(name, () -> new ParticleType<>(false, deserializer) {
            @Override
            public MapCodec<ParticleOptions> codec()
            {
                return codec.apply(this);
            }
        });
    }

    private static Id<SimpleParticleType> register(String name)
    {
        return new Id<>(PARTICLE_TYPES.register(name, () -> new SimpleParticleType(false)));
    }

    public record Id<T extends ParticleOptions>(DeferredHolder<ParticleType<?>, ParticleType<T>> holder)
        implements RegistryHolder<ParticleType<?>, ParticleType<T>> {}
}
