/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Collections;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.ISlowEntities;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;

public class TFCLeavesBlock extends Block implements ILeavesBlock, IForgeBlockExtension, IFluidLoggable, ISlowEntities
{
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER;


    public static void doParticles(ServerLevel level, double x, double y, double z, int count)
    {
        level.sendParticles(TFCParticles.LEAF.get(), x, y, z, count, 0, 0, 0, 0.3f);
    }

    public static void onEntityInside(Level level, Entity entity)
    {
        if (Helpers.isEntity(entity, TFCTags.Entities.DESTROYED_BY_LEAVES))
        {
            entity.kill();
        }
        if (level.random.nextInt(20) == 0 && level instanceof ServerLevel server && Helpers.hasMoved(entity))
        {
            doParticles(server, entity.getX(), entity.getEyeY() - 0.25D, entity.getZ(), 3);
        }
    }

    public static void dripRainwater(Level level, BlockPos pos, RandomSource random)
    {
        if (level.isRainingAt(pos.above()))
        {
            if (random.nextInt(15) == 1)
            {
                final BlockPos belowPos = pos.below();
                final BlockState belowState = level.getBlockState(belowPos);
                if (!belowState.canOcclude() || !belowState.isFaceSturdy(level, belowPos, Direction.UP))
                {
                    ParticleUtils.spawnParticleBelow(level, pos, random, ParticleTypes.DRIPPING_WATER);
                }
            }
        }
    }

    /* The maximum value of the decay property. */
    private final int maxDecayDistance;
    private final ExtendedProperties properties;
    private final int autumnIndex;
    @Nullable private final Supplier<? extends Block> fallenLeaves;
    @Nullable private final Supplier<? extends Block> fallenTwig;

