package net.dries007.tfc;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TFCEventFactory
{
    /**
     * This starts a fire on the passed in face if the event is not intercepted. See {@link StartFireEvent} for usage considerations.
     * @return Whether a fire block was placed or not
     */
    public static boolean startFire(World world, BlockPos pos, BlockState state, Direction direction, @Nullable PlayerEntity player, @Nullable ItemStack stack)
    {
        boolean isExecuted = !MinecraftForge.EVENT_BUS.post(new StartFireEvent(world, pos, state, direction, player, stack));
        pos = pos.relative(direction);
        if (isExecuted && AbstractFireBlock.canBePlacedAt(world, pos, direction))
        {
            world.setBlock(pos, AbstractFireBlock.getState(world, pos), 11);
            return true;
        }
        return false;
    }
}
