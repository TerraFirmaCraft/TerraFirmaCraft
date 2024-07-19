package net.dries007.tfc.data.providers;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper.ResourceType;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.IdHolder;
import net.dries007.tfc.util.registry.RegistryHolder;

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
        // ===== Minecraft Tags ===== //
        tag(BlockTags.REPLACEABLE).add(BuiltInRegistries.BLOCK
            .entrySet()
            .stream()
            .filter(e -> e.getValue().defaultBlockState().canBeReplaced() && e.getKey().location().getNamespace().equals(TerraFirmaCraft.MOD_ID))
            .sorted(Map.Entry.comparingByKey()) // Determinism
            .map(Map.Entry::getValue));

        // ===== Common Tags ===== //
        pivot(TFCBlocks.METALS, Metal.BlockType.BLOCK).forEach((metal, block) -> tag(storageBlockTagOf(Registries.BLOCK, metal)).add(block));

        // ===== TFC Tags ===== //
        tag(LAMPS).add(pivot(TFCBlocks.METALS, Metal.BlockType.LAMP));
        tag(CAN_TRIGGER_COLLAPSE).addTags(Tags.Blocks.ORES, Tags.Blocks.STONES);
        tag(CAN_START_COLLAPSE).addTags(Tags.Blocks.ORES, TFCTags.Blocks.STONES_RAW);
        tag(CAN_COLLAPSE).addTags(Tags.Blocks.ORES, Tags.Blocks.STONES, STONES_SMOOTH, STONES_SPIKE);

        tag(STONES).addTags(STONES_RAW, STONES_HARDENED);
        tag(STONES_RAW).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.RAW);
        tag(STONES_HARDENED).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.HARDENED);
        tag(STONES_SMOOTH).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.SMOOTH);
        tag(STONES_SPIKE).add(TFCBlocks.ROCK_BLOCKS, Rock.BlockType.SPIKE);
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

        BlockTagAppender add(Block... blocks) { return add(Arrays.stream(blocks)); }
        BlockTagAppender add(TFCBlocks.Id<?>... blocks) { return add(Arrays.stream(blocks).map(RegistryHolder::get)); }
        BlockTagAppender add(Stream<Block> blocks) { blocks.forEach(item -> add(key(item))); return this; }
        BlockTagAppender add(Map<?, ? extends IdHolder<? extends Block>> blocks) { return add(blocks.values().stream().map(IdHolder::get)); }
        <T1, T2, V extends IdHolder<? extends Block>> BlockTagAppender add(Map<T1, Map<T2, V>> blocks, T2 key) { return add(pivot(blocks, key)); }

        @Override @SafeVarargs public final BlockTagAppender addTags(TagKey<Block>... values) { return (BlockTagAppender) super.addTags(values); }

        private ResourceKey<Block> key(Block block)
        {
            return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
        }
    }
}
