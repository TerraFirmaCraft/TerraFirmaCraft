/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.vein;

import java.util.Random;

import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.api.types.Ore;

public class VeinSphere extends Vein
{
    private final double radiusSq;

    VeinSphere(BlockPos pos, VeinType type, Ore.Grade grade, Random rand)
    {
        super(pos, type, grade);
        double innerRadius = (0.5 + 0.3 * rand.nextDouble()) * type.getWidth();
        this.radiusSq = innerRadius * innerRadius;
    }

    @Override
    public double getChanceToGenerate(BlockPos pos)
    {
        double dist = this.pos.distanceSq(pos) / radiusSq;
        if (dist < 0.8)
        {
            return type.getDensity();
        }
        else if (dist < 1)
        {
            return type.getDensity() * (1 - dist) / 0.2;
        }
        else
        {
            return 0;
        }
    }
}
