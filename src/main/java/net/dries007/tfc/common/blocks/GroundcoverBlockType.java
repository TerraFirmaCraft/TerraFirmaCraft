package net.dries007.tfc.common.blocks;

import net.minecraft.util.math.shapes.VoxelShape;

public enum GroundcoverBlockType
{
    BONE(GroundcoverBlock.MEDIUM),
    CLAM(GroundcoverBlock.SMALL),
    DEAD_GRASS(GroundcoverBlock.PIXEL_HIGH),
    DRIFTWOOD(GroundcoverBlock.FLAT),
    FEATHER(GroundcoverBlock.FLAT),
    FLINT(GroundcoverBlock.SMALL),
    GUANO(GroundcoverBlock.SMALL),
    MOLLUSK(GroundcoverBlock.SMALL),
    MUSSEL(GroundcoverBlock.SMALL),
    PINECONE(GroundcoverBlock.SMALL),
    PODZOL(GroundcoverBlock.PIXEL_HIGH),
    ROTTEN_FLESH(GroundcoverBlock.FLAT),
    SALT_LICK(GroundcoverBlock.PIXEL_HIGH),
    SEAWEED(GroundcoverBlock.FLAT),
    STICK(GroundcoverBlock.FLAT);

    private final VoxelShape shape;

    GroundcoverBlockType(VoxelShape shape)
    {
        this.shape = shape;
    }

    public VoxelShape getShape()
    {
        return shape;
    }
}
