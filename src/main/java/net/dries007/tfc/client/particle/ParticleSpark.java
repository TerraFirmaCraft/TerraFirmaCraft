/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.dries007.tfc.Constants.RNG;

@SideOnly(Side.CLIENT)
public class ParticleSpark extends Particle
{
    public ParticleSpark(World worldIn, double x, double y, double z, double speedX, double speedY, double speedZ, int duration)
    {
        super(worldIn, x, y, z, speedX, speedY, speedZ);
        this.motionX *= 0.5D;
        this.motionY *= 0.5D;
        this.motionZ *= 0.5D;
        this.motionX *= speedX;
        this.motionY *= speedY;
        this.motionZ *= speedZ;
        float f = (float) (RNG.nextFloat() * 0.3D + 0.6D);
        float f2 = RNG.nextFloat() / 5;
        this.particleRed = f;
        this.particleGreen = f - f2;
        this.particleBlue = 0;
        this.particleScale *= 0.75F;
        this.particleMaxAge = duration + RNG.nextInt(5);
    }

    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) { this.setExpired(); }

        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.particleRed > 0)
            this.particleRed *= 0.9D;
        if (this.particleGreen > 0)
            this.particleGreen *= 0.82D;

        this.motionX *= 0.9D;
        this.motionY *= 0.7D;
        this.motionZ *= 0.9D;
        this.motionY -= 0.03D;

        if (this.onGround)
        {
            this.motionX = 0D;
            this.motionZ = 0D;
        }
    }

    @Override
    public int getFXLayer() { return 1; }
}
