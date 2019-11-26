package com.chenerzhu.crawler.proxy.pool.util;

import com.chenerzhu.crawler.proxy.pool.common.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
/**
 * @author chenerzhu
 * @create 2018-08-11 11:25
 **/
@Slf4j
public class HttpClientUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static String send(final String url, String content, Map<String, String> headerMap,Map<String, String> formParamMap, String contentCharset, String resultCharset, HttpMethod method) {
        if (StringUtils.isEmpty(contentCharset)) {
            contentCharset = DEFAULT_CHARSET;
        }
        try {
            Request request = null;
            Response response = null;
            switch (method) {
                case GET:
                    request = Request.Get(url);
                    request.connectTimeout(5000);
                    for(String k : headerMap.keySet()){
                        request.setHeader(k,headerMap.get(k));
                    }
                    response = request.execute();
                    break;
                case POST:

                    request = Request.Post(url);
                    request.connectTimeout(5000);
                    for(String k : headerMap.keySet()){
                        request.setHeader(k,headerMap.get(k));
                    }
                    if(formParamMap==null||formParamMap.isEmpty()){
                        request.body(new StringEntity(content, contentCharset));
                    }else{
                        List<NameValuePair> ls = new ArrayList<NameValuePair>();
                        for(Map.Entry<String, String> param:formParamMap.entrySet()){
                            ls.add(new BasicNameValuePair(param.getKey(),param.getValue()));
                        }
                        request.body(new UrlEncodedFormEntity(ls,"UTF-8"));
                    }
                    response = request.execute();
                    break;
                case DELETE:
                    request = Request.Delete(url);
                    request.connectTimeout(5000);
                    for(String k : headerMap.keySet()){
                        request.setHeader(k,headerMap.get(k));
                    }
                    response = request.execute();
                    break;
                case PUT:
                    request = Request.Put(url);
                    request.connectTimeout(5000);
                    for(String k : headerMap.keySet()){
                        request.setHeader(k,headerMap.get(k));
                    }
                    request.body(new StringEntity(content, contentCharset));
                    response = request.execute();
                    break;
                case PATCH:
                    request = Request.Patch(url);
                    request.connectTimeout(5000);
                    for(String k : headerMap.keySet()){
                        request.setHeader(k,headerMap.get(k));
                    }
                    request.body(new StringEntity(content, contentCharset));
                    response = request.execute();
                    break;
            }
            final HttpResponse _response = response.returnResponse(); // content will be consume only once

            log.info("request url：" + url + "; response status：" + _response.getStatusLine());
            if (_response.getStatusLine().getStatusCode() == 200) {
                BufferedHttpEntity entity = new BufferedHttpEntity(_response.getEntity());
                //获取响应内容
                if (StringUtils.isEmpty(resultCharset)) {
                    resultCharset = DEFAULT_CHARSET;
                }
                return EntityUtils.toString(entity, resultCharset);
            }
        } catch (ClientProtocolException e) {
            log.error("Protocol error", e);
        } catch (IOException e) {
            log.error("Network error", e);
        }
        return null;
    }

    /**
     * 添加请求头
     *
     * @param httpRequest
     * @param headerMap
     * @return
     */
    private static HttpRequestBase addHeader(HttpRequestBase httpRequest, Map<String, String> headerMap) {
        if (headerMap != null && !headerMap.isEmpty()) {
            Set<String> keys = headerMap.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                httpRequest.addHeader(key, headerMap.get(key));
            }
        }
        return httpRequest;
    }

    /**
     * @param url 路径
     * @return int
     * @author
     * @date
     */
    private static int getPort(String url) {
        int startIndex = url.indexOf("://") + "://".length();
        String host = url.substring(startIndex);
        if (host.indexOf("/") != -1) {
            host = host.substring(0, host.indexOf("/"));
        }
        int port = 443;
        if (host.contains(":")) {
            int i = host.indexOf(":");
            port = new Integer(host.substring(i + 1));
        }
        return port;
    }

    /**
     * 初始化HTTPS请求服务
     *
     * @param httpClient HTTP客户端
     * @param port       端口
     */
    private static void initSSL(CloseableHttpClient httpClient, int port) {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            final X509TrustManager trustManager = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            // 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            ConnectionSocketFactory ssf = new SSLConnectionSocketFactory(sslContext);
            Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create().register("https", ssf).build();
            BasicHttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(r);
            HttpClients.custom().setConnectionManager(ccm).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String sendGet(final String url, Map<String, String> headerMap) {
        return sendGet(url, headerMap, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    public static String sendGet(final String url, Map<String, String> headerMap, String contentCharset, String resultCharset) {
        return send(url, "", headerMap,null, contentCharset, resultCharset, HttpMethod.GET);
    }

    public static String sendPost(final String url, String content, Map<String, String> headerMap) {
        return sendPost(url, content, headerMap, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    public static String sendPostForm(final String url, String content, Map<String, String> headerMap, Map<String, String> formParamMap) {
        return send(url, content, headerMap,formParamMap, DEFAULT_CHARSET, DEFAULT_CHARSET,HttpMethod.POST);
    }

    public static String sendPost(final String url, String content, Map<String, String> headerMap, String contentCharset, String resultCharset) {
        return send(url, content, headerMap,null, contentCharset, resultCharset, HttpMethod.POST);
    }

    public static String sendPostForm(final String url, String content, Map<String, String> headerMap, Map<String, String> formParamMap,String contentCharset, String resultCharset) {
        return send(url, content, headerMap, formParamMap,contentCharset, resultCharset, HttpMethod.POST);
    }

    public static String sendDelete(final String url, String content, Map<String, String> headerMap) {
        return sendDelete(url, content, headerMap, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    public static String sendDelete(final String url, String content, Map<String, String> headerMap, String contentCharset, String resultCharset) {
        return send(url, content, headerMap, null,contentCharset, resultCharset, HttpMethod.DELETE);
    }

    public static String sendPut(final String url, String content, Map<String, String> headerMap) {
        return sendPut(url, content, headerMap, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    public static String sendPut(final String url, String content, Map<String, String> headerMap, String contentCharset, String resultCharset) {
        return send(url, content, headerMap,null, contentCharset, resultCharset, HttpMethod.PUT);
    }

    public static String sendPatch(final String url, String content, Map<String, String> headerMap) {
        return sendPatch(url, content, headerMap, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    public static String sendPatch(final String url, String content, Map<String, String> headerMap, String contentCharset, String resultCharset) {
        return send(url, content, headerMap, null,contentCharset, resultCharset, HttpMethod.PATCH);
    }

    public static void main(String[] args) {
        String result = HttpClientUtils.sendGet("https://www.baidu.com", null);
        System.out.println(result);
    }
}