/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickableBlockEntity;
import net.dries007.tfc.common.blocks.RiverWaterBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rotation.WaterWheelBlock;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.SourceNode;
import net.dries007.tfc.world.river.Flow;

public class WaterWheelBlockEntity extends TickableBlockEntity implements RotatingBlockEntity
{
    public static final float MAX_SPEED = Mth.TWO_PI / (4 * 20);
    public static final float LERP_SPEED = MAX_SPEED / (20 * 20);

    private static final float MAX_FLOW = 10f;

    public static void serverTick(Level level, BlockPos pos, BlockState state, WaterWheelBlockEntity wheel)
    {
        wheel.checkForLastTickSync();

        clientTick(level, pos, state, wheel);

        if (level.getGameTime() % 40 == 0)
        {
            final Float maybeFlowRate = calculateFlowRateAndObstruction(level, pos, state.getValue(WaterWheelBlock.AXIS));
            if (maybeFlowRate == null)
            {
                // Water wheel is obstructed somehow
                level.destroyBlock(pos, true);
            }
            else
            {
                wheel.targetSpeed = maybeFlowRate * MAX_SPEED / MAX_FLOW;
                wheel.markForSync();
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, WaterWheelBlockEntity wheel)
    {
        final Rotation.Tickable rotation = wheel.node.rotation();
        final float currentSpeed = rotation.speed();
        final float targetSpeed = wheel.targetSpeed;

        final float nextSpeed = targetSpeed > currentSpeed
            ? Math.min(targetSpeed, currentSpeed + LERP_SPEED)
            : Math.max(targetSpeed, currentSpeed - LERP_SPEED);

        rotation.tick();
        rotation.setSpeed(nextSpeed);
    }

    /**
     * Check if the water wheel is obstructed, and calculate the potential flow rate.
     * The flow rate is based on a contributing flow and obstructing flow.
     * <ul>
     *     <li><strong>Contributing flow</strong> can only be provided by flowing river water, in the lower two blocks. The maximum contributing flow is 2 x 5 x 1.0f, with all blocks flowing in the exact correct direction.</li>
     *     <li><strong>Obstructing flow</strong> can occur from many source, including stationary water blocks in the way, other water blocks at or above the water wheel's center line, or even flowing river water in the wrong direction.</li>
     * </ul>
     * The net flow is based on all possible contributing flows, less all possible obstructing flows.
     * <p>
     * This method also checks for possible obstructions - any non-solid blocks in the path of the water wheel - and will remove them.
     * @return {@code null} if the water wheel is obstructed, otherwise the net flow, which will be in the range [-10f, 10f]
     */
    @Nullable
    public static Float calculateFlowRateAndObstruction(Level level, BlockPos pos, Direction.Axis axis)
    {
        // Flow rate is based on the number of flowing water blocks in the bottom two rows
        // All blocks must be completely obstructed - only water, or air
        // Any water blocks above the bottom two rows decrease flow rate
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        float contributingFlow = 0;
        float obstructionFlow = 0;

        for (int dH = -2; dH <= 2; dH++)
        {
            for (int dy = -2; dy <= 2; dy++)
            {
                if (dH != 0 || dy != 0)
                {
                    cursor.setWithOffset(pos, axis == Direction.Axis.X ? 0 : dH, dy, axis == Direction.Axis.Z ? 0 : dH);

                    final BlockState state = level.getBlockState(cursor);
                    if (state.getBlock() == TFCBlocks.RIVER_WATER.get())
                    {
                        // River water provides flow, if it is in the bottom section of the wheel, otherwise it obstructs flow
                        final Flow flow = state.getValue(RiverWaterBlock.FLOW);
                        final float flowRate = (float) (axis == Direction.Axis.X ? -flow.getVector().z : flow.getVector().x);

                        if (dy < 0)
                        {
                            contributingFlow += flowRate;
                        }
                        else
                        {
                            obstructionFlow += Math.abs(flowRate);
                        }
                        continue;
                    }
                    if (state.getBlock() == Blocks.WATER)
                    {
                        // Water is legal, but provides a minor obstruction, less so when it's in the bottom section of the wheel
                        obstructionFlow += dy < 0 ? 0.25f : 1f;
                        continue;
                    }
                    if (state.isAir() || state.getCollisionShape(level, cursor).isEmpty())
                    {
                        continue; // No obstruction and no flow contribution
                    }

                    // Any other block is an obstruction, and will cause the wheel to break
                    return null;
                }
            }
        }

        // Calculate the resultant flow, based on the maximum contributing flow minus obstructions
        if (contributingFlow > 0)
        {
            return -Math.max(0, contributingFlow - obstructionFlow);
        }
        else
        {
            return -Math.min(0, contributingFlow + obstructionFlow);
        }
    }

    private final SourceNode node;
    private boolean invalid;

    private float targetSpeed;

    public WaterWheelBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.WATER_WHEEL.get(), pos, state);
    }

    public WaterWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);


        // The water wheel is unique in that it may turn in both directions
        // It does not switch the rotation direction when it does this, rather, it just rotates in reverse and accumulates angle in negative values.
        final Direction.Axis axis = state.getValue(WaterWheelBlock.AXIS);

        this.targetSpeed = 0f;
        this.invalid = false;
        this.node = new SourceNode(pos, Node.ofAxis(axis), Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE), 0f) {
            @Override
            public String toString()
            {
                return "WaterWheel[pos=%s, axis=%s]".formatted(pos(), axis);
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        node.rotation().saveToTag(tag);
        tag.putBoolean("invalid", invalid);
        tag.putFloat("targetSpeed", targetSpeed);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        node.rotation().loadFromTag(tag);
        invalid = tag.getBoolean("invalid");
        targetSpeed = tag.getFloat("targetSpeed");
    }

    @Override
    protected void onLoadAdditional()
    {
        performNetworkAction(NetworkAction.ADD_SOURCE);
    }

    @Override
    protected void onUnloadAdditional()
    {
        performNetworkAction(NetworkAction.REMOVE);
    }

    @Override
    public void markAsInvalidInNetwork()
    {
        invalid = true;
    }

    @Override
    public boolean isInvalidInNetwork()
    {
        return invalid;
    }

    @Override
    public Node getRotationNode()
    {
        return node;
    }
}
