package org.zoomdev.zoom.common.utils;


import org.zoomdev.zoom.common.caster.Caster;

import java.util.List;

public class DataList {

    final List src;

    public DataList(List src) {
        this.src = src;
    }

    public double getDouble(int index) {
        return Caster.to(src.get(index), double.class);
    }

    public int getInt(int index) {
        return Caster.to(src.get(index), int.class);
    }

    public String getString(int index) {
        return Caster.to(src.get(index), String.class);
    }

    public int size() {
        return src.size();
    }


}
