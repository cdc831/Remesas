package ar.com.istorming;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;

import ar.com.istorming.Remesas;

public class RemesasTest {

	@Test
	public void validarRespuestaRemesas() {

		Remesas rm = new Remesas();
		rm.iniciarMR("https://trs005.tusrecibos.com.py/rest/mtess/remesas/service.php", "prosegurws",
				"asRExrQQ5FaQWXcEg2tf", "AR_PROXY.LATAM1.PROSEGUR.LOCAL", 8080);
		rm.procesarDatos(29, "Remesa Gaston2", "2021-03-10");
		try {
			rm.enviaJsonRemesaPost();	
			assertNotEquals(rm.getIdRemesa(), 0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
}
