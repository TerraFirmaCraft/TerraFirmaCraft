package net.dries007.tfc;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import net.dries007.tfc.common.items.FirestarterItem;
import net.dries007.tfc.util.InteractionManager;

@Cancelable
public class StartFireEvent extends Event
{
    private final World world;
    private final BlockPos pos;
    private final BlockState state;
    private final Direction direction;
    private final PlayerEntity player;
    private final ItemStack stack;

    /**
     * This event is used for lighting fires or optionally lightable blocks. If it's not cancelled, TFC will try to place a fire block.
     * This should always be called via {@link TFCEventFactory#startFire(World, BlockPos, BlockState, Direction, PlayerEntity, ItemStack)}
     * as that is what contains the logic to spawn the fire block.
     *
     * For things like flint and steel that don't require special mechanics, this event logic is handled for you in
     * {@link InteractionManager#setup()}. Adding items to the tag #starts_fires_with_items or #starts_fires_with_durability
     * will emulate this behavior (and allow TFC devices to be lit by your item)
     *
     * If a greater degree of control is needed, post the event using the method in the Event Factory.
     * See {@link FirestarterItem} for how this works.
     *
     * Note that the parameters of this event are the same as those expected by an item use, ie that the position, state, and direction
     * reflect what would happen if a block was clicked on. Direction refers to the face that was clicked.
     */
    public StartFireEvent(World world, BlockPos pos, BlockState state, Direction direction, @Nullable PlayerEntity player, ItemStack stack)
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
