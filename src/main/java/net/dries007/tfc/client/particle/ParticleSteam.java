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
public class ParticleSteam extends Particle
{
    protected ParticleSteam(World world, double x, double y, double z, double speedX, double speedY, double speedZ, int duration)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.particleAlpha = 0.05F;
        this.particleMaxAge = duration;
    }

    @Override
    public boolean shouldDisableDepth()
    {
        // This is needed to order the transparency later than the fluid block
        // Fix it being completely transparent
        return true;
    }

    @Override
    public int getFXLayer()
    {
        return 1;
    }
}
