package org.zoomdev.zoom.web.utils;


import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.res.ClassResolver;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.res.ResScanner.ClassRes;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.Visitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;

;

public class ClassResolvers  {

    List<ClassResolver> resolvers;


    public ClassResolvers(ClassResolver... resolvers) {
        this.resolvers = Arrays.asList(resolvers);
    }

    public void add(ClassResolver classResolver) {
        this.resolvers.add(classResolver);
    }

    public void visit(final ResScanner scanner) {
        for(final ClassResolver resolver : resolvers){
            resolver.resolve(scanner);
        }

    }


}