    public TFCLeavesBlock(ExtendedProperties properties, int autumnIndex, @Nullable Supplier<? extends Block> fallenLeaves, @Nullable Supplier<? extends Block> fallenTwig)
    {
        super(properties.properties());

        this.maxDecayDistance = Collections.max(getDistanceProperty().getPossibleValues());
        this.properties = properties;
        this.fallenLeaves = fallenLeaves;
        this.fallenTwig = fallenTwig;
        this.autumnIndex = autumnIndex;

        // Distance is dependent on tree species
        registerDefaultState(stateDefinition.any().setValue(getDistanceProperty(), 1).setValue(PERSISTENT, false));
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        final int distance = getDistance(facingState) + 1;
        if (distance != 1 || state.getValue(getDistanceProperty()) != distance)
        {
            level.scheduleTick(currentPos, this, 1);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos)
    {
        return 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return 0.2F;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (!state.getValue(PERSISTENT) && random.nextInt(30) == 0)
        {
            if (Calendars.CLIENT.getCalendarMonthOfYear().getSeason() == Season.FALL || ClimateRenderCache.INSTANCE.getWind().lengthSquared() > 0.42f * 0.42f)
            {
                final BlockState belowState = level.getBlockState(pos.below());
                if (belowState.isAir())
                {
                    final BlockState aboveState = level.getBlockState(pos.above());
                    ParticleOptions particle;
                    if (Helpers.isBlock(aboveState, BlockTags.SNOW) && random.nextBoolean())
                    {
                        particle = TFCParticles.SNOWFLAKE.get();
                    }
                    else
                    {
                        particle = new BlockParticleOption(TFCParticles.FALLING_LEAF.get(), state);
                    }
                    ParticleUtils.spawnParticleBelow(level, pos, random, particle);

                }
            }
        }
        dripRainwater(level, pos, random);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        super.randomTick(state, level, pos, rand); // super calls tick()
        if (state.getValue(getDistanceProperty()) > maxDecayDistance && !state.getValue(PERSISTENT))
        {
            level.removeBlock(pos, false);
            if (rand.nextFloat() < 0.01f) createDestructionEffects(state, level, pos, rand, false);
            doParticles(level, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat(), 1);
        }
        else if (rand.nextFloat() < 0.0005f && Calendars.SERVER.getCalendarMonthOfYear().getSeason() == Season.FALL && !state.getValue(PERSISTENT))
        {
            createDestructionEffects(state, level, pos, rand, true);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        final int oldDistance = state.getValue(getDistanceProperty());
        int distance = updateDistance(level, pos);
        if (distance > maxDecayDistance)
        {
            if (!state.getValue(PERSISTENT))
            {
                if (!TFCConfig.SERVER.enableLeavesDecaySlowly.get())
                {
                    level.removeBlock(pos, false);
                    if (rand.nextFloat() < 0.01f) createDestructionEffects(state, level, pos, rand, false);
                    doParticles(level, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat(), 1);
                }
                else
                {
                    // max + 1 means it must decay next random tick
                    level.setBlockAndUpdate(pos, state.setValue(getDistanceProperty(), maxDecayDistance + 1));
                }
            }
            else
            {
                level.setBlock(pos, state.setValue(getDistanceProperty(), maxDecayDistance), 3);
            }
        }
        else if (distance != oldDistance)
        {
            level.setBlock(pos, state.setValue(getDistanceProperty(), distance), 3);
        }
    }

    public void createDestructionEffects(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, boolean replaceOnlyAir)
    {
        final BlockState twig = getFallenTwig();
        final BlockState leaf = getFallenLeaves();
        if (twig == null && leaf == null)
        {
            return;
        }
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        cursor.set(pos);
        BlockState stateAt = Blocks.AIR.defaultBlockState();
        while (stateAt.getBlock() instanceof ILeavesBlock || stateAt.canBeReplaced())
        {
            cursor.move(0, -1, 0);

            stateAt = level.getBlockState(cursor);
        }
        cursor.move(0, 1, 0);
        stateAt = level.getBlockState(cursor);

        if (stateAt.canBeReplaced())
        {
            BlockState placeState = twig == null ? leaf : twig;
            if (leaf != null && twig != null && random.nextFloat() < 0.5f)
            {
                placeState = leaf;
                if (stateAt.getBlock() == leaf.getBlock() && stateAt.getBlock() instanceof FallenLeavesBlock leavesBlock && stateAt.getValue(FallenLeavesBlock.LAYERS) < FallenLeavesBlock.MAX_LAYERS)
                {
                    final  int layers = stateAt.getValue(FallenLeavesBlock.LAYERS);
                    final BlockState toPlace = layers + 1 == FallenLeavesBlock.MAX_LAYERS ? leavesBlock.getLeaves().get().defaultBlockState().setValue(TFCLeavesBlock.PERSISTENT, true) : state.setValue(FallenLeavesBlock.LAYERS, layers + 1);
                    level.setBlockAndUpdate(pos, toPlace);
                }
            }
            if (placeState.canSurvive(level, cursor))
            {
                if (replaceOnlyAir && !stateAt.isAir())
                {
                    return;
                }
                level.setBlockAndUpdate(cursor, placeState);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        onEntityInside(level, entity);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState()
            .setValue(PERSISTENT, context.getPlayer() != null)
            .setValue(getFluidProperty(), getFluidProperty().keyForOrEmpty(fluid.getType()));
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PERSISTENT, getDistanceProperty(), getFluidProperty());
    }

    @Nullable
    public BlockState getFallenLeaves()
    {
        return fallenLeaves == null ? null : fallenLeaves.get().defaultBlockState();
    }

    @Nullable
    public BlockState getFallenTwig()
    {
        return fallenTwig == null ? null : fallenTwig.get().defaultBlockState();
    }

    @Override
    public float slowEntityFactor(BlockState state)
    {
        return state.getFluidState().isEmpty() ? TFCConfig.SERVER.leavesMovementModifier.get().floatValue() : NO_SLOW;
    }

    public int getAutumnIndex()
    {
        return autumnIndex;
    }

    protected IntegerProperty getDistanceProperty()
    {
        return TFCBlockStateProperties.DISTANCE_9;
    }

    private int updateDistance(LevelAccessor level, BlockPos pos)
    {
        int distance = 1 + maxDecayDistance;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : Helpers.DIRECTIONS)
        {
            mutablePos.set(pos).move(direction);
            distance = Math.min(distance, getDistance(level.getBlockState(mutablePos)) + 1);
            if (distance == 1)
            {
                break;
            }
        }
        return distance;
    }

    private int getDistance(BlockState neighbor)
    {
        if (Helpers.isBlock(neighbor.getBlock(), BlockTags.LOGS))
        {
            return 0;
        }
        else
        {
            // Check against this leaf block only, not any leaves
            return neighbor.getBlock() == this ? neighbor.getValue(getDistanceProperty()) : maxDecayDistance;
        }
    }
}