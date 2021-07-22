/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.shapes.VoxelShape;

public enum GroundcoverBlockType
{
    BONE(GroundcoverBlock.MEDIUM, () -> Items.BONE),
    CLAM(GroundcoverBlock.SMALL),
    DEAD_GRASS(GroundcoverBlock.PIXEL_HIGH),
    DRIFTWOOD(GroundcoverBlock.FLAT),
    FEATHER(GroundcoverBlock.FLAT, () -> Items.FEATHER),
    FLINT(GroundcoverBlock.SMALL, () -> Items.FLINT),
    GUANO(GroundcoverBlock.SMALL),
    MOLLUSK(GroundcoverBlock.SMALL),
    MUSSEL(GroundcoverBlock.SMALL),
    PINECONE(GroundcoverBlock.SMALL),
    PODZOL(GroundcoverBlock.PIXEL_HIGH),
    ROTTEN_FLESH(GroundcoverBlock.FLAT, () -> Items.ROTTEN_FLESH),
    SALT_LICK(GroundcoverBlock.PIXEL_HIGH),
    SEAWEED(GroundcoverBlock.FLAT),
    STICK(GroundcoverBlock.FLAT, () -> Items.STICK);

    private final VoxelShape shape;
    @Nullable
    private final Supplier<? extends Item> vanillaItem; // The vanilla item this corresponds to

    GroundcoverBlockType(VoxelShape shape)
    {
        this(shape, null);
    }

    GroundcoverBlockType(VoxelShape shape, @Nullable Supplier<? extends Item> vanillaItem)
    {
        this.shape = shape;
        this.vanillaItem = vanillaItem;
    }

    public VoxelShape getShape()
    {
        return shape;
    }

    /**
     * @return true if a block item should be created for this groundcover block
     */
    public boolean shouldCreateBlockItem()
    {
        return vanillaItem == null;
    }

    @Nullable
    public Supplier<? extends Item> getVanillaItem()
    {
        return vanillaItem;
    }
}
