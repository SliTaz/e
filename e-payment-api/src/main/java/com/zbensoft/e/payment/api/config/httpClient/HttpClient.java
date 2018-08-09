package com.zbensoft.e.payment.api.config.httpClient;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by admin on 2017/2/6.
 */
@Configuration
public class HttpClient {

    @Value("${httpClient.maxTotal}")
    private Integer maxTotal;

    @Value("${httpClient.defaultMaxPerRoute}")
    private Integer defaultMaxPerRoute;

    @Value("${httpClient.connectTimeout}")
    private Integer connectTimeout;

    @Value("${httpClient.connectionRequestTimeout}")
    private Integer connectionRequestTimeout;

    @Value("${httpClient.socketTimeout}")
    private Integer socketTimeout;

    @Value("${httpClient.staleConnectionCheckEnabled}")
    private boolean staleConnectionCheckEnabled;

    /**
     * 首先实例化一个连接池管理器，设置最大连接数、并发连接数
     * @return
     * @throws KeyStoreException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getHttpClientConnectionManager() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
    	
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
		    public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		        return true;
		    }
		}).build();
		
		X509HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,hostnameVerifier);
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
		        .register("http", PlainConnectionSocketFactory.getSocketFactory())
		        .register("https", sslSocketFactory)
		        .build();

//        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        //最大连接数
        httpClientConnectionManager.setMaxTotal(maxTotal);
        //并发数
        httpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return httpClientConnectionManager;
    }

    /**
     * 实例化连接池，设置连接池管理器。
     * 这里需要以参数形式注入上面实例化的连接池管理器
     * @param httpClientConnectionManager
     * @return
     * @throws Exception 
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(@Qualifier("httpClientConnectionManager")PoolingHttpClientConnectionManager httpClientConnectionManager) throws Exception{

		// HttpClientBuilder中的构造方法被protected修饰，所以这里不能直接使用new来实例化一个HttpClientBuilder，可以使用HttpClientBuilder提供的静态方法create()来获取HttpClientBuilder对象
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
		    public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		        return true;
		    }
		}).build();
		httpClientBuilder.setSslcontext(sslContext);
		httpClientBuilder.setConnectionManager(httpClientConnectionManager);

		return httpClientBuilder;
	}

    /**
     * 注入连接池，用于获取httpClient
     * @param httpClientBuilder
     * @return
     */
    @Bean
    public CloseableHttpClient getCloseableHttpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder){
        return httpClientBuilder.build();
    }

    /**
     * Builder是RequestConfig的一个内部类
     * 通过RequestConfig的custom方法来获取到一个Builder对象
     * 设置builder的连接信息
     * 这里还可以设置proxy，cookieSpec等属性。有需要的话可以在此设置
     * @return
     */
    @Bean(name = "builder")
    public RequestConfig.Builder getBuilder(){
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .setStaleConnectionCheckEnabled(staleConnectionCheckEnabled);
    }

	/**
	 * 使用builder构建一个RequestConfig对象
	 * 
	 * @param builder
	 * @return
	 */
	@Bean
	public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder) {
		return builder.build();
	}

}
