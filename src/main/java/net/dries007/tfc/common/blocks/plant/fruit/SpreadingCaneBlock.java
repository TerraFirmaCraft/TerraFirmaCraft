/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateRange;

public class SpreadingCaneBlock extends SpreadingBushBlock implements IBushBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape CANE_EAST = Block.box(0.0D, 3.0D, 0.0D, 8.0D, 12.0D, 16.0D);
    private static final VoxelShape CANE_WEST = Block.box(8.0D, 3.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    private static final VoxelShape CANE_SOUTH = Block.box(0.0D, 3.0D, 0.0D, 16.0D, 12.0D, 8.0D);
    private static final VoxelShape CANE_NORTH = Block.box(0.0D, 3.0D, 8.0D, 16.0D, 12.0D, 16.0D);

    public SpreadingCaneBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages, Supplier<? extends Block> companion, int maxHeight, Supplier<ClimateRange> climateRange)
    {
        super(properties, productItem, stages, companion, maxHeight, climateRange);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (state.getValue(LIFECYCLE) == Lifecycle.FLOWERING)
        {
            ItemStack held = player.getItemInHand(hand);
            if (Helpers.isItem(held, TFCTags.Items.BUSH_CUTTING_TOOLS))
            {
                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 0.5f, 1.0f);
                if (!level.isClientSide())
                {
                    final int finalStage = state.getValue(STAGE) - 1 - level.getRandom().nextInt(2);
                    if (finalStage >= 0)
                    {
                        // We didn't kill the bush, but we have cut the flowers off
                        level.setBlock(pos, state.setValue(STAGE, finalStage).setValue(LIFECYCLE, Lifecycle.HEALTHY), 3);
                    }
                    else
                    {
                        // Oops
                        level.destroyBlock(pos, false, player);
                    }
                }

                held.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));

                // But, if we were successful, we have obtained a clipping (2 / 3 chance)
                if (level.getRandom().nextInt(3) != 0)
                {
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(this));
                }
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(FACING))
            {
                case NORTH -> CANE_NORTH;
                case WEST -> CANE_WEST;
                case SOUTH -> CANE_SOUTH;
                default -> CANE_EAST;
            };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    protected BlockState getDeadState(BlockState state)
    {
        return TFCBlocks.DEAD_CANE.get().defaultBlockState().setValue(STAGE, state.getValue(STAGE)).setValue(FACING, state.getValue(FACING));
    }

    @Override
    protected void propagate(Level level, BlockPos pos, Random random, BlockState state)
    {
        final int stage = state.getValue(STAGE);
        if (stage == 2)
        {
            level.setBlockAndUpdate(pos, companion.get().defaultBlockState().setValue(STAGE, stage));
            level.getBlockEntity(pos, TFCBlockEntities.BERRY_BUSH.get()).ifPresent(bush -> bush.reduceCounter(-1 * ICalendar.TICKS_IN_DAY * bush.getTicksSinceUpdate()));
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return true;
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return Helpers.isBlock(level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())), TFCTags.Blocks.ANY_SPREADING_BUSH);
    }
}
