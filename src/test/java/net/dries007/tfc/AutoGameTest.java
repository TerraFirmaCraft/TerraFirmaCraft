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

import net.minecraft.gametest.framework.GameTestHelper;

/**
 * An annotation to specify a method that will be picked up and automatically executed through {@link TestAssertions#unitTestGenerator()}
 * These methods do not invoke typical {@link net.minecraft.gametest.framework.GameTest} functionality such as setting up success / failure conditions, and instead mimic JUnit tests with assertions, except with the requirement of a loaded world and registry content.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoGameTest {}
