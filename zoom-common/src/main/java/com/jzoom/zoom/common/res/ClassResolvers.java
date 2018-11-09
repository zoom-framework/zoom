package com.jzoom.zoom.common.res;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jzoom.zoom.common.res.ResScanner.ClassRes;
import com.jzoom.zoom.common.utils.Visitor;;

public class ClassResolvers {

	List<ClassResolver> resolvers;
	
	
	public ClassResolvers(ClassResolver... resolvers) {
		this.resolvers = Arrays.asList(resolvers);
	}

	public void add(ClassResolver classResolver){
		this.resolvers.add(classResolver);
	}
	
	
	public void visit( ResScanner scanner ) {
		final List<ClassRes> classes = new ArrayList<ClassRes>();
		scanner.visitClass(new Visitor<ClassRes>() {

			@Override
			public void visit(ClassRes data) {
            for (ClassResolver resolver : resolvers) {
                if(resolver.acceptClassName(data.getName())) {
                    classes.add(data);
                }
            }
			}
			
		});
		
		for (ClassResolver classResolver : resolvers) {
			for (ClassRes res : classes) {
				if(classResolver.acceptClass(res.getType())) {
					classResolver.visitClass(res.getType());
					
					if(classResolver.resolveFields()) {
						for (Field field : res.getFields()) {
							classResolver.visitField(field);
						}
					}
					
					if(classResolver.resolveMethods()) {
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

}
