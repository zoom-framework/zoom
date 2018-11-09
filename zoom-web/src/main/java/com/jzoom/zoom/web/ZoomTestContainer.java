package com.jzoom.zoom.web;

import java.io.File;

import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.aop.javassist.JavassistAopFactory;
import com.jzoom.zoom.common.config.ConfigReader;
import com.jzoom.zoom.common.res.ResLoader;
import com.jzoom.zoom.ioc.IocContainer;

public class ZoomTestContainer {
	protected IocContainer ioc;
	private AopFactory factory;


	public ZoomTestContainer() {
		loadApplicationConfig();
		///createIocContainer();
	}
	private void loadApplicationConfig() {
		// 加载全局配置

		File file = ResLoader.getResourceAsFile("application.properties");
		if (file == null) {
			file = ResLoader.getResourceAsFile("application.json");
		}

		if (file == null) {
			// 目前这个版本支持两种主配置，properties/yml
			return;
			//throw new RuntimeException("启动失败，请确认application.properties或application.json存在");
		}
		ConfigReader.getDefault().load(file);

	}
	private void createAopFactory() {
		factory = new JavassistAopFactory();
	}

//	public class ClassEnhanceAdapter implements ClassEnhance {
//		private AopFactory factory;
//
//		public ClassEnhanceAdapter(AopFactory factory) {
//			this.factory = factory;
//		}
//
//		@Override
//		public Class<?> enhance(Class<?> src) {
//			return factory.enhance(src);
//		}
//
//	}
//
//	private void createIocContainer() {
//		createAopFactory();
//
//		IocSettingLoader loader = new SimpleIocSettingLoader();
//		IocSetting[] settings = loader.load();
//		ioc = new SimpleIocContainer(new SimpleIocClassFactory(new ClassEnhanceAdapter(factory)));
//		// ioc = new GlobalIocContainer(new ClassEnhanceAdapter(factory),settings);
//		ioc.register(AopFactory.class.getName(), factory);
//		WebUtils.setIoc(ioc);
//
//	}
}
