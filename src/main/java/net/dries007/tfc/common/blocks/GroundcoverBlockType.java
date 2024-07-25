/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Function;
import java.util.function.Supplier;

import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;

import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum GroundcoverBlockType
{
    BONE(GroundcoverBlock.MEDIUM, Items.BONE),
    CLAM(GroundcoverBlock.SMALL),
    DEAD_GRASS(GroundcoverBlock.PIXEL_HIGH),
    DRIFTWOOD(GroundcoverBlock.FLAT),
    FEATHER(GroundcoverBlock.FLAT, Items.FEATHER),
    FLINT(GroundcoverBlock.SMALL, Items.FLINT),
    GUANO(GroundcoverBlock.PIXEL_HIGH),
    HUMUS(GroundcoverBlock.PIXEL_HIGH),
    MOLLUSK(GroundcoverBlock.SMALL),
    MUSSEL(GroundcoverBlock.SMALL),
    PINECONE(GroundcoverBlock.SMALL),
    PUMICE(GroundcoverBlock.SMALL),
    ROTTEN_FLESH(GroundcoverBlock.FLAT, Items.ROTTEN_FLESH),
    SALT_LICK(GroundcoverBlock.PIXEL_HIGH, TFCItems.POWDERS.get(Powder.SALT)),
    SEAWEED(GroundcoverBlock.FLAT),
    SEA_URCHIN(GroundcoverBlock.MEDIUM),
    STICK(GroundcoverBlock.FLAT, Items.STICK);

    private final VoxelShape shape;
    private final @Nullable ItemLike vanillaItem; // The vanilla item this corresponds to

    GroundcoverBlockType(VoxelShape shape)
    {
        this(shape, null);
    }

    GroundcoverBlockType(VoxelShape shape, @Nullable ItemLike vanillaItem)
    {
        this.shape = shape;
        this.vanillaItem = vanillaItem;
    }

    public VoxelShape getShape()
    {
        return shape;
    }

    @Nullable
    public Function<Block, BlockItem> createBlockItem()
    {
        return vanillaItem == null ? block -> new BlockItem(block, new Item.Properties()) : null;
    }

    @Nullable
    public ItemLike getVanillaItem()
    {
        return vanillaItem;
    }
}
