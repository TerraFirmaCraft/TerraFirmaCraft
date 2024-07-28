/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.common.blocks.wood.TFCLeavesBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;

public class FruitTreeLeavesBlock extends SeasonalPlantBlock implements IForgeBlockExtension, ILeavesBlock, IBushBlock, HoeOverlayBlock, IFluidLoggable
{
    /**
     * Taking into account only environment rainfall, on a scale [0, 100]
     */
    public static int getHydration(Level level, BlockPos pos)
    {
        return (int) (Climate.getRainfall(level, pos) / 5);
    }

    public static MapColor getMapColor(BlockState state)
    {
        return switch(state.getValue(LIFECYCLE))
            {
                case DORMANT -> MapColor.COLOR_BROWN;
                case FLOWERING -> MapColor.COLOR_PINK;
                default -> MapColor.PLANT;
            };
    }

    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER;

    private final int flowerColor;

    public FruitTreeLeavesBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages, Supplier<ClimateRange> climateRange, int flowerColor)
    {
        super(properties, climateRange, productItem, stages);
        this.flowerColor = flowerColor;

        registerDefaultState(getStateDefinition().any().setValue(PERSISTENT, false).setValue(LIFECYCLE, Lifecycle.HEALTHY));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.block();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(PERSISTENT, context.getPlayer() != null).setValue(getFluidProperty(), getFluidProperty().keyForOrEmpty(fluid.getType()));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(LIFECYCLE) == Lifecycle.FLOWERING && random.nextInt(10) == 0)
        {
            final BlockState belowState = level.getBlockState(pos.below());
            if (belowState.isAir())
            {
                final BlockState aboveState = level.getBlockState(pos.above());
                ParticleOptions particle;
                if (Helpers.isBlock(aboveState, TFCTags.Blocks.SNOW) && random.nextBoolean())
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
        TFCLeavesBlock.dripRainwater(level, pos, random);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        IBushBlock.randomTick(this, state, level, pos, random);
    }


    // this is superficially the same as the StationaryBerryBushBlock onUpdate, we can condense them
    @Override
    public void onUpdate(Level level, BlockPos pos, BlockState state)
    {
        // Fruit tree leaves work like berry bushes, but don't have propagation or growth functionality.
        // Which makes them relatively simple, as then they only need to keep track of their lifecycle.
        if (state.getValue(PERSISTENT)) return; // persistent leaves don't grow
        if (level.getBlockEntity(pos) instanceof BerryBushBlockEntity leaves)
        {
            Lifecycle currentLifecycle = state.getValue(LIFECYCLE);
            Lifecycle expectedLifecycle = getLifecycleForCurrentMonth();
            // if we are not working with a plant that is or should be dormant
            if (!checkAndSetDormant(level, pos, state, currentLifecycle, expectedLifecycle))
            {
                final ClimateRange range = climateRange.get();
                final int hydration = getHydration(level, pos);

                if (range.checkBoth(hydration, Climate.getAverageTemperature(level, pos), false))
                {
                    currentLifecycle = currentLifecycle.advanceTowards(expectedLifecycle);
                }
                else
                {
                    currentLifecycle = Lifecycle.DORMANT;
                }

                BlockState newState = state.setValue(LIFECYCLE, currentLifecycle);

                if (state != newState)
                {
                    level.setBlock(pos, newState, 3);
                }
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        TFCLeavesBlock.onEntityInside(level, entity);
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, Consumer<Component> text, boolean isDebug)
    {
        final ClimateRange range = climateRange.get();
        text.accept(FarmlandBlock.getHydrationTooltip(level, pos, range, false, getHydration(level, pos)));
        text.accept(FarmlandBlock.getAverageTemperatureTooltip(level, pos, range, false));
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    public int getFlowerColor()
    {
        return flowerColor;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIFECYCLE, PERSISTENT, getFluidProperty()); // avoid "STAGE" property
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        if (isValid(level, currentPos, state))
        {
            return state;
        }
        if (level instanceof ServerLevel server)
        {
            TFCLeavesBlock.doParticles(server, currentPos.getX() + level.getRandom().nextFloat(), currentPos.getY() + level.getRandom().nextFloat(), currentPos.getZ() + level.getRandom().nextFloat(), 1);
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return true;
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos)
    {
        return 1;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return 0.2F;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (!isValid(level, pos, state))
        {
            level.destroyBlock(pos, true);
            TFCLeavesBlock.doParticles(level, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat(), 1);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    private boolean isValid(LevelAccessor level, BlockPos pos, BlockState state)
    {
        if (state.getValue(PERSISTENT))
        {
            return true;
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : Helpers.DIRECTIONS)
        {
            mutablePos.set(pos).move(direction);
            if (Helpers.isBlock(level.getBlockState(mutablePos), TFCTags.Blocks.FRUIT_TREE_BRANCH))
            {
                return true;
            }
        }
        return false;
    }
}
