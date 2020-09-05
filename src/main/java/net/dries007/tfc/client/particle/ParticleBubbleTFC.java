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
public class ParticleBubbleTFC extends Particle
{
    public ParticleBubbleTFC(World worldIn, double x, double y, double z, double speedX, double speedY, double speedZ, int duration)
    {
        super(worldIn, x, y, z, speedX, speedY, speedZ);
        this.motionX = speedX * 0.2D + (RNG.nextFloat() * 2.0D - 1.0D) * 0.02D;
        this.motionY = speedY * 0.2D + (RNG.nextFloat() * 2.0D - 1.0D) * 0.02D;
        this.motionZ = speedZ * 0.2D + (RNG.nextFloat() * 2.0D - 1.0D) * 0.02D;
        this.motionX += speedX;
        this.motionY += speedY;
        this.motionZ += speedZ;
        this.particleMaxAge = duration + RNG.nextInt(3);
        this.particleScale *= RNG.nextFloat() * 0.6F + 0.2F;
    }

    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) { this.setExpired(); }

        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionY += 0.002D;

        this.motionX *= 0.85D;
        this.motionY *= 0.85D;
        this.motionZ *= 0.85D;
    }

    @Override
    public int getFXLayer()
    {
        return 1;
    }
}
