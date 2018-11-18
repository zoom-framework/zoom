package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.AutoGenerateValue;

import java.util.concurrent.atomic.AtomicInteger;

public class MockAutoGenerateValue implements AutoGenerateValue {


    static AtomicInteger base = new AtomicInteger(
            0
    );

    @Override
    public Object nextVal() {
        return base.incrementAndGet();
    }
}
