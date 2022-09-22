/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import java.util.function.Predicate;

import net.dries007.tfc.util.advancements.TFCAdvancements;
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
 * This event is used for lighting fires or optionally light-able blocks. This event should alwyas be cancelled if it was handled by the event listener
 * The default behavior, FireResult.IF_FAILED, places a fire block when the event is not canceled. This can be set to ALWAYS and NEVER, which either guarantee or ban a fire block from placing.
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
    public static boolean startFire(Level level, BlockPos pos, BlockState state, Direction direction, @Nullable Player player, ItemStack stack)
    {
        return startFire(level, pos, state, direction, player, stack, FireResult.IF_FAILED, FireStrength.STRONG);
    }

    public static boolean startFire(Level level, BlockPos pos, BlockState state, Direction direction, @Nullable Player player, ItemStack stack, FireResult result)
    {
        return startFire(level, pos, state, direction, player, stack, result, FireStrength.STRONG);
    }

    public static boolean startFire(Level level, BlockPos pos, BlockState state, Direction direction, @Nullable Player player, ItemStack stack, FireResult fireResult, FireStrength strength)
    {
        final StartFireEvent event = new StartFireEvent(level, pos, state, direction, player, stack, fireResult, strength);
        final boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
        boolean actionPerformed = false;
        if (cancelled)
        {
            if (player instanceof ServerPlayer serverPlayer)
            {
                TFCAdvancements.LIT.trigger(serverPlayer, state);
            }
            actionPerformed = true;
        }
        if (event.fireResult.predicate.test(event))
        {
            pos = pos.relative(direction);
            if (BaseFireBlock.canBePlacedAt(level, pos, direction))
            {
                level.setBlock(pos, BaseFireBlock.getState(level, pos), 11);
                actionPerformed = true;
            }
        }
        return actionPerformed;
    }

    private final Level world;
    private final BlockPos pos;
    private final BlockState state;
    private final Direction direction;
    @Nullable
    private final Player player;
    private final ItemStack stack;
    private final FireStrength strength;
    private FireResult fireResult;

    private StartFireEvent(Level world, BlockPos pos, BlockState state, Direction direction, @Nullable Player player, ItemStack stack, FireResult result, FireStrength strength)
    {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.direction = direction;
        this.player = player;
        this.stack = stack;
        this.fireResult = result;
        this.strength = strength;
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

    public ItemStack getItemStack()
    {
        return stack;
    }

    public FireResult getFireResult()
    {
        return fireResult;
    }

    public void setFireResult(FireResult result)
    {
        this.fireResult = result;
    }

    public boolean isStrong()
    {
        return strength == FireStrength.STRONG;
    }

    public enum FireResult
    {
        ALWAYS(event -> true),
        NEVER(event -> false),
        IF_FAILED(event -> !event.isCanceled());

        private final Predicate<StartFireEvent> predicate;

        FireResult(Predicate<StartFireEvent> predicate)
        {
            this.predicate = predicate;
        }
    }

    public enum FireStrength
    {
        /**
         * Strong represents a fire starting where:
         * 1. The fire starting was the primary functionality (i.e. not a side effect). This is to make it obvious to the player what can happen, or
         * 2. Destructive fire starting behaviors (such as creating fire blocks, lighting log piles, etc.) is desired.
         */
        STRONG,
        /**
         * Weak represents a fire starting where:
         * 1. The fire starting may have been secondary behavior or a side effect (i.e. easy to misclick).
         * 2. Destructive fire starting behaviors should <strong>not</strong> be attempted.
         */
        WEAK;
        // More granularity may be added if needed
    }
}
