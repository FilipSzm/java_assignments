package uj.java.map2d;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Map2DImplementation<R, C, V> implements Map2D<R, C, V> {
    Map<R, Map<C, V>> map2D;

    enum TypeOfView {
        ROW,
        COLUMN
    }

    private void createMap() {
        map2D = new HashMap<>();
    }

    public Map2DImplementation() {
        createMap();
    }

    @Override
    public V put(R rowKey, C columnKey, V value) {
        if (rowKey == null || columnKey == null) throw new NullPointerException();
        V prevValue = get(rowKey, columnKey);
        map2D.computeIfAbsent(rowKey, k -> new HashMap<>());
        map2D.get(rowKey).put(columnKey, value);
        return prevValue;
    }

    @Override
    public V get(R rowKey, C columnKey) {
        return getOrDefault(rowKey, columnKey, null);
    }

    @Override
    public V getOrDefault(R rowKey, C columnKey, V defaultValue) {
        if (map2D.get(rowKey) == null)
            return defaultValue;
        V value = map2D.get(rowKey).get(columnKey);
        if (value == null)
            return defaultValue;
        return value;
    }

    @Override
    public V remove(R rowKey, C columnKey) {
        V value = get(rowKey, columnKey);
        var subMap = map2D.get(rowKey);
        if (subMap != null) {
            subMap.remove(columnKey);
            if (subMap.isEmpty())
                map2D.remove(rowKey);
        }
        return  value;
    }

    @Override
    public boolean isEmpty() {
        return map2D.isEmpty();
    }

    @Override
    public boolean nonEmpty() {
        return !isEmpty();
    }

    @Override
    public int size() {
        int size = 0;
        for (var m : map2D.values())
            size += m.size();
        return size;
    }

    @Override
    public void clear() {
        map2D.clear();
    }

    @Override
    public Map<C, V> rowView(R rowKey) {
        if (rowKey == null || !hasRow(rowKey))
            return Collections.unmodifiableMap(new HashMap<>());
        return Map.copyOf(map2D.get(rowKey));
    }

    @Override
    public Map<R, V> columnView(C columnKey) {
        var columnMap = new HashMap<R, V>();
        for (var entry : map2D.entrySet()) {
            V value = entry.getValue().get(columnKey);
            if (value != null)
                columnMap.put(entry.getKey(), value);
        }
        return Collections.unmodifiableMap(columnMap);
    }

    @Override
    public boolean hasValue(V value) {
        for (var m : map2D.values())
            if (m.containsValue(value))
                return true;
        return false;
    }

    @Override
    public boolean hasKey(R rowKey, C columnKey) {
        if (hasRow(rowKey))
            return map2D.get(rowKey).containsKey(columnKey);
        return false;
    }

    @Override
    public boolean hasRow(R rowKey) {
        return map2D.containsKey(rowKey);
    }

    @Override
    public boolean hasColumn(C columnKey) {
        for (var m : map2D.values())
            if (m.containsKey(columnKey))
                return true;
        return false;
    }

    @Override
    public Map<R, Map<C, V>> rowMapView() {
        return Collections.unmodifiableMap(copyToView(TypeOfView.ROW));
    }

    @Override
    public Map<C, Map<R, V>> columnMapView() {
        return Collections.unmodifiableMap(copyToView(TypeOfView.COLUMN));
    }

    @Override
    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey) {
        target.putAll(rowView(rowKey));
        return this;
    }

    @Override
    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey) {
        target.putAll(columnView(columnKey));
        return this;
    }

    @Override
    public Map2D<R, C, V> putAll(Map2D<? extends R, ? extends C, ? extends V> source) {
        var rowMapView = source.rowMapView();
        for (var entry : rowMapView.entrySet()) {
            map2D.putIfAbsent(entry.getKey(), new HashMap<>());
            map2D.get(entry.getKey()).putAll(entry.getValue());
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToRow(Map<? extends C, ? extends V> source, R rowKey) {
        map2D.get(rowKey).putAll(source);
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToColumn(Map<? extends R, ? extends V> source, C columnKey) {
        for (var entry : source.entrySet())
            put(entry.getKey(), columnKey, entry.getValue());
        return this;
    }

    @Override
    public <R2, C2, V2> Map2D<R2, C2, V2> copyWithConversion(Function<? super R, ? extends R2> rowFunction, Function<? super C, ? extends C2> columnFunction, Function<? super V, ? extends V2> valueFunction) {
        Map2D<R2, C2, V2> convertedMap = new Map2DImplementation<>();
        for (var entry : map2D.entrySet()) {
            for (var subEntry : entry.getValue().entrySet()) {
                convertedMap.put(
                        rowFunction.apply(entry.getKey()),
                        columnFunction.apply(subEntry.getKey()),
                        valueFunction.apply(subEntry.getValue())
                );
            }
        }
        return convertedMap;
    }

    @SuppressWarnings("all")
    private <T1, T2> Map<T1, Map<T2, V>> copyToView(TypeOfView type) {
        Map<T1, Map<T2, V>> view = new HashMap<>();
        for (var entry : map2D.entrySet())
            for (var subEntry : entry.getValue().entrySet()) {
                if (type == TypeOfView.ROW) {
                    view.putIfAbsent((T1) entry.getKey(), new HashMap<>());
                    view.get(entry.getKey()).put((T2) subEntry.getKey(), subEntry.getValue());
                } else {
                    view.putIfAbsent((T1) subEntry.getKey(), new HashMap<>());
                    view.get(subEntry.getKey()).put((T2) entry.getKey(), subEntry.getValue());
                }
            }
        return view;
    }
}
