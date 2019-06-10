package org.zoomdev.zoom.ioc;

import org.zoomdev.zoom.common.exceptions.ZoomException;

public class IocException extends ZoomException {

    public IocException(String message, Throwable cause) {
        super(message, cause);
    }

    public IocException(String message) {
        super(message);
    }

    public IocException(Throwable cause) {
        super(cause);
    }

    /**
     *
     */
    private static final long serialVersionUID = -7783013537702779377L;

}
