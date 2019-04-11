package org.zoomdev.zoom.common.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class OrderedList<E> {

    private class OrderInfo {
        E element;
        int order;

        OrderInfo(E element, int order) {
            this.element = element;
            this.order = order;
        }
    }

    private List<OrderInfo> list;

    public OrderedList() {
        list = new LinkedList<OrderInfo>();
    }

    public void add(E element, int order) {
        list.add(new OrderInfo(element, order));
    }

    private int getMaxOrder() {
        final List<OrderInfo> list = this.list;
        if (list.size() == 0) {
            return 0;
        }
        int max = Integer.MIN_VALUE;
        for (OrderInfo orderInfo : list) {
            if (max < orderInfo.order) {
                max = orderInfo.order;
            }
        }
        return max;
    }

    public void addAll(E... elements) {
        int max = getMaxOrder();
        for (E e : elements) {
            list.add(new OrderInfo(e, ++max));
        }
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }


    public List<E> toList() {
        final List<OrderInfo> list = this.list;
        Collections.sort(list, comparator);
        return Collections.unmodifiableList(CollectionUtils.map(list, new Converter<OrderInfo, E>() {
            @Override
            public E convert(OrderInfo data) {
                return data.element;
            }
        }));
    }

    private Comparator comparator = new Comparator<OrderInfo>() {

        @Override
        public int compare(OrderInfo arg0, OrderInfo arg1) {
            if (arg0.order > arg1.order) {
                return 1;
            }
            if (arg0.order == arg1.order) {
                return 0;
            }
            return -1;
        }
    };

    public E[] toArray(E[] array) {
        assert (array != null && array.length == list.size());
        final List<OrderInfo> list = this.list;
        int c = list.size();
        Collections.sort(list, comparator);

        for (int i = 0; i < c; ++i) {
            OrderInfo info = list.get(i);
            array[i] = info.element;
        }
        return array;
    }

}
