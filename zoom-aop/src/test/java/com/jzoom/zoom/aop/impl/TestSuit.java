package com.jzoom.zoom.aop.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jzoom.zoom.aop.javassist.JavassistAopFactoryTest;
import com.jzoom.zoom.aop.reflect.javassist.JavassistClassInfoTest;

@RunWith(Suite.class)
@SuiteClasses({JavassistAopFactoryTest.class,JavassistClassInfoTest.class})
public class TestSuit {

}
