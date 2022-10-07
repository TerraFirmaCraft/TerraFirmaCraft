/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.server.level.ServerLevel;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class PlantBlock extends TFCBushBlock
{
    public static final IntegerProperty AGE = TFCBlockStateProperties.AGE_3;

    protected static final VoxelShape PLANT_SHAPE = box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public static PlantBlock create(RegistryPlant plant, ExtendedProperties properties)
    {
        return new PlantBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected PlantBlock(ExtendedProperties properties)
    {
        super(properties);

        BlockState stateDefinition = getStateDefinition().any().setValue(AGE, 0);
        IntegerProperty stageProperty = getPlant().getStageProperty();
        if (stageProperty != null)
        {
            stateDefinition = stateDefinition.setValue(stageProperty, 0);
        }
        registerDefaultState(stateDefinition);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random)
    {
        if (random.nextInt(400) == 0 && Helpers.isBlock(state, BlockTags.FLOWERS) && Calendars.CLIENT.getCalendarMonthOfYear().getSeason() == Season.SPRING)
        {
            level.addParticle(TFCParticles.BUTTERFLY.get(), pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0, 0, 0);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return PLANT_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        if (random.nextDouble() < TFCConfig.SERVER.plantGrowthChance.get())
        {
            state = state.setValue(AGE, Math.min(state.getValue(AGE) + 1, 3));
        }
        level.setBlockAndUpdate(pos, updateStateWithCurrentMonth(state));
    }

    /**
     * Gets the plant metadata for this block.
     *
     * The stage property is isolated and referenced via this as it is needed in the {@link Block} constructor - which builds the state container, and requires all property references to be computed in {@link Block#createBlockStateDefinition(StateDefinition.Builder)}.
     *
     * See the various {@link PlantBlock#create(RegistryPlant, ExtendedProperties)} methods and subclass versions for how to use.
     */
    public abstract RegistryPlant getPlant();

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return updateStateWithCurrentMonth(defaultBlockState());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        if (getPlant().getStageProperty() != null)
        {
            builder.add(getPlant().getStageProperty());
        }
        builder.add(AGE);
    }

    @Override
    public float getSpeedFactor()
    {
        final float modifier = TFCConfig.SERVER.plantsMovementModifier.get().floatValue(); // 0.0 = full speed factor, 1.0 = no modifier
        return Helpers.lerp(modifier, speedFactor, 1.0f);
    }

    protected BlockState updateStateWithCurrentMonth(BlockState state)
    {
        return getPlant().getStageProperty() != null ? state.setValue(getPlant().getStageProperty(), getPlant().stageFor(Calendars.SERVER.getCalendarMonthOfYear())) : state;
    }
}
