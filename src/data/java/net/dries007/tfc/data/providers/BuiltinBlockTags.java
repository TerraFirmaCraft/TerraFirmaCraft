/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.Arrays;
import java.util.Locale;
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
import net.minecraft.world.item.DyeColor;
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
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.fluids.TFCFluids;
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
            .add(TFCBlocks.ORES, Ore.LAPIS_LAZULI)
            .add(TFCBlocks.ORES, Ore.OPAL);
        // Needs Stone Tool is ~ Copper, which is every TFC pickaxe, so we don't bother here
        // "Incorrect For Tool" includes the "Needs For Tool", so we don't touch, since we don't add levels
        tag(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON).add(TFCBlocks.SEA_ICE, TFCBlocks.ICE_PILE).add(TFCBlocks.MAGMA_BLOCKS);
        tag(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON).add(TFCBlocks.SOIL.get(SoilBlockType.MUD));
        tag(BlockTags.REPLACEABLE).addEveryTFC(e -> e.defaultBlockState().canBeReplaced());

        // ===== Common Tags ===== //

        //Anvils

        for (Metal metal : Metal.values())
        {
            if (metal.allParts())
            {
                tag(commonTagOf(Registries.BLOCK, "anvils/" + metal.getSerializedName())).add(TFCBlocks.METALS.get(metal).get(Metal.BlockType.ANVIL));
                tag(commonTagOf(Registries.BLOCK, "anvils")).addTag(commonTagOf(Registries.BLOCK, "anvils/" + metal.getSerializedName()));
            }
        }

        tag(commonTagOf(Registries.BLOCK, "anvils/stone")).add(TFCBlocks.ROCK_ANVILS);
        tag(commonTagOf(Registries.BLOCK, "anvils")).addTag(commonTagOf(Registries.BLOCK, "anvils/stone"));

        //Barrels

        tag(Tags.Blocks.BARRELS_WOODEN).add(TFCBlocks.WOODS, Wood.BlockType.BARREL);

        //Bars

        for (Metal metal : Metal.values())
        {
            if (metal.allParts())
            {
                tag(commonTagOf(Registries.BLOCK, "bars/" + metal.getSerializedName())).add(TFCBlocks.METALS.get(metal).get(Metal.BlockType.BARS));
                tag(commonTagOf(Registries.BLOCK, "bars")).addTag(commonTagOf(Registries.BLOCK, "bars/" + metal.getSerializedName()));
            }
        }

        //Bookshelves

        tag(commonTagOf(Registries.BLOCK, "bookshelves/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.BOOKSHELF);
        tag(commonTagOf(Registries.BLOCK, "bookshelves")).addTags(commonTagOf(Registries.BLOCK, "bookshelves/wooden"));

        //Bricks

        for (DyeColor color : DyeColor.values())
        {
            tag(commonTagOf(Registries.BLOCK, "bricks/plaster"))
                .add(TFCBlocks.ALABASTER_BRICKS.get(color).key(),
                    TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color).slab().key(), TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color).stair().key(), TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color).wall().key()
                );

        }

        for (SoilBlockType.Variant soil : SoilBlockType.Variant.values())
        {
            tag(commonTagOf(Registries.BLOCK, "bricks/mud")).add(
                TFCBlocks.SOIL.get(SoilBlockType.MUD_BRICKS).get(soil).key(),
                TFCBlocks.MUD_BRICK_DECORATIONS.get(soil).slab().key(), TFCBlocks.MUD_BRICK_DECORATIONS.get(soil).stair().key(), TFCBlocks.MUD_BRICK_DECORATIONS.get(soil).wall().key()
            );
        }

        TFCBlocks.ROCK_BLOCKS.forEach((rock, blockMap) -> {
            tag(commonTagOf(Registries.BLOCK, "bricks/" + rock.getSerializedName())).add(
                TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.BRICKS),
                TFCBlocks.ROCK_DECORATIONS.get(rock).get(Rock.BlockType.BRICKS).slab(),
                TFCBlocks.ROCK_DECORATIONS.get(rock).get(Rock.BlockType.BRICKS).stair(),
                TFCBlocks.ROCK_DECORATIONS.get(rock).get(Rock.BlockType.BRICKS).wall()
            );
            tag(commonTagOf(Registries.BLOCK, "bricks")).addTag(commonTagOf(Registries.BLOCK, "bricks/" + rock.getSerializedName()));
        });

        tag(commonTagOf(Registries.BLOCK, "bricks/mud")).add(TFCBlocks.SMOOTH_MUD_BRICKS);

        tag(commonTagOf(Registries.BLOCK, "bricks/plaster")).add(TFCBlocks.PLAIN_ALABASTER_BRICKS);
        tag(commonTagOf(Registries.BLOCK, "bricks/fire")).add(
            TFCBlocks.FIRE_BRICKS, TFCBlocks.REINFORCED_FIRE_BRICKS,
            TFCBlocks.FIRE_BRICK_SHELF
        );

        tag(commonTagOf(Registries.BLOCK, "bricks")).addTags(
            commonTagOf(Registries.BLOCK, "bricks/plaster"),
            commonTagOf(Registries.BLOCK, "bricks/fire"),
            commonTagOf(Registries.BLOCK, "bricks/mud")
        );

        //Buttons

        tag(commonTagOf(Registries.BLOCK, "buttons/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.BUTTON);
        tag(commonTagOf(Registries.BLOCK, "buttons/stone")).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.BUTTON);
        tag(commonTagOf(Registries.BLOCK, "buttons")).addTags(
            commonTagOf(Registries.BLOCK, "buttons/wooden"),
            commonTagOf(Registries.BLOCK, "buttons/stone")
        );

        //Candles

        for (DyeColor color : DyeColor.values())
        {
            tag(commonTagOf(Registries.BLOCK, "candles/" + color.getSerializedName())).add(
                TFCBlocks.DYED_CANDLE_CAKES.get(color).key(), TFCBlocks.DYED_CANDLE.get(color).key()
            );
            tag(commonTagOf(Registries.BLOCK, "candles")).addTag(commonTagOf(Registries.BLOCK, "candles/" + color.getSerializedName()));
            tag(commonTagOf(Registries.BLOCK, "dyed/" + color.getSerializedName())).add(
                TFCBlocks.DYED_CANDLE_CAKES.get(color).key(), TFCBlocks.DYED_CANDLE.get(color).key()
            );
        }

        tag(commonTagOf(Registries.BLOCK, "candles")).add(TFCBlocks.CANDLE);

        //Chains

        for (Metal metal : Metal.values())
        {
            if (metal.allParts())
            {
                tag(commonTagOf(Registries.BLOCK, "chains/" + metal.getSerializedName())).add(TFCBlocks.METALS.get(metal).get(Metal.BlockType.CHAIN));
                tag(Tags.Blocks.CHAINS).addTag(commonTagOf(Registries.BLOCK, "chains/" + metal.getSerializedName()));
            }
        }

        //Chests

        tag(Tags.Blocks.CHESTS_WOODEN)
            .add(TFCBlocks.WOODS, Wood.BlockType.CHEST)
            .add(TFCBlocks.WOODS, Wood.BlockType.TRAPPED_CHEST);
        tag(Tags.Blocks.CHESTS_TRAPPED).add(TFCBlocks.WOODS, Wood.BlockType.TRAPPED_CHEST);

        //Clays

        for (SoilBlockType.Variant soil : SoilBlockType.Variant.values())
        {
            tag(commonTagOf(Registries.BLOCK, "clays/normal")).add(
                TFCBlocks.SOIL.get(SoilBlockType.CLAY).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.CLAY_DUFF).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).get(soil)
            );
        }
        tag(commonTagOf(Registries.BLOCK, "clays/kaolin")).add(
            TFCBlocks.PINK_KAOLIN_CLAY,
            TFCBlocks.RED_KAOLIN_CLAY,
            TFCBlocks.WHITE_KAOLIN_CLAY,
            TFCBlocks.KAOLIN_CLAY_GRASS
        );
        tag(commonTagOf(Registries.BLOCK, "clays/fire")).add(TFCBlocks.FIRE_CLAY_BLOCK);
        tag(commonTagOf(Registries.BLOCK, "clays/hardened")).add(TFCBlocks.HARDENED_CLAY);

        tag(commonTagOf(Registries.BLOCK, "clays")).addTags(
            commonTagOf(Registries.BLOCK, "clays/normal"),
            commonTagOf(Registries.BLOCK, "clays/kaolin"),
            commonTagOf(Registries.BLOCK, "clays/fire"),
            commonTagOf(Registries.BLOCK, "clays/hardened")
        );

        //Cobblestones

        for (Rock rock : Rock.values())
        {
            tag(commonTagOf(Registries.BLOCK, "cobblestones/" + rock.getSerializedName())).add(
                TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.COBBLE).get(),
                TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.MOSSY_COBBLE).get()
            );
            tag(Tags.Blocks.COBBLESTONES_MOSSY).add(TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.MOSSY_COBBLE).get());
            tag(commonTagOf(Registries.BLOCK, "cobblestones")).addTag(commonTagOf(Registries.BLOCK, "cobblestones/" + rock.getSerializedName()));
        }

        //Corals
        //c:corals, c:corals/dead, c:corals/living, c:corals/block, c:corals/fan, c:coral/plant

        tag(commonTagOf(Registries.BLOCK, "corals/plant"))
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL);
        tag(commonTagOf(Registries.BLOCK, "corals/fan"))
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL_FAN)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL_FAN)
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL_WALL_FAN)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL_WALL_FAN);
        tag(commonTagOf(Registries.BLOCK, "corals/block")).add(
            Blocks.BRAIN_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK,
            Blocks.BUBBLE_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK,
            Blocks.FIRE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK,
            Blocks.HORN_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK,
            Blocks.TUBE_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL_BLOCK
        );
        tag(commonTagOf(Registries.BLOCK, "corals/living")).add(
                Blocks.BRAIN_CORAL_BLOCK,
                Blocks.BUBBLE_CORAL_BLOCK,
                Blocks.FIRE_CORAL_BLOCK,
                Blocks.HORN_CORAL_BLOCK,
                Blocks.TUBE_CORAL_BLOCK)
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL)
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL_FAN)
            .add(TFCBlocks.CORAL, Coral.BlockType.CORAL_WALL_FAN);
        tag(commonTagOf(Registries.BLOCK, "corals/dead")).add(
                Blocks.DEAD_BRAIN_CORAL_BLOCK,
                Blocks.DEAD_BUBBLE_CORAL_BLOCK,
                Blocks.DEAD_FIRE_CORAL_BLOCK,
                Blocks.DEAD_HORN_CORAL_BLOCK,
                Blocks.DEAD_TUBE_CORAL_BLOCK)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL_FAN)
            .add(TFCBlocks.CORAL, Coral.BlockType.DEAD_CORAL_WALL_FAN);
        tag(commonTagOf(Registries.BLOCK, "corals")).addTags(
            commonTagOf(Registries.BLOCK, "corals/plant"),
            commonTagOf(Registries.BLOCK, "corals/fan"),
            commonTagOf(Registries.BLOCK, "corals/block"),
            commonTagOf(Registries.BLOCK, "corals/living"),
            commonTagOf(Registries.BLOCK, "corals/dead")
        );


        //Crops

        for (Crop crop : Crop.values())
        {
            tag(commonTagOf(Registries.BLOCK, "crops/" + crop.getSerializedName()))
                .add(TFCBlocks.CROPS.get(crop).key(), TFCBlocks.DEAD_CROPS.get(crop).key(), TFCBlocks.WILD_CROPS.get(crop).key());
            tag(commonTagOf(Registries.BLOCK, "crops"))
                .addTag(commonTagOf(Registries.BLOCK, "crops/" + crop.getSerializedName()));
        }
        tag(commonTagOf(Registries.BLOCK, "crops/corn"))
            .add(TFCBlocks.CROPS.get(Crop.MAIZE).key(), TFCBlocks.DEAD_CROPS.get(Crop.MAIZE).key(), TFCBlocks.WILD_CROPS.get(Crop.MAIZE).key());
        tag(commonTagOf(Registries.BLOCK, "crops"))
            .addTag(commonTagOf(Registries.BLOCK, "crops/corn"));
        tag(commonTagOf(Registries.BLOCK, "crops/beetroot"))
            .add(TFCBlocks.CROPS.get(Crop.BEET).key(), TFCBlocks.DEAD_CROPS.get(Crop.BEET).key(), TFCBlocks.WILD_CROPS.get(Crop.BEET).key());
        tag(commonTagOf(Registries.BLOCK, "crops"))
            .addTag(commonTagOf(Registries.BLOCK, "crops/beetroot"));
        tag(commonTagOf(Registries.BLOCK, "crops/pumpkin")).add(TFCBlocks.PUMPKIN);
        tag(commonTagOf(Registries.BLOCK, "crops/melon")).add(TFCBlocks.MELON);

        //Doors

        tag(commonTagOf(Registries.BLOCK, "doors/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.DOOR);
        tag(commonTagOf(Registries.BLOCK, "doors/iron"))
            .add(Blocks.IRON_DOOR)
            .add(TFCBlocks.FIREPROOF_DOOR);
        tag(commonTagOf(Registries.BLOCK, "doors")).addTags(
            commonTagOf(Registries.BLOCK, "doors/wooden"),
            commonTagOf(Registries.BLOCK, "doors/iron")
        );


        //Dyed
        //c:dyed, c:dyed/COLOR

        for (DyeColor color : DyeColor.values())
        {
            tag(commonTagOf(Registries.BLOCK, "dyed/" + color.getSerializedName())).add(
                TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color).slab().key(), TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color).stair().key(),
                TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color).wall().key(), TFCBlocks.POLISHED_ALABASTER.get(color).key(),
                TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color).slab().key(), TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color).stair().key(),
                TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color).wall().key(), TFCBlocks.RAW_ALABASTER.get(color).key(),
                TFCBlocks.GLAZED_LARGE_VESSELS.get(color).key(), TFCBlocks.DYED_CANDLE_CAKES.get(color).key()
            );
        }

        //Fences
        tag(Tags.Blocks.FENCES_WOODEN).add(TFCBlocks.WOODS, Wood.BlockType.FENCE);
        tag(Tags.Blocks.FENCE_GATES_WOODEN).add(TFCBlocks.WOODS, Wood.BlockType.FENCE_GATE);
        tag(Tags.Blocks.FENCES_WOODEN).add(TFCBlocks.WOODS, Wood.BlockType.LOG_FENCE);

        //Foods

        tag(commonTagOf(Registries.BLOCK, "foods/cake")).add(TFCBlocks.CAKE).add(TFCBlocks.DYED_CANDLE_CAKES);
        tag(commonTagOf(Registries.BLOCK, "foods/edible_when_placed")).add(TFCBlocks.CAKE).add(TFCBlocks.DYED_CANDLE_CAKES);
        tag(commonTagOf(Registries.BLOCK, "cake")).add(TFCBlocks.CAKE).add(TFCBlocks.DYED_CANDLE_CAKES);
        tag(commonTagOf(Registries.BLOCK, "foods")).addTags(
            commonTagOf(Registries.BLOCK, "foods/cake"),
            commonTagOf(Registries.BLOCK, "foods/edible_when_placed")
        );

        //Gravels

        for (Rock rock : Rock.values())
        {
            tag(commonTagOf(Registries.BLOCK, "gravels/" + rock.getSerializedName())).add(TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.GRAVEL));
            tag(commonTagOf(Registries.BLOCK, "gravels")).addTag(commonTagOf(Registries.BLOCK, "gravels/" + rock.getSerializedName()));
        }

        //Item Piles
        //c:item_piles, c:item_piles/TYPE

        tag(commonTagOf(Registries.BLOCK, "item_piles/log")).add(TFCBlocks.LOG_PILE, TFCBlocks.BURNING_LOG_PILE);
        tag(commonTagOf(Registries.BLOCK, "item_piles/double_ingot")).add(TFCBlocks.DOUBLE_INGOT_PILE);
        tag(commonTagOf(Registries.BLOCK, "item_piles/ingot")).add(TFCBlocks.INGOT_PILE);

        tag(commonTagOf(Registries.BLOCK, "item_piles")).addTags(
            commonTagOf(Registries.BLOCK, "item_piles/log"),
            commonTagOf(Registries.BLOCK, "item_piles/double_ingot"),
            commonTagOf(Registries.BLOCK, "item_piles/ingot")
        );

        // Ores
        // We don't include "ore_bearing_ground/???" tags, because they are specific to stone (or known vanilla stones) only
        // Also ignore "ore_rates/???" because unsure how they are supposed to apply...
        // Ore Rates added, singular means one item dropped when mined, plural means multiple items dropped when mined (ie lapis, copper, redstone etc)
        // For ores, we group ores by metal, not by ore. So ores/copper, not ores/tetrahedrite
        // For graded ores, we add ores/<metal>/grade, and include all grades in the main ore tag
        //c:ores c:ores/METAL, c:ores_in_ground/ROCK, c:ore_rates/RATE, c:ores/METAL/GRADE

        //Deposits
        for (OreDeposit dep : OreDeposit.values())
        {
            final String metalName = dep == OreDeposit.CASSITERITE ? "tin"
                : dep == OreDeposit.NATIVE_COPPER ? "copper"
                : dep == OreDeposit.NATIVE_GOLD ? "gold"
                : dep == OreDeposit.NATIVE_SILVER ? "silver"
                : dep.name().toLowerCase(Locale.ROOT);
            for (Rock rock : Rock.values())
            {
                tag(commonTagOf(Registries.BLOCK, "ores_in_ground/gravel")).add(TFCBlocks.ORE_DEPOSITS.get(rock).get(dep));
                tag(commonTagOf(Registries.BLOCK, "ore_rates/sparse")).add(TFCBlocks.ORE_DEPOSITS.get(rock).get(dep));
                tag(commonTagOf(Registries.BLOCK, "ores/" + metalName + "/small")).add(TFCBlocks.ORE_DEPOSITS.get(rock).get(dep));
            }
            tag(commonTagOf(Registries.BLOCK, "ores/" + metalName)).addTag(commonTagOf(Registries.BLOCK, "ores/" + metalName + "/small"));
            tag(commonTagOf(Registries.BLOCK, "ores")).addTag(commonTagOf(Registries.BLOCK, "ores/" + metalName));
        }
        tag(commonTagOf(Registries.BLOCK, "ores_in_ground")).addTag(commonTagOf(Registries.BLOCK, "ores_in_ground/gravel"));

        //Regular Ores
        for (Rock rock : Rock.values())
        {
            for (Ore ore : Ore.values())
            {
                if (ore.isGraded())
                {
                    final String metalName = ore.metal() == Metal.CAST_IRON ? "iron" : ore.metal().getSerializedName();
                    for (Ore.Grade grade : Ore.Grade.values())
                    {
                        tag(commonTagOf(Registries.BLOCK, "ores/" + metalName + "/" + grade.name().toLowerCase(Locale.ROOT))).add(TFCBlocks.GRADED_ORES.get(rock).get(ore).get(grade));
                        tag(commonTagOf(Registries.BLOCK, "ore_rates/singular")).add(TFCBlocks.GRADED_ORES.get(rock).get(ore).get(grade));
                        tag(commonTagOf(Registries.BLOCK, "ores_in_ground/" + rock.getSerializedName())).add(TFCBlocks.GRADED_ORES.get(rock).get(ore).get(grade));
                        tag(commonTagOf(Registries.BLOCK, "ores/" + metalName)).addTag(commonTagOf(Registries.BLOCK, "ores/" + metalName + "/" + grade.name().toLowerCase(Locale.ROOT)));
                    }
                    tag(commonTagOf(Registries.BLOCK, "ores")).addTag(commonTagOf(Registries.BLOCK, "ores/" + metalName));
                }
                else if (ore.hasBlock())
                {
                    tag(commonTagOf(Registries.BLOCK, "ores/" + ore.name().toLowerCase(Locale.ROOT))).add(TFCBlocks.ORES.get(rock).get(ore));
                    tag(commonTagOf(Registries.BLOCK, "ore_rates/singular")).add(TFCBlocks.ORES.get(rock).get(ore));
                    tag(commonTagOf(Registries.BLOCK, "ores_in_ground/" + rock.getSerializedName())).add(TFCBlocks.ORES.get(rock).get(ore));
                    tag(commonTagOf(Registries.BLOCK, "ores")).addTag(commonTagOf(Registries.BLOCK, "ores/" + ore.name().toLowerCase(Locale.ROOT)));
                }
            }
            tag(commonTagOf(Registries.BLOCK, "ores_in_ground")).addTag(commonTagOf(Registries.BLOCK, "ores_in_ground/" + rock.getSerializedName()));
        }

        for (var entry : TFCBlocks.SMALL_ORES.entrySet())
        {
            Ore ore = entry.getKey();
            Metal metal = ore.metal();
            final String metalName = metal == Metal.CAST_IRON ? "iron" : metal.getSerializedName();
            tag(commonTagOf(Registries.BLOCK, "ores/" + metalName + "/small")).add(TFCBlocks.SMALL_ORES.get(ore));
            tag(Tags.Blocks.ORE_RATES_SINGULAR).add(TFCBlocks.SMALL_ORES.get(ore));
            tag(commonTagOf(Registries.BLOCK, "ores/" + metalName)).addTag(commonTagOf(Registries.BLOCK, "ores/" + metalName + "/small"));
        }

        //No Block

        tag(commonTagOf(Registries.BLOCK, "ores/salt")).add(TFCBlocks.HALITE);
        tag(Tags.Blocks.ORE_RATES_DENSE).add(TFCBlocks.HALITE);
        tag(commonTagOf(Registries.BLOCK, "ores/coal")).add(
            TFCBlocks.LIGNITE,
            TFCBlocks.BITUMINOUS_COAL
        );
        tag(Tags.Blocks.ORE_RATES_SINGULAR).add(
            TFCBlocks.LIGNITE,
            TFCBlocks.BITUMINOUS_COAL
        );

        tag(commonTagOf(Registries.BLOCK, "ores")).addTags(
            commonTagOf(Registries.BLOCK, "ores/salt"),
            commonTagOf(Registries.BLOCK, "ores/coal")
        );

        //Leaves
        //c:leaves

        tag(commonTagOf(Registries.BLOCK, "leaves")).add(TFCBlocks.WOODS, Wood.BlockType.LEAVES);

        //Logs
        //c:logs, c:logs/WOOD

        for (Wood wood : Wood.VALUES)
        {
            tag(commonTagOf(Registries.BLOCK, "logs/" + wood.getSerializedName())).add(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LOG));
            tag(commonTagOf(Registries.BLOCK, "logs")).addTags(commonTagOf(Registries.BLOCK, "logs/" + wood.getSerializedName()));
        }

        //Planks
        //c:planks, c:planks/WOOD

        for (Wood wood : Wood.values())
        {
            tag(commonTagOf(Registries.BLOCK, "planks/" + wood.getSerializedName())).add(
                TFCBlocks.WOODS.get(wood).get(Wood.BlockType.PLANKS),
                TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SLAB),
                TFCBlocks.WOODS.get(wood).get(Wood.BlockType.STAIRS)
            );
            tag(commonTagOf(Registries.BLOCK, "planks")).addTag(commonTagOf(Registries.BLOCK, "planks" + wood.getSerializedName()));
        }

        //Plants
        //c:plants, c:plants/tall, c:plants/small, c:plants/water, c:plants/salt_water, c:plants/ground, c:plants/wall


        //Player Workstations
        //c:player_workstations, c:player_workstations/WORK_STATION

        tag(commonTagOf(Registries.BLOCK, "player_workstations/anvil"))
            .add(TFCBlocks.METALS, Metal.BlockType.ANVIL)
            .add(TFCBlocks.ROCK_ANVILS);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/blast_furnace")).add(TFCBlocks.BLAST_FURNACE);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/bloomery")).add(TFCBlocks.BLOOMERY);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/composter")).add(TFCBlocks.COMPOSTER);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/crucible")).add(TFCBlocks.CRUCIBLE);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/charcoal_forge")).add(TFCBlocks.CHARCOAL_FORGE);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/firebox")).add(TFCBlocks.FIREBOX);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/firepit")).add(TFCBlocks.FIREPIT);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/pit_kiln")).add(TFCBlocks.PIT_KILN);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/grill")).add(TFCBlocks.GRILL);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/mold_table")).add(TFCBlocks.MOLD_TABLE);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/nest_box")).add(TFCBlocks.NEST_BOX);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/quern")).add(TFCBlocks.QUERN);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/lectern")).add(TFCBlocks.WOODS, Wood.BlockType.LECTERN);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/loom")).add(TFCBlocks.WOODS, Wood.BlockType.LOOM);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/scraping")).add(TFCBlocks.SCRAPING);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/scribing_table")).add(TFCBlocks.WOODS, Wood.BlockType.SCRIBING_TABLE);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/sewing_table")).add(TFCBlocks.WOODS, Wood.BlockType.SEWING_TABLE);
        tag(commonTagOf(Registries.BLOCK, "player_workstations/stove")).add(TFCBlocks.STOVE, TFCBlocks.STOVE_POT);
        tag(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES).add(TFCBlocks.WOODS, Wood.BlockType.WORKBENCH);
        tag(commonTagOf(Registries.BLOCK, "player_workstations")).addTags(
            commonTagOf(Registries.BLOCK, "player_workstations/anvil"),
            commonTagOf(Registries.BLOCK, "player_workstations/blast_furnace"),
            commonTagOf(Registries.BLOCK, "player_workstations/bloomery"),
            commonTagOf(Registries.BLOCK, "player_workstations/composter"),
            commonTagOf(Registries.BLOCK, "player_workstations/charcoal_forge"),
            commonTagOf(Registries.BLOCK, "player_workstations/crucible"),
            commonTagOf(Registries.BLOCK, "player_workstations/firebox"),
            commonTagOf(Registries.BLOCK, "player_workstations/firepit"),
            commonTagOf(Registries.BLOCK, "player_workstations/grill"),
            commonTagOf(Registries.BLOCK, "player_workstations/mold_table"),
            commonTagOf(Registries.BLOCK, "player_workstations/nest_box"),
            commonTagOf(Registries.BLOCK, "player_workstations/quern"),
            commonTagOf(Registries.BLOCK, "player_workstations/lectern"),
            commonTagOf(Registries.BLOCK, "player_workstations/loom"),
            commonTagOf(Registries.BLOCK, "player_workstations/scraping"),
            commonTagOf(Registries.BLOCK, "player_workstations/scribing_table"),
            commonTagOf(Registries.BLOCK, "player_workstations/sewing_table"),
            commonTagOf(Registries.BLOCK, "player_workstations/stove")
        );

        //Potted Plants

        tag(commonTagOf(Registries.BLOCK, "potted_plants")).add(TFCBlocks.POTTED_PLANTS).add(TFCBlocks.FRUIT_TREE_POTTED_SAPLINGS).add(TFCBlocks.WOODS, Wood.BlockType.POTTED_SAPLING);

        //Power
        //c:power, c:power/capability, c:power/capability/tfc_mech, c:power/function, c:power/function/provider, c:power/function/transmitter, c:power/function/consumer

        tag(commonTagOf(Registries.BLOCK, "power/capability/tfc_mech"))
            .add(TFCBlocks.WOODS, Wood.BlockType.AXLE)
            .add(TFCBlocks.WOODS, Wood.BlockType.ENCASED_AXLE)
            .add(TFCBlocks.WOODS, Wood.BlockType.CLUTCH)
            .add(TFCBlocks.WOODS, Wood.BlockType.BLADED_AXLE)
            .add(TFCBlocks.WOODS, Wood.BlockType.GEAR_BOX)
            .add(TFCBlocks.WOODS, Wood.BlockType.WATER_WHEEL)
            .add(TFCBlocks.WOODS, Wood.BlockType.WINDMILL)
            .add(TFCBlocks.TRIP_HAMMER, TFCBlocks.CREATIVE_MOTOR, TFCBlocks.CRANKSHAFT, TFCBlocks.POWER_LOOM, TFCBlocks.QUERN);
        tag(commonTagOf(Registries.BLOCK, "power/function/provider"))
            .add(TFCBlocks.WOODS, Wood.BlockType.WATER_WHEEL)
            .add(TFCBlocks.WOODS, Wood.BlockType.WATER_WHEEL)
            .add(TFCBlocks.CREATIVE_MOTOR);
        tag(commonTagOf(Registries.BLOCK, "power/function/transmitter"))
            .add(TFCBlocks.WOODS, Wood.BlockType.AXLE)
            .add(TFCBlocks.WOODS, Wood.BlockType.ENCASED_AXLE)
            .add(TFCBlocks.WOODS, Wood.BlockType.CLUTCH)
            .add(TFCBlocks.WOODS, Wood.BlockType.BLADED_AXLE)
            .add(TFCBlocks.WOODS, Wood.BlockType.GEAR_BOX);
        tag(commonTagOf(Registries.BLOCK, "power/capability/tfc_mech")).add(
            TFCBlocks.TRIP_HAMMER, TFCBlocks.CRANKSHAFT,
            TFCBlocks.QUERN, TFCBlocks.POWER_LOOM
        );

        tag(commonTagOf(Registries.BLOCK, "power/capability")).addTag(commonTagOf(Registries.BLOCK, "power/capability/tfc_mech"));
        tag(commonTagOf(Registries.BLOCK, "power")).addTag(commonTagOf(Registries.BLOCK, "power/capability"));
        tag(commonTagOf(Registries.BLOCK, "power/function")).addTag(commonTagOf(Registries.BLOCK, "power/function/provider"));
        tag(commonTagOf(Registries.BLOCK, "power/function")).addTag(commonTagOf(Registries.BLOCK, "power/function/transmitter"));
        tag(commonTagOf(Registries.BLOCK, "power/function")).addTag(commonTagOf(Registries.BLOCK, "power/function/consumer"));
        tag(commonTagOf(Registries.BLOCK, "power")).addTag(commonTagOf(Registries.BLOCK, "power/function"));

        //Pressure Plates
        //c:pressure_plates, c:pressure_plates/wooden, c:pressure_plates/stone

        tag(commonTagOf(Registries.BLOCK, "pressure_plates/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.PRESSURE_PLATE);
        tag(commonTagOf(Registries.BLOCK, "pressure_plates/stone")).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.PRESSURE_PLATE);

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

        tag(commonTagOf(Registries.BLOCK, "rods/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.TWIG);
        tag(commonTagOf(Registries.BLOCK, "rods")).addTag(commonTagOf(Registries.BLOCK, "rods/wooden"));

        //Sands

        tag(commonTagOf(Registries.BLOCK, "sands/volcanic")).add(TFCBlocks.SAND.get(SandBlockType.BLACK));
        tag(commonTagOf(Registries.BLOCK, "sands/hematitic")).add(
            TFCBlocks.SAND.get(SandBlockType.RED),
            TFCBlocks.SAND.get(SandBlockType.PINK),
            TFCBlocks.SAND.get(SandBlockType.YELLOW)
        );
        tag(commonTagOf(Registries.BLOCK, "sands/olivine")).add(
            TFCBlocks.SAND.get(SandBlockType.BROWN),
            TFCBlocks.SAND.get(SandBlockType.GREEN)
        );
        tag(commonTagOf(Registries.BLOCK, "sands/silica")).add(TFCBlocks.SAND.get(SandBlockType.WHITE));

        tag(commonTagOf(Registries.BLOCK, "sands/black")).add(TFCBlocks.SAND.get(SandBlockType.BLACK));
        tag(commonTagOf(Registries.BLOCK, "sands/red")).add(TFCBlocks.SAND.get(SandBlockType.RED));
        tag(commonTagOf(Registries.BLOCK, "sands/pink")).add(TFCBlocks.SAND.get(SandBlockType.PINK));
        tag(commonTagOf(Registries.BLOCK, "sands/yellow")).add(TFCBlocks.SAND.get(SandBlockType.YELLOW));
        tag(commonTagOf(Registries.BLOCK, "sands/brown")).add(TFCBlocks.SAND.get(SandBlockType.BROWN));
        tag(commonTagOf(Registries.BLOCK, "sands/green")).add(TFCBlocks.SAND.get(SandBlockType.GREEN));

        tag(commonTagOf(Registries.BLOCK, "sands")).addTags(
            commonTagOf(Registries.BLOCK, "sands/volcanic"),
            commonTagOf(Registries.BLOCK, "sands/hematitic"),
            commonTagOf(Registries.BLOCK, "sands/olivine"),
            commonTagOf(Registries.BLOCK, "sands/silica"),
            commonTagOf(Registries.BLOCK, "sands/black"),
            commonTagOf(Registries.BLOCK, "sands/red"),
            commonTagOf(Registries.BLOCK, "sands/pink"),
            commonTagOf(Registries.BLOCK, "sands/yellow"),
            commonTagOf(Registries.BLOCK, "sands/brown"),
            commonTagOf(Registries.BLOCK, "sands/green")
        );

        //Sandstone

        tag(Tags.Blocks.SANDSTONE_BLOCKS).add2(TFCBlocks.SANDSTONE);
        tag(Tags.Blocks.SANDSTONE_SLABS).add2(TFCBlocks.SANDSTONE_DECORATIONS, DecorationBlockHolder::slab);
        tag(Tags.Blocks.SANDSTONE_STAIRS).add2(TFCBlocks.SANDSTONE_DECORATIONS, DecorationBlockHolder::stair);
        tag(commonTagOf(Registries.BLOCK, "sandstone/walls")).add2(TFCBlocks.SANDSTONE_DECORATIONS, DecorationBlockHolder::wall);

        tag(commonTagOf(Registries.BLOCK, "sandstone/black_blocks")).add(
            TFCBlocks.SANDSTONE.get(SandBlockType.BLACK).get(SandstoneBlockType.CUT),
            TFCBlocks.SANDSTONE.get(SandBlockType.BLACK).get(SandstoneBlockType.RAW),
            TFCBlocks.SANDSTONE.get(SandBlockType.BLACK).get(SandstoneBlockType.SMOOTH)
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/brown_blocks")).add(
            TFCBlocks.SANDSTONE.get(SandBlockType.BROWN).get(SandstoneBlockType.CUT),
            TFCBlocks.SANDSTONE.get(SandBlockType.BROWN).get(SandstoneBlockType.RAW),
            TFCBlocks.SANDSTONE.get(SandBlockType.BROWN).get(SandstoneBlockType.SMOOTH)
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/green_blocks")).add(
            TFCBlocks.SANDSTONE.get(SandBlockType.GREEN).get(SandstoneBlockType.CUT),
            TFCBlocks.SANDSTONE.get(SandBlockType.GREEN).get(SandstoneBlockType.RAW),
            TFCBlocks.SANDSTONE.get(SandBlockType.GREEN).get(SandstoneBlockType.SMOOTH)
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/pink_blocks")).add(
            TFCBlocks.SANDSTONE.get(SandBlockType.PINK).get(SandstoneBlockType.CUT),
            TFCBlocks.SANDSTONE.get(SandBlockType.PINK).get(SandstoneBlockType.RAW),
            TFCBlocks.SANDSTONE.get(SandBlockType.PINK).get(SandstoneBlockType.SMOOTH)
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/red_blocks")).add(
            TFCBlocks.SANDSTONE.get(SandBlockType.RED).get(SandstoneBlockType.CUT),
            TFCBlocks.SANDSTONE.get(SandBlockType.RED).get(SandstoneBlockType.RAW),
            TFCBlocks.SANDSTONE.get(SandBlockType.RED).get(SandstoneBlockType.SMOOTH)
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/white_blocks")).add(
            TFCBlocks.SANDSTONE.get(SandBlockType.WHITE).get(SandstoneBlockType.CUT),
            TFCBlocks.SANDSTONE.get(SandBlockType.WHITE).get(SandstoneBlockType.RAW),
            TFCBlocks.SANDSTONE.get(SandBlockType.WHITE).get(SandstoneBlockType.SMOOTH)
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/yellow_blocks")).add(
            TFCBlocks.SANDSTONE.get(SandBlockType.YELLOW).get(SandstoneBlockType.CUT),
            TFCBlocks.SANDSTONE.get(SandBlockType.YELLOW).get(SandstoneBlockType.RAW),
            TFCBlocks.SANDSTONE.get(SandBlockType.YELLOW).get(SandstoneBlockType.SMOOTH)
        );

        tag(commonTagOf(Registries.BLOCK, "sandstone/black_slabs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BLACK).get(SandstoneBlockType.CUT).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BLACK).get(SandstoneBlockType.RAW).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BLACK).get(SandstoneBlockType.SMOOTH).slab()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/brown_slabs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BROWN).get(SandstoneBlockType.CUT).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BROWN).get(SandstoneBlockType.RAW).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BROWN).get(SandstoneBlockType.SMOOTH).slab()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/green_slabs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.GREEN).get(SandstoneBlockType.CUT).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.GREEN).get(SandstoneBlockType.RAW).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.GREEN).get(SandstoneBlockType.SMOOTH).slab()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/pink_slabs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.PINK).get(SandstoneBlockType.CUT).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.PINK).get(SandstoneBlockType.RAW).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.PINK).get(SandstoneBlockType.SMOOTH).slab()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/red_slabs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.RED).get(SandstoneBlockType.CUT).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.RED).get(SandstoneBlockType.RAW).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.RED).get(SandstoneBlockType.SMOOTH).slab()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/white_slabs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.WHITE).get(SandstoneBlockType.CUT).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.WHITE).get(SandstoneBlockType.RAW).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.WHITE).get(SandstoneBlockType.SMOOTH).slab()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/yellow_slabs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.YELLOW).get(SandstoneBlockType.CUT).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.YELLOW).get(SandstoneBlockType.RAW).slab(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.YELLOW).get(SandstoneBlockType.SMOOTH).slab()
        );

        tag(commonTagOf(Registries.BLOCK, "sandstone/black_stairs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BLACK).get(SandstoneBlockType.CUT).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BLACK).get(SandstoneBlockType.RAW).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BLACK).get(SandstoneBlockType.SMOOTH).stair()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/brown_stairs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BROWN).get(SandstoneBlockType.CUT).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BROWN).get(SandstoneBlockType.RAW).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BROWN).get(SandstoneBlockType.SMOOTH).stair()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/green_stairs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.GREEN).get(SandstoneBlockType.CUT).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.GREEN).get(SandstoneBlockType.RAW).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.GREEN).get(SandstoneBlockType.SMOOTH).stair()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/pink_stairs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.PINK).get(SandstoneBlockType.CUT).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.PINK).get(SandstoneBlockType.RAW).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.PINK).get(SandstoneBlockType.SMOOTH).stair()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/red_stairs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.RED).get(SandstoneBlockType.CUT).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.RED).get(SandstoneBlockType.RAW).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.RED).get(SandstoneBlockType.SMOOTH).stair()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/white_stairs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.WHITE).get(SandstoneBlockType.CUT).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.WHITE).get(SandstoneBlockType.RAW).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.WHITE).get(SandstoneBlockType.SMOOTH).stair()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/yellow_stairs")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.YELLOW).get(SandstoneBlockType.CUT).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.YELLOW).get(SandstoneBlockType.RAW).stair(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.YELLOW).get(SandstoneBlockType.SMOOTH).stair()
        );

        tag(commonTagOf(Registries.BLOCK, "sandstone/black_walls")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BLACK).get(SandstoneBlockType.CUT).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BLACK).get(SandstoneBlockType.RAW).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BLACK).get(SandstoneBlockType.SMOOTH).wall()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/brown_walls")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BROWN).get(SandstoneBlockType.CUT).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BROWN).get(SandstoneBlockType.RAW).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.BROWN).get(SandstoneBlockType.SMOOTH).wall()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/green_walls")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.GREEN).get(SandstoneBlockType.CUT).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.GREEN).get(SandstoneBlockType.RAW).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.GREEN).get(SandstoneBlockType.SMOOTH).wall()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/pink_walls")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.PINK).get(SandstoneBlockType.CUT).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.PINK).get(SandstoneBlockType.RAW).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.PINK).get(SandstoneBlockType.SMOOTH).wall()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/red_walls")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.RED).get(SandstoneBlockType.CUT).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.RED).get(SandstoneBlockType.RAW).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.RED).get(SandstoneBlockType.SMOOTH).wall()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/white_walls")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.WHITE).get(SandstoneBlockType.CUT).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.WHITE).get(SandstoneBlockType.RAW).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.WHITE).get(SandstoneBlockType.SMOOTH).wall()
        );
        tag(commonTagOf(Registries.BLOCK, "sandstone/yellow_walls")).add(
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.YELLOW).get(SandstoneBlockType.CUT).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.YELLOW).get(SandstoneBlockType.RAW).wall(),
            TFCBlocks.SANDSTONE_DECORATIONS.get(SandBlockType.YELLOW).get(SandstoneBlockType.SMOOTH).wall()
        );

        tag(commonTagOf(Registries.BLOCK, "sandstone")).addTags(
            commonTagOf(Registries.BLOCK, "sandstone/walls"),
            commonTagOf(Registries.BLOCK, "sandstone/black_blocks"),
            commonTagOf(Registries.BLOCK, "sandstone/brown_blocks"),
            commonTagOf(Registries.BLOCK, "sandstone/green_blocks"),
            commonTagOf(Registries.BLOCK, "sandstone/pink_blocks"),
            commonTagOf(Registries.BLOCK, "sandstone/red_blocks"),
            commonTagOf(Registries.BLOCK, "sandstone/white_blocks"),
            commonTagOf(Registries.BLOCK, "sandstone/yellow_blocks"),
            commonTagOf(Registries.BLOCK, "sandstone/black_slabs"),
            commonTagOf(Registries.BLOCK, "sandstone/brown_slabs"),
            commonTagOf(Registries.BLOCK, "sandstone/green_slabs"),
            commonTagOf(Registries.BLOCK, "sandstone/pink_slabs"),
            commonTagOf(Registries.BLOCK, "sandstone/red_slabs"),
            commonTagOf(Registries.BLOCK, "sandstone/white_slabs"),
            commonTagOf(Registries.BLOCK, "sandstone/yellow_slabs"),
            commonTagOf(Registries.BLOCK, "sandstone/black_stairs"),
            commonTagOf(Registries.BLOCK, "sandstone/brown_stairs"),
            commonTagOf(Registries.BLOCK, "sandstone/green_stairs"),
            commonTagOf(Registries.BLOCK, "sandstone/pink_stairs"),
            commonTagOf(Registries.BLOCK, "sandstone/red_stairs"),
            commonTagOf(Registries.BLOCK, "sandstone/white_stairs"),
            commonTagOf(Registries.BLOCK, "sandstone/yellow_stairs"),
            commonTagOf(Registries.BLOCK, "sandstone/black_walls"),
            commonTagOf(Registries.BLOCK, "sandstone/brown_walls"),
            commonTagOf(Registries.BLOCK, "sandstone/green_walls"),
            commonTagOf(Registries.BLOCK, "sandstone/pink_walls"),
            commonTagOf(Registries.BLOCK, "sandstone/red_walls"),
            commonTagOf(Registries.BLOCK, "sandstone/white_walls"),
            commonTagOf(Registries.BLOCK, "sandstone/yellow_walls")
        );

        //Soils -- Could also be Dirts
        //c:soils, c:soils/VARIANT

        for (SoilBlockType.Variant soil : SoilBlockType.Variant.values())
        {
            tag(commonTagOf(Registries.BLOCK, "soils/" + soil.name())).add(
                TFCBlocks.SOIL.get(SoilBlockType.CLAY).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.CLAY_DUFF).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.COARSE_DIRT).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.DRYING_BRICKS).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.DUFF).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.GRASS_PATH).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.MUD).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.MUDDY_ROOTS).get(soil),
                TFCBlocks.SOIL.get(SoilBlockType.ROOTED_DIRT).get(soil)
            );
            tag(commonTagOf(Registries.BLOCK, "soils")).addTag(commonTagOf(Registries.BLOCK, "soils/" + soil.name()));
        }

        //Stones
        //c:stones, c:stones/ROCK, c:stones/mossy, c:stones/spike, c:stones/raw, c:stones/hardened, c:stones/loose

        for (Rock rock : Rock.values())
        {
            tag(commonTagOf(Registries.BLOCK, "stones/" + rock.getSerializedName())).add(
                TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.HARDENED),
                TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.LOOSE),
                TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.MOSSY_LOOSE),
                TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.RAW),
                TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.SPIKE)
            );
            tag(Tags.Blocks.STONES).addTags(commonTagOf(Registries.BLOCK, "stones/" + rock.getSerializedName()));
        }

        tag(STONES_SPIKE).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.SPIKE);
        tag(STONES_RAW).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.RAW);
        tag(STONES_HARDENED).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.HARDENED);
        tag(STONES_LOOSE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.LOOSE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_LOOSE);
        tag(commonTagOf(Registries.BLOCK, "stones/mossy")).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_LOOSE);
        tag(Tags.Blocks.STONES).addTags(STONES_HARDENED, STONES_LOOSE, STONES_RAW, STONES_SPIKE, commonTagOf(Registries.BLOCK, "stones/mossy"));

        //Saplings
        //c:saplings, c:saplings/WOOD

        for (Wood wood : Wood.VALUES)
        {
            tag(commonTagOf(Registries.BLOCK, "saplings/" + wood.getSerializedName())).add(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SAPLING));
            tag(commonTagOf(Registries.BLOCK, "saplings")).addTags(commonTagOf(Registries.BLOCK, "saplings/" + wood.getSerializedName()));
        }

        //Shelves
        //c:shelves, c:shelves/wooden, c:shelves/brick

        tag(commonTagOf(Registries.BLOCK, "shelves/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.SHELF);
        tag(commonTagOf(Registries.BLOCK, "shelves/brick")).add(TFCBlocks.FIRE_BRICK_SHELF);
        tag(commonTagOf(Registries.BLOCK, "shelves")).addTags(
            commonTagOf(Registries.BLOCK, "shelves/wooden"),
            commonTagOf(Registries.BLOCK, "shelves/brick")
        );

        //Signs

        tag(commonTagOf(Registries.BLOCK, "signs/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.SIGN).add(TFCBlocks.WOODS, Wood.BlockType.WALL_SIGN);
        tag(commonTagOf(Registries.BLOCK, "signs/hanging/bismuth_bronze")).add(TFCBlocks.CEILING_HANGING_SIGNS, Metal.BISMUTH_BRONZE).add(TFCBlocks.WALL_HANGING_SIGNS, Metal.BISMUTH_BRONZE);
        tag(commonTagOf(Registries.BLOCK, "signs/hanging/black_bronze")).add(TFCBlocks.CEILING_HANGING_SIGNS, Metal.BLACK_BRONZE).add(TFCBlocks.WALL_HANGING_SIGNS, Metal.BLACK_BRONZE);
        tag(commonTagOf(Registries.BLOCK, "signs/hanging/black_steel")).add(TFCBlocks.CEILING_HANGING_SIGNS, Metal.BLACK_STEEL).add(TFCBlocks.WALL_HANGING_SIGNS, Metal.BLACK_STEEL);
        tag(commonTagOf(Registries.BLOCK, "signs/hanging/blue_steel")).add(TFCBlocks.CEILING_HANGING_SIGNS, Metal.BLUE_STEEL).add(TFCBlocks.WALL_HANGING_SIGNS, Metal.BLUE_STEEL);
        tag(commonTagOf(Registries.BLOCK, "signs/hanging/bronze")).add(TFCBlocks.CEILING_HANGING_SIGNS, Metal.BRONZE).add(TFCBlocks.WALL_HANGING_SIGNS, Metal.BRONZE);
        tag(commonTagOf(Registries.BLOCK, "signs/hanging/copper")).add(TFCBlocks.CEILING_HANGING_SIGNS, Metal.COPPER).add(TFCBlocks.WALL_HANGING_SIGNS, Metal.COPPER);
        tag(commonTagOf(Registries.BLOCK, "signs/hanging/red_steel")).add(TFCBlocks.CEILING_HANGING_SIGNS, Metal.RED_STEEL).add(TFCBlocks.WALL_HANGING_SIGNS, Metal.RED_STEEL);
        tag(commonTagOf(Registries.BLOCK, "signs/hanging/steel")).add(TFCBlocks.CEILING_HANGING_SIGNS, Metal.STEEL).add(TFCBlocks.WALL_HANGING_SIGNS, Metal.STEEL);
        tag(commonTagOf(Registries.BLOCK, "signs/hanging/iron")).add(TFCBlocks.CEILING_HANGING_SIGNS, Metal.WROUGHT_IRON).add(TFCBlocks.WALL_HANGING_SIGNS, Metal.WROUGHT_IRON);
        tag(commonTagOf(Registries.BLOCK, "signs/hanging")).addTags(
            commonTagOf(Registries.BLOCK, "signs/hanging/bismuth_bronze"),
            commonTagOf(Registries.BLOCK, "signs/hanging/black_bronze"),
            commonTagOf(Registries.BLOCK, "signs/hanging/black_steel"),
            commonTagOf(Registries.BLOCK, "signs/hanging/blue_steel"),
            commonTagOf(Registries.BLOCK, "signs/hanging/bronze"),
            commonTagOf(Registries.BLOCK, "signs/hanging/copper"),
            commonTagOf(Registries.BLOCK, "signs/hanging/red_steel"),
            commonTagOf(Registries.BLOCK, "signs/hanging/steel"),
            commonTagOf(Registries.BLOCK, "signs/hanging/iron")
        );
        tag(commonTagOf(Registries.BLOCK, "signs")).addTags(
            commonTagOf(Registries.BLOCK, "signs/wooden"),
            commonTagOf(Registries.BLOCK, "signs/hanging")
        );

        //Slabs


        //Sluices

        tag(commonTagOf(Registries.BLOCK, "sluices/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.SLUICE);
        tag(commonTagOf(Registries.BLOCK, "sluices")).addTags(commonTagOf(Registries.BLOCK, "sluices/wooden"));

        //Stairs


        //Storage Blocks
        //Storage blocks are blocks that take 4 or 9 items and compress them into a block and can be then converted back into the 4/9 items

        tag(Tags.Blocks.STORAGE_BLOCKS_WHEAT).remove(Blocks.HAY_BLOCK); // We repurpose this as storing straw

        //Stripped Logs
        //c:stripped_logs, c:stripped_logs/WOOD

        for (Wood wood : Wood.values())
        {
            tag(commonTagOf(Registries.BLOCK, "stripped_logs/" + wood.getSerializedName())).add(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.STRIPPED_LOG));
            tag(Tags.Blocks.STRIPPED_LOGS).addTag(commonTagOf(Registries.BLOCK, "stripped_logs/" + wood.getSerializedName()));
        }

        //Stripped Wood
        //c:stripped_woods, c:stripped_woods/WOOD

        for (Wood wood : Wood.values())
        {
            tag(commonTagOf(Registries.BLOCK, "stripped_woods/" + wood.getSerializedName())).add(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.STRIPPED_WOOD));
            tag(Tags.Blocks.STRIPPED_WOODS).addTag(commonTagOf(Registries.BLOCK, "stripped_woods/" + wood.getSerializedName()));
        }

        //Supports

        tag(commonTagOf(Registries.BLOCK, "supports/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.HORIZONTAL_SUPPORT).add(TFCBlocks.WOODS, Wood.BlockType.VERTICAL_SUPPORT);
        tag(commonTagOf(Registries.BLOCK, "supports")).addTags(commonTagOf(Registries.BLOCK, "supports/wooden"));

        //Thatch

        tag(commonTagOf(Registries.BLOCK, "thatch")).add(TFCBlocks.THATCH);

        //Tool Racks

        tag(commonTagOf(Registries.BLOCK, "tool_racks/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.TOOL_RACK);
        tag(commonTagOf(Registries.BLOCK, "tool_racks")).addTags(commonTagOf(Registries.BLOCK, "tool_racks/wooden"));

        //Trapdoors

        tag(commonTagOf(Registries.BLOCK, "trapdoors/wooden")).add(TFCBlocks.WOODS, Wood.BlockType.TRAPDOOR);
        tag(commonTagOf(Registries.BLOCK, "trapdoors/metal")).add(TFCBlocks.METALS, Metal.BlockType.TRAPDOOR);
        tag(commonTagOf(Registries.BLOCK, "trapdoors")).addTags(
            commonTagOf(Registries.BLOCK, "trapdoors/wooden"),
            commonTagOf(Registries.BLOCK, "trapdoors/metal")
        );

        //Walls


        //Woods
        //c:woods, c:woods/WOOD

        for (Wood wood : Wood.VALUES)
        {
            tag(commonTagOf(Registries.BLOCK, "woods/" + wood.getSerializedName())).add(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.WOOD));
            tag(commonTagOf(Registries.BLOCK, "woods")).addTags(commonTagOf(Registries.BLOCK, "woods/" + wood.getSerializedName()));
        }

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