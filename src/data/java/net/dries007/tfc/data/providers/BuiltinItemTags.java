/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
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
import net.neoforged.neoforge.common.data.internal.NeoForgeItemTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.HideItemType;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

import static net.dries007.tfc.common.TFCTags.Items.*;

public class BuiltinItemTags extends TagsProvider<Item> implements Accessors
{
    private static final Field TAGS_TO_COPY = Helpers.uncheck(() -> {
        final Field field = ItemTagsProvider.class.getDeclaredField("tagsToCopy");
        field.setAccessible(true);
        return field;
    });

    private final ExistingFileHelper.IResourceType resourceType;
    private final Function<HolderLookup.Provider, ItemTagsProvider> vanillaItemTags;
    private final Function<HolderLookup.Provider, ItemTagsProvider> neoItemTags;
    private final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags;
    private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy = new HashMap<>();

    @SuppressWarnings("UnstableApiUsage")
    public BuiltinItemTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags)
    {
        super(event.getGenerator().getPackOutput(), Registries.ITEM, lookup, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
        this.blockTags = blockTags;
        this.resourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", Registries.tagsDirPath(registryKey));
        this.vanillaItemTags = provider -> new VanillaItemTagsProvider(event.getGenerator().getPackOutput(), lookup, blockTags)
        {{
            addTags(provider);
        }};
        this.neoItemTags = provider -> {
            final var tags = new NeoForgeItemTagsProvider(event.getGenerator().getPackOutput(), lookup, blockTags, event.getExistingFileHelper());
            tags.addTags(provider);
            return tags;
        };
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        // ===== Copy BlockTags => ItemTags ===== //

        // Uses the vanilla and neo builders to establish which tags need to be copied,
        // and our tag provider knows not to copy empty tags, so it saves us some effort
        this.tagsToCopy.putAll(Helpers.uncheck(() -> TAGS_TO_COPY.get(vanillaItemTags.apply(provider))));
        this.tagsToCopy.putAll(Helpers.uncheck(() -> TAGS_TO_COPY.get(neoItemTags.apply(provider))));

        // ===== Common Tags ===== //

        tag(Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES).add(TFCBlocks.WOODS, Wood.BlockType.WORKBENCH);
        tag(Tags.Items.STORAGE_BLOCKS_WHEAT).remove(Items.HAY_BLOCK);
        tag(Tags.Items.STRINGS).add(TFCItems.WOOL_YARN);
        tag(Tags.Items.SEEDS).add(TFCItems.CROP_SEEDS);

        // ===== TFC Tags ===== //

        for (Metal metal : Metal.values())
        {
            metalTag(metal, Metal.ItemType.INGOT);
            if (metal.defaultParts())
            {
                metalTag(metal, Metal.ItemType.DOUBLE_INGOT);
                metalTag(metal, Metal.ItemType.SHEET);
                metalTag(metal, Metal.ItemType.DOUBLE_SHEET);
                metalTag(metal, Metal.ItemType.ROD);
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
        tag(GRAINS).add(Food.BARLEY_GRAIN, Food.MAIZE_GRAIN, Food.OAT_GRAIN, Food.RYE_GRAIN, Food.RICE_GRAIN, Food.WHEAT_GRAIN);
        tag(BREAD)
            .add(Food.BARLEY_BREAD, Food.MAIZE_BREAD, Food.OAT_BREAD, Food.RYE_BREAD, Food.RICE_BREAD, Food.WHEAT_BREAD)
            .add(Items.BREAD);
        tag(DAIRY).add(Food.CHEESE);
        tag(SALADS).add(TFCItems.SALADS);
        tag(SOUPS).add(TFCItems.SOUPS);
        tag(PRESERVES).add(TFCItems.UNSEALED_FRUIT_PRESERVES);
        tag(SEALED_PRESERVES).add(TFCItems.FRUIT_PRESERVES);
        tag(JARS)
            .addTags(SEALED_PRESERVES, PRESERVES)
            .add(
                TFCItems.EMPTY_JAR,
                TFCItems.EMPTY_JAR_WITH_LID
            );
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
        tag(PIG_FOOD).addTag(FOODS);
        tag(COW_FOOD).addTag(GRAINS);
        tag(YAK_FOOD).addTag(GRAINS);
        tag(GOAT_FOOD).addTags(GRAINS, FRUITS, VEGETABLES);
        tag(ALPACA_FOOD).addTags(GRAINS, FRUITS);
        tag(SHEEP_FOOD).addTag(GRAINS);
        tag(MUSK_OX_FOOD).addTag(GRAINS);
        tag(CHICKEN_FOOD).addTags(GRAINS, FRUITS, VEGETABLES, Tags.Items.SEEDS, BREAD);
        tag(DUCK_FOOD).addTag(CHICKEN_FOOD);
        tag(QUAIL_FOOD).addTag(CHICKEN_FOOD);
        tag(DONKEY_FOOD).addTag(HORSE_FOOD);
        tag(MULE_FOOD).addTag(HORSE_FOOD);
        tag(HORSE_FOOD).addTags(GRAINS, FRUITS);
        tag(CAT_FOOD).addTags(GRAINS, COOKED_MEATS, DAIRY, COOKED_FISH);
        tag(DOG_FOOD).addTag(MEATS);
        tag(PENGUIN_FOOD).addTags(TURTLE_FOOD, RAW_FISH);
        tag(TURTLE_FOOD).add(TFCItems.FOOD.get(Food.DRIED_KELP), TFCItems.FOOD.get(Food.DRIED_SEAWEED));
        tag(FROG_FOOD).addTags(TURTLE_FOOD, GRAINS);
        tag(RABBIT_FOOD).addTags(GRAINS, VEGETABLES);

        // Greens and Browns intentionally overlap - we check browns first, then greens, to resolve
        tag(COMPOST_GREENS).addTags(COMPOST_GREENS_LOW, COMPOST_GREENS_MEDIUM, COMPOST_GREENS_HIGH);
        tag(COMPOST_GREENS_LOW).addTag(PLANTS);
        tag(COMPOST_GREENS_MEDIUM).addTag(GRAINS);
        tag(COMPOST_GREENS_HIGH).addTags(VEGETABLES, FRUITS);
        tag(COMPOST_BROWNS).addTags(COMPOST_BROWNS_LOW, COMPOST_BROWNS_MEDIUM, COMPOST_BROWNS_HIGH);
        tag(COMPOST_BROWNS_LOW)
            .addTag(ItemTags.LEAVES)
            .add(
                TFCBlocks.PLANTS.get(Plant.HANGING_VINES),
                TFCBlocks.PLANTS.get(Plant.SPANISH_MOSS),
                TFCBlocks.PLANTS.get(Plant.LIANA),
                TFCBlocks.PLANTS.get(Plant.TREE_FERN),
                TFCBlocks.PLANTS.get(Plant.ARUNDO),
                TFCBlocks.PLANTS.get(Plant.DRY_PHRAGMITE),
                TFCBlocks.PLANTS.get(Plant.JUNGLE_VINES),
                Items.HANGING_ROOTS);
        tag(COMPOST_BROWNS_MEDIUM).add(
            TFCItems.POWDERS.get(Powder.WOOD_ASH),
            TFCItems.JUTE);
        tag(COMPOST_BROWNS_HIGH).add(
            Items.PAPER,
            TFCItems.JUTE_FIBER,
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.HUMUS),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.DEAD_GRASS),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.DRIFTWOOD),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.PINECONE));

        tag(SMALL_FISHING_BAIT)
            .addTag(Tags.Items.SEEDS)
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

        // todo
        tag(PLANTS)
            .addOnly(TFCBlocks.PLANTS, Plant::needsItem);
        tag(WILD_CROPS).add(TFCBlocks.WILD_CROPS);

        tag(COLORED_WOOL).addNotWhite("wool");
        tag(COLORED_CARPETS).addNotWhite("carpet");
        tag(COLORED_BEDS).addNotWhite("bed");
        tag(COLORED_BANNERS).addNotWhite("banner");
        tag(COLORED_TERRACOTTA).addNotWhite("terracotta");
        tag(COLORED_GLAZED_TERRACOTTA).addNotWhite("glazed_terracotta");
        tag(COLORED_SHULKER_BOXES).addNotWhite("shulker_box");
        tag(COLORED_CONCRETE_POWDER).addNotWhite("concrete_powder");
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
        tag(BARRELS).add(TFCBlocks.WOODS, Wood.BlockType.BARREL);
        copy(TFCTags.Blocks.LAMPS, LAMPS);
        tag(Tags.Items.BUCKETS).add(
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
            .addTag(COLORED_WINDMILL_BLADES)
            .add(
                TFCItems.WINDMILL_BLADES.get(DyeColor.WHITE),
                TFCItems.LATTICE_WINDMILL_BLADE,
                TFCItems.RUSTIC_WINDMILL_BLADE);
        tag(AXLES).add(TFCBlocks.WOODS, Wood.BlockType.AXLE);
        tag(GEAR_BOXES).add(TFCBlocks.WOODS, Wood.BlockType.GEAR_BOX);
        tag(CLUTCHES).add(TFCBlocks.WOODS, Wood.BlockType.CLUTCH);
        tag(SUPPORT_BEAMS).add(TFCItems.SUPPORTS);
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

        // Vanilla Tool Tags
        tag(ItemTags.SWORDS).add(TFCItems.METAL_ITEMS, Metal.ItemType.SWORD);
        tag(ItemTags.AXES)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.AXE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.AXE);
        tag(ItemTags.HOES)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.HOE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.HOE);
        tag(ItemTags.PICKAXES).add(TFCItems.METAL_ITEMS, Metal.ItemType.PICKAXE);
        tag(ItemTags.SHOVELS)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.SHOVEL)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.SHOVEL);

        // Common `#c:tools/???`
        tag(Tags.Items.TOOLS_SHIELD).add(TFCItems.METAL_ITEMS, Metal.ItemType.SHIELD);
        tag(Tags.Items.TOOLS_FISHING_ROD).add(TFCItems.METAL_ITEMS, Metal.ItemType.FISHING_ROD);
        tag(Tags.Items.TOOLS_SPEAR)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.JAVELIN)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.JAVELIN);
        tag(Tags.Items.TOOLS_SHEAR).add(TFCItems.METAL_ITEMS, Metal.ItemType.SHEARS);
        tag(Tags.Items.TOOLS_IGNITER).add(TFCItems.FIRESTARTER);
        tag(Tags.Items.TOOLS_MACE).add(TFCItems.METAL_ITEMS, Metal.ItemType.MACE);
        // N.B.
        // melee_weapons, ranged_weapons, and mining_tool are all poorly defined, their use case is not clear,
        // and they don't contain other tool tags (???) so it's unclear what the point of them is.

        // TFC Added `#c:tools/`
        tag(TOOLS_HAMMER).add(TFCItems.METAL_ITEMS, Metal.ItemType.HAMMER);
        tag(TOOLS_SAW).add(TFCItems.METAL_ITEMS, Metal.ItemType.SAW);
        tag(TOOLS_SCYTHE).add(TFCItems.METAL_ITEMS, Metal.ItemType.SCYTHE);
        tag(TOOLS_KNIFE)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.KNIFE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.KNIFE);
        tag(TOOLS_CHISEL).add(TFCItems.METAL_ITEMS, Metal.ItemType.CHISEL);
        tag(TOOLS_GLASSWORKING).add(TFCItems.PADDLE, TFCItems.JACKS, TFCItems.GEM_SAW);
        tag(TOOLS_BLOWPIPE).add(TFCItems.BLOWPIPE, TFCItems.CERAMIC_BLOWPIPE);
        tag(TOOLS_SHARP).addTags(
            ItemTags.AXES,
            TOOLS_KNIFE,
            TOOLS_SCYTHE,
            TOOLS_SAW);

        // Common `#c:tools`
        tag(Tags.Items.TOOLS).addTags(
            TOOLS_HAMMER,
            TOOLS_SAW,
            TOOLS_SCYTHE,
            TOOLS_KNIFE,
            TOOLS_CHISEL,
            TOOLS_GLASSWORKING,
            TOOLS_BLOWPIPE);

        // Tool Damage Types
        tag(DEALS_SLASHING_DAMAGE).addTags(
            ItemTags.SWORDS,
            ItemTags.AXES,
            ItemTags.HOES,
            Tags.Items.TOOLS_SHEAR,
            TOOLS_SAW,
            TOOLS_SCYTHE);
        tag(DEALS_PIERCING_DAMAGE).addTags(
            ItemTags.PICKAXES,
            Tags.Items.TOOLS_BOW,
            Tags.Items.TOOLS_CROSSBOW,
            Tags.Items.TOOLS_SPEAR,
            TOOLS_KNIFE,
            TOOLS_CHISEL);
        tag(DEALS_CRUSHING_DAMAGE).addTags(
            ItemTags.SHOVELS,
            Tags.Items.TOOLS_SHIELD,
            Tags.Items.TOOLS_FISHING_ROD,
            Tags.Items.TOOLS_MACE,
            TOOLS_HAMMER);

        tag(GLASS_BATCHES_T2).add(TFCItems.SILICA_GLASS_BATCH, TFCItems.HEMATITIC_GLASS_BATCH);
        tag(GLASS_BATCHES_T3).addTag(GLASS_BATCHES_T2).add(TFCItems.OLIVINE_GLASS_BATCH);
        tag(GLASS_BATCHES).addTag(GLASS_BATCHES_T3).add(TFCItems.VOLCANIC_GLASS_BATCH);
        tag(GLASS_BATCHES_NOT_T1).add(TFCItems.HEMATITIC_GLASS_BATCH, TFCItems.OLIVINE_GLASS_BATCH, TFCItems.VOLCANIC_GLASS_BATCH);
        tag(GLASS_BLOWPIPES).add(TFCItems.BLOWPIPE_WITH_GLASS, TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS);
        tag(BLOWPIPES).addTags(TOOLS_BLOWPIPE, GLASS_BLOWPIPES);
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
        tag(GEM_POWDERS).addOnly(TFCItems.ORE_POWDERS, Ore::isGem);
        tag(BOOKS).add(
            Items.BOOK,
            Items.ENCHANTED_BOOK,
            Items.WRITABLE_BOOK,
            Items.WRITTEN_BOOK,
            Items.KNOWLEDGE_BOOK);
        tag(ORE_DEPOSITS).addAll(TFCBlocks.ORE_DEPOSITS);
        final var tannin = EnumSet.of(Wood.BIRCH, Wood.CHESTNUT, Wood.DOUGLAS_FIR, Wood.HICKORY, Wood.MAPLE, Wood.OAK, Wood.SEQUOIA);
        tag(TANNIN_LOGS)
            .addOnly(pivot(TFCBlocks.WOODS, Wood.BlockType.LOG), tannin::contains)
            .addOnly(pivot(TFCBlocks.WOODS, Wood.BlockType.WOOD), tannin::contains);

        tag(FIREPIT_KINDLING)
            .addTags(ItemTags.LEAVES, BOOKS)
            .add(TFCItems.STRAW, Items.PAPER);
        tag(FIREPIT_STICKS).addTag(Tags.Items.RODS_WOODEN);
        tag(FIREPIT_LOGS).addTag(ItemTags.LOGS_THAT_BURN);
        tag(LOG_PILE_LOGS).addTag(ItemTags.LOGS);
        tag(PIT_KILN_STRAW).add(TFCItems.STRAW);
        tag(PIT_KILN_LOGS).addTags(ItemTags.LOGS_THAT_BURN);
        tag(INEFFICIENT_LOGGING_AXES).add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.AXE);
        tag(CAN_BE_LIT_ON_TORCH).addTag(Tags.Items.RODS_WOODEN);
        tag(ROCK_KNAPPING).addTag(STONES_LOOSE);
        tag(CLAY_KNAPPING).add(Items.CLAY_BALL);
        tag(FIRE_CLAY_KNAPPING).add(TFCItems.FIRE_CLAY);
        tag(LEATHER_KNAPPING).add(Items.LEATHER);
        tag(GOAT_HORN_KNAPPING).add(Items.GOAT_HORN);
        tag(QUERN_HANDSTONES).add(TFCItems.HANDSTONE);
        tag(SEWING_LIGHT_CLOTH).add(TFCItems.WOOL_CLOTH, TFCItems.SILK_CLOTH);
        tag(SEWING_DARK_CLOTH).add(TFCItems.BURLAP_CLOTH);
        tag(SEWING_NEEDLES).add(TFCItems.BONE_NEEDLE);
        tag(FIREPIT_FUEL)
            .addTags(BOOKS, ItemTags.LEAVES, ItemTags.LOGS_THAT_BURN)
            .add(
                TFCBlocks.PEAT,
                TFCBlocks.PEAT_GRASS,
                TFCItems.STICK_BUNDLE,
                Items.PAPER,
                TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.PINECONE),
                TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.DRIFTWOOD));
        tag(FORGE_FUEL).addTag(ItemTags.COALS);
        tag(BLAST_FURNACE_FUEL).add(Items.CHARCOAL);
        tag(BLAST_FURNACE_SHEETS)
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.SHEET))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.SHEET))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.SHEET))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.SHEET))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.SHEET));
        tag(BLAST_FURNACE_TUYERES).add(TFCItems.METAL_ITEMS, Metal.ItemType.TUYERE);
        tag(TOOL_RACK_TOOLS)
            .addTags(Tags.Items.TOOLS, SEWING_NEEDLES)
            .add(TFCItems.SANDPAPER, Items.SPYGLASS);
        tag(POWDER_KEG_FUEL).add(Items.GUNPOWDER);
        tag(MINECART_HOLDABLE)
            // Don't use tags, as this is technically restricted to only having blocks, so we don't want it to include other values accidentally
            .add(TFCBlocks.WOODS, Wood.BlockType.BARREL)
            .add(TFCBlocks.METALS, Metal.BlockType.ANVIL)
            .add(TFCBlocks.GLAZED_LARGE_VESSELS)
            .add(
                TFCBlocks.LARGE_VESSEL,
                TFCBlocks.CRUCIBLE,
                TFCBlocks.POWDERKEG
            );
        tag(TRIP_HAMMERS).add(TFCItems.METAL_ITEMS, Metal.ItemType.HAMMER); // N.B. Technical tag, don't include sub-tags
        tag(WELDING_FLUX).add(TFCItems.POWDERS.get(Powder.FLUX));
        tag(THATCH_BED_HIDES).add(TFCItems.HIDES.get(HideItemType.RAW).get(HideItemType.Size.LARGE));
        tag(BOWL_POWDERS) // N.B. Technical tag, don't include sub-tags
            .add(TFCItems.POWDERS)
            .add(TFCItems.ORE_POWDERS)
            .add(
                Items.REDSTONE,
                Items.GLOWSTONE_DUST,
                Items.BLAZE_POWDER,
                Items.GUNPOWDER
            );
        tag(SCRAPING_WAXES).add(TFCItems.GLUE, Items.HONEYCOMB);
        pivot(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.LOOSE).forEach((rock, item) -> tag(STONES_LOOSE_CATEGORY.get(rock.category())).add(item));
        pivot(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_LOOSE).forEach((rock, item) -> tag(STONES_LOOSE_CATEGORY.get(rock.category())).add(item));

        tag(FLUID_ITEM_INGREDIENT_EMPTY_CONTAINERS)
            .addTag(GLASS_BOTTLES)
            .add(
                Items.BUCKET,
                TFCItems.WOODEN_BUCKET,
                TFCItems.BLUE_STEEL_BUCKET,
                TFCItems.RED_STEEL_BUCKET,
                TFCItems.JUG);
        tag(DISABLED_MONSTER_HELD_ITEMS).add(
            Items.IRON_SHOVEL,
            Items.IRON_SWORD,
            Items.FISHING_ROD,
            Items.NAUTILUS_SHELL);
        tag(FOX_SPAWNS_WITH)
            .add(
                Items.RABBIT_FOOT,
                Items.FEATHER,
                Items.BONE,
                Items.FLINT,
                Items.EGG,
                TFCItems.HIDES.get(HideItemType.RAW).get(HideItemType.Size.SMALL))
            .add(
                Food.SALMON,
                Food.BLUEGILL,
                Food.CLOUDBERRY,
                Food.STRAWBERRY,
                Food.GOOSEBERRY,
                Food.RABBIT);
        tag(CARRIED_BY_HORSE).addTags(Tags.Items.CHESTS_WOODEN, BARRELS);

        Stream.of(Metal.COPPER, Metal.BRONZE, Metal.BISMUTH_BRONZE, Metal.BLACK_BRONZE)
            .map(TFCItems.METAL_ITEMS::get)
            .forEach(items -> {
                tag(MOB_HEAD_ARMOR).add(items.get(Metal.ItemType.HELMET));
                tag(MOB_CHEST_ARMOR).add(items.get(Metal.ItemType.CHESTPLATE));
                tag(MOB_LEG_ARMOR).add(items.get(Metal.ItemType.GREAVES));
                tag(MOB_FEET_ARMOR).add(items.get(Metal.ItemType.BOOTS));
                tag(SKELETON_WEAPONS).add(items.get(Metal.ItemType.JAVELIN));
            });

        tag(SKELETON_WEAPONS)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.AXE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.JAVELIN)
            .add(Items.BOW);

        for (Ore ore : Ore.values())
        {
            if (ore.isGraded())
            {
                copy(oreBlockTagOf(ore, Ore.Grade.POOR));
                copy(oreBlockTagOf(ore, Ore.Grade.NORMAL));
                copy(oreBlockTagOf(ore, Ore.Grade.RICH));
            }
            else
            {
                copy(oreBlockTagOf(ore, null));
            }
        }

        for (Wood wood : Wood.VALUES)
        {
            copy(logsTagOf(Registries.BLOCK, wood), logsTagOf(Registries.ITEM, wood));
        }

        copy(TFCTags.Blocks.STONES_RAW, STONES_RAW);
        copy(TFCTags.Blocks.STONES_HARDENED, STONES_HARDENED);
        copy(TFCTags.Blocks.STONES_SMOOTH, STONES_SMOOTH);
        copy(TFCTags.Blocks.STONES_SMOOTH_SLABS, STONES_SMOOTH_SLABS);
        copy(TFCTags.Blocks.STONES_PRESSURE_PLATES, STONES_PRESSURE_PLATES);
        copy(TFCTags.Blocks.STONES_LOOSE, STONES_LOOSE);

        copy(TFCTags.Blocks.DIRT, DIRT);
        copy(TFCTags.Blocks.GRASS, GRASS);
        copy(TFCTags.Blocks.MUD, MUD);
        copy(TFCTags.Blocks.MUD_BRICKS, MUD_BRICKS);

        copy(TFCTags.Blocks.ANVILS, ANVILS);
        copy(TFCTags.Blocks.WORKBENCHES, WORKBENCHES);
        copy(TFCTags.Blocks.AQUEDUCTS, AQUEDUCTS);

        copy(TFCTags.Blocks.FALLEN_LEAVES, FALLEN_LEAVES);
        copy(TFCTags.Blocks.CLAY_INDICATORS, CLAY_INDICATORS);
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

    private void copy(TagKey<Block> blockTag)
    {
        this.tagsToCopy.put(blockTag, TagKey.create(Registries.ITEM, blockTag.location()));
    }

    private void copy(TagKey<Block> blockTag, TagKey<Item> itemTag)
    {
        this.tagsToCopy.put(blockTag, itemTag);
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> createContentsProvider()
    {
        return super.createContentsProvider().thenCombine(blockTags, (lookup, tagLookup) -> {
            tagsToCopy.forEach((blockTag, itemTag) -> {
                tagLookup.apply(blockTag)
                    .map(TagBuilder::build)
                    .filter(e -> !e.isEmpty())
                    .ifPresentOrElse(content -> {
                        // N.B. Only copy the tag if the original is non-empty. We do this since we copy all vanilla tags by default,
                        // and we only really want to include the ones that we are adding to
                        final TagBuilder builder = getOrCreateRawBuilder(itemTag);
                        content.forEach(builder::add);
                    }, () -> {
                        // Throw an error if we try and copy a TFC tag that didn't exist
                        if (blockTag.location().getNamespace().equals("tfc")) throw new IllegalArgumentException("Copying empty or missing tag " + blockTag.location());
                    });
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

        ItemTagAppender addNotWhite(String itemName)
        {
            for (DyeColor c : Helpers.DYE_COLORS_NOT_WHITE) add(itemOf(ResourceLocation.withDefaultNamespace(c.getSerializedName() + "_" + itemName)));
            return this;
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
