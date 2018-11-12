package org.zoomdev.zoom.web.action;

public interface ActionInterceptor {

    /**
     * 请求参数预处理器,处理成功之后，调用context.setPreParam保存预处理的结果
     * 这里可以将请求的参数如json字符串转成Map等
     * 如果发现请求不符合要求，则可以如下处理：
     * 1、直接抛出异常，然后使用定义的异常处理程序处理输出
     * 2、直接调用context.setResult设置一个值，表示已经处理完成，要求输出
     * {@link org.zoomdev.zoom.web.action.ActionContext#STATE_PRE_PARSE}
     *
     * @param request
     * @param response
     * @param action
     * @return 返回false将直接终止，所以如果有输出，则必须手动从response输出
     */
    boolean preParse(ActionContext context) throws Exception;


    /**
     * 参数已经做了预处理
     * {@link org.zoomdev.zoom.web.action.ActionContext#STATE_PARSE}
     *
     * @param action
     * @throws Exception
     */
    void parse(ActionContext context) throws Exception;

    /**
     * 调用函数完毕
     *
     * @param context
     * @throws Exception
     */
    void whenResult(ActionContext context) throws Exception;

    /**
     * 发生异常
     *
     * @param throwable
     * @throws Exception
     */
    boolean whenError(ActionContext context) throws Exception;

    /**
     * 完成一次request
     *
     * @throws Exception
     */
    void complete(ActionContext context) throws Exception;

}
