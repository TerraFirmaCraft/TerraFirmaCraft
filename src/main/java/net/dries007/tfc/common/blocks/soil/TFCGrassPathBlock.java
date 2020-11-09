/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassPathBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.blocks.TFCBlocks;


public class TFCGrassPathBlock extends GrassPathBlock implements ISoilBlock
{
    private final Supplier<Block> dirtBlock;

    public TFCGrassPathBlock(Properties builder, SoilBlockType soil, SoilBlockType.Variant variant)
    {
        this(builder, TFCBlocks.SOIL.get(soil).get(variant));
    }

    protected TFCGrassPathBlock(Properties builder, Supplier<Block> dirtBlock)
    {
        super(builder);

        this.dirtBlock = dirtBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();
        if (!state.canSurvive(context.getLevel(), context.getClickedPos()))
        {
            return Block.pushEntitiesUp(state, getDirt(), context.getLevel(), context.getClickedPos());
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        worldIn.setBlockAndUpdate(pos, Block.pushEntitiesUp(state, getDirt(), worldIn, pos));
    }

    @Override
    public BlockState getDirt()
    {
        return dirtBlock.get().defaultBlockState();
    }
}