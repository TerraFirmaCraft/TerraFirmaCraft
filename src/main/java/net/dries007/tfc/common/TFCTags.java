/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import net.minecraftforge.common.Tags;

import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.util.Helpers;

public class TFCTags
{
    public static Tags.IOptionalNamedTag<Item> itemTag(String id)
    {
        return ItemTags.createOptional(Helpers.identifier(id));
    }

    public static Tags.IOptionalNamedTag<Block> forgeBlockTag(String name)
    {
        return BlockTags.createOptional(new ResourceLocation("forge", name));
    }


    public static Tags.IOptionalNamedTag<Block> blockTag(String id)
    {
        return BlockTags.createOptional(Helpers.identifier(id));
    }

    public static Tags.IOptionalNamedTag<Item> forgeItemTag(String name)
    {
        return ItemTags.createOptional(new ResourceLocation("forge", name));
    }

    public static class Blocks
    {
        public static final Tags.IOptionalNamedTag<Block> CAN_TRIGGER_COLLAPSE = blockTag("can_trigger_collapse");
        public static final Tags.IOptionalNamedTag<Block> CAN_START_COLLAPSE = blockTag("can_start_collapse");
        public static final Tags.IOptionalNamedTag<Block> CAN_COLLAPSE = blockTag("can_collapse");
        public static final Tags.IOptionalNamedTag<Block> CAN_LANDSLIDE = blockTag("can_landslide");

        public static final Tags.IOptionalNamedTag<Block> SUPPORTS_LANDSLIDE = blockTag("supports_landslide"); // Non-full blocks that count as full blocks for the purposes of landslide side support check

        public static final Tags.IOptionalNamedTag<Block> GRASS = blockTag("grass"); // Used for connected textures on grass blocks, different from the vanilla/forge tag

        public static final Tags.IOptionalNamedTag<Block> TREE_GROWS_ON = blockTag("tree_grows_on"); // Used for tree growth
    }

    public static class Items
    {
        public static final Map<Metal.BlockType, Tags.IOptionalNamedTag<Item>> METAL_ITEM_BLOCK_TYPES = Util.make(new EnumMap<>(Metal.BlockType.class), map ->
        {
            for (Metal.BlockType type : Metal.BlockType.values())
            {
                if (type.getTag() != null)
                {
                    map.put(type, forgeItemTag(type.getTag()));
                }
            }
        });

        public static final Map<Metal.ItemType, Tags.IOptionalNamedTag<Item>> METAL_ITEM_TYPES = Util.make(new EnumMap<>(Metal.ItemType.class), map ->
        {
            for (Metal.ItemType type : Metal.ItemType.values())
            {
                if (type.hasTag())
                {
                    map.put(type, forgeItemTag(type.getTag()));
                }
            }
        });

        public static final Map<Metal.Default, Map<Metal.BlockType, Tags.IOptionalNamedTag<Item>>> METAL_ITEM_BLOCKS = Util.make(new EnumMap<>(Metal.Default.class), map ->
        {
            for (Metal.Default metal : Metal.Default.values())
            {
                Map<Metal.BlockType, Tags.IOptionalNamedTag<Item>> inner = new EnumMap<>(Metal.BlockType.class);
                for (Metal.BlockType type : Metal.BlockType.values())
                {
                    if (type.getTag() != null && type.hasMetal(metal))
                    {
                        inner.put(type, forgeItemTag(type.getTag().concat("/" + metal.name().toLowerCase())));
                    }
                }
                map.put(metal, inner);
            }
        });

        public static final Map<Metal.Default, Map<Metal.ItemType, Tags.IOptionalNamedTag<Item>>> METAL_ITEMS = Util.make(new EnumMap<>(Metal.Default.class), map ->
        {
            for (Metal.Default metal : Metal.Default.values())
            {
                Map<Metal.ItemType, Tags.IOptionalNamedTag<Item>> inner = new EnumMap<>(Metal.ItemType.class);
                for (Metal.ItemType type : Metal.ItemType.values())
                {
                    if (type.hasTag() && type.hasMetal(metal))
                    {
                        inner.put(type, forgeItemTag(type.getTag().concat("/" + metal.name().toLowerCase())));
                    }
                }
                map.put(metal, inner);
            }
        });
    }
}