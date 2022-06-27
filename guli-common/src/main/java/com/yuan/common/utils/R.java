/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yuan.common.utils;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
/**
	注意：R是一个哈希map的类型的返回  data数据无法进行保存，必须以键值对方式保存
 			所以此方法返回数据不可用
* **/
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;


	//可以自定传入要获取的key的名称
	public <T>T getData(String key,TypeReference<T> typeReference){ //TypeReference：阿里巴巴提供的泛型
		Object data = get(key); //取到data，转化为Json数据,后面再进行逆转为对应的类型
		String json_data = JSON.toJSONString(data);
		T t = JSON.parseObject(json_data, typeReference); //JSON.parseObject:将某数据字符串转化为某个类型
		return  t;
	}


	public <T>T getData(TypeReference<T> typeReference){ //TypeReference：阿里巴巴提供的泛型
		Object data = get("data"); //取到data，转化为Json数据,后面再进行逆转为对应的类型
		String json_data = JSON.toJSONString(data);
		T t = JSON.parseObject(json_data, typeReference); //JSON.parseObject:将某数据字符串转化为某个类型
		return  t;
	}
	/**
	 * 创建一个可以注入所有类型数据的方法，并且调用自身拥有的put方法
	 * 		返回值就是自身数据
	 * **/
	public  R setData(Object data){
		put("data", data);
		return  this;
	}


	public R() {
		put("code", 0);
		put("msg", "success");
	}
	
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "500异常，未知错误");
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}


	/**
	 * 记录返回code状态
	 * */
	public int getCode(){
		return (Integer)this.get("code");
	}

}
