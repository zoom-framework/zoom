package org.zoomdev.zoom.common.res;


import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.res.ResScanner.ClassRes;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.Visitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

;

public class ClassResolvers implements Destroyable {

    List<ClassResolver> resolvers;


    public ClassResolvers(ClassResolver... resolvers) {
        this.resolvers = Arrays.asList(resolvers);
    }

    public void add(ClassResolver classResolver) {
        this.resolvers.add(classResolver);
    }


    public void visit(ResScanner scanner) {
        final List<ResScanner.ClassRes> classes = new ArrayList<ResScanner.ClassRes>();
        scanner.visitClass(new Visitor<ClassRes>() {

            @Override
            public void visit(ResScanner.ClassRes data) {
                for (ClassResolver resolver : resolvers) {
                    if (resolver.acceptClassName(data.getName())) {
                        classes.add(data);
                    }
                }
            }

        });


        for (ClassResolver classResolver : resolvers) {
            for (ResScanner.ClassRes res : classes) {
                if (classResolver.acceptClass(res.getType())) {
                    classResolver.visitClass(res.getType());

                    if (classResolver.resolveFields()) {
                        for (Field field : res.getFields()) {
                            classResolver.visitField(field);
                        }
                    }

                    if (classResolver.resolveMethods()) {
                        for (Method method : res.getPubMethods()) {
                            classResolver.visitMethod(method);
                        }
                    }

                    classResolver.clear();
                }
            }

            classResolver.endResolve();
        }

    }

    @Override
    public void destroy() {
        Classes.destroy(this.resolvers);
    }
}
