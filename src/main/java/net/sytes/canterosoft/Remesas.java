package net.sytes.canterosoft;

import com.google.common.net.HttpHeaders;
import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.Closeable;
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


    public Remesas(){
        ObjetoPrincipal = new JsonObject();
    }

    public static void main(String args[]){
//        setEmpresa(29);
//        setNombre("PruebaServicio Cristian  ");
//        setFecha("2020-10-27");
//        setAuthUser("prosegurws");
//        setAuthPass("asRExrQQ5FaQWXcEg2tf");
//        setUrl("https://trs005.tusrecibos.com.py/rest/mtess/remesas/service.php");
//        enviaJsonRemesaPost();
//        System.out.println("Codigo: " + getCodRespuesta());
//        System.out.println("Descripcion: " + getMensajeRespuesta());
//        System.out.println("RemesaID: " + getIdRemesaRespuesta());
    }

    public void enviaJsonRemesaPost() {
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
                getCodRespuesta();
                getMensajeRespuesta();
                getIdRemesaRespuesta();
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

    public Integer getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Integer empresa) {
        this.empresa = empresa;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {this.fecha = fecha; }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthUser() {return authUser; }

    public void setAuthUser(String authUser) {this.authUser = authUser; }

    public String getAuthPass() {return authPass; }

    public void setAuthPass(String authPass) {this.authPass = authPass; }

    public String getCodRespuesta() { return codRespuesta; }

    public void setCodRespuesta(String codRespuesta) { this.codRespuesta = codRespuesta; }

    public String getMensajeRespuesta() { return mensajeRespuesta; }

    public void setMensajeRespuesta(String mensajeRespuesta) { this.mensajeRespuesta = mensajeRespuesta; }

    public Integer getIdRemesaRespuesta() { return idRemesaRespuesta; }

    public void setIdRemesaRespuesta(Integer idRemesaRespuesta) { this.idRemesaRespuesta = idRemesaRespuesta; }
}
