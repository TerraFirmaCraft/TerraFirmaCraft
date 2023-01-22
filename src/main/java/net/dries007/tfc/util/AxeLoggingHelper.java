/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.wood.BranchDirection;

public class AxeLoggingHelper
{
    private static final BooleanProperty NATURAL = TFCBlockStateProperties.NATURAL;

    public static void doLogging(LevelAccessor level, BlockPos pos, Player player, ItemStack axe)
    {
        final boolean inefficient = Helpers.isItem(axe, TFCTags.Items.INEFFICIENT_LOGGING_AXES);
        for (BlockPos log : findLogs(level, pos))
        {
            level.destroyBlock(log, !inefficient || level.getRandom().nextFloat() < 0.6f, player);
            axe.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            if (axe.isEmpty())
            {
                return; // stop breaking if the axe is broken
            }
        }
    }

    public static List<BlockPos> findLogs(LevelAccessor level, BlockPos pos)
    {
        final Set<BlockPos> seen = new HashSet<>(64);
        final List<BlockPos> logs = new ArrayList<>(16);
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        logs.add(pos);
        for (int i = 0; i < logs.size(); i++)
        {
            final BlockPos log = logs.get(i);
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dy = -1; dy <= 1; dy++)
                {
                    for (int dz = -1; dz <= 1; dz++)
                    {
                        cursor.setWithOffset(log, dx, dy, dz);
                        if (!seen.contains(cursor))
                        {
                            final BlockPos cursorPos = cursor.immutable();
                            final BlockState cursorState = level.getBlockState(cursorPos);

                            if (isLoggingBlock(cursorState))
                            {
                                if (isConnected(log, cursorPos, cursorState))
                                {
                                    logs.add(cursorPos);
                                    seen.add(cursorPos); // For connected logs, mark them as seen as we add them to the queue
                                }
                                // But for non-connected blocks, don't mark it as seen, as we might need to check this again from another angle
                            }
                            else
                            {
                                // Mark non-logging blocks as seen, so we don't re-check them
                                seen.add(cursorPos);
                            }
                        }
                    }
                }
            }
        }

        // Sort the list in terms of max distance to the original tree
        logs.sort(Comparator.comparing(x -> -x.distSqr(pos)));
        return logs;
    }

    public static boolean isLoggingAxe(ItemStack stack)
    {
        return Helpers.isItem(stack.getItem(), TFCTags.Items.AXES_THAT_LOG);
    }

    public static boolean isLoggingBlock(BlockState state)
    {
        return Helpers.isBlock(state.getBlock(), TFCTags.Blocks.LOGS_THAT_LOG) && (!state.hasProperty(NATURAL) || state.getValue(NATURAL));
    }

    private static boolean isConnected(BlockPos rootPos, BlockPos branchPos, BlockState branchState)
    {
        if (branchState.hasProperty(TFCBlockStateProperties.BRANCH_DIRECTION))
        {
            final BranchDirection branch = branchState.getValue(TFCBlockStateProperties.BRANCH_DIRECTION);
            return branch.connected(rootPos, branchPos);
        }
        return true; // Default to assuming that all blocks are connected
    }
}
