package com.exxeta.we.kubernetes.monitor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.exxeta.we.kubernetes.monitor.config.MultiDomainConfig;

public class MultiDomainCollector {
	public MultiDomainCollector(String configFile) throws Exception {
		
		Map<String, StatusReport> reports = new HashMap<>();
		MultiDomainConfig config = MultiDomainConfig.getConfigFromFile(new File(configFile));
		
		String[] domainConfigs = config.getDomainConfigs();
		for (String domainConfig : domainConfigs) {
			StatusReport report = new Collector().collect(domainConfig);
			reports.put(report.getDomainName(), report);
		}

	}

	public static void main(String[] args) throws Exception {
		new MultiDomainCollector(args[0]) ;
		
	}
}
