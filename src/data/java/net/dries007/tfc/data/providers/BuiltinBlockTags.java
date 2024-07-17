package net.dries007.tfc.data.providers;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Metal;

public class BuiltinBlockTags extends BlockTagsProvider
{
    public BuiltinBlockTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(event.getGenerator().getPackOutput(), lookup, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(BlockTags.REPLACEABLE, BuiltInRegistries.BLOCK
            .entrySet()
            .stream()
            .filter(e -> e.getValue().defaultBlockState().canBeReplaced() && e.getKey().location().getNamespace().equals(TerraFirmaCraft.MOD_ID))
            .map(Map.Entry::getValue));

        tag(TFCTags.Blocks.LAMPS, TFCBlocks.METALS.values().stream().map(m -> m.get(Metal.BlockType.LAMP).get()));
    }

    private void tag(TagKey<Block> tag, Stream<Block> blocks)
    {
        blocks.forEach(tag(tag)::add);
    }
}
