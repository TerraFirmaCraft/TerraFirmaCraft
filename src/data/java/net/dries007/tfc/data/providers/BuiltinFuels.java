package net.dries007.tfc.data.providers;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.data.Fuel;

public class BuiltinFuels extends DataManagerProvider<Fuel> implements Accessors
{
    public BuiltinFuels(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(Fuel.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(Wood.ACACIA, 650, 1000, 0.95f);
        add(Wood.ASH, 696, 1250, 0.95f);
        add(Wood.ASPEN, 611, 1000, 0.95f);
        add(Wood.BIRCH, 652, 1750, 0.95f);
        add(Wood.BLACKWOOD, 720, 1750, 0.95f);
        add(Wood.CHESTNUT, 651, 1500, 0.95f);
        add(Wood.DOUGLAS_FIR, 707, 1500, 1f);
        add(Wood.HICKORY, 762, 2000, 0.8f);
        add(Wood.KAPOK, 645, 1000, 0.95f);
        add(Wood.MANGROVE, 655, 1000, 0.95f);
        add(Wood.MAPLE, 745, 2000, 0.95f);
        add(Wood.OAK, 728, 2250, 1f);
        add(Wood.PALM, 730, 1250, 0.95f);
        add(Wood.PINE, 627, 1250, 0.6f);
        add(Wood.ROSEWOOD, 640, 1500, 0.95f);
        add(Wood.SEQUOIA, 612, 1750, 0.7f);
        add(Wood.SPRUCE, 608, 1500, 0.6f);
        add(Wood.SYCAMORE, 653, 1750, 0.95f);
        add(Wood.WHITE_CEDAR, 625, 1500, 0.95f);
        add(Wood.WILLOW, 603, 1000, 0.95f);

        add("coal", Ingredient.of(Items.COAL, TFCItems.ORES.get(Ore.BITUMINOUS_COAL)), 2000, 1415, 1f);
        add(TFCItems.ORES.get(Ore.LIGNITE), 2200, 1350, 1f);
        add(Items.CHARCOAL, 1800, 1350, 1f);
        add(TFCBlocks.PEAT, 2500, 600, 1f);
        add(TFCItems.STICK_BUNDLE, 600, 900, 0.8f);
        add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.DRIFTWOOD), 400, 650, 0.4f);
        add(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.PINECONE), 220, 150, 0.15f);
        add("paper", Ingredient.of(Items.PAPER, Items.BOOK, Items.WRITTEN_BOOK, Items.KNOWLEDGE_BOOK, Items.WRITABLE_BOOK, Items.ENCHANTED_BOOK), 150, 199, 0.7f);
        add("leaves", Ingredient.of(ItemTags.LEAVES), 600, 100, 0.25f);
    }

    private void add(Wood wood, int duration, float temperature, float purity)
    {
        final Map<Wood.BlockType, TFCBlocks.Id<Block>> blocks = TFCBlocks.WOODS.get(wood);
        add(wood.getSerializedName() + "_logs", Ingredient.of(
            blocks.get(Wood.BlockType.LOG),
            blocks.get(Wood.BlockType.STRIPPED_LOG),
            blocks.get(Wood.BlockType.WOOD),
            blocks.get(Wood.BlockType.STRIPPED_WOOD)
        ), duration, temperature, purity);
        add(wood.getSerializedName() + "_planks", Ingredient.of(blocks.get(Wood.BlockType.PLANKS)), (int) (duration * 0.4f), temperature + 50f, purity);
    }

    private void add(ItemLike item, int duration, float temperature, float purity)
    {
        add(nameOf(item), new Fuel(Ingredient.of(item), duration, temperature, purity));
    }

    private void add(String name, Ingredient item, int duration, float temperature, float purity)
    {
        add(name, new Fuel(item, duration, temperature, purity));
    }
}
