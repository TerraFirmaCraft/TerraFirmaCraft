/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Contract;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;


public final class TestAssertions
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AssertionModel ASSERTIONS;

    static
    {
        if (detectJUnitAssertions())
        {
            LOGGER.info("Using JUnit Assertions");
            ASSERTIONS = new AssertionModel(
                Assertions::assertEquals,
                Assertions::assertNotEquals,
                Assertions::assertNotNull,
                Assertions::assertTrue,
                Assertions::assertFalse
            );
        }
        else
        {
            LOGGER.info("Using TFC Assertions");
            ASSERTIONS = new AssertionModel(
                AssertionsImpl::assertEquals,
                AssertionsImpl::assertNotEquals,
                AssertionsImpl::assertNotNull,
                AssertionsImpl::assertTrue,
                AssertionsImpl::assertFalse
            );
        }
    }

    private static boolean detectJUnitAssertions()
    {
        try
        {
            Class.forName("org.junit.jupiter.api.Assertions");
            return true;
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }

    public static Collection<TestFunction> testGenerator()
    {
        try
        {
            final Class<?> clazz = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
            final List<TestFunction> functions = new ArrayList<>();
            final String className = clazz.getSimpleName();
            final Object instance = clazz.getDeclaredConstructor().newInstance();
            for (Method method : clazz.getDeclaredMethods())
            {
                if (method.isAnnotationPresent(MyTest.class))
                {
                    final MyTest annotation = method.getAnnotation(MyTest.class);
                    final String methodName = method.getName();

                    final Consumer<GameTestHelper> action = helper -> {
                        try
                        {
                            method.invoke(instance, helper);
                        }
                        catch (InvocationTargetException e)
                        {
                            if (e.getTargetException() instanceof AssertionError ae)
                            {
                                throw ae;
                            }
                            if (e.getTargetException() instanceof GameTestAssertException ae)
                            {
                                throw ae;
                            }
                            throwAsUnchecked(e);
                        }
                        catch (IllegalAccessException e)
                        {
                            throwAsUnchecked(e);
                        }
                    };

                    final Consumer<GameTestHelper> testAction = annotation.unitTest() ? asUnitTest(className, methodName, action) : action;

                    functions.add(new TestFunction("default", className + '.' + methodName, Helpers.identifier(annotation.structure()).toString(), annotation.timeoutTicks(), annotation.setupTicks(), true, testAction));
                }
            }
            functions.sort(Comparator.comparing(TestFunction::getTestName));
            return functions;
        }
        catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
        {
            return throwAsUnchecked(e);
        }
    }

    /**
     * Invoked from a {@link net.minecraft.gametest.framework.GameTest} method.
     * Treats the contents as a JUnit unit test, where an assertion failing indicates a failing test, otherwise the test succeeds.
     */
    public static Consumer<GameTestHelper> asUnitTest(String className, String methodName, Consumer<GameTestHelper> action)
    {
        return helper -> {
            try
            {
                action.accept(helper);
                helper.succeed();
            }
            catch (AssertionError e)
            {
                LOGGER.error("AutoGameTest {}.{}() failed: {}", className, methodName, e.getMessage());
                LOGGER.error("Error", e);
                helper.fail("Assertion Failed: " + e.getMessage());
            }
        };
    }

    // assertEquals() variants for vanilla types without equality()

    public static void assertEquals(FluidStack expected, FluidStack actual, String message)
    {
        assertEquals(wrap(expected), wrap(actual), message);
    }

    public static void assertEquals(FluidStack expected, FluidStack actual)
    {
        assertEquals(wrap(expected), wrap(actual));
    }

    public static void assertEquals(ItemStack expected, ItemStack actual, String message)
    {
        assertEquals(wrap(expected), wrap(actual), message);
    }

    public static void assertEquals(ItemStack expected, ItemStack actual)
    {
        assertEquals(wrap(expected), wrap(actual));
    }

    public static void assertEquals(Ingredient expected, Ingredient actual, String message)
    {
        assertEquals(wrap(expected), wrap(actual), message);
    }

    public static void assertEquals(Ingredient expected, Ingredient actual)
    {
        assertEquals(wrap(expected), wrap(actual));
    }

    public static void assertEquals(Recipe<?> expected, Recipe<?> actual, String message)
    {
        assertEquals(wrap(expected), wrap(actual), message);
    }

    public static void assertEquals(Recipe<?> expected, Recipe<?> actual)
    {
        assertEquals(wrap(expected), wrap(actual));
    }

    public static void assertEquals(ItemStackProvider expected, ItemStackProvider actual, String message)
    {
        assertEquals(wrap(expected), wrap(actual), message);
    }

    public static void assertEquals(ItemStackProvider expected, ItemStackProvider actual)
    {
        assertEquals(wrap(expected), wrap(actual));
    }

    // Bouncers for JUnit Assertions, if present.
    // Otherwise these use TFC Assertions in GameTest, where JUnit is not loaded.

    @Contract(value = "null, null, _ -> _; null, !null, _ -> fail; !null, null, _ -> fail", pure = true)
    public static void assertEquals(Object expected, Object actual, String message)
    {
        ASSERTIONS.assertEquals.accept(expected, actual, message);
    }

    @Contract(value = "null, null -> _; null, !null -> fail; !null, null -> fail", pure = true)
    public static void assertEquals(Object expected, Object actual)
    {
        assertEquals(expected, actual, "Expected " + expected + " to be equal to " + actual);
    }

    @Contract(value = "null, null, _ -> fail; null, !null, _ -> _; !null, null, _ -> _", pure = true)
    public static void assertNotEquals(Object expected, Object actual, String message)
    {
        ASSERTIONS.assertNotEquals.accept(expected, actual, message);
    }

    @Contract(value = "null, null -> fail; null, !null -> _; !null, null -> _", pure = true)
    public static void assertNotEquals(Object expected, Object actual)
    {
        assertNotEquals(expected, actual, "Expected " + expected + " to be not equal to " + actual);
    }

    @Contract(value = "null, _ -> fail;", pure = true)
    public static void assertNotNull(Object actual, String message)
    {
        ASSERTIONS.assertNotNull.accept(actual, message);
    }

    @Contract(value = "null -> fail;", pure = true)
    public static void assertNotNull(Object actual)
    {
        assertNotNull(actual, "Expected non null");
    }

    @Contract(value = "null, _ -> fail;false, _ -> fail", pure = true)
    public static void assertTrue(Boolean actual, String message)
    {
        ASSERTIONS.assertTrue.accept(actual, message);
    }

    @Contract(value = "null -> fail;false -> fail", pure = true)
    public static void assertTrue(Boolean actual)
    {
        assertTrue(actual, "Expected true");
    }

    @Contract(value = "null, _ -> fail;true, _ -> fail", pure = true)
    public static void assertFalse(Boolean actual, String message)
    {
        ASSERTIONS.assertFalse.accept(actual, message);
    }

    @Contract(value = "null -> fail;true -> fail", pure = true)
    public static void assertFalse(Boolean actual)
    {
        assertFalse(actual, "Expected false");
    }

    // Wrapper methods

    public static Type wrap(FluidStack stack)
    {
        return new Named<>(stack, "%d mB of %s".formatted(stack.getAmount(), stack.getFluid().getRegistryName()));
    }

    public static Type wrap(ItemStack stack)
    {
        record TItemStack(Item item, int count, CompoundTag tag) {}
        return new Named<>(new TItemStack(stack.getItem(), stack.getCount(), stack.getTag()), stack.toString() + (stack.getTag() != null ? stack.getTag() : ""));
    }

    public static Type wrap(Ingredient ingredient)
    {
        record TIngredient(Class<?> clazz, IIngredientSerializer<? extends Ingredient> serializer, List<Type> stacks) implements Type {}
        return new TIngredient(ingredient.getClass(), ingredient.getSerializer(), wrap(ingredient.getItems(), TestAssertions::wrap));
    }

    public static Type wrap(Recipe<?> recipe)
    {
        record TRecipe(Class<?> clazz, ResourceLocation id, String group, Type result, List<Type> ingredients) implements Type {}
        return new Named<>(new TRecipe(recipe.getClass(), recipe.getId(), recipe.getGroup(), wrap(recipe.getResultItem()), wrap(recipe.getIngredients(), TestAssertions::wrap)), "[Recipe " + recipe.getId() +  " of type " + recipe.getType() + " and serializer " + recipe.getSerializer().getRegistryName() + "]");
    }

    public static Type wrap(ItemStackProvider provider)
    {
        record TItemStackProvider(Type stack, ItemStackModifier[] modifiers) implements Type {}
        return new TItemStackProvider(wrap(provider.stack().get()), provider.modifiers());
    }

    private static <T> List<Type> wrap(T[] array, Function<T, Type> wrap)
    {
        return Arrays.stream(array).map(wrap).toList();
    }

    private static <T> List<Type> wrap(List<T> list, Function<T, Type> wrap)
    {
        return list.stream().map(wrap).toList();
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable, T> T throwAsUnchecked(Exception e) throws E
    {
        LOGGER.error("Unhandled Exception Thrown", e);
        throw (E) e;
    }

    @FunctionalInterface
    public interface Action
    {
        Object run(GameTestHelper helper) throws Exception;
    }

    interface Type {}

    record Named<T>(T t, String name) implements Type
    {
        @Override
        public String toString()
        {
            return name;
        }
    }

    @FunctionalInterface
    interface TriConsumer<A, B, C>
    {
        void accept(A first, B second, C third);
    }

    record AssertionModel(
        TriConsumer<Object, Object, String> assertEquals,
        TriConsumer<Object, Object, String> assertNotEquals,
        BiConsumer<Object, String> assertNotNull,
        BiConsumer<Boolean, String> assertTrue,
        BiConsumer<Boolean, String> assertFalse
    ) {}

    static class AssertionsImpl
    {
        static void assertEquals(Object expected, Object actual, String message)
        {
            if (!expected.equals(actual)) throw new AssertionError(message);
        }

        static void assertNotEquals(Object expected, Object actual, String message)
        {
            if (expected.equals(actual)) throw new AssertionError(message);
        }

        static void assertNotNull(Object actual, String message)
        {
            if (actual == null) throw new AssertionError(message);
        }

        static void assertTrue(Boolean actual, String message)
        {
            if (!actual) throw new AssertionError(message);
        }

        static void assertFalse(Boolean actual, String message)
        {
            if (actual) throw new AssertionError(message);
        }
    }
}
