/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public class InsertOnlyEnumTable<R extends Enum<R>, C extends Enum<C>, T> implements Table<R, C, T>
{
    private final EnumMap<R, EnumMap<C, T>> rowMap;
    private final Class<C> colClass;

    public InsertOnlyEnumTable(Class<R> rowClass, Class<C> colClass)
    {
        if (rowClass == null || colClass == null) throw new NullPointerException();
        rowMap = new EnumMap<>(rowClass);
        this.colClass = colClass;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
    {
        if (rowKey == null || columnKey == null) return false;
        EnumMap<C, T> row = rowMap.get(rowKey);
        if (row == null) return false;
        return row.containsKey(columnKey);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean containsRow(@Nullable Object rowKey)
    {
        return rowMap.containsKey(rowKey);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean containsColumn(@Nullable Object columnKey)
    {
        return rowMap.values().stream().anyMatch(x -> x.containsKey(columnKey));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean containsValue(@Nullable Object value)
    {
        return rowMap.values().stream().anyMatch(x -> x.containsValue(value));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public T get(@Nullable Object rowKey, @Nullable Object columnKey)
    {
        if (rowKey == null || columnKey == null) return null;
        EnumMap<C, T> row = rowMap.get(rowKey);
        if (row == null) return null;
        return row.get(columnKey);
    }

    @Override
    public boolean isEmpty()
    {
        return rowMap.isEmpty() || rowMap.values().stream().allMatch(Map::isEmpty);
    }

    @Override
    public int size()
    {
        return rowMap.values().stream().mapToInt(Map::size).sum();
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public T put(R rowKey, C columnKey, T value)
    {
        EnumMap<C, T> row = rowMap.get(rowKey);
        if (row == null)
        {
            row = new EnumMap<>(colClass);
            row.put(columnKey, value);
            rowMap.put(rowKey, row);
        }
        else
        {
            if (row.containsKey(columnKey)) throw new UnsupportedOperationException();
            row.put(columnKey, value);
        }
        return null;
    }

    @Override
    public void putAll(Table<? extends R, ? extends C, ? extends T> table)
    {
        table.rowMap().forEach((row, x) -> x.forEach((col, val) -> put(row, col, val)));
    }

    @Nullable
    @Override
    public T remove(@Nullable Object rowKey, @Nullable Object columnKey)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableMap<C, T> row(R rowKey)
    {
        return ImmutableMap.copyOf(rowMap.get(rowKey));
    }

    @Override
    public ImmutableMap<R, T> column(C columnKey)
    {
        return rowMap.entrySet().stream().map(e -> new ImmutablePair<>(e.getKey(), e.getValue().get(columnKey))).collect(ImmutableMap.toImmutableMap(x -> x.a, x -> x.b));
    }

    @Override
    public ImmutableSet<Cell<R, C, T>> cellSet()
    {
        return rowMap.entrySet().stream().map(
            e1 -> e1.getValue().entrySet().stream().map(
                e2 -> Tables.immutableCell(e1.getKey(), e2.getKey(), e2.getValue())
            ).collect(Collectors.toSet())).flatMap(Set::stream).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ImmutableSet<R> rowKeySet()
    {
        return ImmutableSet.copyOf(rowMap.keySet());
    }

    @Override
    public ImmutableSet<C> columnKeySet()
    {
        return rowMap.values().stream().map(Map::keySet).flatMap(Set::stream).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ImmutableSet<T> values()
    {
        return rowMap.values().stream().map(Map::values).flatMap(Collection::stream).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ImmutableMap<R, Map<C, T>> rowMap()
    {
        return rowMap.entrySet().stream().map(e -> new ImmutablePair<>(e.getKey(), ImmutableMap.copyOf(e.getValue()))).collect(ImmutableMap.toImmutableMap(x -> x.a, x -> x.b));
    }

    @Override
    public ImmutableMap<C, Map<R, T>> columnMap()
    {
        return columnKeySet().stream().map(x -> new ImmutablePair<>(x, column(x))).collect(ImmutableMap.toImmutableMap(x -> x.a, x -> x.b));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public T get(@Nullable R rowKey, @Nullable C columnKey)
    {
        if (rowKey == null || columnKey == null) return null;
        EnumMap<C, T> row = rowMap.get(rowKey);
        if (row == null) return null;
        return row.get(columnKey);
    }
}
