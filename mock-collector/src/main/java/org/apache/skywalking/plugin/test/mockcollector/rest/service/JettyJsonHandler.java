/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.plugin.test.mockcollector.rest.service;

import com.google.gson.JsonElement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.nonNull;

@Slf4j
public abstract class JettyJsonHandler extends HttpServlet {

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            reply(resp, doGet(req));
        } catch (IOException e) {
            try {
                replyError(resp, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
            } catch (IOException replyException) {
                log.error(replyException.getMessage(), e);
            }
        }
    }

    protected abstract JsonElement doGet(HttpServletRequest req);

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            reply(resp, doPost(req));
        } catch (IOException e) {
            try {
                replyError(resp, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
            } catch (IOException replyException) {
                log.error(replyException.getMessage(), e);
            }
        }
    }

    protected abstract JsonElement doPost(HttpServletRequest req) throws IOException;

    private void reply(HttpServletResponse response, JsonElement resJson) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();
        if (nonNull(resJson)) {
            out.print(resJson);
        }
        out.flush();
        out.close();
    }

    private void replyError(HttpServletResponse response, String errorMessage, int status) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(status);
        response.setHeader("error-message", errorMessage);

        PrintWriter out = response.getWriter();
        out.flush();
        out.close();
    }

    public String getJsonBody(HttpServletRequest req) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
        }
        return stringBuffer.toString();
    }
}
