package org.zoomdev.zoom.web.router;

/**
 * 路由参数的匹配规则
 *
 * @author jzoom
 */
public interface RouterParamRule {

    /**
     * 对于url中的模式部分，获取参数名字
     * 如  /users/{id}  返回id
     * 接收的参数为 {id}
     * <p>
     * 形式可配置，如/users/:id   /users/{id}等
     *
     * @param value
     * @return
     */
    String getParamName(String value);

    /**
     * 是否含有这个规则
     *
     * @param url
     * @return
     */
    boolean match(String url);


}
