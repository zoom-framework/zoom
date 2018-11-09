package org.zoomdev.zoom.ioc;

import java.lang.reflect.Field;

public interface IocField extends IocInjector {

	IocKey getKey();

	void set(IocObject obj, IocObject value);

	IocValue getValue();

    Field getField();
}
