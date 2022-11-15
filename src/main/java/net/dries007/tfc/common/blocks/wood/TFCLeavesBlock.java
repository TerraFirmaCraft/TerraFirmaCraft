/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public abstract class TFCLeavesBlock extends Block implements ILeavesBlock, IForgeBlockExtension, IFluidLoggable
{
    public static void doParticles(ServerLevel level, double x, double y, double z, int count)
    {
        level.sendParticles(TFCParticles.LEAF.get(), x, y, z, count, Helpers.triangle(level.random), Helpers.triangle(level.random), Helpers.triangle(level.random), 0.3f);
    }

    public static void onEntityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        final float modifier = TFCConfig.SERVER.leavesMovementModifier.get().floatValue();
        if (modifier < 1 && level.getFluidState(pos).isEmpty())
        {
            Helpers.slowEntityInBlock(entity, modifier, 5);
        }
        if (Helpers.isEntity(entity, TFCTags.Entities.DESTROYED_BY_LEAVES))
        {
            entity.kill();
        }
        if (level.random.nextInt(20) == 0 && level instanceof ServerLevel server)
        {
            doParticles(server, entity.getX(), entity.getEyeY() - 0.25D, entity.getZ(), 3);
        }
    }

    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER;

    public static TFCLeavesBlock create(ExtendedProperties properties, int maxDecayDistance)
    {
        final IntegerProperty distanceProperty = getDistanceProperty(maxDecayDistance);
        return new TFCLeavesBlock(properties, maxDecayDistance)
        {
            @Override
            protected IntegerProperty getDistanceProperty()
            {
                return distanceProperty;
            }
        };
    }

    private static IntegerProperty getDistanceProperty(int maxDecayDistance)
    {
        if (maxDecayDistance >= 7 && maxDecayDistance < 7 + TFCBlockStateProperties.DISTANCES.length)
        {
            return TFCBlockStateProperties.DISTANCES[maxDecayDistance - 7 + 1]; // we select one higher than max
        }
        throw new IllegalArgumentException("No property set for distance: " + maxDecayDistance);
    }

    /* The maximum value of the decay property. */
    private final int maxDecayDistance;
    private final ExtendedProperties properties;

    protected TFCLeavesBlock(ExtendedProperties properties, int maxDecayDistance)
    {
        super(properties.properties());
        this.maxDecayDistance = maxDecayDistance;
        this.properties = properties;

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
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        super.randomTick(state, level, pos, rand); // super calls tick()
        if (state.getValue(getDistanceProperty()) > maxDecayDistance && !state.getValue(PERSISTENT))
        {
            level.removeBlock(pos, false);
            doParticles(level, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat(), 1);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        int distance = updateDistance(level, pos);
        if (distance > maxDecayDistance)
        {
            if (!state.getValue(PERSISTENT))
            {
                if (!TFCConfig.SERVER.enableLeavesDecaySlowly.get())
                {
                    level.removeBlock(pos, false);
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
        else
        {
            level.setBlock(pos, state.setValue(getDistanceProperty(), distance), 3);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        onEntityInside(state, level, pos, entity);
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

    /**
     * The reason this is not a constructor parameter is because the super class (Block) will use this directly, and nothing else is initialized in time.
     */
    protected abstract IntegerProperty getDistanceProperty();

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