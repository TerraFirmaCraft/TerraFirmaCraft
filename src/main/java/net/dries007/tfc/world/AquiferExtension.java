package net.dries007.tfc.world;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Aquifer;

public interface AquiferExtension extends Aquifer
{
    ChunkPos getPos();
}
