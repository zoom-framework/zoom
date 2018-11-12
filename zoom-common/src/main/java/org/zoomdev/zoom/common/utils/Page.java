package org.zoomdev.zoom.common.utils;

import java.util.List;

public class Page<T> {

    private List<T> list;
    private int size;
    private int page;
    private int total;

    public Page() {

    }

    public Page(List<T> list, int page, int size, int total) {
        this();
        this.list = list;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return String.format("Page(page:%d,total:%d,size:%d,list:%d)", page, total, size, list.size());
    }


}
