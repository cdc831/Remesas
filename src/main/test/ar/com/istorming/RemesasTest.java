package ar.com.istorming;

import static org.junit.Assert.*;
import org.junit.Test;

public class RemesasTest {

	@Test
	public void validarRespuestaRemesas() {

		Remesas rm = new Remesas();
		rm.iniciarMR("https://trs005.tusrecibos.com.py/rest/mtess/remesas/service.php", "prosegurws",
				"asRExrQQ5FaQWXcEg2tf", "AR_PROXY.LATAM1.PROSEGUR.LOCAL", 8080);
		rm.procesarDatos(29, "Remesa Gaston22-2222", "2021-03-10");
		try {
			rm.enviaJsonRemesaPost();
			System.out.println(rm.getCodRespuesta());
			assertNotEquals(rm.getIdRemesa(), 0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
