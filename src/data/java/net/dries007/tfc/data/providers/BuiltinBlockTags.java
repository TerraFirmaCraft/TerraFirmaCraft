package net.dries007.tfc.data.providers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import com.google.common.base.Preconditions;
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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper.ResourceType;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.TFCBlocks;
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
        // ===== Minecraft Functionality Tags ===== //
        tag(BlockTags.REPLACEABLE).add(BuiltInRegistries.BLOCK
            .entrySet()
            .stream()
            .filter(e -> e.getValue().defaultBlockState().canBeReplaced() && e.getKey().location().getNamespace().equals(TerraFirmaCraft.MOD_ID))
            .sorted(Map.Entry.comparingByKey()) // Determinism
            .map(Map.Entry::getValue));

        tag(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON).add(TFCBlocks.SEA_ICE, TFCBlocks.ICE_PILE);
        tag(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON).add(TFCBlocks.SOIL.get(SoilBlockType.MUD));

        // ===== Common Tags ===== //

        pivot(TFCBlocks.METALS, Metal.BlockType.BLOCK).forEach((metal, block) ->
            tag(storageBlockTagOf(Registries.BLOCK, metal)).add(block));

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

        tag(Tags.Blocks.STORAGE_BLOCKS_WHEAT).remove(Blocks.HAY_BLOCK);
        tag(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES).addTag(WORKBENCHES);

        // ===== TFC Tags ===== //

        tag(CAN_TRIGGER_COLLAPSE).addTags(Tags.Blocks.ORES, Tags.Blocks.STONES);
        tag(CAN_START_COLLAPSE).addTags(Tags.Blocks.ORES, TFCTags.Blocks.STONES_RAW);
        tag(CAN_COLLAPSE).addTags(Tags.Blocks.ORES, Tags.Blocks.STONES, STONES_SMOOTH, STONES_SPIKE);

        tag(CAN_LANDSLIDE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.COBBLE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_COBBLE)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.GRAVEL)
            .add(TFCBlocks.SAND)
            .addAll(TFCBlocks.ORE_DEPOSITS)
            .add(
                TFCBlocks.WHITE_KAOLIN_CLAY,
                TFCBlocks.PINK_KAOLIN_CLAY,
                TFCBlocks.RED_KAOLIN_CLAY
            );
        tag(SUPPORTS_LANDSLIDE).addTags(FARMLANDS, PATHS);
        tag(NOT_SOLID_SUPPORTING).addTags(STONES_SMOOTH);
        tag(TOUGHNESS_1).add(TFCBlocks.CHARCOAL_PILE, TFCBlocks.CHARCOAL_FORGE);
        tag(TOUGHNESS_2).addTag(STONES);
        tag(TOUGHNESS_3).add(Blocks.BEDROCK);
        tag(BREAKS_WHEN_ISOLATED).addTag(STONES_RAW);

        tag(STONES).addTags(STONES_RAW, STONES_HARDENED);
        tag(STONES_RAW).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.RAW);
        tag(STONES_HARDENED).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.HARDENED);
        tag(STONES_SMOOTH).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.SMOOTH);
        tag(BlockTags.STONE_BRICKS)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.BRICKS)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.CRACKED_BRICKS)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_BRICKS)
            .add(Blocks.BRICKS)
            .add(TFCBlocks.FIRE_BRICKS);
        tag(STONES_SMOOTH_SLABS).add(pivot(TFCBlocks.ROCK_DECORATIONS, Rock.BlockType.SMOOTH).values(), DecorationBlockHolder::slab);
        tag(STONES_SPIKE).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.SPIKE);
        tag(STONES_PRESSURE_PLATES)
            .add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.PRESSURE_PLATE)
            .addOptionalTag(ResourceLocation.withDefaultNamespace("stone_pressure_plates"));
        tag(INSULATION)
            .addTags(STONES, STONES_SMOOTH, BlockTags.STONE_BRICKS, Tags.Blocks.COBBLESTONES)
            .add(Blocks.BRICKS)
            .add(TFCBlocks.FIRE_BRICKS);

        tag(Tags.Blocks.COBBLESTONES_NORMAL).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.COBBLE);
        tag(Tags.Blocks.COBBLESTONES_MOSSY).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.MOSSY_COBBLE);
        tag(Tags.Blocks.GRAVELS).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.GRAVEL);
        tag(Tags.Blocks.SANDS).add(TFCBlocks.SAND);

        tag(LAMPS).add(TFCBlocks.METALS, Metal.BlockType.LAMP);

        // Minecraft logs tags contain all log, stripped log, wood, and stripped wood
        // logs contains logs_that_burn + nether logs
        tag(BlockTags.LOGS_THAT_BURN)
            .add(TFCBlocks.WOODS, Wood.BlockType.LOG)
            .add(TFCBlocks.WOODS, Wood.BlockType.STRIPPED_LOG)
            .add(TFCBlocks.WOODS, Wood.BlockType.WOOD)
            .add(TFCBlocks.WOODS, Wood.BlockType.STRIPPED_WOOD);
        tag(LOGS_THAT_LOG).addTag(BlockTags.LOGS);
        tag(WORKBENCHES).add(TFCBlocks.WOODS, Wood.BlockType.WORKBENCH);
        tag(SUPPORT_BEAMS)
            .add(TFCBlocks.WOODS, Wood.BlockType.HORIZONTAL_SUPPORT)
            .add(TFCBlocks.WOODS, Wood.BlockType.VERTICAL_SUPPORT);

        tag(CHARCOAL_PIT_INSULATION).add(
            TFCBlocks.LOG_PILE,
            TFCBlocks.BURNING_LOG_PILE,
            TFCBlocks.CHARCOAL_PILE);
        tag(CHARCOAL_FORGE_INSULATION).addTag(INSULATION);
        tag(CHARCOAL_FORGE_INVISIBLE).add(TFCBlocks.CRUCIBLE);
        tag(BLOOMERY_INSULATION).addTag(INSULATION);
        tag(BLAST_FURNACE_INSULATION).add(TFCBlocks.FIRE_BRICKS);
        tag(SCRAPING_SURFACE).addTag(BlockTags.LOGS);

        tag(MINEABLE_WITH_PROPICK); // Empty
        tag(MINEABLE_WITH_CHISEL); // Empty
        tag(MINEABLE_WITH_HAMMER).addTag(MINEABLE_WITH_BLUNT_TOOL);
        tag(MINEABLE_WITH_KNIFE).addTag(MINEABLE_WITH_SHARP_TOOL);
        tag(MINEABLE_WITH_SCYTHE).addTag(MINEABLE_WITH_SHARP_TOOL);
        tag(MINEABLE_WITH_GLASS_SAW)
            .addTags(Tags.Blocks.GLASS_BLOCKS, Tags.Blocks.GLASS_PANES)
            .add(TFCBlocks.COLORED_POURED_GLASS)
            .add(TFCBlocks.POURED_GLASS);

        tag(MINEABLE_WITH_BLUNT_TOOL).addTag(BlockTags.LOGS);
        tag(MINEABLE_WITH_SHARP_TOOL).addTag(BlockTags.MINEABLE_WITH_HOE); // Make our "sharp tools" equivalent to hoes

        tag(PROSPECTABLE).addTags(Tags.Blocks.ORES);

        tag(BlockTags.DIRT).addTags(GRASS, DIRT, MUD);
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
        tag(CREEPING_STONE_PLANTABLE_ON).addTags(STONES, STONES_SMOOTH, Tags.Blocks.COBBLESTONES);
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

        BlockTagAppender add(Stream<Block> blocks)
        {
            blocks.forEach(b -> add(key(b)));
            return this;
        }

        BlockTagAppender add(Collection<DecorationBlockHolder> blocks, Function<DecorationBlockHolder, ? extends DeferredHolder<Block, ? extends Block>> type)
        {
            blocks.forEach(b -> add(type.apply(b).get()));
            return this;
        }

        @SafeVarargs
        final <T extends IdHolder<? extends Block>> BlockTagAppender add(T... blocks)
        {
            return add(Arrays.stream(blocks).map(IdHolder::get));
        }

        BlockTagAppender add(Map<?, ? extends IdHolder<? extends Block>> blocks)
        {
            blocks.values().forEach(this::add);
            return this;
        }

        BlockTagAppender addAll(Map<?, ? extends Map<?, ? extends IdHolder<? extends Block>>> blocks)
        {
            blocks.values().forEach(m -> m.values().forEach(this::add));
            return this;
        }

        <T1, T2, V extends IdHolder<? extends Block>> BlockTagAppender add(Map<T1, Map<T2, V>> blocks, T2 key)
        {
            return add(pivot(blocks, key));
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
