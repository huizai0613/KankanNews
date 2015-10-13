package com.kankan.kankanews.bean;

import java.io.Serializable;

public class Keyboard implements Serializable {
	private String text;
	private String color;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
