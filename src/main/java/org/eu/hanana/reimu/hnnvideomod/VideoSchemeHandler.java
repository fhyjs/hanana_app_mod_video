// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package org.eu.hanana.reimu.hnnvideomod;

import org.apache.commons.lang3.StringEscapeUtils;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;
import org.eu.hanana.reimu.hnnapp.Datas;
import org.eu.hanana.reimu.hnnapp.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Vector;

/**
 * The example for the second scheme with domain handling is a more
 * complex example and is taken from the parent project CEF. Please
 * see CEF: "cefclient/scheme_test.cpp" for futher details
 */
public class VideoSchemeHandler extends CefResourceHandlerAdapter {
    public static final String scheme = "video";
    public static final String domain = "player";

    private byte[] data_;
    private String mime_type_;
    private int offset_ = 0;

    public VideoSchemeHandler() {
        super();
    }

    @Override
    public synchronized boolean processRequest(CefRequest request, CefCallback callback) {
        boolean handled = false;
        String url = request.getURL();
        data_ = "".getBytes();
        if (url.contains("plugin.js")) {
            // Build the response html
            String html;
            html = Utils.getAssets(VideoMod.class.getClassLoader(),"video/plugin.js");

            data_ = html.getBytes();

            handled = true;
            // Set the resulting mime type
            mime_type_ = "application/javascript";
        }
        if (url.contains("create")) {
            String[] data = URLDecoder.decode(request.toString().split("\n\n")[1].split("data=")[1]).split("->");
            String dData=null;
            if (data[2]!=null){
                try {
                    dData= org.eu.hanana.reimu.hnnvideomod.Utils.fetchTextFromURL(data[2]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            VlcPlayer player = new VlcPlayer(Datas.mainFrame,dData);
            player.play(data[1]);
            VideoMod.PLAYER_MAP.put(data[0],player);
            handled=true;
            mime_type_ = "application/javascript";
        }
        if (url.contains("move")) {
            String[] data = URLDecoder.decode(request.toString().split("\n\n")[1].split("data=")[1]).split("->");
            VlcPlayer player = VideoMod.PLAYER_MAP.get(data[0]);
            //player.frame.setLocation((int)Float.parseFloat(data[1]),(int)Float.parseFloat(data[2]));
            player.frame.setLocation(Datas.mainFrame.getX()+(int)Float.parseFloat(data[1]),Datas.mainFrame.getY()+(int)Float.parseFloat(data[2]));
            handled=true;
        }
        if (handled) {
            // Indicate the headers are available.
            callback.Continue();
            return true;
        }

        return false;
    }

    @Override
    public void getResponseHeaders(
            CefResponse response, IntRef response_length, StringRef redirectUrl) {
        response.setMimeType(mime_type_);
        response.setStatus(200);
        response.setHeaderByName("Access-Control-Allow-Origin","*",true);

        // Set the resulting response length
        response_length.set(data_.length);
    }

    @Override
    public synchronized boolean readResponse(
            byte[] data_out, int bytes_to_read, IntRef bytes_read, CefCallback callback) {
        boolean has_data = false;

        if (offset_ < data_.length) {
            // Copy the next block of data into the buffer.
            int transfer_size = Math.min(bytes_to_read, (data_.length - offset_));
            System.arraycopy(data_, offset_, data_out, 0, transfer_size);
            offset_ += transfer_size;

            bytes_read.set(transfer_size);
            has_data = true;
        } else {
            offset_ = 0;
            bytes_read.set(0);
        }

        return has_data;
    }

    private boolean loadContent(String resName) {
        InputStream inStream = getClass().getResourceAsStream(resName);
        if (inStream != null) {
            try {
                ByteArrayOutputStream outFile = new ByteArrayOutputStream();
                int readByte = -1;
                while ((readByte = inStream.read()) >= 0) outFile.write(readByte);
                data_ = outFile.toByteArray();
                return true;
            } catch (IOException e) {
            }
        }
        return false;
    }
}
