package pers.mihao.ancient_empire.common.util;

import java.io.Serializable;

/**
 * @Author mihao
 * @Date 2021/5/17 23:22
 */
public class Pair<K, V> implements Serializable {

    private K key;
    private V value;

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.key + "=" + this.value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 31 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Pair)) {
            return false;
        } else {
            Pair pair = (Pair) o;
            if (this.key != null) {
                if (!this.key.equals(pair.key)) {
                    return false;
                }
            } else if (pair.key != null) {
                return false;
            }

            if (this.value != null) {
                if (!this.value.equals(pair.value)) {
                    return false;
                }
            } else if (pair.value != null) {
                return false;
            }

            return true;
        }
    }
}
