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

public class ApplicationInstanceState {

	private final String stage;
	private final String region;
	private final String cluster;
	private final String namespace;
	
	private List<ObjectClassState> objectStates = new ArrayList<>();
	
	private List<String> podNotReady = new ArrayList<>();
	
	private List<Container> containers = new ArrayList<>();
	
	public ApplicationInstanceState(String stage, String region, String cluster, String namespace) {
		super();
		this.stage = stage;
		this.region = region;
		this.cluster=cluster;
		this.namespace=namespace;
	}

	public String getStage() {
		return stage;
	}

	public String getRegion() {
		return region;
	}
	
	public String getCluster() {
		return cluster;
	}
	
	public String getNamespace() {
		return namespace;
	}

	public void addPodNotReady(String name) {
		podNotReady.add(name);
		
	}
	
	public List<String> getPodNotReady() {
		return podNotReady;
	}
	
	public void addObjectState(ObjectClassState state){
		objectStates.add(state);
	}
	
	public List<ObjectClassState> getObjectStates() {
		return objectStates;
	}

	public void addContainer(String name, String version, Boolean ready) {
		containers.add(new Container(name, version, ready));
	}
}
