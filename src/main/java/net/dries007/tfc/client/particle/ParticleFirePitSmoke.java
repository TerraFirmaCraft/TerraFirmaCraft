/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleFirePitSmoke extends Particle
{
    public ParticleFirePitSmoke(World worldIn, double x, double y, double z, double speedX, double speedY, double speedZ, int duration)
    {
        super(worldIn, x, y, z, speedX, speedY, speedZ);
        this.particleMaxAge = duration;
        this.motionX *= 0.1D;
        this.motionY *= 0.1D;
        this.motionZ *= 0.1D;
        this.motionX += speedX;
        this.motionY += speedY;
        this.motionZ += speedZ;
        this.particleScale *= 1.3F + (rand.nextFloat() / 4);
        this.particleAlpha = (float) 0.5;
    }

    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }

        this.motionY += 0.004D;
        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.96D;
        this.motionY *= 0.96D;
        this.motionZ *= 0.96D;

        if (this.onGround)
        {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
        }
    }

    @Override
    public int getFXLayer()
    {
        return 1;
    }
}
