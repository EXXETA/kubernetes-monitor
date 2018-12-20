/*
 * Kubernetes Monitor
 * Copyright (C) 2018 Thomas Pohl and EXXETA AG
 * http://www.exxeta.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exxeta.we.kubernetes.monitor;

import java.util.ArrayList;
import java.util.List;

public class StatusReport {
	private final long timestamp = System.currentTimeMillis();
	private boolean  malformedJson = false;
	private final List<ApplicationState> applications = new ArrayList<>();
	private List<UnknownNamespace> unknownNamespaces = new ArrayList<>();
	private List<ClusterAvailablility> clusterAvailability = new ArrayList<>();
	private String domainName;
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public List<ApplicationState> getApplications() {
		return applications;
	}
	
	public void setApplicationInstanceState(String applicationName, ApplicationInstanceState state){
		
		ApplicationState application = getApplication(applicationName); 
		if (application==null){
			application = new ApplicationState(applicationName);
			applications.add(application);
		}
		application.setApplicationInstanceSate(state);
	}
	
	private ApplicationState getApplication(String name) {
		for (ApplicationState application: applications){
			if (application.getName().equals(name)) {
				return application;
			}
		}
		
		return null;
	}

	public void addUnknownNamespace(String cluster, String namespace){
		unknownNamespaces.add(new UnknownNamespace(cluster, namespace));
	}
	public List<UnknownNamespace> getUnknownNamespaces() {
		return unknownNamespaces;
	}
	
	public void addClusterAvailability(ClusterAvailablility availability){
		this.clusterAvailability.add(availability);
	}
	
	public ClusterAvailablility getClusterAvailability(String clusterName) {
		for (ClusterAvailablility a : clusterAvailability){
			if (a.getClusterName().equals(clusterName)){
				return a;
			}
		}
		return null;
	}
	
	public void setMalformedJson(boolean malformedJson) {
		this.malformedJson = malformedJson;
	}
	
	public boolean isMalformedJson() {
		return malformedJson;
	}
	
	public String getDomainName() {
		return domainName;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public List<ClusterAvailablility> getClusterAvailability() {
		return clusterAvailability;
	}


	
}
