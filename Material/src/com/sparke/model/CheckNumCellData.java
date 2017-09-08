package com.sparke.model;

public class CheckNumCellData {
	
	//检查签到人员信息类
	//name：签到人姓名
	//id：签到人学号
	//sign_time：签到时间
	public String name;
	public String id;
	public String sign_time;
	
	public CheckNumCellData(String name, String id, String sign_time){
		this.name = name;
		this.id = id;
		this.sign_time = sign_time;
	}
}
