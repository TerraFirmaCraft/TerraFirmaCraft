/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.IFluidLoggable;

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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == this.asItem())
        {
            if (!level.isClientSide() && handIn == InteractionHand.MAIN_HAND && state.getBlock() == this)
            {
                int count = state.getValue(COUNT);
                if (count < 3)
                {
                    level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    level.setBlockAndUpdate(pos, state.setValue(COUNT, count + 1));
                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        }
        return super.use(state, level, pos, player, handIn, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(COUNT))
            {
                case 1 -> ONE;
                case 2 -> TWO;
                case 3 -> THREE;
                default -> throw new IllegalStateException("Unknown value for property LooseRockBlock#ROCKS: " + state.getValue(COUNT));
            };
    }
}
