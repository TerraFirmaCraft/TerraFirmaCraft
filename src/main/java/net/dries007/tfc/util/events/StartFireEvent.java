/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import net.dries007.tfc.util.InteractionManager;

/**
 * This event is used for lighting fires or optionally light-able blocks. If it's not cancelled, TFC will try to place a fire block.
 *
 * For things like flint and steel that don't require special mechanics, this event logic is handled for you in
 * {@link InteractionManager#registerDefaultInteractions()}. Adding items to the tag #starts_fires_with_items or #starts_fires_with_durability
 * will emulate this behavior (and allow TFC devices to be lit by your item)
 *
 * Note that the parameters of this event are the same as those expected by an item use, ie that the position, state, and direction
 * reflect what would happen if a block was clicked on. Direction refers to the face that was clicked.
 */
@Cancelable
public final class StartFireEvent extends Event
{
    public static boolean startFire(Level level, BlockPos pos, BlockState state, Direction direction, @Nullable Player player, @Nullable ItemStack stack)
    {
        return startFire(level, pos, state, direction, player, stack, true);
    }

    public static boolean startFire(Level level, BlockPos pos, BlockState state, Direction direction, @Nullable Player player, @Nullable ItemStack stack, boolean placeFireBlockIfFailed)
    {
        boolean cancelled = MinecraftForge.EVENT_BUS.post(new StartFireEvent(level, pos, state, direction, player, stack));
        if (cancelled)
        {
            if (player instanceof ServerPlayer serverPlayer)
            {
                SpecialBlockTrigger.LIT.trigger(serverPlayer, state);
            }
            return true;
        }
        if (placeFireBlockIfFailed)
        {
            pos = pos.relative(direction);
            if (BaseFireBlock.canBePlacedAt(level, pos, direction))
            {
                level.setBlock(pos, BaseFireBlock.getState(level, pos), 11);
                return true;
            }
        }
        return false;
    }

    private final Level world;
    private final BlockPos pos;
    private final BlockState state;
    private final Direction direction;
    private final Player player;
    @Nullable
    private final ItemStack stack;

    private StartFireEvent(Level world, BlockPos pos, BlockState state, Direction direction, @Nullable Player player, @Nullable ItemStack stack)
    {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.direction = direction;
        this.player = player;
        this.stack = stack;
    }

    public Level getLevel()
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
    public Player getPlayer()
    {
        return player;
    }

    @Nullable
    public ItemStack getItemStack()
    {
        return stack;
    }
}
