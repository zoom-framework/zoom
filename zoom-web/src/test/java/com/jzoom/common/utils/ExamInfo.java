package com.jzoom.common.utils;


/**
 * 年审信息
 * @author renxueliang
 *
 */
 class ExamInfo {
	 String idCard;
	/**
	 * 用户信息
	 */
	//@ParamValidate(requireMessage="请输人姓名",rule={Rules.chinese},message={"请输入中文姓名"})
	 String name;
	//@ParamValidate(requireMessage="请输人手机号码")
	 String phone;
	
	 String custNo;
	
	 String postCode;

	 String schoolName;
	 String schoolCode;
	 String birth;
	 String cityCode;
	 String areaCode;
	 String navCode;
	 int sex;
	 int savType;
	 String cardId;
	
	
	 byte[] img1;
	 byte[] img2;
	//0:身份证 1：户口
	 int idCardType;
	
	 boolean local;
	
	
	 int type;
	
	 int status;
	
}
