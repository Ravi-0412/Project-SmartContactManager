package com.smart.helper;

// Made this class to give message to user

public class Message {
	
	private String content;
	private String type;
	public Message(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
	
	

