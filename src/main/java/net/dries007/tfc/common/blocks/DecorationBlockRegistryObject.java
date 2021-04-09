/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraftforge.fml.RegistryObject;

/**
 * A triple of {@link net.minecraftforge.fml.RegistryObject}s for slabs, stairs, and walls
 */
public class DecorationBlockRegistryObject
{
    private final RegistryObject<? extends SlabBlock> slab;
    private final RegistryObject<? extends StairsBlock> stair;
    private final RegistryObject<? extends WallBlock> wall;

    public DecorationBlockRegistryObject(RegistryObject<? extends SlabBlock> slab, RegistryObject<? extends StairsBlock> stair, RegistryObject<? extends WallBlock> wall)
    {
        this.slab = slab;
        this.stair = stair;
        this.wall = wall;
    }

    public SlabBlock getSlab()
    {
        return slab.get();
    }

    public StairsBlock getStair()
    {
        return stair.get();
    }

    public WallBlock getWall()
    {
        return wall.get();
    }
}
