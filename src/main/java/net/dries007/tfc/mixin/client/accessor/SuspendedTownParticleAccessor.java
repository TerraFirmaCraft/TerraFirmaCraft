package net.dries007.tfc.mixin.client.accessor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SuspendedTownParticle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SuspendedTownParticle.class)
public interface SuspendedTownParticleAccessor
{
    @Invoker("<init>")
    static SuspendedTownParticle invoke$new(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) { throw new AssertionError(); }
}
