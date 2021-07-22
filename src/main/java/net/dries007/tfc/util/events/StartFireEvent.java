/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import net.dries007.tfc.util.InteractionManager;

/**
 * This event is used for lighting fires or optionally light-able blocks. If it's not cancelled, TFC will try to place a fire block.
 *
 * For things like flint and steel that don't require special mechanics, this event logic is handled for you in
 * {@link InteractionManager#setup()}. Adding items to the tag #starts_fires_with_items or #starts_fires_with_durability
 * will emulate this behavior (and allow TFC devices to be lit by your item)
 *
 * Note that the parameters of this event are the same as those expected by an item use, ie that the position, state, and direction
 * reflect what would happen if a block was clicked on. Direction refers to the face that was clicked.
 */
@Cancelable
public class StartFireEvent extends Event
{
    public static boolean startFire(World world, BlockPos pos, BlockState state, Direction direction, @Nullable PlayerEntity player, @Nullable ItemStack stack)
    {
        return startFire(world, pos, state, direction, player, stack, true);
    }

    public static boolean startFire(World world, BlockPos pos, BlockState state, Direction direction, @Nullable PlayerEntity player, @Nullable ItemStack stack, boolean placeFireBlockIfFailed)
    {
        boolean cancelled = MinecraftForge.EVENT_BUS.post(new StartFireEvent(world, pos, state, direction, player, stack));
        if (cancelled)
        {
            return true;
        }
        if (placeFireBlockIfFailed)
        {
            pos = pos.relative(direction);
            if (AbstractFireBlock.canBePlacedAt(world, pos, direction))
            {
                world.setBlock(pos, AbstractFireBlock.getState(world, pos), 11);
                return true;
            }
        }
        return false;
    }

    private final World world;
    private final BlockPos pos;
    private final BlockState state;
    private final Direction direction;
    private final PlayerEntity player;
    private final ItemStack stack;

    private StartFireEvent(World world, BlockPos pos, BlockState state, Direction direction, @Nullable PlayerEntity player, ItemStack stack)
    {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.direction = direction;
        this.player = player;
        this.stack = stack;
    }

    public World getLevel()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public BlockState getState()
    {
        return state;
    }

    public Direction getTargetedFace()
    {
        return direction;
    }

    @Nullable
    public PlayerEntity getPlayer()
    {
        return player;
    }

    public ItemStack getItemStack()
    {
        return stack;
    }
}
