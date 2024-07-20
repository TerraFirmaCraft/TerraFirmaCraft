package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.component.size.ItemSizeDefinition;
import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.data.Accessors;

public class BuiltinItemSizes extends DataManagerProvider<ItemSizeDefinition> implements Accessors
{
    public BuiltinItemSizes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(ItemSizeManager.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add("logs", ItemTags.LOGS, Size.VERY_LARGE, Weight.MEDIUM);
        add("chests", Tags.Items.CHESTS, Size.LARGE, Weight.LIGHT);
        add("slabs", ItemTags.SLABS, Size.SMALL, Weight.VERY_LIGHT);
        add("stairs", ItemTags.STAIRS, Size.SMALL, Weight.LIGHT);
        add("doors", ItemTags.DOORS, Size.VERY_LARGE, Weight.HEAVY);
        add("trapdoors", ItemTags.TRAPDOORS, Size.LARGE, Weight.HEAVY);
        add("signs", ItemTags.SIGNS, Size.VERY_SMALL, Weight.HEAVY);
        add("hanging_signs", ItemTags.HANGING_SIGNS, Size.VERY_SMALL, Weight.HEAVY);
        add("boats", ItemTags.BOATS, Size.VERY_LARGE, Weight.HEAVY);
        add("ingots", Tags.Items.INGOTS, Size.LARGE, Weight.MEDIUM);
        add("double_ingots", TFCTags.Items.DOUBLE_INGOTS, Size.LARGE, Weight.MEDIUM);
        add("sheets", TFCTags.Items.SHEETS, Size.LARGE, Weight.MEDIUM);
        add("double_sheets", TFCTags.Items.DOUBLE_SHEETS, Size.LARGE, Weight.MEDIUM);
        add("rods", Tags.Items.RODS, Size.NORMAL, Weight.LIGHT);
        add("dyes", Tags.Items.DYES, Size.TINY, Weight.LIGHT);
        add("foods", Tags.Items.FOODS, Size.SMALL, Weight.LIGHT);
        add("dusts", Tags.Items.DUSTS, Size.VERY_SMALL, Weight.VERY_LIGHT);

        add("quern", TFCBlocks.QUERN, Size.VERY_LARGE, Weight.MEDIUM);
        add("tool_racks", TFCTags.Items.TOOL_RACKS, Size.LARGE, Weight.VERY_HEAVY);
        add("scribing_tables", CompoundIngredient.of(
            Ingredient.of(TFCTags.Items.SCRIBING_TABLES),
            Ingredient.of(TFCTags.Items.SEWING_TABLES),
            Ingredient.of(Items.LOOM)
        ), Size.LARGE, Weight.LIGHT);
        add("vessels", TFCTags.Items.VESSELS, Size.NORMAL, Weight.HEAVY);
        add("large_vessels", TFCTags.Items.LARGE_VESSELS, Size.HUGE, Weight.HEAVY);
        add("molds", TFCTags.Items.MOLDS, Size.NORMAL, Weight.MEDIUM);
        add("powders", TFCTags.Items.POWDERS, Size.TINY, Weight.VERY_LIGHT);
        add("stick_bunch", TFCItems.STICK_BUNCH, Size.NORMAL, Weight.LIGHT);
        add("stick_bundle", TFCItems.STICK_BUNDLE, Size.VERY_LARGE, Weight.MEDIUM);
        add("jute", Ingredient.of(TFCItems.JUTE_FIBER, TFCItems.JUTE), Size.SMALL, Weight.VERY_LIGHT);
        add("burlap_cloth", TFCItems.BURLAP_CLOTH, Size.SMALL, Weight.VERY_LIGHT);
        add("straw", TFCItems.STRAW, Size.SMALL, Weight.VERY_LIGHT);
        add("wool", TFCItems.WOOL, Size.SMALL, Weight.LIGHT);
        add("wool_cloth", TFCItems.WOOL_CLOTH, Size.SMALL, Weight.LIGHT);
        add("alabaster_brick", TFCItems.ALABASTER_BRICK, Size.SMALL, Weight.LIGHT);
        add("glue", TFCItems.GLUE, Size.TINY, Weight.LIGHT);
        add("brass_mechanisms", TFCItems.BRASS_MECHANISMS, Size.NORMAL, Weight.LIGHT);
        add("grill", TFCBlocks.GRILL, Size.LARGE, Weight.HEAVY);
        add("soups", TFCTags.Items.SOUPS, Size.VERY_SMALL, Weight.MEDIUM);
        add("salads", TFCTags.Items.SALADS, Size.VERY_SMALL, Weight.MEDIUM);
        add("bloomery", TFCBlocks.BLOOMERY, Size.LARGE, Weight.VERY_HEAVY);
        add("small_tools", CompoundIngredient.of(
            Ingredient.of(TFCTags.Items.TOOLS_CHISEL),
            Ingredient.of(TFCTags.Items.TOOLS_KNIFE),
            Ingredient.of(Tags.Items.TOOLS_SHEAR),
            Ingredient.of(TFCTags.Items.TOOLS_GLASSWORKING),
            Ingredient.of(TFCTags.Items.TOOLS_BLOWPIPE)
        ), Size.LARGE, Weight.MEDIUM);
        add("tools", CompoundIngredient.of(
            Ingredient.of(Tags.Items.TOOLS_FISHING_ROD),
            Ingredient.of(Tags.Items.MINING_TOOL_TOOLS),
            Ingredient.of(Tags.Items.MELEE_WEAPON_TOOLS),
            Ingredient.of(Tags.Items.RANGED_WEAPON_TOOLS),
            Ingredient.of(Tags.Items.TOOLS_SHIELD)
        ), Size.VERY_LARGE, Weight.VERY_HEAVY);
        add("plants", TFCTags.Items.PLANTS, Size.TINY, Weight.VERY_LIGHT);
        add("sluices", TFCTags.Items.SLUICES, Size.VERY_LARGE, Weight.VERY_HEAVY);
        add("lamps", TFCTags.Items.LAMPS, Size.NORMAL, Weight.MEDIUM);
        add("buckets", Tags.Items.BUCKETS, Size.LARGE, Weight.MEDIUM);
        add("anvils", TFCTags.Items.ANVILS, Size.HUGE, Weight.VERY_HEAVY);
        add("minecarts", TFCTags.Items.MINECARTS, Size.VERY_LARGE, Weight.VERY_HEAVY);
        add("looms", TFCTags.Items.LOOMS, Size.LARGE, Weight.VERY_HEAVY);
        add("tuyeres", TFCTags.Items.TUYERES, Size.LARGE, Weight.HEAVY);
        add("ores", TFCTags.Items.ORE_PIECES, Size.SMALL, Weight.MEDIUM);
        add("small_ores", TFCTags.Items.SMALL_ORE_PIECES, Size.SMALL, Weight.LIGHT);
        add("jars", TFCTags.Items.JARS, Size.NORMAL, Weight.VERY_HEAVY);
        add("empty_jars", Ingredient.of(TFCItems.EMPTY_JAR, TFCItems.EMPTY_JAR_WITH_LID), Size.NORMAL, Weight.LIGHT);
        add("glass_bottles", TFCTags.Items.GLASS_BOTTLES, Size.NORMAL, Weight.MEDIUM);
        add("windmill_blades", TFCTags.Items.WINDMILL_BLADES, Size.VERY_LARGE, Weight.VERY_HEAVY);
        add("water_wheels", TFCTags.Items.WATER_WHEELS, Size.VERY_LARGE, Weight.VERY_HEAVY);
    }

    private void add(String name, TagKey<Item> item, Size size, Weight weight)
    {
        add(name, new ItemSizeDefinition(Ingredient.of(item), size, weight));
    }

    private void add(String name, ItemLike item, Size size, Weight weight)
    {
        add(name, new ItemSizeDefinition(Ingredient.of(item), size, weight));
    }

    private void add(String name, Ingredient item, Size size, Weight weight)
    {
        add(name, new ItemSizeDefinition(item, size, weight));
    }
}
