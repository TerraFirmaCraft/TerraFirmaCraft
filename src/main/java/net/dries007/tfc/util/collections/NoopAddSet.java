/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.collections;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;

public class NoopAddSet<E> extends AbstractSet<E>
{
    @Override public Iterator<E> iterator() { return Collections.emptyIterator(); }
    @Override public int size() { return 0; }
    @Override public boolean add(E e) { return false; }
}
