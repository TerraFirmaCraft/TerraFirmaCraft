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

import net.dries007.tfc.util.Helpers;

public class VariableHeightSmokeParticle extends TextureSheetParticle
{
    public VariableHeightSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int lifeTime)
    {
        super(level, x, y, z);
        scale(3.0F);
        setSize(0.25F, 0.25F);
        lifetime = random.nextInt(50) + lifeTime;
        gravity = 3.0E-6F;
        xd = xSpeed;
        yd = ySpeed + (double) (random.nextFloat() / 500.0F);
        zd = zSpeed;
    }

    @Override
    public void tick()
    {
        xo = x;
        yo = y;
        zo = z;
        if (age++ < lifetime && alpha > 0)
        {
            xd += Helpers.triangle(random) / 5000;
            zd += Helpers.triangle(random) / 5000;
            yd -= gravity;
            move(xd, yd, zd);
            if (age >= lifetime - 60 && alpha > 0.01F)
            {
                alpha -= 0.015F;
            }

        }
        else
        {
            remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites, int lifetime) implements ParticleProvider<SimpleParticleType>
    {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            final VariableHeightSmokeParticle particle = new VariableHeightSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, lifetime);
            particle.pickSprite(sprites);
            particle.setAlpha(0.92f);
            return particle;
        }
    }
}
