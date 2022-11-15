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

public class AnimatedParticle extends TextureSheetParticle
{
    private final SpriteSet sprites;

    protected AnimatedParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites)
    {
        super(level, x, y, z);
        this.xd = Mth.nextFloat(random, -0.1f, 0.1f);
        this.yd = Mth.nextFloat(random, -0.05f, 0.1f);
        this.zd = Mth.nextFloat(random, -0.1f, 0.1f);

        this.lifetime = 40 + this.random.nextInt(60);
        scale(0.5f + random.nextFloat());
        this.sprites = sprites;
        setSpriteFromAge(sprites);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!removed)
        {
            setSprite(sprites.get(age % 4, 4));
        }
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType>
    {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new AnimatedParticle(level, x, y, z, sprites);
        }
    }
}
