package com.mygdx.game;

public class Upgrade {
	public String name;
	public String description;
	public int time;
	
	public Upgrade(String name, String d) {
		this.name = name;
		description = d;
	}
	
	public void setTime(int t) {
		time = t;
	}
	
}
