package net.dries007.tfc.data.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    public final List<WithMelting> withMelting = new ArrayList<>();
    private final CompletableFuture<?> before;

    public BuiltinItemHeat(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        this(output, lookup, CompletableFuture.completedFuture(null));
    }

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
        TFCBlocks.METALS.forEach((metal, map) -> map.forEach((type, block) ->
            add(metal, type.name(), ingredientOf(metal, type), units(type))));
        TFCItems.METAL_ITEMS.forEach((metal, map) -> map.forEach((type, item) ->
            add(metal, type.name(), ingredientOf(metal, type), units(type))));

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
        add(CompoundIngredient.of(
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
        withMelting.add(new WithMelting(item, Metal.CAST_IRON, units));
        add(nameOf(item), Ingredient.of(item), Metal.WROUGHT_IRON, units);
    }

    private void addAndMelt(ItemLike item, Metal metal, int units)
    {
        withMelting.add(new WithMelting(item, metal, units));
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

    public record WithMelting(ItemLike item, Metal metal, int units) {}
}
