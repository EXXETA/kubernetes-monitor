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
package com.exxeta.we.kubernetes.monitor.saver;

import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.saver.file.DomainOverviewFileSaver;
import com.exxeta.we.kubernetes.monitor.saver.webdav.DomainOverviewWebdavService;

public abstract class DomainOverviewSaver extends AbstractSaver {

    public DomainOverviewSaver(MonitorConfig config) {
        super(config);
    }

    public static DomainOverviewSaver getSaver(MonitorConfig config) {
        if (config.getReportUrl().startsWith("http")) {
            return new DomainOverviewWebdavService(config);
        } else {
            return new DomainOverviewFileSaver(config);
        }
    }
}
