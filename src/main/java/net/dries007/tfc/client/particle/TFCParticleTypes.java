package net.dries007.tfc.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCParticleTypes
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);

    public static final RegistryObject<BasicParticleType> BUBBLE = PARTICLE_TYPES.register("bubble", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> STEAM = PARTICLE_TYPES.register("steam", () -> new BasicParticleType(false));
}
