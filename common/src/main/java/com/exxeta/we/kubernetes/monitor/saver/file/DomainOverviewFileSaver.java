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
package com.exxeta.we.kubernetes.monitor.saver.file;

import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.saver.DomainOverviewSaver;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;

public class DomainOverviewFileSaver extends DomainOverviewSaver {

    public DomainOverviewFileSaver(MonitorConfig config) {
        super(config);
    }

    @Override
    public void saveRawData(byte[] rawData) {
        try {
            FileUtils.writeByteArrayToFile(FileUtils.toFile(new URL(config.getOverviewUrl())), rawData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] loadRawData() {
        try {
            return downloadToByteArray(new URL(config.getOverviewUrl()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
