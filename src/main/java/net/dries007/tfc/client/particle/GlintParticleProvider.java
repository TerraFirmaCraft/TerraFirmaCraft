package net.dries007.tfc.client.particle;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class GlintParticleProvider implements ParticleProvider<SimpleParticleType>
{
    private final SpriteSet sprite;
    private final float red;
    private final float green;
    private final float blue;

    public GlintParticleProvider(SpriteSet sprite, ChatFormatting format)
    {
        this.sprite = sprite;
        final int color = format.getColor();
        this.red = ((color >> 16) & 0xFF) / 255F;
        this.green = ((color >> 8) & 0xFF) / 255F;
        this.blue = (color & 0xFF) / 255F;
    }
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        SuspendedTownParticle suspendedtownparticle = new SuspendedTownParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
        suspendedtownparticle.pickSprite(sprite);
        suspendedtownparticle.setColor(red, green, blue);
        return suspendedtownparticle;
    }
}