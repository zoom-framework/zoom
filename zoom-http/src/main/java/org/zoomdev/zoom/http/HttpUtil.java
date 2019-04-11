package org.zoomdev.zoom.http;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpUtil {
    public static int READ_TIMEOUT = 24000;
    public static int CONNECT_TIMEOUT = 12000;
    private static final SSLContext sc;
    private static final HostnameVerifier HOSTNAME_VERIFIER = new TrustAnyHostnameVerifier();
    static{
        try {
            sc = SSLContext.getInstance("SSL", "SunJSSE");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new SecureRandom());
        } catch (Exception e){
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

    public static void setConnections(HttpsURLConnection httpConnection){
        httpConnection.setSSLSocketFactory(sc.getSocketFactory());
        httpConnection.setHostnameVerifier(HOSTNAME_VERIFIER);
    }

    public static HttpURLConnection createGet(String url) throws IOException {
        return createConnection(url,"GET","UTF-8",null,0);
    }

    public static HttpURLConnection createConnection(
            String url,
            String method,
            String encoding,
            String contentType,
            int length)
            throws IOException {
        URL postUrl = new URL(url);
        HttpURLConnection connection = null;
        if(url.startsWith("https")){
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) postUrl.openConnection();
            setConnections(httpsURLConnection);
            connection = httpsURLConnection;
        }else{
            connection = (HttpURLConnection) postUrl.openConnection();
        }

        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Accept-Charset", encoding);
        if(length>0){
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Length", String.valueOf(length));
            connection.setRequestProperty("Content-Type",contentType);
            connection.setDoOutput(true);
        }else{
            connection.setRequestMethod(method);
        }
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);

        return connection;
    }
}
