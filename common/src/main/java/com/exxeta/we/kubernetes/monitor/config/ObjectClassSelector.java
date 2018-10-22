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
package com.exxeta.we.kubernetes.monitor.config;

import java.util.List;

public class ObjectClassSelector {

	private int expectedNumber;
	private List<String> patterns;
	private String objectClass;
	
	public int getExpectedNumber() {
		return expectedNumber;
	}
	public void setExpectedNumber(int expectedNumber) {
		this.expectedNumber = expectedNumber;
	}
	public List<String> getPatterns() {
		return patterns;
	}
	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}
	public String getObjectClass() {
		return objectClass;
	}
	
	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}
	
	
}
 