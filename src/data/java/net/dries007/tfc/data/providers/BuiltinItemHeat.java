/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.HeatDefinition;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.FluidHeat;


public class BuiltinItemHeat extends DataManagerProvider<HeatDefinition> implements Accessors
{
    public final List<MeltingRecipe> meltingRecipes = new ArrayList<>();
    private final CompletableFuture<?> before;

    public BuiltinItemHeat(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<?> before)
    {
        super(HeatCapability.MANAGER, output, lookup);
        this.before = before;
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> beforeRun()
    {
        return before.thenCompose(v -> super.beforeRun());
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        TFCBlocks.METALS.forEach((metal, blocks) -> {
            if (metal.weatheredParts())
            {
                add(metal, "block", ingredientOf(
                    ingredientOf(metal, Metal.BlockType.BLOCK),
                    Ingredient.of(blocks.get(Metal.BlockType.EXPOSED_BLOCK)),
                    Ingredient.of(blocks.get(Metal.BlockType.WEATHERED_BLOCK)),
                    Ingredient.of(blocks.get(Metal.BlockType.OXIDIZED_BLOCK))
                ), units(Metal.BlockType.BLOCK));
                add(metal, "block_slab", Ingredient.of(
                    blocks.get(Metal.BlockType.BLOCK_SLAB),
                    blocks.get(Metal.BlockType.EXPOSED_BLOCK_SLAB),
                    blocks.get(Metal.BlockType.WEATHERED_BLOCK_SLAB),
                    blocks.get(Metal.BlockType.OXIDIZED_BLOCK_SLAB)
                ), units(Metal.BlockType.BLOCK_SLAB));
                add(metal, "block_stairs", Ingredient.of(
                    blocks.get(Metal.BlockType.BLOCK_STAIRS),
                    blocks.get(Metal.BlockType.EXPOSED_BLOCK_STAIRS),
                    blocks.get(Metal.BlockType.WEATHERED_BLOCK_STAIRS),
                    blocks.get(Metal.BlockType.OXIDIZED_BLOCK_STAIRS)
                ), units(Metal.BlockType.BLOCK_STAIRS));
            }
            else
            {
                add(metal, Metal.BlockType.BLOCK);
                add(metal, Metal.BlockType.BLOCK_SLAB);
                add(metal, Metal.BlockType.BLOCK_STAIRS);
            }

            add(metal, Metal.BlockType.ANVIL);
            add(metal, Metal.BlockType.BARS);
            add(metal, Metal.BlockType.CHAIN);
            add(metal, Metal.BlockType.LAMP);
            add(metal, Metal.BlockType.TRAPDOOR);
        });
        TFCItems.METAL_ITEMS.forEach((metal, items) -> {
            add(metal, Metal.ItemType.INGOT);
            add(metal, Metal.ItemType.DOUBLE_INGOT);
            add(metal, Metal.ItemType.SHEET);
            add(metal, Metal.ItemType.DOUBLE_SHEET);
            add(metal, Metal.ItemType.ROD);

            for (int amount : new int[] {50, 100, 200, 400, 600, 800, 1200})
            {
                final ItemLike[] parts = Arrays.stream(Metal.ItemType.values())
                    .filter(type -> !type.isCommonTagPart() && units(type) == amount)
                    .map(items::get)
                    .filter(Objects::nonNull)
                    .toArray(ItemLike[]::new);
                if (parts.length > 0) add(metal, "parts_" + amount, Ingredient.of(parts), amount);
            }
        });

        TFCItems.GRADED_ORES.forEach((ore, blocks) ->
            add(ore.name(), Ingredient.of(
                TFCBlocks.SMALL_ORES.get(ore),
                blocks.get(Ore.Grade.POOR),
                blocks.get(Ore.Grade.NORMAL),
                blocks.get(Ore.Grade.RICH)
            ), ore.metal(), 40)); // Average at 40 mB / ore piece - consolidating does reduce net heat capacity, but not overly so, and less so for higher richness ores.

        addAndMeltIron(TFCItems.RAW_IRON_BLOOM, 100);
        addAndMeltIron(TFCItems.REFINED_IRON_BLOOM, 100);
        addAndMeltIron(TFCItems.WROUGHT_IRON_GRILL, 400);
        addAndMeltIron(Items.IRON_DOOR, 200);

        addAndMelt(Items.BELL, Metal.GOLD, 100);
        addAndMelt(TFCBlocks.BRONZE_BELL, Metal.BRONZE, 100);
        addAndMelt(TFCBlocks.BRASS_BELL, Metal.BRASS, 100);
        addAndMelt(TFCItems.JACKS, Metal.BRASS, 100);
        addAndMelt(TFCItems.GEM_SAW, Metal.BRASS, 50);
        addAndMelt(TFCItems.JAR_LID, Metal.TIN, 12);

        add(TFCTags.Items.GLASS_BLOWPIPES, 0.7f);
        add(Tags.Items.RODS_WOODEN, 2.5f); // Includes twigs
        add(TFCItems.STICK_BUNCH, 20f); // 9x sticks
        add(TFCItems.UNFIRED_BRICK, 0.4f);
        add(TFCItems.UNFIRED_FIRE_BRICK, 1.2f);
        add(TFCItems.UNFIRED_FLOWER_POT, 0.6f);
        add(TFCItems.UNFIRED_JUG, 0.8f);
        add(TFCItems.UNFIRED_PAN, 0.6f);
        add(TFCItems.UNFIRED_BOWL, 0.4f);
        add(TFCItems.UNFIRED_POT, 0.8f);
        add(TFCItems.UNFIRED_SPINDLE_HEAD, 0.8f);
        add(TFCItems.UNFIRED_CRUCIBLE, 2.5f);
        add(TFCItems.UNFIRED_BLOWPIPE, 0.6f);
        add(TFCTags.Items.UNFIRED_VESSELS, 1.0f);
        add(TFCTags.Items.UNFIRED_LARGE_VESSELS, 1.5f);
        add(TFCTags.Items.UNFIRED_MOLDS, 1.5f);
        add(Items.CLAY, 0.5f);
        add(TFCItems.KAOLIN_CLAY, 2.0f);
        add(ingredientOf(
            Ingredient.of(Items.TERRACOTTA),
            Ingredient.of(Items.WHITE_TERRACOTTA),
            Ingredient.of(TFCTags.Items.COLORED_TERRACOTTA)
        ), 0.5f);
        add(TFCTags.Items.DOUGH, 1.0f);
        add(TFCTags.Items.BREAD, 1.0f);
        add(TFCTags.Items.MEATS, 1.0f);
        add(TFCTags.Items.FISH, 1.0f);
        add(Ingredient.of(
            TFCItems.FOOD.get(Food.FRESH_SEAWEED),
            TFCItems.FOOD.get(Food.DRIED_SEAWEED),
            TFCItems.FOOD.get(Food.DRIED_KELP),
            TFCBlocks.PLANTS.get(Plant.GIANT_KELP_FLOWER)
        ), 1.0f);
        add(TFCItems.FOOD.get(Food.POTATO), 1.0f);
        add(Items.EGG, 1.0f);
        add(TFCItems.POWDERS.get(Powder.FLUX), 0.7f);
    }

    private void addAndMeltIron(ItemLike item, int units)
    {
        meltingRecipes.add(new MeltingRecipe(item, Metal.CAST_IRON, units));
        add(nameOf(item), Ingredient.of(item), Metal.WROUGHT_IRON, units);
    }

    private void addAndMelt(ItemLike item, Metal metal, int units)
    {
        meltingRecipes.add(new MeltingRecipe(item, metal, units));
        add(nameOf(item), Ingredient.of(item), metal, units);
    }

    private void add(ItemLike item, float heatCapacity)
    {
        add(Ingredient.of(item), heatCapacity);
    }

    private void add(TagKey<Item> item, float heatCapacity)
    {
        add(Ingredient.of(item), heatCapacity);
    }

    private void add(Ingredient item, float heatCapacity)
    {
        add(nameOf(item), new HeatDefinition(item, heatCapacity, 0f, 0f));
    }

    private void add(Metal metal, Metal.ItemType type)
    {
        if (type.has(metal)) add(metal.getSerializedName() + "/" + type.name().toLowerCase(Locale.ROOT), ingredientOf(metal, type), metal, units(type));
    }

    private void add(Metal metal, Metal.BlockType type)
    {
        if (type.has(metal)) add(metal.getSerializedName() + "/" + type.name().toLowerCase(Locale.ROOT), ingredientOf(metal, type), metal, units(type));
    }

    private void add(Metal metal, String typeName, Ingredient ingredient, int units)
    {
        add(metal.getSerializedName() + "/" + typeName.toLowerCase(Locale.ROOT), ingredient, metal, units);
    }

    private void add(String name, Ingredient ingredient, Metal metal, int units)
    {
        final FluidHeat fluidHeat = FluidHeat.MANAGER.getOrThrow(Helpers.identifier(metal.getSerializedName()));
        add(name, new HeatDefinition(
            ingredient,
            (fluidHeat.specificHeatCapacity() / BuiltinFluidHeat.HEAT_CAPACITY) * (units / 100f),
            fluidHeat.meltTemperature() * 0.6f,
            fluidHeat.meltTemperature() * 0.8f));
    }

    record MeltingRecipe(ItemLike item, Metal metal, int units) {}
}
