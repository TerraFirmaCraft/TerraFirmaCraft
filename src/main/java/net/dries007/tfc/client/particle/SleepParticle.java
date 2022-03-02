package net.dries007.tfc.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class SleepParticle extends TextureSheetParticle
{
    public SleepParticle(ClientLevel level, double x, double y, double z)
    {
        super(level, x, y, z);
        quadSize *= 0.75f;
        lifetime = 60 + level.random.nextInt(12);
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public record Provider(SpriteSet sprite) implements ParticleProvider<SimpleParticleType>
    {
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            SleepParticle particle = new SleepParticle(level, x, y, z);
            particle.xd = xSpeed; particle.yd = ySpeed; particle.zd = zSpeed;
            particle.pickSprite(sprite);
            return particle;
        }
    }
}
