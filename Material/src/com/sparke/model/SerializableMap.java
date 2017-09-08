package com.sparke.model;

import java.io.Serializable;
import java.util.Map;
/**
 * ��Map<String, String>�����л����ԷŽ�Bundle��
 * @author �˵�
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
 * ��װ��Bundle������
 * 	Map<String, String> data = new HashMap<String, String>();
 * 	SerializableMap sMap = new SerializableMap();
 * 	sMap.setMap(data);
 * 	bundle.putSerializable("Tag", sMap);
 * */
/* ��Bundle��ȡ��Map��
 * 	Bundle bundle = getIntent().getExtras()��
 * 	SerializableMap sMap = (SerializableMap) bundle.get("Tag");
 * */
