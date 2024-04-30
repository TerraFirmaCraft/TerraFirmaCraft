/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.items.WindmillBladeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.blocks.rotation.WindmillBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.SourceNode;

import static net.dries007.tfc.TerraFirmaCraft.*;


public class WindmillBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements RotatingBlockEntity
{
    public static final int SLOTS = 5;
    public static final float MIN_SPEED = Mth.TWO_PI / (20 * 20);
    public static final float MAX_SPEED = Mth.TWO_PI / (8 * 20);
    private static final float LERP_SPEED = MIN_SPEED / (5 * 20);

    // client-only
    public boolean hasFullIdenticalSet = false;

    public static void serverTick(Level level, BlockPos pos, BlockState state, WindmillBlockEntity windmill)
    {
        windmill.checkForLastTickSync();
        if (windmill.needsStateUpdate)
        {
            windmill.updateState();
        }

        clientTick(level, pos, state, windmill);

        if (level.getGameTime() % 40 == 0 && isObstructedBySolidBlocks(level, pos, state.getValue(WindmillBlock.AXIS)))
        {
            // Check every two seconds if the windmill is obstructed, and if so, break
            level.destroyBlock(pos, true);
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, WindmillBlockEntity windmill)
    {
        final Rotation.Tickable rotation = windmill.node.rotation();

        rotation.tick();

        final float targetSpeed = Mth.map(state.getValue(WindmillBlock.COUNT), 1, SLOTS, MIN_SPEED, MAX_SPEED);
        final float currentSpeed = rotation.speed();
        final float nextSpeed = targetSpeed > currentSpeed
            ? Math.min(targetSpeed, currentSpeed + LERP_SPEED)
            : Math.max(targetSpeed, currentSpeed - LERP_SPEED);

        rotation.setSpeed(nextSpeed);

        windmill.hasFullIdenticalSet = windmill.hasFullIdenticalSet();
    }

    public static boolean isObstructedBySolidBlocks(Level level, BlockPos pos, Direction.Axis axis)
    {
        // Check every two seconds if the windmill is obstructed, and if so, break
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dH = -6; dH <= 6; dH++)
        {
            for (int dy = -6; dy <= 6; dy++)
            {
                if (dH * dH + dy * dy < 7 * 7 && (dH != 0 || dy != 0))
                {
                    cursor.setWithOffset(pos, axis == Direction.Axis.X ? 0 : dH, dy, axis == Direction.Axis.Z ? 0 : dH);

                    final BlockState state = level.getBlockState(cursor);
                    if (!state.isAir() && !state.getCollisionShape(level, cursor).isEmpty())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.windmill");

    private final SourceNode node;
    private boolean invalid;
    private boolean needsStateUpdate = true;

    public WindmillBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.WINDMILL.get(), pos, state, defaultInventory(SLOTS), NAME);
    }

    public WindmillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<ItemStackHandler> inventory, Component defaultName)
    {
        super(type, pos, state, inventory, defaultName);
        // Windmills can have up to five blades added, which increase their maximum speed.
        // - Rotation speed interpolates as not to have a sharp jump between levels.
        // - Connections are static and only in the horizontal directions specified by the axis
        // - Rotation is always in the 'forward' direction (so windmills look somewhat consistent).
        final Direction.Axis axis = state.getValue(WindmillBlock.AXIS);

        this.invalid = false;
        this.node = new SourceNode(pos, Node.ofAxis(axis), Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE), 0f) {
            @Override
            public String toString()
            {
                return "Windmill[pos=%s, axis=%s]".formatted(pos(), axis);
            }
        };
    }

    public int updateState()
    {
        assert level != null;
        needsStateUpdate = false;
        int count = 0;
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            if (!stack.isEmpty())
            {
                count++;
            }
        }
        if (count == 0)
        {
            BlockState axleState = ((WindmillBlock) getBlockState().getBlock()).getAxle().defaultBlockState();
            axleState = Helpers.copyProperties(axleState, getBlockState());
            level.setBlockAndUpdate(worldPosition, axleState);
        }
        else
        {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(WindmillBlock.COUNT, count));
        }
        return count;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsStateUpdate = true;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.ALL_WINDMILL_BLADES);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        node.rotation().saveToTag(tag);
        tag.putBoolean("invalid", invalid);
    }

    @Override
    public void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        node.rotation().loadFromTag(tag);
        invalid = tag.getBoolean("invalid");
    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
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

    public boolean hasFullIdenticalSet()
    {
        ItemStack stack = inventory.getStackInSlot(0);

        // catch anything that's not a windmill blade
        for (int i = 1; i < SLOTS; i++)
        {
            if (!inventory.getStackInSlot(i).is(TFCTags.Items.ALL_WINDMILL_BLADES))
            {
                return false;
            }
        }

        WindmillBladeItem item = (WindmillBladeItem) stack.getItem();
        WindmillBladeItem.BladeModel model = item.getModel();

        for (int i = 1; i < SLOTS; i++)
        {
            if (((WindmillBladeItem) inventory.getStackInSlot(i).getItem()).getModel() != model)
            {
                return false;
            }
        }

        return true;
    }
}
