package com.ddphin.base.common.constant;

public enum AppType {
	H5(1000, "h5"),

	APP(2000, "app"),
	ANDROID(2100, "android"),
	IOS(2200, "ios"),

	WX(3000, "srv"),
	SRV(3100, "srv"),
	SUB(3200, "sub"),
	XCX(3300, "xcx"),

	ALI(4000, "ali"),
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
