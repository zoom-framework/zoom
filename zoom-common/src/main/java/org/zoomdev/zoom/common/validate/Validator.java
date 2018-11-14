package org.zoomdev.zoom.common.validate;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 数据校验接口
 */
public interface Validator<T> {
    /**
     * 校验通过，返回null，否则返回不通过的原因
     *
     * @param data  所有提交数据
     * @param value 本字段对应的值
     * @return
     */
    String validate(T data, Object value);
}
