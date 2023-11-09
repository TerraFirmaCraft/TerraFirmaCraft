/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.blocks.mechanical.WindmillBlock;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.RotationNetworkManager;
import net.dries007.tfc.util.rotation.SourceNode;


public class WindmillBlockEntity extends TickableBlockEntity
{
    private static final float MIN_SPEED = Mth.TWO_PI / (20 * 20);
    private static final float MAX_SPEED = Mth.TWO_PI / (8 * 20);
    private static final float LERP_SPEED = MIN_SPEED / (5 * 20);

    public static void serverTick(Level level, BlockPos pos, BlockState state, WindmillBlockEntity windmill)
    {
        windmill.checkForLastTickSync();
        clientTick(level, pos, state, windmill);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, WindmillBlockEntity windmill)
    {
        final Rotation.Tickable rotation = windmill.node.rotation();

        rotation.tick();

        final float targetSpeed = Mth.map(state.getValue(WindmillBlock.COUNT), 1, 5, MIN_SPEED, MAX_SPEED);
        final float currentSpeed = rotation.speed();
        final float nextSpeed = targetSpeed > currentSpeed
            ? Math.min(targetSpeed, currentSpeed + LERP_SPEED)
            : Math.max(targetSpeed, currentSpeed - LERP_SPEED);

        rotation.setSpeed(nextSpeed);
    }

    private final SourceNode node;

    public WindmillBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.WINDMILL.get(), pos, state);

        // Windmills can have up to five blades added, which increase their maximum speed.
        // - Rotation speed interpolates as not to have a sharp jump between levels.
        // - Connections are static and only in the horizontal directions specified by the axis
        // - Rotation is always in the 'forward' direction (so windmills
        final Direction.Axis axis = state.getValue(WindmillBlock.AXIS);
        final Direction forward = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        final Direction backwards = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);

        this.node = new SourceNode(pos, EnumSet.of(forward, backwards), Rotation.of(forward, 0f)) {
            @NotNull
            @Override
            public Rotation.Tickable rotation(Direction exitDirection)
            {
                assert exitDirection.getAxis() == axis;
                return rotation;
            }
        };
    }

    public float getRotationAngle(float partialTick)
    {
        return Rotation.angle(node.rotation(), partialTick);
    }

    @Override
    public void setRemoved()
    {
        assert level != null;
        super.setRemoved();
        RotationNetworkManager.remove(level, node);
    }

    @Override
    public void onChunkUnloaded()
    {
        assert level != null;
        super.onChunkUnloaded();
        RotationNetworkManager.remove(level, node);
    }

    @Override
    public void onLoad()
    {
        assert level != null;
        super.onLoad();
        RotationNetworkManager.addSource(level, node);
    }
}
