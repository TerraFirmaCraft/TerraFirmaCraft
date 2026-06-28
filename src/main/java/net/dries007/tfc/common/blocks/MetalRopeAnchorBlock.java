/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A free-standing, craftable rope anchor (e.g. a steel piton) that can be placed anywhere a rope could survive, as an
 * alternative to tying onto a rock spike. Unlike {@link RockRopeAnchorBlock}, this block persists in the world whether
 * or not a rope is attached, so it tracks that with {@link #HAS_ROPE}: it is placed bare, becomes roped when thrown,
 * and reverts to bare when its rope is recalled.
 */
public class MetalRopeAnchorBlock extends RopeAnchorBlock
{
    public static final BooleanProperty HAS_ROPE = TFCBlockStateProperties.HAS_ROPE;

    public MetalRopeAnchorBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any()
            .setValue(FLUID, FLUID.keyFor(Fluids.EMPTY))
            .setValue(HAS_ROPE, false));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        // Only react to an empty-handed click when there is actually a rope to recall.
        if (!state.getValue(HAS_ROPE))
        {
            return InteractionResult.PASS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected BlockState getStateAfterRemoval(BlockState state)
    {
        return state.setValue(HAS_ROPE, false);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_ROPE);
    }
}
