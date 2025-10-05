/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import net.dries007.tfc.client.ClimateRenderCache;

public class WindParticle extends TextureSheetParticle
{
    private final float xBias, zBias, amplitude;

    public WindParticle(ClientLevel level, double x, double y, double z)
    {
        super(level, x, y, z);
        final Vec2 wind = ClimateRenderCache.INSTANCE.getWind();
        lifetime = 100;
        age = random.nextInt(20);
        amplitude = random.nextFloat() * 0.034f;
        final float speed = 0.4f * (wind.length() * 0.2f + 0.9f) + amplitude;
        xBias = wind.x * speed;
        zBias = wind.y * speed;
        scale(random.nextFloat() * 0.4f + 0.1f);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (onGround)
        {
            yd = 0.09f;
        }
        else
        {
            yd = Mth.lerp(yd, Mth.sin(age * 0.05f) * amplitude, 0.1f);
        }
        xd = xBias;
        zd = zBias;
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public record Provider(SpriteSet sprite) implements ParticleProvider<SimpleParticleType>
    {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            WindParticle particle = new WindParticle(level, x, y, z);
            particle.pickSprite(sprite);
            return particle;
        }
    }
}
