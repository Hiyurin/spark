/*
 * This file is part of bytebin, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package me.lucko.spark.util;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

/**
 * Utility for posting content to bytebin.
 */
public class BytebinClient {

    /** The bytebin URL */
    private final String url;
    /** The client user agent */
    private final String userAgent;
    /** The http client */
    protected final OkHttpClient okHttp;

    /**
     * Creates a new bytebin instance
     *
     * @param url the bytebin url
     * @param userAgent the client user agent string
     */
    public BytebinClient(String url, String userAgent) {
        if (url.endsWith("/")) {
            this.url = url + "post";
        } else {
            this.url = url + "/post";
        }
        this.userAgent = userAgent;
        this.okHttp = new OkHttpClient();
    }

    /**
     * Posts GZIP compressed content to bytebin.
     *
     * @param buf the compressed content
     * @param contentType the type of the content
     * @return the key of the resultant content
     * @throws IOException if an error occurs
     */
    public String postGzippedContent(byte[] buf, MediaType contentType) throws IOException {
        RequestBody body = RequestBody.create(contentType, buf);

        Request.Builder requestBuilder = new Request.Builder()
                .url(this.url)
                .header("User-Agent", this.userAgent)
                .header("Content-Encoding", "gzip")
                .post(body);

        Request request = requestBuilder.build();
        try (Response response = makeHttpRequest(request)) {
            return response.header("Location");
        }
    }

    protected Response makeHttpRequest(Request request) throws IOException {
        Response response = this.okHttp.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("Request was unsuccessful: " + response.code() + " - " + response.message());
        }
        return response;
    }
}