/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);

    public static final RegistryObject<SimpleParticleType> BUBBLE = PARTICLE_TYPES.register("bubble", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> STEAM = PARTICLE_TYPES.register("steam", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> NITROGEN = PARTICLE_TYPES.register("nitrogen", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> PHOSPHORUS = PARTICLE_TYPES.register("phosphorus", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> POTASSIUM = PARTICLE_TYPES.register("potassium", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> COMPOST_READY = PARTICLE_TYPES.register("compost_ready", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> COMPOST_ROTTEN = PARTICLE_TYPES.register("compost_rotten", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SLEEP = PARTICLE_TYPES.register("sleep", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> LEAF = PARTICLE_TYPES.register("leaf", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SPARK = PARTICLE_TYPES.register("spark", () -> new SimpleParticleType(false));
}
