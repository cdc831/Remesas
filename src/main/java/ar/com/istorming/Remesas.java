package ar.com.istorming;

import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class Remesas {

	private JsonObject ObjetoPrincipal;
	private Integer empresa;
	private String fecha;
	private String nombre;
	private String respuestaJson;
	private int idRemesa = 0 ;
	private String codRespuesta;
	private String descripcionRespuesta;
	private ManejadorREST mr;
	

	public Remesas() {
		ObjetoPrincipal = new JsonObject();
	}
	
	
	public void iniciarMR(String url , String user, String pass, String  urlProxy, int portProxy) {
		mr = new ManejadorREST();
		mr.setUser(user);
		mr.setPass(pass);
		mr.setUrl(url);
		mr.setUrlProxy(urlProxy);
		mr.setPortProxy(portProxy);
	}
	
	
	public void procesarDatos(int empresa, String nombre, String fecha ) {
		this.empresa = empresa;
		this.nombre = nombre;
		this.fecha = fecha;
	}

	public void enviaJsonRemesaPost() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException, ClientProtocolException, IOException {
		HttpEntity entity = mr.enviarInformacion(devuelveJsonRemesaPost());
		procesarRespuesta(EntityUtils.toString(entity));
	}
	
	public String devuelveJsonRemesaPost() {
		ObjetoPrincipal.addProperty("empresaCod", this.empresa);
		ObjetoPrincipal.addProperty("fechaRegistro", String.valueOf(this.fecha));
		ObjetoPrincipal.addProperty("remesaNombre", this.nombre);
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(ObjetoPrincipal);
	}

	public void procesarRespuesta(String respuestaApi) {
		JsonObject jsonObject = mr.obtenerJsonRepuesta(respuestaApi);
		try {
			setCodRespuesta(jsonObject.get("code").getAsString());
			setDescripcionRespuesta(jsonObject.get("description").getAsString());
			//En caso que sea respuesta 2 significa que la remesa ya existe para esta empresa y con el nombre estipulado
			if (this.codRespuesta.equals("2")) {
				this.setIdRemesa(0);
			}else {
				if (jsonObject.get("remesaCod") == null) {
					this.setIdRemesa(0);
				} else {
					this.setIdRemesa(jsonObject.get("remesaCod").getAsInt());
				}
			}
		} catch (Exception e) {
			this.setCodRespuesta("555");
			this.setDescripcionRespuesta("Error: " + e.getMessage());
			this.setIdRemesa(0);
		}
		
	}
	public Integer getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(Integer empresa) {
		this.empresa = empresa;
	}

	public String getFecha() {
		return this.fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getRespuestaJson() {
		return respuestaJson;
	}


	public void setRespuestaJson(String respuestaJson) {
		this.respuestaJson = respuestaJson;
	}


	public int getIdRemesa() {
		return idRemesa;
	}


	public void setIdRemesa(int idRemesa) {
		this.idRemesa = idRemesa;
	}


	public String getCodRespuesta() {
		return codRespuesta;
	}


	public void setCodRespuesta(String codRespuesta) {
		this.codRespuesta = codRespuesta;
	}


	public String getDescripcionRespuesta() {
		return descripcionRespuesta;
	}


	public void setDescripcionRespuesta(String descripcionRespuesta) {
		this.descripcionRespuesta = descripcionRespuesta;
	}
	
	
	
	

	
}
