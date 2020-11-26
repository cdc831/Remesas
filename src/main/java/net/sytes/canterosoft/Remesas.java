package net.sytes.canterosoft;

import com.google.common.net.HttpHeaders;
import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;
import java.io.Closeable;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class Remesas {

    private JsonObject ObjetoPrincipal;
    private Integer empresa;
    private String fecha;
    private String nombre;
    private String url;
    private String authUser;
    private String authPass;
    private String codRespuesta;
    private String mensajeRespuesta;
    private Integer idRemesaRespuesta;
    private String proxy;
    private Integer puerto;
    private String autorizacionToken;

    public Remesas(){
        ObjetoPrincipal = new JsonObject();
        autorizacionToken = "";
    }

    public static void main(String args[]){
        Remesas rem = new Remesas();
//        rem.ping();
        rem.setEmpresa(29);
        rem.setNombre("PruebaServicio Cristian  ");
        rem.setFecha("2020-10-27");
        rem.setAuthUser("prosegurws");
        rem.setAuthPass("asRExrQQ5FaQWXcEg2tf");
        //rem.setProxy("proxy.emea.prosegur.local");
        rem.setProxy("AR_PROXY.LATAM1.PROSEGUR.LOCAL");
        rem.setPuerto(8080);
        rem.setUrl("https://trs005.tusrecibos.com.py/rest/mtess/remesas/service.php");
        rem.enviaJsonRemesaPost();
        //rem.enviaJsonRemesaPostSinProxy();
        System.out.println("Codigo: " + rem.getCodRespuesta());
        System.out.println("Descripcion: " + rem.getMensajeRespuesta());
        System.out.println("RemesaID: " + rem.getIdRemesaRespuesta());
    }

    public void enviaJsonRemesaPostSinProxy() {
        String informacion = "{pre-envio}";
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            String encoding = Base64.getEncoder().encodeToString((this.authUser + ":" + this.authPass).getBytes("UTF-8"));
            HttpPost request = new HttpPost(this.url);
            request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);

            StringEntity params = new StringEntity(devuelveJsonRemesaPost(), ContentType.APPLICATION_JSON);
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);

            HttpEntity responseEntity = response.getEntity();
            informacion = "{post-envio}";
            if(responseEntity!=null) {
                informacion = EntityUtils.toString(responseEntity);
                procesarRespuesta(informacion);
            }else{
                setCodRespuesta("555");
                setMensajeRespuesta("Error: responseEntity es nulo, esto ocurro al tratar de obtener la respues del servicio");
                setIdRemesaRespuesta(0);
            }
            EntityUtils.consume(responseEntity);
            ((Closeable) response).close();
        } catch (Exception ex) {
            informacion = "{error-envio post sin proxy:" + ex.getMessage() + "}";
            ex.printStackTrace();
        }
    }

    public void enviaJsonRemesaPost() {
        String informacion = "{pre-envio}";
        //HttpClient httpClient = HttpClientBuilder.create().build();
        HttpClient httpClient = HttpClientBuilder.create().setProxy(new HttpHost(getProxy(),getPuerto())).build();
        try {
            httpClient = getAllSSLClient();
        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        try {
            String encoding = Base64.getEncoder().encodeToString((this.authUser + ":" + this.authPass).getBytes("UTF-8"));
            HttpPost request = new HttpPost(this.url);
            request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
            StringEntity params = new StringEntity(devuelveJsonRemesaPost(), ContentType.APPLICATION_JSON);

            // Token del Proxy de Prosegur
            if (this.autorizacionToken.length() > 0 ) {
                request.addHeader(HttpHeaders.AUTHORIZATION, this.autorizacionToken);
            }

            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);

            HttpEntity responseEntity = response.getEntity();
            informacion = "{post-envio}";
            if(responseEntity!=null) {
                informacion = EntityUtils.toString(responseEntity);
                procesarRespuesta(informacion);
            }else{
                setCodRespuesta("555");
                setMensajeRespuesta("Error: responseEntity es nulo, esto ocurro al tratar de obtener la respues del servicio");
                setIdRemesaRespuesta(0);
            }
            EntityUtils.consume(responseEntity);
            ((Closeable) response).close();
        } catch (Exception ex) {
            informacion = "{error-envio post sin proxy:" + ex.getMessage() + "}";
            ex.printStackTrace();
        }
    }

    public String devuelveJsonRemesaPost(){

        ObjetoPrincipal.addProperty("empresaCod",this.empresa);
        ObjetoPrincipal.addProperty("fechaRegistro", String.valueOf(this.fecha));
        ObjetoPrincipal.addProperty("remesaNombre",this.nombre);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        return gson.toJson(ObjetoPrincipal);
    }

    public void procesarRespuesta(String respuestaApi){
        JsonObject jsonObject = obtenerJsonRepuesta(respuestaApi);
        try{
            setCodRespuesta(jsonObject.get("code").getAsString());
            setMensajeRespuesta(jsonObject.get("description").getAsString());
            if (jsonObject.get("remesaCod") == null) {
                setIdRemesaRespuesta(0);
            }else{
                setIdRemesaRespuesta(jsonObject.get("remesaCod").getAsInt());
            }
        }catch (Exception e){
            setCodRespuesta("555");
            setMensajeRespuesta("Error: " + e.getMessage());
            setIdRemesaRespuesta(0);
        }

    }

    public JsonObject obtenerJsonRepuesta(String repuestaApi){
        JsonParser parser = new JsonParser();
        JsonElement unJson = parser.parse(repuestaApi);
        return unJson.getAsJsonObject();
    }

    /*
    * * Metodo utilizado para ver si las variables estan cargadas
    */
    public void mostrarVariables(){
        JOptionPane.showMessageDialog(null,this.codRespuesta);
        JOptionPane.showMessageDialog(null,this.mensajeRespuesta);
        JOptionPane.showMessageDialog(null,this.idRemesaRespuesta);
    }

    public static HttpClient getAllSSLClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
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
        }};
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, trustAllCerts, null);

        HttpClientBuilder builder = HttpClientBuilder.create();
        SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(context, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        builder.setSSLSocketFactory(sslConnectionFactory);

        PlainConnectionSocketFactory plainConnectionSocketFactory = new PlainConnectionSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslConnectionFactory)
                .register("http", plainConnectionSocketFactory)
                .build();

        HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
        builder.setConnectionManager(ccm);
        return builder.build();
    }

    public boolean ping(){
        // Para probar haciendo ping y chequear que funcione el host
        String ipAddress = "181.40.66.32";
        try {
            InetAddress inet = InetAddress.getByName(ipAddress);
            JOptionPane.showMessageDialog(null,"Envío de solicitud de ping a " + ipAddress);
            JOptionPane.showMessageDialog(null,inet.isReachable(5000) ? "El host es accesible" : "El host es inaccesible");
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Ocurrio un error al tratar de conseguir la ip.");
            System.out.println(e.getMessage());
        }
        return true;
    }
    public Integer getEmpresa() { return this.empresa; }

    public void setEmpresa(Integer empresa) { this.empresa = empresa; }

    public String getFecha() { return this.fecha; }

    public void setFecha(String fecha) {this.fecha = fecha; }

    public String getNombre() { return this.nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }

    public String getAuthUser() {return this.authUser; }

    public void setAuthUser(String authUser) {this.authUser = authUser; }

    public String getAuthPass() {return this.authPass; }

    public void setAuthPass(String authPass) {this.authPass = authPass; }

    public String getCodRespuesta() { return this.codRespuesta; }

    public void setCodRespuesta(String codRespuesta) { this.codRespuesta = codRespuesta; }

    public String getMensajeRespuesta() { return this.mensajeRespuesta; }

    public void setMensajeRespuesta(String mensajeRespuesta) { this.mensajeRespuesta = mensajeRespuesta; }

    public Integer getIdRemesaRespuesta() { return this.idRemesaRespuesta; }

    public void setIdRemesaRespuesta(Integer idRemesaRespuesta) { this.idRemesaRespuesta = idRemesaRespuesta; }

    public String getProxy() { return proxy; }

    public void setProxy(String proxy) { this.proxy = proxy; }

    public Integer getPuerto() { return puerto; }

    public void setPuerto(Integer puerto) { this.puerto = puerto; }

    public void setAutorizacionToken(String autorizacion) {
        this.autorizacionToken = autorizacion.replace("<char34>", "\"");
    }

    public String getAutorizacionToken() { return this.autorizacionToken; }
}
