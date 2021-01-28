/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;

public class TFCFarmlandBlock extends FarmlandBlock implements ISoilBlock, IForgeBlockProperties
{
    public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;

    public static void turnToDirt(BlockState state, World worldIn, BlockPos pos)
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
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        final BlockState defaultState = defaultBlockState();
        return defaultState.canSurvive(context.getLevel(), context.getClickedPos()) ? defaultState : getDirt();
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!state.canSurvive(worldIn, pos))
        {
            // Turn to TFC farmland dirt
            turnToDirt(state, worldIn, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        // No-op
        // todo: trigger TE updates for moisture?
    }

    @Override
    public void fallOn(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
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
