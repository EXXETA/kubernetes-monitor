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

public class ObjectClassState {
	private String objectClass;
	private int actualNumber = 0;
	private int expectedNumber = 0;
	private List<String> unexpectedObjects = new ArrayList<>();
	private List<String> expectedObjects = new ArrayList<>();
	private boolean errorDuringSelection = true;

	public String getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}

	public int getActualNumber() {
		return actualNumber;
	}

	public int getExpectedNumber() {
		return expectedNumber;
	}

	public void setExpectedNumber(int expectedNumber) {
		this.expectedNumber = expectedNumber;
	}

	public List<String> getUnexpectedObjects() {
		return unexpectedObjects;
	}

	public void addUnexpectedObject(String unexpectedObject) {
		this.unexpectedObjects.add(unexpectedObject);
	}

	public List<String> getExpectedObjects() {
		return expectedObjects;
	}

	public void addExpectedObject(String expectedObject) {
		this.expectedObjects.add(expectedObject);
	}

	public void incActualNumber() {
		actualNumber++;
	}
	
	public void setErrorDuringSelection(boolean errorDuringSelection) {
		this.errorDuringSelection = errorDuringSelection;
	}

	public boolean isErrorDuringSelection() {
		return errorDuringSelection;
	}

	
	
}
