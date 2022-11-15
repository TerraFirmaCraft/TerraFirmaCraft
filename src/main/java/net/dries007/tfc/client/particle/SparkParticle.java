/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class SparkParticle extends TextureSheetParticle
{
    private SparkParticle(ClientLevel level, double x, double y, double z)
    {
        super(level, x, y, z);
        this.rCol = level.random.nextFloat() * 0.3f + 0.6f;
        this.gCol = this.rCol - (level.random.nextFloat() / 5f);
        this.bCol = 0;
        this.lifetime = 60 + level.random.nextInt(40);
        scale(Mth.nextFloat(level.random, 0.7f, 0.9f));
    }

    @Override
    public void tick()
    {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime)
        {
            remove();
        }
        else
        {
            move(xd, yd, zd);
            if (speedUpWhenYMotionIsBlocked && y == yo)
            {
                xd *= 1.1D;
                zd *= 1.1D;
            }

            xd *= 0.9f;
            yd *= 0.7f;
            zd *= 0.9f;
            yd -= 0.03f;

            if (onGround)
            {
                xd = yd = zd = 0f;
            }

        }
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
            SparkParticle particle = new SparkParticle(level, x, y, z);
            particle.xd = xSpeed;
            particle.yd = ySpeed;
            particle.zd = zSpeed;
            particle.pickSprite(sprite);
            return particle;
        }
    }
}
