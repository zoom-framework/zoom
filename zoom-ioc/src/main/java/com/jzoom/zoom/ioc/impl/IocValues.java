package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.common.config.ConfigReader;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocKey;
import com.jzoom.zoom.ioc.IocObject;
import com.jzoom.zoom.ioc.IocValue;

import java.lang.reflect.Field;

public class IocValues {

    static class IocValueValue implements  IocValue{

        private IocObject value;

        IocValueValue(IocObject value){
            this.value = value;
        }

        @Override
        public IocObject getValue(IocContainer ioc, IocKey key) {
            return value;
        }
    }


    public static final IocValue VALUE = new IocValue() {
        @Override
        public IocObject getValue(IocContainer ioc, IocKey key) {
            return ioc.get(key);
        }
    };


    public static final IocValue createConfig (Field field,IocKey key){

       Object data =  ConfigReader.getDefault().get(key.getName());
       data = Caster.to(data,field.getType());

        return new IocValueValue(ZoomIocObject.wrap(null, data));
    }

}
