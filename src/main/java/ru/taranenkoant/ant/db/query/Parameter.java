package ru.taranenkoant.ant.db.query;

import java.util.Objects;

/**
 * {@code @author:} TaranenkoAnt
 * {@code @createDate:} 02.01.2024
 */
final class Parameter {

    private final int index;
    private final String key;
    private final Object value;

    public Parameter(int index, String key, Object value) {
        this.index = index;
        this.key = key;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return index == parameter.index
                && Objects.equals(key, parameter.key)
                && Objects.equals(value, parameter.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, key, value);
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "index=" + index +
                ", key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
