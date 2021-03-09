package net.sytes.canterosoft;

import static org.junit.Assert.*;

import org.junit.Test;

public class RemesasTest {

	
	
	@Test
	public void validarRespuestaRemesas() {
		String nombreRemesa = "0";
		Remesas rem = new Remesas();
		rem.setEmpresa(29);
		rem.setNombre(nombreRemesa);
		rem.setFecha("2021-03-08");
		rem.setAuthUser("prosegurws");
		rem.setAuthPass("asRExrQQ5FaQWXcEg2tf");
		rem.setProxy("AR_PROXY.LATAM1.PROSEGUR.LOCAL");
		rem.setPuerto(8080);
		rem.setUrl("https://trs005.tusrecibos.com.py/rest/mtess/remesas/service.php");
		
		boolean esCorrecto  = rem.enviaJsonRemesaPost();
		String codigoRepuesta = rem.getCodRespuesta(); 
		assertEquals(codigoRepuesta, "0");
	}
	@Test
	public void validarCodRemesas() {
		String nombreRemesa = "1";
		Remesas rem = new Remesas();
		rem.setEmpresa(29);
		rem.setNombre(nombreRemesa);
		rem.setFecha("2021-03-08");
		rem.setAuthUser("prosegurws");
		rem.setAuthPass("asRExrQQ5FaQWXcEg2tf");
		rem.setProxy("AR_PROXY.LATAM1.PROSEGUR.LOCAL");
		rem.setPuerto(8080);
		rem.setUrl("https://trs005.tusrecibos.com.py/rest/mtess/remesas/service.php");
		
		boolean esCorrecto  = rem.enviaJsonRemesaPost();
		int codigoRepuesta = rem.getIdRemesaRespuesta(); 
		assertNotEquals(codigoRepuesta, 0);
	}
}
