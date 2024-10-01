/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rotation;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.dries007.tfc.common.blockentities.rotation.ClutchBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.wood.ExtendedRotatedPillarBlock;
import net.dries007.tfc.util.network.RotationOwner;

public class ClutchBlock extends ExtendedRotatedPillarBlock implements EntityBlockExtension, ConnectedAxleBlock
{
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private final Supplier<? extends AxleBlock> axle;

    public ClutchBlock(ExtendedProperties properties, Supplier<? extends AxleBlock> axle)
    {
        super(properties);

        this.axle = axle;

        registerDefaultState(getStateDefinition().any().setValue(POWERED, false).setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public AxleBlock getAxle()
    {
        return axle.get();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(BlockState state)
    {
        return true;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        final boolean signal = level.hasNeighborSignal(pos);
        if (signal != state.getValue(POWERED))
        {
            level.setBlockAndUpdate(pos, state.cycle(POWERED));
            if (level.getBlockEntity(pos) instanceof ClutchBlockEntity clutch)
            {
                clutch.updateConnections();
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final BlockState state = super.getStateForPlacement(context);
        if (state != null)
        {
            return state.setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(POWERED));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(POWERED) && random.nextFloat() < 0.1f)
        {
            ParticleUtils.spawnParticlesOnBlockFaces(level, pos, DustParticleOptions.REDSTONE, UniformInt.of(1, 3));
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        RotationOwner.onTick(level, pos);
    }
}
