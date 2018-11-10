package org.zoomdev.zoom.ioc;

import java.lang.reflect.Field;

public interface IocField extends IocInjector {

	/**
	 * inject的别名
	 * @param obj
	 */
	void set(IocObject obj);

    Field getField();
}
