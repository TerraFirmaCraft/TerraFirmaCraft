/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.vein;

import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.util.OreSpawnData;
import net.dries007.tfc.world.classic.worldgen.WorldGenOre;

public abstract class VeinType
{
    public final BlockPos pos;
    public final OreSpawnData.OreEntry oreSpawnData;
    public final Ore.Grade grade;

    VeinType(BlockPos pos, OreSpawnData.OreEntry oreSpawnData, Ore.Grade grade)
    {
        this.pos = pos;
        this.oreSpawnData = oreSpawnData;
        this.grade = grade;
    }

    public final boolean inRange(BlockPos pos1)
    {
        return Math.pow(pos1.getX() - this.pos.getX(), 2) + Math.pow(pos1.getZ() - this.pos.getZ(), 2) <= WorldGenOre.VEIN_MAX_RADIUS_SQUARED;
    }

    public int getLowestY()
    {
        return Math.max(pos.getY() - WorldGenOre.VEIN_MAX_RADIUS / 2, oreSpawnData.minY);
    }

    public int getHighestY()
    {
        return Math.min(pos.getY() + WorldGenOre.VEIN_MAX_RADIUS / 2, oreSpawnData.maxY);
    }

    public abstract double getChanceToGenerate(BlockPos pos1);
}
