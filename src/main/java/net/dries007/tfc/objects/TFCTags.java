/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import net.dries007.tfc.api.Metal;
import net.dries007.tfc.util.Helpers;

public class TFCTags
{
    public static Tag<Item> itemTag(String id)
    {
        return new ItemTags.Wrapper(Helpers.identifier(id));
    }

    public static Tag<Block> forgeBlockTag(String name)
    {
        return new BlockTags.Wrapper(new ResourceLocation("forge", name));
    }


    public static Tag<Block> blockTag(String id)
    {
        return new BlockTags.Wrapper(Helpers.identifier(id));
    }

    public static Tag<Item> forgeItemTag(String name)
    {
        return new ItemTags.Wrapper(new ResourceLocation("forge", name));
    }

    public static class Blocks
    {
        public static final Tag<Block> CAN_TRIGGER_COLLAPSE = blockTag("can_trigger_collapse");
        public static final Tag<Block> CAN_START_COLLAPSE = blockTag("can_start_collapse");
        public static final Tag<Block> CAN_COLLAPSE = blockTag("can_collapse");
        public static final Tag<Block> CAN_LANDSLIDE = blockTag("can_landslide");

        public static final Tag<Block> SUPPORTS_LANDSLIDE = blockTag("supports_landslide"); // Non-full blocks that count as full blocks for the purposes of landslide side support check

        public static final Tag<Block> GRASS = blockTag("grass");
    }

    public static class Items
    {
        public static final Map<Metal.BlockType, Tag<Item>> METAL_ITEM_BLOCK_TYPES = Util.make(new EnumMap<>(Metal.BlockType.class), map ->
        {
            for (Metal.BlockType type : Metal.BlockType.values())
            {
                if (type.hasTag())
                {
                    map.put(type, forgeItemTag(type.getTag()));
                }
            }
        });

        public static final Map<Metal.ItemType, Tag<Item>> METAL_ITEM_TYPES = Util.make(new EnumMap<>(Metal.ItemType.class), map ->
        {
            for (Metal.ItemType type : Metal.ItemType.values())
            {
                if (type.hasTag())
                {
                    map.put(type, forgeItemTag(type.getTag()));
                }
            }
        });

        public static final Map<Metal.Default, Map<Metal.BlockType, Tag<Item>>> METAL_ITEM_BLOCKS = Util.make(new EnumMap<>(Metal.Default.class), map ->
        {
            for (Metal.Default metal : Metal.Default.values())
            {
                Map<Metal.BlockType, Tag<Item>> inner = new EnumMap<>(Metal.BlockType.class);
                for (Metal.BlockType type : Metal.BlockType.values())
                {
                    if (type.hasTag() && type.hasType(metal))
                    {
                        inner.put(type, forgeItemTag(type.getTag().concat("/" + metal.name().toLowerCase())));
                    }
                }
                map.put(metal, inner);
            }
        });

        public static final Map<Metal.Default, Map<Metal.ItemType, Tag<Item>>> METAL_ITEMS = Util.make(new EnumMap<>(Metal.Default.class), map ->
        {
            for (Metal.Default metal : Metal.Default.values())
            {
                Map<Metal.ItemType, Tag<Item>> inner = new EnumMap<>(Metal.ItemType.class);
                for (Metal.ItemType type : Metal.ItemType.values())
                {
                    if (type.hasTag() && type.hasType(metal))
                    {
                        inner.put(type, forgeItemTag(type.getTag().concat("/" + metal.name().toLowerCase())));
                    }
                }
                map.put(metal, inner);
            }
        });
    }
}
