/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BurningLogPileBlockEntity;
import net.dries007.tfc.common.blockentities.LogPileBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;

public class BurningLogPileBlock extends Block implements IForgeBlockExtension, EntityBlockExtension
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_16;
    public static void lightLogPile(Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof LogPileBlockEntity pile)
        {
            final int logs = pile.logCount();
            BlockState state = level.getBlockState(pos);
            pile.clearContent();
            level.setBlockAndUpdate(pos, Helpers.copyProperties(TFCBlocks.BURNING_LOG_PILE.get().defaultBlockState(), state));
            Helpers.playSound(level, pos, SoundEvents.BLAZE_SHOOT);
            if (level.getBlockEntity(pos) instanceof BurningLogPileBlockEntity burningPile)
            {
                burningPile.light(logs);
                tryLightNearby(level, pos);
            }
        }
    }

    public static void tryLightLogPile(Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof LogPileBlockEntity)
        {
            level.scheduleTick(pos, level.getBlockState(pos).getBlock(), TICK_DELAY);
        }
    }

    protected VoxelShape getShapeByDirByCount(Direction.Axis axis, int count)
    {
        count--;
        if (axis == Direction.Axis.X)
        {
            return LogPileBlock.SHAPES_BY_DIR_BY_COUNT[0][count];
        }
        return LogPileBlock.SHAPES_BY_DIR_BY_COUNT[1][count];
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter levle, BlockPos pos, CollisionContext context)
    {
        return getShapeByDirByCount(state.getValue(AXIS), state.getValue(COUNT));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return getShapeByDirByCount(state.getValue(AXIS), state.getValue(COUNT));
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return getShapeByDirByCount(state.getValue(AXIS), state.getValue(COUNT));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS).add(COUNT));
    }

    private static boolean isValidCoverBlock(BlockState offsetState, Level level, BlockPos pos, Direction side)
    {
        if (Helpers.isBlock(offsetState, TFCTags.Blocks.CHARCOAL_PIT_INSULATION))// log pile, charcoal pile, this
        {
            return true;
        }
        return !offsetState.isFlammable(level, pos, side) && offsetState.isFaceSturdy(level, pos, side);
    }

    private static void tryLightNearby(Level level, BlockPos pos)
    {
        if (level.isClientSide()) return;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (Direction side : Helpers.DIRECTIONS)
        {
            cursor.setWithOffset(pos, side);
            final BlockState offsetState = level.getBlockState(cursor);
            if (isValidCoverBlock(offsetState, level, cursor, side.getOpposite()))
            {
                if (Helpers.isBlock(offsetState, TFCBlocks.LOG_PILE.get()))
                {
                    tryLightLogPile(level, cursor);
                }
            }
            else if (offsetState.isAir())
            {
                // If we can, try and spawn fire in the offset position - but don't delete anything in the process
                level.setBlockAndUpdate(cursor, Blocks.FIRE.defaultBlockState());
            }
            else if (level.random.nextInt(7) == 0)
            {
                // If we can't spawn fire directly above, but we don't have a valid cover, then this block is invalid, but it can't spawn fire and let it burn itself away
                // So, we have a low chance of replacing this block, with fire.
                level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                return;
            }
        }
    }

    private static final int TICK_DELAY = 30;

    private final ExtendedProperties properties;

    public BurningLogPileBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
        registerDefaultState(getStateDefinition().any().setValue(AXIS, Direction.Axis.X).setValue(COUNT, 1));
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        tryLightNearby(level, pos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand)
    {
        if (level.getBlockState(pos.above(2)).canBeReplaced())
        {
            double x = pos.getX() + rand.nextFloat();
            double y = pos.getY() + 1.125;
            double z = pos.getZ() + rand.nextFloat();
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0f, 0.1f + 0.1f * rand.nextFloat(), 0f);
            if (rand.nextInt(12) == 0)
            {
                level.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
            }
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, (0.5F - rand.nextFloat()) / 10, 0.1f + rand.nextFloat() / 8, (0.5F - rand.nextFloat()) / 10);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }
}
