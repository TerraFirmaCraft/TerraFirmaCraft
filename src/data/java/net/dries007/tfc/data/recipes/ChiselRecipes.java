package net.dries007.tfc.data.recipes;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.registry.HolderHolder;

public interface ChiselRecipes extends Recipes
{
    default void chiselRecipes()
    {
        TFCBlocks.ROCK_BLOCKS.forEach((type, blocks) -> {
            stairSlab(
                List.of(blocks.get(Rock.BlockType.RAW), blocks.get(Rock.BlockType.HARDENED)),
                TFCBlocks.ROCK_DECORATIONS.get(type).get(Rock.BlockType.RAW));
            chisel(
                List.of(blocks.get(Rock.BlockType.RAW), blocks.get(Rock.BlockType.HARDENED)),
                blocks.get(Rock.BlockType.SMOOTH));
            chisel(
                blocks.get(Rock.BlockType.BRICKS),
                blocks.get(Rock.BlockType.CHISELED));
        });

        TFCBlocks.SANDSTONE.forEach((color, blocks) -> {
            blocks.forEach((type, block) -> stairSlab(block, TFCBlocks.SANDSTONE_DECORATIONS.get(color).get(type)));
            chisel(blocks.get(SandstoneBlockType.RAW), blocks.get(SandstoneBlockType.SMOOTH));
            chisel(blocks.get(SandstoneBlockType.SMOOTH), blocks.get(SandstoneBlockType.CUT));
        });

        chisel(TFCBlocks.PLAIN_ALABASTER, TFCBlocks.PLAIN_POLISHED_ALABASTER);

        TFCBlocks.ALABASTER_BRICKS.forEach((color, block) ->
            stairSlab(block, TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color)));
        TFCBlocks.POLISHED_ALABASTER.forEach((color, block) ->
            stairSlab(block, TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color)));

        TFCBlocks.MUD_BRICK_DECORATIONS.forEach((soil, blocks) ->
            stairSlab(TFCBlocks.SOIL.get(SoilBlockType.MUD_BRICKS).get(soil), blocks));

        TFCBlocks.WOODS.forEach((wood, blocks) ->
            stairSlab(blocks.get(Wood.BlockType.PLANKS), blocks.get(Wood.BlockType.STAIRS), blocks.get(Wood.BlockType.SLAB)));
    }

    private void stairSlab(Supplier<? extends Block> input, DecorationBlockHolder output)
    {
        stairSlab(List.of(input), output);
    }

    private void stairSlab(List<? extends Supplier<? extends Block>> input, DecorationBlockHolder output)
    {
        chisel(input, output.stair(), ChiselRecipe.Mode.STAIR, ItemStackProvider.empty());
        chisel(input, output.slab(), ChiselRecipe.Mode.SLAB, ItemStackProvider.of(output.slab().get()));
    }

    private void stairSlab(Supplier<? extends Block> input, Supplier<? extends Block> stair, Supplier<? extends Block> slab)
    {
        chisel(List.of(input), stair, ChiselRecipe.Mode.STAIR, ItemStackProvider.empty());
        chisel(List.of(input), slab, ChiselRecipe.Mode.SLAB, ItemStackProvider.of(slab.get()));
    }

    private void chisel(Supplier<? extends Block> input, Supplier<? extends Block> output)
    {
        chisel(List.of(input), output);
    }

    private void chisel(List<? extends Supplier<? extends Block>> input, Supplier<? extends Block> output)
    {
        chisel(input, output, ChiselRecipe.Mode.SMOOTH, ItemStackProvider.empty());
    }

    private void chisel(List<? extends Supplier<? extends Block>> input, Supplier<? extends Block> output, ChiselRecipe.Mode mode, ItemStackProvider outputItem)
    {
        add(new ChiselRecipe(
            BlockIngredient.of(input.stream().map(Supplier::get)),
            output.get().defaultBlockState(),
            mode,
            outputItem
        ));
    }
}
