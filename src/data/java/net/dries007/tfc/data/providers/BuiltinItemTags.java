package net.dries007.tfc.data.providers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Metal;

import static net.dries007.tfc.common.TFCTags.Items.*;
import static net.neoforged.neoforge.common.Tags.Items.*;

public class BuiltinItemTags extends TagsProvider<Item> implements Accessors
{
    private final ExistingFileHelper.IResourceType resourceType;
    private final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags;
    private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy = new HashMap<>();

    public BuiltinItemTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags)
    {
        super(event.getGenerator().getPackOutput(), Registries.ITEM, lookup, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
        this.blockTags = blockTags;
        this.resourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", Registries.tagsDirPath(registryKey));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        // ===== Common Tags ===== //

        tag(PLAYER_WORKSTATIONS_CRAFTING_TABLES).add(TFCBlocks.WOODS, Wood.BlockType.WORKBENCH);
        tag(STORAGE_BLOCKS_WHEAT).remove(Items.HAY_BLOCK);

        tag(SEEDS).add(TFCItems.CROP_SEEDS);
        tag(BOOKS).add(
            Items.BOOK,
            Items.ENCHANTED_BOOK,
            Items.WRITABLE_BOOK,
            Items.WRITTEN_BOOK,
            Items.KNOWLEDGE_BOOK);

        // ===== TFC Tags ===== //

        for (Metal metal : Metal.values())
        {
            metalTag(metal, Metal.ItemType.INGOT);
            tag(PILEABLE_INGOTS).addTag(commonTagOf(metal, Metal.ItemType.INGOT));
            if (metal.defaultParts())
            {
                metalTag(metal, Metal.ItemType.DOUBLE_INGOT);
                metalTag(metal, Metal.ItemType.SHEET);
                metalTag(metal, Metal.ItemType.DOUBLE_SHEET);
                metalTag(metal, Metal.ItemType.ROD);
                tag(PILEABLE_DOUBLE_INGOTS).addTag(commonTagOf(metal, Metal.ItemType.DOUBLE_INGOT));
                tag(PILEABLE_SHEETS).addTag(commonTagOf(metal, Metal.ItemType.SHEET));
                copy(storageBlockTagOf(Registries.BLOCK, metal), storageBlockTagOf(Registries.ITEM, metal));
            }
        }

        tag(DOUBLE_SHEETS_ANY_BRONZE).addTags(
            commonTagOf(Metal.BRONZE, Metal.ItemType.DOUBLE_SHEET),
            commonTagOf(Metal.BISMUTH_BRONZE, Metal.ItemType.DOUBLE_SHEET),
            commonTagOf(Metal.BLACK_BRONZE, Metal.ItemType.DOUBLE_SHEET));

        tag(FRUITS).add(Food.BLACKBERRY, Food.BLUEBERRY, Food.BUNCHBERRY, Food.CLOUDBERRY, Food.CRANBERRY, Food.ELDERBERRY, Food.GOOSEBERRY, Food.RASPBERRY, Food.SNOWBERRY, Food.STRAWBERRY, Food.WINTERGREEN_BERRY, Food.BANANA, Food.CHERRY, Food.GREEN_APPLE, Food.LEMON, Food.OLIVE, Food.ORANGE, Food.PEACH, Food.PLUM, Food.RED_APPLE, Food.PUMPKIN_CHUNKS);
        tag(VEGETABLES).add(Food.BEET, Food.CABBAGE, Food.CARROT, Food.GARLIC, Food.GREEN_BEAN, Food.GREEN_BELL_PEPPER, Food.ONION, Food.POTATO, Food.BAKED_POTATO, Food.RED_BELL_PEPPER, Food.SOYBEAN, Food.SUGARCANE, Food.SQUASH, Food.TOMATO, Food.YELLOW_BELL_PEPPER);
        tag(RAW_MEATS).add(Food.BEEF, Food.PORK, Food.CHICKEN, Food.QUAIL, Food.MUTTON, Food.BEAR, Food.HORSE_MEAT, Food.PHEASANT, Food.GROUSE, Food.TURKEY, Food.PEAFOWL, Food.VENISON, Food.WOLF, Food.RABBIT, Food.FOX, Food.HYENA, Food.DUCK, Food.CHEVON, Food.GRAN_FELINE, Food.TURTLE, Food.CAMELIDAE, Food.FROG_LEGS);
        tag(COOKED_MEATS).add(Food.COOKED_BEEF, Food.COOKED_PORK, Food.COOKED_CHICKEN, Food.COOKED_QUAIL, Food.COOKED_MUTTON, Food.COOKED_BEAR, Food.COOKED_HORSE_MEAT, Food.COOKED_PHEASANT, Food.COOKED_TURKEY, Food.COOKED_PEAFOWL, Food.COOKED_GROUSE, Food.COOKED_VENISON, Food.COOKED_WOLF, Food.COOKED_RABBIT, Food.COOKED_FOX, Food.COOKED_HYENA, Food.COOKED_DUCK, Food.COOKED_CHEVON, Food.COOKED_CAMELIDAE, Food.COOKED_FROG_LEGS, Food.COOKED_GRAN_FELINE);
        tag(MEATS).addTag(RAW_MEATS).addTag(COOKED_MEATS);
        tag(RAW_FISH).add(Food.COD, Food.TROPICAL_FISH, Food.CALAMARI, Food.SHELLFISH, Food.BLUEGILL, Food.CRAPPIE, Food.LAKE_TROUT, Food.LARGEMOUTH_BASS, Food.RAINBOW_TROUT, Food.SALMON, Food.SMALLMOUTH_BASS);
        tag(COOKED_FISH).add(Food.COOKED_TURTLE, Food.COOKED_COD, Food.COOKED_TROPICAL_FISH, Food.COOKED_CALAMARI, Food.COOKED_SHELLFISH, Food.COOKED_BLUEGILL, Food.COOKED_CRAPPIE, Food.COOKED_LAKE_TROUT, Food.COOKED_LARGEMOUTH_BASS, Food.COOKED_RAINBOW_TROUT, Food.COOKED_SALMON, Food.COOKED_SMALLMOUTH_BASS);
        tag(FISH).addTags(RAW_FISH, COOKED_FISH);
        tag(FLOUR).add(Food.BARLEY_FLOUR, Food.MAIZE_FLOUR, Food.OAT_FLOUR, Food.RYE_FLOUR, Food.RICE_FLOUR, Food.WHEAT_FLOUR);
        tag(DOUGH).add(Food.BARLEY_DOUGH, Food.MAIZE_DOUGH, Food.OAT_DOUGH, Food.RYE_DOUGH, Food.RICE_DOUGH, Food.WHEAT_DOUGH);
        tag(BREAD)
            .add(Food.BARLEY_BREAD, Food.MAIZE_BREAD, Food.OAT_BREAD, Food.RYE_BREAD, Food.RICE_BREAD, Food.WHEAT_BREAD)
            .add(Items.BREAD);
        tag(DAIRY).add(Food.CHEESE);
        tag(SALADS).add(TFCItems.SALADS);
        tag(SOUPS).add(TFCItems.SOUPS);
        tag(PRESERVES).add(TFCItems.UNSEALED_FRUIT_PRESERVES);
        tag(SEALED_PRESERVES).add(TFCItems.FRUIT_PRESERVES);
        tag(SWEETENERS).add(Items.SUGAR);
        tag(BOWLS).add(Items.BOWL, TFCBlocks.CERAMIC_BOWL);
        tag(SALAD_BOWLS).addTag(BOWLS);
        tag(SOUP_BOWLS).addTag(BOWLS);
        tag(USABLE_IN_SALAD)
            .addTags(FRUITS, VEGETABLES, COOKED_MEATS);
        tag(USABLE_IN_SOUP)
            .addTags(FRUITS, VEGETABLES, MEATS, COOKED_MEATS)
            .add(Food.COOKED_RICE);
        tag(USABLE_IN_SANDWICH).addTags(VEGETABLES, COOKED_MEATS, DAIRY);
        tag(USABLE_IN_JAM_SANDWICH).addTags(COOKED_MEATS, DAIRY, PRESERVES);
        tag(CAN_BE_SALTED);

        tag(SMALL_FISHING_BAIT)
            .addTag(SEEDS)
            .add(Food.SHELLFISH);
        tag(LARGE_FISHING_BAIT)
            .add(Food.COD, Food.SALMON, Food.TROPICAL_FISH, Food.BLUEGILL);
        tag(HOLDS_SMALL_FISHING_BAIT).add(
            TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.FISHING_ROD),
            TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.FISHING_ROD),
            TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.FISHING_ROD),
            TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.FISHING_ROD));
        tag(HOLDS_LARGE_FISHING_BAIT)
            .addTag(HOLDS_SMALL_FISHING_BAIT)
            .add(
                TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.FISHING_ROD),
                TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.FISHING_ROD),
                TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.FISHING_ROD),
                TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.FISHING_ROD),
                TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.FISHING_ROD));

        tag(PLANTS); // todo

        tagNotWhite(COLORED_WOOL, "wool");
        tagNotWhite(COLORED_CARPETS, "carpet");
        tagNotWhite(COLORED_BEDS, "bed");
        tagNotWhite(COLORED_BANNERS, "banner");
        tagNotWhite(COLORED_TERRACOTTA, "terracotta");
        tagNotWhite(COLORED_GLAZED_TERRACOTTA, "glazed_terracotta");
        tagNotWhite(COLORED_SHULKER_BOXES, "shulker_box");
        tagNotWhite(COLORED_CONCRETE_POWDER, "concrete_powder");
        tag(COLORED_CANDLES).add(TFCBlocks.DYED_CANDLE);
        tag(COLORED_WINDMILL_BLADES)
            .addOnly(TFCItems.WINDMILL_BLADES, c -> c != DyeColor.WHITE);
        tag(COLORED_RAW_ALABASTER).add(TFCBlocks.RAW_ALABASTER);
        tag(COLORED_ALABASTER_BRICKS).add(TFCBlocks.ALABASTER_BRICKS);
        tag(COLORED_POLISHED_ALABASTER).add(TFCBlocks.POLISHED_ALABASTER);
        tag(COLORED_VESSELS).add(TFCItems.UNFIRED_GLAZED_VESSELS);
        tag(COLORED_LARGE_VESSELS).add(TFCItems.UNFIRED_GLAZED_LARGE_VESSELS);

        tag(TOOL_RACKS).add(TFCBlocks.WOODS, Wood.BlockType.TOOL_RACK);
        tag(SCRIBING_TABLES).add(TFCBlocks.WOODS, Wood.BlockType.SCRIBING_TABLE);
        tag(SEWING_TABLES).add(TFCBlocks.WOODS, Wood.BlockType.SEWING_TABLE);
        tag(SLUICES).add(TFCBlocks.WOODS, Wood.BlockType.SLUICE);
        tag(LOOMS).add(TFCBlocks.WOODS, Wood.BlockType.LOOM);

        copy(TFCTags.Blocks.LAMPS, LAMPS);
        tag(BUCKETS).add(
            TFCItems.WOODEN_BUCKET,
            TFCItems.RED_STEEL_BUCKET,
            TFCItems.BLUE_STEEL_BUCKET);
        tag(MINECARTS).add(TFCItems.CHEST_MINECARTS);
        tag(ORE_PIECES)
            .add(TFCItems.ORES)
            .addAll(TFCItems.GRADED_ORES);
        tag(SMALL_ORE_PIECES)
            .add(TFCBlocks.SMALL_ORES);
        tag(WATER_WHEELS).add(TFCBlocks.WOODS, Wood.BlockType.WATER_WHEEL);
        tag(WINDMILL_BLADES)
            .add(TFCItems.WINDMILL_BLADES.get(DyeColor.WHITE))
            .addTag(COLORED_WINDMILL_BLADES);
        tag(AXLES).add(TFCBlocks.WOODS, Wood.BlockType.AXLE);
        tag(LUMBER).add(TFCItems.LUMBER);

        tag(VESSELS).addTags(UNFIRED_VESSELS, FIRED_VESSELS);
        tag(UNFIRED_VESSELS)
            .add(TFCItems.UNFIRED_VESSEL)
            .add(TFCItems.UNFIRED_GLAZED_VESSELS);
        tag(FIRED_VESSELS)
            .add(TFCItems.VESSEL).add(TFCItems.GLAZED_VESSELS);
        tag(LARGE_VESSELS).addTags(UNFIRED_LARGE_VESSELS, FIRED_LARGE_VESSELS);
        tag(UNFIRED_LARGE_VESSELS)
            .add(TFCItems.UNFIRED_LARGE_VESSEL)
            .add(TFCItems.UNFIRED_GLAZED_LARGE_VESSELS);
        tag(FIRED_LARGE_VESSELS)
            .add(TFCBlocks.LARGE_VESSEL)
            .add(TFCBlocks.GLAZED_LARGE_VESSELS);
        tag(MOLDS).addTags(UNFIRED_MOLDS, FIRED_MOLDS);
        tag(UNFIRED_MOLDS)
            .add(TFCItems.UNFIRED_MOLDS)
            .add(TFCItems.UNFIRED_FIRE_INGOT_MOLD)
            .add(TFCItems.UNFIRED_BELL_MOLD);
        tag(FIRED_MOLDS)
            .add(TFCItems.MOLDS)
            .add(TFCItems.FIRE_INGOT_MOLD)
            .add(TFCItems.BELL_MOLD);

        tag(FIREPIT_FUEL)
            .addTags(BOOKS, ItemTags.LEAVES)
            .add(
                TFCBlocks.PEAT,
                TFCBlocks.PEAT_GRASS,
                TFCItems.STICK_BUNDLE,
                Items.PAPER,
                TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.PINECONE),
                TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.DRIFTWOOD));
        tag(FORGE_FUEL).addTag(ItemTags.COALS);
        tag(BLAST_FURNACE_FUEL).add(Items.CHARCOAL);

        tag(TOOLS).addTags(TOOLS_KNIFE, TOOLS_CHISEL, TOOLS_SHEAR, TOOLS_GLASSWORKING, TOOLS_BLOWPIPE);
        tag(TOOLS_SHIELD).add(TFCItems.METAL_ITEMS, Metal.ItemType.SHIELD);
        tag(TOOLS_FISHING_ROD).add(TFCItems.METAL_ITEMS, Metal.ItemType.FISHING_ROD);
        tag(TOOLS_SPEAR)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.JAVELIN)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.JAVELIN);
        tag(TOOLS_KNIFE)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.KNIFE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.KNIFE);
        tag(TOOLS_SHEAR).add(TFCItems.METAL_ITEMS, Metal.ItemType.SHEARS);
        tag(TOOLS_IGNITER).add(TFCItems.FIRESTARTER);
        tag(TOOLS_MACE).add(TFCItems.METAL_ITEMS, Metal.ItemType.MACE);
        tag(TOOLS_CHISEL).add(TFCItems.METAL_ITEMS, Metal.ItemType.CHISEL);
        tag(TOOLS_HAMMER).add(TFCItems.METAL_ITEMS, Metal.ItemType.HAMMER);
        tag(TOOLS_SAW).add(TFCItems.METAL_ITEMS, Metal.ItemType.SAW);
        tag(MINING_TOOL_TOOLS).add(TFCItems.METAL_ITEMS, Metal.ItemType.PICKAXE);
        tag(TOOLS_GLASSWORKING).add(TFCItems.PADDLE, TFCItems.JACKS, TFCItems.GEM_SAW);
        tag(TOOLS_BLOWPIPE).add(TFCItems.BLOWPIPE, TFCItems.CERAMIC_BLOWPIPE);

        tag(GLASS_BATCHES_T2).add(TFCItems.SILICA_GLASS_BATCH, TFCItems.HEMATITIC_GLASS_BATCH);
        tag(GLASS_BATCHES_T3).addTag(GLASS_BATCHES_T2).add(TFCItems.OLIVINE_GLASS_BATCH);
        tag(GLASS_BATCHES).addTag(GLASS_BATCHES_T3).add(TFCItems.VOLCANIC_GLASS_BATCH);
        tag(GLASS_BATCHES_NOT_T1).add(TFCItems.HEMATITIC_GLASS_BATCH, TFCItems.OLIVINE_GLASS_BATCH, TFCItems.VOLCANIC_GLASS_BATCH);
        tag(GLASS_BLOWPIPES).add(TFCItems.BLOWPIPE_WITH_GLASS, TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS);
        tag(GLASS_POWDERS).add(GlassOperation.POWDERS.get().keySet().stream());
        tag(GLASS_BOTTLES).add(
            TFCItems.SILICA_GLASS_BOTTLE,
            TFCItems.HEMATITIC_GLASS_BOTTLE,
            TFCItems.OLIVINE_GLASS_BOTTLE,
            TFCItems.VOLCANIC_GLASS_BOTTLE);
        tag(GLASS_POTASH).add(TFCItems.POWDERS.get(Powder.SODA_ASH), TFCItems.ORE_POWDERS.get(Ore.SALTPETER));

        tag(SILICA_SAND).add(
            TFCBlocks.SAND.get(SandBlockType.WHITE));
        tag(OLIVINE_SAND).add(
            TFCBlocks.SAND.get(SandBlockType.GREEN),
            TFCBlocks.SAND.get(SandBlockType.BROWN));
        tag(HEMATITIC_SAND).add(
            TFCBlocks.SAND.get(SandBlockType.YELLOW),
            TFCBlocks.SAND.get(SandBlockType.RED),
            TFCBlocks.SAND.get(SandBlockType.PINK));
        tag(VOLCANIC_SAND).add(
            TFCBlocks.SAND.get(SandBlockType.BLACK));

        tag(HIGH_QUALITY_CLOTH).add(TFCItems.SILK_CLOTH, TFCItems.WOOL_CLOTH);
        tag(STRINGS).add(TFCItems.WOOL_YARN);
        tag(GEM_POWDERS).addOnly(TFCItems.ORE_POWDERS, Ore::isGem);

        copy(Tags.Blocks.STONES, STONES);
        copy(TFCTags.Blocks.STONES_RAW, STONES_RAW);
        copy(TFCTags.Blocks.STONES_HARDENED, STONES_HARDENED);
        copy(TFCTags.Blocks.STONES_SMOOTH, STONES_SMOOTH);
        copy(TFCTags.Blocks.STONES_SMOOTH_SLABS, STONES_SMOOTH_SLABS);
        copy(TFCTags.Blocks.STONES_PRESSURE_PLATES, STONES_PRESSURE_PLATES);

        pivot(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.LOOSE).forEach((rock, item) -> tag(STONES_OF_CATEGORY.get(rock.category())).add(item));
        pivot(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_LOOSE).forEach((rock, item) -> tag(STONES_OF_CATEGORY.get(rock.category())).add(item));

        copy(BlockTags.DIRT, ItemTags.DIRT);
        copy(TFCTags.Blocks.DIRT, DIRT);
        copy(TFCTags.Blocks.GRASS, GRASS);
        copy(TFCTags.Blocks.MUD, MUD);
        copy(TFCTags.Blocks.MUD_BRICKS, MUD_BRICKS);

        copy(Tags.Blocks.COBBLESTONES_NORMAL, COBBLESTONES_NORMAL);
        copy(Tags.Blocks.COBBLESTONES_MOSSY, COBBLESTONES_MOSSY);
    }

    @Override
    protected ItemTagAppender tag(TagKey<Item> tag)
    {
        return new ItemTagAppender(getOrCreateRawBuilder(tag), modId);
    }

    @Override
    protected TagBuilder getOrCreateRawBuilder(TagKey<Item> tag)
    {
        if (existingFileHelper != null) existingFileHelper.trackGenerated(tag.location(), resourceType);
        return this.builders.computeIfAbsent(tag.location(), key -> new TagBuilder()
        {
            @Override
            public TagBuilder add(TagEntry entry)
            {
                Preconditions.checkArgument(!entry.getId().equals(BuiltInRegistries.BLOCK.getDefaultKey()), "Adding air to block tag");
                return super.add(entry);
            }
        });
    }

    private void metalTag(Metal metal, Metal.ItemType type)
    {
        tag(commonTagOf(metal, type)).add(TFCItems.METAL_ITEMS.get(metal).get(type));
    }

    private void tagNotWhite(TagKey<Item> tag, String itemName)
    {
        tag(tag).add(Arrays.stream(DyeColor.values())
            .filter(c -> c != DyeColor.WHITE)
            .map(c -> itemOf(ResourceLocation.withDefaultNamespace(c.getSerializedName() + "_" + itemName))));
    }

    public void copy(TagKey<Block> blockTag, TagKey<Item> itemTag)
    {
        this.tagsToCopy.put(blockTag, itemTag);
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> createContentsProvider()
    {
        return super.createContentsProvider().thenCombine(blockTags, (lookup, tagLookup) -> {
            tagsToCopy.forEach((blockTag, itemTag) -> {
                final TagBuilder builder = getOrCreateRawBuilder(itemTag);
                tagLookup.apply(blockTag)
                    .orElseThrow(() -> new IllegalStateException("Missing block tag " + itemTag.location()))
                    .build()
                    .forEach(builder::add);
            });
            return lookup;
        });
    }

    @SuppressWarnings("UnusedReturnValue")
    static class ItemTagAppender extends TagAppender<Item> implements Accessors
    {
        ItemTagAppender(TagBuilder builder, String modId)
        {
            super(builder, modId);
        }

        ItemTagAppender add(ItemLike... items)
        {
            for (ItemLike item : items) add(key(item));
            return this;
        }

        ItemTagAppender add(Stream<? extends ItemLike> items)
        {
            items.forEach(item -> add(key(item)));
            return this;
        }

        ItemTagAppender add(Map<?, ? extends ItemLike> items)
        {
            return add(items.values().stream());
        }

        <T> ItemTagAppender addOnly(Map<T, ? extends ItemLike> items, Predicate<T> only)
        {
            return add(items.entrySet().stream().filter(e -> only.test(e.getKey())).map(Map.Entry::getValue));
        }

        ItemTagAppender addAll(Map<?, ? extends Map<?, ? extends ItemLike>> items)
        {
            return add(items.values().stream().flatMap(m -> m.values().stream()));
        }

        <T1, T2, V extends ItemLike> ItemTagAppender add(Map<T1, Map<T2, V>> items, T2 key)
        {
            return add(pivot(items, key));
        }

        ItemTagAppender add(Food... foods)
        {
            for (Food food : foods) add(TFCItems.FOOD.get(food));
            return this;
        }

        @Override
        public ItemTagAppender addTag(TagKey<Item> tag)
        {
            return (ItemTagAppender) super.addTag(tag);
        }

        @Override
        @SafeVarargs
        public final ItemTagAppender addTags(TagKey<Item>... values)
        {
            return (ItemTagAppender) super.addTags(values);
        }

        ItemTagAppender remove(ItemLike... items)
        {
            for (ItemLike item : items) remove(key(item));
            return this;
        }

        private ResourceKey<Item> key(ItemLike item)
        {
            return BuiltInRegistries.ITEM.getResourceKey(item.asItem()).orElseThrow();
        }
    }
}
