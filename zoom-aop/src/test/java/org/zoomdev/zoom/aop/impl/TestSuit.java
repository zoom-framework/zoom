package org.zoomdev.zoom.aop.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.zoomdev.zoom.aop.javassist.JavassistAopFactoryTest;
import org.zoomdev.zoom.aop.reflect.javassist.JavassistClassInfoTest;

@RunWith(Suite.class)
@SuiteClasses({JavassistAopFactoryTest.class, JavassistClassInfoTest.class})
public class TestSuit {

}
