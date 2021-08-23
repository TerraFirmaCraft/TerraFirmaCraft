/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlocks;

public class TFCFarmlandBlock extends FarmBlock implements ISoilBlock, IForgeBlockExtension, EntityBlockExtension
{
    public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;

    public static void turnToDirt(BlockState state, Level worldIn, BlockPos pos)
    {
        worldIn.setBlockAndUpdate(pos, pushEntitiesUp(state, ((TFCFarmlandBlock) state.getBlock()).getDirt(), worldIn, pos));
    }

    private final ForgeBlockProperties properties;
    private final Supplier<? extends Block> dirt;

    public TFCFarmlandBlock(ForgeBlockProperties properties, SoilBlockType.Variant variant)
    {
        this(properties, TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant));
    }

    public TFCFarmlandBlock(ForgeBlockProperties properties, Supplier<? extends Block> dirt)
    {
        super(properties.properties());

        this.properties = properties;
        this.dirt = dirt;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final BlockState defaultState = defaultBlockState();
        return defaultState.canSurvive(context.getLevel(), context.getClickedPos()) ? defaultState : getDirt();
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand)
    {
        if (!state.canSurvive(worldIn, pos))
        {
            // Turn to TFC farmland dirt
            turnToDirt(state, worldIn, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random)
    {
        // No-op
        // todo: trigger TE updates for moisture?
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance)
    {
        // No-op
    }

    @Override
    public BlockState getDirt()
    {
        return dirt.get().defaultBlockState();
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }
}
