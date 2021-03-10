package ar.com.istorming;

import static org.junit.Assert.*;

import org.junit.Test;

public class RecibosTest {

	@Test
	public void validarRepuestaRecibos() {
		Recibos rc = new Recibos();
		rc.iniciarMR("https://trs005.tusrecibos.com.py/rest/mtess/recibohaber/service.php", "prosegurws",
				"asRExrQQ5FaQWXcEg2tf", "AR_PROXY.LATAM1.PROSEGUR.LOCAL", 8080);
		rc.procesarDatos("RUC", "80094119-5", "106", "CI", "4791126", "LIQ", "2020-10-10", "/home/gaston/prueba.pdf");
		try {
			rc.enviaJsonRecibosPost();
			assertNotEquals(rc.getCodRecibo(), 0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
