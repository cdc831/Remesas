package ar.com.istorming;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Recibos {

	private String empresaDocumentoTipo;
	private String empresaDocumento;
	private String remesaCod;
	private String empleadoDocumentoTipo;
	private String empleadoDocumento;
	private String reciboTipo;
	private String reciboFecha;
	private String reciboPDF;
	private String codRespuesta;
	private String descripcionRespuesta;
	private int codRecibo;
	private JsonObject ObjetoPrincipal;
	private ManejadorREST mr;

	public void iniciarMR(String url, String user, String pass, String urlProxy, int portProxy) {
		mr = new ManejadorREST();
		mr.setUser(user);
		mr.setPass(pass);
		mr.setUrl(url);
		mr.setUrlProxy(urlProxy);
		mr.setPortProxy(portProxy);
	}

	public void procesarDatos(String empresaDocumentoTipo, String empresaDocumento, String remesaCod,
			String empleadoDocumentoTipo, String empleadoDocumento, String reciboTipo, String reciboFecha,
			String reciboPDF) {
		this.empresaDocumentoTipo = empresaDocumentoTipo;
		this.empresaDocumento = empresaDocumento;
		this.remesaCod = remesaCod;
		this.empleadoDocumentoTipo = empleadoDocumentoTipo;
		this.empleadoDocumento = empleadoDocumento;
		this.reciboTipo = reciboTipo;
		this.reciboFecha = reciboFecha;
		this.reciboPDF = mr.encodearBase64(reciboPDF);
	}

	public Recibos() {
		ObjetoPrincipal = new JsonObject();
	}
	
	public void enviaJsonRecibosPost() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException, ClientProtocolException, IOException {
		HttpEntity entity = mr.enviarInformacion(devuelveJsonRecibosPost());
		procesarRespuesta(EntityUtils.toString(entity));
	}

	public String devuelveJsonRecibosPost() {
		ObjetoPrincipal.addProperty("empresaDocumentoTipo", this.empresaDocumentoTipo);
		ObjetoPrincipal.addProperty("empresaDocumento", String.valueOf(this.empresaDocumento));
		ObjetoPrincipal.addProperty("remesaCod", this.remesaCod);
		ObjetoPrincipal.addProperty("empleadoDocumentoTipo", this.empleadoDocumentoTipo);
		ObjetoPrincipal.addProperty("empleadoDocumento", this.empleadoDocumento);
		ObjetoPrincipal.addProperty("reciboTipo", this.reciboTipo);
		ObjetoPrincipal.addProperty("reciboFecha", this.reciboFecha);
		ObjetoPrincipal.addProperty("reciboPDF", this.reciboPDF);

		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(ObjetoPrincipal);
	}
	
	
	public void procesarRespuesta(String respuestaApi) {
		JsonObject jsonObject = mr.obtenerJsonRepuesta(respuestaApi);
		try {
			setCodRespuesta(jsonObject.get("code").getAsString());
			setDescripcionRespuesta(jsonObject.get("description").getAsString());
			//En caso que sea respuesta 2 significa que la Recibo ya existe para esta empresa y con el nombre estipulado
			if (this.codRespuesta.equals("2")) {
				this.setCodRecibo(0);
			}else {
				if (jsonObject.get("reciboCod") == null) {
					this.setCodRecibo(0);
				} else {
					this.setCodRecibo(jsonObject.get("reciboCod").getAsInt());
				}
			}
		} catch (Exception e) {
			this.setCodRespuesta("555");
			this.setDescripcionRespuesta("Error: " + e.getMessage());
			this.setCodRecibo(0);
		}
		
	}

	public String getEmpresaDocumentoTipo() {
		return empresaDocumentoTipo;
	}

	public void setEmpresaDocumentoTipo(String empresaDocumentoTipo) {
		this.empresaDocumentoTipo = empresaDocumentoTipo;
	}

	public String getEmpresaDocumento() {
		return empresaDocumento;
	}

	public void setEmpresaDocumento(String empresaDocumento) {
		this.empresaDocumento = empresaDocumento;
	}

	public String getRemesaCod() {
		return remesaCod;
	}

	public void setRemesaCod(String remesaCod) {
		this.remesaCod = remesaCod;
	}

	public String getEmpleadoDocumentoTipo() {
		return empleadoDocumentoTipo;
	}

	public void setEmpleadoDocumentoTipo(String empleadoDocumentoTipo) {
		this.empleadoDocumentoTipo = empleadoDocumentoTipo;
	}

	public String getEmpleadoDocumento() {
		return empleadoDocumento;
	}

	public void setEmpleadoDocumento(String empleadoDocumento) {
		this.empleadoDocumento = empleadoDocumento;
	}

	public String getReciboTipo() {
		return reciboTipo;
	}

	public void setReciboTipo(String reciboTipo) {
		this.reciboTipo = reciboTipo;
	}

	public String getReciboFecha() {
		return reciboFecha;
	}

	public void setReciboFecha(String reciboFecha) {
		this.reciboFecha = reciboFecha;
	}

	public String getReciboPDF() {
		return reciboPDF;
	}

	public void setReciboPDF(String reciboPDF) {
		this.reciboPDF = reciboPDF;
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

	public int getCodRecibo() {
		return codRecibo;
	}

	public void setCodRecibo(int codRecibo) {
		this.codRecibo = codRecibo;
	}
	
	

}
