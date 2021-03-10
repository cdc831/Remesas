package ar.com.istorming;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import com.google.common.net.HttpHeaders;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ManejadorREST {

	private String url;
	private String user;
	private String pass;
	private String urlProxy;
	private int portProxy;
	
	
	public ManejadorREST() {
		
	}

	public HttpEntity enviarInformacion(String json) throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException, ClientProtocolException, IOException {
		HttpClient httpClient = HttpClientBuilder.create().setProxy(new HttpHost(urlProxy, portProxy)).build();
		httpClient = getAllSSLClient();
		String encoding = Base64.getEncoder().encodeToString((this.user + ":" + this.pass).getBytes("UTF-8"));
		HttpPost request = new HttpPost(this.url);
		request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
		StringEntity params = new StringEntity(json, ContentType.APPLICATION_JSON);
		request.setEntity(params);
		HttpResponse response = httpClient.execute(request);
		HttpEntity responseEntity = response.getEntity();
		return responseEntity;
	}
	
	public JsonObject obtenerJsonRepuesta(String repuestaApi) {
		JsonParser parser = new JsonParser();
		JsonElement unJson = parser.parse(repuestaApi);
		return unJson.getAsJsonObject();
	}
	
	public String encodearBase64(String file) {
	    try {
	        byte[] fileContent = Files.readAllBytes(new File(file).toPath());
	        return Base64.getEncoder().encodeToString(fileContent);
	    } catch (IOException e) {
	        throw new IllegalStateException("could not read file " + file, e);
	    }
	}


	
	public static HttpClient getAllSSLClient()
			throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };
		SSLContext context = SSLContext.getInstance("SSL");
		context.init(null, trustAllCerts, null);

		HttpClientBuilder builder = HttpClientBuilder.create();
		SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(context,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		builder.setSSLSocketFactory(sslConnectionFactory);

		PlainConnectionSocketFactory plainConnectionSocketFactory = new PlainConnectionSocketFactory();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslConnectionFactory).register("http", plainConnectionSocketFactory).build();

		HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
		builder.setConnectionManager(ccm);
		return builder.build();
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getUrlProxy() {
		return urlProxy;
	}

	public void setUrlProxy(String urlProxy) {
		this.urlProxy = urlProxy;
	}

	public int getPortProxy() {
		return portProxy;
	}

	public void setPortProxy(int portProxy) {
		this.portProxy = portProxy;
	}

	

	
	
}
