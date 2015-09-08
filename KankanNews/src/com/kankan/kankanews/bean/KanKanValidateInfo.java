package com.kankan.kankanews.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class KanKanValidateInfo implements Serializable {
	private String telephone;

	private String deviceName;

	private String deviceVersion;

	private String serviceProvider;

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceVersion() {
		return deviceVersion;
	}

	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}

	public String getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

}
