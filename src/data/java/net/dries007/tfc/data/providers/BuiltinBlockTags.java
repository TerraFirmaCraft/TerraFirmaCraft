/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper.ResourceType;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.OreDeposit;
import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBellBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.crop.ICropBlock;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeLeavesBlock;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.IdHolder;
import net.dries007.tfc.world.biome.TFCBiomes;

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
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.CHISELED)
            .add(Blocks.BRICKS)
            .add(TFCBlocks.FIRE_BRICKS)
            .add(TFCBlocks.REINFORCED_FIRE_BRICKS);
        tag(BlockTags.WOODEN_BUTTONS).add(TFCBlocks.WOODS, Wood.BlockType.BUTTON);
        tag(BlockTags.STONE_BUTTONS).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.BUTTON);
        tag(BlockTags.WOODEN_DOORS).add(TFCBlocks.WOODS, Wood.BlockType.DOOR);
        tag(BlockTags.DOORS).add(TFCBlocks.FIREPROOF_DOOR);
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
        tag(BlockTags.WALLS).addEveryTFC(b -> b instanceof WallBlock);
        tag(BlockTags.LEAVES)
            .add(TFCBlocks.WOODS, Wood.BlockType.LEAVES)
            .addTags(FALLEN_LEAVES)
            .add(TFCBlocks.FRUIT_TREE_LEAVES);
        // Includes wooden trapdoors
        tag(BlockTags.TRAPDOORS).add(TFCBlocks.METALS, Metal.BlockType.TRAPDOOR);
        tag(BlockTags.DIRT).addTags(GRASS, DIRT, COARSE_DIRT, MUD);
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
        tag(BlockTags.WALL_POST_OVERRIDE).add(
            TFCBlocks.TORCH,
            TFCBlocks.DEAD_TORCH);
        tag(BlockTags.CLIMBABLE).add(
            TFCBlocks.PLANTS.get(Plant.JUNGLE_VINES).get(),
            TFCBlocks.PLANTS.get(Plant.HANGING_VINES).get(),
            TFCBlocks.PLANTS.get(Plant.HANGING_VINES_PLANT).get(),
            TFCBlocks.PLANTS.get(Plant.SPANISH_MOSS).get(),
            TFCBlocks.PLANTS.get(Plant.SPANISH_MOSS_PLANT).get(),
            TFCBlocks.PLANTS.get(Plant.LIANA).get(),
            TFCBlocks.PLANTS.get(Plant.LIANA_PLANT).get(),
            TFCBlocks.ROPE.get(),
            TFCBlocks.HANGING_ROPE.get()
        );
        tag(BlockTags.INFINIBURN_OVERWORLD).add(TFCBlocks.PIT_KILN);
        tag(BlockTags.INFINIBURN_END).add(TFCBlocks.PIT_KILN);
        tag(BlockTags.INFINIBURN_NETHER).add(TFCBlocks.PIT_KILN);
        tag(BlockTags.FENCE_GATES).add(TFCBlocks.WOODS, Wood.BlockType.FENCE_GATE);
        tag(BlockTags.BASE_STONE_OVERWORLD)
            .addTags(STONES_RAW, STONES_HARDENED);
        tag(BlockTags.STONE_ORE_REPLACEABLES).addTag(STONES_RAW); // Used for vanilla-like ore generation
        tag(BlockTags.OVERWORLD_CARVER_REPLACEABLES)
            // Already includes base stone overworld, which includes raw and hardened stone
            .addTags(Tags.Blocks.GRAVELS, Tags.Blocks.COBBLESTONES)
            .add(TFCBlocks.SANDSTONE, SandstoneBlockType.RAW)
            .remove(Blocks.WATER);
        tag(EXTRA_CAVE_CARVER_REPLACEABLE)
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.LIMESTONE).get(Rock.BlockType.RAW).get())
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.DOLOMITE).get(Rock.BlockType.RAW).get())
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.CHALK).get(Rock.BlockType.RAW).get())
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.MARBLE).get(Rock.BlockType.RAW).get())
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.BASALT).get(Rock.BlockType.RAW).get())
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.LIMESTONE).get(Rock.BlockType.GRAVEL).get())
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.DOLOMITE).get(Rock.BlockType.GRAVEL).get())
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.CHALK).get(Rock.BlockType.GRAVEL).get())
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.MARBLE).get(Rock.BlockType.GRAVEL).get())
            .add(TFCBlocks.ROCK_BLOCKS.get(Rock.BASALT).get(Rock.BlockType.GRAVEL).get());
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
                TFCBlocks.BARREL_RACK,
                TFCBlocks.MELON,
                TFCBlocks.PUMPKIN,
                TFCBlocks.ROTTEN_MELON,
                TFCBlocks.ROTTEN_PUMPKIN,
                TFCBlocks.JACK_O_LANTERN
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
                TFCBlocks.ROPE,
                TFCBlocks.HANGING_ROPE
            );
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add2(TFCBlocks.SANDSTONE)
            .addAll2(TFCBlocks.SANDSTONE_DECORATIONS)
            .add2(TFCBlocks.ORES)
            .add3(TFCBlocks.GRADED_ORES)
            .add(TFCBlocks.SMALL_ORES)
            .add(TFCBlocks.HALITE)
            .add(TFCBlocks.LIGNITE)
            .add(TFCBlocks.BITUMINOUS_COAL)
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
            .add(TFCBlocks.FIRE_BRICK_SHELF)
            .add(TFCBlocks.FIREPROOF_DOOR)
            .add(TFCBlocks.FIREBOX)
            .add(TFCBlocks.VANE)
            .add(TFCBlocks.ANEMOMETER)
            .add(TFCBlocks.STOVE)
            .add(TFCBlocks.STOVE_POT)
            .add(TFCBlocks.STOVE)
            .add(TFCBlocks.STOVE_POT)
            .add(
                TFCBlocks.ICICLE,
                TFCBlocks.SEA_ICE,
                TFCBlocks.ICE_PILE,
                TFCBlocks.CALCITE,
                TFCBlocks.PLAIN_ALABASTER,
                TFCBlocks.PLAIN_ALABASTER_BRICKS,
                TFCBlocks.PLAIN_POLISHED_ALABASTER,
                TFCBlocks.FIRE_BRICKS,
                TFCBlocks.REINFORCED_FIRE_BRICKS,
                TFCBlocks.QUERN,
                TFCBlocks.CRUCIBLE,
                TFCBlocks.BLOOMERY,
                TFCBlocks.BLOOM,
                TFCBlocks.POT,
                TFCBlocks.GRILL,
                TFCBlocks.FIREPIT,
                TFCBlocks.INGOT_PILE,
                TFCBlocks.DOUBLE_INGOT_PILE,
                TFCBlocks.BLAST_FURNACE,
                TFCBlocks.CERAMIC_BOWL,
                TFCBlocks.CRANKSHAFT,
                TFCBlocks.STEEL_PIPE,
                TFCBlocks.STEEL_PUMP,
                TFCBlocks.TRIP_HAMMER,
                TFCBlocks.POWER_LOOM,
                TFCBlocks.BRONZE_BELL,
                TFCBlocks.BRASS_BELL,
                TFCBlocks.LARGE_VESSEL,
                TFCBlocks.STEEL_ROPE_ANCHOR
            )
            .add(TFCBlocks.MOLD_TABLE)
            .add(TFCBlocks.CHANNEL)
            .add(TFCBlocks.CALENDAR_CLOCK)
            .add(TFCBlocks.THERMOMETER)
            .add(TFCBlocks.CREATIVE_MOTOR);
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
                TFCBlocks.SMOOTH_MUD_BRICKS,
                TFCBlocks.HARDENED_CLAY
            );
        // Sword Efficient in vanilla is 'mines faster with sword', so we don't include anything extra in there,
        // since again, we typically want to refer to sharp tools instead
        // Needs Stone Tool is ~ Copper, which is every TFC pickaxe, so we don't bother here
        // "Incorrect For Tool" includes the "Needs For Tool", so we don't touch, since we don't add levels
        tag(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON).add(TFCBlocks.SEA_ICE, TFCBlocks.ICE_PILE).add(TFCBlocks.MAGMA_BLOCKS);
        tag(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON).add(TFCBlocks.SOIL.get(SoilBlockType.MUD));
        tag(BlockTags.REPLACEABLE).addEveryTFC(e -> e.defaultBlockState().canBeReplaced());

        // ===== Common Tags ===== //

        final Function<String, TagKey<Block>> c = path -> commonTagOf(Registries.BLOCK, path);

        //Anvils

        final TagKey<Block> anvilsTag = c.apply("anvils");

        TFCBlocks.METALS.forEach((metal, blocks) -> {
            if (metal.allParts())
            {
                final TagKey<Block> anvilTag = c.apply("anvils/" + metal.getSerializedName());

                tag(anvilTag)
                    .add(blocks.get(Metal.BlockType.ANVIL));

                tag(anvilsTag)
                    .addTag(anvilTag);
            }
        });

        tag(c.apply("anvils/stone"))
            .add(TFCBlocks.ROCK_ANVILS);

        tag(anvilsTag)
            .addTag(c.apply("anvils/stone"));

        //Barrels

        tag(Tags.Blocks.BARRELS_WOODEN).add(TFCBlocks.WOODS, Wood.BlockType.BARREL);

        //Bars

        final TagKey<Block> barsTag = c.apply("bars");

        TFCBlocks.METALS.forEach((metal, blocks) -> {
            if (metal.allParts())
            {
                final TagKey<Block> metalBarsTag = c.apply("bars/" + metal.getSerializedName());

                tag(metalBarsTag)
                    .add(blocks.get(Metal.BlockType.BARS));

                tag(barsTag)
                    .addTag(metalBarsTag);
            }
        });

        //Bookshelves

        tag(c.apply("bookshelves/wooden"))
            .add(TFCBlocks.WOODS, Wood.BlockType.BOOKSHELF);

        tag(c.apply("bookshelves"))
            .addTag(c.apply("bookshelves/wooden"));

        //Bricks

        final TagKey<Block> bricksTag = c.apply("bricks");

        tag(c.apply("bricks/plaster"))
            .add(TFCBlocks.ALABASTER_BRICKS)
            .addAll(TFCBlocks.ALABASTER_BRICK_DECORATIONS);

        tag(c.apply("bricks/mud"))
            .add(TFCBlocks.SOIL.get(SoilBlockType.MUD_BRICKS))
            .addAll(TFCBlocks.MUD_BRICK_DECORATIONS);

        TFCBlocks.ROCK_BLOCKS.forEach((rock, blocks) -> {
            final TagKey<Block> rockBricksTag = c.apply("bricks/" + rock.getSerializedName());
            final Map<Rock.BlockType, DecorationBlockHolder> decorations = TFCBlocks.ROCK_DECORATIONS.get(rock);

            tag(rockBricksTag).add(
                blocks.get(Rock.BlockType.BRICKS),
                blocks.get(Rock.BlockType.MOSSY_BRICKS),
                blocks.get(Rock.BlockType.CRACKED_BRICKS),
                blocks.get(Rock.BlockType.CHISELED),
                blocks.get(Rock.BlockType.AQUEDUCT),
                decorations.get(Rock.BlockType.BRICKS).slab(),
                decorations.get(Rock.BlockType.BRICKS).stair(),
                decorations.get(Rock.BlockType.BRICKS).wall(),
                decorations.get(Rock.BlockType.MOSSY_BRICKS).slab(),
                decorations.get(Rock.BlockType.MOSSY_BRICKS).stair(),
                decorations.get(Rock.BlockType.MOSSY_BRICKS).wall(),
                decorations.get(Rock.BlockType.CRACKED_BRICKS).slab(),
                decorations.get(Rock.BlockType.CRACKED_BRICKS).stair(),
                decorations.get(Rock.BlockType.CRACKED_BRICKS).wall()
            );

            tag(bricksTag)
                .addTag(rockBricksTag);
        });

        tag(c.apply("bricks/chiseled"))
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.CHISELED);

        tag(c.apply("bricks/cracked"))
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.CRACKED_BRICKS)
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.CRACKED_BRICKS)
                .values().stream().map(DecorationBlockHolder::slab))
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.CRACKED_BRICKS)
                .values().stream().map(DecorationBlockHolder::stair))
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.CRACKED_BRICKS)
                .values().stream().map(DecorationBlockHolder::wall));

        tag(c.apply("bricks/mossy"))
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_BRICKS)
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.MOSSY_BRICKS)
                .values().stream().map(DecorationBlockHolder::slab))
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.MOSSY_BRICKS)
                .values().stream().map(DecorationBlockHolder::stair))
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.MOSSY_BRICKS)
                .values().stream().map(DecorationBlockHolder::wall));

        tag(c.apply("bricks/mud"))
            .add(TFCBlocks.SMOOTH_MUD_BRICKS);

        tag(c.apply("bricks/plaster"))
            .add(TFCBlocks.PLAIN_ALABASTER_BRICKS);

        tag(c.apply("bricks/fire")).add(
            TFCBlocks.FIRE_BRICKS, TFCBlocks.REINFORCED_FIRE_BRICKS,
            TFCBlocks.FIRE_BRICK_SHELF
        );

        tag(c.apply("bricks/aqueduct"))
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.AQUEDUCT);

        tag(bricksTag).addTags(
            c.apply("bricks/plaster"),
            c.apply("bricks/fire"),
            c.apply("bricks/mud"),
            c.apply("bricks/mossy"),
            c.apply("bricks/cracked"),
            c.apply("bricks/chiseled"),
            c.apply("bricks/aqueduct")
        );

        //Buttons

        tag(c.apply("buttons/wooden"))
            .add(TFCBlocks.WOODS, Wood.BlockType.BUTTON);
        tag(c.apply("buttons/stone"))
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.BUTTON);

        tag(c.apply("buttons")).addTags(
            c.apply("buttons/wooden"),
            c.apply("buttons/stone")
        );

        //Candles

        final TagKey<Block> candlesTag = c.apply("candles");

        TFCBlocks.DYED_CANDLE.forEach((color, candle) -> {
            final TagKey<Block> coloredCandleTag = c.apply("candles/" + color.getSerializedName());
            final TagKey<Block> dyedTag = c.apply("dyed/" + color.getSerializedName());

            tag(coloredCandleTag)
                .add(candle.key());

            tag(candlesTag)
                .addTag(coloredCandleTag);

            tag(dyedTag)
                .add(candle.key());
        });

        tag(candlesTag)
            .add(TFCBlocks.CANDLE);

        //Chains

        TFCBlocks.METALS.forEach((metal, blocks) -> {
            if (metal.allParts())
            {
                final TagKey<Block> chainTag = c.apply("chains/" + metal.getSerializedName());

                tag(chainTag)
                    .add(blocks.get(Metal.BlockType.CHAIN));

                tag(Tags.Blocks.CHAINS)
                    .addTag(chainTag);
            }
        });

        //Chests

        tag(Tags.Blocks.CHESTS_WOODEN)
            .add(TFCBlocks.WOODS, Wood.BlockType.CHEST)
            .add(TFCBlocks.WOODS, Wood.BlockType.TRAPPED_CHEST);

        tag(Tags.Blocks.CHESTS_TRAPPED)
            .add(TFCBlocks.WOODS, Wood.BlockType.TRAPPED_CHEST);

        //Clays

        tag(c.apply("clays/normal"))
            .add(TFCBlocks.SOIL.get(SoilBlockType.CLAY))
            .add(TFCBlocks.SOIL.get(SoilBlockType.CLAY_DUFF))
            .add(TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS));
        tag(c.apply("clays/kaolin")).add(
            TFCBlocks.PINK_KAOLIN_CLAY,
            TFCBlocks.RED_KAOLIN_CLAY,
            TFCBlocks.WHITE_KAOLIN_CLAY,
            TFCBlocks.KAOLIN_CLAY_GRASS
        );
        tag(c.apply("clays/fire")).add(TFCBlocks.FIRE_CLAY_BLOCK);
        tag(c.apply("clays/hardened")).add(TFCBlocks.HARDENED_CLAY);

        tag(c.apply("clays")).addTags(
            c.apply("clays/normal"),
            c.apply("clays/kaolin"),
            c.apply("clays/fire"),
            c.apply("clays/hardened")
        );

        //Cobblestones

        final TagKey<Block> cobblestonesTag = c.apply("cobblestones");

        TFCBlocks.ROCK_BLOCKS.forEach((rock, blocks) -> {
            final TagKey<Block> rockCobblestonesTag = c.apply("cobblestones/" + rock.getSerializedName());
            final Map<Rock.BlockType, DecorationBlockHolder> decorations = TFCBlocks.ROCK_DECORATIONS.get(rock);

            tag(rockCobblestonesTag).add(
                blocks.get(Rock.BlockType.COBBLE),
                blocks.get(Rock.BlockType.MOSSY_COBBLE),
                decorations.get(Rock.BlockType.COBBLE).slab(),
                decorations.get(Rock.BlockType.COBBLE).stair(),
                decorations.get(Rock.BlockType.COBBLE).wall(),
                decorations.get(Rock.BlockType.MOSSY_COBBLE).slab(),
                decorations.get(Rock.BlockType.MOSSY_COBBLE).stair(),
                decorations.get(Rock.BlockType.MOSSY_COBBLE).wall()
            );

            tag(cobblestonesTag)
                .addTag(rockCobblestonesTag);
        });

        tag(c.apply("cobblestones/mossy"))
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_COBBLE)
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.MOSSY_COBBLE)
                .values().stream().map(DecorationBlockHolder::slab))
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.MOSSY_COBBLE)
                .values().stream().map(DecorationBlockHolder::stair))
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.MOSSY_COBBLE)
                .values().stream().map(DecorationBlockHolder::wall));

        //Corals
        //c:corals, c:corals/dead, c:corals/living, c:corals/block, c:corals/fan, c:coral/plant

        tag(c.apply("corals/plant"))
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL);

        tag(c.apply("corals/fan"))
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL_FAN)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL_FAN)
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL_WALL_FAN)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL_WALL_FAN);

        tag(c.apply("corals/block")).add(
            Blocks.BRAIN_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK,
            Blocks.BUBBLE_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK,
            Blocks.FIRE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK,
            Blocks.HORN_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK,
            Blocks.TUBE_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL_BLOCK
        );

        tag(c.apply("corals/living")).add(
                Blocks.BRAIN_CORAL_BLOCK,
                Blocks.BUBBLE_CORAL_BLOCK,
                Blocks.FIRE_CORAL_BLOCK,
                Blocks.HORN_CORAL_BLOCK,
                Blocks.TUBE_CORAL_BLOCK)
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL)
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL_FAN)
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL_WALL_FAN);

        tag(c.apply("corals/dead")).add(
                Blocks.DEAD_BRAIN_CORAL_BLOCK,
                Blocks.DEAD_BUBBLE_CORAL_BLOCK,
                Blocks.DEAD_FIRE_CORAL_BLOCK,
                Blocks.DEAD_HORN_CORAL_BLOCK,
                Blocks.DEAD_TUBE_CORAL_BLOCK)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL_FAN)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL_WALL_FAN);

        tag(c.apply("corals")).addTags(
            c.apply("corals/plant"),
            c.apply("corals/fan"),
            c.apply("corals/block"),
            c.apply("corals/living"),
            c.apply("corals/dead")
        );

        //Crates

        tag(c.apply("crates/wooden"))
            .add(TFCBlocks.WOODS, Wood.BlockType.CRATE);

        tag(c.apply("crates"))
            .addTag(c.apply("crates/wooden"));

        //Crops

        final TagKey<Block> cropsTag = c.apply("crops");

        TFCBlocks.CROPS.forEach((crop, cropBlock) -> {
            final TagKey<Block> cropTag = c.apply("crops/" + crop.getSerializedName());

            tag(cropTag)
                .add(
                    cropBlock,
                    TFCBlocks.DEAD_CROPS.get(crop),
                    TFCBlocks.WILD_CROPS.get(crop)
                );

            tag(cropsTag)
                .addTag(cropTag);
        });

        tag(c.apply("crops/corn"))
            .addTag(c.apply("crops/" + Crop.MAIZE.getSerializedName()));

        tag(c.apply("crops/beetroot"))
            .addTag(c.apply("crops/" + Crop.BEET.getSerializedName()));

        tag(c.apply("crops/pumpkin"))
            .add(TFCBlocks.PUMPKIN);

        tag(c.apply("crops/melon"))
            .add(TFCBlocks.MELON);

        tag(cropsTag).addTags(
            c.apply("crops/corn"),
            c.apply("crops/beetroot"),
            c.apply("crops/pumpkin"),
            c.apply("crops/melon")
        );

        TFCBlocks.FRUIT_TREE_LEAVES.forEach((crop, cropBlock) -> {
            final TagKey<Block> cropTag = c.apply("crops/" + crop.getSerializedName());

            tag(cropTag).add(
                cropBlock,
                TFCBlocks.FRUIT_TREE_BRANCHES.get(crop),
                TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.get(crop),
                TFCBlocks.FRUIT_TREE_SAPLINGS.get(crop)
            );

            tag(cropsTag)
                .addTag(cropTag);

        });

        TFCBlocks.SPREADING_BUSHES.forEach((crop, cropBlock) ->{
            final TagKey<Block> cropTag = c.apply("crops/" + crop.name().toLowerCase(Locale.ROOT));

            tag(cropTag).add(
                cropBlock,
                TFCBlocks.SPREADING_CANES.get(crop)
            );

            tag(cropsTag)
                .addTag(cropTag);

        });

        TFCBlocks.STATIONARY_BUSHES.forEach((crop, cropBlock) ->{
            final TagKey<Block> cropTag = c.apply("crops/" + crop.name().toLowerCase(Locale.ROOT));

            tag(cropTag).add(
                cropBlock
            );

            tag(cropsTag)
                .addTag(cropTag);

        });

        tag(c.apply("crops/apple")).add(
            TFCBlocks.FRUIT_TREE_SAPLINGS.get(FruitBlocks.Tree.GREEN_APPLE), TFCBlocks.FRUIT_TREE_SAPLINGS.get(FruitBlocks.Tree.RED_APPLE),
            TFCBlocks.FRUIT_TREE_LEAVES.get(FruitBlocks.Tree.GREEN_APPLE), TFCBlocks.FRUIT_TREE_LEAVES.get(FruitBlocks.Tree.RED_APPLE),
            TFCBlocks.FRUIT_TREE_BRANCHES.get(FruitBlocks.Tree.GREEN_APPLE), TFCBlocks.FRUIT_TREE_BRANCHES.get(FruitBlocks.Tree.RED_APPLE),
            TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.get(FruitBlocks.Tree.GREEN_APPLE), TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.get(FruitBlocks.Tree.RED_APPLE)
        );

        tag(c.apply("crops/banana")).add(
            TFCBlocks.BANANA_PLANT, TFCBlocks.BANANA_SAPLING
        );

        tag(cropsTag).addTags(
            c.apply("crops/apple"),
            c.apply(("crops/banana"))
        );

        //Doors

        tag(c.apply("doors/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.DOOR);
        tag(c.apply("doors/iron"))
            .add(Blocks.IRON_DOOR)
            .add(TFCBlocks.FIREPROOF_DOOR);
        tag(c.apply("doors")).addTags(
            c.apply("doors/wooden"),
            c.apply("doors/iron")
        );

        //Dyed
        //c:dyed, c:dyed/COLOR

        final TagKey<Block> dyedTag = c.apply("dyed");

        TFCBlocks.RAW_ALABASTER.forEach((color, rawAlabaster) -> {
            final TagKey<Block> colorDyedTag = c.apply("dyed/" + color.getSerializedName());

            final DecorationBlockHolder brickDecorations = TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color);
            final DecorationBlockHolder polishedDecorations = TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color);

            tag(colorDyedTag).add(
                brickDecorations.slab().key(),
                brickDecorations.stair().key(),
                brickDecorations.wall().key(),
                TFCBlocks.POLISHED_ALABASTER.get(color).key(),
                polishedDecorations.slab().key(),
                polishedDecorations.stair().key(),
                polishedDecorations.wall().key(),
                rawAlabaster.key(),
                TFCBlocks.DYED_CANDLE_CAKES.get(color).key(),
                TFCBlocks.GLAZED_LARGE_VESSELS.get(color).key(),
                TFCBlocks.STAINED_WATTLE.get(color).key(),
                TFCBlocks.ALABASTER_BRICKS.get(color).key()
            );

            tag(dyedTag)
                .addTag(colorDyedTag);
        });

        //Fences

        tag(Tags.Blocks.FENCES_WOODEN)
            .add(TFCBlocks.WOODS, Wood.BlockType.FENCE)
            .add(TFCBlocks.WOODS, Wood.BlockType.LOG_FENCE);
        tag(Tags.Blocks.FENCE_GATES_WOODEN)
            .add(TFCBlocks.WOODS, Wood.BlockType.FENCE_GATE);

        //Flowers

        tag(c.apply("flowers/tall")).add(
            TFCBlocks.PLANTS.get(Plant.AZALEA), TFCBlocks.PLANTS.get(Plant.BEAR_GRASS), TFCBlocks.PLANTS.get(Plant.CANNA),
            TFCBlocks.PLANTS.get(Plant.FOXGLOVE), TFCBlocks.PLANTS.get(Plant.LILAC), TFCBlocks.PLANTS.get(Plant.MOUNTAIN_HULLWORT),
            TFCBlocks.PLANTS.get(Plant.PALASH), TFCBlocks.PLANTS.get(Plant.ROSE), TFCBlocks.PLANTS.get(Plant.SAPPHIRE_TOWER),
            TFCBlocks.PLANTS.get(Plant.SEA_LAVENDER), TFCBlocks.PLANTS.get(Plant.STRELITZIA), TFCBlocks.PLANTS.get(Plant.SUNFLOWER),
            TFCBlocks.PLANTS.get(Plant.WATER_CANNA)
        );

        tag(c.apply("flowers/small")).add(
            TFCBlocks.PLANTS.get(Plant.ALLIUM), TFCBlocks.PLANTS.get(Plant.ANTHURIUM), TFCBlocks.PLANTS.get(Plant.BLACK_ORCHID),
            TFCBlocks.PLANTS.get(Plant.BLOOD_LILY), TFCBlocks.PLANTS.get(Plant.BLUE_GINGER), TFCBlocks.PLANTS.get(Plant.BLUE_ORCHID),
            TFCBlocks.PLANTS.get(Plant.BUTTERCUP), TFCBlocks.PLANTS.get(Plant.BUTTERFLY_MILKWEED), TFCBlocks.PLANTS.get(Plant.CALENDULA),
            TFCBlocks.PLANTS.get(Plant.CORNFLOWER), TFCBlocks.PLANTS.get(Plant.DANDELION), TFCBlocks.PLANTS.get(Plant.DESERT_FLAME),
            TFCBlocks.PLANTS.get(Plant.EDELWEISS), TFCBlocks.PLANTS.get(Plant.FIELD_HORSETAIL), TFCBlocks.PLANTS.get(Plant.GOLDENROD),
            TFCBlocks.PLANTS.get(Plant.GRAPE_HYACINTH), TFCBlocks.PLANTS.get(Plant.GUZMANIA), TFCBlocks.PLANTS.get(Plant.HEATHER),
            TFCBlocks.PLANTS.get(Plant.HELICONIA), TFCBlocks.PLANTS.get(Plant.HOUSTONIA), TFCBlocks.PLANTS.get(Plant.KANGAROO_PAW),
            TFCBlocks.PLANTS.get(Plant.LABRADOR_TEA), TFCBlocks.PLANTS.get(Plant.LILY_OF_THE_VALLEY), TFCBlocks.PLANTS.get(Plant.MAIDEN_PINK),
            TFCBlocks.PLANTS.get(Plant.MEADS_MILKWEED), TFCBlocks.PLANTS.get(Plant.MORNING_GLORY), TFCBlocks.PLANTS.get(Plant.NASTURTIUM),
            TFCBlocks.PLANTS.get(Plant.OXEYE_DAISY), TFCBlocks.PLANTS.get(Plant.PENWORTEL), TFCBlocks.PLANTS.get(Plant.PEROVSKIA),
            TFCBlocks.PLANTS.get(Plant.POPPY), TFCBlocks.PLANTS.get(Plant.PRIMROSE), TFCBlocks.PLANTS.get(Plant.PULSATILLA),
            TFCBlocks.PLANTS.get(Plant.PURPLE_WATER_LILY), TFCBlocks.PLANTS.get(Plant.QANTU), TFCBlocks.PLANTS.get(Plant.RAMIREZELLA),
            TFCBlocks.PLANTS.get(Plant.RAMUNDA), TFCBlocks.PLANTS.get(Plant.SACRED_DATURA), TFCBlocks.PLANTS.get(Plant.SILVER_SPURFLOWER),
            TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_PINK), TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_RED), TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_WHITE),
            TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_YELLOW), TFCBlocks.PLANTS.get(Plant.TANK_BROMELIAD), TFCBlocks.PLANTS.get(Plant.TRILLIUM),
            TFCBlocks.PLANTS.get(Plant.TROPICAL_MILKWEED), TFCBlocks.PLANTS.get(Plant.TULIP_ORANGE), TFCBlocks.PLANTS.get(Plant.TULIP_PINK),
            TFCBlocks.PLANTS.get(Plant.TULIP_RED), TFCBlocks.PLANTS.get(Plant.TULIP_WHITE), TFCBlocks.PLANTS.get(Plant.WHITE_WATER_LILY),
            TFCBlocks.PLANTS.get(Plant.YELLOW_SAXIFRAGE), TFCBlocks.PLANTS.get(Plant.YELLOW_WATER_LILY), TFCBlocks.PLANTS.get(Plant.YUCCA)
        );

        tag(c.apply("flowers")).addTags(
            c.apply("flowers/tall"),
            c.apply("flowers/small")
        );

        //Foods

        tag(c.apply("foods/cake"))
            .add(TFCBlocks.CAKE)
            .add(TFCBlocks.DYED_CANDLE_CAKES);
        tag(c.apply("foods/edible_when_placed"))
            .add(TFCBlocks.CAKE)
            .add(TFCBlocks.DYED_CANDLE_CAKES);
        tag(c.apply("cake"))
            .add(TFCBlocks.CAKE)
            .add(TFCBlocks.DYED_CANDLE_CAKES);
        tag(c.apply("foods")).addTags(
            c.apply("foods/cake"),
            c.apply("foods/edible_when_placed")
        );

        //Grates

        final TagKey<Block> gratesTag = c.apply("grates");

        TFCBlocks.METALS.forEach((metal, blocks) -> {
            if (metal.allParts())
            {
                final TagKey<Block> metalGratesTag = c.apply("grates/" + metal.getSerializedName());

                tag(metalGratesTag)
                    .add(blocks.get(Metal.BlockType.GRATE));

                if (metal.weatheredParts())
                {
                    tag(metalGratesTag)
                        .add(blocks.get(Metal.BlockType.EXPOSED_GRATE))
                        .add(blocks.get(Metal.BlockType.OXIDIZED_GRATE))
                        .add(blocks.get(Metal.BlockType.WEATHERED_GRATE));
                }
            }
        });

        tag(c.apply("grates/normal"))
            .add(TFCBlocks.METALS, Metal.BlockType.GRATE);

        tag(c.apply("grates/exposed"))
            .add(TFCBlocks.METALS, Metal.BlockType.EXPOSED_GRATE);

        tag(c.apply("grates/oxidized"))
            .add(TFCBlocks.METALS, Metal.BlockType.OXIDIZED_GRATE);

        tag(c.apply("grates/weathered"))
            .add(TFCBlocks.METALS, Metal.BlockType.WEATHERED_GRATE);

        tag(gratesTag).addTags(
            c.apply("grates/normal"),
            c.apply("grates/exposed"),
            c.apply("grates/oxidized"),
            c.apply("grates/weathered")
        );

        //Gravels

        final TagKey<Block> gravelsTag = c.apply("gravels");

        TFCBlocks.ROCK_BLOCKS.forEach((rock, blocks) -> {
            final TagKey<Block> rockGravelTag = c.apply("gravels/" + rock.getSerializedName());

            tag(rockGravelTag)
                .add(blocks.get(Rock.BlockType.GRAVEL));

            tag(gravelsTag)
                .addTag(rockGravelTag);
        });

        //Ice

        tag(c.apply("ice"))
            .add(TFCBlocks.SEA_ICE)
            .add(Blocks.ICE);

        //Icicle

        tag(c.apply("icicle"))
            .add(TFCBlocks.ICICLE);

        //Item Piles
        //c:item_piles, c:item_piles/TYPE

        tag(c.apply("item_piles/double_ingot"))
            .add(TFCBlocks.DOUBLE_INGOT_PILE);

        tag(c.apply("item_piles/ingot"))
            .add(TFCBlocks.INGOT_PILE);

        tag(c.apply("item_piles/log"))
            .add(TFCBlocks.LOG_PILE, TFCBlocks.BURNING_LOG_PILE);

        tag(c.apply("item_piles")).addTags(
            c.apply("item_piles/double_ingot"),
            c.apply("item_piles/ingot"),
            c.apply("item_piles/log")
        );

        //Magma

        tag(c.apply("magma"))
            .add(TFCBlocks.MAGMA_BLOCKS);

        // Ores
        // We don't include "ore_bearing_ground/???" tags, because they are specific to stone (or known vanilla stones) only
        // Also ignore "ore_rates/???" because unsure how they are supposed to apply...
        // Ore Rates added, singular means one item dropped when mined, plural means multiple items dropped when mined (ie lapis, copper, redstone etc)
        // For ores, we group ores by metal, not by ore. So ores/copper, not ores/tetrahedrite
        // For graded ores, we add ores/<metal>/grade, and include all grades in the main ore tag
        //c:ores c:ores/METAL, c:ores_in_ground/ROCK, c:ore_rates/RATE, c:ores/METAL/GRADE

        final TagKey<Block> oresInGroundTag = c.apply("ores_in_ground");

        //Deposits

        for (OreDeposit dep : OreDeposit.values())
        {
            final String metalName = dep == OreDeposit.CASSITERITE ? "tin"
                : dep == OreDeposit.NATIVE_COPPER ? "copper"
                : dep == OreDeposit.NATIVE_GOLD ? "gold"
                : dep == OreDeposit.NATIVE_SILVER ? "silver"
                : dep.name().toLowerCase(Locale.ROOT);

            final TagKey<Block> metalOreTag = c.apply("ores/" + metalName);
            final TagKey<Block> smallMetalOreTag = c.apply("ores/" + metalName + "/small");

            for (Rock rock : Rock.values())
            {
                final var deposit = TFCBlocks.ORE_DEPOSITS.get(rock).get(dep);

                tag(c.apply("ores_in_ground/gravel"))
                    .add(deposit);

                tag(Tags.Blocks.ORE_RATES_SPARSE)
                    .add(deposit);

                tag(smallMetalOreTag)
                    .add(deposit);
            }
            tag(metalOreTag)
                .addTag(smallMetalOreTag);

            tag(Tags.Blocks.ORES)
                .addTag(metalOreTag);
        }
        tag(oresInGroundTag)
            .addTag(c.apply("ores_in_ground/gravel"));

        //Regular Ores
        for (Rock rock : Rock.values())
        {
            final TagKey<Block> oresInRockTag = c.apply("ores_in_ground/" + rock.getSerializedName());

            for (Ore ore : Ore.values())
            {
                if (ore.isGraded())
                {
                    final String metalName = ore.metal() == Metal.CAST_IRON ? "iron" : ore.metal().getSerializedName();
                    final TagKey<Block> metalOreTag = c.apply("ores/" + metalName);

                    for (Ore.Grade grade : Ore.Grade.values())
                    {
                        final String gradeName = grade.name().toLowerCase(Locale.ROOT);
                        final TagKey<Block> gradedOreTag = c.apply("ores/" + metalName + "/" + gradeName);
                        final var oreBlock = TFCBlocks.GRADED_ORES.get(rock).get(ore).get(grade);

                        tag(gradedOreTag)
                            .add(oreBlock);

                        tag(Tags.Blocks.ORE_RATES_SINGULAR)
                            .add(oreBlock);

                        tag(oresInRockTag)
                            .add(oreBlock);

                        tag(metalOreTag)
                            .addTag(gradedOreTag);
                    }

                    tag(Tags.Blocks.ORES)
                        .addTag(metalOreTag);
                }
                else if (ore.hasBlock())
                {
                    final String oreName = ore.name().toLowerCase(Locale.ROOT);
                    final TagKey<Block> oreTag = c.apply("ores/" + oreName);
                    final var oreBlock = TFCBlocks.ORES.get(rock).get(ore);

                    tag(oreTag)
                        .add(oreBlock);

                    tag(Tags.Blocks.ORE_RATES_SINGULAR)
                        .add(oreBlock);

                    tag(oresInRockTag)
                        .add(oreBlock);

                    tag(Tags.Blocks.ORES)
                        .addTag(oreTag);
                }
            }

            tag(oresInGroundTag)
                .addTag(oresInRockTag);
        }

        //Small Ores
        for (var entry : TFCBlocks.SMALL_ORES.entrySet())
        {
            final Ore ore = entry.getKey();
            final var smallOre = entry.getValue();

            final Metal metal = ore.metal();
            final String metalName = metal == Metal.CAST_IRON ? "iron" : metal.getSerializedName();

            final TagKey<Block> smallOreTag = c.apply("ores/" + metalName + "/small");
            final TagKey<Block> metalOreTag = c.apply("ores/" + metalName);

            tag(smallOreTag)
                .add(smallOre);

            tag(Tags.Blocks.ORE_RATES_SINGULAR)
                .add(smallOre);

            tag(metalOreTag)
                .addTag(smallOreTag);
        }

        //No Block
        tag(c.apply("ores/salt")).add(
            TFCBlocks.HALITE,
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.SALT_LICK)
        );
        tag(c.apply("ores/coal")).add(
            TFCBlocks.LIGNITE,
            TFCBlocks.BITUMINOUS_COAL
        );
        tag(c.apply("ores/flint"))
            .add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.FLINT))
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.GRAVEL);

        tag(Tags.Blocks.ORE_RATES_SINGULAR).add(
            TFCBlocks.LIGNITE,
            TFCBlocks.BITUMINOUS_COAL
        );

        tag(Tags.Blocks.ORE_RATES_DENSE).add(
            TFCBlocks.HALITE
        );

        tag(Tags.Blocks.ORE_RATES_SPARSE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.GRAVEL);

        tag(c.apply("ores")).addTags(
            c.apply("ores/salt"),
            c.apply("ores/coal"),
            c.apply(("ores/flint"))
        );

        //Lamps

        final TagKey<Block> lampsTag = c.apply("lamps");

        TFCBlocks.METALS.forEach((metal, blocks) -> {
            if (metal.allParts())
            {
                final TagKey<Block> metalLampTag = c.apply("lamps/" + metal.getSerializedName());

                tag(metalLampTag)
                    .add(blocks.get(Metal.BlockType.LAMP));

                tag(lampsTag)
                    .addTag(metalLampTag);
            }
        });

        //Leaves
        //c:leaves

        tag(c.apply("leaves"))
            .add(TFCBlocks.WOODS, Wood.BlockType.LEAVES)
            .add(TFCBlocks.WOODS, Wood.BlockType.FALLEN_LEAVES);

        //Logs
        //c:logs, c:logs/WOOD

        final TagKey<Block> logsTag = c.apply("logs");

        TFCBlocks.WOODS.forEach((wood, blocks) -> {
            final TagKey<Block> woodLogsTag = c.apply("logs/" + wood.getSerializedName());

            tag(woodLogsTag)
                .add(blocks.get(Wood.BlockType.LOG));

            tag(logsTag)
                .addTag(woodLogsTag);
        });

        //Peat

        tag(c.apply("peat"))
            .add(TFCBlocks.PEAT, TFCBlocks.PEAT_GRASS);

        //Pipes

        tag(c.apply("pipes/fluid"))
            .add(TFCBlocks.STEEL_PIPE);

        tag(c.apply("pipes"))
            .addTag(c.apply("pipes/fluid"));

        //Planks
        //c:planks, c:planks/WOOD

        final TagKey<Block> planksTag = c.apply("planks");

        TFCBlocks.WOODS.forEach((wood, blocks) -> {
            final TagKey<Block> woodPlanksTag = c.apply("planks/" + wood.getSerializedName());

            tag(woodPlanksTag).add(
                blocks.get(Wood.BlockType.PLANKS),
                blocks.get(Wood.BlockType.SLAB),
                blocks.get(Wood.BlockType.STAIRS)
            );

            tag(planksTag)
                .addTag(woodPlanksTag);
        });

        //Plants
        //c:plants, c:plants/tall, c:plants/small, c:plants/water, c:plants/salt_water, c:plants/floating, c:plants/wall

        tag(c.apply("plants/tall"))
            .addOnly(TFCBlocks.PLANTS, Plant::isTallPlant);

        tag(c.apply("plants/small"))
            .addOnly(TFCBlocks.PLANTS, Plant::isSmallPlant);

        tag(c.apply("plants/water"))
            .addOnly(TFCBlocks.PLANTS, Plant::isFreshWaterPlant);

        tag(c.apply("plants/salt_water"))
            .addOnly(TFCBlocks.PLANTS, Plant::isSaltWaterPlant);

        tag(c.apply("plants/hanging"))
            .addOnly(TFCBlocks.PLANTS, Plant::isHangingPlant);

        tag(c.apply("plants/wall"))
            .addOnly(TFCBlocks.PLANTS, Plant::isWallPlant);

        tag(c.apply("plants/floating"))
            .addOnly(TFCBlocks.PLANTS, Plant::isSurfaceWaterPlant);

        tag(c.apply("plants")).addTags(
            c.apply("plants/tall"),
            c.apply("plants/small"),
            c.apply("plants/water"),
            c.apply("plants/salt_water"),
            c.apply("plants/hanging"),
            c.apply("plants/wall"),
            c.apply("plants/floating")
        );

        //Player Workstations
        //c:player_workstations, c:player_workstations/WORK_STATION

        final TagKey<Block> playerWorkstationsTag = c.apply("player_workstations");

        tag(c.apply("player_workstations/anvil"))
            .add(TFCBlocks.METALS, Metal.BlockType.ANVIL)
            .add(TFCBlocks.ROCK_ANVILS);

        tag(c.apply("player_workstations/blast_furnace"))
            .add(TFCBlocks.BLAST_FURNACE);

        tag(c.apply("player_workstations/bloomery"))
            .add(TFCBlocks.BLOOMERY);

        tag(c.apply("player_workstations/charcoal_forge"))
            .add(TFCBlocks.CHARCOAL_FORGE);

        tag(c.apply("player_workstations/composter"))
            .add(TFCBlocks.COMPOSTER);

        tag(c.apply("player_workstations/crucible"))
            .add(TFCBlocks.CRUCIBLE);

        tag(c.apply("player_workstations/firebox"))
            .add(TFCBlocks.FIREBOX);

        tag(c.apply("player_workstations/firepit"))
            .add(TFCBlocks.FIREPIT);

        tag(c.apply("player_workstations/grill"))
            .add(TFCBlocks.GRILL);

        tag(c.apply("player_workstations/lectern"))
            .add(TFCBlocks.WOODS, Wood.BlockType.LECTERN);

        tag(c.apply("player_workstations/loom"))
            .add(TFCBlocks.WOODS, Wood.BlockType.LOOM);

        tag(c.apply("player_workstations/mold_table"))
            .add(TFCBlocks.MOLD_TABLE);

        tag(c.apply("player_workstations/nest_box"))
            .add(TFCBlocks.NEST_BOX);

        tag(c.apply("player_workstations/pit_kiln"))
            .add(TFCBlocks.PIT_KILN);

        tag(c.apply("player_workstations/quern"))
            .add(TFCBlocks.QUERN);

        tag(c.apply("player_workstations/scraping"))
            .add(TFCBlocks.SCRAPING);

        tag(c.apply("player_workstations/scribing_table"))
            .add(TFCBlocks.WOODS, Wood.BlockType.SCRIBING_TABLE);

        tag(c.apply("player_workstations/sewing_table"))
            .add(TFCBlocks.WOODS, Wood.BlockType.SEWING_TABLE);

        tag(c.apply("player_workstations/stove"))
            .add(TFCBlocks.STOVE, TFCBlocks.STOVE_POT);

        tag(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES)
            .add(TFCBlocks.WOODS, Wood.BlockType.WORKBENCH);

        tag(playerWorkstationsTag).addTags(
            c.apply("player_workstations/anvil"),
            c.apply("player_workstations/blast_furnace"),
            c.apply("player_workstations/bloomery"),
            c.apply("player_workstations/charcoal_forge"),
            c.apply("player_workstations/composter"),
            c.apply("player_workstations/crucible"),
            c.apply("player_workstations/firebox"),
            c.apply("player_workstations/firepit"),
            c.apply("player_workstations/grill"),
            c.apply("player_workstations/lectern"),
            c.apply("player_workstations/loom"),
            c.apply("player_workstations/mold_table"),
            c.apply("player_workstations/nest_box"),
            c.apply("player_workstations/pit_kiln"),
            c.apply("player_workstations/quern"),
            c.apply("player_workstations/scraping"),
            c.apply("player_workstations/scribing_table"),
            c.apply("player_workstations/sewing_table"),
            c.apply("player_workstations/stove")
        );

        //Potted Plants

        tag(c.apply("potted_plants"))
            .add(TFCBlocks.POTTED_PLANTS)
            .add(TFCBlocks.FRUIT_TREE_POTTED_SAPLINGS)
            .add(TFCBlocks.WOODS, Wood.BlockType.POTTED_SAPLING);

        //Pressure Plates
        //c:pressure_plates, c:pressure_plates/wooden, c:pressure_plates/stone

        tag(c.apply("pressure_plates/wooden"))
            .add(TFCBlocks.WOODS, Wood.BlockType.PRESSURE_PLATE);

        tag(c.apply("pressure_plates/stone"))
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.PRESSURE_PLATE);

        tag(c.apply("pressure_plates")).addTags(
            c.apply("pressure_plates/wooden"),
            c.apply("pressure_plates/stone")
        );

        //Relocation Not Supported
        //c:relocation_not_supported

        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(
            TFCBlocks.BURNING_LOG_PILE,
            TFCBlocks.CHARCOAL_PILE,
            TFCBlocks.INGOT_PILE,
            TFCBlocks.DOUBLE_INGOT_PILE,
            TFCBlocks.LOG_PILE,
            TFCBlocks.PLACED_ITEM,
            TFCBlocks.PIT_KILN,
            TFCBlocks.HOT_POURED_GLASS,
            TFCBlocks.GLASS_BASIN,
            TFCBlocks.SCRAPING
        );

        //Rods

        tag(c.apply("rods/wooden"))
            .add(TFCBlocks.WOODS, Wood.BlockType.TWIG);
        tag(c.apply("rods"))
            .addTag(c.apply("rods/wooden"));

        //Sands

        for (SandBlockType sand : SandBlockType.values())
        {
            final TagKey<Block> sandTag = c.apply("sands/" + sand.name().toLowerCase(Locale.ROOT));

            tag(sandTag)
                .add(TFCBlocks.SAND.get(sand));

            tag(c.apply("sands"))
                .addTag(sandTag);
        }

        tag(c.apply("sands/volcanic"))
            .add(TFCBlocks.SAND.get(SandBlockType.BLACK));

        tag(c.apply("sands/hematitic")).add(
            TFCBlocks.SAND.get(SandBlockType.RED),
            TFCBlocks.SAND.get(SandBlockType.PINK),
            TFCBlocks.SAND.get(SandBlockType.YELLOW)
        );
        tag(c.apply("sands/olivine")).add(
            TFCBlocks.SAND.get(SandBlockType.BROWN),
            TFCBlocks.SAND.get(SandBlockType.GREEN)
        );
        tag(c.apply("sands/silica")).add(TFCBlocks.SAND.get(SandBlockType.WHITE));

        tag(c.apply("sands")).addTags(
            c.apply("sands/volcanic"),
            c.apply("sands/hematitic"),
            c.apply("sands/olivine"),
            c.apply("sands/silica")
        );

        //Sandstone

        tag(Tags.Blocks.SANDSTONE_BLOCKS)
            .add2(TFCBlocks.SANDSTONE);
        tag(Tags.Blocks.SANDSTONE_SLABS)
            .add2(TFCBlocks.SANDSTONE_DECORATIONS, DecorationBlockHolder::slab);
        tag(Tags.Blocks.SANDSTONE_STAIRS)
            .add2(TFCBlocks.SANDSTONE_DECORATIONS, DecorationBlockHolder::stair);
        tag(c.apply("sandstone/walls"))
            .add2(TFCBlocks.SANDSTONE_DECORATIONS, DecorationBlockHolder::wall);

        tag(c.apply("sandstone"))
            .addTag(c.apply("sandstone/walls"));

        for (SandBlockType sand : SandBlockType.values())
        {
            final String sandName = sand.name().toLowerCase(Locale.ROOT);

            final TagKey<Block> blocksTag = c.apply("sandstone/" + sandName + "_blocks");
            final TagKey<Block> slabsTag = c.apply("sandstone/" + sandName + "_slabs");
            final TagKey<Block> stairsTag = c.apply("sandstone/" + sandName + "_stairs");
            final TagKey<Block> wallsTag = c.apply("sandstone/" + sandName + "_walls");

            tag(blocksTag)
                .add(TFCBlocks.SANDSTONE.get(sand));

            tag(slabsTag)
                .add(TFCBlocks.SANDSTONE_DECORATIONS.get(sand).values().stream().map(DecorationBlockHolder::slab));

            tag(stairsTag)
                .add(TFCBlocks.SANDSTONE_DECORATIONS.get(sand).values().stream().map(DecorationBlockHolder::stair));

            tag(wallsTag)
                .add(TFCBlocks.SANDSTONE_DECORATIONS.get(sand).values().stream().map(DecorationBlockHolder::wall));

            tag(c.apply("sandstone"))
                .addTags(blocksTag, slabsTag, stairsTag, wallsTag);
        }

        //Soils -- Could also be Dirts
        //c:soils, c:soils/VARIANT

        //Soils

        final TagKey<Block> soilsTag = c.apply("soils");

        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            final TagKey<Block> variantSoilsTag = c.apply("soils/" + variant.name().toLowerCase(Locale.ROOT));

            tag(variantSoilsTag).add(
                TFCBlocks.SOIL.get(SoilBlockType.CLAY_DUFF).get(variant),
                TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).get(variant),
                TFCBlocks.SOIL.get(SoilBlockType.COARSE_DIRT).get(variant),
                TFCBlocks.SOIL.get(SoilBlockType.DRYING_BRICKS).get(variant),
                TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant),
                TFCBlocks.SOIL.get(SoilBlockType.DUFF).get(variant),
                TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(variant),
                TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(variant),
                TFCBlocks.SOIL.get(SoilBlockType.GRASS_PATH).get(variant),
                TFCBlocks.SOIL.get(SoilBlockType.MUD).get(variant),
                TFCBlocks.SOIL.get(SoilBlockType.MUDDY_ROOTS).get(variant),
                TFCBlocks.SOIL.get(SoilBlockType.ROOTED_DIRT).get(variant)
            );

            tag(soilsTag)
                .addTag(variantSoilsTag);
        }

        //Stones
        //c:stones, c:stones/ROCK, c:stones/mossy, c:stones/spike, c:stones/raw, c:stones/hardened, c:stones/loose, c:stones/smooth

        for (Rock rock : Rock.values())
        {
            final TagKey<Block> rockStonesTag = c.apply("stones/" + rock.getSerializedName());
            final Map<Rock.BlockType, ? extends IdHolder<? extends Block>> blocks = TFCBlocks.ROCK_BLOCKS.get(rock);
            final Map<Rock.BlockType, DecorationBlockHolder> decorations = TFCBlocks.ROCK_DECORATIONS.get(rock);

            tag(rockStonesTag).add(
                blocks.get(Rock.BlockType.HARDENED),
                blocks.get(Rock.BlockType.LOOSE),
                blocks.get(Rock.BlockType.MOSSY_LOOSE),
                blocks.get(Rock.BlockType.RAW),
                blocks.get(Rock.BlockType.SMOOTH),
                blocks.get(Rock.BlockType.SPIKE),
                decorations.get(Rock.BlockType.RAW).slab(),
                decorations.get(Rock.BlockType.RAW).stair(),
                decorations.get(Rock.BlockType.RAW).wall(),
                decorations.get(Rock.BlockType.SMOOTH).slab(),
                decorations.get(Rock.BlockType.SMOOTH).stair(),
                decorations.get(Rock.BlockType.SMOOTH).wall()
            );

            tag(Tags.Blocks.STONES)
                .addTag(rockStonesTag);
        }

        tag(STONES_SPIKE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.SPIKE);

        tag(STONES_RAW)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.RAW)
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.RAW)
                .values().stream().map(DecorationBlockHolder::slab))
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.RAW)
                .values().stream().map(DecorationBlockHolder::stair))
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.RAW)
                .values().stream().map(DecorationBlockHolder::wall));

        tag(STONES_SMOOTH)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.SMOOTH)
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.SMOOTH)
                .values().stream().map(DecorationBlockHolder::slab))
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.SMOOTH)
                .values().stream().map(DecorationBlockHolder::stair))
            .add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.SMOOTH)
                .values().stream().map(DecorationBlockHolder::wall));

        tag(STONES_HARDENED)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.HARDENED);

        tag(STONES_LOOSE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.LOOSE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_LOOSE);

        tag(c.apply("stones/mossy"))
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_LOOSE);

        tag(Tags.Blocks.STONES).addTags(
            STONES_HARDENED,
            STONES_LOOSE,
            STONES_RAW,
            STONES_SPIKE,
            STONES_SMOOTH,
            c.apply("stones/mossy")
        );

        //Saplings
        //c:saplings, c:saplings/WOOD

        final TagKey<Block> saplingsTag = c.apply("saplings");

        TFCBlocks.WOODS.forEach((wood, blocks) -> {
            final TagKey<Block> woodSaplingsTag = c.apply("saplings/" + wood.getSerializedName());

            tag(woodSaplingsTag)
                .add(blocks.get(Wood.BlockType.SAPLING));

            tag(saplingsTag)
                .addTag(woodSaplingsTag);
        });

        //Shelves
        //c:shelves, c:shelves/wooden, c:shelves/brick

        tag(c.apply("shelves/wooden"))
            .add(TFCBlocks.WOODS, Wood.BlockType.SHELF);
        tag(c.apply("shelves/brick"))

            .add(TFCBlocks.FIRE_BRICK_SHELF);

        tag(c.apply("shelves")).addTags(
            c.apply("shelves/wooden"),
            c.apply("shelves/brick")
        );

        //Signs

        final TagKey<Block> signTag = c.apply("signs");
        final TagKey<Block> woodenSignTag = c.apply("signs/wooden");
        final TagKey<Block> hangingSignTag = c.apply("signs/hanging");

        for (Metal metal : Metal.values()){

            if(metal.allParts())
            {
                final String metalName = metal == Metal.WROUGHT_IRON ? "iron" : metal.getSerializedName();
                final TagKey<Block> metalHangingSignsTag = c.apply("signs/hanging/" + metalName);

                tag(metalHangingSignsTag)
                    .add(TFCBlocks.CEILING_HANGING_SIGNS, metal)
                    .add(TFCBlocks.WALL_HANGING_SIGNS, metal);

                tag(woodenSignTag)
                    .add(TFCBlocks.CEILING_HANGING_SIGNS, metal)
                    .add(TFCBlocks.WALL_HANGING_SIGNS, metal);

                tag(hangingSignTag)
                    .addTag(metalHangingSignsTag);
            }
        }

        tag(woodenSignTag)
            .add(TFCBlocks.WOODS, Wood.BlockType.SIGN)
            .add(TFCBlocks.WOODS, Wood.BlockType.WALL_SIGN);

        tag(signTag).addTags(
            woodenSignTag,
            hangingSignTag
        );

        //Slabs


        //Sluices

        tag(c.apply("sluices/wooden"))
            .add(TFCBlocks.WOODS, Wood.BlockType.SLUICE);

        tag(c.apply("sluices"))
            .addTags(c.apply("sluices/wooden"));

        //Stairs


        //Storage Blocks

        tag(Tags.Blocks.STORAGE_BLOCKS_WHEAT)
            .remove(Blocks.HAY_BLOCK); // We repurpose this as storing straw

        tag(Tags.Blocks.STORAGE_BLOCKS_SLIME)
            .remove(Blocks.SLIME_BLOCK);

        tag(c.apply("storage_blocks/glue"))
            .add(Blocks.SLIME_BLOCK);

        tag(c.apply("storage_blocks/thatch"))
            .add(Blocks.HAY_BLOCK);

        //Stripped Logs
        //c:stripped_logs, c:stripped_logs/WOOD

        TFCBlocks.WOODS.forEach((wood, blocks) -> {
            final TagKey<Block> strippedWoodLogsTag = c.apply("stripped_logs/" + wood.getSerializedName());

            tag(strippedWoodLogsTag)
                .add(blocks.get(Wood.BlockType.STRIPPED_LOG));

            tag(Tags.Blocks.STRIPPED_LOGS)
                .addTag(strippedWoodLogsTag);
        });

        //Stripped Wood
        //c:stripped_woods, c:stripped_woods/WOOD

        TFCBlocks.WOODS.forEach((wood, blocks) -> {
            final TagKey<Block> strippedWoodTag = c.apply("stripped_woods/" + wood.getSerializedName());

            tag(strippedWoodTag)
                .add(blocks.get(Wood.BlockType.STRIPPED_WOOD));

            tag(Tags.Blocks.STRIPPED_WOODS)
                .addTag(strippedWoodTag);
        });

        //Supports

        tag(c.apply("supports/wooden"))
            .add(TFCBlocks.WOODS, Wood.BlockType.HORIZONTAL_SUPPORT).add(TFCBlocks.WOODS, Wood.BlockType.VERTICAL_SUPPORT);

        tag(c.apply("supports"))
            .addTags(c.apply("supports/wooden"));

        //Thatch

        tag(c.apply("thatch"))
            .add(TFCBlocks.THATCH);

        //Tool Racks

        tag(c.apply("tool_racks/wooden"))
            .add(TFCBlocks.WOODS, Wood.BlockType.TOOL_RACK);

        tag(c.apply("tool_racks"))
            .addTags(c.apply("tool_racks/wooden"));

        //Trapdoors

        tag(c.apply("trapdoors/wooden"))
            .add(TFCBlocks.WOODS, Wood.BlockType.TRAPDOOR);

        tag(c.apply("trapdoors/metal"))
            .add(TFCBlocks.METALS, Metal.BlockType.TRAPDOOR);

        tag(c.apply("trapdoors")).addTags(
            c.apply("trapdoors/wooden"),
            c.apply("trapdoors/metal")
        );

        //Walls

        //Wattle

        tag(c.apply("wattle"))
            .add(TFCBlocks.STAINED_WATTLE)
            .add(TFCBlocks.WATTLE, TFCBlocks.UNSTAINED_WATTLE);

        //Woods
        //c:woods, c:woods/WOOD

        final TagKey<Block> woodsTag = c.apply("woods");

        TFCBlocks.WOODS.forEach((wood, blocks) -> {
            final TagKey<Block> woodTag = c.apply("woods/" + wood.getSerializedName());

            tag(woodTag)
                .add(blocks.get(Wood.BlockType.WOOD));

            tag(woodsTag)
                .addTag(woodTag);
        });

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
            .add(TFCBlocks.SOIL.get(SoilBlockType.MUD))
            .add(TFCBlocks.SOIL.get(SoilBlockType.COARSE_DIRT))
            .add(TFCBlocks.SOIL.get(SoilBlockType.DUFF))
            .add(TFCBlocks.SOIL.get(SoilBlockType.CLAY_DUFF))
            .add(
                TFCBlocks.WHITE_KAOLIN_CLAY,
                TFCBlocks.PINK_KAOLIN_CLAY,
                TFCBlocks.RED_KAOLIN_CLAY
            );
        tag(SUPPORTS_LANDSLIDE).addTags(FARMLANDS, PATHS);
        tag(NOT_SOLID_SUPPORTING).addTags(STONES_SMOOTH);
        tag(TOUGHNESS_1).add(TFCBlocks.CHARCOAL_PILE, TFCBlocks.CHARCOAL_FORGE);
        tag(TOUGHNESS_2).addTag(Tags.Blocks.STONES).addTag(Tags.Blocks.COBBLESTONES);
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

        tag(STONES_PRESSURE_PLATES)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.PRESSURE_PLATE)
            .addOptionalTag(ResourceLocation.withDefaultNamespace("stone_pressure_plates"));

        tag(SMOKES_IN_RAIN).add(TFCBlocks.MAGMA_BLOCKS).add(Blocks.MAGMA_BLOCK);
        tag(INSULATION)
            .addTags(Tags.Blocks.STONES, STONES_SMOOTH, BlockTags.STONE_BRICKS, Tags.Blocks.COBBLESTONES, Tags.Blocks.SANDSTONE_BLOCKS)
            .add(Blocks.BRICKS)
            .add(TFCBlocks.FIRE_BRICKS)
            .add(TFCBlocks.FIRE_BRICKS);

        tag(LAMPS).add(TFCBlocks.METALS, Metal.BlockType.LAMP);
        tag(ANVILS).add(TFCBlocks.METALS, Metal.BlockType.ANVIL).add(TFCBlocks.ROCK_ANVILS);

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
        tag(BLAST_FURNACE_INSULATION).add(TFCBlocks.REINFORCED_FIRE_BRICKS).add(TFCBlocks.REINFORCED_FIRE_BRICKS);
        tag(HEAT_INSULATION).add(TFCBlocks.FIRE_BRICKS).add(TFCBlocks.REINFORCED_FIRE_BRICKS).add(Blocks.BRICKS).add(Blocks.BRICK_STAIRS).add(Blocks.BRICK_SLAB).add(Blocks.TINTED_GLASS).add(TFCBlocks.FIREPROOF_DOOR);
        tag(HEAT_PASSABLE)
            .add(TFCBlocks.METALS, Metal.BlockType.GRATE)
            .add(TFCBlocks.METALS, Metal.BlockType.EXPOSED_GRATE)
            .add(TFCBlocks.METALS, Metal.BlockType.OXIDIZED_GRATE)
            .add(TFCBlocks.METALS, Metal.BlockType.WEATHERED_GRATE);
        tag(SCRAPING_SURFACE).addTag(BlockTags.LOGS);
        tag(GLASS_POURING_TABLE)
            .add(TFCBlocks.METALS.get(Metal.BRASS).get(Metal.BlockType.BLOCK))
            .add(TFCBlocks.METALS.get(Metal.BRASS).get(Metal.BlockType.EXPOSED_BLOCK))
            .add(TFCBlocks.METALS.get(Metal.BRASS).get(Metal.BlockType.WEATHERED_BLOCK))
            .add(TFCBlocks.METALS.get(Metal.BRASS).get(Metal.BlockType.OXIDIZED_BLOCK));
        tag(GLASS_BASIN_BLOCKS)
            .add(TFCBlocks.METALS.get(Metal.BRASS).get(Metal.BlockType.BLOCK))
            .add(TFCBlocks.METALS.get(Metal.BRASS).get(Metal.BlockType.EXPOSED_BLOCK))
            .add(TFCBlocks.METALS.get(Metal.BRASS).get(Metal.BlockType.WEATHERED_BLOCK))
            .add(TFCBlocks.METALS.get(Metal.BRASS).get(Metal.BlockType.OXIDIZED_BLOCK));
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
        tag(BlockTags.BAMBOO_BLOCKS).add(TFCBlocks.GOLDEN_BAMBOO_BLOCK);
        tag(BlockTags.BAMBOO_PLANTABLE_ON).add(TFCBlocks.PLANTS.get(Plant.GOLDEN_BAMBOO_SAPLING), TFCBlocks.PLANTS.get(Plant.GOLDEN_BAMBOO)).remove(Tags.Blocks.SANDS);
        tag(LIVING_SPREADING_BUSHES)
            .add(TFCBlocks.SPREADING_BUSHES)
            .add(TFCBlocks.SPREADING_CANES);
        tag(SPREADING_BUSHES)
            .addTags(LIVING_SPREADING_BUSHES)
            .add(TFCBlocks.DEAD_BERRY_BUSH, TFCBlocks.DEAD_CANE, TFCBlocks.BANANA_PLANT);
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
        tag(BlockTags.SMALL_FLOWERS).add(
            TFCBlocks.PLANTS.get(Plant.GOLDENROD),
            TFCBlocks.PLANTS.get(Plant.ROSE),
            TFCBlocks.PLANTS.get(Plant.ALLIUM),
            TFCBlocks.PLANTS.get(Plant.ANTHURIUM),
            TFCBlocks.PLANTS.get(Plant.BLOOD_LILY),
            TFCBlocks.PLANTS.get(Plant.BLUE_GINGER),
            TFCBlocks.PLANTS.get(Plant.BLUE_ORCHID),
            TFCBlocks.PLANTS.get(Plant.BUTTERCUP),
            TFCBlocks.PLANTS.get(Plant.BUTTERFLY_MILKWEED),
            TFCBlocks.PLANTS.get(Plant.BLACK_ORCHID),
            TFCBlocks.PLANTS.get(Plant.CALENDULA),
            TFCBlocks.PLANTS.get(Plant.CORNFLOWER),
            TFCBlocks.PLANTS.get(Plant.DANDELION),
            TFCBlocks.PLANTS.get(Plant.DESERT_FLAME),
            TFCBlocks.PLANTS.get(Plant.EDELWEISS),
            TFCBlocks.PLANTS.get(Plant.FIELD_HORSETAIL),
            TFCBlocks.PLANTS.get(Plant.GRAPE_HYACINTH),
            TFCBlocks.PLANTS.get(Plant.HELICONIA),
            TFCBlocks.PLANTS.get(Plant.HEATHER),
            TFCBlocks.PLANTS.get(Plant.HOUSTONIA),
            TFCBlocks.PLANTS.get(Plant.NASTURTIUM),
            TFCBlocks.PLANTS.get(Plant.OXEYE_DAISY),
            TFCBlocks.PLANTS.get(Plant.POPPY),
            TFCBlocks.PLANTS.get(Plant.PULSATILLA),
            TFCBlocks.PLANTS.get(Plant.SILVER_SPURFLOWER),
            TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_PINK),
            TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_RED),
            TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_WHITE),
            TFCBlocks.PLANTS.get(Plant.SNAPDRAGON_YELLOW),
            TFCBlocks.PLANTS.get(Plant.TRILLIUM),
            TFCBlocks.PLANTS.get(Plant.TROPICAL_MILKWEED),
            TFCBlocks.PLANTS.get(Plant.TULIP_ORANGE),
            TFCBlocks.PLANTS.get(Plant.TULIP_RED),
            TFCBlocks.PLANTS.get(Plant.TULIP_PINK),
            TFCBlocks.PLANTS.get(Plant.TULIP_WHITE),
            TFCBlocks.PLANTS.get(Plant.YELLOW_SAXIFRAGE)
        );
        tag(BlockTags.TALL_FLOWERS).add(
            TFCBlocks.PLANTS.get(Plant.ROSE),
            TFCBlocks.PLANTS.get(Plant.SUNFLOWER),
            TFCBlocks.PLANTS.get(Plant.FOXGLOVE),
            TFCBlocks.PLANTS.get(Plant.LILAC),
            TFCBlocks.PLANTS.get(Plant.SAPPHIRE_TOWER)
        );
        tag(BlockTags.FLOWERS).add(
            TFCBlocks.PLANTS.get(Plant.WHITE_WATER_LILY),
            TFCBlocks.PLANTS.get(Plant.YELLOW_WATER_LILY),
            TFCBlocks.PLANTS.get(Plant.PURPLE_WATER_LILY),
            TFCBlocks.PLANTS.get(Plant.SARGASSUM),
            TFCBlocks.PLANTS.get(Plant.LOTUS),
            TFCBlocks.PLANTS.get(Plant.PISTIA),
            TFCBlocks.PLANTS.get(Plant.CANNA),
            TFCBlocks.PLANTS.get(Plant.WATER_CANNA),
            TFCBlocks.PLANTS.get(Plant.GOLDENROD),
            TFCBlocks.PLANTS.get(Plant.PEROVSKIA),
            TFCBlocks.PLANTS.get(Plant.AZALEA),
            TFCBlocks.PLANTS.get(Plant.BEAR_GRASS),
            TFCBlocks.PLANTS.get(Plant.BUR_REED),
            TFCBlocks.PLANTS.get(Plant.GUZMANIA),
            TFCBlocks.PLANTS.get(Plant.HIBISCUS),
            TFCBlocks.PLANTS.get(Plant.KANGAROO_PAW),
            TFCBlocks.PLANTS.get(Plant.KINNIKINNICK),
            TFCBlocks.PLANTS.get(Plant.LABRADOR_TEA),
            TFCBlocks.PLANTS.get(Plant.LILY_OF_THE_VALLEY),
            TFCBlocks.PLANTS.get(Plant.MAIDEN_PINK),
            TFCBlocks.PLANTS.get(Plant.MARIGOLD),
            TFCBlocks.PLANTS.get(Plant.MEADS_MILKWEED),
            TFCBlocks.PLANTS.get(Plant.MORNING_GLORY),
            TFCBlocks.PLANTS.get(Plant.MOUNTAIN_HULLWORT),
            TFCBlocks.PLANTS.get(Plant.MOSS_CAMPION),
            TFCBlocks.PLANTS.get(Plant.PALASH),
            TFCBlocks.PLANTS.get(Plant.PENWORTEL),
            TFCBlocks.PLANTS.get(Plant.PHRAGMITE),
            TFCBlocks.PLANTS.get(Plant.PICKERELWEED),
            TFCBlocks.PLANTS.get(Plant.PRIMROSE),
            TFCBlocks.PLANTS.get(Plant.QANTU),
            TFCBlocks.PLANTS.get(Plant.RAMIREZELLA),
            TFCBlocks.PLANTS.get(Plant.RAMUNDA),
            TFCBlocks.PLANTS.get(Plant.SACRED_DATURA),
            TFCBlocks.PLANTS.get(Plant.SEA_LAVENDER),
            TFCBlocks.PLANTS.get(Plant.STRELITZIA),
            TFCBlocks.PLANTS.get(Plant.TANK_BROMELIAD),
            TFCBlocks.PLANTS.get(Plant.VRIESEA),
            TFCBlocks.PLANTS.get(Plant.YUCCA)
        );
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
        tag(MINEABLE_WITH_HOE).addTag(BlockTags.MINEABLE_WITH_HOE);
        tag(MINEABLE_WITH_GLASS_SAW)
            .addTags(Tags.Blocks.GLASS_BLOCKS, Tags.Blocks.GLASS_PANES)
            .add(TFCBlocks.COLORED_POURED_GLASS)
            .add(TFCBlocks.POURED_GLASS);

        tag(PROSPECTABLE).addTags(Tags.Blocks.ORES);

        tag(BlockTags.VALID_SPAWN).addTags(GRASS, DUFF);
        tag(DUFF)
            .add(TFCBlocks.SOIL.get(SoilBlockType.DUFF))
            .add(TFCBlocks.SOIL.get(SoilBlockType.CLAY_DUFF));
        tag(COARSE_DIRT)
            .add(TFCBlocks.SOIL.get(SoilBlockType.COARSE_DIRT));
        tag(DIRT)
            .add(Blocks.DIRT)
            .add(TFCBlocks.SOIL.get(SoilBlockType.DIRT))
            .add(TFCBlocks.SOIL.get(SoilBlockType.ROOTED_DIRT))
            .add(TFCBlocks.SOIL.get(SoilBlockType.DUFF));
        tag(GRASS)
            .add(Blocks.GRASS_BLOCK)
            .add(TFCBlocks.SOIL.get(SoilBlockType.GRASS));
        tag(FARMLANDS)
            .add(Blocks.FARMLAND)
            .add(TFCBlocks.SOIL.get(SoilBlockType.FARMLAND));
        tag(VERY_RICH_FARMLAND)
            .add(TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(SoilBlockType.Variant.MOLLISOL));
        tag(RICH_FARMLAND)
            .add(TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(SoilBlockType.Variant.ANDISOL))
            .add(TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(SoilBlockType.Variant.FLUVISOL))
            .add(TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(SoilBlockType.Variant.ALFISOL));
        tag(NORMAL_FARMLAND)
            .add(TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(SoilBlockType.Variant.MOLLISOL));
        tag(POOR_FARMLAND)
            .add(TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(SoilBlockType.Variant.PODZOL))
            .add(TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(SoilBlockType.Variant.ARIDISOL));
        tag(VERY_POOR_FARMLAND)
            .add(TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(SoilBlockType.Variant.OXISOL));
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
            .add(TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS))
            .add(TFCBlocks.SOIL.get(SoilBlockType.CLAY_DUFF));
        tag(KAOLIN_CLAYS).add(
            TFCBlocks.KAOLIN_CLAY_GRASS,
            TFCBlocks.WHITE_KAOLIN_CLAY,
            TFCBlocks.PINK_KAOLIN_CLAY,
            TFCBlocks.RED_KAOLIN_CLAY);
        tag(INCREASES_SOIL_HYDRATION).addTags(
            CLAYS
        ).add(Blocks.CLAY);
        tag(DECREASES_SOIL_HYDRATION).addTags(
            Tags.Blocks.GRAVELS,
            Tags.Blocks.SANDS
        );

        tag(TREE_GROWS_ON).addTag(BlockTags.DIRT);
        tag(WILD_CROP_GROWS_ON).addTag(BlockTags.DIRT);
        tag(SPREADING_FRUIT_GROWS_ON).addTag(BUSH_PLANTABLE_ON);
        tag(BUSH_PLANTABLE_ON).addTags(BlockTags.DIRT, FARMLANDS, CLAYS);
        tag(DRY_PLANT_PLANTABLE_ON).addTags(BlockTags.SAND, Tags.Blocks.SANDS, Tags.Blocks.GRAVELS, COARSE_DIRT, BUSH_PLANTABLE_ON).add(TFCBlocks.SANDSTONE, SandstoneBlockType.RAW);
        tag(EPIPHYTE_PLANTABLE_ON).addTags(BlockTags.LOGS, STONES_RAW, STONES_HARDENED).add(TFCBlocks.SANDSTONE, SandstoneBlockType.RAW);
        tag(GRASS_PLANTABLE_ON)
            .addTags(BlockTags.DIRT, FARMLANDS, CLAYS)
            .add(TFCBlocks.PEAT, TFCBlocks.PEAT_GRASS);
        tag(SEA_BUSH_PLANTABLE_ON).addTags(BlockTags.DIRT, Tags.Blocks.GRAVELS, Tags.Blocks.SANDS);
        tag(HALOPHYTE_PLANTABLE_ON).addTag(BlockTags.DIRT);
        tag(CREEPING_STONE_PLANTABLE_ON).addTags(Tags.Blocks.STONES, STONES_SMOOTH, Tags.Blocks.COBBLESTONES, Tags.Blocks.SANDSTONE_BLOCKS);
        tag(CREEPING_PLANT_NOT_PLANTABLE_ON).add(Blocks.PACKED_ICE, Blocks.SNOW_BLOCK, Blocks.BLUE_ICE, TFCBlocks.SEA_ICE.get(), Blocks.POWDER_SNOW);
        tag(ANEMONE_PLANTABLE_ON).addTags(Tags.Blocks.STONES, STONES_SMOOTH, Tags.Blocks.COBBLESTONES, Tags.Blocks.GRAVELS, Tags.Blocks.SANDS, Tags.Blocks.SANDSTONE_BLOCKS);

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
        tag(CAMEL_FASTER_ON)
            .addTags(
                COARSE_DIRT,
                Tags.Blocks.GRAVELS,
                Tags.Blocks.SANDS
            )
            .add(TFCBlocks.SANDSTONE, SandstoneBlockType.RAW);
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

        tag(tfcTagOf(Registries.BLOCK, "fresh_underwater_plants")).add(
            TFCBlocks.PLANTS.get(Plant.COONTAIL),
            TFCBlocks.PLANTS.get(Plant.EEL_GRASS),
            TFCBlocks.PLANTS.get(Plant.MILFOIL),
            TFCBlocks.PLANTS.get(Plant.SAGO)
        );

        tag(tfcTagOf(Registries.BLOCK, "salty_underwater_plants")).add(
            TFCBlocks.PLANTS.get(Plant.BADDERLOCKS),
            TFCBlocks.PLANTS.get(Plant.TURTLE_GRASS),
            TFCBlocks.PLANTS.get(Plant.STAR_GRASS),
            TFCBlocks.PLANTS.get(Plant.MANATEE_GRASS),
            TFCBlocks.PLANTS.get(Plant.GUTWEED),
            TFCBlocks.PLANTS.get(Plant.LAMINARIA),
            TFCBlocks.PLANTS.get(Plant.WINGED_KELP),
            TFCBlocks.PLANTS.get(Plant.WINGED_KELP_PLANT),
            TFCBlocks.PLANTS.get(Plant.LEAFY_KELP),
            TFCBlocks.PLANTS.get(Plant.LEAFY_KELP_PLANT),
            TFCBlocks.PLANTS.get(Plant.GIANT_KELP_FLOWER),
            TFCBlocks.PLANTS.get(Plant.GIANT_KELP_PLANT)
        );

        tag(tfcTagOf(Registries.BLOCK, "fresh_emergent_plants")).add(
            TFCBlocks.PLANTS.get(Plant.ARROWHEAD),
            TFCBlocks.PLANTS.get(Plant.BUR_REED),
            TFCBlocks.PLANTS.get(Plant.CATTAIL),
            TFCBlocks.PLANTS.get(Plant.MARIGOLD),
            TFCBlocks.PLANTS.get(Plant.PHRAGMITE),
            TFCBlocks.PLANTS.get(Plant.PICKERELWEED),
            TFCBlocks.PLANTS.get(Plant.WATER_TARO)
        );

        tag(tfcTagOf(Registries.BLOCK, "salty_emergent_plants")).add(
            TFCBlocks.PLANTS.get(Plant.CORDGRASS),
            TFCBlocks.PLANTS.get(Plant.SEA_LAVENDER)
        );

        tag(tfcTagOf(Registries.BLOCK, "fresh_floating_plants")).add(
            TFCBlocks.PLANTS.get(Plant.PURPLE_WATER_LILY),
            TFCBlocks.PLANTS.get(Plant.YELLOW_WATER_LILY),
            TFCBlocks.PLANTS.get(Plant.WHITE_WATER_LILY),
            TFCBlocks.PLANTS.get(Plant.LOTUS),
            TFCBlocks.PLANTS.get(Plant.GREEN_ALGAE),
            TFCBlocks.PLANTS.get(Plant.DUCKWEED),
            TFCBlocks.PLANTS.get(Plant.PISTIA),
            TFCBlocks.PLANTS.get(Plant.WATER_CANNA)
        );

        tag(tfcTagOf(Registries.BLOCK, "salty_floating_plants")).add(
            TFCBlocks.PLANTS.get(Plant.SARGASSUM),
            TFCBlocks.PLANTS.get(Plant.RED_ALGAE)
        );

        tag(tfcTagOf(Registries.BLOCK, "tall_grasses")).add(
            TFCBlocks.PLANTS.get(Plant.BEACHGRASS),
            TFCBlocks.PLANTS.get(Plant.BLUEGRASS),
            TFCBlocks.PLANTS.get(Plant.BROMEGRASS),
            TFCBlocks.PLANTS.get(Plant.PAMPAS_GRASS),
            TFCBlocks.PLANTS.get(Plant.FOUNTAIN_GRASS),
            TFCBlocks.PLANTS.get(Plant.ORCHARD_GRASS),
            TFCBlocks.PLANTS.get(Plant.RYEGRASS),
            TFCBlocks.PLANTS.get(Plant.SCUTCH_GRASS),
            TFCBlocks.PLANTS.get(Plant.TIMOTHY_GRASS),
            TFCBlocks.PLANTS.get(Plant.RADDIA_GRASS),
            TFCBlocks.PLANTS.get(Plant.RED_OAT_GRASS),
            TFCBlocks.PLANTS.get(Plant.DRY_GRASS),
            TFCBlocks.PLANTS.get(Plant.TALL_FESCUE_GRASS),
            TFCBlocks.PLANTS.get(Plant.SWITCHGRASS)
        );

        tag(tfcTagOf(Registries.BLOCK, "cacti")).add(
            TFCBlocks.PLANTS.get(Plant.BARREL_CACTUS),
            TFCBlocks.PLANTS.get(Plant.SAGUARO_FRUIT),
            TFCBlocks.PLANTS.get(Plant.SAGUARO_PLANT),
            TFCBlocks.PLANTS.get(Plant.SAGUARO),
            TFCBlocks.PLANTS.get(Plant.SILKEN_PINCUSHION_CACTUS),
            TFCBlocks.PLANTS.get(Plant.PRICKLY_PEAR),
            TFCBlocks.PLANTS.get(Plant.PRICKLY_PEAR_PURPLE)
        );

        tag(tfcTagOf(Registries.BLOCK, "ferns")).add(
            TFCBlocks.PLANTS.get(Plant.ATHYRIUM_FERN),
            TFCBlocks.PLANTS.get(Plant.BIRD_NEST_FERN),
            TFCBlocks.PLANTS.get(Plant.KING_FERN),
            TFCBlocks.PLANTS.get(Plant.LADY_FERN),
            TFCBlocks.PLANTS.get(Plant.LICORICE_FERN),
            TFCBlocks.PLANTS.get(Plant.OSTRICH_FERN),
            TFCBlocks.PLANTS.get(Plant.SWORD_FERN)
        );

        tag(tfcTagOf(Registries.BLOCK, "shrubs")).add(
            TFCBlocks.PLANTS.get(Plant.AZALEA),
            TFCBlocks.PLANTS.get(Plant.HIBISCUS),
            TFCBlocks.PLANTS.get(Plant.KINNIKINNICK),
            TFCBlocks.PLANTS.get(Plant.MOUNTAIN_HULLWORT),
            TFCBlocks.PLANTS.get(Plant.PALASH),
            TFCBlocks.PLANTS.get(Plant.PENWORTEL),
            TFCBlocks.PLANTS.get(Plant.QANTU),
            TFCBlocks.PLANTS.get(Plant.SHAWIASH)
        );

        tag(tfcTagOf(Registries.BLOCK, "epiphytes")).add(
            TFCBlocks.PLANTS.get(Plant.GUZMANIA),
            TFCBlocks.PLANTS.get(Plant.LICORICE_FERN),
            TFCBlocks.PLANTS.get(Plant.ARTISTS_CONK),
            TFCBlocks.PLANTS.get(Plant.RAMIREZELLA),
            TFCBlocks.PLANTS.get(Plant.SILVER_BROMELIAD),
            TFCBlocks.PLANTS.get(Plant.TANK_BROMELIAD),
            TFCBlocks.PLANTS.get(Plant.VRIESEA)
        );

        tag(tfcTagOf(Registries.BLOCK, "mosses")).add(
            TFCBlocks.PLANTS.get(Plant.COBBLESTONE_LICHEN),
            TFCBlocks.PLANTS.get(Plant.MOSS),
            TFCBlocks.PLANTS.get(Plant.REINDEER_LICHEN),
            TFCBlocks.PLANTS.get(Plant.ELEGANT_SUNBURST_LICHEN)
        );

        tag(tfcTagOf(Registries.BLOCK, "dead_plants")).add(
            TFCBlocks.PLANTS.get(Plant.DRY_GRASS),
            TFCBlocks.PLANTS.get(Plant.DEAD_BUSH)
        );

        tag(tfcTagOf(Registries.BLOCK, "shore_plants")).add(
            TFCBlocks.PLANTS.get(Plant.SEA_PALM),
            TFCBlocks.PLANTS.get(Plant.BEACHGRASS)
        );

        tag(tfcTagOf(Registries.BLOCK, "dry_plants")).add(
            TFCBlocks.PLANTS.get(Plant.DEAD_BUSH),
            TFCBlocks.PLANTS.get(Plant.DRY_GRASS),
            TFCBlocks.PLANTS.get(Plant.SAGEBRUSH),
            TFCBlocks.PLANTS.get(Plant.YUCCA)
        );

        tag(BEAR_CRAWLS_ON).add(TFCBlocks.SEA_ICE).add(Blocks.ICE).add(Blocks.POWDER_SNOW);

        // Vanilla "corals" includes coral fans, + "coral_plants" (which includes corals), we mirror the same
        tag(SALT_WATER_CORAL_PLANTS).add(TFCBlocks.CORAL, Coral.BlockType.CORAL);
        tag(SALT_WATER_CORALS)
            .addTags(SALT_WATER_CORAL_PLANTS)
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL_FAN);
        tag(SALT_WATER_WALL_CORALS).add(TFCBlocks.CORAL, Coral.BlockType.CORAL_WALL_FAN);
        tag(HALOPHYTE).add(
            TFCBlocks.PLANTS.get(Plant.SEA_LAVENDER),
            TFCBlocks.PLANTS.get(Plant.CORDGRASS));
        tag(BlockTags.GEODE_INVALID_BLOCKS).add(TFCFluids.SALT_WATER.createSourceBlock().getBlock()).addTag(BlockTags.DIRT);
        tag(SINGLE_BLOCK_REPLACEABLE)
            .addTag(BlockTags.SMALL_FLOWERS)
            .add(
                TFCBlocks.PLANTS.get(Plant.BEACHGRASS),
                TFCBlocks.PLANTS.get(Plant.BLUEGRASS),
                TFCBlocks.PLANTS.get(Plant.BROMEGRASS),
                TFCBlocks.PLANTS.get(Plant.FOUNTAIN_GRASS),
                TFCBlocks.PLANTS.get(Plant.MANATEE_GRASS),
                TFCBlocks.PLANTS.get(Plant.ORCHARD_GRASS),
                TFCBlocks.PLANTS.get(Plant.RYEGRASS),
                TFCBlocks.PLANTS.get(Plant.SCUTCH_GRASS),
                TFCBlocks.PLANTS.get(Plant.STAR_GRASS),
                TFCBlocks.PLANTS.get(Plant.TIMOTHY_GRASS),
                TFCBlocks.PLANTS.get(Plant.RADDIA_GRASS),
                TFCBlocks.PLANTS.get(Plant.RED_OAT_GRASS),
                TFCBlocks.PLANTS.get(Plant.TURTLE_GRASS),
                TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.HUMUS)
            )
            .addTag(FALLEN_LEAVES);
        tag(POWDER_SNOW_REPLACEABLE).add(
            Blocks.SNOW_BLOCK,
            Blocks.PACKED_ICE,
            Blocks.BLUE_ICE);
        tag(COLD_OCEAN_BLOCKS)
            .addTag(POWDER_SNOW_REPLACEABLE)
            .add(Blocks.POWDER_SNOW)
            .add(TFCBlocks.SEA_ICE);
        tag(TIDE_POOL_BLOCKS).add(
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.CLAM),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.MOLLUSK),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.MUSSEL),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.SEA_URCHIN));
        tag(CANCELS_FLOOD_FILL_LAKE)
            .addTag(BAMBOO);
        tag(KAOLIN_CLAY_REPLACEABLE).addTags(DIRT, Tags.Blocks.STONES, Tags.Blocks.GRAVELS);
        tag(KAOLIN_CLAY_REPLACEABLE)
            .addTags(DIRT, Tags.Blocks.GRAVELS)
            .add(Blocks.SNOW_BLOCK);
        tag(THERMOMETER_READABLE)
            .add(TFCBlocks.STOVE_POT)
            .add(TFCBlocks.STOVE)
            .add(TFCBlocks.FIREBOX)
            .add(TFCBlocks.BLAST_FURNACE)
            .add(TFCBlocks.CRUCIBLE);
        tag(CLOCK_READABLE)
            .add(TFCBlocks.WOODS, Wood.BlockType.BARREL)
            .add(TFCBlocks.BLOOMERY)
            .add(TFCBlocks.FIREBOX);
        tag(SEA_STACK_ROCKS).add(
            TFCBlocks.ROCK_BLOCKS.get(Rock.BASALT).get(Rock.BlockType.HARDENED),
            TFCBlocks.ROCK_BLOCKS.get(Rock.LIMESTONE).get(Rock.BlockType.HARDENED),
            TFCBlocks.ROCK_BLOCKS.get(Rock.MARBLE).get(Rock.BlockType.HARDENED),
            TFCBlocks.ROCK_BLOCKS.get(Rock.RHYOLITE).get(Rock.BlockType.HARDENED)
        );
    }

    @Override
    protected BlockTagAppender tag(TagKey<Block> tag)
    {
        return new BlockTagAppender(getOrCreateRawBuilder(tag));
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
        BlockTagAppender(TagBuilder builder)
        {
            super(builder);
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

        /**
         * Adds every TFC-added block matching the given predicate
         */
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
            blocks.forEach((k, v) -> {if (key.test(k)) add(v);});
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