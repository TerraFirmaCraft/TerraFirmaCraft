/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

import java.util.function.Supplier;

/**
 * @see AreaFactory
 */
public interface TypedAreaFactory<A> extends Supplier<TypedArea<A>> {}
