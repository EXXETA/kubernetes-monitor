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

import com.exxeta.we.kubernetes.monitor.StatusReport;
import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.encryption.AsymmetricCryptography;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.google.gson.GsonBuilder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Abstract class for save-mechanisms
 */
public abstract class AbstractSaver {
    protected MonitorConfig config;

    // http proxy env var keys
    private final String PROXY_HOST = "PROXY_HOST";
    private final String PROXY_PORT = "PROXY_PORT";

    public abstract void saveRawData(byte[] rawData);

    protected abstract byte[] loadRawData();

    public AbstractSaver(MonitorConfig config) {
        this.config = config;
    }

    public StatusReport load() {
        byte[] json = loadJson();
        if (json == null) {
            return null;
        }
        return parse(new String(loadJson()));
    }

    public byte[] loadJson() {
        byte[] rawData = loadRawData();
        if (config.getEncrypt() != null && config.getEncrypt()) {
            try {
                return decrypt(rawData);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException
                | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException | IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return rawData;
        }
    }

    protected StatusReport parse(String reportData) {
        return new GsonBuilder().create().fromJson(reportData, StatusReport.class);
    }

    protected String serialize(Object report) {
        return new GsonBuilder().create().toJson(report);
    }

    protected byte[] encrypt(byte[] data)
        throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
        BadPaddingException, InvalidKeySpecException, NoSuchProviderException, IOException {
        return new AsymmetricCryptography(config.getPrivateKey(), config.getPublicKey()).encrypt(data);

    }

    protected byte[] decrypt(byte[] data)
        throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException,
        InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, IOException {
        return AsymmetricCryptography.decrypt(config, data);
    }

    public void save(StatusReport result) {
        try {
            byte[] myStatus = serialize(result).getBytes();

            if (config.getEncrypt() != null && config.getEncrypt()) {
                myStatus = encrypt(myStatus);
            }
            saveRawData(myStatus);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
            | BadPaddingException | InvalidKeySpecException | NoSuchProviderException | IOException e) {
            e.printStackTrace();
        }
    }

    protected InetSocketAddress getProxyAddress() {
        if (!System.getenv().containsKey(PROXY_HOST) ||
            !System.getenv().containsKey(PROXY_PORT)) {
            return null;
        }
        String proxyHost = System.getenv(PROXY_HOST);
        String proxyPort = System.getenv(PROXY_PORT);
        int port = 0;
        try {
            port = Integer.parseInt(proxyPort);
        } catch (NumberFormatException nfe) {
            Logger.getAnonymousLogger().warning(String.format("Invalid proxy port '%s' given!", proxyPort));
        }
        if (port != 0 && !proxyHost.isEmpty()) {
            return new InetSocketAddress(proxyHost, port);
        }
        // fallback for wrong data
        return null;
    }

    protected Sardine getSardineInstance() {
        InetSocketAddress proxyAddress = getProxyAddress();
        Sardine sardine;
        if (proxyAddress != null) {
            sardine = SardineFactory.begin(config.getWebDavUser(), config.getWebDavPassword(), ProxySelector.of(proxyAddress));
        } else {
            sardine = SardineFactory.begin(config.getWebDavUser(), config.getWebDavPassword());
        }
        return sardine;
    }

    public byte[] downloadToByteArray(URL source) {
        Proxy proxy = getProxyConfiguration();
        byte[] byteArray = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            InputStream inputStream;
            if (proxy == null) {
                inputStream = source.openConnection().getInputStream();
            } else {
                inputStream = source.openConnection(proxy).getInputStream();
            }
            byte[] buffer = new byte[32 * 1024];
            int len;
            while ((len = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            // out stream is closed by try-with-resources
            byteArray = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public Proxy getProxyConfiguration() {
        InetSocketAddress proxyAddress = getProxyAddress();
        if (proxyAddress != null) {
            return new Proxy(Proxy.Type.HTTP, proxyAddress);
        }
        // fallback for wrong data
        return null;
    }
}
