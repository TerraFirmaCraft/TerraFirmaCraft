/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

/**
 * This is a test framework for game tests. It is similar to the existing {@link GameTestHelper} but with a few changes:
 * <ul>
 *     <li>Firstly, we can extend and modify this as needed, to add additional specific helper methods, unlike {@link GameTestHelper}</li>
 *     <li>Second, this uses a thread-local {@code helper}, meaning we don't need to explicitly access the helper through game test methods</li>
 *     <li>Third, the architecture of {@link PositionContext} means that all helper methods can be accessed through integer coordinates, or {@link BlockPos}, reducing the number of overloads that need to be written, and allowing more concise tests</li>
 * </ul>
 * In order to use this with {@link MyTest}, simply use the same generator, except with {@link #testGenerator()} instead. This class is meant
 * to be statically imported, much like JUnit or other assertions classes, which puts all the methods into the test classes namespace for easy access.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class GameTestAssertions
{
    private static final ThreadLocal<GameTestHelper> HELPER = ThreadLocal.withInitial(() -> null);
    private static final @Nullable String PASS = null;

    public static GameTestHelper helper()
    {
        return HELPER.get();
    }

    public static Collection<TestFunction> testGenerator()
    {
        return TestAssertions.testGenerator(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
    }

    /**
     * Executes {@code action} every tick, and if it ever executes without raising an assertion, it will succeed.
     */
    public static void succeedWhen(Runnable action)
    {
        helper().succeedWhen(with(action));
    }

    /**
     * Immediately causes the test to pass. Typically used when checking a condition at a specific time.
     */
    public static void succeed()
    {
        helper().succeed();
    }

    /**
     * Runs {@code action} after {@code delay} ticks have passed.
     */
    public static void runAfterDelay(int delay, Runnable action)
    {
        helper().runAfterDelay(delay, with(action));
    }

    public static PositionContext at(int x, int y, int z)
    {
        return at(new BlockPos(x, y, z));
    }

    public static PositionContext at(BlockPos pos)
    {
        return new PositionContext(pos);
    }

    public record PositionContext(BlockPos pos)
    {
        public BlockEntity getBlockEntity()
        {
            return helper().getBlockEntity(pos);
        }

        @SuppressWarnings("unchecked")
        public <T extends BlockEntity> T getBlockEntity(BlockEntityType<T> type)
        {
            is(type); // Check the block entity type
            return (T) helper().getBlockEntity(pos);
        }

        public BlockState getBlockState()
        {
            return helper().getBlockState(pos);
        }

        // =============================== Actions ============================ //

        public PositionContext spawnItem(Item item)
        {
            return spawnItem(new ItemStack(item));
        }

        public PositionContext spawnItem(Item item, int count)
        {
            return count > 0 ? spawnItem(new ItemStack(item, count)) : this;
        }

        public PositionContext spawnItem(ItemStack stack)
        {
            final GameTestHelper helper = helper();
            final ServerLevel level = helper.getLevel();
            final BlockPos absPos = helper.absolutePos(pos);
            final ItemEntity entity = new ItemEntity(level, absPos.getX() + 0.5, absPos.getY() + 0.5, absPos.getZ() + 0.5, stack);
            entity.setDeltaMovement(0, 0, 0);
            level.addFreshEntity(entity);
            return this;
        }

        public PositionContext setBlock(Block block)
        {
            helper().setBlock(pos, block);
            return this;
        }

        public PositionContext destroyBlock()
        {
            helper().destroyBlock(pos);
            return this;
        }

        public PositionContext pullLever()
        {
            helper().pullLever(pos);
            return this;
        }

        // ============================= Predicates ================================== //

        public PositionContext isAir()
        {
            return is(Blocks.AIR);
        }

        public PositionContext is(Block block)
        {
            return is(stateIn -> stateIn.getBlock() == block ? PASS : "Expected " + block.getName().getString() + " Got " + stateIn);
        }

        public PositionContext is(BlockState state)
        {
            return is(stateIn -> stateIn == state ? PASS : "Expected " + state + " Got " + stateIn);
        }

        public PositionContext is(Predicate<BlockState> state, String message)
        {
            return is(stateIn -> state.test(stateIn) ? PASS : message);
        }

        public <T extends Comparable<T>> PositionContext is(Property<T> property, T value)
        {
            return is(stateIn -> stateIn.hasProperty(property) && stateIn.getValue(property) == value ? PASS : "Expected property " + property.getName() + " to be " + value + " on block " + stateIn);
        }

        private PositionContext is(ErrorPredicate<BlockState> predicate)
        {
            final GameTestHelper helper = helper();
            final @Nullable String error = predicate.test(helper.getBlockState(pos));
            if (error != null)
            {
                throw new GameTestAssertPosException(error, helper.absolutePos(pos), pos, helper.getTick());
            }
            return this;
        }

        public <T extends BlockEntity> PositionContext is(BlockEntityType<T> type)
        {
            return is(type, entity -> true, "");
        }

        @SuppressWarnings({"unchecked", "deprecation"})
        public <T extends BlockEntity> PositionContext is(BlockEntityType<T> type, Predicate<T> predicate, String message)
        {
            final GameTestHelper helper = helper();
            final @Nullable BlockEntity entity = helper.getBlockEntity(pos);
            if (entity == null || entity.getType() != type)
            {
                throw new GameTestAssertPosException("Expected block entity " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type) + " Got " + helper.getBlockState(pos), helper.absolutePos(pos), pos, helper.getTick());
            }
            if (!predicate.test((T) entity))
            {
                throw new GameTestAssertPosException("Expected " + message + " Got " + helper.getBlockState(pos) + " with nbt " + entity.saveWithoutMetadata(), helper.absolutePos(pos), pos, helper.getTick());
            }
            return this;
        }

        public PositionContext itemEntityIsPresent(Item item, int exactCount, double radius)
        {
            helper().assertItemEntityCountIs(item, pos, radius, exactCount);
            return this;
        }

        public PositionContext itemEntityIsPresent(Item item, double radius)
        {
            helper().assertItemEntityPresent(item, pos, radius);
            return this;
        }
    }

    static void setHelper(GameTestHelper helper)
    {
        HELPER.set(helper);
    }

    /**
     * Binds the current {@link GameTestHelper} to the given {@code action}, so it can run later from the correct context.
     */
    private static Runnable with(Runnable action)
    {
        final GameTestHelper helper = helper();
        return () -> {
            setHelper(helper);
            action.run();
        };
    }

    @FunctionalInterface
    interface ErrorPredicate<T>
    {
        @Nullable String test(T t);
    }
}
