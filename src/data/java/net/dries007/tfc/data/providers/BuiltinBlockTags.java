/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper.ResourceType;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.IdHolder;

import static net.dries007.tfc.common.TFCTags.Blocks.*;

public class BuiltinBlockTags extends TagsProvider<Block> implements Accessors
{
    private final ExistingFileHelper.IResourceType resourceType;

    public BuiltinBlockTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(event.getGenerator().getPackOutput(), Registries.BLOCK, lookup, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
        this.resourceType = new ResourceType(PackType.SERVER_DATA, ".json", Registries.tagsDirPath(registryKey));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        // ===== Minecraft Tags (ordered like BlockTags) ===== //

        tag(BlockTags.PLANKS).add(TFCBlocks.WOODS, Wood.BlockType.PLANKS);
        tag(BlockTags.STONE_BRICKS)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.BRICKS)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.CRACKED_BRICKS)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_BRICKS)
            .add(Blocks.BRICKS)
            .add(TFCBlocks.FIRE_BRICKS);
        tag(BlockTags.WOODEN_BUTTONS).add(TFCBlocks.WOODS, Wood.BlockType.BUTTON);
        tag(BlockTags.STONE_BUTTONS).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.BUTTON);
        tag(BlockTags.WOODEN_DOORS).add(TFCBlocks.WOODS, Wood.BlockType.DOOR);
        tag(BlockTags.WOODEN_STAIRS).add(TFCBlocks.WOODS, Wood.BlockType.STAIRS);
        tag(BlockTags.WOODEN_SLABS).add(TFCBlocks.WOODS, Wood.BlockType.SLAB);
        tag(BlockTags.WOODEN_FENCES)
            .add(TFCBlocks.WOODS, Wood.BlockType.FENCE)
            .add(TFCBlocks.WOODS, Wood.BlockType.LOG_FENCE);
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(TFCBlocks.WOODS, Wood.BlockType.PRESSURE_PLATE);
        tag(BlockTags.STONE_PRESSURE_PLATES).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.PRESSURE_PLATE);
        tag(BlockTags.WOODEN_TRAPDOORS).add(TFCBlocks.WOODS, Wood.BlockType.TRAPDOOR);
        tag(BlockTags.SAPLINGS).add(TFCBlocks.WOODS, Wood.BlockType.SAPLING);
        // Minecraft logs tags contain all log, stripped log, wood, and stripped wood
        // logs contains logs_that_burn + nether logs
        tag(BlockTags.LOGS_THAT_BURN).addTags(w -> logsTagOf(Registries.BLOCK, w), Wood.values());
        // Only includes logs that spawn naturally, we have both wood and logs that spawn naturally, so include both
        tag(BlockTags.OVERWORLD_NATURAL_LOGS)
            .add(TFCBlocks.WOODS, Wood.BlockType.LOG)
            .add(TFCBlocks.WOODS, Wood.BlockType.WOOD);
        tag(BlockTags.SAND).add(TFCBlocks.SAND);
        tag(BlockTags.STAIRS).addEveryTFC(b -> b instanceof StairBlock);
        tag(BlockTags.SLABS).addEveryTFC(b -> b instanceof SlabBlock);
        tag(BlockTags.WALLS).addEveryTFC(b -> b instanceof SlabBlock);
        tag(BlockTags.LEAVES)
            .add(TFCBlocks.WOODS, Wood.BlockType.LEAVES)
            .addTags(FALLEN_LEAVES)
            .add(TFCBlocks.FRUIT_TREE_LEAVES);
        // Includes wooden trapdoors
        tag(BlockTags.TRAPDOORS).add(TFCBlocks.METALS, Metal.BlockType.TRAPDOOR);
        tag(BlockTags.DIRT).addTags(GRASS, DIRT, MUD);
        tag(BlockTags.FLOWER_POTS).add(TFCBlocks.POTTED_PLANTS);
        tag(BlockTags.ICE).add(
            TFCBlocks.SEA_ICE,
            TFCBlocks.ICE_PILE);
        // Vanilla adds coral blocks, plants, and fans. We add just plants and fans. These are used in worldgen,
        // specifically to select random ones from a tag. For this, we add our own TFC tags (as not to grab vanilla corals)
        tag(BlockTags.WALL_CORALS).addTag(SALT_WATER_WALL_CORALS);
        tag(BlockTags.CORAL_PLANTS).addTag(SALT_WATER_CORAL_PLANTS);
        tag(BlockTags.CORALS).addTag(SALT_WATER_CORALS);
        tag(BlockTags.STANDING_SIGNS).add(TFCBlocks.WOODS, Wood.BlockType.SIGN);
        tag(BlockTags.WALL_SIGNS).add(TFCBlocks.WOODS, Wood.BlockType.WALL_SIGN);
        tag(BlockTags.CEILING_HANGING_SIGNS).add2(TFCBlocks.CEILING_HANGING_SIGNS);
        tag(BlockTags.WALL_HANGING_SIGNS).add2(TFCBlocks.WALL_HANGING_SIGNS);
        tag(BlockTags.CROPS)
            .add(TFCBlocks.CROPS)
            .add(TFCBlocks.DEAD_CROPS)
            .add(TFCBlocks.WILD_CROPS);
        // todo: other crops?
        // todo: add vines to climable tag?
        tag(BlockTags.FENCE_GATES).add(TFCBlocks.WOODS, Wood.BlockType.FENCE_GATE);
        tag(BlockTags.BASE_STONE_OVERWORLD)
            .addTags(STONES_RAW, STONES_HARDENED);
        tag(BlockTags.STONE_ORE_REPLACEABLES).addTag(STONES_RAW); // Used for vanilla-like ore generation
        tag(BlockTags.OVERWORLD_CARVER_REPLACEABLES)
            // Already includes base stone overworld, which includes raw and hardened stone
            .addTags(Tags.Blocks.GRAVELS, Tags.Blocks.COBBLESTONES)
            .add(TFCBlocks.SANDSTONE, SandstoneBlockType.RAW);
        tag(BlockTags.CANDLE_CAKES)
            .add(TFCBlocks.CANDLE_CAKE)
            .add(TFCBlocks.DYED_CANDLE_CAKES);
        tag(BlockTags.SNOW).add(TFCBlocks.SNOW_PILE); // Includes snow layers, blocks, and powder snow
        tag(BlockTags.MINEABLE_WITH_AXE)
            .addOnly2(TFCBlocks.WOODS, k -> k != Wood.BlockType.LEAVES && k != Wood.BlockType.SAPLING && k != Wood.BlockType.POTTED_SAPLING && k != Wood.BlockType.FALLEN_LEAVES)
            .add(TFCBlocks.FRUIT_TREE_BRANCHES)
            .add(TFCBlocks.FRUIT_TREE_GROWING_BRANCHES)
            .add(TFCBlocks.STAINED_WATTLE)
            .add(
                TFCBlocks.PALM_MOSAIC,
                TFCBlocks.PALM_MOSAIC_SLAB,
                TFCBlocks.PALM_MOSAIC_STAIRS,
                TFCBlocks.WATTLE,
                TFCBlocks.UNSTAINED_WATTLE,
                TFCBlocks.BANANA_PLANT,
                TFCBlocks.DEAD_BANANA_PLANT,
                TFCBlocks.LOG_PILE,
                TFCBlocks.BURNING_LOG_PILE,
                TFCBlocks.COMPOSTER,
                TFCBlocks.NEST_BOX,
                TFCBlocks.POWDERKEG,
                TFCBlocks.WOODEN_BOWL,
                TFCBlocks.ASPEN_KRUMMHOLZ,
                TFCBlocks.PINE_KRUMMHOLZ,
                TFCBlocks.DOUGLAS_FIR_KRUMMHOLZ,
                TFCBlocks.SPRUCE_KRUMMHOLZ,
                TFCBlocks.WHITE_CEDAR_KRUMMHOLZ,
                TFCBlocks.BELLOWS,
                TFCBlocks.BARREL_RACK
            );
        // Note, our hoes do not use this tag, but instead we co-opt the values as a 'sharp tool'
        tag(BlockTags.MINEABLE_WITH_HOE)
            .add(TFCBlocks.PLANTS)
            .add(TFCBlocks.WILD_CROPS)
            .add(TFCBlocks.DEAD_CROPS)
            .add(TFCBlocks.CROPS)
            .add(TFCBlocks.SPREADING_BUSHES)
            .add(TFCBlocks.SPREADING_CANES)
            .add(TFCBlocks.STATIONARY_BUSHES)
            .add(TFCBlocks.WOODS, Wood.BlockType.LEAVES)
            .add(TFCBlocks.WOODS, Wood.BlockType.FALLEN_LEAVES)
            .add(TFCBlocks.WOODS, Wood.BlockType.SAPLING)
            .add2(TFCBlocks.WALL_HANGING_SIGNS)
            .add2(TFCBlocks.CEILING_HANGING_SIGNS)
            .add(TFCBlocks.FRUIT_TREE_LEAVES)
            .add(TFCBlocks.FRUIT_TREE_SAPLINGS)
            .add(
                TFCBlocks.SEA_PICKLE,
                TFCBlocks.CRANBERRY_BUSH,
                TFCBlocks.DEAD_BERRY_BUSH,
                TFCBlocks.DEAD_CANE,
                TFCBlocks.THATCH,
                TFCBlocks.THATCH_BED,
                TFCBlocks.TREE_ROOTS,
                TFCBlocks.MELON,
                TFCBlocks.PUMPKIN,
                TFCBlocks.ROTTEN_MELON,
                TFCBlocks.ROTTEN_PUMPKIN,
                TFCBlocks.JACK_O_LANTERN
            );
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add2(TFCBlocks.SANDSTONE)
            .addAll2(TFCBlocks.SANDSTONE_DECORATIONS)
            .add2(TFCBlocks.ORES)
            .add3(TFCBlocks.GRADED_ORES)
            .add(TFCBlocks.SMALL_ORES)
            .addOnly2(TFCBlocks.ROCK_BLOCKS, k -> k != Rock.BlockType.GRAVEL)
            .addAll2(TFCBlocks.ROCK_DECORATIONS)
            .add(TFCBlocks.ROCK_ANVILS)
            .add(TFCBlocks.MAGMA_BLOCKS)
            .add2(TFCBlocks.METALS)
            .add2(TFCBlocks.CORAL)
            .add(TFCBlocks.RAW_ALABASTER)
            .add(TFCBlocks.ALABASTER_BRICKS)
            .addAll(TFCBlocks.ALABASTER_BRICK_DECORATIONS)
            .add(TFCBlocks.POLISHED_ALABASTER)
            .addAll(TFCBlocks.ALABASTER_POLISHED_DECORATIONS)
            .add(TFCBlocks.GROUNDCOVER)
            .add(TFCBlocks.GLAZED_LARGE_VESSELS)
            .add(
                TFCBlocks.ICICLE,
                TFCBlocks.SEA_ICE,
                TFCBlocks.ICE_PILE,
                TFCBlocks.CALCITE,
                TFCBlocks.PLAIN_ALABASTER,
                TFCBlocks.PLAIN_ALABASTER_BRICKS,
                TFCBlocks.PLAIN_POLISHED_ALABASTER,
                TFCBlocks.FIRE_BRICKS,
                TFCBlocks.QUERN,
                TFCBlocks.CRUCIBLE,
                TFCBlocks.BLOOMERY,
                TFCBlocks.BLOOM,
                TFCBlocks.POT,
                TFCBlocks.GRILL,
                TFCBlocks.FIREPIT,
                TFCBlocks.INGOT_PILE,
                TFCBlocks.DOUBLE_INGOT_PILE,
                TFCBlocks.SHEET_PILE,
                TFCBlocks.BLAST_FURNACE,
                TFCBlocks.CERAMIC_BOWL,
                TFCBlocks.CRANKSHAFT,
                TFCBlocks.STEEL_PIPE,
                TFCBlocks.STEEL_PUMP,
                TFCBlocks.TRIP_HAMMER,
                TFCBlocks.BRONZE_BELL,
                TFCBlocks.BRASS_BELL,
                TFCBlocks.LARGE_VESSEL
            );
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .add2(TFCBlocks.SOIL)
            .add(TFCBlocks.SAND)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.GRAVEL)
            .add2(TFCBlocks.ORE_DEPOSITS)
            .addAll(TFCBlocks.MUD_BRICK_DECORATIONS)
            .add(
                TFCBlocks.PEAT,
                TFCBlocks.PEAT_GRASS,
                TFCBlocks.KAOLIN_CLAY_GRASS,
                TFCBlocks.WHITE_KAOLIN_CLAY,
                TFCBlocks.PINK_KAOLIN_CLAY,
                TFCBlocks.RED_KAOLIN_CLAY,
                TFCBlocks.SNOW_PILE,
                TFCBlocks.AGGREGATE,
                TFCBlocks.FIRE_CLAY_BLOCK,
                TFCBlocks.CHARCOAL_PILE,
                TFCBlocks.CHARCOAL_FORGE,
                TFCBlocks.SMOOTH_MUD_BRICKS
            );
        // Sword Efficient in vanilla is 'mines faster with sword', so we don't include anything extra in there,
        // since again, we typically want to refer to sharp tools instead
        // Requires >= Black Steel
        tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
            .add(TFCBlocks.ORES, Ore.DIAMOND)
            .add(TFCBlocks.ORES, Ore.RUBY)
            .add(TFCBlocks.ORES, Ore.SAPPHIRE);
        // Requires >= Steel
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .add(TFCBlocks.ORES, Ore.AMETHYST)
            .add(TFCBlocks.ORES, Ore.EMERALD)
            .add(TFCBlocks.ORES, Ore.TOPAZ);
        // Requires >=bronze
        tag(BlockTags.NEEDS_IRON_TOOL)
            .add2(pivot(TFCBlocks.GRADED_ORES, Ore.GARNIERITE))
            .add(TFCBlocks.ORES, Ore.CINNABAR)
            .add(TFCBlocks.ORES, Ore.CRYOLITE)
            .add(TFCBlocks.ORES, Ore.HALITE)
            .add(TFCBlocks.ORES, Ore.LAPIS_LAZULI)
            .add(TFCBlocks.ORES, Ore.OPAL);
        // Needs Stone Tool is ~ Copper, which is every TFC pickaxe, so we don't bother here
        // "Incorrect For Tool" includes the "Needs For Tool", so we don't touch, since we don't add levels
        tag(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON).add(TFCBlocks.SEA_ICE, TFCBlocks.ICE_PILE);
        tag(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON).add(TFCBlocks.SOIL.get(SoilBlockType.MUD));
        tag(BlockTags.REPLACEABLE).addEveryTFC(e -> e.defaultBlockState().canBeReplaced());

        // ===== Common Tags ===== //

        tag(Tags.Blocks.CHAINS).add(TFCBlocks.METALS, Metal.BlockType.CHAIN);
        tag(Tags.Blocks.CHESTS_WOODEN)
            .add(TFCBlocks.WOODS, Wood.BlockType.CHEST)
            .add(TFCBlocks.WOODS, Wood.BlockType.TRAPPED_CHEST);
        tag(Tags.Blocks.CHESTS_TRAPPED).add(TFCBlocks.WOODS, Wood.BlockType.TRAPPED_CHEST);
        tag(Tags.Blocks.COBBLESTONES_NORMAL).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.COBBLE);
        tag(Tags.Blocks.COBBLESTONES_MOSSY).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_COBBLE);

        // Ignore dyed tags, as it seems to imply trivial dye application?

        // fences/wooden includes minecraft:wooden_fences - we only add to the minecraft tag
        // fence_gates/wooden and minecraft:fence_gates don't have any relationship - we need to add to both
        tag(Tags.Blocks.FENCE_GATES_WOODEN).add(TFCBlocks.WOODS, Wood.BlockType.FENCE_GATE);
        tag(Tags.Blocks.GRAVELS).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.GRAVEL);

        // Ore Tags
        // We don't include "ore_bearing_ground/???" tags, because they are specific to stone (or known vanilla stones) only
        // Also ignore "ore_rates/???" because unsure how they are supposed to apply...
        // For ores, we group ores by metal, not by ore. So ores/copper, not ores/tetrahedrite
        // For graded ores, we add ores/<metal>/grade, and include all grades in the main ore tag
        for (Ore ore : Ore.values())
        {
            if (ore.isGraded())
            {
                final var ores = pivot(TFCBlocks.GRADED_ORES, ore);

                tag(Tags.Blocks.ORES).addTags(
                    oreBlockTagOf(ore, Ore.Grade.POOR),
                    oreBlockTagOf(ore, Ore.Grade.NORMAL),
                    oreBlockTagOf(ore, Ore.Grade.RICH));
                tag(oreBlockTagOf(ore, Ore.Grade.POOR)).add(ores, Ore.Grade.POOR);
                tag(oreBlockTagOf(ore, Ore.Grade.NORMAL)).add(ores, Ore.Grade.NORMAL);
                tag(oreBlockTagOf(ore, Ore.Grade.RICH)).add(ores, Ore.Grade.RICH);
            }
            else
            {
                tag(Tags.Blocks.ORES).addTag(oreBlockTagOf(ore, null));
                tag(oreBlockTagOf(ore, null)).add(TFCBlocks.ORES, ore);
            }
        }

        // Unless there's incentive, I don't know why we would add other workstations here - also why doesn't this just use the vanilla tag?
        tag(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES).addTag(WORKBENCHES);

        // Don't bother with sands/<color> because it's assuming vanilla red sand only. wtf is "colorless" sand
        tag(Tags.Blocks.SANDS).add(TFCBlocks.SAND);
        tag(Tags.Blocks.SANDSTONE_BLOCKS).add2(TFCBlocks.SANDSTONE);
        tag(Tags.Blocks.SANDSTONE_SLABS).add2(TFCBlocks.SANDSTONE_DECORATIONS, DecorationBlockHolder::slab);
        tag(Tags.Blocks.SANDSTONE_STAIRS).add2(TFCBlocks.SANDSTONE_DECORATIONS, DecorationBlockHolder::stair);

        tag(Tags.Blocks.STONES).addTags(STONES_RAW, STONES_HARDENED);

        tag(Tags.Blocks.STORAGE_BLOCKS_WHEAT).remove(Blocks.HAY_BLOCK); // We repurpose this as storing straw
        pivot(TFCBlocks.METALS, Metal.BlockType.BLOCK).forEach((metal, block) -> tag(storageBlockTagOf(Registries.BLOCK, metal)).add(block));

        // ===== TFC Tags ===== //

        for (Wood wood : Wood.VALUES)
        {
            tag(logsTagOf(Registries.BLOCK, wood)).add(
                TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LOG),
                TFCBlocks.WOODS.get(wood).get(Wood.BlockType.STRIPPED_LOG),
                TFCBlocks.WOODS.get(wood).get(Wood.BlockType.WOOD),
                TFCBlocks.WOODS.get(wood).get(Wood.BlockType.STRIPPED_WOOD));
        }

        tag(CAN_TRIGGER_COLLAPSE).addTags(Tags.Blocks.ORES, Tags.Blocks.STONES);
        tag(CAN_START_COLLAPSE).addTags(Tags.Blocks.ORES, TFCTags.Blocks.STONES_RAW);
        tag(CAN_COLLAPSE).addTags(Tags.Blocks.ORES, Tags.Blocks.STONES, STONES_SMOOTH, STONES_SPIKE);

        tag(CAN_LANDSLIDE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.COBBLE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_COBBLE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.GRAVEL)
            .add(TFCBlocks.SAND)
            .add2(TFCBlocks.ORE_DEPOSITS)
            .add(
                TFCBlocks.WHITE_KAOLIN_CLAY,
                TFCBlocks.PINK_KAOLIN_CLAY,
                TFCBlocks.RED_KAOLIN_CLAY
            );
        tag(SUPPORTS_LANDSLIDE).addTags(FARMLANDS, PATHS);
        tag(NOT_SOLID_SUPPORTING).addTags(STONES_SMOOTH);
        tag(TOUGHNESS_1).add(TFCBlocks.CHARCOAL_PILE, TFCBlocks.CHARCOAL_FORGE);
        tag(TOUGHNESS_2).addTag(Tags.Blocks.STONES);
        tag(TOUGHNESS_3).add(Blocks.BEDROCK);
        tag(BREAKS_WHEN_ISOLATED).addTag(STONES_RAW);
        tag(FALLEN_LEAVES).add(TFCBlocks.WOODS, Wood.BlockType.FALLEN_LEAVES);
        tag(SEASONAL_LEAVES).addOnly(pivot(TFCBlocks.WOODS, Wood.BlockType.LEAVES), e -> !e.isConifer());

        tag(STONES_RAW).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.RAW);
        tag(STONES_HARDENED).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.HARDENED);
        tag(STONES_SMOOTH).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.SMOOTH);
        tag(STONES_SMOOTH_SLABS).add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.SMOOTH)
            .values()
            .stream()
            .map(DecorationBlockHolder::slab));
        tag(STONES_SPIKE).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.SPIKE);
        tag(STONES_PRESSURE_PLATES)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.PRESSURE_PLATE)
            .addOptionalTag(ResourceLocation.withDefaultNamespace("stone_pressure_plates"));
        tag(STONES_LOOSE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.LOOSE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_LOOSE);
        tag(INSULATION)
            .addTags(Tags.Blocks.STONES, STONES_SMOOTH, BlockTags.STONE_BRICKS, Tags.Blocks.COBBLESTONES)
            .add(Blocks.BRICKS)
            .add(TFCBlocks.FIRE_BRICKS);

        tag(LAMPS).add(TFCBlocks.METALS, Metal.BlockType.LAMP);
        tag(ANVILS).add(TFCBlocks.METALS, Metal.BlockType.ANVIL);

        tag(LOGS_THAT_LOG).addTag(BlockTags.LOGS);
        tag(WORKBENCHES).add(TFCBlocks.WOODS, Wood.BlockType.WORKBENCH);
        tag(SUPPORT_BEAMS)
            .add(TFCBlocks.WOODS, Wood.BlockType.HORIZONTAL_SUPPORT)
            .add(TFCBlocks.WOODS, Wood.BlockType.VERTICAL_SUPPORT);
        tag(AQUEDUCTS).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.AQUEDUCT);

        tag(CHARCOAL_PIT_INSULATION).add(
            TFCBlocks.LOG_PILE,
            TFCBlocks.BURNING_LOG_PILE,
            TFCBlocks.CHARCOAL_PILE);
        tag(CHARCOAL_FORGE_INSULATION).addTag(INSULATION);
        tag(CHARCOAL_FORGE_INVISIBLE).add(TFCBlocks.CRUCIBLE);
        tag(BLOOMERY_INSULATION).addTag(INSULATION);
        tag(BLAST_FURNACE_INSULATION).add(TFCBlocks.FIRE_BRICKS);
        tag(SCRAPING_SURFACE).addTag(BlockTags.LOGS);
        tag(GLASS_POURING_TABLE).add(TFCBlocks.METALS.get(Metal.BRASS).get(Metal.BlockType.BLOCK));
        tag(GLASS_BASIN_BLOCKS).add(TFCBlocks.METALS.get(Metal.BRASS).get(Metal.BlockType.BLOCK));
        tag(THATCH_BED_THATCH).add(TFCBlocks.THATCH);
        tag(FRUIT_TREE_BRANCH)
            .add(TFCBlocks.BANANA_PLANT, TFCBlocks.DEAD_BANANA_PLANT)
            .add(TFCBlocks.FRUIT_TREE_BRANCHES)
            .add(TFCBlocks.FRUIT_TREE_GROWING_BRANCHES);
        tag(FRUIT_TREE_LEAVES).add(TFCBlocks.FRUIT_TREE_LEAVES);
        tag(FRUIT_TREE_SAPLING).add(TFCBlocks.FRUIT_TREE_SAPLINGS);
        tag(KELP_TREE).add(
            TFCBlocks.PLANTS.get(Plant.GIANT_KELP_PLANT),
            TFCBlocks.PLANTS.get(Plant.GIANT_KELP_FLOWER));
        tag(KELP_BRANCH).add(TFCBlocks.PLANTS.get(Plant.GIANT_KELP_PLANT));
        tag(BAMBOO).add(TFCBlocks.PLANTS.get(Plant.GOLDEN_BAMBOO)).add(Blocks.BAMBOO);
        tag(BAMBOO_SAPLING).add(TFCBlocks.PLANTS.get(Plant.GOLDEN_BAMBOO_SAPLING)).add(Blocks.BAMBOO_SAPLING);
        tag(BlockTags.BAMBOO_PLANTABLE_ON).add(TFCBlocks.PLANTS.get(Plant.GOLDEN_BAMBOO_SAPLING), TFCBlocks.PLANTS.get(Plant.GOLDEN_BAMBOO));
        tag(LIVING_SPREADING_BUSHES)
            .add(TFCBlocks.SPREADING_BUSHES)
            .add(TFCBlocks.SPREADING_CANES);
        tag(SPREADING_BUSHES)
            .addTags(LIVING_SPREADING_BUSHES)
            .add(TFCBlocks.DEAD_BERRY_BUSH, TFCBlocks.DEAD_CANE);
        tag(THORNY_BUSHES).add(
            TFCBlocks.SPREADING_BUSHES.get(FruitBlocks.SpreadingBush.RASPBERRY),
            TFCBlocks.SPREADING_BUSHES.get(FruitBlocks.SpreadingBush.BLACKBERRY));
        tag(POWDERKEG_CANNOT_BREAK).add(Blocks.BEDROCK); // unfortunately bedrock is breakable via large enough explosions so this is necessary
        tag(POWDERKEG_CAN_BREAK).addTags(BlockTags.DIRT, Tags.Blocks.STONES, Tags.Blocks.ORES, Tags.Blocks.GRAVELS);
        tag(CAN_BE_SNOW_PILED)
            .addTags(FALLEN_LEAVES, STONES_LOOSE)
            .add(TFCBlocks.SMALL_ORES)
            .add(TFCBlocks.GROUNDCOVER)
            .add(TFCBlocks.WILD_CROPS)
            .add(TFCBlocks.WOODS, Wood.BlockType.TWIG)
            .addOnly(TFCBlocks.PLANTS, Plant::canBeSnowPiled);
        tag(CAN_BE_ICE_PILED).addOnly(TFCBlocks.PLANTS, Plant::canBeIcePiled);
        tag(CONVERTS_TO_HUMUS).addTag(FALLEN_LEAVES);
        tag(SOLID_TOP_FACE).add(Blocks.HOPPER);
        tag(LIT_BY_DROPPED_TORCH)
            .addTag(BlockTags.LEAVES)
            .add(
                TFCBlocks.THATCH,
                TFCBlocks.LOG_PILE,
                TFCBlocks.PIT_KILN
            );

        tag(MINEABLE_WITH_PROPICK); // Empty
        tag(MINEABLE_WITH_CHISEL); // Empty
        tag(MINEABLE_WITH_HAMMER).addTag(BlockTags.LOGS);
        tag(MINEABLE_WITH_KNIFE).addTag(BlockTags.MINEABLE_WITH_HOE);
        tag(MINEABLE_WITH_SCYTHE).addTag(BlockTags.MINEABLE_WITH_HOE);
        tag(MINEABLE_WITH_GLASS_SAW)
            .addTags(Tags.Blocks.GLASS_BLOCKS, Tags.Blocks.GLASS_PANES)
            .add(TFCBlocks.COLORED_POURED_GLASS)
            .add(TFCBlocks.POURED_GLASS);
        tag(MINEABLE_WITH_HOE); // Empty

        tag(PROSPECTABLE).addTags(Tags.Blocks.ORES);

        tag(DIRT)
            .add(Blocks.DIRT)
            .add(TFCBlocks.SOIL.get(SoilBlockType.DIRT))
            .add(TFCBlocks.SOIL.get(SoilBlockType.ROOTED_DIRT));
        tag(GRASS)
            .add(Blocks.GRASS_BLOCK)
            .add(TFCBlocks.SOIL.get(SoilBlockType.GRASS));
        tag(FARMLANDS)
            .add(Blocks.FARMLAND)
            .add(TFCBlocks.SOIL.get(SoilBlockType.FARMLAND));
        tag(PATHS)
            .add(Blocks.DIRT_PATH)
            .add(TFCBlocks.SOIL.get(SoilBlockType.GRASS_PATH));
        tag(MUD)
            .add(Blocks.MUD)
            .add(TFCBlocks.SOIL.get(SoilBlockType.MUD));
        tag(MUD_BRICKS)
            .add(Blocks.MUD_BRICKS)
            .add(TFCBlocks.SOIL.get(SoilBlockType.MUD_BRICKS));
        tag(CLAYS)
            .addTags(KAOLIN_CLAYS)
            .add(TFCBlocks.SOIL.get(SoilBlockType.CLAY))
            .add(TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS));
        tag(KAOLIN_CLAYS).add(
            TFCBlocks.KAOLIN_CLAY_GRASS,
            TFCBlocks.WHITE_KAOLIN_CLAY,
            TFCBlocks.PINK_KAOLIN_CLAY,
            TFCBlocks.RED_KAOLIN_CLAY);

        tag(TREE_GROWS_ON).addTag(BlockTags.DIRT);
        tag(WILD_CROP_GROWS_ON).addTag(BlockTags.DIRT);
        tag(SPREADING_FRUIT_GROWS_ON).addTags(BlockTags.DIRT, FARMLANDS, Tags.Blocks.GRAVELS);
        tag(BUSH_PLANTABLE_ON).addTags(BlockTags.DIRT, FARMLANDS);
        tag(GRASS_PLANTABLE_ON)
            .addTags(BlockTags.DIRT, FARMLANDS, CLAYS)
            .add(TFCBlocks.PEAT, TFCBlocks.PEAT_GRASS);
        tag(SEA_BUSH_PLANTABLE_ON).addTags(BlockTags.DIRT, Tags.Blocks.GRAVELS, Tags.Blocks.SANDS);
        tag(HALOPHYTE_PLANTABLE_ON).addTag(BlockTags.DIRT);
        tag(CREEPING_STONE_PLANTABLE_ON).addTags(Tags.Blocks.STONES, STONES_SMOOTH, Tags.Blocks.COBBLESTONES);

        tag(RABBIT_RAIDABLE)
            .add(Blocks.CARROTS)
            .add(
                TFCBlocks.CROPS.get(Crop.CARROT),
                TFCBlocks.CROPS.get(Crop.CABBAGE)
            );
        tag(FOX_RAIDABLE)
            .add(TFCBlocks.STATIONARY_BUSHES)
            .add(TFCBlocks.CRANBERRY_BUSH);
        tag(PET_SITS_ON)
            .addTags(
                BlockTags.WOOL_CARPETS,
                BlockTags.WOOL,
                Tags.Blocks.CHESTS
            )
            .add(TFCBlocks.LARGE_VESSEL)
            .add(TFCBlocks.GLAZED_LARGE_VESSELS)
            .add(TFCBlocks.QUERN);
        tag(MONSTER_SPAWNS_ON)
            .addTags(
                BlockTags.DIRT,
                Tags.Blocks.STONES,
                Tags.Blocks.GRAVELS,
                Tags.Blocks.ORES
            )
            .add(Blocks.OBSIDIAN);
        tag(CONSUMES_TOOL_DURABILITY).add(TFCBlocks.PLANTS.values()
            .stream()
            .filter(b -> b.get().defaultBlockState().getDestroySpeed(empty(), BlockPos.ZERO) == 0f));
        tag(NATURAL_REGROWING_PLANTS).add(TFCBlocks.PLANTS);
        tag(ANIMAL_IGNORED_PLANTS).add(TFCBlocks.PLANTS.values()
            .stream()
            .filter(b -> b.get().getSpeedFactor() != 1.0f));

        tag(CLAY_INDICATORS).add(
            TFCBlocks.PLANTS.get(Plant.ATHYRIUM_FERN),
            TFCBlocks.PLANTS.get(Plant.CANNA),
            TFCBlocks.PLANTS.get(Plant.GOLDENROD),
            TFCBlocks.PLANTS.get(Plant.PAMPAS_GRASS),
            TFCBlocks.PLANTS.get(Plant.PEROVSKIA),
            TFCBlocks.PLANTS.get(Plant.WATER_CANNA));

        // Vanilla "corals" includes coral fans, + "coral_plants" (which includes corals), we mirror the same
        tag(SALT_WATER_CORAL_PLANTS).add(TFCBlocks.CORAL, Coral.BlockType.CORAL);
        tag(SALT_WATER_CORALS)
            .addTags(SALT_WATER_CORAL_PLANTS)
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL_FAN);
        tag(SALT_WATER_WALL_CORALS).add(TFCBlocks.CORAL, Coral.BlockType.CORAL_WALL_FAN);
        tag(HALOPHYTE).add(
            TFCBlocks.PLANTS.get(Plant.SEA_LAVENDER),
            TFCBlocks.PLANTS.get(Plant.CORDGRASS));
        tag(SINGLE_BLOCK_REPLACEABLE); // todo
        tag(TIDE_POOL_BLOCKS).add(
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.CLAM),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.MOLLUSK),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.MUSSEL),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.SEA_URCHIN));
        tag(KAOLIN_CLAY_REPLACEABLE).addTags(DIRT, Tags.Blocks.STONES, Tags.Blocks.GRAVELS);
        tag(KAOLIN_CLAY_REPLACEABLE)
            .addTags(DIRT, Tags.Blocks.GRAVELS)
            .add(Blocks.SNOW_BLOCK);
    }

    @Override
    protected BlockTagAppender tag(TagKey<Block> tag)
    {
        return new BlockTagAppender(getOrCreateRawBuilder(tag), modId);
    }

    @Override
    protected TagBuilder getOrCreateRawBuilder(TagKey<Block> tag)
    {
        if (existingFileHelper != null) existingFileHelper.trackGenerated(tag.location(), resourceType);
        return this.builders.computeIfAbsent(tag.location(), key -> new TagBuilder()
        {
            @Override
            public TagBuilder add(TagEntry entry)
            {
                Preconditions.checkArgument(!entry.getId().equals(BuiltInRegistries.ITEM.getDefaultKey()), "Adding air to block tag");
                return super.add(entry);
            }
        });
    }

    @SuppressWarnings("UnusedReturnValue")
    static class BlockTagAppender extends TagAppender<Block> implements Accessors
    {
        BlockTagAppender(TagBuilder builder, String modId)
        {
            super(builder, modId);
        }

        BlockTagAppender add(Block... blocks)
        {
            for (Block block : blocks) add(key(block));
            return this;
        }

        BlockTagAppender add(Stream<? extends Supplier<? extends Block>> blocks)
        {
            blocks.forEach(b -> add(key(b.get())));
            return this;
        }

        @SafeVarargs
        final <T extends IdHolder<? extends Block>> BlockTagAppender add(T... blocks)
        {
            return add(Arrays.stream(blocks));
        }

        /** Adds every TFC-added block matching the given predicate */
        BlockTagAppender addEveryTFC(Predicate<Block> predicate)
        {
            return add(TFCBlocks.BLOCKS.getEntries().stream().filter(e -> predicate.test(e.get())));
        }

        BlockTagAppender add(Map<?, ? extends IdHolder<? extends Block>> blocks)
        {
            blocks.values().forEach(this::add);
            return this;
        }

        BlockTagAppender add2(Map<?, ? extends Map<?, ? extends IdHolder<? extends Block>>> blocks)
        {
            blocks.values().forEach(m -> m.values().forEach(this::add));
            return this;
        }

        <V> BlockTagAppender add2(Map<?, ? extends Map<?, V>> blocks, Function<V, ? extends IdHolder<? extends Block>> ap)
        {
            blocks.values().forEach(m -> m.values().forEach(v -> add(ap.apply(v))));
            return this;
        }

        BlockTagAppender add3(Map<?, ? extends Map<?, ? extends Map<?, ? extends IdHolder<? extends Block>>>> blocks)
        {
            blocks.values().forEach(m1 -> m1.values().forEach(m2 -> m2.values().forEach(this::add)));
            return this;
        }

        BlockTagAppender addAll(Map<?, DecorationBlockHolder> blocks)
        {
            blocks.values().forEach(h -> add(h.slab(), h.stair(), h.wall()));
            return this;
        }

        BlockTagAppender addAll2(Map<?, ? extends Map<?, DecorationBlockHolder>> blocks)
        {
            blocks.values().forEach(m -> m.values().forEach(h -> add(h.slab(), h.stair(), h.wall())));
            return this;
        }

        <T1, T2, V extends IdHolder<? extends Block>> BlockTagAppender add(Map<T1, Map<T2, V>> blocks, T2 key)
        {
            return add(pivot(blocks, key));
        }

        <T, V extends IdHolder<? extends Block>> BlockTagAppender addOnly(Map<T, V> blocks, Predicate<T> key)
        {
            blocks.forEach((k, v) -> { if (key.test(k)) add(v); });
            return this;
        }

        <T1, T2, V extends IdHolder<? extends Block>> BlockTagAppender addOnly2(Map<T1, Map<T2, V>> blocks, Predicate<T2> key)
        {
            blocks.values().forEach(m -> addOnly(m, key));
            return this;
        }

        @SafeVarargs
        @SuppressWarnings("unchecked")
        final <K> BlockTagAppender addTags(Function<K, TagKey<Block>> apply, K... values)
        {
            return addTags(Arrays.stream(values).map(apply).toArray(TagKey[]::new));
        }

        @Override
        public BlockTagAppender addTag(TagKey<Block> tag)
        {
            return (BlockTagAppender) super.addTag(tag);
        }

        @Override
        @SafeVarargs
        public final BlockTagAppender addTags(TagKey<Block>... values)
        {
            return (BlockTagAppender) super.addTags(values);
        }

        BlockTagAppender remove(Block... blocks)
        {
            for (Block block : blocks) remove(key(block));
            return this;
        }

        private ResourceKey<Block> key(Block block)
        {
            return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
        }
    }
}
