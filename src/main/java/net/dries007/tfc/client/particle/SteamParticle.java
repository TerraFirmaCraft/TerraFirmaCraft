/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class SteamParticle extends SpriteTexturedParticle
{
    private SteamParticle(ClientWorld worldIn, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(worldIn, x, y, z, motionX, motionY, motionZ);
        setAlpha(0.05F);
        setLifetime((int) (12.0F / (random.nextFloat() * 0.9F + 0.1F)));
    }

    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite spriteSet)
        {
            sprite = spriteSet;
        }

        public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            SteamParticle particle = new SteamParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(sprite);
            return particle;
        }
    }
}
