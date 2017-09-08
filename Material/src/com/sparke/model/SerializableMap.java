package com.sparke.model;

import java.io.Serializable;
import java.util.Map;
/**
 * 将Map<String, String>类序列化，以放进Bundle中
 * @author 运德
 *
 */
public class SerializableMap implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> map;
	
	public Map<String, String> getMap() {
		return map;
	}
	
	public void setMap(Map<String, String> map) {
		this.map = map;
	}
}

/*
 * 封装到Bundle中例：
 * 	Map<String, String> data = new HashMap<String, String>();
 * 	SerializableMap sMap = new SerializableMap();
 * 	sMap.setMap(data);
 * 	bundle.putSerializable("Tag", sMap);
 * */
/* 从Bundle中取出Map：
 * 	Bundle bundle = getIntent().getExtras()；
 * 	SerializableMap sMap = (SerializableMap) bundle.get("Tag");
 * */
