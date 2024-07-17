package net.dries007.tfc.data.providers;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.IdHolder;

import static net.dries007.tfc.common.TFCTags.Items.*;

public class BuiltinItemTags extends TagsProvider<Item> implements Accessors
{
    public BuiltinItemTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags)
    {
        super(event.getGenerator().getPackOutput(), Registries.ITEM, lookup, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        for (Metal metal : Metal.values())
        {
            metalTag(metal, Metal.ItemType.INGOT);
            tag(PILEABLE_INGOTS).addTag(tagOf(metal, Metal.ItemType.INGOT));
            if (metal.defaultParts())
            {
                metalTag(metal, Metal.ItemType.DOUBLE_INGOT);
                metalTag(metal, Metal.ItemType.SHEET);
                metalTag(metal, Metal.ItemType.DOUBLE_SHEET);
                metalTag(metal, Metal.ItemType.ROD);
                tag(PILEABLE_DOUBLE_INGOTS).addTag(tagOf(metal, Metal.ItemType.DOUBLE_INGOT));
                tag(PILEABLE_SHEETS).addTag(tagOf(metal, Metal.ItemType.SHEET));
            }
        }

        foodTag(FRUITS, Food.BLACKBERRY, Food.BLUEBERRY, Food.BUNCHBERRY, Food.CLOUDBERRY, Food.CRANBERRY, Food.ELDERBERRY, Food.GOOSEBERRY, Food.RASPBERRY, Food.SNOWBERRY, Food.STRAWBERRY, Food.WINTERGREEN_BERRY, Food.BANANA, Food.CHERRY, Food.GREEN_APPLE, Food.LEMON, Food.OLIVE, Food.ORANGE, Food.PEACH, Food.PLUM, Food.RED_APPLE, Food.PUMPKIN_CHUNKS);
        foodTag(VEGETABLES, Food.BEET, Food.CABBAGE, Food.CARROT, Food.GARLIC, Food.GREEN_BEAN, Food.GREEN_BELL_PEPPER, Food.ONION, Food.POTATO, Food.BAKED_POTATO, Food.RED_BELL_PEPPER, Food.SOYBEAN, Food.SUGARCANE, Food.SQUASH, Food.TOMATO, Food.YELLOW_BELL_PEPPER);
        foodTag(RAW_MEATS, Food.BEEF, Food.PORK, Food.CHICKEN, Food.QUAIL, Food.MUTTON, Food.BEAR, Food.HORSE_MEAT, Food.PHEASANT, Food.GROUSE, Food.TURKEY, Food.PEAFOWL, Food.VENISON, Food.WOLF, Food.RABBIT, Food.FOX, Food.HYENA, Food.DUCK, Food.CHEVON, Food.GRAN_FELINE, Food.TURTLE, Food.CAMELIDAE, Food.FROG_LEGS, Food.COD, Food.TROPICAL_FISH, Food.CALAMARI, Food.SHELLFISH, Food.BLUEGILL, Food.CRAPPIE, Food.LAKE_TROUT, Food.LARGEMOUTH_BASS, Food.RAINBOW_TROUT, Food.SALMON, Food.SMALLMOUTH_BASS);
        foodTag(COOKED_MEATS, Food.COOKED_BEEF, Food.COOKED_PORK, Food.COOKED_CHICKEN, Food.COOKED_QUAIL, Food.COOKED_MUTTON, Food.COOKED_BEAR, Food.COOKED_HORSE_MEAT, Food.COOKED_PHEASANT, Food.COOKED_TURKEY, Food.COOKED_PEAFOWL, Food.COOKED_GROUSE, Food.COOKED_VENISON, Food.COOKED_WOLF, Food.COOKED_RABBIT, Food.COOKED_FOX, Food.COOKED_HYENA, Food.COOKED_DUCK, Food.COOKED_CHEVON, Food.COOKED_CAMELIDAE, Food.COOKED_FROG_LEGS, Food.COOKED_GRAN_FELINE, Food.COOKED_TURTLE, Food.COOKED_COD, Food.COOKED_TROPICAL_FISH, Food.COOKED_CALAMARI, Food.COOKED_SHELLFISH, Food.COOKED_BLUEGILL, Food.COOKED_CRAPPIE, Food.COOKED_LAKE_TROUT, Food.COOKED_LARGEMOUTH_BASS, Food.COOKED_RAINBOW_TROUT, Food.COOKED_SALMON, Food.COOKED_SMALLMOUTH_BASS);
        tag(MEATS).addTag(RAW_MEATS).addTag(COOKED_MEATS);
        tag(SALADS).add(TFCItems.SALADS);
        tag(SOUPS).add(TFCItems.SOUPS);
        tag(PRESERVES).add(TFCItems.UNSEALED_FRUIT_PRESERVES);
        tag(SEALED_PRESERVES).add(TFCItems.FRUIT_PRESERVES);

        tagNotWhite(COLORED_WOOL, "wool");
        tagNotWhite(COLORED_CARPETS, "carpet");
        tagNotWhite(COLORED_BEDS, "bed");
        tagNotWhite(COLORED_BANNERS, "banner");
        tagNotWhite(COLORED_TERRACOTTA, "terracotta");
        tagNotWhite(COLORED_GLAZED_TERRACOTTA, "glazed_terracotta");
        tagNotWhite(COLORED_SHULKER_BOXES, "shulker_box");
        tagNotWhite(COLORED_CONCRETE_POWDER, "concrete_powder");
        tag(COLORED_CANDLES).add(TFCBlocks.DYED_CANDLE);
        tag(COLORED_WINDMILL_BLADES).add(TFCItems.WINDMILL_BLADES.entrySet().stream().filter(e -> e.getKey() != DyeColor.WHITE).map(Map.Entry::getValue));
        tag(COLORED_RAW_ALABASTER).add(TFCBlocks.RAW_ALABASTER);
        tag(COLORED_ALABASTER_BRICKS).add(TFCBlocks.ALABASTER_BRICKS);
        tag(COLORED_POLISHED_ALABASTER).add(TFCBlocks.POLISHED_ALABASTER);
        tag(COLORED_VESSELS).add(TFCItems.UNFIRED_GLAZED_VESSELS);
        tag(COLORED_LARGE_VESSELS).add(TFCItems.UNFIRED_GLAZED_LARGE_VESSELS);

        tag(TOOL_RACKS).add(TFCBlocks.WOODS, Wood.BlockType.TOOL_RACK);
        tag(VESSELS).addTag(UNFIRED_VESSELS).addTag(FIRED_VESSELS);
        tag(UNFIRED_VESSELS).add(TFCItems.UNFIRED_VESSEL).add(TFCItems.UNFIRED_GLAZED_VESSELS);
        tag(FIRED_VESSELS).add(TFCItems.VESSEL).add(TFCItems.GLAZED_VESSELS);
        tag(LARGE_VESSELS).addTag(UNFIRED_LARGE_VESSELS).addTag(FIRED_LARGE_VESSELS);
        tag(UNFIRED_LARGE_VESSELS).add(TFCBlocks.LARGE_VESSEL).add(TFCBlocks.GLAZED_LARGE_VESSELS);
        tag(TOOLS_KNIVES)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.KNIFE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.KNIFE);
        tag(TOOLS_CHISELS).add(TFCItems.METAL_ITEMS, Metal.ItemType.CHISEL);
        tag(TOOLS_SHEARS).add(TFCItems.METAL_ITEMS, Metal.ItemType.SHEARS);
        tag(TOOLS_GLASSWORKING).add(TFCItems.PADDLE, TFCItems.JACKS, TFCItems.GEM_SAW);
        tag(TOOLS_BLOWPIPES).add(TFCItems.BLOWPIPE, TFCItems.CERAMIC_BLOWPIPE);
    }

    @Override
    protected ItemTagAppender tag(TagKey<Item> tag)
    {
        return new ItemTagAppender(getOrCreateRawBuilder(tag), modId);
    }

    private void metalTag(Metal metal, Metal.ItemType type)
    {
        tag(tagOf(metal, type)).add(TFCItems.METAL_ITEMS.get(metal).get(type).get());
    }

    private void foodTag(TagKey<Item> tag, Food... foods)
    {
        tag(tag).addAll(Arrays.stream(foods).map(k -> TFCItems.FOOD.get(k).key()).toList());
    }

    private void tagNotWhite(TagKey<Item> tag, String itemName)
    {
        tag(tag).add(Arrays.stream(DyeColor.values())
            .filter(c -> c != DyeColor.WHITE)
            .map(c -> itemOf(ResourceLocation.withDefaultNamespace(c.getSerializedName() + "_" + itemName)))
            .toArray(Item[]::new));
    }

    static class ItemTagAppender extends TagAppender<Item>
    {
        ItemTagAppender(TagBuilder builder, String modId)
        {
            super(builder, modId);
        }

        ItemTagAppender add(ItemLike... items) { return add(Arrays.stream(items)); }
        ItemTagAppender add(Stream<? extends ItemLike> items) { items.forEach(item -> add(key(item))); return this; }
        ItemTagAppender add(Map<?, ? extends IdHolder<? extends ItemLike>> items) { return add(items.values().stream().map(IdHolder::get)); }
        <T> ItemTagAppender add(Map<?, ? extends Map<T, ? extends IdHolder<? extends ItemLike>>> items, T key) { return add(items.values().stream().map(e -> e.get(key).get())); }

        private ResourceKey<Item> key(ItemLike item)
        {
            return BuiltInRegistries.ITEM.getResourceKey(item.asItem()).orElseThrow();
        }
    }
}
