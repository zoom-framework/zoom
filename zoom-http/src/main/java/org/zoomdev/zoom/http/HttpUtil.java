package org.zoomdev.zoom.http;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpUtil {

    private static final SSLContext sc;
    private static final HostnameVerifier HOSTNAME_VERIFIER = new TrustAnyHostnameVerifier();

    static {
        try {
            sc = SSLContext.getInstance("SSL", "SunJSSE");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new SecureRandom());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static void setConnections(HttpsURLConnection httpConnection) {
        httpConnection.setSSLSocketFactory(sc.getSocketFactory());
        httpConnection.setHostnameVerifier(HOSTNAME_VERIFIER);
    }



    public static Response execute(Request request) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = createConnection(request.url, request.method, request.contentType, request.body == null ? 0 : request.body.length);
            String encoding = request.encoding;
            if (encoding == null) {
                encoding = "UTF-8";
            }
            connection.setRequestProperty("Accept-Charset", encoding);
            if (request.headers != null) {
                for (Map.Entry<String, String> entry : request.headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if(request.body!=null){
                connection.setDoInput(true);

                connection.getOutputStream().write(request.body);
                connection.getOutputStream().flush();
            }

            connection.setConnectTimeout(request.readTimeout);
            connection.setReadTimeout(request.connectTimeout);
            Response response = new Response(connection, request);
            return response;
        } catch (IOException e) {
            throw e;
        }
    }

    //
    //       connection.setRequestProperty("Accept-Charset", encoding);
    public static HttpURLConnection createConnection(
            String url,
            String method,
            String contentType,
            int length)
            throws IOException {
        URL postUrl = new URL(url);
        HttpURLConnection connection = null;
        if (url.startsWith("https")) {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) postUrl.openConnection();
            setConnections(httpsURLConnection);
            connection = httpsURLConnection;
        } else {
            connection = (HttpURLConnection) postUrl.openConnection();
        }

        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        if (length > 0) {
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Length", String.valueOf(length));
            connection.setRequestProperty("Content-Type", contentType);
            connection.setDoOutput(true);
        } else {
            connection.setRequestMethod(method);
        }


        return connection;
    }
}
