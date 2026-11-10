/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.ISlowEntities;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class KelpTreeBlock extends PipePlantBlock implements IFluidLoggable, ISlowEntities
{
    public static KelpTreeBlock create(RegistryPlant plant, ExtendedProperties properties, FluidProperty fluid)
    {
        return new KelpTreeBlock(properties)
        {
            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }

            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected KelpTreeBlock(ExtendedProperties properties)
    {
        super(0.3125F, properties);
        registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false).setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));

    }

    /**
     * Gets the plant metadata for this block.
     * See the various {@link PlantBlock#create(RegistryPlant, ExtendedProperties)} methods and subclass versions for how to use.
     */
    public abstract RegistryPlant getPlant();


    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        FluidHelpers.tickFluid(level, pos, state);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(getFluidProperty()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        if (state.getFluidState().isEmpty())
        {
            return false; // Requires water.
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    protected boolean testDown(BlockState state)
    {
        return Helpers.isBlock(state, TFCTags.Blocks.KELP_BRANCH) || Helpers.isBlock(state, TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
    }

    @Override
    protected boolean testUp(BlockState state)
    {
        return testHorizontal(state);
    }

    @Override
    protected boolean testHorizontal(BlockState state)
    {
        return Helpers.isBlock(state, TFCTags.Blocks.KELP_TREE);
    }

    @Override
    public float slowEntityFactor(BlockState state)
    {
        final float modifier = TFCConfig.SERVER.plantsMovementModifier.get().floatValue(); // 0.0 = full speed factor, 1.0 = no modifier
        return Helpers.lerp(modifier, getPlant().getSpeedFactor(), 1.0f);
    }

}
