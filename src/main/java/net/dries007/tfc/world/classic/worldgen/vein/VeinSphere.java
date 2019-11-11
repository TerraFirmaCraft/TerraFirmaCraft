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
    private final double innerRadiusSq, outerRadiusSq;

    VeinSphere(BlockPos pos, VeinType type, Ore.Grade grade, Random rand)
    {
        super(pos, type, grade);
        double innerRadius = (0.5 + 0.3 * rand.nextDouble()) * type.getWidth();
        this.innerRadiusSq = innerRadius * innerRadius;
        this.outerRadiusSq = (innerRadius + 0.1 * type.getWidth()) * (innerRadius + 0.1 * type.getWidth());
    }

    @Override
    public double getChanceToGenerate(BlockPos pos)
    {
        double dist = this.pos.distanceSq(pos);
        if (dist < innerRadiusSq)
        {
            return type.getDensity();
        }
        else if (dist < outerRadiusSq)
        {
            return type.getDensity() * (outerRadiusSq - dist) / (outerRadiusSq - innerRadiusSq);
        }
        return 0;
    }
}
