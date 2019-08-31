package com.ddphin.base.common.constant;

public enum AppType {
	H5(1000, "H5"),

	ANDROID(2100, "Android"),
	IOS(2200, "IOS"),

	WX(3000, "微信"),
	WX_SRV(3100, "微信服务号"),
	WX_SUB(3200, "微信订阅号"),
	WX_XCX(3300, "微信小程序"),

	ALI_PAY(4000, "支付宝"),
			;

	private Integer code;
	private String name;

	AppType(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	public Integer getCode() {
		return this.code;
	}
	public String getName() {
		return this.name;
	}
	public static AppType fromCode(Integer code) {
		for (AppType o : values()) {
			if (o.getCode().equals(code)) {
				return o;
			}
		}
		return null;
	}
}
