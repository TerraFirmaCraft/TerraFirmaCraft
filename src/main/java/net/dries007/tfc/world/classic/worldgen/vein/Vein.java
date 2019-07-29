/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.vein;

import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.world.classic.worldgen.WorldGenOreVeins;

public abstract class Vein
{
    public final BlockPos pos;
    public final VeinType type;
    public final Ore.Grade grade;

    Vein(BlockPos pos, VeinType type, Ore.Grade grade)
    {
        this.pos = pos;
        this.type = type;
        this.grade = grade;
    }

    public final boolean inRange(BlockPos pos1)
    {
        return Math.pow(pos1.getX() - this.pos.getX(), 2) + Math.pow(pos1.getZ() - this.pos.getZ(), 2) <= WorldGenOreVeins.VEIN_MAX_RADIUS * WorldGenOreVeins.VEIN_MAX_RADIUS;
    }

    public int getLowestY()
    {
        return Math.max(pos.getY() - WorldGenOreVeins.VEIN_MAX_RADIUS / 2, type.minY);
    }

    public int getHighestY()
    {
        return Math.min(pos.getY() + WorldGenOreVeins.VEIN_MAX_RADIUS / 2, type.maxY);
    }

    public abstract double getChanceToGenerate(BlockPos pos1);
}
