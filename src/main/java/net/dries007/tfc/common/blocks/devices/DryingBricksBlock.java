/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class DryingBricksBlock extends BottomSupportedDeviceBlock
{
    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_4;
    public static final BooleanProperty DRIED = TFCBlockStateProperties.DRIED;

    public static final VoxelShape SHAPE_1 = box(2, 0, 1, 6, 3, 7);
    public static final VoxelShape SHAPE_2 = Shapes.or(SHAPE_1, box(10, 0, 1, 14, 3, 7));
    public static final VoxelShape SHAPE_3 = Shapes.or(SHAPE_2, box(2, 0, 9, 6, 3, 15));
    public static final VoxelShape SHAPE_4 = Shapes.or(SHAPE_3, box(10, 0, 9, 14, 3, 15));

    private final Supplier<? extends Item> dryItem;

    public DryingBricksBlock(ExtendedProperties properties, Supplier<? extends Item> dryItem)
    {
        super(properties, InventoryRemoveBehavior.NOOP, SHAPE_1);
        this.dryItem = dryItem;
        registerDefaultState(getStateDefinition().any().setValue(COUNT, 1).setValue(DRIED, false));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (Helpers.isItem(stack, asItem()) && !player.isShiftKeyDown() && !state.getValue(DRIED))
        {
            final int count = state.getValue(COUNT);
            if (count < 4)
            {
                level.setBlockAndUpdate(pos, state.setValue(COUNT, count + 1));
                playSound(state, level, pos, player);
                TickCounterBlockEntity.reset(level, pos);
                if (!player.isCreative())
                {
                    stack.shrink(1);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        else if (stack.isEmpty() && player.isShiftKeyDown())
        {
            int count = state.getValue(COUNT);
            ItemStack drop = new ItemStack(state.getValue(DRIED) ? dryItem.get() : asItem());
            ItemHandlerHelper.giveItemToPlayer(player, drop);
            if (count > 1)
            {
                level.setBlockAndUpdate(pos, state.setValue(COUNT, count - 1));
            }
            else
            {
                level.setBlockAndUpdate(pos, state.getFluidState().createLegacyBlock());
            }
            playSound(state, level, pos, player);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = level.getBlockState(pos);
        if (!state.getFluidState().isEmpty())
        {
            return null;
        }
        if (state.getBlock() == this && !state.getValue(DRIED) && state.getValue(COUNT) < 4)
        {
            return state.setValue(COUNT, state.getValue(COUNT) + 1);
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return state.getValue(DRIED) ? dryItem.get().getDefaultInstance() : super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context)
    {
        return !context.isSecondaryUseActive() && Helpers.isItem(context.getItemInHand(), this.asItem()) && state.getValue(COUNT) < 4 && !state.getValue(DRIED) || super.canBeReplaced(state, context);
    }

    private void playSound(BlockState state, Level level, BlockPos pos, @Nullable Entity entity)
    {
        final SoundType soundType = getSoundType(state, level, pos, entity);
        level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1f) / 2f, soundType.getPitch() * 0.8f);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TickCounterBlockEntity.reset(level, pos);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch(state.getValue(COUNT))
            {
                case 2 -> SHAPE_2;
                case 3 -> SHAPE_3;
                case 4 -> SHAPE_4;
                default -> SHAPE_1;
            };
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return !state.getValue(DRIED);
    }

    /**
     * Random tick falls through to here
     */
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (state.getValue(DRIED)) return;
        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(counter -> {
            if (level.isRainingAt(pos.above()))
            {
                counter.resetCounter();
            }
            else
            {
                final int ticks = TFCConfig.SERVER.mudBricksTicks.get();
                if (ticks > -1 && counter.getTicksSinceUpdate() > ticks)
                {
                    level.setBlockAndUpdate(pos, state.setValue(DRIED, true));

                    final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
                    for (Direction d : Direction.Plane.HORIZONTAL)
                    {
                        cursor.setWithOffset(pos, d);
                        final BlockState stateAt = level.getBlockState(cursor);
                        if (stateAt.getBlock() instanceof DryingBricksBlock)
                        {
                            level.scheduleTick(cursor, stateAt.getBlock(), 1);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(COUNT, DRIED));
    }

}
