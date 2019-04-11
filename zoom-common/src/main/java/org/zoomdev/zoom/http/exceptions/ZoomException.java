package org.zoomdev.zoom.http.exceptions;

public class ZoomException extends RuntimeException {
    public ZoomException() {
    }

    public ZoomException(String message) {
        super(message);
    }

    public ZoomException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZoomException(Throwable cause) {
        super(cause);
    }
}
