package net.dries007.tfc.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.ThatchBedBlock;

/**
 * This exists due to problems in handling right click events
 * Forge provides a right click block event. This works for intercepting would-be calls to {@link net.minecraft.block.BlockState#use(World, PlayerEntity, Hand, BlockRayTraceResult)}
 * However, this cannot be used (maintaining vanilla behavior) for item usages, or calls to {@link net.minecraft.item.ItemStack#onItemUse(ItemUseContext, Function)}, as the priority of those two behaviors are very different (blocks take priority, cancelling the event with an item behavior forces the item to take priority
 *
 * This is in lieu of a system such as https://github.com/MinecraftForge/MinecraftForge/pull/6615
 */
public final class InteractionManager
{
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final Map<Predicate<ItemStack>, Action> ACTIONS = new HashMap<>();

    static
    {
        register(stack -> TFCTags.Items.THATCH_BED_HIDES.contains(stack.getItem()), (stack, context) -> {
            final World world = context.getLevel();
            final PlayerEntity player = context.getPlayer();
            if (!world.isClientSide() && player != null)
            {
                final BlockPos basePos = context.getClickedPos();
                final Direction facing = context.getHorizontalDirection();
                final BlockState bed = TFCBlocks.THATCH_BED.get().defaultBlockState();
                for (Direction direction : new Direction[] {facing, facing.getClockWise(), facing.getOpposite(), facing.getCounterClockWise()})
                {
                    final BlockPos headPos = basePos.relative(direction, 1);
                    if (world.getBlockState(basePos).is(TFCTags.Blocks.THATCH_BED_THATCH) && world.getBlockState(headPos).is(TFCTags.Blocks.THATCH_BED_THATCH))
                    {
                        final BlockPos playerPos = player.blockPosition();
                        if (playerPos != headPos && playerPos != basePos)
                        {
                            world.setBlock(basePos, bed.setValue(ThatchBedBlock.PART, BedPart.FOOT).setValue(ThatchBedBlock.FACING, direction), 16);
                            world.setBlock(headPos, bed.setValue(ThatchBedBlock.PART, BedPart.HEAD).setValue(ThatchBedBlock.FACING, direction.getOpposite()), 16);
                            stack.shrink(1);
                            return ActionResultType.SUCCESS;
                        }

                    }
                }
            }
            return ActionResultType.FAIL;
        });

        // todo: hide tag right click -> generic scraping recipe
        // todo: knapping tags
        // todo: log piles
        // todo: charcoal piles
    }

    public static void register(Predicate<ItemStack> predicate, Action action)
    {
        ACTIONS.put(predicate, action);
    }

    public static Optional<ActionResultType> onItemUse(ItemStack stack, ItemUseContext context)
    {
        if (!ACTIVE.get())
        {
            ACTIVE.set(true);
            for (Map.Entry<Predicate<ItemStack>, Action> entry : ACTIONS.entrySet())
            {
                if (entry.getKey().test(stack))
                {
                    return Optional.of(entry.getValue().onItemUse(stack, context));
                }
            }
            ACTIVE.set(false);
        }
        return Optional.empty();
    }

    public interface Action
    {
        ActionResultType onItemUse(ItemStack stack, ItemUseContext context);
    }
}
