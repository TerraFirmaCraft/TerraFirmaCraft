/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.world.Codecs;

public class FluidParticleOption implements ParticleOptions
{
    public static MapCodec<FluidParticleOption> codec(ParticleType<FluidParticleOption> type)
    {
        return Codecs.FLUID.xmap(
            fluid -> new FluidParticleOption(type, fluid),
            option -> option.fluid
        ).fieldOf("fluid");
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, FluidParticleOption> streamCodec(ParticleType<FluidParticleOption> type)
    {
        return ByteBufCodecs.registry(Registries.FLUID).map(
            fluid -> new FluidParticleOption(type, fluid),
            option -> option.fluid
        );
    }

    private final ParticleType<FluidParticleOption> type;
    private final Fluid fluid;

    public FluidParticleOption(ParticleType<FluidParticleOption> type, Fluid fluid)
    {
        this.type = type;
        this.fluid = fluid;
    }

    public Fluid getFluid()
    {
        return fluid;
    }

    @Override
    public ParticleType<?> getType()
    {
        return type;
    }
}
