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
package com.exxeta.we.kubernetes.monitor.rest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.saver.ReportSaver;

public class MyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ReportSaver saver;
	private final MonitorConfig config;

	public MyServlet() {
		MonitorConfig loadedConfig = null;
		try {
			loadedConfig = MonitorConfig.getConfigFromFile(new File("config.json"));
		} catch (IOException e) {
			System.out.println(new File("config.json").getAbsolutePath());
			
			e.printStackTrace();
		}
		config = loadedConfig;
		saver = ReportSaver.getSaver(config);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("application/json");

		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		if ("/status".equals(req.getPathInfo())) {
			PrintWriter out = resp.getWriter();
			byte[] json = saver.loadJson();
			out.write(new String(json));
		} else if ("/status/alert".equals(req.getPathInfo())) {

		} else {
			resp.setStatus(404);
		}

	}

}
