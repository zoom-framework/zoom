package org.zoomdev.zoom.web.action;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.utils.Classes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Action上下文，保存一次http请求的上下文
 * 其中比较重要的属性是state,表示对action的处理进度
 *
 * @author jzoom
 */
public class ActionContext implements Destroyable {

    /**
     * 初始化阶段，对request的解析还未开始
     */
    public static final int STATE_INIT = 0;

    /**
     * 预处理阶段,可以判断登录信息等，这个阶段将解析request中的数据，如将json字符串转为Map输入对象
     */
    public static final int STATE_PRE_PARSE = 1;

    /**
     * request的输入流已经解析了，这个阶段会将输入对象转成调用方法的参数数组
     */
    public static final int STATE_PARSE = 2;

    /**
     * 在调用方法之前
     */
    public static final int STATE_BEFORE_INVOKE = 3;

    /**
     * 在渲染之前
     */
    public static final int STATE_BEFORE_RENDER = 4;

    /**
     * 在渲染之后
     */
    public static final int STATE_AFTER_RENDER = 5;


    private int state;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Action action;
    private Object preParam;
    private Object[] args;
    private Object result;
    private Object target;
    private Throwable exception;

    /**
     * 这里可以在Request期间存放一些数据
     */
    private Map<String, Object> data;

    /**
     * 需要渲染的对象
     */
    private Object renderObject;

    private static ThreadLocal<ActionContext> local = new ThreadLocal<ActionContext>();


    public ActionContext(HttpServletRequest request, HttpServletResponse response, Action action) {
        this.request = request;
        this.response = response;
        this.action = action;
        local.set(this);
    }

    @Override
    public String toString() {
        if (request != null) {
            return request.getServletPath();
        }
        return super.toString();
    }

    private Map<String, Object> checkData() {
        if (data == null) {
            data = new HashMap<String, Object>();
        }
        return data;
    }

    /**
     * 与put是同义词
     *
     * @param key
     * @param value
     * @return
     */
    public ActionContext set(String key, Object value) {
        checkData().put(key, value);
        return this;
    }

    /**
     * set的同义词
     *
     * @param key
     * @param value
     * @return
     */
    public ActionContext put(String key, Object value) {
        return set(key, value);
    }

    public <T> T get(String key, Class<T> classOfT) {
        return Caster.to(checkData().get(key), classOfT);
    }

    /**
     * 确定类型可以强制转化
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(String key) {
        return (T) checkData().get(key);
    }

    /**
     * @return
     */
    public static ActionContext get() {
        return local.get();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Object getResult() {
        return result;
    }

    public Object getPreParam() {
        return preParam;
    }

    public Object[] getArgs() {
        return args;
    }

    public int getState() {
        return state;
    }

    public void setResult(Object result) {
        this.result = result;
        this.exception = null;
        setState(STATE_BEFORE_RENDER);
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setPreParam(Object preParam) {
        this.preParam = preParam;
        setState(STATE_PARSE);
    }

    public void setArgs(Object[] args) {
        this.args = args;
        setState(STATE_BEFORE_INVOKE);
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    public <T> T getTarget() {
        return (T) target;
    }

    public void setException(Throwable e) {
        this.exception = e;
    }

    public Throwable getException() {
        return this.exception;
    }


    public Map<String, Object> getData() {
        return data;
    }

    public Object getRenderObject() {
        if (renderObject != null) {
            return renderObject;
        }
        if (exception != null) {
            return exception;
        }
        return result;
    }

    /**
     * 这个方法将直接设置要渲染的对象，并且将状态改成 {@link ActionContext#STATE_BEFORE_RENDER}
     *
     * @param renderObject
     */
    public void setRenderObject(Object renderObject) {
        this.renderObject = renderObject;
        this.result = result;
        this.exception = null;
        setState(STATE_BEFORE_RENDER);
    }

    @Override
    public void destroy() {
        if (this.data != null) {
            Classes.destroy(this.data);
            this.data = null;
        }
        exception = null;
        renderObject = null;
        result = null;
        preParam = null;
        target = null;
        if (args != null) {
            Arrays.fill(args, null);
            args = null;
        }

        local.remove();
    }


    public ActionContext setSession(String key, Object value) {
        HttpSession session = null;
        if (request != null && ((session = request.getSession()) != null)) {
            try {
                session.setAttribute(key, value);
            } catch (Throwable t) {

            }
        }
        return this;
    }


    public ActionContext removeSession(String key) {
        HttpSession session = null;
        if (request != null && ((session = request.getSession()) != null)) {
            try {
                session.removeAttribute(key);
            } catch (Throwable t) {

            }
        }
        return this;
    }


    /**
     * 有可能获取到null
     *
     * @return
     */
    public Cookie[] getCookies() {
        if (request != null) {
            return request.getCookies();
        }
        return null;
    }


    /**
     * 有可能获取到null
     *
     * @return
     */
    public String getSessionId() {
        HttpSession session = null;
        if (request != null && ((session = request.getSession()) != null)) {
            try {
                return session.getId();
            } catch (Throwable t) {

            }
        }
        return null;
    }

    public Object getSession(String key) {
        HttpSession session = null;
        if (request != null && ((session = request.getSession()) != null)) {
            try {
                return session.getAttribute(key);
            } catch (Throwable t) {

            }
        }
        return null;
    }
}
