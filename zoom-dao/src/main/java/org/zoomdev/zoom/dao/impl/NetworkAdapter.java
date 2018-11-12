package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.DaoException;

import java.io.IOException;
import java.util.Map;

/**
 * 网络请求适配器
 */
public interface NetworkAdapter {

    /**
     * 调用对方服务器的一个api，使用指定的参数,
     * 最简单的是 发送一个sql语句，和参数语句，然后等结果回来，在序列化,在本地缓存保存结构
     *
     * @param api
     * @param data
     * @param <T>
     * @return
     * @throws IOException
     * @throws DaoException
     */
    <T> T call(String api, Map<String, Object> data) throws IOException, DaoException;
}
