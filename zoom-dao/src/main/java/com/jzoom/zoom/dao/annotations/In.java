package com.jzoom.zoom.dao.annotations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ValidTypes({Object[].class,Iterable.class,List.class,ArrayList.class,Iterator.class})
public @interface In {

}
