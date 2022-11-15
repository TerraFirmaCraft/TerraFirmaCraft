/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class SteamParticle extends TextureSheetParticle
{
    private SteamParticle(ClientLevel worldIn, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(worldIn, x, y, z, motionX, motionY, motionZ);
        setAlpha(0.2F);
        setLifetime((int) (12.0F / (random.nextFloat() * 0.9F + 0.1F)));
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprite) implements ParticleProvider<SimpleParticleType>
    {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            SteamParticle particle = new SteamParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(sprite);
            return particle;
        }
    }
}
