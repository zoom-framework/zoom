package org.zoomdev.zoom.web.parameter;

import org.zoomdev.zoom.web.action.ActionContext;

import java.util.List;

public interface PreParameterParserManager {

    List<PreParameterParser> getParsers();

    void addParser(PreParameterParser parser);

    Object preParse(ActionContext context) throws Exception;

}
