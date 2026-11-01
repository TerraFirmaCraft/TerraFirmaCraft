/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class CreepingWaterPlantBlock extends CreepingPlantBlock implements IFluidLoggable
{
    public static final BooleanProperty OPEN = TFCBlockStateProperties.OPEN;

    public static CreepingWaterPlantBlock create(RegistryPlant plant, FluidProperty fluid, ExtendedProperties properties)
    {
        return new CreepingWaterPlantBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }

            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }

            @Override
            public boolean canCreepOn(LevelReader level, BlockPos pos, BlockState state, Direction direction)
            {
                return Helpers.isBlock(state, TFCTags.Blocks.ANEMONE_PLANTABLE_ON) && super.canCreepOn(level, pos, state, direction);
            }

        };
    }

    public static CreepingWaterPlantBlock createRock(RegistryPlant plant, FluidProperty fluid, ExtendedProperties properties)
    {
        return new CreepingWaterPlantBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }

            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }

            @Override
            public boolean canCreepOn(LevelReader level, BlockPos pos, BlockState state, Direction direction)
            {
                return Helpers.isBlock(state, TFCTags.Blocks.CREEPING_STONE_PLANTABLE_ON) && super.canCreepOn(level, pos, state, direction);
            }

        };
    }

    protected CreepingWaterPlantBlock(ExtendedProperties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any().setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)).setValue(OPEN, false));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos pos = context.getClickedPos();
        Fluid fluid = context.getLevel().getFluidState(pos).getType();
        BlockState state = defaultBlockState();
        if (getFluidProperty().canContain(fluid))
        {
            state = state.setValue(getFluidProperty(), getFluidProperty().keyFor(fluid));
            if (fluid == TFCFluids.SALT_WATER.getSource())
            {
                state = state.setValue(OPEN, true);
            }
        }
        return updateStateFromSides(context.getLevel(), context.getClickedPos(), state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), canCreepOn(level, facingPos, facingState, direction));
        return isEmptyContents(state) ? state.getFluidState().createLegacyBlock() : state;
    }

    private static boolean isEmptyContents(BlockState state)
    {
        for (BooleanProperty property : SHAPES.keySet())
        {
            if (state.getValue(property))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(OPEN));
        builder.add(getFluidProperty());
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (!level.isClientSide && level.getFluidState(pos).getType().isSame(TFCFluids.SALT_WATER.getSource()))
        {
            if (level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(pos.getCenter(), 1, 1, 1)).isEmpty())
            {
                level.setBlock(pos, state.setValue(OPEN, true), Block.UPDATE_ALL);
            }
            else
            {
                level.scheduleTick(pos, this, 160);
            }
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (!level.isClientSide && state.getValue(OPEN))
        {
            level.setBlock(pos, state.setValue(OPEN, false), Block.UPDATE_ALL);
            level.scheduleTick(pos, this, level.random.nextInt(40, 160));
        }
    }
}
