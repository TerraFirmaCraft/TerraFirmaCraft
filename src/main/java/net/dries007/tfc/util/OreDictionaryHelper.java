/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import javax.annotation.Nonnull;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.damage.DamageType;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.Powder;
import net.dries007.tfc.objects.blocks.BlockDecorativeStone;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemPowder;

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

    public static void registerRockType(Block thing, Rock.Type type, Object... prefixParts)
    {
        registerRockType(new Thing(thing), type, prefixParts);
    }

    public static void registerDamageType(Item thing, DamageType type)
    {
        register(thing, "damage", "type", type.name().toLowerCase());
    }

    public static void init()
    {
        done = true;
        MAP.forEach((t, s) -> OreDictionary.registerOre(s, t.toItemStack()));
        MAP.clear(); // No need to keep this stuff around

        // Vanilla ore dict values
        OreDictionary.registerOre("clay", Items.CLAY_BALL);
        OreDictionary.registerOre("gemCoal", new ItemStack(Items.COAL, 1, 0));
        OreDictionary.registerOre("charcoal", new ItemStack(Items.COAL, 1, 1));
        OreDictionary.registerOre("fireStarter", new ItemStack(Items.FLINT_AND_STEEL, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("fireStarter", new ItemStack(Items.FIRE_CHARGE));
        OreDictionary.registerOre("bowl", Items.BOWL);
        OreDictionary.registerOre("blockClay", Blocks.CLAY);

        //adding oredict to dyeables for dye support. Instead of adding specific recipes color can be changed universally.
        OreDictionary.registerOre("bed", new ItemStack(Items.BED, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("carpet", new ItemStack(Blocks.CARPET, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("powderConcrete", new ItemStack(Blocks.CONCRETE_POWDER, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("terracotta", new ItemStack(Blocks.HARDENED_CLAY, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("terracotta", new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, OreDictionary.WILDCARD_VALUE));

        //oredict support for TFC dyes
        OreDictionary.registerOre("dyePink", new ItemStack(ItemPowder.get(Powder.KAOLINITE)));
        OreDictionary.registerOre("dyeGray", new ItemStack(ItemPowder.get(Powder.GRAPHITE)));
        OreDictionary.registerOre("dyeRed", new ItemStack(ItemPowder.get(Powder.HEMATITE)));
        OreDictionary.registerOre("dyeBlue", new ItemStack(ItemPowder.get(Powder.LAPIS_LAZULI)));
        OreDictionary.registerOre("dyeYellow", new ItemStack(ItemPowder.get(Powder.LIMONITE)));
        OreDictionary.registerOre("dyeGreen", new ItemStack(ItemPowder.get(Powder.MALACHITE)));
        OreDictionary.registerOre("dyeBrown", new ItemStack(ItemPowder.get(Powder.FERTILIZER)));
        OreDictionary.registerOre("dyeBlack", new ItemStack(ItemPowder.get(Powder.CHARCOAL)));
        OreDictionary.registerOre("dyeBlack", new ItemStack(ItemPowder.get(Powder.COKE)));

        BlockDecorativeStone.ALABASTER_BRICKS.forEach((dyeColor, blockDecorativeStone) -> OreDictionary.registerOre("alabasterBricks", new ItemStack(blockDecorativeStone)));
        BlockDecorativeStone.ALABASTER_BRICKS.forEach((dyeColor, blockDecorativeStone) -> OreDictionary.registerOre("bricksAlabaster", new ItemStack(blockDecorativeStone)));
        BlockDecorativeStone.ALABASTER_POLISHED.forEach((dyeColor, blockDecorativeStone) -> OreDictionary.registerOre("alabasterPolished", new ItemStack(blockDecorativeStone)));
        BlockDecorativeStone.ALABASTER_RAW.forEach((dyeColor, blockDecorativeStone) -> OreDictionary.registerOre("alabasterRaw", new ItemStack(blockDecorativeStone)));

        OreDictionary.registerOre("alabasterBricks", new ItemStack(BlocksTFC.ALABASTER_BRICKS_PLAIN));
        OreDictionary.registerOre("bricksAlabaster", new ItemStack(BlocksTFC.ALABASTER_BRICKS_PLAIN));
        OreDictionary.registerOre("alabasterRaw", new ItemStack(BlocksTFC.ALABASTER_RAW_PLAIN));
        OreDictionary.registerOre("alabasterPolished", new ItemStack(BlocksTFC.ALABASTER_POLISHED_PLAIN));

        // Register a name without any items
        OreDictionary.getOres("infiniteFire", true);
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

    private static void register(Thing thing, Object... parts)
    {
        if (done) throw new IllegalStateException("Cannot use the helper to register after postInit has past.");
        MAP.put(thing, toString(parts));
    }

    private static void registerRockType(Thing thing, Rock.Type type, Object... prefixParts)
    {
        switch (type)
        {
            case RAW:
                MAP.put(thing, toString(prefixParts, "stone"));
                break;
            case SMOOTH:
                MAP.put(thing, toString(prefixParts, "stone", "polished"));
                break;
            case COBBLE:
                MAP.put(thing, toString(prefixParts, "cobblestone"));
                break;
            case BRICKS:
                MAP.put(thing, toString(prefixParts, "stone", "brick"));
                MAP.put(thing, toString(prefixParts, "brick", "stone"));
                break;
            case DRY_GRASS:
                MAP.put(thing, toString(prefixParts, type, "dry"));
                break;
            case CLAY:
                MAP.put(thing, toString(prefixParts, "block", type, "dirt"));
                break;
            case CLAY_GRASS:
                MAP.put(thing, toString(prefixParts, "block", type));
                break;
            case SAND:
            case GRAVEL:
            case DIRT:
            case GRASS:
            default:
                MAP.put(thing, toString(prefixParts, type));
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
            meta = 0;
        }

        private Thing(Item thing)
        {
            this(thing, -1);
        }

        private Thing(Item thing, int meta)
        {
            block = null;
            item = thing;
            this.meta = meta;
        }

        private ItemStack toItemStack()
        {
            if (block != null)
            {
                return new ItemStack(block, 1, meta);
            }
            else if (item != null)
            {
                int meta = this.meta;
                if (meta == -1 && item.isDamageable())
                {
                    meta = OreDictionary.WILDCARD_VALUE;
                }
                return new ItemStack(item, 1, meta);
            }
            return ItemStack.EMPTY;
        }
    }
}
