package org.zoomdev.zoom.web.parameter;

import org.zoomdev.zoom.web.action.Action;
import org.zoomdev.zoom.web.action.ActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface PreParameterParserManager {

    List<PreParameterParser> getParsers();

    void addParser(PreParameterParser parser);

    Object preParse(ActionContext context) throws Exception;

}
