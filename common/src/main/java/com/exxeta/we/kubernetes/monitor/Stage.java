package com.exxeta.we.kubernetes.monitor;

import java.util.List;

public class Stage {
	private String name;
	private List<String> stages;
	
	public Stage(String name) {
		this.name = name;
	}
	
	public Stage(String name, List<String> stages) {
		
		this.name = name;
		this.stages = stages;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getStages() {
		return stages;
	}

	public void setStages(List<String> stages) {
		this.stages = stages;
	}
}	