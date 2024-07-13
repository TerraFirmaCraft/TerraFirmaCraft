/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.wood.BranchDirection;

public class AxeLoggingHelper
{
    private static final EnumProperty<BranchDirection> BRANCH_DIRECTION = TFCBlockStateProperties.BRANCH_DIRECTION;

    public static boolean shouldLog(LevelAccessor level, BlockPos pos, BlockState state, ItemStack stack)
    {
        return isLoggingAxe(stack)
            && isLoggingBlock(state)
            && !isPartOfLargerTrunk(level, pos, state);
    }

    public static void doLogging(LevelAccessor level, BlockPos pos, Player player, ItemStack axe)
    {
        final boolean inefficient = Helpers.isItem(axe, TFCTags.Items.INEFFICIENT_LOGGING_AXES);
        for (BlockPos log : findLogs(level, pos))
        {
            level.destroyBlock(log, !inefficient || level.getRandom().nextFloat() < 0.6f, player);
            Helpers.damageItem(axe, player, InteractionHand.MAIN_HAND);
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

        Collections.reverse(logs);
        return logs;
    }

    public static boolean isLoggingAxe(ItemStack stack)
    {
        return Helpers.isItem(stack.getItem(), TFCTags.Items.AXES_THAT_LOG);
    }

    public static boolean isLoggingBlock(BlockState state)
    {
        return Helpers.isBlock(state.getBlock(), TFCTags.Blocks.LOGS_THAT_LOG) && state.hasProperty(BRANCH_DIRECTION) && state.getValue(BRANCH_DIRECTION).natural();
    }

    public static boolean isLoggingTrunk(BlockState state)
    {
        return isLoggingBlock(state) && state.getValue(BRANCH_DIRECTION).trunk();
    }

    /**
     * @return {@code true} if the log at {@code pos} and {@code state} is a trunk block, and is connected in an adjacent direction to at least one other trunk.
     */
    public static boolean isPartOfLargerTrunk(LevelAccessor level, BlockPos pos, BlockState state)
    {
        return switch (state.getValue(BRANCH_DIRECTION))
        {
            case TRUNK_NORTH_EAST -> isPartOfLargerTrunk(level, pos, Direction.NORTH, Direction.EAST);
            case TRUNK_NORTH_WEST -> isPartOfLargerTrunk(level, pos, Direction.NORTH, Direction.WEST);
            case TRUNK_SOUTH_EAST -> isPartOfLargerTrunk(level, pos, Direction.SOUTH, Direction.EAST);
            case TRUNK_SOUTH_WEST -> isPartOfLargerTrunk(level, pos, Direction.SOUTH, Direction.WEST);
            default -> false;
        };
    }

    private static boolean isPartOfLargerTrunk(LevelAccessor level, BlockPos pos, Direction first, Direction second)
    {
        return isLoggingTrunk(level.getBlockState(pos.relative(first))) || isLoggingTrunk(level.getBlockState(pos.relative(second)));
    }

    private static boolean isConnected(BlockPos rootPos, BlockPos branchPos, BlockState branchState)
    {
        if (branchState.hasProperty(BRANCH_DIRECTION))
        {
            final BranchDirection branch = branchState.getValue(BRANCH_DIRECTION);
            return branch.connected(rootPos, branchPos);
        }
        return false;
    }
}
