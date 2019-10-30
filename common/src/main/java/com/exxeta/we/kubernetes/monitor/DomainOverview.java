package com.exxeta.we.kubernetes.monitor;

import java.util.List;

public class DomainOverview {

	private String name;
	private Long timestamp;
	private List<ClusterAvailablility> clusterConfig;
	private String url;
	private String status = "good";
	private List<Stage> stages;

	
	public void collect(StatusReport statusReport) {
		
		this.setClusterConfig(statusReport.getClusterAvailability());
		this.setLastUpdate(statusReport.getTimestamp());
		
		for (ApplicationState application : statusReport.getApplications()) {

			for (ApplicationInstanceState applicationInstanceState : application.getInstances()) {

				for (ObjectClassState objectClassState : applicationInstanceState.getObjectStates()) {

					if (objectClassState.getExpectedNumber() > 0) {

						if (objectClassState.getExpectedNumber() == objectClassState.getActualNumber()) {
							// Everything is fine
						} else if (objectClassState.getActualNumber() == 0) {
							// All down
							this.setStatus("danger");
						} else if (objectClassState.getActualNumber() > 0) {
							// At least one is active
							this.setStatus("warning");
						}
					}
					
					if(!objectClassState.getUnexpectedObjects().isEmpty()) {
						this.setStatus("warning");
					}
				}
			}
		}
	}
	
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
