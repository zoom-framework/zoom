package org.zoomdev.zoom.http.caster;


/**
 * This interface define a way to cast one class to another class
 *
 * @author Administrator
 */
public interface ValueCaster {

    /**
     * @param src source value
     * @return casted value
     */
    Object to(Object src);
}
