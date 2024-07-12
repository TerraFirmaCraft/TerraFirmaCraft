/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.PitKilnBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

public class PitKilnBlock extends DeviceBlock
{
    public static final IntegerProperty STAGE = TFCBlockStateProperties.PIT_KILN_STAGE;
    public static final int STRAW_END = 7;
    public static final int LOG_START = 8;
    public static final int LIT = 16;
    public static final VoxelShape[] SHAPE_BY_LAYER = Util.make(new VoxelShape[17], shapes -> {
        for (int i = 0; i < 8; i++)
        {
            shapes[i] = Block.box(0.0D, 0.0D, 0.0D, 16.0D, i + 1, 16.0D);
        }
        for (int i = 0; i < 4; i++)
        {
            shapes[8 + i] = Shapes.or(shapes[7 + i], Block.box(4 * i, 8.0D, 0.0D, 4 * (i + 1), 12.0D, 16.0D));
        }
        for (int i = 0; i < 4; i++)
        {
            shapes[12 + i] = Shapes.or(shapes[11 + i], Block.box(4 * i, 12.0D, 0.0D, 4 * (i + 1), 16.0D, 16.0D));
        }
        shapes[16] = Shapes.block(); // lit stage
    });

    public PitKilnBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(STAGE) == LIT)
        {
            double x = pos.getX() + random.nextFloat();
            double y = pos.getY() + random.nextFloat();
            double z = pos.getZ() + random.nextFloat();
            for (int i = 0; i < random.nextInt(3); i++)
            {
                level.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, 0, 0.1f + random.nextFloat() / 8, 0);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(STAGE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !facingState.isFaceSturdy(level, facingPos, Direction.UP))
        {
            return Blocks.AIR.defaultBlockState();
        }
        if (facing == Direction.UP && facingState.getBlock() == Blocks.FIRE && level.getBlockEntity(currentPos) instanceof PitKilnBlockEntity kiln && !kiln.isLit())
        {
            level.setBlock(facingPos, Blocks.AIR.defaultBlockState(), 3);
            if (kiln.tryLight())
            {
                return state.setValue(STAGE, LIT);
            }
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND)
        {
            if (level.getBlockEntity(pos) instanceof PitKilnBlockEntity kiln)
            {
                ItemStack held = player.getItemInHand(hand);
                Item item = held.getItem();
                int stage = state.getValue(STAGE);
                if (stage < STRAW_END && Helpers.isItem(item, TFCTags.Items.PIT_KILN_STRAW))
                {
                    level.setBlock(pos, state.setValue(STAGE, stage + 1), 10);
                    kiln.addStraw(held.split(1), stage + 1);
                    Helpers.playPlaceSound(level, pos, SoundType.GRASS);
                }
                else if (stage >= STRAW_END && stage < LIT - 1 && Helpers.isItem(item, TFCTags.Items.PIT_KILN_LOGS))
                {
                    level.setBlock(pos, state.setValue(STAGE, stage + 1), 10);
                    kiln.addLog(held.split(1), stage - LOG_START + 1);
                    Helpers.playPlaceSound(level, pos, SoundType.WOOD);
                }
                else if (held.isEmpty())
                {
                    if (stage != LIT)
                    {
                        NonNullList<ItemStack> logItems = kiln.getLogs();
                        NonNullList<ItemStack> strawItems = kiln.getStraws();
                        ItemStack dropStack;
                        if (stage >= LOG_START)
                        {
                            dropStack = logItems.get(stage - LOG_START).copy();
                            kiln.deleteLog(stage - LOG_START);
                        }
                        else
                        {
                            dropStack = strawItems.get(stage).copy();
                            kiln.deleteStraw(stage);
                        }
                        if (!dropStack.isEmpty())
                        {
                            ItemHandlerHelper.giveItemToPlayer(player, dropStack);
                        }
                        if (stage == 0)
                        {
                            PitKilnBlockEntity.convertPitKilnToPlacedItem(level, pos);
                        }
                        else
                        {
                            level.setBlock(pos, state.setValue(STAGE, stage - 1), 10);
                        }
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockState blockstate = level.getBlockState(pos.below());
        return Block.isFaceFull(blockstate.getCollisionShape(level, pos.below()), Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter levle, BlockPos pos, CollisionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(STAGE)];
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult result, BlockGetter level, BlockPos pos, Player player)
    {
        if (result instanceof BlockHitResult blockResult && level.getBlockEntity(pos) instanceof PitKilnBlockEntity placedItem)
        {
            return placedItem.getCloneItemStack(state, blockResult);
        }
        return ItemStack.EMPTY;
    }
}
