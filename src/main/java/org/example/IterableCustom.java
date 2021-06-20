package org.example;
import lombok.NonNull;

import java.util.Iterator;

public class IterableCustom implements Iterable<Integer> {

    private Integer[] arrayList;
    private int currentSize;

    public IterableCustom(Integer[] newArray) {
        this.arrayList = newArray;
        this.currentSize = arrayList.length;
    }

    @Override
    @NonNull
    public Iterator<Integer> iterator() {
        Iterator<Integer> it = new Iterator<Integer>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < currentSize && arrayList[currentIndex] != null;
            }

            @Override
            public Integer next() {
                return arrayList[currentIndex++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }
}

interface Iterator2<T> {
    boolean hasNext();

    T next();
}

class IteratorImpl<T> implements Iterator2<T>{

    T[] arr;
    int arrLength;
    int next;

    public IteratorImpl(T[] arr) {
        this.arr = arr;
        this.arrLength = arr.length;
    }

    @Override
    public boolean hasNext() {
        return next < arrLength;
    }

    @Override
    public T next() {
        if (hasNext()) {
            ++next;               // return arr[next++]
            return arr[next - 1];
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }
}
