/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.registry.RegistrySoilVariant;

public class PathBlock extends DirtPathBlock implements ISoilBlock
{
    private final Supplier<? extends Block> dirt;

    public PathBlock(Properties builder, Supplier<? extends Block> dirt)
    {
        super(builder);
        this.dirt = dirt;
    }

    PathBlock(Properties properties, SoilBlockType soil, RegistrySoilVariant variant)
    {
        this(properties, variant.getBlock(soil));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();
        if (!state.canSurvive(context.getLevel(), context.getClickedPos()))
        {
            return Block.pushEntitiesUp(state, getDirt(), context.getLevel(), context.getClickedPos());
        }
        return super.getStateForPlacement(context);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        level.setBlockAndUpdate(pos, Block.pushEntitiesUp(state, getDirt(), level, pos));
    }

    @Override
    public BlockState getDirt()
    {
        return dirt.get().defaultBlockState();
    }
}