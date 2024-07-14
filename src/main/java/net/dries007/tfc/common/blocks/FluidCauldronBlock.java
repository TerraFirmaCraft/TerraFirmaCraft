/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Collections;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.CauldronInteractions;

/**
 * A cauldron block that holds a single fluid, and interacts with any fluid-capable item.
 */
public class FluidCauldronBlock extends AbstractCauldronBlock
{
    public FluidCauldronBlock(Properties properties)
    {
        super(properties, Collections.emptyMap());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        return FluidHelpers.transferBetweenBlockHandlerAndItem(stack, CauldronInteractions.createFluidHandler(level, pos), level, pos, FluidHelpers.with(player, hand))
            ? ItemInteractionResult.SUCCESS
            : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public boolean isFull(BlockState state)
    {
        return true;
    }
}
