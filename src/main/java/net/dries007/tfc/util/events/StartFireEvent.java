/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

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
 * This event is used for lighting things with fire. It can be cancelled to handle lighting of an external device or source.
 * <p>
 * When the strength of the event is {@link #isStrong()}, if it is <strong>not</strong> cancelled, a fire block will be created. If this was cancelled, the {@link TFCAdvancements#LIT} will be triggered.
 * <p>
 * For simple devices that create fires either by right-clicking (like flint and steel) or by consuming (like fire charges), they can be added to the tags
 * {@code #tfc:starts_fires_with_durability} or {@code #tfc:starts_fires_with_items} and this event will be fired from {@link InteractionManager} automatically.
 */
@Cancelable
public final class StartFireEvent extends Event
{
    public static boolean startFire(Level level, BlockPos pos, BlockState state, Direction direction, @Nullable Player player, ItemStack stack)
    {
        return startFire(level, pos, state, direction, player, stack, FireStrength.STRONG);
    }

    public static boolean startFire(Level level, BlockPos pos, BlockState state, Direction direction, @Nullable Player player, ItemStack stack, FireStrength strength)
    {
        final StartFireEvent event = new StartFireEvent(level, pos, state, direction, player, stack, strength);
        final boolean cancelled = MinecraftForge.EVENT_BUS.post(event);

        if (cancelled && player instanceof ServerPlayer serverPlayer)
        {
            TFCAdvancements.LIT.trigger(serverPlayer, state);
        }

        if (!cancelled && event.isStrong())
        {
            // If the block we are targeting is a non-solid block, we delete the block and replace it with fire
            // Otherwise with solid blocks, we place fire on the face we were targeting
            if (state.isCollisionShapeFullBlock(level, pos))
            {
                pos = pos.relative(direction);
            }
            else
            {
                final BlockState stateAt = level.getBlockState(pos);
                if (stateAt.isFlammable(level, pos, direction) && (stateAt.canBeReplaced() || stateAt.getCollisionShape(level, pos).isEmpty()))
                {
                    level.destroyBlock(pos, false);
                }
            }

            if (BaseFireBlock.canBePlacedAt(level, pos, direction))
            {
                level.setBlock(pos, BaseFireBlock.getState(level, pos), 11);
                return true;
            }
        }
        return cancelled;
    }

    private final Level world;
    private final BlockPos pos;
    private final BlockState state;
    private final Direction direction;
    private final @Nullable Player player;
    private final ItemStack stack;
    private final FireStrength strength;

    private StartFireEvent(Level world, BlockPos pos, BlockState state, Direction direction, @Nullable Player player, ItemStack stack, FireStrength strength)
    {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.direction = direction;
        this.player = player;
        this.stack = stack;
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

    /**
     * @return If the fire starting was <strong>strong</strong>, and is likely to cause destructive behavior like lighting fires.
     */
    public boolean isStrong()
    {
        return strength == FireStrength.STRONG;
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
        WEAK
        // More granularity may be added if needed
    }
}
