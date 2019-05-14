/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Rock;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

/**
 * This is not the best example of good coding practice, but I do think it works rather well.
 * The reason for the delayed registration it because now the helper's functions can be called in the constructor of
 * the blocks/items (BEFORE they are actually in registries). At this point you cannot yet make an itemstack.
 * Storing based on RegistryName is also not possible, as they don't have one yet.
 */
public class OreDictionaryHelper
{
    private static final Multimap<Thing, String> MAP = HashMultimap.create();
    private static final Converter<String, String> UPPER_UNDERSCORE_TO_LOWER_CAMEL = CaseFormat.UPPER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);
    private static final Joiner JOINER_UNDERSCORE = Joiner.on('_').skipNulls();
    private static boolean done = false;

    public static String toString(Object... parts)
    {
        return UPPER_UNDERSCORE_TO_LOWER_CAMEL.convert(JOINER_UNDERSCORE.join(parts));
    }

    public static String toString(Iterable<Object> parts)
    {
        return UPPER_UNDERSCORE_TO_LOWER_CAMEL.convert(JOINER_UNDERSCORE.join(parts));
    }

    public static String toString(Object[] prefix, Object... parts)
    {
        return toString(ImmutableList.builder().add(prefix).add(parts).build());
    }

    public static void register(Block thing, Object... parts)
    {
        register(new Thing(thing), parts);
    }

    public static void register(Item thing, Object... parts)
    {
        register(new Thing(thing), parts);
    }

    public static void registerMeta(Item thing, int meta, Object... parts)
    {
        register(new Thing(thing, meta), parts);
    }

    public static void registerRockType(Block thing, Rock.Type type, Rock rock, Object... prefixParts)
    {
        registerRockType(new Thing(thing), type, rock, prefixParts);
    }

    public static void registerRockType(Item thing, Rock.Type type, Rock rock, Object... prefixParts)
    {
        registerRockType(new Thing(thing), type, rock, prefixParts);
    }

    public static void init()
    {
        registerVanilla();
        done = true;
        MAP.forEach((t, s) -> OreDictionary.registerOre(s, t.toItemStack()));
        MAP.clear(); // No need to keep this stuff around
    }

    public static Predicate<EntityItem> createPredicateItemEntity(String... names)
    {
        return input -> input.isEntityAlive() && createPredicateStack(names).test(input.getItem());
    }

    /**
     * Checks if an ItemStack has an OreDictionary entry that matches 'name'.
     */
    public static boolean doesStackMatchOre(@Nonnull ItemStack stack, String name)
    {
        if (!OreDictionary.doesOreNameExist(name))
        {
            TerraFirmaCraft.getLog().warn("doesStackMatchOre called with non-existing name. stack: {} name: {}", stack, name);
            return false;
        }
        if (stack.isEmpty()) return false;
        int needle = OreDictionary.getOreID(name);
        for (int id : OreDictionary.getOreIDs(stack))
        {
            if (id == needle) return true;
        }
        return false;
    }

    /**
     * Checks is an ItemStack has ore names, which have a certain prefix
     * used to search for all 'ingots' / all 'plates' etc.
     */
    public static boolean doesStackMatchOrePrefix(@Nonnull ItemStack stack, String prefix)
    {
        if (stack.isEmpty()) return false;
        int[] ids = OreDictionary.getOreIDs(stack);
        for (int id : ids)
        {
            String oreName = OreDictionary.getOreName(id);
            if (oreName.length() >= prefix.length())
            {
                if (oreName.substring(0, prefix.length()).equals(prefix))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * OreDict various vanilla blocks.
     */
    private static void registerVanilla()
    {
        register(Blocks.CONCRETE, "concrete");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 0, "concrete", "white");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 1, "concrete", "orange");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 2, "concrete", "magenta");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 3, "concrete", "light", "blue");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 4, "concrete", "yellow");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 5, "concrete", "lime");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 6, "concrete", "pink");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 7, "concrete", "gray");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 8, "concrete", "light", "gray");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 9, "concrete", "cyan");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 10, "concrete", "purple");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 11, "concrete", "blue");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 12, "concrete", "brown");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 13, "concrete", "green");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 14, "concrete", "red");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE), 15, "concrete", "black");
        register(Blocks.CONCRETE_POWDER, "concrete", "powder");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 0, "concrete", "powder", "white");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 1, "concrete", "powder", "orange");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 2, "concrete", "powder", "magenta");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 3, "concrete", "powder", "light", "blue");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 4, "concrete", "powder", "yellow");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 5, "concrete", "powder", "lime");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 6, "concrete", "powder", "pink");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 7, "concrete", "powder", "gray");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 8, "concrete", "powder", "light", "gray");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 9, "concrete", "powder", "cyan");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 10, "concrete", "powder", "purple");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 11, "concrete", "powder", "blue");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 12, "concrete", "powder", "brown");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 13, "concrete", "powder", "green");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 14, "concrete", "powder", "red");
        registerMeta(Item.getItemFromBlock(Blocks.CONCRETE_POWDER), 15, "concrete", "powder", "black");
        register(Blocks.WOOL, "wool");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 0, "wool", "white");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 1, "wool", "orange");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 2, "wool", "magenta");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 3, "wool", "light", "blue");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 4, "wool", "yellow");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 5, "wool", "lime");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 6, "wool", "pink");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 7, "wool", "gray");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 8, "wool", "light", "gray");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 9, "wool", "cyan");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 10, "wool", "purple");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 11, "wool", "blue");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 12, "wool", "brown");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 13, "wool", "green");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 14, "wool", "red");
        registerMeta(Item.getItemFromBlock(Blocks.WOOL), 15, "wool", "black");
        register(Blocks.CARPET, "carpet");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 0, "carpet", "white");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 1, "carpet", "orange");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 2, "carpet", "magenta");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 3, "carpet", "light", "blue");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 4, "carpet", "yellow");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 5, "carpet", "lime");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 6, "carpet", "pink");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 7, "carpet", "gray");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 8, "carpet", "light", "gray");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 9, "carpet", "cyan");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 10, "carpet", "purple");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 11, "carpet", "blue");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 12, "carpet", "brown");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 13, "carpet", "green");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 14, "carpet", "red");
        registerMeta(Item.getItemFromBlock(Blocks.CARPET), 15, "carpet", "black");
        register(Blocks.HARDENED_CLAY, "terracotta");
        register(Blocks.STAINED_HARDENED_CLAY, "terracotta");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 0, "terracotta", "white");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 1, "terracotta", "orange");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 2, "terracotta", "magenta");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 3, "terracotta", "light", "blue");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 4, "terracotta", "yellow");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 5, "terracotta", "lime");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 6, "terracotta", "pink");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 7, "terracotta", "gray");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 8, "terracotta", "light", "gray");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 9, "terracotta", "cyan");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 10, "terracotta", "purple");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 11, "terracotta", "blue");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 12, "terracotta", "brown");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 13, "terracotta", "green");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 14, "terracotta", "red");
        registerMeta(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY), 15, "terracotta", "black");
        register(Blocks.WHITE_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.WHITE_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.WHITE_GLAZED_TERRACOTTA, "terracotta", "glazed", "white");
        register(Blocks.ORANGE_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.ORANGE_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.ORANGE_GLAZED_TERRACOTTA, "terracotta", "glazed", "orange");
        register(Blocks.MAGENTA_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.MAGENTA_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.MAGENTA_GLAZED_TERRACOTTA, "terracotta", "glazed", "magenta");
        register(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, "terracotta", "glazed", "light", "blue");
        register(Blocks.YELLOW_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.YELLOW_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.YELLOW_GLAZED_TERRACOTTA, "terracotta", "glazed", "yellow");
        register(Blocks.LIME_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.LIME_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.LIME_GLAZED_TERRACOTTA, "terracotta", "glazed", "lime");
        register(Blocks.PINK_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.PINK_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.PINK_GLAZED_TERRACOTTA, "terracotta", "glazed", "pink");
        register(Blocks.GRAY_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.GRAY_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.GRAY_GLAZED_TERRACOTTA, "terracotta", "glazed", "gray");
        register(Blocks.SILVER_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.SILVER_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.SILVER_GLAZED_TERRACOTTA, "terracotta", "glazed", "light", "gray");
        register(Blocks.CYAN_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.CYAN_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.CYAN_GLAZED_TERRACOTTA, "terracotta", "glazed", "cyan");
        register(Blocks.PURPLE_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.PURPLE_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.PURPLE_GLAZED_TERRACOTTA, "terracotta", "glazed", "purple");
        register(Blocks.BLUE_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.BLUE_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.BLUE_GLAZED_TERRACOTTA, "terracotta", "glazed", "blue");
        register(Blocks.BROWN_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.BROWN_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.BROWN_GLAZED_TERRACOTTA, "terracotta", "glazed", "brown");
        register(Blocks.GREEN_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.GREEN_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.GREEN_GLAZED_TERRACOTTA, "terracotta", "glazed", "green");
        register(Blocks.RED_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.RED_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.RED_GLAZED_TERRACOTTA, "terracotta", "glazed", "red");
        register(Blocks.BLACK_GLAZED_TERRACOTTA, "terracotta");
        register(Blocks.BLACK_GLAZED_TERRACOTTA, "terracotta", "glazed");
        register(Blocks.BLACK_GLAZED_TERRACOTTA, "terracotta", "glazed", "black");
        register(Items.ACACIA_BOAT, "boat");
        register(Items.ACACIA_BOAT, "boat", "acacia");
        register(Items.BIRCH_BOAT, "boat");
        register(Items.BIRCH_BOAT, "boat", "birch");
        register(Items.BOAT, "boat");
        register(Items.BOAT, "boat", "oak");
        register(Items.DARK_OAK_BOAT, "boat");
        register(Items.DARK_OAK_BOAT, "boat", "hickory");
        register(Items.JUNGLE_BOAT, "boat");
        register(Items.JUNGLE_BOAT, "boat", "kapok");
        register(Items.SPRUCE_BOAT, "boat");
        register(Items.SPRUCE_BOAT, "boat", "spruce");
    }

    private static Predicate<ItemStack> createPredicateStack(String... names)
    {
        return input -> {
            if (input.isEmpty()) return false;
            int[] ids = OreDictionary.getOreIDs(input);
            for (String name : names)
                if (ArrayUtils.contains(ids, OreDictionary.getOreID(name)))
                    return true;
            return false;
        };
    }

    private static void register(Thing thing, Object... parts)
    {
        if (done) throw new IllegalStateException("Cannot use the helper to register after postInit has past.");
        MAP.put(thing, toString(parts));
    }

    @SuppressWarnings("ConstantConditions")
    private static void registerRockType(Thing thing, Rock.Type type, Rock rock, Object... prefixParts)
    {
        switch (type)
        {
            case RAW:
                MAP.put(thing, toString(prefixParts, "stone"));
                MAP.put(thing, toString(prefixParts, "stone", "raw"));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRegistryName().getPath()));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRegistryName().getPath(), "raw"));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRockCategory().getRegistryName().getPath()));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRockCategory().getRegistryName().getPath(), "raw"));
                if (rock.getIsFluxstone())
                {
                    MAP.put(thing, toString(prefixParts, "stone", "flux"));
                    MAP.put(thing, toString(prefixParts, "stone", "flux", "raw"));
                }
                break;
            case SMOOTH:
                MAP.put(thing, toString(prefixParts, "stone"));
                MAP.put(thing, toString(prefixParts, "stone", "polished"));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRegistryName().getPath()));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRegistryName().getPath(), "polished"));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRockCategory().getRegistryName().getPath()));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRockCategory().getRegistryName().getPath(), "polished"));
                if (rock.getIsFluxstone())
                {
                    MAP.put(thing, toString(prefixParts, "stone", "flux"));
                    MAP.put(thing, toString(prefixParts, "stone", "flux", "polished"));
                }
                break;
            case COBBLE:
                MAP.put(thing, toString(prefixParts, "cobblestone"));
                MAP.put(thing, toString(prefixParts, "cobblestone", rock.getRegistryName().getPath()));
                MAP.put(thing, toString(prefixParts, "cobblestone", rock.getRockCategory().getRegistryName().getPath()));
                if (rock.getIsFluxstone())
                {
                    MAP.put(thing, toString(prefixParts, "stone", "flux"));
                    MAP.put(thing, toString(prefixParts, "cobblestone", "flux"));
                }
                break;
            case BRICKS:
                MAP.put(thing, toString(prefixParts, "stone", "brick"));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRegistryName().getPath(), "brick"));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRockCategory().getRegistryName().getPath(), "brick"));
                if (rock.getIsFluxstone())
                {
                    MAP.put(thing, toString(prefixParts, "stone", "flux"));
                    MAP.put(thing, toString(prefixParts, "stone", "flux", "brick"));
                }
                break;
            case CLAY_GRASS:
                MAP.put(thing, toString(prefixParts, "clay"));
                MAP.put(thing, toString(prefixParts, "clay", rock.getRegistryName().getPath()));
                MAP.put(thing, toString(prefixParts, "clay", rock.getRockCategory().getRegistryName().getPath()));
                MAP.put(thing, toString(prefixParts, "clay", "grass"));
                MAP.put(thing, toString(prefixParts, "clay", rock.getRegistryName().getPath(), "grass"));
                MAP.put(thing, toString(prefixParts, "clay", rock.getRockCategory().getRegistryName().getPath(), "grass"));
                break;
            case DRY_GRASS:
                MAP.put(thing, toString(prefixParts, type, "dry"));
                MAP.put(thing, toString(prefixParts, type, rock.getRegistryName().getPath(), "dry"));
                MAP.put(thing, toString(prefixParts, type, rock.getRockCategory().getRegistryName().getPath(), "dry"));
                break;
            case SAND:
            case GRAVEL:
            case DIRT:
            case GRASS:
            case CLAY:
            default:
                MAP.put(thing, toString(prefixParts, type));
                MAP.put(thing, toString(prefixParts, type, rock.getRegistryName().getPath()));
                MAP.put(thing, toString(prefixParts, type, rock.getRockCategory().getRegistryName().getPath()));
        }
    }

    private static class Thing
    {
        private final Block block;
        private final Item item;
        private final int meta;

        private Thing(Block thing)
        {
            block = thing;
            item = null;
            meta = WILDCARD_VALUE;
        }

        private Thing(Item thing)
        {
            this(thing, WILDCARD_VALUE);
        }

        private Thing(Item thing, int meta)
        {
            item = thing;
            this.meta = meta;
            block = null;
        }

        private ItemStack toItemStack()
        {
            //noinspection ConstantConditions
            return (block == null) ? new ItemStack(item, 1, meta) : new ItemStack(block, 1, meta);
        }
    }
}
