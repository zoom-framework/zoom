package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.BeanTableAdapter;
import org.zoomdev.zoom.dao.BeanTableInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupBeanTableAdapter implements BeanTableAdapter {

    private List<BeanTableAdapter> adapters;

    public GroupBeanTableAdapter(BeanTableAdapter...adapters){
        this.adapters = new ArrayList<BeanTableAdapter>();
        Collections.addAll(this.adapters,adapters);
    }

    public void addBeanTableAdapter(BeanTableAdapter adapter){
        this.adapters.add(adapter);
    }

    @Override
    public BeanTableInfo getTableInfo(Class<?> type) {
        for(BeanTableAdapter adapter : adapters){
            BeanTableInfo info = adapter.getTableInfo(type);
            if(info!=null){
                return info;
            }
        }
        return null;
    }
}
