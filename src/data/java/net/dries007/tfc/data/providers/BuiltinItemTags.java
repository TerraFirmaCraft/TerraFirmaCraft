/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.lang.reflect.Field;
import java.util.Comparator;
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
import net.dries007.tfc.common.blocks.crop.Crop;
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

        // ===== Minecraft Tags ===== //

        tag(ItemTags.ARROWS).add(TFCItems.GLOW_ARROW.key());
        tag(ItemTags.BOATS).add(TFCItems.BOATS);

        // Vanilla Armor Tags
        tag(ItemTags.HEAD_ARMOR).add(TFCItems.METAL_ITEMS, Metal.ItemType.HELMET);
        tag(ItemTags.CHEST_ARMOR).add(TFCItems.METAL_ITEMS, Metal.ItemType.CHESTPLATE);
        tag(ItemTags.LEG_ARMOR).add(TFCItems.METAL_ITEMS, Metal.ItemType.GREAVES);
        tag(ItemTags.FOOT_ARMOR).add(TFCItems.METAL_ITEMS, Metal.ItemType.BOOTS);

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

        // ===== Common Tags ===== //

        //-----Block Item Tags------//

        tag(Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES).add(TFCBlocks.WOODS, Wood.BlockType.WORKBENCH);
        tag(Tags.Items.STORAGE_BLOCKS_WHEAT).remove(Items.HAY_BLOCK);

        tag(Tags.Items.FERTILIZERS)
            .add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.GUANO).get().asItem().builtInRegistryHolder().key());

        //Sands

        tag(Tags.Items.SANDS).addTags(
            SILICA_SAND,
            OLIVINE_SAND,
            HEMATITIC_SAND,
            VOLCANIC_SAND
        );

        tag(SILICA_SAND).add(
            TFCBlocks.SAND.get(SandBlockType.WHITE)
        );
        tag(OLIVINE_SAND).add(
            TFCBlocks.SAND.get(SandBlockType.GREEN),
            TFCBlocks.SAND.get(SandBlockType.BROWN)
        );
        tag(HEMATITIC_SAND).add(
            TFCBlocks.SAND.get(SandBlockType.YELLOW),
            TFCBlocks.SAND.get(SandBlockType.RED),
            TFCBlocks.SAND.get(SandBlockType.PINK)
        );
        tag(VOLCANIC_SAND).add(
            TFCBlocks.SAND.get(SandBlockType.BLACK)
        );

        for (var entry : TFCBlocks.SMALL_ORES.entrySet())
        {
            Ore ore = entry.getKey();
            Metal metal = ore.metal();
            tag(commonTagOf(Registries.ITEM, "raw_materials/" + metal.name() + "/small"))
                .add(entry.getValue().asItem().builtInRegistryHolder().key());
        }

        //-----Item Tags-----//

        //Armors

        tag(commonTagOf(Registries.ITEM, "armors/horse"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.HORSE_ARMOR);

        //Arrows

        tag(commonTagOf(Registries.ITEM, "arrows"))
            .add(TFCItems.GLOW_ARROW.key());

        //Boats

        tag(commonTagOf(Registries.ITEM, "boats"))
            .add(TFCItems.BOATS);

        //Bricks

        tag(Tags.Items.BRICKS).addTags(
             commonTagOf(Registries.ITEM, "bricks/mud"),
             commonTagOf(Registries.ITEM, "bricks/plaster"),
             commonTagOf(Registries.ITEM, "bricks/fire"),
             commonTagOf(Registries.ITEM, "bricks/stone")
        );

        tag(commonTagOf(Registries.ITEM, "bricks/mud")).add(
            TFCItems.ENTISOL_MUD_BRICK, TFCItems.ALFISOL_MUD_BRICK,
            TFCItems.PODZOL_MUD_BRICK, TFCItems.ANDISOL_MUD_BRICK,
            TFCItems.FLUVISOL_MUD_BRICK, TFCItems.ARIDISOL_MUD_BRICK,
            TFCItems.OXISOL_MUD_BRICK, TFCItems.MOLLISOL_MUD_BRICK
        );
        tag(commonTagOf(Registries.ITEM, "bricks/plaster"))
            .add(TFCItems.ALABASTER_BRICK.key());
        tag(commonTagOf(Registries.ITEM, "bricks/fire"))
            .add(TFCItems.FIRE_BRICK.key());
        tag(commonTagOf(Registries.ITEM, "bricks/stone"))
            .add(TFCItems.BRICKS);

        //Cloths

        tag(commonTagOf(Registries.ITEM, "cloths")).addTags(
            commonTagOf(Registries.ITEM, "cloths/burlap"),
            commonTagOf(Registries.ITEM, "cloths/silk"),
            commonTagOf(Registries.ITEM, "cloths/wool")
        );

        tag(commonTagOf(Registries.ITEM, "cloths/burlap"))
            .add(TFCItems.BURLAP_CLOTH.key());
        tag(commonTagOf(Registries.ITEM, "cloths/silk"))
            .add(TFCItems.SILK_CLOTH.key());
        tag(commonTagOf(Registries.ITEM, "cloths/wool"))
            .add(TFCItems.WOOL_CLOTH.key());

        //Crops TODO: Check

        tag(Tags.Items.CROPS)
             .addTags(
                    commonTagOf(Registries.ITEM, "crops/alfalfa"),
                    commonTagOf(Registries.ITEM, "crops/apple"), commonTagOf(Registries.ITEM, "crops/banana"), commonTagOf(Registries.ITEM, "crops/barley"),
                    commonTagOf(Registries.ITEM, "crops/beetroot"), commonTagOf(Registries.ITEM, "crops/beet"), commonTagOf(Registries.ITEM, "crops/corn"),
                    commonTagOf(Registries.ITEM, "crops/bell_pepper"), commonTagOf(Registries.ITEM, "crops/blackberry"), commonTagOf(Registries.ITEM, "crops/blueberry"),
                    commonTagOf(Registries.ITEM, "crops/bunchberry"), commonTagOf(Registries.ITEM, "crops/cabbage"), commonTagOf(Registries.ITEM, "crops/carrot"),
                    commonTagOf(Registries.ITEM, "crops/cassava"), commonTagOf(Registries.ITEM, "crops/cattail_root"), commonTagOf(Registries.ITEM, "crops/cherry"),
                    commonTagOf(Registries.ITEM, "crops/cloudberry"), commonTagOf(Registries.ITEM, "crops/cranberry"), commonTagOf(Registries.ITEM, "crops/canola"),
                    commonTagOf(Registries.ITEM, "crops/elderberry"), commonTagOf(Registries.ITEM, "crops/garlic"), commonTagOf(Registries.ITEM, "crops/gooseberry"),
                    commonTagOf(Registries.ITEM, "crops/green_apple"), commonTagOf(Registries.ITEM, "crops/green_bean"), commonTagOf(Registries.ITEM, "crops/green_bell_pepper"),
                    commonTagOf(Registries.ITEM, "crops/jute"), commonTagOf(Registries.ITEM, "crops/lemon"), commonTagOf(Registries.ITEM, "crops/lentil"),
                    commonTagOf(Registries.ITEM, "crops/maize"), commonTagOf(Registries.ITEM, "crops/corn"), commonTagOf(Registries.ITEM, "crops/oat"),
                    commonTagOf(Registries.ITEM, "crops/olive"), commonTagOf(Registries.ITEM, "crops/onion"), commonTagOf(Registries.ITEM, "crops/orange"),
                    commonTagOf(Registries.ITEM, "crops/peach"), commonTagOf(Registries.ITEM, "crops/peanut"), commonTagOf(Registries.ITEM, "crops/plum"),
                    commonTagOf(Registries.ITEM, "crops/potato"), commonTagOf(Registries.ITEM, "crops/papyrus"), commonTagOf(Registries.ITEM, "crops/radish"),
                    commonTagOf(Registries.ITEM, "crops/raspberry"), commonTagOf(Registries.ITEM, "crops/red_bell_pepper"), commonTagOf(Registries.ITEM, "crops/rice"),
                    commonTagOf(Registries.ITEM, "crops/rye"), commonTagOf(Registries.ITEM, "crops/seaweed"), commonTagOf(Registries.ITEM, "crops/snowberry"),
                    commonTagOf(Registries.ITEM, "crops/soybean"), commonTagOf(Registries.ITEM, "crops/squash"), commonTagOf(Registries.ITEM, "crops/strawberry"),
                    commonTagOf(Registries.ITEM, "crops/sugarcane"), commonTagOf(Registries.ITEM, "crops/taro"), commonTagOf(Registries.ITEM, "crops/taro_root"),
                    commonTagOf(Registries.ITEM, "crops/tomato"), commonTagOf(Registries.ITEM, "crops/wheat"), commonTagOf(Registries.ITEM, "crops/wintergreen_berry"),
                    commonTagOf(Registries.ITEM, "crops/yellow_bell_pepper")
             );

        tag(commonTagOf(Registries.ITEM, "crops/alfalfa"))
            .add(TFCItems.ALFALFA.key());
        tag(commonTagOf(Registries.ITEM, "crops/apple"))
            .add(Food.GREEN_APPLE, Food.RED_APPLE);
        tag(commonTagOf(Registries.ITEM, "crops/banana"))
            .add(Food.BANANA);
        tag(commonTagOf(Registries.ITEM, "crops/barley"))
            .add(Food.BARLEY);
        tag(commonTagOf(Registries.ITEM, "crops/beetroot"))
            .add(Food.BEET);
        tag(commonTagOf(Registries.ITEM, "crops/beet"))
            .add(Food.BEET);
        tag(commonTagOf(Registries.ITEM, "crops/corn"))
            .add(Food.MAIZE);
        tag(commonTagOf(Registries.ITEM, "crops/bell_pepper"))
            .add(Food.GREEN_BELL_PEPPER, Food.RED_BELL_PEPPER, Food.YELLOW_BELL_PEPPER);
        tag(commonTagOf(Registries.ITEM, "crops/blackberry"))
            .add(Food.BLACKBERRY);
        tag(commonTagOf(Registries.ITEM, "crops/blueberry"))
            .add(Food.BLUEBERRY);
        tag(commonTagOf(Registries.ITEM, "crops/bunchberry"))
            .add(Food.BUNCHBERRY);
        tag(commonTagOf(Registries.ITEM, "crops/cabbage"))
            .add(Food.CABBAGE);
        tag(commonTagOf(Registries.ITEM, "crops/carrot"))
            .add(Food.CARROT);
        tag(commonTagOf(Registries.ITEM, "crops/cassava"))
            .add(Food.CASSAVA);
        tag(commonTagOf(Registries.ITEM, "crops/cattail_root"))
            .add(Food.CATTAIL_ROOT);
        tag(commonTagOf(Registries.ITEM, "crops/cherry"))
            .add(Food.CHERRY);
        tag(commonTagOf(Registries.ITEM, "crops/cloudberry"))
            .add(Food.CLOUDBERRY);
        tag(commonTagOf(Registries.ITEM, "crops/cranberry"))
            .add(Food.CRANBERRY);
        tag(commonTagOf(Registries.ITEM, "crops/canola"))
            .add(TFCItems.CANOLA.key());
        tag(commonTagOf(Registries.ITEM, "crops/elderberry"))
            .add(Food.ELDERBERRY);
        tag(commonTagOf(Registries.ITEM, "crops/garlic"))
            .add(Food.GARLIC);
        tag(commonTagOf(Registries.ITEM, "crops/gooseberry"))
            .add(Food.GOOSEBERRY);
        tag(commonTagOf(Registries.ITEM, "crops/green_apple"))
            .add(Food.GREEN_APPLE);
        tag(commonTagOf(Registries.ITEM, "crops/green_bean"))
            .add(Food.GREEN_BEAN);
        tag(commonTagOf(Registries.ITEM, "crops/green_bell_pepper"))
            .add(Food.GREEN_BELL_PEPPER);
        tag(commonTagOf(Registries.ITEM, "crops/jute"))
            .add(TFCItems.JUTE.key());
        tag(commonTagOf(Registries.ITEM, "crops/lemon"))
            .add(Food.LEMON);
        tag(commonTagOf(Registries.ITEM, "crops/lentil"))
            .add(Food.LENTIL);
        tag(commonTagOf(Registries.ITEM, "crops/maize"))
            .add(Food.MAIZE);
        tag(commonTagOf(Registries.ITEM, "crops/corn"))
            .add(Food.MAIZE);
        tag(commonTagOf(Registries.ITEM, "crops/oat"))
            .add(Food.OAT);
        tag(commonTagOf(Registries.ITEM, "crops/olive"))
            .add(Food.OLIVE);
        tag(commonTagOf(Registries.ITEM, "crops/onion"))
            .add(Food.ONION);
        tag(commonTagOf(Registries.ITEM, "crops/orange"))
            .add(Food.ORANGE);
        tag(commonTagOf(Registries.ITEM, "crops/peach"))
            .add(Food.PEACH);
        tag(commonTagOf(Registries.ITEM, "crops/peanut"))
            .add(Food.PEANUT);
        tag(commonTagOf(Registries.ITEM, "crops/plum"))
            .add(Food.PLUM);
        tag(commonTagOf(Registries.ITEM, "crops/potato"))
            .add(Food.POTATO);
        tag(commonTagOf(Registries.ITEM, "crops/papyrus"))
            .add(TFCItems.PAPYRUS.key());
        tag(commonTagOf(Registries.ITEM, "crops/radish"))
            .add(Food.RADISH);
        tag(commonTagOf(Registries.ITEM, "crops/raspberry"))
            .add(Food.RASPBERRY);
        tag(commonTagOf(Registries.ITEM, "crops/red_bell_pepper"))
            .add(Food.RED_BELL_PEPPER);
        tag(commonTagOf(Registries.ITEM, "crops/rice"))
            .add(Food.RICE);
        tag(commonTagOf(Registries.ITEM, "crops/rye"))
            .add(Food.RYE);
        tag(commonTagOf(Registries.ITEM, "crops/seaweed"))
            .add(Food.FRESH_SEAWEED);
        tag(commonTagOf(Registries.ITEM, "crops/snowberry"))
            .add(Food.SNOWBERRY);
        tag(commonTagOf(Registries.ITEM, "crops/soybean"))
            .add(Food.SOYBEAN);
        tag(commonTagOf(Registries.ITEM, "crops/squash"))
            .add(Food.SQUASH);
        tag(commonTagOf(Registries.ITEM, "crops/strawberry"))
            .add(Food.STRAWBERRY);
        tag(commonTagOf(Registries.ITEM, "crops/sugarcane"))
            .add(Food.SUGARCANE);
        tag(commonTagOf(Registries.ITEM, "crops/taro"))
            .add(Food.TARO_ROOT);
        tag(commonTagOf(Registries.ITEM, "crops/taro_root"))
            .add(Food.TARO_ROOT);
        tag(commonTagOf(Registries.ITEM, "crops/tomato"))
            .add(Food.TOMATO);
        tag(commonTagOf(Registries.ITEM, "crops/wheat"))
            .add(Food.WHEAT);
        tag(commonTagOf(Registries.ITEM, "crops/wintergreen_berry"))
            .add(Food.WINTERGREEN_BERRY);
        tag(commonTagOf(Registries.ITEM, "crops/yellow_bell_pepper"))
            .add(Food.YELLOW_BELL_PEPPER);


        //Doughs

        tag(commonTagOf(Registries.ITEM, "doughs"))
            .addTags(
                commonTagOf(Registries.ITEM, "doughs/barley"), commonTagOf(Registries.ITEM, "doughs/maize"),
                commonTagOf(Registries.ITEM, "doughs/oat"), commonTagOf(Registries.ITEM, "doughs/rye"),
                commonTagOf(Registries.ITEM, "doughs/rice"), commonTagOf(Registries.ITEM, "doughs/wheat")
            );

        tag(commonTagOf(Registries.ITEM, "doughs/barley"))
            .add(Food.BARLEY_DOUGH);
        tag(commonTagOf(Registries.ITEM, "doughs/maize"))
            .add(Food.MAIZE_DOUGH);
        tag(commonTagOf(Registries.ITEM, "doughs/oat"))
            .add(Food.OAT_DOUGH);
        tag(commonTagOf(Registries.ITEM, "doughs/rye"))
            .add(Food.RYE_DOUGH);
        tag(commonTagOf(Registries.ITEM, "doughs/rice"))
            .add(Food.RICE_DOUGH);
        tag(commonTagOf(Registries.ITEM, "doughs/wheat"))
            .add(Food.WHEAT_DOUGH);

        //Buckets

        tag(Tags.Items.BUCKETS).add(
            TFCItems.WOODEN_BUCKET,
            TFCItems.RED_STEEL_BUCKET,
            TFCItems.BLUE_STEEL_BUCKET);

        tag(Tags.Items.BUCKETS_ENTITY_WATER).add(
            TFCItems.JELLYFISH_BUCKET.key(),
            TFCItems.TROPICAL_FISH_BUCKET.key(),
            TFCItems.PUFFERFISH_BUCKET.key(),
            TFCItems.COD_BUCKET.key()
        );
        tag(Tags.Items.BUCKETS_ENTITY_WATER)
            .add(TFCItems.FRESHWATER_FISH_BUCKETS);

        //Dusts

        tag(commonTagOf(Registries.ITEM, "dusts/saltpeter"))
            .add(TFCItems.ORE_POWDERS.get(Ore.SALTPETER).key());
        tag(commonTagOf(Registries.ITEM, "dusts/charcoal"))
            .add(TFCItems.POWDERS.get(Powder.CHARCOAL).key());
        tag(commonTagOf(Registries.ITEM, "dusts/coal_coke"))
            .add(TFCItems.POWDERS.get(Powder.COKE).key());
        tag(commonTagOf(Registries.ITEM, "dusts/kaolinite"))
            .add(TFCItems.POWDERS.get(Powder.KAOLINITE).key());
        tag(commonTagOf(Registries.ITEM, "dusts/graphite"))
            .add(TFCItems.ORE_POWDERS.get(Ore.GRAPHITE).key());
        tag(commonTagOf(Registries.ITEM, "dusts/sylvite"))
            .add(TFCItems.ORE_POWDERS.get(Ore.SYLVITE).key());
        tag(commonTagOf(Registries.ITEM, "dusts/salt"))
            .add(TFCItems.POWDERS.get(Powder.SALT).key());
        tag(commonTagOf(Registries.ITEM, "dusts/flux"))
            .add(TFCItems.POWDERS.get(Powder.FLUX).key());
        tag(commonTagOf(Registries.ITEM, "dusts/ash"))
            .add(TFCItems.POWDERS.get(Powder.WOOD_ASH).key());
        tag(commonTagOf(Registries.ITEM, "dusts/wood_ash"))
            .add(TFCItems.POWDERS.get(Powder.WOOD_ASH).key());
        tag(commonTagOf(Registries.ITEM, "dusts/soda_ash"))
            .add(TFCItems.POWDERS.get(Powder.SODA_ASH).key());
        tag(commonTagOf(Registries.ITEM, "dusts/sulfur"))
            .add(TFCItems.ORE_POWDERS.get(Ore.SULFUR).key());
        tag(commonTagOf(Registries.ITEM, "dusts/lime"))
            .add(TFCItems.POWDERS.get(Powder.LIME).key());

        //Dyed

        for (DyeColor color : DyeColor.values())
        {
            tag(commonTagOf(Registries.ITEM, "dyed/" + color.getSerializedName()))
                .add(TFCItems.GLAZED_VESSELS.get(color))
                .add(TFCItems.UNFIRED_GLAZED_LARGE_VESSELS.get(color))
                .add(TFCItems.UNFIRED_GLAZED_VESSELS.get(color))
                .add(TFCItems.WINDMILL_BLADES.get(color));
        }

        //Fertilizers

        tag(Tags.Items.FERTILIZERS).add(
            TFCItems.COMPOST.key(),
            TFCItems.FOOD.get(Food.SHELLFISH).key(),
            TFCItems.ORE_POWDERS.get(Ore.SALTPETER).key(),
            TFCItems.POWDERS.get(Powder.WOOD_ASH).key(),
            TFCItems.ORE_POWDERS.get(Ore.SYLVITE).key(),
            TFCItems.PURE_NITROGEN.key(),
            TFCItems.PURE_PHOSPHORUS.key(),
            TFCItems.PURE_POTASSIUM.key()
        );

        //Fiber

        tag(commonTagOf(Registries.ITEM, "fibers"))
            .addTag(commonTagOf(Registries.ITEM, "fibers/jute"));

        tag(commonTagOf(Registries.ITEM, "fibers/jute"))
            .add(TFCItems.JUTE_FIBER.key());

        //TODO: Check
        //Foods -- Mods are all over the place with food tags and there seems to be no general consensus on how they should be tagged so I add what I saw were the most common tags used. How I believe they should be used is as follow
        //c:crops for food type items that cannot be eaten without processing (e.g. sugarcane, kelp, pumpkin etc.) tagging should follow the format of c:crops/<crop_name> (e.g. c:crops/sugarcane) and the tag should only contain the crop item itself, no seeds or other items related to the crop. All sub tags are added to the main c:crops tag.
        //c:foods for all food items that can be eaten, if you right-click and the eating animation plays it goes here. For individual items they should follow the format c:foods/<food_name> (e.g. c:foods/blueberry) and the tag should only contain the food item itself, no seeds or other items related to the food. For groups of items that share a common name but are different variants of the same item (e.g. blueberry, blackberry, etc.) they should be tagged with a common tag c:foods/berry, c:foods/fruit or c:foods/vegtable. An item might have multiple group tags.

        final EnumSet<Food> RAW_MEATS_FOODS = EnumSet.of(
            Food.BEEF, Food.BEAR, Food.BISON, Food.BLUEGILL, Food.BLUEGILL,
            Food.CALAMARI, Food.CALAMARI, Food.CAMELIDAE, Food.CHEVON, Food.CHICKEN,
            Food.COD, Food.COD, Food.CRAPPIE, Food.CRAPPIE, Food.DUCK,
            Food.FOX, Food.FROG_LEGS, Food.GRAN_FELINE, Food.GROUSE, Food.HORSE_MEAT,
            Food.HYENA, Food.LAKE_TROUT, Food.LAKE_TROUT, Food.LARGEMOUTH_BASS, Food.LARGEMOUTH_BASS,
            Food.MUTTON, Food.PEAFOWL, Food.PHEASANT, Food.PORK, Food.QUAIL,
            Food.RABBIT, Food.RAINBOW_TROUT, Food.RAINBOW_TROUT, Food.SALMON,
            Food.SALMON, Food.SHELLFISH, Food.SHELLFISH, Food.SMALLMOUTH_BASS,
            Food.SMALLMOUTH_BASS, Food.TROPICAL_FISH, Food.TROPICAL_FISH, Food.TURKEY,
            Food.TURTLE, Food.VENISON, Food.WOLF
        );

        tag(FOODS).addTags(
            DAIRY, SALADS, SANDWICHES, DOUGH, FLOUR, GRAINS,
            commonTagOf(Registries.ITEM, "foods/raw_poultry"),
            commonTagOf(Registries.ITEM, "foods/cooked_poultry"),
            commonTagOf(Registries.ITEM, "foods/jam")
        );

        for(Food food : Food.values())
        {
            if (RAW_MEATS_FOODS.contains(food))
            {
                tag(commonTagOf(Registries.ITEM, "foods/raw_" + food.getSerializedName())).add(food);
                tag(FOODS).addTag(commonTagOf(Registries.ITEM, "foods/raw_" + food.getSerializedName()));
                tag(commonTagOf(Registries.ITEM, "raw_" + food.getSerializedName())).add(food);
            }
            else
            {
                tag(commonTagOf(Registries.ITEM, "foods/" + food.getSerializedName())).add(food);
                tag(FOODS).addTag(commonTagOf(Registries.ITEM, "foods/" + food.getSerializedName()));
                tag(commonTagOf(Registries.ITEM, food.getSerializedName())).add(food); //Added for compatibility with mods that don't use the c:foods/<food_name> format and just put their food items in a tag with the same name as the item. This is not ideal, but it seems to be a common practice by some authors, so I added it for compatibility reasons.
            }
        }

        tag(FOODS).addTags(
            commonTagOf(Registries.ITEM, "foods/apple"),
            commonTagOf(Registries.ITEM, "foods/bell_pepper"),
            commonTagOf(Registries.ITEM, "foods/taro"),
            commonTagOf(Registries.ITEM, "foods/beetroot"),
            commonTagOf(Registries.ITEM, "foods/corn"),
            commonTagOf(Registries.ITEM, "foods/melon"),
            commonTagOf(Registries.ITEM, "foods/pumpkin"),
            commonTagOf(Registries.ITEM, "foods/seaweed")
            );

        tag(commonTagOf(Registries.ITEM, "foods/apple")).add(
            Food.GREEN_APPLE, Food.RED_APPLE
        );
        tag(commonTagOf(Registries.ITEM, "apple")).add(
            Food.GREEN_APPLE, Food.RED_APPLE
        );
        tag(commonTagOf(Registries.ITEM, "foods/bell_pepper")).add(
            Food.GREEN_BELL_PEPPER, Food.RED_BELL_PEPPER, Food.YELLOW_BELL_PEPPER
        );
        tag(commonTagOf(Registries.ITEM, "bell_pepper")).add(
            Food.GREEN_BELL_PEPPER, Food.RED_BELL_PEPPER, Food.YELLOW_BELL_PEPPER
        );
        tag(commonTagOf(Registries.ITEM, "foods/beetroot"))
            .add(Food.BEET);
        tag(commonTagOf(Registries.ITEM, "beetroot"))
            .add(Food.BEET);
        tag(commonTagOf(Registries.ITEM, "foods/corn"))
            .add(Food.MAIZE);
        tag(commonTagOf(Registries.ITEM, "corn"))
            .add(Food.MAIZE);
        tag(commonTagOf(Registries.ITEM, "foods/melon"))
            .add(Food.MELON_SLICE);
        tag(commonTagOf(Registries.ITEM, "melon"))
            .add(Food.MELON_SLICE);
        tag(commonTagOf(Registries.ITEM, "foods/pumpkin"))
            .add(Food.PUMPKIN_CHUNKS);
        tag(commonTagOf(Registries.ITEM, "pumpkin"))
            .add(Food.PUMPKIN_CHUNKS);
        tag(commonTagOf(Registries.ITEM, "foods/taro"))
            .add(Food.TARO_ROOT);
        tag(commonTagOf(Registries.ITEM, "foods/seaweed"))
            .add(Food.FRESH_SEAWEED);
        tag(commonTagOf(Registries.ITEM, "seaweed"))
            .add(Food.FRESH_SEAWEED);
        tag(commonTagOf(Registries.ITEM, "taro"))
            .add(Food.TARO_ROOT);
        tag(commonTagOf(Registries.ITEM, "foods/raw_lobster"))
            .add(Food.SHELLFISH);
        tag(commonTagOf(Registries.ITEM, "foods/cooked_lobster"))
            .add(Food.COOKED_SHELLFISH);
        tag(commonTagOf(Registries.ITEM, "foods/raw_squid"))
            .add(Food.CALAMARI);
        tag(commonTagOf(Registries.ITEM, "raw_squid"))
            .add(Food.CALAMARI);
        tag(commonTagOf(Registries.ITEM, "foods/raw_goat"))
            .add(Food.CHEVON);
        tag(commonTagOf(Registries.ITEM, "cooked_goat"))
            .add(Food.COOKED_CHEVON);
        tag(commonTagOf(Registries.ITEM, "foods/raw_camel"))
            .add(Food.CAMELIDAE);
        tag(commonTagOf(Registries.ITEM, "cooked_camel"))
            .add(Food.COOKED_CAMELIDAE);

        tag(BREAD)
            .add(Food.BARLEY_BREAD, Food.MAIZE_BREAD, Food.OAT_BREAD, Food.RYE_BREAD, Food.RICE_BREAD, Food.WHEAT_BREAD)
            .add(Items.BREAD);
        tag(DOUGH).add(
            Food.BARLEY_DOUGH, Food.MAIZE_DOUGH, Food.OAT_DOUGH, Food.RYE_DOUGH, Food.RICE_DOUGH, Food.WHEAT_DOUGH);
        tag(FLOUR).add(
            Food.BARLEY_FLOUR, Food.MAIZE_FLOUR, Food.OAT_FLOUR, Food.RYE_FLOUR, Food.RICE_FLOUR, Food.WHEAT_FLOUR);
        tag(GRAINS).add(
            Food.BARLEY_GRAIN, Food.MAIZE_GRAIN, Food.OAT_GRAIN, Food.RYE_GRAIN, Food.RICE_GRAIN, Food.WHEAT_GRAIN);

        tag(FISH).addTags(RAW_FISH, COOKED_FISH); //Might be unnecessary, but I left it in case any addons are using it.

        tag(COOKED_FISH).add(
            Food.COOKED_COD, Food.COOKED_TROPICAL_FISH, Food.COOKED_CALAMARI, Food.COOKED_SHELLFISH,
            Food.COOKED_BLUEGILL, Food.COOKED_CRAPPIE, Food.COOKED_LAKE_TROUT, Food.COOKED_LARGEMOUTH_BASS,
            Food.COOKED_RAINBOW_TROUT, Food.COOKED_SALMON, Food.COOKED_SMALLMOUTH_BASS
        );
        tag(RAW_FISH).add(
            Food.COD, Food.TROPICAL_FISH, Food.CALAMARI, Food.SHELLFISH,
            Food.BLUEGILL, Food.CRAPPIE, Food.LAKE_TROUT, Food.LARGEMOUTH_BASS,
            Food.RAINBOW_TROUT, Food.SALMON, Food.SMALLMOUTH_BASS
        );

        tag(COOKED_MEATS).add(
            Food.COOKED_BEEF, Food.COOKED_PORK, Food.COOKED_CHICKEN, Food.COOKED_QUAIL,
            Food.COOKED_MUTTON, Food.COOKED_BEAR, Food.COOKED_HORSE_MEAT, Food.COOKED_PHEASANT,
            Food.COOKED_TURKEY, Food.COOKED_PEAFOWL, Food.COOKED_GROUSE, Food.COOKED_VENISON,
            Food.COOKED_BISON, Food.COOKED_WOLF, Food.COOKED_RABBIT, Food.COOKED_FOX, Food.COOKED_HYENA,
            Food.COOKED_DUCK, Food.COOKED_CHEVON, Food.COOKED_CAMELIDAE, Food.COOKED_FROG_LEGS,
            Food.COOKED_GRAN_FELINE, Food.COOKED_TURTLE
        );
        tag(RAW_MEATS).add(
            Food.BEEF, Food.PORK, Food.CHICKEN, Food.QUAIL, Food.MUTTON,
            Food.BEAR, Food.HORSE_MEAT, Food.PHEASANT, Food.GROUSE, Food.TURKEY,
            Food.PEAFOWL, Food.VENISON, Food.BISON, Food.WOLF, Food.RABBIT,
            Food.FOX, Food.HYENA, Food.DUCK, Food.CHEVON, Food.GRAN_FELINE,
            Food.TURTLE, Food.CAMELIDAE, Food.FROG_LEGS
        );

        tag(commonTagOf(Registries.ITEM, "foods/raw_poultry")).add(
            Food.CHICKEN, Food.QUAIL, Food.PHEASANT,
            Food.GROUSE, Food.TURKEY, Food.PEAFOWL,
            Food.DUCK
        );
        tag(commonTagOf(Registries.ITEM, "foods/cooked_poultry")).add(
            Food.COOKED_CHICKEN, Food.COOKED_QUAIL, Food.COOKED_PHEASANT,
            Food.COOKED_GROUSE, Food.COOKED_TURKEY, Food.COOKED_PEAFOWL,
            Food.COOKED_DUCK
        );

        tag(Tags.Items.FOODS_BERRY).add(
            Food.BLACKBERRY, Food.BLUEBERRY, Food.BUNCHBERRY, Food.CLOUDBERRY,
            Food.CRANBERRY, Food.ELDERBERRY, Food.GOOSEBERRY, Food.RASPBERRY,
            Food.SNOWBERRY, Food.STRAWBERRY, Food.WINTERGREEN_BERRY);
        tag(FRUITS).add(
            Food.BLACKBERRY, Food.BLUEBERRY, Food.BUNCHBERRY, Food.CLOUDBERRY, Food.CRANBERRY, Food.ELDERBERRY,
            Food.GOOSEBERRY, Food.RASPBERRY, Food.SNOWBERRY, Food.STRAWBERRY, Food.WINTERGREEN_BERRY, Food.BANANA,
            Food.CHERRY, Food.GREEN_APPLE, Food.LEMON, Food.OLIVE, Food.ORANGE, Food.PEACH, Food.PLUM, Food.RED_APPLE, Food.MELON_SLICE
        );
        tag(VEGETABLES).add(
            Food.BEET, Food.CABBAGE, Food.CARROT, Food.GARLIC, Food.GREEN_BEAN, Food.GREEN_BELL_PEPPER,
            Food.ONION, Food.POTATO, Food.BAKED_POTATO, Food.RED_BELL_PEPPER, Food.SOYBEAN, Food.SUGARCANE,
            Food.SQUASH, Food.TOMATO, Food.YELLOW_BELL_PEPPER, Food.CASSAVA, Food.COOKED_CASSAVA, Food.LENTIL,
            Food.COOKED_LENTIL, Food.PEANUT, Food.RADISH, Food.PUMPKIN_CHUNKS
        );

        tag(DAIRY).add(Food.CHEESE);
        tag(SALADS).add(TFCItems.SALADS);
        tag(SOUPS).add(TFCItems.SOUPS);
        tag(commonTagOf(Registries.ITEM, "salad"))
            .add(TFCItems.SALADS);
        tag(commonTagOf(Registries.ITEM, "soup"))
            .add(TFCItems.SOUPS);

        tag(SANDWICHES).add(
            Food.BARLEY_BREAD_JAM_SANDWICH, Food.BARLEY_BREAD_SANDWICH, Food.MAIZE_BREAD_JAM_SANDWICH, Food.MAIZE_BREAD_SANDWICH,
            Food.OAT_BREAD_JAM_SANDWICH, Food.OAT_BREAD_SANDWICH, Food.RYE_BREAD_JAM_SANDWICH, Food.RYE_BREAD_SANDWICH,
            Food.RICE_BREAD_JAM_SANDWICH, Food.RICE_BREAD_SANDWICH, Food.WHEAT_BREAD_JAM_SANDWICH, Food.WHEAT_BREAD_SANDWICH);

        tag(commonTagOf(Registries.ITEM, "foods/jam"))
            .addTag(JAM);

        for (Map.Entry<Food, TFCItems.ItemId> entry : TFCItems.JAM.entrySet())
        {
            final Food jam = entry.getKey();
            final String jamName = jam == Food.PUMPKIN_CHUNKS ? "pumpkin_jam"
                : jam == Food.MELON_SLICE ? "melon_jam"
                : jam.getSerializedName();
            tag(commonTagOf(Registries.ITEM, "foods/" + jamName))
                .add(entry.getValue().key());
            tag(FOODS)
                .addTag(commonTagOf(Registries.ITEM, "foods/" + jamName));
            tag(commonTagOf(Registries.ITEM, entry.getKey().getSerializedName()))
                .add(entry.getValue().key());
        }

        tag(commonTagOf(Registries.ITEM, "salads"))
            .add(TFCItems.SALADS);
        tag(commonTagOf(Registries.ITEM, "soups"))
            .add(TFCItems.SOUPS);

        //Flours

        tag(commonTagOf(Registries.ITEM, "flours")).addTags(
            commonTagOf(Registries.ITEM, "flours/barley"),
            commonTagOf(Registries.ITEM, "flours/maize"),
            commonTagOf(Registries.ITEM, "flours/oat"),
            commonTagOf(Registries.ITEM, "flours/rye"),
            commonTagOf(Registries.ITEM, "flours/rice"),
            commonTagOf(Registries.ITEM, "flours/wheat")
        );

        tag(commonTagOf(Registries.ITEM, "flours/barley"))
            .add(Food.BARLEY_FLOUR);
        tag(commonTagOf(Registries.ITEM, "flours/maize"))
            .add(Food.MAIZE_FLOUR);
        tag(commonTagOf(Registries.ITEM, "flours/oat"))
            .add(Food.OAT_FLOUR);
        tag(commonTagOf(Registries.ITEM, "flours/rye"))
            .add(Food.RYE_FLOUR);
        tag(commonTagOf(Registries.ITEM, "flours/rice"))
            .add(Food.RICE_FLOUR);
        tag(commonTagOf(Registries.ITEM, "flours/wheat"))
            .add(Food.WHEAT_FLOUR);

        //Gems

        for (Map.Entry<Ore, TFCItems.ItemId> gemEntry : TFCItems.GEMS.entrySet())
        {
            final Ore ore = gemEntry.getKey();
            final String gemName = ore == Ore.LAPIS_LAZULI ? "lapis" : ore.name().toLowerCase();
            tag(commonTagOf(Registries.ITEM, "gems/" + gemName))
                .add(gemEntry.getValue().key());
            tag(Tags.Items.GEMS)
                .addTag(commonTagOf(Registries.ITEM, "gems/" + gemName));
            tag(commonTagOf(Registries.ITEM, "dusts/" + gemName))
                .add(TFCItems.ORE_POWDERS.get(ore).key());
            tag(Tags.Items.DUSTS)
                .addTag(commonTagOf(Registries.ITEM, "dusts/" + gemName));
        }

        //Glues

        tag(commonTagOf(Registries.ITEM, "glue"))
            .add(TFCItems.GLUE.key());

        //Grains

        tag(commonTagOf(Registries.ITEM, "grains")).addTags(
            commonTagOf(Registries.ITEM, "grains/barley"),
            commonTagOf(Registries.ITEM, "grains/maize"),
            commonTagOf(Registries.ITEM, "grains/oat"),
            commonTagOf(Registries.ITEM, "grains/rye"),
            commonTagOf(Registries.ITEM, "grains/rice"),
            commonTagOf(Registries.ITEM, "grains/wheat")
        );

        tag(commonTagOf(Registries.ITEM, "grains/barley"))
            .add(Food.BARLEY_GRAIN);
        tag(commonTagOf(Registries.ITEM, "grains/maize"))
            .add(Food.MAIZE_GRAIN);
        tag(commonTagOf(Registries.ITEM, "grains/oat"))
            .add(Food.OAT_GRAIN);
        tag(commonTagOf(Registries.ITEM, "grains/rye"))
            .add(Food.RYE_GRAIN);
        tag(commonTagOf(Registries.ITEM, "grains/rice"))
            .add(Food.RICE_GRAIN);
        tag(commonTagOf(Registries.ITEM, "grains/wheat"))
            .add(Food.WHEAT_GRAIN);

        //Lumber

        tag(commonTagOf(Registries.ITEM, "lumbers"))
            .addTag(LUMBER);

        //Metal Items

        for (Metal metal : Metal.values())
        {
            metalTag(metal, Metal.ItemType.INGOT, Tags.Items.INGOTS);
            if (metal.defaultParts())
            {
                metalTag(metal, Metal.ItemType.DOUBLE_INGOT, DOUBLE_INGOTS);
                metalTag(metal, Metal.ItemType.SHEET, PLATES); // changed to plates to match other mods usage
                metalTag(metal, Metal.ItemType.DOUBLE_SHEET, DOUBLE_PLATES);  //Changed to Double plates to match other mods usage
                metalTag(metal, Metal.ItemType.ROD, Tags.Items.RODS);
                metalTag(metal, Metal.ItemType.ROD, commonTagOf(Registries.ITEM, "rods/all_metal"));
                metalTag(metal, Metal.ItemType.SHEET, commonTagOf(Registries.ITEM, "sheets/" + metal.name().toLowerCase())); //For compatibility with old sheets tag
                metalTag(metal, Metal.ItemType.DOUBLE_SHEET, commonTagOf(Registries.ITEM, "double_sheets/" + metal.name().toLowerCase())); //For compatibility with old double_sheets tag
                //Incorrect tag usage, storage blocks are for blocks that convert back and forth between block and items
            }
        }

        //Minecart

        tag(MINECARTS)
            .add(Items.MINECART)
            .add(TFCItems.CHEST_MINECARTS);

        //Misc

        tag(commonTagOf(Registries.ITEM, "gears/brass"))
            .add(TFCItems.BRASS_MECHANISMS); //????

        tag(Tags.Items.STRINGS).add(TFCItems.WOOL_YARN);

        tag(Tags.Items.MUSIC_DISCS)
            .add(TFCItems.BLANK_DISC.key());

        tag(commonTagOf(Registries.ITEM, "straw"))
            .add(TFCItems.STRAW.key());

        //Molds

        tag(commonTagOf(Registries.ITEM, "molds")).addTags(
                commonTagOf(Registries.ITEM, "molds/fired"),
                commonTagOf(Registries.ITEM, "molds/unfired")
            );

        tag(commonTagOf(Registries.ITEM, "molds/fired"))
            .addTag(FIRED_MOLDS);
        tag(commonTagOf(Registries.ITEM, "molds/unfired"))
            .addTag(UNFIRED_MOLDS);

        //Raw Materials

        tag(Tags.Items.RAW_MATERIALS).addTags(
            commonTagOf(Registries.ITEM, "raw_materials/saltpeter"),
            commonTagOf(Registries.ITEM, "raw_materials/graphite"),
            commonTagOf(Registries.ITEM, "raw_materials/sylvite"),
            commonTagOf(Registries.ITEM, "raw_materials/salt"),
            commonTagOf(Registries.ITEM, "raw_materials/flux"),
            commonTagOf(Registries.ITEM, "raw_materials/sulfur"),
            commonTagOf(Registries.ITEM, "raw_materials/amethyst"),
            commonTagOf(Registries.ITEM, "raw_materials/diamond"),
            commonTagOf(Registries.ITEM, "raw_materials/emerald"),
            commonTagOf(Registries.ITEM, "raw_materials/lapis"),
            commonTagOf(Registries.ITEM, "raw_materials/opal"),
            commonTagOf(Registries.ITEM, "raw_materials/ruby"),
            commonTagOf(Registries.ITEM, "raw_Materials/topaz"),
            commonTagOf(Registries.ITEM, "raw_materials/redstone"),
            commonTagOf(Registries.ITEM, "raw_materials/plaster")
        );

        tag(commonTagOf(Registries.ITEM, "raw_materials/amethyst"))
            .add(TFCItems.ORES.get(Ore.AMETHYST).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/diamond"))
            .add(TFCItems.ORES.get(Ore.DIAMOND).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/emerald"))
            .add(TFCItems.ORES.get(Ore.EMERALD).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/lapis"))
            .add(TFCItems.ORES.get(Ore.LAPIS_LAZULI).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/opal"))
            .add(TFCItems.ORES.get(Ore.OPAL).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/ruby"))
            .add(TFCItems.ORES.get(Ore.RUBY).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/sapphire"))
            .add(TFCItems.ORES.get(Ore.SAPPHIRE).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/topaz"))
            .add(TFCItems.ORES.get(Ore.TOPAZ).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/redstone"))
            .add(TFCItems.ORES.get(Ore.CINNABAR).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/redstone"))
            .add(TFCItems.ORES.get(Ore.CRYOLITE).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/flux"))
            .addTag(FLUXSTONE);
        tag(commonTagOf(Registries.ITEM, "raw_materials/graphite"))
            .add(TFCItems.ORES.get(Ore.GRAPHITE).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/plaster"))
            .add(TFCItems.ORES.get(Ore.GYPSUM).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/saltpeter"))
            .add(TFCItems.ORES.get(Ore.SALTPETER).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/sulfur"))
            .add(TFCItems.ORES.get(Ore.SULFUR).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/sylvite"))
            .add(TFCItems.ORES.get(Ore.SYLVITE).key());
        tag(commonTagOf(Registries.ITEM, "raw_materials/salt"))
            .add(TFCItems.ORES.get(Ore.HALITE).key());

        for (var entry : TFCItems.GRADED_ORES.entrySet())
        {
            Ore ore = entry.getKey();
            var gradedOres = entry.getValue();
            Metal metal = ore.metal();
            for (var grade : Ore.Grade.values())
            {
                String tagName = "raw_materials/" + metal.name() + "/" + grade.name();
                tag(commonTagOf(Registries.ITEM, tagName))
                    .add(gradedOres.get(grade).key());
                tag(Tags.Items.RAW_MATERIALS).addTags(commonTagOf(Registries.ITEM, tagName));
            }
        }

        //Rods

        tag(Tags.Items.RODS_WOODEN).add(TFCBlocks.WOODS, Wood.BlockType.TWIG);

        //Seeds

        for (Crop crop : Crop.values())
        {
            tag(commonTagOf(Registries.ITEM, "seeds/" + crop.getSerializedName()))
                .add(TFCItems.CROP_SEEDS.get(crop).key());
            tag(Tags.Items.SEEDS)
                .addTag(commonTagOf(Registries.ITEM, "seeds/" + crop.getSerializedName()));
        }
        tag(commonTagOf(Registries.ITEM, "seeds/corn"))
            .add(TFCItems.CROP_SEEDS.get(Crop.MAIZE).key());
        tag(Tags.Items.SEEDS)
            .addTag(commonTagOf(Registries.ITEM, "seeds/corn"));
        tag(commonTagOf(Registries.ITEM, "seeds/beetroot"))
            .add(TFCItems.CROP_SEEDS.get(Crop.BEET).key());
        tag(Tags.Items.SEEDS)
            .addTag(commonTagOf(Registries.ITEM, "seeds/beetroot"));

        //Tools

        tag(Tags.Items.TOOLS).addTags(
            TOOLS_HAMMER,
            TOOLS_SAW,
            TOOLS_SCYTHE,
            TOOLS_PROPICK,
            TOOLS_KNIFE,
            TOOLS_CHISEL,
            TOOLS_GLASSWORKING,
            TOOLS_BLOWPIPE);

        tag(Tags.Items.TOOLS_SHIELD).add(TFCItems.METAL_ITEMS, Metal.ItemType.SHIELD);
        tag(Tags.Items.TOOLS_FISHING_ROD).add(TFCItems.METAL_ITEMS, Metal.ItemType.FISHING_ROD);
        tag(Tags.Items.TOOLS_SPEAR)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.JAVELIN)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.JAVELIN);
        tag(Tags.Items.TOOLS_SHEAR).add(TFCItems.METAL_ITEMS, Metal.ItemType.SHEARS);
        tag(Tags.Items.TOOLS_IGNITER)
            .add(TFCItems.FIRESTARTER)
            .add(TFCItems.FLINT_AND_PYRITE);
        tag(Tags.Items.TOOLS_MACE).add(TFCItems.METAL_ITEMS, Metal.ItemType.MACE);
        tag(Tags.Items.MINING_TOOL_TOOLS).add(TFCItems.METAL_ITEMS, Metal.ItemType.PICKAXE);
        tag(Tags.Items.RANGED_WEAPON_TOOLS).add(TFCItems.METAL_ITEMS, Metal.ItemType.JAVELIN).add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.JAVELIN);
        tag(Tags.Items.MELEE_WEAPON_TOOLS).add(TFCItems.METAL_ITEMS, Metal.ItemType.SWORD).add(TFCItems.METAL_ITEMS, Metal.ItemType.AXE).add(TFCItems.METAL_ITEMS, Metal.ItemType.MACE).add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.AXE);

        tag(TOOLS_HAMMER)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.HAMMER)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.HAMMER);
        tag(TOOLS_SAW).add(TFCItems.METAL_ITEMS, Metal.ItemType.SAW);
        tag(TOOLS_SCYTHE).add(TFCItems.METAL_ITEMS, Metal.ItemType.SCYTHE);
        tag(TOOLS_PROPICK).add(TFCItems.METAL_ITEMS, Metal.ItemType.PROPICK);
        tag(TOOLS_KNIFE)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.KNIFE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.KNIFE);
        tag(TOOLS_CHISEL).add(TFCItems.METAL_ITEMS, Metal.ItemType.CHISEL);
        tag(TOOLS_GLASSWORKING).add(TFCItems.PADDLE, TFCItems.JACKS, TFCItems.GEM_SAW);
        tag(TOOLS_BLOWPIPE).add(TFCItems.BLOWPIPE, TFCItems.CERAMIC_BLOWPIPE);
        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/sandpaper")))
            .add(TFCItems.SANDPAPER.key());
        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/spindle")))
            .add(TFCItems.SPINDLE.key());

        //Tool Heads

        tag(commonTagOf(Registries.ITEM, "tool_heads")).addTags(
            commonTagOf(Registries.ITEM, "tool_heads/pickaxe"),
            commonTagOf(Registries.ITEM, "tool_heads/axe"),
            commonTagOf(Registries.ITEM, "tool_heads/sword"),
            commonTagOf(Registries.ITEM, "tool_heads/shovel"),
            commonTagOf(Registries.ITEM, "tool_heads/hoe"),
            commonTagOf(Registries.ITEM, "tool_heads/hammer"),
            commonTagOf(Registries.ITEM, "tool_heads/scythe"),
            commonTagOf(Registries.ITEM, "tool_heads/propick"),
            commonTagOf(Registries.ITEM, "tool_heads/saw"),
            commonTagOf(Registries.ITEM, "tool_heads/mace"),
            commonTagOf(Registries.ITEM, "tool_heads/javelin"),
            commonTagOf(Registries.ITEM, "tool_heads/knife"),
            commonTagOf(Registries.ITEM, "tool_heads/chisel"),
            commonTagOf(Registries.ITEM, "tool_heads/spindle"),
            commonTagOf(Registries.ITEM, "tool_heads/fishing_hook")
            );

        tag(commonTagOf(Registries.ITEM, "tool_heads/pickaxe"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.PICKAXE_HEAD);
        tag(commonTagOf(Registries.ITEM, "tool_heads/axe"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.AXE_HEAD)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.AXE_HEAD);
        tag(commonTagOf(Registries.ITEM, "tool_heads/sword"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.SWORD_BLADE);
        tag(commonTagOf(Registries.ITEM, "tool_heads/shovel"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.SHOVEL_HEAD)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.SHOVEL_HEAD);
        tag(commonTagOf(Registries.ITEM, "tool_heads/hoe"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.HOE_HEAD)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.HOE_HEAD);
        tag(commonTagOf(Registries.ITEM, "tool_heads/hammer"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.HAMMER_HEAD)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.HAMMER_HEAD);
        tag(commonTagOf(Registries.ITEM, "tool_heads/scythe"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.SCYTHE_BLADE);
        tag(commonTagOf(Registries.ITEM, "tool_heads/propick"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.PROPICK_HEAD);
        tag(commonTagOf(Registries.ITEM, "tool_heads/saw"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.SAW_BLADE);
        tag(commonTagOf(Registries.ITEM, "tool_heads/mace"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.MACE_HEAD);
        tag(commonTagOf(Registries.ITEM, "tool_heads/javelin"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.JAVELIN_HEAD)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.JAVELIN_HEAD);
        tag(commonTagOf(Registries.ITEM, "tool_heads/knife"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.KNIFE_BLADE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.KNIFE_HEAD);
        tag(commonTagOf(Registries.ITEM, "tool_heads/chisel"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.CHISEL_HEAD);
        tag(commonTagOf(Registries.ITEM, "tool_heads/spindle"))
            .add(TFCItems.SPINDLE_HEAD.key());
        tag(commonTagOf(Registries.ITEM, "tool_heads/fishing_hook"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.FISH_HOOK);

        // ===== TFC Tags ===== //

        tag(DOUBLE_SHEETS_ANY_BRONZE).addTags(
            commonTagOf(Metal.BRONZE, Metal.ItemType.DOUBLE_SHEET),
            commonTagOf(Metal.BISMUTH_BRONZE, Metal.ItemType.DOUBLE_SHEET),
            commonTagOf(Metal.BLACK_BRONZE, Metal.ItemType.DOUBLE_SHEET));

        tag(JAM).add(TFCItems.JAM);
        tag(PRESERVES).add(TFCItems.UNSEALED_FRUIT_PRESERVES);
        tag(SEALED_PRESERVES).add(TFCItems.FRUIT_PRESERVES);
        tag(EMPTY_JARS)
            .add(
                TFCItems.EMPTY_JAR,
                TFCItems.EMPTY_JAR_WITH_LID
            );
        tag(FILLED_JARS).addTags(SEALED_PRESERVES, PRESERVES);
        tag(EMPTY_JARS_WITH_LID).add(TFCItems.EMPTY_JAR_WITH_LID);
        tag(JARS).addTags(EMPTY_JARS, FILLED_JARS);
        tag(SWEETENERS).add(Items.SUGAR);
        tag(BOWLS).add(Items.BOWL, TFCBlocks.CERAMIC_BOWL);
        tag(SALAD_BOWLS).addTag(BOWLS);
        tag(SOUP_BOWLS).addTag(BOWLS);
        tag(USABLE_IN_SALAD)
            .addTags(FRUITS, VEGETABLES, COOKED_MEATS, COOKED_FISH);
        tag(USABLE_IN_SOUP)
            .addTags(FRUITS, VEGETABLES, RAW_MEATS, COOKED_MEATS, RAW_FISH, COOKED_FISH, COOKED_FISH)
            .add(Food.COOKED_RICE);
        tag(USABLE_IN_SANDWICH).addTags(VEGETABLES, COOKED_MEATS, COOKED_FISH, DAIRY);
        tag(USABLE_IN_JAM_SANDWICH).addTags(COOKED_MEATS, COOKED_FISH, DAIRY, PRESERVES, commonTagOf(Registries.ITEM, "foods/jam"));
        tag(CAN_BE_SALTED).addTags(RAW_MEATS, COOKED_MEATS, RAW_FISH, COOKED_FISH);
        tag(PIG_FOOD).addTag(FOODS);
        tag(COW_FOOD).addTags(GRAINS, commonTagOf(Registries.ITEM, "grains"));
        tag(YAK_FOOD).addTags(GRAINS, commonTagOf(Registries.ITEM, "grains"));
        tag(GOAT_FOOD).addTags(GRAINS, FRUITS, VEGETABLES, commonTagOf(Registries.ITEM, "grains"));
        tag(ALPACA_FOOD).addTags(GRAINS, FRUITS, commonTagOf(Registries.ITEM, "grains"));
        tag(SHEEP_FOOD).addTags(GRAINS, commonTagOf(Registries.ITEM, "grains"));
        tag(MUSK_OX_FOOD).addTags(GRAINS, commonTagOf(Registries.ITEM, "grains"));
        tag(CHICKEN_FOOD).addTags(GRAINS, FRUITS, VEGETABLES, Tags.Items.SEEDS, BREAD, commonTagOf(Registries.ITEM, "grains"));
        tag(DUCK_FOOD).addTag(CHICKEN_FOOD);
        tag(QUAIL_FOOD).addTag(CHICKEN_FOOD);
        tag(DONKEY_FOOD).addTag(HORSE_FOOD);
        tag(MULE_FOOD).addTag(HORSE_FOOD);
        tag(HORSE_FOOD).addTags(GRAINS, FRUITS, commonTagOf(Registries.ITEM, "grains"));
        tag(CAT_FOOD).addTags(GRAINS, COOKED_MEATS, DAIRY, COOKED_FISH, commonTagOf(Registries.ITEM, "grains"));
        tag(OCELOT_FOOD).addTags(RAW_FISH);
        tag(DOG_FOOD).addTags(RAW_MEATS, COOKED_MEATS, RAW_FISH, RAW_MEATS);
        tag(PENGUIN_FOOD).addTags(RAW_FISH);
        tag(SEAL_FOOD).addTags(RAW_FISH);
        tag(TURTLE_FOOD).add(TFCItems.FOOD.get(Food.DRIED_KELP), TFCItems.FOOD.get(Food.DRIED_SEAWEED));
        tag(FROG_FOOD).addTag(RAW_FISH).add(Items.SPIDER_EYE);
        tag(RABBIT_FOOD).addTags(GRAINS, VEGETABLES, commonTagOf(Registries.ITEM, "grains"));
        tag(ItemTags.PANDA_FOOD).addTag(BAMBOO);

        // Greens and Browns intentionally overlap - we check browns first, then greens, to resolve
        tag(COMPOST_GREENS).addTags(COMPOST_GREENS_LOW, COMPOST_GREENS_MEDIUM, COMPOST_GREENS_HIGH);
        tag(COMPOST_GREENS_LOW)
            .addOnly(TFCBlocks.PLANTS, Plant::givesGreenCompost)
            .add(TFCItems.ALFALFA);
        tag(COMPOST_GREENS_MEDIUM).addTag(GRAINS);
        tag(COMPOST_GREENS_HIGH).addTags(VEGETABLES, FRUITS).add(TFCBlocks.PUMPKIN, TFCBlocks.MELON);
        tag(COMPOST_BROWNS).addTags(COMPOST_BROWNS_LOW, COMPOST_BROWNS_MEDIUM, COMPOST_BROWNS_HIGH);
        tag(COMPOST_BROWNS_LOW)
            .addTag(ItemTags.LEAVES)
            .addOnly(TFCBlocks.PLANTS, Plant::givesBrownCompost)
            .add(
                Items.HANGING_ROOTS,
                TFCItems.CANOLA);
        tag(COMPOST_BROWNS_MEDIUM).add(
            TFCItems.POWDERS.get(Powder.WOOD_ASH),
            TFCItems.JUTE);
        tag(COMPOST_BROWNS_HIGH).add(
            Items.PAPER,
            TFCItems.JUTE_FIBER,
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.HUMUS),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.DRIFTWOOD),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.PINECONE));
        tag(COMPOST_POISONS).add(
            Items.BONE,
            Items.BONE_MEAL,
            Items.BONE_BLOCK
        ).addTags(RAW_MEATS, RAW_FISH, COOKED_FISH, COOKED_MEATS);

        tag(SMALL_FISHING_BAIT)
            .addTag(Tags.Items.SEEDS)
            .add(Food.SHELLFISH);
        tag(LARGE_FISHING_BAIT)
            .add(Food.COD, Food.SALMON, Food.TROPICAL_FISH, Food.BLUEGILL);
        tag(HOLDS_SMALL_FISHING_BAIT)
            .addTag(HOLDS_LARGE_FISHING_BAIT).add(
            TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.FISHING_ROD),
            TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.FISHING_ROD),
            TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.FISHING_ROD),
            TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.FISHING_ROD));
        tag(HOLDS_LARGE_FISHING_BAIT)
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
        tag(COLORED_TERRACOTTA).addAllColors("terracotta");
        tag(COLORED_GLAZED_TERRACOTTA).addAllColors("glazed_terracotta");
        tag(COLORED_SHULKER_BOXES).addAllColors("shulker_box");
        tag(COLORED_CONCRETE_POWDER).addAllColors("concrete_powder");
        tag(COLORED_CANDLES).add(TFCBlocks.DYED_CANDLE);
        tag(COLORED_WINDMILL_BLADES)
            .addOnly(TFCItems.WINDMILL_BLADES, c -> c != DyeColor.WHITE);
        tag(COLORED_RAW_ALABASTER).add(TFCBlocks.RAW_ALABASTER);
        tag(COLORED_ALABASTER_BRICKS).add(TFCBlocks.ALABASTER_BRICKS);
        tag(COLORED_POLISHED_ALABASTER).add(TFCBlocks.POLISHED_ALABASTER);
        tag(COLORED_VESSELS).add(TFCItems.UNFIRED_GLAZED_VESSELS);
        tag(COLORED_LARGE_VESSELS).add(TFCItems.UNFIRED_GLAZED_LARGE_VESSELS);
        tag(UNFIRED_POTTERY)
            .add(TFCItems.UNFIRED_GLAZED_VESSELS)
            .add(TFCItems.UNFIRED_MOLDS)
            .add(TFCItems.UNFIRED_BLOWPIPE)
            .add(TFCItems.UNFIRED_BOWL)
            .add(TFCItems.UNFIRED_BRICK)
            .add(TFCItems.UNFIRED_BELL_MOLD)
            .add(TFCItems.UNFIRED_FIRE_BRICK)
            .add(TFCItems.UNFIRED_FIRE_INGOT_MOLD)
            .add(TFCItems.UNFIRED_FLOWER_POT)
            .add(TFCItems.UNFIRED_GLAZED_LARGE_VESSELS)
            .add(TFCItems.UNFIRED_JUG)
            .add(TFCItems.UNFIRED_CRUCIBLE)
            .add(TFCItems.UNFIRED_PAN)
            .add(TFCItems.UNFIRED_POT)
            .add(TFCItems.UNFIRED_SPINDLE_HEAD)
            .add(TFCItems.UNFIRED_VESSEL)
            .add(TFCItems.UNFIRED_LARGE_VESSEL)
            .add(TFCItems.UNFIRED_CHANNEL)
            .add(TFCItems.UNFIRED_MOLD_TABLE);

        tag(TOOL_RACKS).add(TFCBlocks.WOODS, Wood.BlockType.TOOL_RACK);
        tag(SCRIBING_TABLES).add(TFCBlocks.WOODS, Wood.BlockType.SCRIBING_TABLE);
        tag(SEWING_TABLES).add(TFCBlocks.WOODS, Wood.BlockType.SEWING_TABLE);
        tag(SLUICES).add(TFCBlocks.WOODS, Wood.BlockType.SLUICE);
        tag(LOOMS).add(TFCBlocks.WOODS, Wood.BlockType.LOOM);
        tag(BARRELS).add(TFCBlocks.WOODS, Wood.BlockType.BARREL);
        tag(TWIGS).add(TFCBlocks.WOODS, Wood.BlockType.TWIG);

        copy(TFCTags.Blocks.LAMPS, LAMPS);

        tag(ORE_PIECES)
            .add(TFCItems.ORES)
            .addAll(TFCItems.GRADED_ORES);
        tag(SMALL_ORE_PIECES)
            .add(TFCBlocks.SMALL_ORES);
        tag(ItemTags.TRIM_MATERIALS)
            .add(TFCItems.GEMS)
            .add(TFCItems.METAL_ITEMS.get(Metal.SILVER).get(Metal.ItemType.INGOT))
            .add(TFCItems.METAL_ITEMS.get(Metal.STERLING_SILVER).get(Metal.ItemType.INGOT))
            .add(TFCItems.METAL_ITEMS.get(Metal.GOLD).get(Metal.ItemType.INGOT))
            .add(TFCItems.METAL_ITEMS.get(Metal.ROSE_GOLD).get(Metal.ItemType.INGOT))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH).get(Metal.ItemType.INGOT));
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
        tag(FLUXSTONE)
            .add(TFCItems.FOOD.get(Food.SHELLFISH))
            .add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.MOLLUSK))
            .add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.CLAM))
            .add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.MUSSEL))
            .add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.SEA_URCHIN))
            .add(Items.TURTLE_SCUTE)
            .add(Items.ARMADILLO_SCUTE)
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.LIMESTONE).get(Rock.BlockType.LOOSE))
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.LIMESTONE).get(Rock.BlockType.MOSSY_LOOSE))
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.DOLOMITE).get(Rock.BlockType.LOOSE))
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.DOLOMITE).get(Rock.BlockType.MOSSY_LOOSE))
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.CHALK).get(Rock.BlockType.LOOSE))
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.CHALK).get(Rock.BlockType.MOSSY_LOOSE))
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.MARBLE).get(Rock.BlockType.LOOSE))
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.MARBLE).get(Rock.BlockType.MOSSY_LOOSE))
            .add(TFCBlocks.PLANTS.get(Plant.MUSSELS).get())
            .add(TFCBlocks.PLANTS.get(Plant.BARNACLES).get());
        tag(METAL_PLATED_BLOCKS)
            .add(TFCBlocks.METALS, Metal.BlockType.BLOCK);


        tag(TOOLS_SHARP).addTags(
            ItemTags.HOES,
            TOOLS_KNIFE,
            TOOLS_SCYTHE);
        tag(SCRAPED_HIDES)
            .add(TFCItems.HIDES.get(HideItemType.SCRAPED));
        tag(TOOLS_STONE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.HAMMER)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.AXE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.HOE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.JAVELIN)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.KNIFE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.SHOVEL);
        tag(TOOLS_COPPER)
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.TUYERE))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.FISHING_ROD))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.PICKAXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.PROPICK))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.AXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.SHOVEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.HOE))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.CHISEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.HAMMER))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.SAW))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.JAVELIN))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.SWORD))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.MACE))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.KNIFE))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.SCYTHE))
            .add(TFCItems.METAL_ITEMS.get(Metal.COPPER).get(Metal.ItemType.SHEARS));
        tag(TOOLS_BRONZE)
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.TUYERE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.FISHING_ROD))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.PICKAXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.PROPICK))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.AXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.SHOVEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.HOE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.CHISEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.HAMMER))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.SAW))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.JAVELIN))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.SWORD))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.MACE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.KNIFE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.SCYTHE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.SHEARS));
        tag(TOOLS_BISMUTH_BRONZE)
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.TUYERE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.FISHING_ROD))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.PICKAXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.PROPICK))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.AXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.SHOVEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.HOE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.CHISEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.HAMMER))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.SAW))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.JAVELIN))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.SWORD))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.MACE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.KNIFE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.SCYTHE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BISMUTH_BRONZE).get(Metal.ItemType.SHEARS));
        tag(TOOLS_BLACK_BRONZE)
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.TUYERE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.FISHING_ROD))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.PICKAXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.PROPICK))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.AXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.SHOVEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.HOE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.CHISEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.HAMMER))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.SAW))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.JAVELIN))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.SWORD))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.MACE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.KNIFE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.SCYTHE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.SHEARS));
        tag(TOOLS_WROUGHT_IRON)
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.TUYERE))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.FISHING_ROD))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.PICKAXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.PROPICK))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.AXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.SHOVEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.HOE))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.CHISEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.HAMMER))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.SAW))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.JAVELIN))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.SWORD))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.MACE))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.KNIFE))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.SCYTHE))
            .add(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.SHEARS));
        tag(TOOLS_STEEL)
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.TUYERE))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.FISHING_ROD))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.PICKAXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.PROPICK))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.AXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.SHOVEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.HOE))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.CHISEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.HAMMER))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.SAW))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.JAVELIN))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.SWORD))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.MACE))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.KNIFE))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.SCYTHE))
            .add(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.SHEARS));
        tag(TOOLS_BLACK_STEEL)
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.TUYERE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.FISHING_ROD))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.PICKAXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.PROPICK))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.AXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.SHOVEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.HOE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.CHISEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.HAMMER))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.SAW))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.JAVELIN))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.SWORD))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.MACE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.KNIFE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.SCYTHE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLACK_STEEL).get(Metal.ItemType.SHEARS));
        tag(TOOLS_RED_STEEL)
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.TUYERE))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.FISHING_ROD))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.PICKAXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.PROPICK))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.AXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.SHOVEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.HOE))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.CHISEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.HAMMER))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.SAW))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.JAVELIN))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.SWORD))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.MACE))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.KNIFE))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.SCYTHE))
            .add(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.SHEARS));
        tag(TOOLS_BLUE_STEEL)
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.TUYERE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.FISHING_ROD))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.PICKAXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.PROPICK))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.AXE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.SHOVEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.HOE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.CHISEL))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.HAMMER))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.SAW))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.JAVELIN))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.SWORD))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.MACE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.KNIFE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.SCYTHE))
            .add(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.SHEARS));

        tag(METAL_ORES)
            .addAll(TFCItems.GRADED_ORES);

        tag(MUD_BRICK_ITEMS)
            .addTag(MUD_BRICKS)
            .add(TFCItems.ENTISOL_MUD_BRICK)
            .add(TFCItems.ALFISOL_MUD_BRICK)
            .add(TFCItems.PODZOL_MUD_BRICK)
            .add(TFCItems.ANDISOL_MUD_BRICK)
            .add(TFCItems.FLUVISOL_MUD_BRICK)
            .add(TFCItems.ARIDISOL_MUD_BRICK)
            .add(TFCItems.OXISOL_MUD_BRICK)
            .add(TFCItems.MOLLISOL_MUD_BRICK);

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
        tag(GLASS_POWDERS).add(GlassOperation.POWDERS.get().keySet().stream().sorted(Comparator.comparing(item -> Item.getId(item)))); // Sorted to make generation deterministic
        tag(GLASS_BOTTLES).add(
            TFCItems.SILICA_GLASS_BOTTLE,
            TFCItems.HEMATITIC_GLASS_BOTTLE,
            TFCItems.OLIVINE_GLASS_BOTTLE,
            TFCItems.VOLCANIC_GLASS_BOTTLE);
        tag(GLASS_POTASH)
            .add(TFCItems.POWDERS.get(Powder.SODA_ASH), TFCItems.ORE_POWDERS.get(Ore.SALTPETER))
            .addTags(commonTagOf(Registries.ITEM, "soda_ash"), commonTagOf(Registries.ITEM, "saltpeter"), commonTagOf(Registries.ITEM, "potash"));

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

        tag(ItemTags.LOGS_THAT_BURN).add(TFCItems.STICK_BUNDLE, TFCItems.DRIED_CACTUS_WOOD);
        tag(FIREPIT_KINDLING)
            .addTags(ItemTags.LEAVES, BOOKS)
            .add(TFCItems.STRAW, Items.PAPER);
        tag(FIREPIT_STICKS).addTag(Tags.Items.RODS_WOODEN);
        tag(FIREPIT_LOGS).addTag(ItemTags.LOGS_THAT_BURN);
        tag(LOG_PILE_LOGS).addTag(ItemTags.LOGS).add(TFCItems.STICK_BUNDLE, TFCItems.DRIED_CACTUS_WOOD, TFCItems.CACTUS_WOOD);
        tag(PIT_KILN_STRAW).add(TFCItems.STRAW);
        tag(PIT_KILN_4_STRAW).add(TFCBlocks.THATCH);
        tag(PIT_KILN_LOGS).addTags(ItemTags.LOGS_THAT_BURN);
        tag(INEFFICIENT_LOGGING_AXES).add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.AXE);
        tag(CAN_BE_LIT_ON_TORCH).addTag(Tags.Items.RODS_WOODEN);
        tag(ROCK_KNAPPING).addTag(STONES_LOOSE);
        tag(CLAY_KNAPPING).add(Items.CLAY_BALL);
        tag(FIRE_CLAY_KNAPPING).add(TFCItems.FIRE_CLAY);
        tag(LEATHER_KNAPPING).add(Items.LEATHER);
        tag(GOAT_HORN_KNAPPING).add(TFCItems.GOAT_HORN);
        tag(QUERN_HANDSTONES).add(TFCItems.HANDSTONE);
        tag(SCRIBING_INK).add(Items.BLACK_DYE);
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
        tag(ItemTags.COALS).add(TFCItems.ORES.get(Ore.BITUMINOUS_COAL), TFCItems.ORES.get(Ore.LIGNITE));
        tag(FORGE_FUEL).addTag(ItemTags.COALS);
        tag(BLAST_FURNACE_FUEL).add(Items.CHARCOAL);
        tag(BLAST_FURNACE_TUYERES).add(TFCItems.METAL_ITEMS, Metal.ItemType.TUYERE);
        tag(TOOL_RACK_TOOLS)
            .addTags(Tags.Items.TOOLS, SEWING_NEEDLES)
            .add(TFCItems.SANDPAPER, Items.SPYGLASS);
        tag(POWDER_KEG_FUEL).add(Items.GUNPOWDER);
        tag(USABLE_IN_MOLD_TABLE).addTag(FIRED_MOLDS);
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
        copy(TFCTags.Blocks.COARSE_DIRT, COARSE_DIRT);
        copy(TFCTags.Blocks.MUD, MUD);
        copy(TFCTags.Blocks.MUD_BRICKS, MUD_BRICKS);

        copy(TFCTags.Blocks.ANVILS, ANVILS);
        copy(TFCTags.Blocks.WORKBENCHES, WORKBENCHES);
        copy(TFCTags.Blocks.AQUEDUCTS, AQUEDUCTS);

        copy(TFCTags.Blocks.FALLEN_LEAVES, FALLEN_LEAVES);
        copy(TFCTags.Blocks.CLAY_INDICATORS, CLAY_INDICATORS);
        copy(TFCTags.Blocks.BAMBOO, BAMBOO);
    }

    @Override
    protected ItemTagAppender tag(TagKey<Item> tag)
    {
        return new ItemTagAppender(getOrCreateRawBuilder(tag));
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

    private void metalTag(Metal metal, Metal.ItemType type, TagKey<Item> baseTag)
    {
        final TagKey<Item> commonTag = commonTagOf(metal, type);
        tag(commonTag).add(TFCItems.METAL_ITEMS.get(metal).get(type));
        tag(baseTag).addTag(commonTag);
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
        ItemTagAppender(TagBuilder builder)
        {
            super(builder);
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

        ItemTagAppender addAllColors(String itemName)
        {
            for (DyeColor c : Helpers.DYE_COLORS) add(itemOf(ResourceLocation.withDefaultNamespace(c.getSerializedName() + "_" + itemName)));
            return this;
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
