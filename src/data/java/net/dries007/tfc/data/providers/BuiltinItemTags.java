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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.internal.NeoForgeItemTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.OreDeposit;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.component.food.Nutrient;
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

        final Function<String, TagKey<Item>> c = path -> commonTagOf(Registries.ITEM, path);

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
        tag(ItemTags.SWORDS)
                .add(TFCItems.METAL_ITEMS, Metal.ItemType.SWORD)
            .add(TFCItems.OBSIDIAN_MACUAHUITL);
        tag(ItemTags.AXES)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.AXE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.AXE)
            .add(TFCItems.OBSIDIAN_AXE);
        tag(ItemTags.HOES)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.HOE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.HOE)
            .add(TFCItems.OBSIDIAN_HOE);
        tag(ItemTags.PICKAXES).add(TFCItems.METAL_ITEMS, Metal.ItemType.PICKAXE);
        tag(ItemTags.SHOVELS)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.SHOVEL)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.SHOVEL)
            .add(TFCItems.OBSIDIAN_SHOVEL);

        // ===== Common Tags ===== //

        //Copy Common Tags Block => Item

        //Anvils
        for (Metal metal : Metal.values())
        {
            if (metal.allParts())
            {
                copyCommon("anvils/" + metal.getSerializedName());
            }
        }

        copyCommon("anvils/stone");
        copyCommon("anvils");

        //Barrels

        copyCommon("barrels/wooden");
        copyCommon("barrels");

        // Bars

        for (Metal metal : Metal.values())
        {
            if (metal.allParts())
            {
                copyCommon("bars/" + metal.getSerializedName());
            }
        }

        copyCommon("bars");

        // Bookshelves

        copyCommon("bookshelves/wooden");
        copyCommon("bookshelves");

        // Brick Blocks

        final TagKey<Item> brickBlocksTag = c.apply("brick_blocks");

        for (Rock rock : Rock.values())
        {
            final String rockName = rock.getSerializedName();
            final TagKey<Item> rockBrickBlocksTag = c.apply("brick_blocks/" + rockName);

            copyCommon(
                "bricks/" + rockName,
                "brick_blocks/" + rockName
            );

            tag(brickBlocksTag)
                .addTag(rockBrickBlocksTag);
        }

        copyCommon("bricks/chiseled", "brick_blocks/chiseled");
        copyCommon("bricks/cracked", "brick_blocks/cracked");
        copyCommon("bricks/fire", "brick_blocks/fire");
        copyCommon("bricks/mossy", "brick_blocks/mossy");
        copyCommon("bricks/mud", "brick_blocks/mud");
        copyCommon("bricks/plaster", "brick_blocks/plaster");

        tag(brickBlocksTag).addTags(
            c.apply("brick_blocks/chiseled"),
            c.apply("brick_blocks/cracked"),
            c.apply("brick_blocks/fire"),
            c.apply("brick_blocks/mossy"),
            c.apply("brick_blocks/mud"),
            c.apply("brick_blocks/plaster")
        );

        // Buttons

        copyCommon("buttons/stone");
        copyCommon("buttons/wooden");
        copyCommon("buttons");

        // Chains

        for (Metal metal : Metal.values())
        {
            if (metal.allParts())
            {
                copyCommon("chains/" + metal.getSerializedName());
            }
        }

        copyCommon("chains");

        // Clays

        copyCommon("clays/fire");
        copyCommon("clays/hardened");
        copyCommon("clays/kaolin");
        copyCommon("clays/normal");
        copyCommon("clays");

        // Cobblestones

        for (Rock rock : Rock.values())
        {
            copyCommon("cobblestones/" + rock.getSerializedName());
        }

        copyCommon("cobblestones/mossy");
        copyCommon("cobblestones");

        // Corals

        copyCommon("corals/plant");
        copyCommon("corals/block");

        // Crates

        copyCommon("crates/wooden");
        copyCommon("crates");

        // Crops

        copyCommon("crops");

        // Doors

        copyCommon("doors/iron");
        copyCommon("doors/wooden");
        copyCommon("doors");

        // Flowers

        copyCommon("flowers/small");
        copyCommon("flowers/tall");
        copyCommon("flowers");

        // Grates

        for (Metal metal : Metal.values())
        {
            if (metal.allParts())
            {
                copyCommon("grates/" + metal.getSerializedName());
            }
        }

        copyCommon("grates/normal");
        copyCommon("grates/exposed");
        copyCommon("grates/oxidized");
        copyCommon("grates/weathered");
        copyCommon("grates");

        // Gravels

        for (Rock rock : Rock.values())
        {
            copyCommon("gravels/" + rock.getSerializedName());
        }

        copyCommon("gravels");

        //Ice

        copyCommon("ice");

        // Icicle

        copyCommon("icicle");

        // Magma

        copyCommon("magma");

        // Lamps

        for (Metal metal : Metal.values())
        {
            if (metal.allParts())
            {
                copyCommon("lamps/" + metal.getSerializedName());
            }
        }

        copyCommon("lamps");

        // Leaves

        copyCommon("leaves");

        // Logs

        for (Wood wood : Wood.VALUES)
        {
            copyCommon("logs/" + wood.getSerializedName());
        }

        copyCommon("logs");

        // Peat

        copyCommon("peat");

        // Pipes

        copyCommon("pipes/fluid");
        copyCommon("pipes");

        // Planks

        for (Wood wood : Wood.VALUES)
        {
            copyCommon("planks/" + wood.getSerializedName());
        }

        copyCommon("planks");

        // Player Workstations

        copyCommon("player_workstations/anvil");
        copyCommon("player_workstations/blast_furnace");
        copyCommon("player_workstations/bloomery");
        copyCommon("player_workstations/composter");
        copyCommon("player_workstations/crucible");
        copyCommon("player_workstations/firebox");
        copyCommon("player_workstations/firepit");
        copyCommon("player_workstations/grill");
        copyCommon("player_workstations/lectern");
        copyCommon("player_workstations/loom");
        copyCommon("player_workstations/mold_table");
        copyCommon("player_workstations/nest_box");
        copyCommon("player_workstations/quern");
        copyCommon("player_workstations/scribing_table");
        copyCommon("player_workstations/sewing_table");
        copyCommon("player_workstations/stove");

        // Pressure Plates

        copyCommon("pressure_plates/stone");
        copyCommon("pressure_plates/wooden");
        copyCommon("pressure_plates");

        // Rods

        copyCommon("rods/wooden");
        copyCommon("rods");

        // Sands

        copyCommon("sands/hematitic");
        copyCommon("sands/olivine");
        copyCommon("sands/silica");
        copyCommon("sands/volcanic");

        for (SandBlockType sand : SandBlockType.values())
        {
            copyCommon(
                "sands/" + sand.name().toLowerCase(Locale.ROOT)
            );
        }

        // Sandstone

        copyCommon("sandstone/walls");

        for (SandBlockType sand : SandBlockType.values())
        {
            final String sandName =
                sand.name().toLowerCase(Locale.ROOT);

            copyCommon("sandstone/" + sandName + "_blocks");
            copyCommon("sandstone/" + sandName + "_slabs");
            copyCommon("sandstone/" + sandName + "_stairs");
            copyCommon("sandstone/" + sandName + "_walls");
        }

        copyCommon("sandstone");

        copyCommon("sands");

        // Soils

        for (SoilBlockType.Variant soil : SoilBlockType.Variant.values())
        {
            copyCommon(
                "soils/" + soil.name().toLowerCase(Locale.ROOT)
            );
        }

        copyCommon("soils");

        // Stones

        for (Rock rock : Rock.values())
        {
            copyCommon(
                "stones/" + rock.getSerializedName()
            );
        }

        copyCommon("stones/hardened");
        copyCommon("stones/loose");
        copyCommon("stones/mossy");
        copyCommon("stones/raw");
        copyCommon("stones/smooth");
        copyCommon("stones/spike");

        copyCommon("stones");

        // Saplings

        for (Wood wood : Wood.VALUES)
        {
            copyCommon("saplings/" + wood.getSerializedName());
        }

        copyCommon("saplings");

        // Shelves

        copyCommon("shelves/brick");
        copyCommon("shelves/wooden");
        copyCommon("shelves");

        // Sluices

        copyCommon("sluices/wooden");
        copyCommon("sluices");

        // Storage Blocks

        copyCommon("storage_blocks/glue");
        copyCommon("storage_blocks/thatch");
        copyCommon("storage_blocks");

        // Stripped Logs

        for (Wood wood : Wood.VALUES)
        {
            copyCommon(
                "stripped_logs/" + wood.getSerializedName()
            );
        }

        copyCommon("stripped_logs");

        // Stripped Wood

        for (Wood wood : Wood.VALUES)
        {
            copyCommon(
                "stripped_woods/" + wood.getSerializedName()
            );
        }

        copyCommon("stripped_woods");

        // Supports

        final TagKey<Item> supportsTag = c.apply("supports");
        final TagKey<Item> woodenSupportsTag = c.apply("supports/wooden");

        tag(woodenSupportsTag)
            .add(TFCItems.SUPPORTS);

        tag(supportsTag)
            .addTag(woodenSupportsTag);

        // Thatch

        copyCommon("thatch");

        // Tool Racks

        copyCommon("tool_racks/wooden");
        copyCommon("tool_racks");

        // Trapdoors

        copyCommon("trapdoors/wooden");
        copyCommon("trapdoors/metal");
        copyCommon("trapdoors");

        // Wattle

        copyCommon("wattle");

        // Woods

        for (Wood wood : Wood.VALUES)
        {
            copyCommon(
                "woods/" + wood.getSerializedName()
            );
        }

        copyCommon("woods");
        //-----Block Item Tags------//

        // Candles

        final TagKey<Item> candlesTag = c.apply("candles");

        TFCBlocks.DYED_CANDLE.forEach((color, candle) -> {
            final TagKey<Item> coloredCandlesTag =
                c.apply("candles/" + color.getSerializedName());

            tag(coloredCandlesTag)
                .add(candle);

            tag(candlesTag)
                .addTag(coloredCandlesTag);

            tag(c.apply("dyed/" + color.getSerializedName()))
                .add(candle);
        });

        tag(candlesTag)
            .add(TFCBlocks.CANDLE);

        //Corals

        tag(c.apply("corals/living"))
            .add(
                Blocks.BRAIN_CORAL_BLOCK,
                Blocks.BUBBLE_CORAL_BLOCK,
                Blocks.FIRE_CORAL_BLOCK,
                Blocks.HORN_CORAL_BLOCK,
                Blocks.TUBE_CORAL_BLOCK
            )
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL);

        tag(c.apply("corals/dead"))
            .add(
                Blocks.DEAD_BRAIN_CORAL_BLOCK,
                Blocks.DEAD_BUBBLE_CORAL_BLOCK,
                Blocks.DEAD_FIRE_CORAL_BLOCK,
                Blocks.DEAD_HORN_CORAL_BLOCK,
                Blocks.DEAD_TUBE_CORAL_BLOCK
            )
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL);

        tag(c.apply("corals")).addTags(
            c.apply("corals/plant"),
            c.apply("corals/block"),
            c.apply("corals/living"),
            c.apply("corals/dead")
        );

        //Crops

        TFCBlocks.FRUIT_TREE_LEAVES.forEach((crop, leaves) -> {
            final TagKey<Item> cropTag =
                c.apply("crops/" + crop.getSerializedName());

            tag(cropTag).add(
                leaves,
                TFCBlocks.FRUIT_TREE_SAPLINGS.get(crop)
            );
        });

        TFCBlocks.SPREADING_BUSHES.forEach((crop, bush) -> {
            final TagKey<Item> cropTag =
                c.apply("crops/" + crop.name().toLowerCase(Locale.ROOT));

            tag(cropTag)
                .add(bush);
        });

        TFCBlocks.STATIONARY_BUSHES.forEach((crop, bush) -> {
            final TagKey<Item> cropTag =
                c.apply("crops/" + crop.name().toLowerCase(Locale.ROOT));

            tag(cropTag)
                .add(bush);
        });

        tag(c.apply("crops/cranberry"))
            .add(TFCBlocks.CRANBERRY_BUSH);

        tag(c.apply("crops/apple")).addTags(
            c.apply("crops/green_apple"),
            c.apply("crops/red_apple")
        );

        tag(c.apply("crops/banana"))
            .add(TFCBlocks.BANANA_SAPLING);

        tag(c.apply("crops/melon"))
            .add(
                TFCBlocks.MELON,
                TFCBlocks.WILD_CROPS.get(Crop.MELON)
            )
            .add(Food.MELON_SLICE);

        tag(c.apply("crops/pumpkin"))
            .add(
                TFCBlocks.PUMPKIN,
                TFCBlocks.WILD_CROPS.get(Crop.PUMPKIN)
            )
            .add(Food.PUMPKIN_CHUNKS);

        copyCommon("crops");

        // Dyed

        for (DyeColor color : DyeColor.values())
        {
            final TagKey<Item> dyedTag = c.apply("dyed/" + color.getSerializedName());

            final DecorationBlockHolder brickDecorations =
                TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color);

            final DecorationBlockHolder polishedDecorations =
                TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color);

            tag(dyedTag)
                // Dyed block items
                .add(
                    TFCBlocks.RAW_ALABASTER.get(color),
                    TFCBlocks.POLISHED_ALABASTER.get(color),

                    brickDecorations.slab(),
                    brickDecorations.stair(),
                    brickDecorations.wall(),

                    polishedDecorations.slab(),
                    polishedDecorations.stair(),
                    polishedDecorations.wall(),

                    TFCBlocks.GLAZED_LARGE_VESSELS.get(color),
                    TFCBlocks.STAINED_WATTLE.get(color)
                )

                // Dyed standalone items
                .add(
                    TFCItems.GLAZED_VESSELS.get(color),
                    TFCItems.UNFIRED_GLAZED_LARGE_VESSELS.get(color),
                    TFCItems.UNFIRED_GLAZED_VESSELS.get(color),
                    TFCItems.WINDMILL_BLADES.get(color)
                );

            tag(Tags.Items.DYED)
                .addTag(dyedTag);
        }

        //Fertilizers

        tag(Tags.Items.FERTILIZERS)
            .add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.GUANO));

        // Foods

        tag(c.apply("foods/cake"))
            .add(TFCBlocks.CAKE);

        tag(c.apply("foods/edible_when_placed"))
            .add(TFCBlocks.CAKE);

        tag(c.apply("cake"))
            .add(TFCBlocks.CAKE);

        tag(c.apply("foods")).addTags(
            c.apply("foods/cake"),
            c.apply("foods/edible_when_placed")
        );

        // Ores

        final Set<String> oreTagsToCopy = getStrings();

        oreTagsToCopy.forEach(this::copyCommon);

        copyCommon("ores/coal");

        tag(c.apply("ores/salt"))
            .add(TFCBlocks.HALITE);

        tag(c.apply("ores/flint"))
            .add(Items.FLINT)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.GRAVEL);

        copyCommon("ores");


        // Ores in Ground
        copyCommon("ores_in_ground/gravel");

        for (Rock rock : Rock.values())
        {
            copyCommon(
                "ores_in_ground/" + rock.getSerializedName()
            );
        }

        copyCommon("ores_in_ground");

        // Plants

        final TagKey<Item> plantsTag = c.apply("plants");
        final TagKey<Item> smallPlantsTag = c.apply("plants/small");
        final TagKey<Item> tallPlantsTag = c.apply("plants/tall");
        final TagKey<Item> waterPlantsTag = c.apply("plants/water");
        final TagKey<Item> saltWaterPlantsTag = c.apply("plants/salt_water");
        final TagKey<Item> floatingPlantsTag = c.apply("plants/floating");
        final TagKey<Item> hangingPlantsTag = c.apply("plants/hanging");
        final TagKey<Item> wallPlantsTag = c.apply("plants/wall");

        tag(smallPlantsTag)
            .addOnly(
                TFCBlocks.PLANTS,
                plant -> plant.needsItem() && plant.isSmallPlant()
            )
            .add(
                TFCBlocks.ASPEN_KRUMMHOLZ,
                TFCBlocks.DOUGLAS_FIR_KRUMMHOLZ,
                TFCBlocks.PINE_KRUMMHOLZ,
                TFCBlocks.SPRUCE_KRUMMHOLZ,
                TFCBlocks.WHITE_CEDAR_KRUMMHOLZ
            );

        tag(tallPlantsTag)
            .addOnly(
                TFCBlocks.PLANTS,
                plant -> plant.needsItem() && plant.isTallPlant()
            );

        tag(waterPlantsTag)
            .addOnly(
                TFCBlocks.PLANTS,
                plant -> plant.needsItem() && plant.isFreshWaterPlant()
            );

        tag(saltWaterPlantsTag)
            .addOnly(
                TFCBlocks.PLANTS,
                plant -> plant.needsItem() && plant.isSaltWaterPlant()
            );

        tag(floatingPlantsTag)
            .addOnly(
                TFCBlocks.PLANTS,
                plant -> plant.needsItem() && plant.isSurfaceWaterPlant()
            );

        tag(hangingPlantsTag)
            .addOnly(
                TFCBlocks.PLANTS,
                plant -> plant.needsItem() && plant.isHangingPlant()
            );

        tag(wallPlantsTag)
            .addOnly(
                TFCBlocks.PLANTS,
                plant -> plant.needsItem() && plant.isWallPlant()
            );

        tag(plantsTag)
            .addOnly(TFCBlocks.PLANTS, Plant::needsItem)
            .add(
                TFCBlocks.ASPEN_KRUMMHOLZ,
                TFCBlocks.DOUGLAS_FIR_KRUMMHOLZ,
                TFCBlocks.PINE_KRUMMHOLZ,
                TFCBlocks.SPRUCE_KRUMMHOLZ,
                TFCBlocks.WHITE_CEDAR_KRUMMHOLZ
            );

        //Player Workstations

        tag( c.apply("player_workstations")).addTags(
            Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES,
            c.apply("player_workstations/anvil"),
            c.apply("player_workstations/blast_furnace"),
            c.apply("player_workstations/bloomery"),
            c.apply("player_workstations/composter"),
            c.apply("player_workstations/crucible"),
            c.apply("player_workstations/firebox"),
            c.apply("player_workstations/firepit"),
            c.apply("player_workstations/grill"),
            c.apply("player_workstations/lectern"),
            c.apply("player_workstations/loom"),
            c.apply("player_workstations/mold_table"),
            c.apply("player_workstations/nest_box"),
            c.apply("player_workstations/quern"),
            c.apply("player_workstations/scribing_table"),
            c.apply("player_workstations/sewing_table"),
            c.apply("player_workstations/stove")
        );

        //Raw Materials

        for (var entry : TFCBlocks.SMALL_ORES.entrySet())
        {
            Ore ore = entry.getKey();
            Metal metal = ore.metal();
            final String metalName = metal == Metal.CAST_IRON ? "iron" : metal.getSerializedName();
            final String tagName = "raw_materials/" + metalName + "/small";
            tag(c.apply(tagName))
                .add(TFCBlocks.SMALL_ORES.get(ore));
            tag(Tags.Items.RAW_MATERIALS)
                .addTag(c.apply(tagName));
        }

        // Signs

        final TagKey<Item> signsTag = c.apply("signs");
        final TagKey<Item> woodenSignsTag = c.apply("signs/wooden");
        final TagKey<Item> hangingSignsTag = c.apply("signs/hanging");

        tag(woodenSignsTag)
            .add(TFCItems.SIGNS);

        for (Metal metal : Metal.values())
        {
            if (metal.allParts())
            {
                final String metalName = metal == Metal.WROUGHT_IRON
                    ? "iron"
                    : metal.getSerializedName();

                final TagKey<Item> metalHangingSignsTag =
                    c.apply("signs/hanging/" + metalName);

                tag(metalHangingSignsTag)
                    .add(TFCItems.HANGING_SIGNS, metal);

                tag(hangingSignsTag)
                    .addTag(metalHangingSignsTag);
            }
        }

        tag(signsTag).addTags(
            hangingSignsTag,
            woodenSignsTag
        );

        //Storage Blocks

        tag(Tags.Items.STORAGE_BLOCKS_WHEAT).remove(Items.HAY_BLOCK);
        tag(Tags.Items.STORAGE_BLOCKS_SLIME).remove(Items.SLIME_BLOCK);

        //-----Item Tags-----//

        //Armors

        tag(c.apply("armors/horse"))
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.HORSE_ARMOR);

        //Arrows

        tag(c.apply("arrows"))
            .add(TFCItems.GLOW_ARROW.key());

        //Boats

        tag(c.apply("boats"))
            .add(TFCItems.BOATS);

        //Bricks

        tag(Tags.Items.BRICKS).addTags(
            c.apply("bricks/mud"),
            c.apply("bricks/plaster"),
            c.apply("bricks/fire"),
            c.apply("bricks/stone")
        );

        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            tag(c.apply("bricks/mud"))
                .add(variant.mudBrick().key());
        }

        tag(c.apply("bricks/plaster"))
            .add(TFCItems.ALABASTER_BRICK.key());
        tag(c.apply("bricks/fire"))
            .add(TFCItems.FIRE_BRICK.key());
        tag(c.apply("bricks/stone"))
            .add(TFCItems.BRICKS);

        //Buckets

        tag(Tags.Items.BUCKETS).add(
            TFCItems.WOODEN_BUCKET,
            TFCItems.RED_STEEL_BUCKET,
            TFCItems.BLUE_STEEL_BUCKET
        );

        tag(Tags.Items.BUCKETS_ENTITY_WATER)
            .add(TFCItems.FRESHWATER_FISH_BUCKETS)
            .add(
                TFCItems.JELLYFISH_BUCKET.key(), TFCItems.TROPICAL_FISH_BUCKET.key(),
                TFCItems.PUFFERFISH_BUCKET.key(), TFCItems.COD_BUCKET.key()
            );

        //Cloths

        tag(c.apply("cloths")).addTags(
            c.apply("cloths/burlap"),
            c.apply("cloths/silk"),
            c.apply("cloths/wool")
        );

        tag(c.apply("cloths/burlap"))
            .add(TFCItems.BURLAP_CLOTH.key());
        tag(c.apply("cloths/silk"))
            .add(TFCItems.SILK_CLOTH.key());
        tag(c.apply("cloths/wool"))
            .add(TFCItems.WOOL_CLOTH.key());

        //Coal

        tag(c.apply("coal"))
            .add(TFCItems.ORES.get(Ore.LIGNITE).key(), TFCItems.ORES.get(Ore.BITUMINOUS_COAL).key());

        //Crops

        final EnumSet<Food> cropFoods = EnumSet.of(
            Food.BANANA, Food.BARLEY, Food.BEET, Food.BLACKBERRY,
            Food.BLUEBERRY, Food.BUNCHBERRY, Food.CABBAGE, Food.CARROT,
            Food.CASSAVA, Food.CATTAIL_ROOT, Food.CHERRY, Food.CLOUDBERRY,
            Food.CRANBERRY, Food.ELDERBERRY, Food.GARLIC, Food.GOOSEBERRY,
            Food.GREEN_APPLE, Food.GREEN_BEAN, Food.GREEN_BELL_PEPPER, Food.LEMON,
            Food.LENTIL, Food.MAIZE, Food.OAT, Food.OLIVE,
            Food.ONION, Food.ORANGE, Food.PEACH, Food.PEANUT,
            Food.PLUM, Food.POTATO, Food.RADISH, Food.RASPBERRY,
            Food.RED_BELL_PEPPER, Food.RICE, Food.RYE, Food.FRESH_SEAWEED,
            Food.SNOWBERRY, Food.SOYBEAN, Food.SQUASH, Food.STRAWBERRY,
            Food.SUGARCANE, Food.TARO_ROOT, Food.TOMATO, Food.WHEAT,
            Food.WINTERGREEN_BERRY, Food.YELLOW_BELL_PEPPER
        );

        for (Food food : cropFoods)
        {
            final String name = food == Food.FRESH_SEAWEED ? "seaweed" : food.getSerializedName();

            final TagKey<Item> cropTag = c.apply("crops/" + name);
            tag(cropTag).add(food);
            tag(Tags.Items.CROPS).addTag(cropTag);
        }

        tag(Tags.Items.CROPS)
            .addTags(
                c.apply("crops/apple"), c.apply("crops/beetroot"), c.apply("crops/corn"),
                c.apply("crops/bell_pepper"), c.apply("crops/taro"), c.apply("crops/alfalfa"),
                c.apply("crops/canola"), c.apply("crops/jute"), c.apply("crops/papyrus")
            );

        tag(c.apply("crops/apple"))
            .add(Food.GREEN_APPLE, Food.RED_APPLE);
        tag(c.apply("crops/beetroot"))
            .add(Food.BEET);
        tag(c.apply("crops/bell_pepper"))
            .add(Food.GREEN_BELL_PEPPER, Food.RED_BELL_PEPPER, Food.YELLOW_BELL_PEPPER);
        tag(c.apply("crops/corn"))
            .add(Food.MAIZE);
        tag(c.apply("crops/taro"))
            .add(Food.TARO_ROOT);

        tag(c.apply("crops/alfalfa"))
            .add(TFCItems.ALFALFA);
        tag(c.apply("crops/canola"))
            .add(TFCItems.CANOLA);
        tag(c.apply("crops/jute"))
            .add(TFCItems.JUTE);
        tag(c.apply("crops/papyrus"))
            .add(TFCItems.PAPYRUS);

        //Doughs

        for (Food food : Food.values())
        {
            final String name = food.getSerializedName();

            if (name.endsWith("_dough"))
            {
                final String grainName = name.substring(0, name.length() - "_dough".length());
                final TagKey<Item> doughTag = c.apply("doughs/" + grainName);

                tag(doughTag).add(food);
                tag(c.apply("doughs")).addTag(doughTag);
            }
        }

        //Dusts

        for (var entry : TFCItems.POWDERS.entrySet())
        {
            final Powder powder = entry.getKey();
            final String name = powder == Powder.COKE ? "coal_coke" : powder.name().toLowerCase(Locale.ROOT);
            final TagKey<Item> dustTag = c.apply("dusts/" + name);

            tag(dustTag).add(entry.getValue().key());
            tag(Tags.Items.DUSTS).addTag(dustTag);
        }

        final EnumSet<Ore> oreDusts = EnumSet.of(
            Ore.SALTPETER, Ore.GRAPHITE,
            Ore.SYLVITE, Ore.SULFUR
        );

        for (Ore ore : oreDusts)
        {
            final TagKey<Item> dustTag = c.apply("dusts/" + ore.name().toLowerCase(Locale.ROOT));

            tag(dustTag).add(TFCItems.ORE_POWDERS.get(ore).key());
            tag(Tags.Items.DUSTS).addTag(dustTag);
        }

        tag(c.apply("dusts/ash"))
            .add(TFCItems.POWDERS.get(Powder.WOOD_ASH).key());
        tag(Tags.Items.DUSTS)
            .addTag(c.apply("dusts/ash"));

        //Dyed

        for (DyeColor color : DyeColor.values())
        {
            final TagKey<Item> dyedTag = c.apply("dyed/" + color.getSerializedName());

            tag(dyedTag)
                .add(TFCItems.GLAZED_VESSELS.get(color))
                .add(TFCItems.UNFIRED_GLAZED_LARGE_VESSELS.get(color))
                .add(TFCItems.UNFIRED_GLAZED_VESSELS.get(color))
                .add(TFCItems.WINDMILL_BLADES.get(color));

            tag(Tags.Items.DYED)
                .addTag(dyedTag);
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

        tag(c.apply("fibers"))
            .addTag(c.apply("fibers/jute"));

        tag(c.apply("fibers/jute"))
            .add(TFCItems.JUTE_FIBER.key());

        //Foods -- Mods are all over the place with food tags, and there seems to be no general consensus on how they should be tagged, so I add what I saw were the most common tags used. How I believe they should be used is as follow
        //c:foods for all food items that can be eaten, if you right-click and the eating animation plays, it goes here. For individual items they should follow the format c:foods/<food_name> (e.g. c:foods/blueberry), and the tag should only contain the food item itself, no seeds or other items related to the food. Items should not be added into c:foods directly, should be added to a subtag and that tag added to c:foods.
        //For groups of items that share a common name but are different variants of the same item (e.g., blueberry, blackberry, etc.) they should be tagged with a common tag c:foods/berry, c:foods/fruit or c:foods/vegtable. An item might have multiple group tags.

        final EnumSet<Food> RAW_MEAT_FOODS = EnumSet.of(
            Food.BEEF, Food.BEAR, Food.BISON, Food.CAMELIDAE,
            Food.CHEVON, Food.FOX, Food.FROG_LEGS, Food.GRAN_FELINE,
            Food.HORSE_MEAT, Food.HYENA, Food.MUTTON, Food.PORK,
            Food.RABBIT, Food.TURTLE, Food.VENISON, Food.WOLF
        );

        final EnumSet<Food> RAW_FISH_FOODS = EnumSet.of(
            Food.BLUEGILL, Food.CALAMARI, Food.COD, Food.CRAPPIE,
            Food.LAKE_TROUT, Food.LARGEMOUTH_BASS, Food.RAINBOW_TROUT, Food.SALMON,
            Food.SHELLFISH, Food.SMALLMOUTH_BASS, Food.TROPICAL_FISH, Food.ARCTIC_CHAR,
            Food.BURBOT, Food.MUKSUN, Food.NORTHERN_PIKE, Food.PACU,
            Food.PEACOCK_BASS, Food.RED_PIRANHA, Food.SPOTTED_GUDGEON, Food.TILAPIA
        );

        final EnumSet<Food> RAW_POULTRY_FOODS = EnumSet.of(
            Food.CHICKEN, Food.DUCK, Food.GROUSE, Food.PEAFOWL,
            Food.PHEASANT, Food.QUAIL, Food.TURKEY
        );

        final EnumSet<Food> NUT_FOODS = EnumSet.of(
            Food.PEANUT
        );

        final EnumSet<Food> VEGETABLE_FOODS = EnumSet.of(
            Food.BEET, Food.CABBAGE, Food.CARROT, Food.GARLIC,
            Food.GREEN_BEAN, Food.GREEN_BELL_PEPPER, Food.ONION, Food.POTATO,
            Food.BAKED_POTATO, Food.RED_BELL_PEPPER, Food.SOYBEAN, Food.SUGARCANE,
            Food.SQUASH, Food.TOMATO, Food.YELLOW_BELL_PEPPER, Food.CASSAVA,
            Food.COOKED_CASSAVA, Food.LENTIL, Food.COOKED_LENTIL, Food.RADISH,
            Food.PUMPKIN_CHUNKS
        );

        for (Food food : Food.values())
        {
            if (RAW_MEAT_FOODS.contains(food) || RAW_FISH_FOODS.contains(food) || RAW_POULTRY_FOODS.contains(food))
            {
                tag(c.apply("foods/raw_" + food.getSerializedName())).add(food);
                tag(FOODS).addTag(c.apply("foods/raw_" + food.getSerializedName()));
                tag(c.apply("raw_" + food.getSerializedName())).add(food);
            }
            else
            {
                tag(c.apply("foods/" + food.getSerializedName())).add(food);
                tag(FOODS).addTag(c.apply("foods/" + food.getSerializedName()));
                tag(c.apply(food.getSerializedName())).add(food); //Added for compatibility with mods that don't use the c:foods/<food_name> format and just put their food items in a tag with the same name as the item. This is not ideal, but it seems to be a common practice by some authors, so I added it for compatibility reasons.
            }
        }

        tag(FOODS).addTags(
            c.apply("foods/apple"),
            c.apply("foods/bell_pepper"),
            c.apply("foods/beetroot"),
            c.apply("foods/corn"),
            c.apply("foods/melon"),
            c.apply("foods/pumpkin"),
            c.apply("foods/seaweed"),
            c.apply("foods/taro"),
            c.apply("foods/raw_lobster"),
            c.apply("foods/cooked_lobster"),
            c.apply("foods/raw_squid"),
            c.apply("foods/cooked_squid"),
            c.apply("foods/raw_goat"),
            c.apply("foods/cooked_goat"),
            c.apply("foods/raw_camel"),
            c.apply("foods/cooked_camel")
        );

        tag(c.apply("foods/apple")).addTags(
            c.apply("foods/red_apple"), c.apply("foods/green_apple")
        );
        tag(c.apply("apple")).addTags(
            c.apply("red_apple"), c.apply("green_apple")
        );
        tag(c.apply("foods/bell_pepper")).addTags(
            c.apply("foods/green_bell_pepper"), c.apply("foods/red_bell_pepper"), c.apply("foods/yellow_bell_pepper")
        );
        tag(c.apply("bell_pepper")).addTags(
            c.apply("green_bell_pepper"), c.apply("red_bell_pepper"), c.apply("yellow_bell_pepper")
        );
        tag(c.apply("foods/beetroot"))
            .add(Food.BEET);
        tag(c.apply("beetroot"))
            .add(Food.BEET);

        tag(c.apply("foods/corn"))
            .add(Food.MAIZE);
        tag(c.apply("corn"))
            .add(Food.MAIZE);

        tag(c.apply("foods/melon"))
            .add(Food.MELON_SLICE);
        tag(c.apply("melon"))
            .add(Food.MELON_SLICE);

        tag(c.apply("foods/pumpkin"))
            .add(Food.PUMPKIN_CHUNKS);
        tag(c.apply("pumpkin"))
            .add(Food.PUMPKIN_CHUNKS);

        tag(c.apply("foods/seaweed"))
            .add(Food.FRESH_SEAWEED);
        tag(c.apply("seaweed"))
            .add(Food.FRESH_SEAWEED);

        tag(c.apply("foods/taro"))
            .add(Food.TARO_ROOT);
        tag(c.apply("taro"))
            .add(Food.TARO_ROOT);

        tag(c.apply("foods/raw_lobster"))
            .add(Food.SHELLFISH);
        tag(c.apply("foods/cooked_lobster"))
            .add(Food.COOKED_SHELLFISH);

        tag(c.apply("foods/raw_squid"))
            .add(Food.CALAMARI);
        tag(c.apply("raw_squid"))
            .add(Food.CALAMARI);

        tag(c.apply("foods/cooked_squid"))
            .add(Food.COOKED_CALAMARI);
        tag(c.apply("cooked_squid"))
            .add(Food.COOKED_CALAMARI);

        tag(c.apply("foods/raw_goat"))
            .add(Food.CHEVON);
        tag(c.apply("raw_goat"))
            .add(Food.CHEVON);

        tag(c.apply("foods/cooked_goat"))
            .add(Food.COOKED_CHEVON);
        tag(c.apply("cooked_goat"))
            .add(Food.COOKED_CHEVON);

        tag(c.apply("foods/raw_camel"))
            .add(Food.CAMELIDAE);
        tag(c.apply("raw_camel"))
            .add(Food.CAMELIDAE);

        tag(c.apply("foods/cooked_camel"))
            .add(Food.COOKED_CAMELIDAE);
        tag(c.apply("cooked_camel"))
            .add(Food.COOKED_CAMELIDAE);

        //Bread, Dough, Flour, Grains, and Sandwiches
        for (Food food : Food.values())
        {
            final String name = food.getSerializedName();

            if (name.endsWith("_bread"))
            {
                tag(BREAD)
                    .addTag(c.apply("foods/" + name));
            }
            else if (name.endsWith("_dough"))
            {
                tag(DOUGH)
                    .addTag(c.apply("foods/" + name));
            }
            else if (name.endsWith("_flour"))
            {
                tag(FLOUR)
                    .addTag(c.apply("foods/" + name));
            }
            else if (name.endsWith("_grain"))
            {
                tag(GRAINS)
                    .addTag(c.apply("foods/" + name));
            }

            else if (name.endsWith("_sandwich"))
            {
                tag(SANDWICHES)
                    .addTag(c.apply("foods/" + name));
            }
        }

        tag(BREAD)
            .add(Items.BREAD);

        tag(FOODS).addTags(
            BREAD, DOUGH, FLOUR, GRAINS, SANDWICHES
        );

        //Salads and Soups
        for (Nutrient nutrient : Nutrient.values())
        {
            final String nutrientName = nutrient.getSerializedName();

            final TagKey<Item> saladTag = c.apply("foods/" + nutrientName + "_salad");
            final TagKey<Item> soupTag = c.apply("foods/" + nutrientName + "_soup");

            tag(saladTag)
                .add(TFCItems.SALADS.get(nutrient));
            tag(soupTag)
                .add(TFCItems.SOUPS.get(nutrient));

            tag(FOODS)
                .addTags(saladTag, soupTag);

            tag(SALADS)
                .addTag(saladTag);
            tag(SOUPS)
                .addTag(soupTag);
        }

        tag(FOODS).addTags(
            SALADS, SOUPS
        );

        tag(DAIRY).addTag(c.apply("foods/cheese"));

        tag(FOODS).addTag(DAIRY);

        //Fish
        for (Food fish : RAW_FISH_FOODS)
        {
            final String fishName = fish.getSerializedName();

            tag(RAW_FISH)
                .addTag(c.apply("foods/raw_" + fishName));
            tag(COOKED_FISH)
                .addTag(c.apply("foods/cooked_" + fishName));
        }

        tag(RAW_FISH).addTags(
            c.apply("foods/raw_squid"),
            c.apply("foods/raw_lobster")
        );

        tag(COOKED_FISH).addTags(
            c.apply("foods/cooked_squid"),
            c.apply("foods/cooked_lobster")
        );

        tag(FOODS).addTags(
            COOKED_FISH, RAW_FISH
        );

        //Meat
        for (Food meat : RAW_MEAT_FOODS)
        {
            final String meatName = meat.getSerializedName();

            tag(RAW_MEATS)
                .addTag(c.apply("foods/raw_" + meatName));
            tag(COOKED_MEATS)
                .addTag(c.apply("foods/cooked_" + meatName));
        }

        tag(RAW_MEATS).addTags(
            c.apply("foods/raw_goat"),
            c.apply("foods/raw_camel")
        );

        tag(COOKED_MEATS).addTags(
            c.apply("foods/cooked_goat"),
            c.apply("foods/cooked_camel")
        );

        tag(FOODS).addTags(
            COOKED_MEATS, RAW_MEATS
        );

        //Poultry
        for (Food poultry : RAW_POULTRY_FOODS)
        {
            final String poultryName = poultry.getSerializedName();

            tag(c.apply("foods/raw_poultry"))
                .addTag(c.apply("foods/raw_" + poultryName));
            tag(c.apply("foods/cooked_poultry"))
                .addTag(c.apply("foods/cooked_" + poultryName));
            tag(RAW_MEATS)
                .addTag(c.apply("foods/raw_" + poultryName));
            tag(COOKED_MEATS)
                .addTag(c.apply("foods/cooked_" + poultryName));
        }

        tag(FOODS).addTags(
            c.apply("foods/cooked_poultry"),
            c.apply("foods/raw_poultry")
        );

        //Berries
        for (Food food : Food.values())
        {
            final String name = food.getSerializedName();

            if (name.endsWith("berry"))
            {
                tag(Tags.Items.FOODS_BERRY).addTag(c.apply("foods/" + name));
            }
        }

        tag(FOODS).addTag(Tags.Items.FOODS_BERRY);

        //Fruits
        for (Food food : Food.values())
        {
            final String name = food.getSerializedName();

            if (food.hasJam() && food != Food.PEANUT)
            {
                tag(FRUITS)
                    .addTag(c.apply("foods/" + name));
            }
        }

        tag(FRUITS).addTags(
            c.apply("foods/apple"),
            c.apply("foods/melon")
        );

        tag(FOODS).addTag(FRUITS);

        //Nuts
        for (Food nut : NUT_FOODS)
        {
            final String name = nut.getSerializedName();

            tag(c.apply("foods/nut"))
                .addTag(c.apply("foods/" + name));
        }

        //Vegetables
        for (Food vegetable : VEGETABLE_FOODS)
        {
            final String name = vegetable.getSerializedName();

            tag(VEGETABLES)
                .addTag(c.apply("foods/" + name));
        }

        tag(VEGETABLES).addTags(
            c.apply("foods/bell_pepper"),
            c.apply("foods/beetroot"),
            c.apply("foods/pumpkin")

        );
        tag(FOODS).addTag(VEGETABLES);


        //Jams
        for (Map.Entry<Food, TFCItems.ItemId> entry : TFCItems.JAM.entrySet())
        {
            final Food jam = entry.getKey();
            final String jamName = jam == Food.MELON_SLICE ? "melon_jam"
                : jam.getSerializedName() + "_jam";

            final TagKey<Item> jamTag = c.apply("foods/" + jamName);

            tag(jamTag)
                .add(entry.getValue().key());

            tag(FOODS)
                .addTag(jamTag);

            tag(c.apply("foods/jam"))
                .addTag(jamTag);
        }

        tag(FOODS).addTag(c.apply("foods/jam"));

        tag(FISH).addTags(RAW_FISH, COOKED_FISH); //Might be unnecessary, but I left it in case any addons are using it.

        //Flours

        for (Food food : Food.values())
        {
            final String name = food.getSerializedName();

            if (name.endsWith("_flour"))
            {
                final String grainName = name.substring(0, name.length() - "_flour".length());
                final TagKey<Item> flourTag = c.apply("flours/" + grainName);

                tag(flourTag).add(food);
                tag(c.apply("flours")).addTag(flourTag);
            }
        }

        //Gems

        for (Ore ore : Ore.values())
        {
            if (ore.isGem())
            {
                final String gemName = ore == Ore.LAPIS_LAZULI ? "lapis" : ore.name().toLowerCase(Locale.ROOT);
                final TagKey<Item> gemTag = c.apply("gems/" + gemName);
                final TagKey<Item> dustTag = c.apply("dusts/" + gemName);

                tag(gemTag)
                    .add(TFCItems.GEMS.get(ore).key());
                tag(Tags.Items.GEMS)
                    .addTag(gemTag);

                tag(dustTag)
                    .add(TFCItems.ORE_POWDERS.get(ore).key());
                tag(Tags.Items.DUSTS)
                    .addTag(dustTag);
            }
        }

        //Glass Bottles

        tag(c.apply("glass_bottle")).add(
            TFCItems.HEMATITIC_GLASS_BOTTLE, TFCItems.OLIVINE_GLASS_BOTTLE, TFCItems.SILICA_GLASS_BOTTLE, TFCItems.VOLCANIC_GLASS_BOTTLE
        );

        //Glues

        tag(c.apply("glue"))
            .add(TFCItems.GLUE.key());

        //Grains

        for (Food food : Food.values())
        {
            final String name = food.getSerializedName();

            if (name.endsWith("_grain"))
            {
                final String grainName = name.substring(0, name.length() - "_grain".length());
                final TagKey<Item> grainTag = c.apply("grains/" + grainName);

                tag(grainTag).add(food);
                tag(c.apply("grains")).addTag(grainTag);
            }
        }

        //Lumber

        tag(c.apply("lumbers"))
            .addTag(LUMBER);

        //Metal Items

        for (Metal metal : Metal.values())
        {
            final String metalName = metal.getSerializedName();

            metalTag(metal, Metal.ItemType.INGOT, Tags.Items.INGOTS);

            if (metal.defaultParts())
            {
                final TagKey<Item> rodTag = c.apply("rods/" + metalName);
                final TagKey<Item> sheetTag = c.apply("sheets/" + metalName);
                final TagKey<Item> doubleSheetTag = c.apply("double_sheets/" + metalName);

                metalTag(metal, Metal.ItemType.DOUBLE_INGOT, DOUBLE_INGOTS);
                metalTag(metal, Metal.ItemType.ROD, Tags.Items.RODS);

                tag(c.apply("rods/all_metal"))
                    .addTag(rodTag);
                tag(sheetTag)
                    .add(TFCItems.METAL_ITEMS.get(metal).get(Metal.ItemType.SHEET));
                tag(SHEETS)
                    .addTag(sheetTag);
                tag(doubleSheetTag)
                    .add(TFCItems.METAL_ITEMS.get(metal).get(Metal.ItemType.DOUBLE_SHEET));
                tag(DOUBLE_SHEETS)
                    .addTag(doubleSheetTag);
            }
        }

        tag(Tags.Items.RODS)
            .addTag(c.apply("rods/all_metal"));

        //Minecart

        tag(MINECARTS)
            .add(Items.MINECART)
            .add(TFCItems.CHEST_MINECARTS);

        //Misc

        tag(c.apply("gears/brass"))
            .add(TFCItems.BRASS_MECHANISMS);

        tag(Tags.Items.STRINGS).add(TFCItems.WOOL_YARN);

        tag(Tags.Items.MUSIC_DISCS)
            .add(TFCItems.BLANK_DISC.key());

        tag(c.apply("straw"))
            .add(TFCItems.STRAW.key());

        //Molds

        tag(c.apply("molds")).addTags(
            c.apply("molds/fired"),
            c.apply("molds/unfired")
        );

        tag(c.apply("molds/fired"))
            .addTag(FIRED_MOLDS);
        tag(c.apply("molds/unfired"))
            .addTag(UNFIRED_MOLDS);

        //Raw Materials

        for (Ore ore : Ore.values())
        {
            if (ore.isGem())
            {
                final String gemName = ore == Ore.LAPIS_LAZULI ? "lapis" : ore.name().toLowerCase(Locale.ROOT);
                final TagKey<Item> rawMaterialTag = c.apply("raw_materials/" + gemName);

                tag(rawMaterialTag)
                    .add(TFCItems.ORES.get(ore).key());
                tag(Tags.Items.RAW_MATERIALS)
                    .addTag(rawMaterialTag);
            }
        }

        for (Ore ore : Ore.values())
        {
            if (ore.hasPowder() && !ore.isGraded() && !ore.isGem())
            {
                final String oreName = ore.name().toLowerCase(Locale.ROOT);
                final TagKey<Item> rawMaterialTag = c.apply("raw_materials/" + oreName);

                tag(rawMaterialTag)
                    .add(TFCItems.ORES.get(ore).key());
                tag(Tags.Items.RAW_MATERIALS)
                    .addTag(rawMaterialTag);
            }
        }

        tag(c.apply("raw_materials/salt"))
            .add(TFCItems.ORES.get((Ore.HALITE)));
        tag(c.apply("raw_materials/redstone"))
            .add(TFCItems.ORES.get(Ore.CINNABAR).key(), TFCItems.ORES.get(Ore.CRYOLITE).key());
        tag(c.apply("raw_materials/plaster"))
            .add(TFCItems.ORES.get(Ore.GYPSUM).key());
        tag(c.apply("raw_materials/flux"))
            .addTag(FLUXSTONE);
        tag(c.apply("raw_materials/obsidian"))
            .add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.OBSIDIAN_SHARD));
        tag(Tags.Items.RAW_MATERIALS).addTags(
            c.apply("raw_materials/salt"),
            c.apply("raw_materials/redstone"),
            c.apply("raw_materials/plaster"),
            c.apply("raw_materials/flux"),
            c.apply("raw_materials/obsiadian")
        );


        for (var entry : TFCItems.GRADED_ORES.entrySet())
        {
            final Ore ore = entry.getKey();
            final Metal metal = ore.metal();
            final String metalName = metal == Metal.CAST_IRON ? "iron" : metal.getSerializedName();

            for (var grade : Ore.Grade.values())
            {
                final TagKey<Item> gradedOreTag = c.apply("raw_materials/" + metalName + "/" + grade.name().toLowerCase(Locale.ROOT));

                tag(gradedOreTag)
                    .add(entry.getValue().get(grade).key());
                tag(Tags.Items.RAW_MATERIALS)
                    .addTag(gradedOreTag);
            }
        }

        //Ropes

        tag(Tags.Items.ROPES).add(TFCItems.ROPE);

        //Seeds

        for (Crop crop : Crop.values())
        {
            final TagKey<Item> seedTag = c.apply("seeds/" + crop.getSerializedName());

            tag(seedTag)
                .add(TFCItems.CROP_SEEDS.get(crop).key());
            tag(Tags.Items.SEEDS)
                .addTag(seedTag);
        }

        tag(c.apply("seeds/corn"))
            .add(TFCItems.CROP_SEEDS.get(Crop.MAIZE).key());
        tag(Tags.Items.SEEDS)
            .addTag(c.apply("seeds/corn"));

        tag(c.apply("seeds/beetroot"))
            .add(TFCItems.CROP_SEEDS.get(Crop.BEET).key());
        tag(Tags.Items.SEEDS)
            .addTag(c.apply("seeds/beetroot"));

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

        tag(Tags.Items.TOOLS_SHIELD)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.SHIELD);
        tag(Tags.Items.TOOLS_FISHING_ROD)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.FISHING_ROD);
        tag(Tags.Items.TOOLS_SPEAR)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.JAVELIN)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.JAVELIN)
            .add(TFCItems.OBSIDIAN_JAVELIN);
        tag(Tags.Items.TOOLS_SHEAR)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.SHEARS);
        tag(Tags.Items.TOOLS_IGNITER)
            .add(TFCItems.FIRESTARTER)
            .add(TFCItems.FLINT_AND_PYRITE);
        tag(Tags.Items.TOOLS_MACE)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.MACE);
        tag(Tags.Items.MINING_TOOL_TOOLS)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.PICKAXE);
        tag(Tags.Items.RANGED_WEAPON_TOOLS)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.JAVELIN)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.JAVELIN);
        tag(Tags.Items.MELEE_WEAPON_TOOLS)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.SWORD)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.AXE)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.MACE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.AXE)
            .add(TFCItems.OBSIDIAN_AXE)
            .add(TFCItems.OBSIDIAN_MACUAHUITL);

        tag(TOOLS_HAMMER)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.HAMMER)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.HAMMER)
 .add(TFCItems.OBSIDIAN_HAMMER);
        tag(TOOLS_SAW)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.SAW);
        tag(TOOLS_SCYTHE)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.SCYTHE);
        tag(TOOLS_PROPICK)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.PROPICK);
        tag(TOOLS_KNIFE)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.KNIFE)
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.KNIFE)
            .add(TFCItems.OBSIDIAN_KNIFE);
        tag(TOOLS_CHISEL)
            .add(TFCItems.METAL_ITEMS, Metal.ItemType.CHISEL);
        tag(TOOLS_GLASSWORKING)
            .add(TFCItems.PADDLE, TFCItems.JACKS, TFCItems.GEM_SAW);
        tag(TOOLS_BLOWPIPE)
            .add(TFCItems.BLOWPIPE, TFCItems.CERAMIC_BLOWPIPE);
        tag(c.apply("tools/sandpaper"))
            .add(TFCItems.SANDPAPER.key());
        tag(c.apply("tools/spindle"))
            .add(TFCItems.SPINDLE.key());

        //Tool Heads

        for (Metal.ItemType type : Metal.ItemType.values())
        {
            final String name = type.name().toLowerCase(Locale.ROOT);

            if (name.endsWith("_head") || name.endsWith("_blade"))
            {
                final String toolName = name.endsWith("_head") ? name.substring(0, name.length() - "_head".length())
                    : name.endsWith("_blade") ? name.substring(0, name.length() - "_blade".length())
                    : name;

                final TagKey<Item> toolHeadTag = c.apply("tool_heads/" + toolName);

                tag(toolHeadTag).add(TFCItems.METAL_ITEMS, type);
                tag(c.apply("tool_heads")).addTag(toolHeadTag);
            }
        }

        for (RockCategory.ItemType type : RockCategory.ItemType.values())
        {
            final String name = type.name().toLowerCase(Locale.ROOT);

            if (name.endsWith("_head") || name.endsWith("_blade"))
            {
                final String toolName = name.endsWith("_head") ? name.substring(0, name.length() - "_head".length())
                    : name.endsWith("_blade") ? name.substring(0, name.length() - "_blade".length())
                    : name;

                final TagKey<Item> toolHeadTag = c.apply("tool_heads/" + toolName);

                tag(toolHeadTag).add(TFCItems.ROCK_TOOLS, type);
                tag(c.apply("tool_heads")).addTag(toolHeadTag);
            }
        }

        tag(c.apply("tool_heads")).addTags(
            c.apply("tool_heads/spindle"),
            c.apply("tool_heads/fishing_hook")
        );
        tag(c.apply("tool_heads/spindle"))
            .add(TFCItems.SPINDLE_HEAD.key());
        tag(c.apply("tool_heads/fishing_hook"))
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
            .addTags(FRUITS, VEGETABLES, RAW_MEATS, COOKED_MEATS, RAW_FISH, COOKED_FISH)
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
        tag(CAMEL_FOOD).addTags(GRAINS, FRUITS, commonTagOf(Registries.ITEM, "grains"));
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
            .add(TFCItems.ALFALFA)
            .add(TFCItems.FLOWER_CUTTING);
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
            .addOnly(TFCItems.WINDMILL_BLADES, color -> color != DyeColor.WHITE);
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
            .add(TFCItems.ROCK_TOOLS, RockCategory.ItemType.SHOVEL)
            .add(TFCItems.OBSIDIAN_AXE)
            .add(TFCItems.OBSIDIAN_HAMMER)
            .add(TFCItems.OBSIDIAN_HOE)
            .add(TFCItems.OBSIDIAN_JAVELIN)
            .add(TFCItems.OBSIDIAN_KNIFE)
            .add(TFCItems.OBSIDIAN_SHOVEL);
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
        tag(GLASS_POWDERS).add(GlassOperation.POWDERS.get().keySet().stream().sorted(Comparator.comparing(Item::getId))); // Sorted to make generation deterministic
        tag(GLASS_BOTTLES).add(
            TFCItems.SILICA_GLASS_BOTTLE,
            TFCItems.HEMATITIC_GLASS_BOTTLE,
            TFCItems.OLIVINE_GLASS_BOTTLE,
            TFCItems.VOLCANIC_GLASS_BOTTLE);
        tag(GLASS_POTASH)
            .add(TFCItems.POWDERS.get(Powder.SODA_ASH), TFCItems.ORE_POWDERS.get(Ore.SALTPETER))
            .addTags(commonTagOf(Registries.ITEM, "dusts/soda_ash"), commonTagOf(Registries.ITEM, "dusts/saltpeter"))
            .addOptionalTag(commonTagOf(Registries.ITEM, "dusts/potash"));

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
        tag(ROCK_KNAPPING).addTag(STONES_LOOSE).add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.OBSIDIAN_SHARD));
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
        tag(TRIP_HAMMERS).add(TFCItems.METAL_ITEMS, Metal.ItemType.HAMMER); // N.B. Technical tag, don't include subtags
        tag(WELDING_FLUX).add(TFCItems.POWDERS.get(Powder.FLUX));
        tag(THATCH_BED_HIDES).add(TFCItems.HIDES.get(HideItemType.RAW).get(HideItemType.Size.LARGE));
        tag(BOWL_POWDERS) // N.B. Technical tag, don't include subtags
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
            .add(TFCItems.OBSIDIAN_JAVELIN)
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

    private static Set<String> getStrings()
    {
        final Set<String> oreTagsToCopy = new LinkedHashSet<>();

        for (OreDeposit deposit : OreDeposit.values())
        {
            final String metalName = switch (deposit)
            {
                case CASSITERITE -> "tin";
                case NATIVE_COPPER -> "copper";
                case NATIVE_GOLD -> "gold";
                case NATIVE_SILVER -> "silver";
            };

            oreTagsToCopy.add("ores/" + metalName + "/small");
            oreTagsToCopy.add("ores/" + metalName);
        }

        for (Ore ore : Ore.values())
        {
            if (ore.isGraded())
            {
                final Metal metal = ore.metal();
                final String metalName =
                    metal == Metal.CAST_IRON ? "iron" : metal.getSerializedName();

                for (Ore.Grade grade : Ore.Grade.values())
                {
                    oreTagsToCopy.add(
                        "ores/" + metalName + "/" +
                            grade.name().toLowerCase(Locale.ROOT)
                    );
                }

                oreTagsToCopy.add("ores/" + metalName);
            }
            else if (ore.hasBlock())
            {
                oreTagsToCopy.add(
                    "ores/" + ore.name().toLowerCase(Locale.ROOT)
                );
            }
        }

        for (Ore ore : TFCBlocks.SMALL_ORES.keySet())
        {
            final Metal metal = ore.metal();
            final String metalName =
                metal == Metal.CAST_IRON ? "iron" : metal.getSerializedName();

            oreTagsToCopy.add("ores/" + metalName + "/small");
            oreTagsToCopy.add("ores/" + metalName);
        }
        return oreTagsToCopy;
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

    private final Set<TagKey<Item>> initializedMetalPartTags = new HashSet<>();

    private void metalTag(Metal metal, Metal.ItemType type, TagKey<Item> baseTag)
    {
        final TagKey<Item> commonTag = type == Metal.ItemType.SHEET
            ? commonTagOf(Registries.ITEM, "plates/" + metal.getSerializedName())
            : type == Metal.ItemType.DOUBLE_SHEET
            ? commonTagOf(Registries.ITEM, "double_plates/" + metal.getSerializedName())
            : commonTagOf(metal, type);

        if (initializedMetalPartTags.add(commonTag))
        {
            tag(commonTag).add(TFCItems.METAL_ITEMS.get(metal).get(type).key());
        }

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

    private void copyCommon(String path)
    {
        copy(commonTagOf(Registries.BLOCK, path), commonTagOf(Registries.ITEM, path));
    }

    private void copyCommon(String blockPath, String itemPath)
    {
        copy(commonTagOf(Registries.BLOCK, blockPath), commonTagOf(Registries.ITEM, itemPath));
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> createContentsProvider()
    {
        return super.createContentsProvider().thenCombine(blockTags, (lookup, tagLookup) -> {
            tagsToCopy.forEach((blockTag, itemTag) -> tagLookup.apply(blockTag)
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
                }));
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

        @SuppressWarnings("SameParameterValue")
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
