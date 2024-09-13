/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
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

    public static PlantBlock createDry(RegistryPlant plant, ExtendedProperties properties)
    {
        return new PlantBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }

            @Override
            public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
            {
                return isDryBlockPlantable(level.getBlockState(pos.below()));
            }
        };
    }

    public static PlantBlock createCactusFlower(RegistryPlant plant, ExtendedProperties properties)
    {
        return new PlantBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }

            @Override
            protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
            {
                return state.getBlock() instanceof BranchingCactusBlock;
            }
        };
    }

    public static PlantBlock createFlat(RegistryPlant plant, ExtendedProperties properties)
    {

        return new PlantBlock(properties)
        {
            static final VoxelShape SHAPE = box(0, 0, 0, 16, 3, 16);

            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }

            @Override
            public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
            {
                return SHAPE;
            }
        };
    }

    public static boolean isDryBlockPlantable(BlockState state)
    {
        return Helpers.isBlock(state, BlockTags.SAND) || Helpers.isBlock(state, Tags.Blocks.SANDS) || Helpers.isBlock(state, TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    protected PlantBlock(ExtendedProperties properties)
    {
        super(properties);

        BlockState stateDefinition = getStateDefinition().any();
        IntegerProperty ageProperty = getPlant().getAgeProperty();
        if (ageProperty != null)
        {
            stateDefinition = stateDefinition.setValue(ageProperty, 0);
        }
        registerDefaultState(stateDefinition);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (random.nextInt(400) == 0 && Helpers.isBlock(state, BlockTags.FLOWERS) && Calendars.CLIENT.getCalendarMonthOfYear().getSeason() == Season.SPRING)
        {
            level.addParticle(TFCParticles.BUTTERFLY.get(), pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0, 0, 0);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return PLANT_SHAPE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        final var ageProp = getPlant().getAgeProperty();
        if (ageProp != null)
        {
            final int age = state.getValue(ageProp);
            if (age < 3)
            {
                state = state.setValue(AGE, age + 1);
                level.setBlockAndUpdate(pos, state);
            }
        }
    }

    /**
     * Gets the plant metadata for this block.
     * See the various {@link PlantBlock#create(RegistryPlant, ExtendedProperties)} methods and subclass versions for how to use.
     */
    public abstract RegistryPlant getPlant();

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        if (getPlant().getAgeProperty() != null)
        {
            builder.add(AGE);
        }
    }

    @Override
    public float getSpeedFactor()
    {
        final float modifier = TFCConfig.SERVER.plantsMovementModifier.get().floatValue(); // 0.0 = full speed factor, 1.0 = no modifier
        return Helpers.lerp(modifier, speedFactor, 1.0f);
    }

}
