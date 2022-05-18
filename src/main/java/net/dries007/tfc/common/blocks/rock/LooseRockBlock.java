/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.IFluidLoggable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class LooseRockBlock extends GroundcoverBlock implements IFluidLoggable
{
    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_3;

    private static final VoxelShape ONE = box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    private static final VoxelShape TWO = box(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    private static final VoxelShape THREE = box(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D);

    public LooseRockBlock(Properties properties)
    {
        super(ExtendedProperties.of(properties), Shapes.empty(), null);

        registerDefaultState(defaultBlockState().setValue(COUNT, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(COUNT));
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == this.asItem())
        {
            if (!worldIn.isClientSide() && handIn == InteractionHand.MAIN_HAND && state.getBlock() == this)
            {
                int count = state.getValue(COUNT);
                if (count < 3)
                {
                    worldIn.setBlockAndUpdate(pos, state.setValue(COUNT, count + 1));
                    stack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        switch (state.getValue(COUNT))
        {
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
        }
        throw new IllegalStateException("Unknown value for property LooseRockBlock#ROCKS: " + state.getValue(COUNT));
    }
}
