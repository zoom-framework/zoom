package org.zoomdev.zoom.plugin;

public class PluginException extends Exception {
    public PluginException(String message, Throwable e) {
        super(message, e);
    }

    public PluginException(Throwable e) {
        super(e);
    }

    public PluginException(String e) {
        super(e);
    }

    /**
     *
     */
    private static final long serialVersionUID = -5792723462079997936L;

}
