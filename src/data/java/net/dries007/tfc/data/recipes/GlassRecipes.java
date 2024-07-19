package net.dries007.tfc.data.recipes;

import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.GlassworkingRecipe;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.component.glass.GlassOperation.*;
import static net.minecraft.world.item.crafting.Ingredient.*;

public interface GlassRecipes extends Recipes
{
    default void glassRecipes()
    {
        final List<GlassOperation> bottle = List.of(BLOW, PINCH, SAW);

        add("", of(TFCItems.SILICA_GLASS_BATCH), bottle, TFCItems.SILICA_GLASS_BOTTLE);
        add("", of(TFCItems.HEMATITIC_GLASS_BATCH), bottle, TFCItems.HEMATITIC_GLASS_BOTTLE);
        add("", of(TFCItems.OLIVINE_GLASS_BATCH), bottle, TFCItems.OLIVINE_GLASS_BOTTLE);
        add("", of(TFCItems.VOLCANIC_GLASS_BATCH), bottle, TFCItems.VOLCANIC_GLASS_BOTTLE);

        add("", of(TFCTags.Items.GLASS_BATCHES), List.of(BLOW, PINCH, FLATTEN, BLOW, SAW), TFCItems.LAMP_GLASS);
        add("", of(TFCTags.Items.GLASS_BATCHES_T2), List.of(BLOW, PINCH, ROLL, SAW), TFCItems.EMPTY_JAR);
        add("", of(TFCItems.SILICA_GLASS_BATCH), List.of(BLOW, STRETCH, ROLL, SAW), TFCItems.LENS);

        add("", of(TFCTags.Items.GLASS_BATCHES_NOT_T1), List.of(AMETHYST, BASIN_POUR), Items.TINTED_GLASS);

        // Colored Glass
        add2("", of(TFCItems.SILICA_GLASS_BATCH), List.of(), TFCBlocks.POURED_GLASS, Items.GLASS);
        add2("", of(TFCItems.SILICA_GLASS_BATCH), List.of(GOLD), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.PINK), Items.PINK_STAINED_GLASS);
        add2("", of(TFCItems.SILICA_GLASS_BATCH), List.of(LAPIS_LAZULI), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.LIGHT_BLUE), Items.LIGHT_BLUE_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES_T2), List.of(SODA_ASH), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.WHITE), Items.WHITE_STAINED_GLASS);
        add2("", of(TFCItems.SILICA_GLASS_BATCH), List.of(PYRITE), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.ORANGE), Items.ORANGE_STAINED_GLASS);
        add2("_default", of(TFCItems.HEMATITIC_GLASS_BATCH), List.of(), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.ORANGE), Items.ORANGE_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES_T2), List.of(RUBY), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.MAGENTA), Items.MAGENTA_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES_T2), List.of(SILVER), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.YELLOW), Items.YELLOW_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES_T2), List.of(IRON, SODA_ASH), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.LIME), Items.LIME_STAINED_GLASS);
        add2("_2", of(TFCTags.Items.GLASS_BATCHES_T2), List.of(SODA_ASH, IRON), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.LIME), Items.LIME_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES_T2), List.of(TIN), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.RED), Items.RED_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES_T2), List.of(IRON), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.GREEN), Items.GREEN_STAINED_GLASS);
        add2("_default", of(TFCItems.OLIVINE_GLASS_BATCH), List.of(), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.GREEN), Items.GREEN_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES_T3), List.of(COPPER, SAPPHIRE), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.CYAN), Items.CYAN_STAINED_GLASS);
        add2("_2", of(TFCTags.Items.GLASS_BATCHES_T3), List.of(SAPPHIRE, COPPER), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.CYAN), Items.CYAN_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES), List.of(NICKEL), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.BROWN), Items.BROWN_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES), List.of(IRON, COPPER), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.PURPLE), Items.PURPLE_STAINED_GLASS);
        add2("_2", of(TFCTags.Items.GLASS_BATCHES), List.of(COPPER, IRON), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.PURPLE), Items.PURPLE_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES), List.of(GRAPHITE, SODA_ASH), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.GRAY), Items.GRAY_STAINED_GLASS);
        add2("_2", of(TFCTags.Items.GLASS_BATCHES), List.of(SODA_ASH, GRAPHITE), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.GRAY), Items.GRAY_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES), List.of(SODA_ASH, SODA_ASH, GRAPHITE), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.LIGHT_GRAY), Items.LIGHT_GRAY_STAINED_GLASS);
        add2("_2", of(TFCTags.Items.GLASS_BATCHES), List.of(SODA_ASH, GRAPHITE, SODA_ASH), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.LIGHT_GRAY), Items.LIGHT_GRAY_STAINED_GLASS);
        add2("_3", of(TFCTags.Items.GLASS_BATCHES), List.of(GRAPHITE, SODA_ASH, SODA_ASH), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.LIGHT_GRAY), Items.LIGHT_GRAY_STAINED_GLASS);
        add2("", of(TFCTags.Items.GLASS_BATCHES), List.of(GRAPHITE), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.BLACK), Items.BLACK_STAINED_GLASS);
        add2("", of(TFCItems.SILICA_GLASS_BATCH), List.of(COPPER), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.BLUE), Items.BLUE_STAINED_GLASS);
        add2("_default", of(TFCItems.VOLCANIC_GLASS_BATCH), List.of(), TFCBlocks.COLORED_POURED_GLASS.get(DyeColor.BLUE), Items.BLUE_STAINED_GLASS);
    }

    private void add2(String suffix, Ingredient input, List<GlassOperation> steps, ItemLike poured, ItemLike stained)
    {
        add(suffix, input, Helpers.immutableAdd(steps, TABLE_POUR), poured);
        add(suffix, input, Helpers.immutableAdd(steps, BASIN_POUR), stained);
    }

    private void add(String suffix, Ingredient input, List<GlassOperation> steps, ItemLike output)
    {
        add(nameOf(output) + suffix, new GlassworkingRecipe(steps, input, new ItemStack(output)));
    }
}
