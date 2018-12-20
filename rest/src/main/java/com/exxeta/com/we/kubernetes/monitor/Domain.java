package com.exxeta.com.we.kubernetes.monitor;

import java.util.List;

import com.exxeta.we.kubernetes.monitor.ClusterAvailablility;


public class Domain {
	
	private String name;
	private Long timestamp;
	private List<ClusterAvailablility> clusterConfig;
	private String url;
	private String status = "good";
	private List<Stage> stages;

	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
		
		this.setUrl("/api/kube/rest/" + name);
		//this.setUrl("/api/rest/rest/" + name);
	}


	public Long getLastUpdate() {
		return timestamp;
	}


	public void setLastUpdate(Long lastUpdate) {
		this.timestamp = lastUpdate;
	}



	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public List<ClusterAvailablility> getClusterConfig() {
		return clusterConfig;
	}


	public void setClusterConfig(List<ClusterAvailablility> clusterConfig) {
		this.clusterConfig = clusterConfig;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		
		switch(this.status) {
		case "good":
			
			if(status == "warning" || status == "danger") {
				this.status = status;
			}
			
			break;
		case "warning":
			
			if(status == "danger") {
				this.status = status;
			}
			
			break;
		case "error":
			break;
		}
	}


	public List<Stage> getStages() {
		return stages;
	}


	public void setStages(List<Stage> stages) {
		this.stages = stages;
	}

}
