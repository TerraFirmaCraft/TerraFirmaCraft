/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a test that is picked up by {@link TestAssertions#testGenerator()}. The containing class must have a method which returns this generator in order for this test to be registered correctly:
 * <pre>{@code
 * @GameTestGenerator
 * public Collection<TestFunction> generator()
 * {
 *     return TestAssertions.testGenerator();
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyTest
{
    /**
     * @return A template structure to use for the test. The {@code tfc} namespace will be used if not specified.
     */
    String structure() default "empty";

    /**
     * @return {@code true} if this test is a unit test. A unit test will fail by throwing an {@link AssertionError} via one of the methods in {@link TestAssertions}.
     */
    boolean unitTest() default false;

    /**
     * @return A number of ticks that this test must succeed before, otherwise it will be considered failed.
     */
    int timeoutTicks() default 100;

    /**
     * @return A number of ticks that this test must exist and run before success conditions are checked, effectively a setup time.
     */
    int setupTicks() default 0;
}
