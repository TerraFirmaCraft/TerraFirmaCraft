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
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper.ResourceType;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.IdHolder;
import net.dries007.tfc.util.registry.RegistryHolder;

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
        tag(BlockTags.REPLACEABLE).add(BuiltInRegistries.BLOCK
            .entrySet()
            .stream()
            .filter(e -> e.getValue().defaultBlockState().canBeReplaced() && e.getKey().location().getNamespace().equals(TerraFirmaCraft.MOD_ID))
            .map(Map.Entry::getValue));

        pivot(TFCBlocks.METALS, Metal.BlockType.BLOCK).forEach((metal, block) -> tag(storageBlockTagOf(Registries.BLOCK, metal)).add(block));
        tag(TFCTags.Blocks.LAMPS).add(pivot(TFCBlocks.METALS, Metal.BlockType.LAMP));
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
    static class BlockTagAppender extends TagAppender<Block>
    {
        BlockTagAppender(TagBuilder builder, String modId)
        {
            super(builder, modId);
        }

        BlockTagAppender add(Block... blocks) { return add(Arrays.stream(blocks)); }
        BlockTagAppender add(TFCBlocks.Id<?>... blocks) { return add(Arrays.stream(blocks).map(RegistryHolder::get)); }
        BlockTagAppender add(Stream<Block> blocks) { blocks.forEach(item -> add(key(item))); return this; }
        BlockTagAppender add(Map<?, ? extends IdHolder<? extends Block>> blocks) { return add(blocks.values().stream().map(IdHolder::get)); }

        private ResourceKey<Block> key(Block block)
        {
            return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
        }
    }
}
