package net.dries007.tfc.data.recipes;

import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.HideItemType;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.HasTraitIngredient;
import net.dries007.tfc.common.recipes.ingredients.HeatIngredient;
import net.dries007.tfc.common.recipes.ingredients.LacksTraitIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.dries007.tfc.common.recipes.outputs.AddHeatModifier;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.DyeLeatherModifier;
import net.dries007.tfc.common.recipes.outputs.EmptyBowlModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.common.recipes.outputs.RemoveTraitModifier;

import static net.minecraft.world.item.crafting.Ingredient.*;

public interface BarrelRecipes extends Recipes
{
    default void barrelRecipes()
    {
        // Sealed Recipes
        for (HideItemType.Size size : HideItemType.Size.values())
        {
            final int amount = 300 + 100 * size.ordinal();

            barrel()
                .input(itemOf(HideItemType.RAW, size)).input(fluidOf(SimpleFluid.LIMEWATER), amount)
                .output(itemOf(HideItemType.SOAKED, size))
                .sealed(hours(8));
            barrel()
                .input(itemOf(HideItemType.SCRAPED, size)).input(SizedFluidIngredient.of(Fluids.WATER, amount))
                .output(TFCItems.HIDES.get(HideItemType.PREPARED).get(size))
                .sealed(hours(8));
            barrel(size.name() + "_leather")
                .input(itemOf(HideItemType.PREPARED, size)).input(fluidOf(SimpleFluid.TANNIN), amount)
                .output(ItemStackProvider.of(Items.LEATHER, 1 + size.ordinal()))
                .sealed(hours(8));
        }

        barrel()
            .input(of(Stream.of(Wood.BlockType.LOG, Wood.BlockType.WOOD)
                .flatMap(type -> TFCBlocks.WOODS.values().stream().map(m -> m.get(type)))
                .map(ItemStack::new)))
            .input(Fluids.WATER, 1000)
            .output(fluidOf(SimpleFluid.TANNIN), 1000)
            .sealed(hours(8));
        barrel()
            .input(TFCItems.JUTE)
            .input(Fluids.WATER, 200)
            .output(TFCItems.JUTE_FIBER)
            .sealed(hours(8));
        barrel()
            .input(notRotten(of(TFCItems.FOOD.get(Food.SUGARCANE))))
            .input(Fluids.WATER, 600)
            .output(Items.SUGAR)
            .sealed(hours(8));
        barrel()
            .input(Items.BONE_MEAL)
            .input(fluidOf(SimpleFluid.LIMEWATER), 600)
            .output(TFCItems.GLUE)
            .sealed(hours(8));
        barrel()
            .input(TFCItems.PAPYRUS_STRIP)
            .input(Fluids.WATER, 200)
            .output(TFCItems.SOAKED_PAPYRUS_STRIP)
            .sealed(hours(8));
        alcohol(of(TFCItems.FOOD.get(Food.BARLEY_FLOUR)), SimpleFluid.BEER);
        alcohol(of(TFCItems.FOOD.get(Food.RED_APPLE), TFCItems.FOOD.get(Food.GREEN_APPLE)), SimpleFluid.CIDER);
        alcohol(of(Items.SUGAR), SimpleFluid.RUM);
        alcohol(of(TFCItems.FOOD.get(Food.RICE_FLOUR)), SimpleFluid.SAKE);
        alcohol(of(TFCItems.FOOD.get(Food.POTATO)), SimpleFluid.VODKA);
        alcohol(of(TFCItems.FOOD.get(Food.WHEAT_FLOUR)), SimpleFluid.WHISKEY);
        alcohol(of(TFCItems.FOOD.get(Food.MAIZE_FLOUR)), SimpleFluid.CORN_WHISKEY);
        alcohol(of(TFCItems.FOOD.get(Food.RYE_FLOUR)), SimpleFluid.RYE_WHISKEY);
        barrel()
            .input(notRotten(of(TFCTags.Items.FRUITS)))
            .input(TFCTags.Fluids.ALCOHOLS, 250)
            .output(fluidOf(SimpleFluid.VINEGAR), 250)
            .sealed(hours(8));

        final Ingredient foods = CompoundIngredient.of(
            Ingredient.of(TFCTags.Items.FRUITS),
            Ingredient.of(TFCTags.Items.VEGETABLES),
            Ingredient.of(TFCTags.Items.MEATS)
        );
        barrel("brined")
            .input(AndIngredient.of(
                foods,
                NotRottenIngredient.INSTANCE,
                LacksTraitIngredient.of(FoodTraits.BRINED)
            ))
            .input(fluidOf(SimpleFluid.BRINE), 125)
            .output(ItemStackProvider.of(
                CopyInputModifier.INSTANCE,
                AddTraitModifier.of(FoodTraits.BRINED)
            ))
            .sealed(hours(4));
        barrel("pickled")
            .input(AndIngredient.of(
                foods,
                NotRottenIngredient.INSTANCE,
                HasTraitIngredient.of(FoodTraits.BRINED),
                LacksTraitIngredient.of(FoodTraits.BRINED)
            ))
            .input(fluidOf(SimpleFluid.VINEGAR), 125)
            .output(ItemStackProvider.of(
                CopyInputModifier.INSTANCE,
                AddTraitModifier.of(FoodTraits.PICKLED)
            ));
        barrel("preserved_in_vinegar")
            .input(AndIngredient.of(
                foods,
                NotRottenIngredient.INSTANCE,
                HasTraitIngredient.of(FoodTraits.PICKLED)
            ))
            .input(fluidOf(SimpleFluid.VINEGAR), 125)
            .sealed(
                ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FoodTraits.PRESERVED_IN_VINEGAR)),
                ItemStackProvider.of(CopyInputModifier.INSTANCE, RemoveTraitModifier.of(FoodTraits.PRESERVED_IN_VINEGAR))
            );
        barrel()
            .input(Tags.Items.SANDS)
            .input(fluidOf(SimpleFluid.LIMEWATER), 100)
            .output(ItemStackProvider.of(TFCItems.MORTAR, 16))
            .sealed(hours(8));
        barrel()
            .input(fluidOf(SimpleFluid.MILK_VINEGAR), 1)
            .output(fluidOf(SimpleFluid.CURDLED_MILK), 1)
            .sealed(hours(12));
        barrel()
            .input(fluidOf(SimpleFluid.CURDLED_MILK), 625)
            .output(TFCItems.FOOD.get(Food.CHEESE))
            .sealed(hours(24));
        barrel()
            .input(TFCItems.ORES.get(Ore.GYPSUM))
            .input(fluidOf(SimpleFluid.LIMEWATER), 100)
            .output(TFCBlocks.PLAIN_ALABASTER)
            .sealed(hours(1));
        barrel()
            .input(Tags.Items.STRINGS)
            .input(fluidOf(SimpleFluid.TALLOW), 40)
            .output(TFCBlocks.CANDLE)
            .sealed(hours(4));
        removeDye(TFCTags.Items.COLORED_WOOL, Items.WHITE_WOOL);
        removeDye(TFCTags.Items.COLORED_CARPETS, Items.WHITE_CARPET);
        removeDye(TFCTags.Items.COLORED_BEDS, Items.WHITE_BED);
        removeDye(TFCTags.Items.COLORED_BANNERS, Items.WHITE_BANNER);
        removeDye(TFCTags.Items.COLORED_TERRACOTTA, Items.WHITE_TERRACOTTA);
        removeDye(TFCTags.Items.COLORED_GLAZED_TERRACOTTA, Items.WHITE_GLAZED_TERRACOTTA);
        removeDye(TFCTags.Items.COLORED_SHULKER_BOXES, Items.SHULKER_BOX);
        removeDye(TFCTags.Items.COLORED_CONCRETE_POWDER, TFCBlocks.AGGREGATE);
        removeDye(TFCTags.Items.COLORED_CANDLES, TFCBlocks.CANDLE);
        removeDye(TFCTags.Items.COLORED_WINDMILL_BLADES, TFCItems.WINDMILL_BLADES.get(DyeColor.WHITE));
        removeDye(TFCTags.Items.COLORED_RAW_ALABASTER, TFCBlocks.RAW_ALABASTER.get(DyeColor.WHITE));
        removeDye(TFCTags.Items.COLORED_ALABASTER_BRICKS, TFCBlocks.ALABASTER_BRICKS.get(DyeColor.WHITE));
        removeDye(TFCTags.Items.COLORED_POLISHED_ALABASTER, TFCBlocks.POLISHED_ALABASTER.get(DyeColor.WHITE));
        removeDye(TFCTags.Items.COLORED_VESSELS, TFCItems.UNFIRED_VESSEL);
        removeDye(TFCTags.Items.COLORED_LARGE_VESSELS, TFCItems.UNFIRED_LARGE_VESSEL);
        dye(Items.WHITE_WOOL, "wool");
        dye(Items.WHITE_CARPET, "carpet");
        dye(Items.WHITE_BED, "bed");
        dye(Items.WHITE_BANNER, "banner");
        dye(Items.WHITE_TERRACOTTA, "terracotta");
        dye(Items.WHITE_GLAZED_TERRACOTTA, "glazed_terracotta");
        dye(Items.SHULKER_BOX, "shulker_box");
        dye(TFCBlocks.AGGREGATE, "concrete_powder");
        dye(TFCBlocks.CANDLE, TFCBlocks.DYED_CANDLE::get);
        dye(TFCItems.WINDMILL_BLADES.get(DyeColor.WHITE), TFCItems.WINDMILL_BLADES::get);
        dye(TFCBlocks.PLAIN_ALABASTER, TFCBlocks.RAW_ALABASTER::get);
        dye(TFCBlocks.PLAIN_ALABASTER_BRICKS, TFCBlocks.ALABASTER_BRICKS::get);
        dye(TFCBlocks.PLAIN_POLISHED_ALABASTER, TFCBlocks.POLISHED_ALABASTER::get);
        dye(TFCItems.UNFIRED_VESSEL, TFCItems.UNFIRED_GLAZED_VESSELS::get);
        dye(TFCItems.UNFIRED_LARGE_VESSEL, TFCItems.UNFIRED_GLAZED_LARGE_VESSELS::get);

        for (DyeColor color : DyeColor.values())
            barrel(color.name() + "_leather")
                .input(fluidOf(color), 25)
                .input(Ingredient.of(Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items
                    .LEATHER_HELMET, Items.LEATHER_HORSE_ARMOR))
                .output(ItemStackProvider.of(CopyInputModifier.INSTANCE, DyeLeatherModifier.of(color)))
                .sealed(hours(1));

        musicDisc(DyeColor.YELLOW, Items.MUSIC_DISC_13);
        musicDisc(DyeColor.ORANGE, Items.MUSIC_DISC_BLOCKS);
        musicDisc(DyeColor.LIME, Items.MUSIC_DISC_CAT);
        musicDisc(DyeColor.RED, Items.MUSIC_DISC_CHIRP);
        musicDisc(DyeColor.GREEN, Items.MUSIC_DISC_FAR);
        musicDisc(DyeColor.PURPLE, Items.MUSIC_DISC_MALL);
        musicDisc(DyeColor.MAGENTA, Items.MUSIC_DISC_MELLOHI);
        musicDisc(DyeColor.CYAN, Items.MUSIC_DISC_OTHERSIDE);
        musicDisc(DyeColor.BLACK, Items.MUSIC_DISC_STAL);
        musicDisc(DyeColor.WHITE, Items.MUSIC_DISC_STRAD);
        musicDisc(DyeColor.LIGHT_BLUE, Items.MUSIC_DISC_WAIT);
        musicDisc(DyeColor.BLUE, Items.MUSIC_DISC_WARD);

        // Instant Recipes
        barrel()
            .input(TFCItems.POWDERS.get(Powder.SALT))
            .input(Fluids.WATER, 125)
            .output(TFCFluids.SALT_WATER.getSource(), 125)
            .instant();
        barrel()
            .input(Ingredient.of(TFCItems.POWDERS.get(Powder.LIME), TFCItems.POWDERS.get(Powder.FLUX)))
            .input(Fluids.WATER, 500)
            .output(fluidOf(SimpleFluid.LIMEWATER), 500)
            .instant();
        barrel()
            .input(fluidOf(SimpleFluid.OLIVE_OIL_WATER), 250)
            .input(TFCItems.JUTE_NET)
            .output(fluidOf(SimpleFluid.OLIVE_OIL), 50)
            .output(TFCItems.DIRTY_JUTE_NET)
            .instant();
        cooling(Fluids.WATER, -5f);
        cooling(TFCFluids.SALT_WATER.getSource(), -5f);
        cooling(fluidOf(SimpleFluid.OLIVE_OIL), -40f);
        barrel("clean_bowl")
            .input(CompoundIngredient.of(
                Ingredient.of(TFCTags.Items.SOUPS),
                Ingredient.of(TFCTags.Items.SALADS)
            ))
            .input(Fluids.WATER, 100)
            .output(ItemStackProvider.of(EmptyBowlModifier.INSTANCE))
            .instant();
        barrel("clean_jar")
            .input(CompoundIngredient.of(
                Ingredient.of(TFCTags.Items.PRESERVES),
                Ingredient.of(TFCTags.Items.SEALED_PRESERVES)
            ))
            .input(Fluids.WATER, 100)
            .output(TFCItems.EMPTY_JAR)
            .instant();
        barrel("clean_jute_net")
            .input(TFCItems.DIRTY_JUTE_NET)
            .input(Fluids.WATER, 100)
            .output(TFCItems.JUTE_NET);

        // Instant Fluid Mixing
        barrel()
            .output(fluidOf(SimpleFluid.BRINE), 10)
            .input(TFCFluids.SALT_WATER.getSource(), 9)
            .instantOnAdd(fluidOf(SimpleFluid.VINEGAR));
        barrel()
            .output(fluidOf(SimpleFluid.MILK_VINEGAR), 10)
            .input(NeoForgeMod.MILK.get(), 9)
            .instantOnAdd(fluidOf(SimpleFluid.VINEGAR));
    }

    private void alcohol(Ingredient item, SimpleFluid fluid)
    {
        barrel()
            .input(notRotten(item))
            .input(Fluids.WATER, 500)
            .output(fluidOf(fluid), 500)
            .sealed(hours(72));
    }

    private void cooling(Fluid fluid, float amount)
    {
        barrel(BuiltInRegistries.FLUID.getKey(fluid).getPath() + "_cooling")
            .input(HeatIngredient.min(1f))
            .input(fluid, 1)
            .output(ItemStackProvider.of(
                CopyInputModifier.INSTANCE,
                AddHeatModifier.of(amount)
            ))
            .sound(SoundEvents.FIRE_EXTINGUISH)
            .instant();
    }

    private void removeDye(TagKey<Item> input, ItemLike output)
    {
        barrel("bleaching_" + nameOf(output).getPath().replace("white_", "").replace("/white", ""))
            .input(input)
            .input(fluidOf(SimpleFluid.LYE), 25)
            .output(output)
            .sealed(hours(1));
    }

    private void dye(ItemLike input, String baseName)
    {
        dye(input, color -> itemOf(ResourceLocation.withDefaultNamespace(color.getSerializedName() + "_" + baseName)));
    }

    private void dye(ItemLike input, Function<DyeColor, ItemLike> output)
    {
        for (DyeColor color : DyeColor.values())
            if (input.asItem() != output.apply(color).asItem())
                barrel()
                    .input(fluidOf(color), 25)
                    .input(input)
                    .output(output.apply(color))
                    .sealed(hours(1));
    }

    private void musicDisc(DyeColor color, ItemLike output)
    {
        barrel()
            .input(fluidOf(color), 25)
            .input(TFCItems.BLANK_DISC)
            .output(output)
            .sealed(hours(1));
    }

    private TFCItems.ItemId itemOf(HideItemType type, HideItemType.Size size)
    {
        return TFCItems.HIDES.get(type).get(size);
    }

    private SizedIngredient notRotten(Ingredient item)
    {
        return new SizedIngredient(AndIngredient.of(item, NotRottenIngredient.INSTANCE), 1);
    }

    private BarrelRecipe.Builder barrel()
    {
        return new BarrelRecipe.Builder(r -> {
            if (!r.getResultItem().isEmpty()) add("barrel", BuiltInRegistries.ITEM.getKey(r.getResultItem().getItem()).getPath(), r);
            else if (!r.getOutputFluid().isEmpty()) add("barrel", BuiltInRegistries.FLUID.getKey(r.getOutputFluid().getFluid()).getPath(), r);
            else throw new IllegalStateException("Barrel recipe requires a custom name!");
        });
    }

    private BarrelRecipe.Builder barrel(String name)
    {
        return new BarrelRecipe.Builder(r -> add("barrel", name, r));
    }
}
