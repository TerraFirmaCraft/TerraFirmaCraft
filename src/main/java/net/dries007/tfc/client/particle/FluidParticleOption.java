/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.world.Codecs;

public class FluidParticleOption implements ParticleOptions
{
    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<FluidParticleOption> DESERIALIZER = new Deserializer<>() {
        @Override
        public FluidParticleOption fromCommand(ParticleType<FluidParticleOption> type, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            final ResourceLocation res = ResourceLocation.read(reader);
            final Fluid fluid = BuiltInRegistries.FLUID.get(res);
            return new FluidParticleOption(type, fluid);
        }

        @Override
        public FluidParticleOption fromNetwork(ParticleType<FluidParticleOption> type, FriendlyByteBuf buffer)
        {
            final Fluid fluid = BuiltInRegistries.FLUID.byId(buffer.readVarInt());
            return new FluidParticleOption(type, fluid);
        }
    };

    public static Codec<FluidParticleOption> getCodec(ParticleType<FluidParticleOption> type)
    {
        return Codecs.FLUID.xmap(f -> new FluidParticleOption(type, f), o -> o.fluid);
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

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(BuiltInRegistries.FLUID.getId(fluid));
    }

    /**
     * Used only in crash reports.
     */
    @Override
    public String writeToString()
    {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(type) + " " + new FluidStack(fluid, 1).getDisplayName();
    }

}
