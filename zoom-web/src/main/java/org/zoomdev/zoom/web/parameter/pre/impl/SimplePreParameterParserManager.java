package org.zoomdev.zoom.web.parameter.pre.impl;

import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.PreParameterParser;
import org.zoomdev.zoom.web.parameter.PreParameterParserManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimplePreParameterParserManager implements PreParameterParserManager {
    private PreParameterParser[] parsers;


    public SimplePreParameterParserManager() {
        try {
            parsers = new PreParameterParser[]{
                    new JsonPreParamParser(),
                    new UploadPreParamParser(),
                    new FormPreParamParser()
            };
        } catch (Throwable t) {
            parsers = new PreParameterParser[]{
                    new JsonPreParamParser(),
                    new FormPreParamParser()
            };
        }

    }

    @Override
    public List<PreParameterParser> getParsers() {
        return Arrays.asList(parsers);
    }

    @Override
    public void addParser(PreParameterParser parser) {

        List<PreParameterParser> list = new ArrayList<PreParameterParser>();
        Collections.addAll(list, parser);

        list.add(parser);

        this.parsers = list.toArray(new PreParameterParser[list.size()]);

    }

    @Override
    public Object preParse(ActionContext context) throws Exception {
        String contentType = context.getRequest().getContentType();

        for (PreParameterParser preParameterParser : parsers) {
            if (preParameterParser.shouldParse(contentType)) {
                return preParameterParser.preParse(context);
            }
        }

        throw new ZoomException("找不到参数解析器");
    }
}
