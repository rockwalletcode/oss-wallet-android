package com.platform.kvstore;

import android.util.Log;

import com.breadwallet.tools.util.Utils;
import com.platform.APIClient;
import com.platform.interfaces.KVStoreAdaptor;
import com.platform.sqlite.KVItem;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;

import static android.R.attr.key;
import static com.platform.kvstore.CompletionObject.RemoteKVStoreError.unknown;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan on <mihail@breadwallet.com> 11/13/16.
 * Copyright (c) 2016 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class RemoteKVStore implements KVStoreAdaptor {
    public static final String TAG = RemoteKVStore.class.getName();

    // Headers in response are back in lowercase.
    private static final String HEADER_ETAG = "etag";
    private static final String HEADER_LAST_MODIFIED = "last-modified";

    private static RemoteKVStore instance;
    private APIClient apiClient;

    private RemoteKVStore(APIClient apiClient) {
        this.apiClient = apiClient;
    }

    public static RemoteKVStore getInstance(APIClient apiClient) {
        if (instance == null)
            instance = new RemoteKVStore(apiClient);
        return instance;
    }

    @Override
    public CompletionObject ver(String key) {
        long v;
        long t;
        String url = String.format("%s/kv/1/%s", APIClient.getBaseWalletApiURL(), key);
        okhttp3.Request request;
        request = new okhttp3.Request.Builder()
                .url(url)
                .head()
                .build();

        APIClient.BRResponse res = apiClient.sendRequest(request, true);
        if (!res.isSuccessful()) {
            return new CompletionObject(0, 0, extractErr(res));
        }
        v = extractVersion(res);
        t = extractDate(res);
        return new CompletionObject(v, t, extractErr(res));
    }

    @Override
    public CompletionObject put(String key, byte[] value, long version) {
        String url = String.format("%s/kv/1/%s", APIClient.getBaseWalletApiURL(), key);
        RequestBody requestBody = RequestBody.create(null, value);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("If-None-Match", String.valueOf(version))
                .addHeader("Content-Type", "application/octet-stream")
                .addHeader("Content-Length", String.valueOf(value.length))
                .build();
        long v;
        long t;
        APIClient.BRResponse res = apiClient.sendRequest(request, true);

        if (!res.isSuccessful()) {
            Log.e(TAG, "put: [KV] PUT key=" + key + ", err=" + (res.getCode()));
            return new CompletionObject(0, 0, extractErr(res));
        }

        v = extractVersion(res);
        t = extractDate(res);
        return new CompletionObject(v, t, extractErr(res));
    }

    @Override
    public CompletionObject del(String key, long version) {
        String url = String.format("%s/kv/1/%s", APIClient.getBaseWalletApiURL(), key);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .delete()
                .addHeader("If-None-Match", String.valueOf(version))
                .build();
        long v;
        long t;
        APIClient.BRResponse res = apiClient.sendRequest(request, true);
        if (res == null) {
            Log.d(TAG, "del: [KV] PUT key=" + key + ", err= response is null (maybe auth challenge)");
            return new CompletionObject(0, 0, extractErr(res));
        }

        if (!res.isSuccessful()) {
            Log.e(TAG, "del: [KV] PUT key=" + key + ", err=" + (res.getCode()));
            return new CompletionObject(0, 0, extractErr(res));
        }

        v = extractVersion(res);
        t = extractDate(res);
        return new CompletionObject(v, t, extractErr(res));
    }

    @Override
    public CompletionObject get(String key, long version) {
        String url = String.format("%s/kv/1/%s", APIClient.getBaseWalletApiURL(), key);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .addHeader("If-None-Match", String.valueOf(version))
                .build();
        long v;
        long t;
        byte[] value;
        APIClient.BRResponse res;
        try {
            res = apiClient.sendRequest(request, true);
            if (res == null) {
                Log.d(TAG, "get: [KV] PUT key=" + key + ", err= response is null (maybe auth challenge)");
                return new CompletionObject(0, 0, unknown);
            }

            if (!res.isSuccessful()) {
                return new CompletionObject(0, 0, extractErr(res));
            }
            v = extractVersion(res);
            t = extractDate(res);
            value = res.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return new CompletionObject(unknown);
        }
        return new CompletionObject(v, t, value, extractErr(res));
    }

    @Override
    public CompletionObject keys() {
        String url = String.format("%s/kv/_all_keys", APIClient.getBaseWalletApiURL());
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        List<KVItem> keys = new ArrayList<>();
        APIClient.BRResponse res = apiClient.sendRequest(request, true);
        if (res == null) {
            Log.d(TAG, "keys: [KV] PUT key=" + key + ", err= response is null (maybe auth challenge)");
            return new CompletionObject(0, 0, unknown);
        }
        if (!res.isSuccessful()) {
            Log.e(TAG, "keys: [KV] PUT key=" + key + ", err=" + (res.getCode()));
            return new CompletionObject(0, 0, extractErr(res));
        }

        byte[] reqData = res.getBody();

        if (Utils.isNullOrEmpty(reqData)) return new CompletionObject(unknown);

        ByteBuffer buffer = ByteBuffer.wrap(reqData).order(java.nio.ByteOrder.LITTLE_ENDIAN);

        try {
            int count = buffer.getInt();

            for (int i = 0; i < count; i++) {
                String key = null;
                long version = 0;
                long time = 0;
                byte deleted = 0;

                int keyLen = buffer.getInt();

                byte[] keyBytes = new byte[keyLen];
                buffer.get(keyBytes, 0, keyLen);
                key = new String(keyBytes, StandardCharsets.UTF_8);

                version = buffer.getLong();
                time = buffer.getLong();
                deleted = buffer.get();
                if (key == null || key.isEmpty()) return new CompletionObject(0, 0, unknown);
                keys.add(new KVItem(0, version, key, null, time, deleted));

            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CompletionObject(0, 0, unknown);
        }
        return new CompletionObject(keys, null);
    }

    private long extractVersion(APIClient.BRResponse res) {
        long remoteVersion = 0;
        try {
            if (res != null) {
                String eTag = res.getHeaders().get(HEADER_ETAG);
                if (!Utils.isNullOrEmpty(eTag)) {
                    remoteVersion = Long.parseLong(eTag);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return remoteVersion;
    }

    private long extractDate(APIClient.BRResponse res) {
        long remoteTime = 0;
        try {
            if (res != null) {
                String lastModified = res.getHeaders().get(HEADER_LAST_MODIFIED);
                if (!Utils.isNullOrEmpty(lastModified))
                    remoteTime = Date.parse(lastModified);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return remoteTime;
    }

    private CompletionObject.RemoteKVStoreError extractErr(APIClient.BRResponse res) {
        if (res == null) return CompletionObject.RemoteKVStoreError.unknown;
        int code = res.getCode();
        if (code <= 399 && code >= 200) code = 999;
        switch (code) {
            case 404:
                return CompletionObject.RemoteKVStoreError.notFound;
            case 409:
                return CompletionObject.RemoteKVStoreError.conflict;
            case 410:
                return CompletionObject.RemoteKVStoreError.tombstone;
            case 999:
                return null;
            default:
                return CompletionObject.RemoteKVStoreError.unknown;
        }
    }
}
