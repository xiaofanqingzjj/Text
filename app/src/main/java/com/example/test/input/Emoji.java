package com.example.test.input;


/**
 * @author samzhan
 * @date 2015年8月20日
 * 
 * 表情信息
 */
public class Emoji {
	
	/** 表情名*/
	public String f_name;
	/** 显示的名称*/
	public String f_showName;
	/** 表情文件名*/
	public String f_fileName;
	/** 表情资源ID*/
	public int f_resId;


	@Override
	public String toString() {
		return "Emoji{" +
				"f_name='" + f_name + '\'' +
				", f_showName='" + f_showName + '\'' +
				", f_fileName='" + f_fileName + '\'' +
				", f_resId=" + f_resId +
				'}';
	}
}
