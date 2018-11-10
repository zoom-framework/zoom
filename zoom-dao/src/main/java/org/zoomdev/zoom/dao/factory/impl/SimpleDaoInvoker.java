package org.zoomdev.zoom.dao.factory.impl;

import org.zoomdev.zoom.dao.Ar;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.factory.DaoInvoker;
import org.zoomdev.zoom.dao.factory.DaoParameter;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.impl.ZoomIocKey;

public class SimpleDaoInvoker implements DaoInvoker {
    private DaoParameter[] parameters;


    /// Dao 配置名称
    private String name;

    /// ioc
    private IocContainer ioc;

    // 最后结果的type
    private Class<?> type;

    /// 默认的表
    private String table;

    //join信息

    public SimpleDaoInvoker(DaoParameter[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public Object invoke(Object[] args) {
        Dao dao = (Dao) ioc.get(new ZoomIocKey(name,Dao.class)).get();
        Ar ar = dao.ar();
        int i=0;
        for(DaoParameter parameter : parameters){
            parameter.handle(ar,args[i]);
            ++i;
        }
        return null;
    }
}
