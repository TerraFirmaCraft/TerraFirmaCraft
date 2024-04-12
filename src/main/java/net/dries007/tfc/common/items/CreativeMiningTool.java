/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * An interface to mark an item that has special {@link Item#mineBlock(ItemStack, Level, BlockState, BlockPos, LivingEntity) Item#mineBlock} behavior to also apply that behavior in creative mode.
 * <p>
 * In {@link net.minecraft.server.level.ServerPlayerGameMode}, {@code mineBlock} is only called if the player is not in creative. Via mixin,
 * we invoke {@link #mineBlockInCreative(ItemStack, Level, BlockState, BlockPos, Player) mineBlockInCreative} in the other case, and allow the item to handle this behavior separately.
 */
public interface CreativeMiningTool
{
    /**
     * Arguments are identical to what would be passed to {@link Item#mineBlock(ItemStack, Level, BlockState, BlockPos, LivingEntity)},
     * except we know the entity (a player) is in creative mode.
     */
    void mineBlockInCreative(ItemStack stack, Level level, BlockState state, BlockPos pos, Player player);
}
