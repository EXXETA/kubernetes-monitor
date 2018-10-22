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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

public class MonitorConfigTest {

	
	@Test
	public void loadTest() throws IOException{
		MonitorConfig config = MonitorConfig.getConfigFromString("{	\"privateKey\": \"KeyPair/privateKey\",	\"publicKey\": \"KeyPair/publicKey\",	\"webDavUser\": \"user\",	\"webDavPassword\": \"pass\",	\"reportUrl\": \"myUrl\","
				+ "\"clusters\":[{\"name\"=\"test\", \"kubeconfig\"=\"pathToKubeConfig\", \"skipNamespaces\"=[], \"applications\"=[{\"name\"=\"myApp\"}]}]}");
		assertNotNull(config);
		
		assertEquals(config.getPrivateKey(),"KeyPair/privateKey");
		assertEquals(config.getPublicKey(),"KeyPair/publicKey");
		assertEquals(config.getWebDavUser(),"user");
		assertEquals(config.getWebDavPassword(),"pass");
		assertEquals(config.getReportUrl(),"myUrl");
		
		assertEquals(config.getClusters().size(),1);
		assertEquals(config.getClusters().get(0).getName(), "test");
		assertEquals(config.getClusters().get(0).getKubeconfig(), "pathToKubeConfig");
		assertEquals(config.getClusters().get(0).getSkipNamespaces().size(), 0);
		assertEquals(config.getClusters().get(0).getApplications().size(), 1);
		assertEquals(config.getClusters().get(0).getApplications().get(0).getName(), "myApp");
		
	}
}
