package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.common.exceptions.ZoomException;

public class DaoException extends ZoomException {

    public DaoException(Throwable e) {
        super(e);
    }

    public DaoException(String message, Throwable e) {
        super(message, e);
    }

    public DaoException(String message) {
        super(message);
    }

    /**
     *
     */
    private static final long serialVersionUID = 1008765440134574580L;

}
