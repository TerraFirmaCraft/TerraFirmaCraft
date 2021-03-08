package net.dries007.tfc.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This is {@link BubbleColumnUpParticle} minus the fluid tag check and with a short lifecycle
 */
public class BubbleParticle extends SpriteTexturedParticle
{
    private BubbleParticle(ClientWorld worldIn, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(worldIn, x, y, z);
        this.setSize(0.02F, 0.02F);
        this.quadSize *= random.nextFloat() * 0.6F + 0.2F;
        this.xd = motionX * 0.2D + (Math.random() * 2.0D - 1.0D) * 0.02D;
        this.yd = motionY * 0.2D + (Math.random() * 2.0D - 1.0D) * 0.02D;
        this.zd = motionZ * 0.2D + (Math.random() * 2.0D - 1.0D) * 0.02D;
        this.lifetime = 3 + random.nextInt(3);
    }

    @Override
    public void tick()
    {
        xo = x;
        yo = y;
        zo = z;
        yd += 0.005D;
        if (lifetime-- <= 0)
        {
            remove();
        }
        else
        {
            move(xd, yd, zd);
            xd *= 0.85F;
            yd *= 0.85F;
            zd *= 0.85F;
        }
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite spriteSet)
        {
            sprite = spriteSet;
        }

        public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            BubbleParticle particle = new BubbleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(sprite);
            return particle;
        }
    }
}
