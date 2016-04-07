package com.banxian.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
public class SpringIocUtils {
	private static Map<String, Object> beanFactoryMap =new HashMap<String, Object>();
	private static WebApplicationContext wac;
	@SuppressWarnings({ "unchecked", "hiding" })
	public static <T> T getBean(Class<T> clazz) {
		wac = ContextLoader.getCurrentWebApplicationContext();
		String beanName = clazz.getSimpleName().substring(0,1).toLowerCase()+clazz.getSimpleName().substring(1);
		if(beanFactoryMap.containsKey(beanName)){
			return (T)beanFactoryMap.get(beanName);
		}else{
			T t =(T) wac.getBean(beanName);
			beanFactoryMap.put(beanName,t);
			return t;
		}
		
	}
}
