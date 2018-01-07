package com.benefire.waldder.midpoint.domain;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.regex.Pattern;

import com.benefire.waldder.midpoint.annotation.Value;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;

/**
 * @author JIANG
 */
public interface AbstractProperties {
	
	Pattern PORT_PATTERN = Pattern.compile("^[0-9]*[1-9][0-9]*$");
	
	default void initProperties(String fileName) {
		System.err.print(getClass());
		Properties propertys = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
		try {
			propertys.load(inputStream);
			Field[] fields = getClass().getDeclaredFields();
			for(int i =0,length=fields.length;i<length;i++){
				Field field = fields[i];
				Value annotation = field.getAnnotation(Value.class);
				if(null != annotation){
					String key = annotation.value();
					String value = propertys.getProperty(key);
					if(!Strings.isNullOrEmpty(value)){
						Class<?> type = field.getType();
						String methodName = String.join("", "set",CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL,field.getName()));
						Method method = getClass().getMethod(methodName, type);
						if(null != method){
							method.invoke(this, basicConvert(value,type));
						}
					}
				}
			}
		} catch (IOException | NoSuchMethodException | IllegalAccessException |InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		} finally{
			try {
				if(null != inputStream) inputStream.close();
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	default <T> T basicConvert(String value,Class<T> clazz){
		if(int.class.isAssignableFrom(clazz)|| Integer.class.isAssignableFrom(clazz)){
			return (T) Integer.valueOf(value);
		}
		if(long.class.isAssignableFrom(clazz)||Long.class.isAssignableFrom(clazz)){
			return (T) Long.valueOf(value);
		}
		if(boolean.class.isAssignableFrom(clazz)||Boolean.class.isAssignableFrom(clazz)){
			return (T) Boolean.valueOf(value);
		}
		if(double.class.isAssignableFrom(clazz)||Double.class.isAssignableFrom(clazz)){
			return (T) Double.valueOf(value);
		}
		if(float.class.isAssignableFrom(clazz)||Float.class.isAssignableFrom(clazz)){
			return (T) Float.valueOf(value);
		}
		if(CharSequence.class.isAssignableFrom(clazz)){
			return (T) String.valueOf(value);
		}
		return (T)value;
	}
	
	static boolean isInt(String value){
		if(Strings.isNullOrEmpty(value)) {
			return false;
		}
		return PORT_PATTERN.matcher(value).matches();
	}

}
