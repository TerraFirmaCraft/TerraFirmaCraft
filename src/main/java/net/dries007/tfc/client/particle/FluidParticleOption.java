/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;

public class FluidParticleOption implements ParticleOptions
{
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_FLUID = new DynamicCommandExceptionType(f -> Helpers.translatable("tfc.commands.particle.no_fluid", f));

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<FluidParticleOption> DESERIALIZER = new Deserializer<>() {
        @Override
        public FluidParticleOption fromCommand(ParticleType<FluidParticleOption> type, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            final ResourceLocation res = ResourceLocation.read(reader);
            final Fluid fluid = ForgeRegistries.FLUIDS.getValue(res);
            if (fluid == null)
            {
                throw ERROR_UNKNOWN_FLUID.create(res.toString());
            }
            return new FluidParticleOption(type, fluid);
        }

        @Override
        public FluidParticleOption fromNetwork(ParticleType<FluidParticleOption> type, FriendlyByteBuf buffer)
        {
            Fluid fluid = buffer.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
            if (fluid == null) fluid = Fluids.WATER;
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
        buffer.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
    }

    /**
     * Used only in crash reports.
     */
    @Override
    public String writeToString()
    {
        return ForgeRegistries.PARTICLE_TYPES.getKey(type) + " " + new FluidStack(fluid, 1).getDisplayName();
    }

}
