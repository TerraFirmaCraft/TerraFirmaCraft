/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.function.Predicate;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

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
        done = true;
        MAP.forEach((t, s) -> OreDictionary.registerOre(s, t.toItemStack()));
        MAP.clear(); // No need to keep this stuff around
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

    public static Predicate<EntityItem> createPredicateItemEntity(String... names)
    {
        return input -> input.isEntityAlive() && createPredicateStack(names).test(input.getItem());
    }

    private static void register(Thing thing, Object... parts)
    {
        if (done) throw new IllegalStateException("Cannot use the helper to register after init has past.");
        MAP.put(thing, toString(parts));
    }

    private static void registerRockType(Thing thing, Rock.Type type, Rock rock, Object... prefixParts)
    {
        switch (type)
        {
            case RAW:
                MAP.put(thing, toString(prefixParts, "stone"));
                MAP.put(thing, toString(prefixParts, "stone", rock));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRockCategory()));
                break;
            case SMOOTH:
                MAP.put(thing, toString(prefixParts, "stone"));
                MAP.put(thing, toString(prefixParts, "stone", "polished"));
                MAP.put(thing, toString(prefixParts, "stone", rock));
                MAP.put(thing, toString(prefixParts, "stone", rock, "polished"));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRockCategory()));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRockCategory(), "polished"));
                break;
            case COBBLE:
                MAP.put(thing, toString(prefixParts, "cobblestone"));
                MAP.put(thing, toString(prefixParts, "cobblestone", rock));
                MAP.put(thing, toString(prefixParts, "cobblestone", rock.getRockCategory()));
                break;
            case BRICKS:
                MAP.put(thing, toString(prefixParts, "stone", "brick"));
                MAP.put(thing, toString(prefixParts, "stone", rock, "brick"));
                MAP.put(thing, toString(prefixParts, "stone", rock.getRockCategory(), "brick"));
                break;
            case CLAY_GRASS:
                MAP.put(thing, toString(prefixParts, "clay"));
                MAP.put(thing, toString(prefixParts, "clay", rock));
                MAP.put(thing, toString(prefixParts, "clay", rock.getRockCategory()));
                MAP.put(thing, toString(prefixParts, "clay", "grass"));
                MAP.put(thing, toString(prefixParts, "clay", rock, "grass"));
                MAP.put(thing, toString(prefixParts, "clay", rock.getRockCategory(), "grass"));
                break;
            case DRY_GRASS:
                MAP.put(thing, toString(prefixParts, type, "dry"));
                MAP.put(thing, toString(prefixParts, type, rock, "dry"));
                MAP.put(thing, toString(prefixParts, type, rock.getRockCategory(), "dry"));
                break;
            case SAND:
            case GRAVEL:
            case DIRT:
            case GRASS:
            case CLAY:
            default:
                MAP.put(thing, toString(prefixParts, type));
                MAP.put(thing, toString(prefixParts, type, rock));
                MAP.put(thing, toString(prefixParts, type, rock.getRockCategory()));
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
