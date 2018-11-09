package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.common.annotations.Inject;
import com.jzoom.zoom.common.utils.CachedClasses;
import com.jzoom.zoom.ioc.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZoomIoc implements IocContainer, IocEventListener {

	private GlobalScope globalScope;

	private ZoomIocClassLoader iocClassLoader;

    private List<IocEventListener> eventListeners =
            Collections.synchronizedList(new ArrayList<IocEventListener>());


	public  ZoomIoc() {
        globalScope = new GlobalScope(this, this);
		this.iocClassLoader = new ZoomIocClassLoader();
		this.iocClassLoader.setClassEnhancer(new NoneEnhancer());
	}




	@Override
	public void destroy() {

		this.iocClassLoader.destroy();
		globalScope.destroy();

	}

	@Override
	public IocObject get(IocKey key) {
		return get(globalScope, key);
	}

	@Override
	public <T> T get(Class<?> type) {
		return (T) get(new ZoomIocKey(type)).get();
	}

	public synchronized IocObject get(IocScope scope, IocKey key) {
		try{
			IocObject obj = scope.get(key);
			IocClass iocClass = null;
			if (obj == null) {
				iocClass = this.iocClassLoader.get(key);
				if (iocClass == null) {
					if(!key.hasName() && !key.isInterface() && key.getClassLoader() == key.getType().getClassLoader()) {
						iocClass = iocClassLoader.append(key.getType());
					}else {
						throw new IocException("找不到IocClass key:" + key + " 请提供ioc的配置");
					}
				}
				obj = iocClass.newInstance(scope);
				if (obj == null) {
					throw new IocException("创建对象失败");
				}
			}

			if (!obj.isInitialized()) {
				obj.initialize();

				if (iocClass == null)
					iocClass = this.iocClassLoader.get(key);

				IocField[] fields = iocClass.getIocFields();
				if(fields!=null) {
					for (IocField field : fields) {
						field.set(obj, field.getValue().getValue(this,field.getKey()));
					}
				}

				IocMethod[] methods = iocClass.getIocMethods();
				if(methods!=null) {
					for (IocMethod method : methods) {
                        invokeMethod(obj, method);
                    }
                }
            }

            return obj;
        } catch (Throwable e) {
            throw new IocException("获取ioc对象失败" + key, e);
        }
    }


    public Object invokeMethod(IocObject obj, IocMethod method) {
		IocKey[] keys = method.getParameterKeys();
        IocObject[] values = new IocObject[keys.length];
        for (int i = 0, c = keys.length; i < c; ++i) {
            values[i] = get(keys[i]);
        }

        return method.invoke(obj, values);
    }

	static final Object[] EMPTY = new Object[0];
	static final IocKey[] EMPTY_KEYS =  new IocKey[0];


    public static Object[] getValues(IocObject... objects) {
		if (objects.length == 0) {
			return EMPTY;
		}
		Object[] values = new Object[objects.length];
		for (int i = 0, c = objects.length; i < c; ++i) {
			values[i] = objects[i].get();
		}
		return values;
	}

	public static IocKey[] parseParameterKeys(
			ClassLoader classLoader,
			Annotation[][] methodAnnotations,
			Class<?>[] types,
			IocClassLoader iocClassLoader) {
		int index = 0;
		IocKey[] args = new IocKey[types.length];
		IocKey arg;
		for (Class<?> type : types) {
			Inject paramInject = null;
			Annotation[] annotations = methodAnnotations[index];
			for (Annotation annotation : annotations) {
				if (annotation instanceof Inject) {
					paramInject = (Inject) annotation;
				}
			}
			if (paramInject != null && !StringUtils.isEmpty(paramInject.value())) {
				arg = new ZoomIocKey(paramInject.value(), type, classLoader);
			} else {
				arg = new ZoomIocKey(type, classLoader);
				iocClassLoader.append(type);
			}
			args[index++] = arg;
		}
		return args;
	}

	public static IocKey[] parseParameterKeys(Object target, Method method,IocClassLoader classLoader) {
		return parseParameterKeys(target.getClass().getClassLoader(), method.getParameterAnnotations(),
				method.getParameterTypes(),classLoader);
	}

	/**
	 * 解析fields
	 * 
	 * @param type
	 * @return
	 */
	public static IocField[] parseFields(Class<?> type,IocClassLoader classLoader) {
		Field[] fields = CachedClasses.getFields(type);
		if(fields==null || fields.length == 0) {
			return null;
		}
		List<IocField> iocFields = new ArrayList<IocField>();
		for (Field field : fields) {
			Inject inject = field.getAnnotation(Inject.class);
			if (inject != null) {
				try{
					IocField iocField = createIocField(inject,field,classLoader);
					iocFields.add(iocField);
				}catch (Throwable t){
					throw new IocException("注入字段失败"+field,t);
				}

			}
		}
		if(iocFields.size()==0)return null;
		return iocFields.toArray(new IocField[iocFields.size()]);
	}


    private IocInjectorFactory iocInjectorFactory;

    private IocMethodProxy createMethodProxy(final IocMethod iocMethod) {
        return new IocMethodProxy() {
            @Override
            public Object invoke(IocObject object) {
                return invokeMethod(object, iocMethod);
            }
        };
    }

    public IocMethodProxy getMethodProxy(IocClass iocClass, Method target) {
		if (iocClass.getIocMethods() != null) {
			for (IocMethod method : iocClass.getIocMethods()) {
				if (method.getMethod() == target) {
					return createMethodProxy(method);
				}
			}
		}

        return createMethodProxy(createIocMethod(
                iocClass.getIocClassLoader(),
                iocClass.getKey().getType(), target));
    }

	@Override
	public void release(Scope scope) {

	}

	/**
	 * 将来改成可配置的
	 * @param inject
	 * @param field
	 * @param classLoader
	 * @return
	 */
	private static IocField createIocField(Inject inject, Field field,IocClassLoader classLoader){
//		IocField result;
//		if( iocInjectorFactory != null && ((result = iocInjectorFactory.createIocField(inject,key,field)) != null) ){
//			return result;
//		}

		Class<?> fieldType = field.getType();
		if(!StringUtils.isEmpty(inject.config())){
			//配置
			String name = inject.config();
			IocKey key = new ZoomIocKey(name, fieldType, field.getDeclaringClass().getClassLoader());
			return new ZoomBeanIocField(key,field,IocValues.createConfig(field,key));
		}else{
			String name = inject.value();
			IocKey key = new ZoomIocKey(name, fieldType, field.getDeclaringClass().getClassLoader());
			if(!key.hasName()) {
				classLoader.append(key.getType());
			}
			return new ZoomBeanIocField(key,field,IocValues.VALUE);
		}


	}

	/**
	 * 解析methods
	 * 
	 * @param type
	 * @return
	 */
	public static IocMethod[] parseMethods(Class<?> type,IocClassLoader classLoader) {
		Method[] methods = CachedClasses.getPublicMethods(type);
		if(methods==null || methods.length==0)return null;
		List<IocMethod> iocMethods = new ArrayList<IocMethod>();
		for (Method method : methods) {
			Inject inject = method.getAnnotation(Inject.class);
			if (inject != null) {
				try{
                    iocMethods.add(createIocMethod(classLoader, type, method));
				}catch (Throwable t){
					throw new IocException("注入方法失败"+method,t);
				}
			}
		}

        if (iocMethods.size() == 0)
            return null;
		return iocMethods.toArray(new IocMethod[iocMethods.size()]);
	}

    public static IocMethod createIocMethod(IocClassLoader classLoader, Class<?> type, Method method) {
        try {
            IocKey[] keys = parseParameterKeys(
                    type.getClassLoader(),
                    method.getParameterAnnotations(),
                    method.getParameterTypes(), classLoader);
            return new ZoomIocMethod(keys, method);
        } catch (Throwable t) {
            throw new IocException("注入方法失败" + method, t);
        }
    }

	public IocClassLoader getIocClassLoader() {
		return iocClassLoader;
	}

	@Override
	public void setClassEnhancer(ClassEnhancer enhancer) {
		this.iocClassLoader.setClassEnhancer(enhancer);
	}

    @Override
    public void addEventListener(IocEventListener listener) {
        eventListeners.add(listener);
    }


    public IocInjectorFactory getIocInjectorFactory() {
		return iocInjectorFactory;
	}

	public void setIocInjectorFactory(IocInjectorFactory iocInjectorFactory) {
		this.iocInjectorFactory = iocInjectorFactory;
	}

    @Override
    public void onObjectCreated(IocScope scope, IocObject object) {

        for (IocEventListener listener : eventListeners) {
            listener.onObjectCreated(scope, object);
        }

    }
}
