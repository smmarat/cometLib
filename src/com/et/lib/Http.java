package com.et.lib;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: vp
 * Date: 15.02.16
 * Time: 14:01
 */
public class Http {

    public Http() {
        System.setProperty("http.keepAlive", "true");
        System.setProperty("http.maxConnections", "20");
    }

    public String postImg(String url, InputStream is) {
        return request(url, "image/png", is, false);
    }

    public String post(String url, String s) {
        return post(url, s, false);
    }

    public String post(String url, String s, boolean isComet) {
        ByteArrayInputStream data = new ByteArrayInputStream(s.getBytes());
        return request(url, null, data, isComet);
    }

    public String get(String url) {
        return request(url, null, null, false);
    }

    public String get(String url, boolean isComet) {
        return request(url, null, null, isComet);
    }

    public String request(String url, String conntype, InputStream data, boolean isComet) {

        String rez = null;
        HttpsURLConnection conn = null;
        try {
            URL a = new URL(url);
            conn = (HttpsURLConnection) a.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(isComet ? 65000 : 10000);
            if (data != null) {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setChunkedStreamingMode(0);
            } else {
                conn.setRequestMethod("GET");
            }
            if (conntype != null) {
                conn.setRequestProperty("Content-type", conntype);
            }
            conn.setRequestProperty("Accept-Encoding", "gzip");
            if (data != null) {
                OutputStream os = conn.getOutputStream();
                byte[] buffer = new byte[512];
                int len;
                while ((len = data.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                os.flush();
                os.close();
            }
            InputStream is = conn.getInputStream();
            rez = read(is, "gzip".equals(conn.getContentEncoding()));
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    int respCode = conn.getResponseCode();
                    System.out.println("resp code " + respCode);
                    InputStream es = conn.getErrorStream();
                    rez = read(es, "gzip".equals(conn.getContentEncoding()));
                    if (es != null) es.close();
                }
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rez;
    }

    private String read(InputStream is, boolean isZip) throws IOException {
        if (is == null) return "";
        StringBuilder sb = new StringBuilder();
        BufferedReader reader;
        if (isZip) reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(is)));
        else reader = new BufferedReader(new InputStreamReader(is));
        String s;
        while ((s = reader.readLine()) != null) sb.append(s);
        return sb.toString();
    }
}
