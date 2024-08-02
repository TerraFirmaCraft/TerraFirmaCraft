package net.dries007.tfc.test.block;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.test.TestSetup;
import net.dries007.tfc.util.Helpers;

import static org.junit.jupiter.api.Assertions.*;

public class BlocksTest implements TestSetup
{
    @Test
    public void testBlockEntityImplementations()
    {
        for (DeferredHolder<Block, ? extends Block> holder : TFCBlocks.BLOCKS.getEntries())
        {
            final Block block = holder.value();
            if (block instanceof IForgeBlockExtension ex && ex.getExtendedProperties().hasBlockEntity())
            {
                assertInstanceOf(EntityBlockExtension.class, block, "Block " + holder.getId() + " is missing " + EntityBlockExtension.class.getSimpleName());
            }
            if (block instanceof EntityBlockExtension b)
            {
                assertTrue(b.getExtendedProperties().hasBlockEntity(), "Block " + holder.getId() + " is missing a block entity");
            }
            if (block instanceof EntityBlock)
            {
                // Blocks with `EntityBlock` applied via vanilla must (1) implement our extension, and (2) remember to override
                // the factory method, so it goes through the extension, not the vanilla superclass. Thus, we must find the method
                // overriden in a non-vanilla class
                final @Nullable Method method = findMethodInAnyParent(block.getClass());

                assertInstanceOf(EntityBlockExtension.class, block);
                assertNotNull(method);
                assertFalse(method.getDeclaringClass().getSimpleName().startsWith("net.minecraft"), "Block " + holder.getId() + " declared newBlockEntity() in " + method.getDeclaringClass());
            }
        }
    }

    @Nullable
    private Method findMethodInAnyParent(Class<?> clazz)
    {
        final @Nullable Method method = findMethod(clazz);
        return method != null
            ? method
            : clazz.getSuperclass() != null
                ? findMethodInAnyParent(clazz.getSuperclass())
                : null;
    }

    @Nullable
    private Method findMethod(Class<?> clazz)
    {
        try { return clazz.getMethod("newBlockEntity", BlockPos.class, BlockState.class); }
        catch (NoSuchMethodException e) { return null; }
    }

    @Test
    public void testBlocksAllHaveMineableTags()
    {
        final Set<Block> expectedNotMineableBlocks = Stream.of(
            List.of(
                TFCBlocks.PLACED_ITEM,
                TFCBlocks.PIT_KILN,
                TFCBlocks.SCRAPING,
                TFCBlocks.CANDLE,
                TFCBlocks.CANDLE_CAKE,
                TFCBlocks.CAKE,
                TFCBlocks.HOT_POURED_GLASS,
                TFCBlocks.GLASS_BASIN,
                TFCBlocks.POURED_GLASS
            ),
            TFCBlocks.DYED_CANDLE.values(),
            TFCBlocks.DYED_CANDLE_CAKES.values(),
            TFCBlocks.COLORED_POURED_GLASS.values()
        ).flatMap(Collection::stream).map(Supplier::get).collect(Collectors.toSet());

        final List<TagKey<Block>> mineableTags = List.of(
            BlockTags.MINEABLE_WITH_AXE,
            BlockTags.MINEABLE_WITH_HOE,
            BlockTags.MINEABLE_WITH_PICKAXE,
            BlockTags.MINEABLE_WITH_SHOVEL
        );

        // All non-fluid, non-exceptional, blocks with hardness > 0, < infinity, should define a tool
        final var blocks = TFCBlocks.BLOCKS.getEntries()
            .stream()
            .filter(holder -> {
                final Block block = holder.value();
                return !(block instanceof LiquidBlock)
                    && block.defaultDestroyTime() > 0
                    && !expectedNotMineableBlocks.contains(block)
                    && mineableTags.stream().noneMatch(t -> Helpers.isBlock(block, t));
            })
            .map(holder -> holder.getId().toString())
            .toList();

        assertTrue(blocks.isEmpty(), "Missing mineable tags on blocks: " + String.join("\n", blocks));
    }
}
