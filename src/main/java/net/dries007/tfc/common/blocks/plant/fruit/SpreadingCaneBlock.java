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
import net.minecraftforge.common.Tags;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public class SpreadingCaneBlock extends SpreadingBushBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape CANE_EAST = Block.box(0.0D, 3.0D, 0.0D, 8.0D, 12.0D, 16.0D);
    private static final VoxelShape CANE_WEST = Block.box(8.0D, 3.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    private static final VoxelShape CANE_SOUTH = Block.box(0.0D, 3.0D, 0.0D, 16.0D, 12.0D, 8.0D);
    private static final VoxelShape CANE_NORTH = Block.box(0.0D, 3.0D, 8.0D, 16.0D, 12.0D, 16.0D);

    public SpreadingCaneBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages, Supplier<? extends Block> companion, int maxHeight, int deathChance)
    {
        super(properties, productItem, stages, companion, maxHeight, deathChance);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND) return InteractionResult.FAIL;
        if (state.getValue(STAGE) == 2)
        {
            ItemStack held = player.getItemInHand(hand);
            if (Helpers.isItem(held.getItem(), Tags.Items.SHEARS))
            {
                BerryBushBlockEntity te = Helpers.getBlockEntity(level, pos, BerryBushBlockEntity.class);
                if (te != null)
                {
                    if (state.getValue(LIFECYCLE) == Lifecycle.DORMANT)
                    {
                        te.setGrowing(true);
                        te.resetDeath();
                        held.hurt(1, level.getRandom(), null);
                        Helpers.playSound(level, pos, SoundEvents.SHEEP_SHEAR);
                        level.setBlockAndUpdate(pos, state.setValue(STAGE, 1));
                        return InteractionResult.SUCCESS;
                    }
                    else if (state.getValue(LIFECYCLE) == Lifecycle.FLOWERING)
                    {
                        held.hurt(1, level.getRandom(), null);
                        Helpers.playSound(level, pos, SoundEvents.SHEEP_SHEAR);
                        if (level.getRandom().nextInt(3) != 0)
                            Helpers.spawnItem(level, pos, new ItemStack(companion.get()));
                        level.destroyBlock(pos, true, null);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(FACING))
            {
                case NORTH -> CANE_NORTH;
                case WEST -> CANE_WEST;
                case EAST -> CANE_EAST;
                case SOUTH -> CANE_SOUTH;
                default -> CANE_EAST;
            };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIFECYCLE, STAGE, FACING);
    }

    @Override
    public void cycle(BerryBushBlockEntity te, Level world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {
        if (lifecycle == Lifecycle.HEALTHY)
        {
            if (!te.isGrowing() || te.isRemoved()) return;

            if (stage == 0)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 1));
            }
            else if (stage == 1 && random.nextInt(7) == 0)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 2));
                if (random.nextInt(deathChance) == 0)
                {
                    te.setGrowing(false);
                }
            }
            else if (stage == 2 && random.nextInt(7) == 0)
            {
                if (((SpreadingCaneBlock) state.getBlock()).canSurvive(state, world, pos))
                {
                    world.setBlockAndUpdate(pos, companion.get().defaultBlockState().setValue(STAGE, 1));
                    TickCounterBlockEntity bush = Helpers.getBlockEntity(world, pos, TickCounterBlockEntity.class);
                    if (bush != null)
                    {
                        bush.reduceCounter(-1 * ICalendar.TICKS_IN_DAY * te.getTicksSinceUpdate());
                    }
                }
                else
                {
                    te.setGrowing(false);
                }
            }
        }
        else if (lifecycle == Lifecycle.DORMANT && !te.isGrowing())
        {
            te.addDeath();
            if (te.willDie() && random.nextInt(3) == 0)
            {
                if (!Helpers.isBlock(world.getBlockState(pos.above()), TFCTags.Blocks.SPREADING_BUSH))
                    world.setBlockAndUpdate(pos, TFCBlocks.DEAD_CANE.get().defaultBlockState().setValue(STAGE, stage).setValue(FACING, state.getValue(FACING)));
            }
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
