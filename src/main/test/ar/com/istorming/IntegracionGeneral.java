package ar.com.istorming;

import static org.junit.Assert.*;

import org.junit.Test;

public class IntegracionGeneral {

	
	
	/*
	 * Prueba de integracion general , Se genera una nueva remesa y se envia un recibo con esa solicitud de remesa
	 */
	@Test
	public void test() {
		
		
		String fecha = "2021-03-10";
		Remesas rm = new Remesas();
		rm.iniciarMR("https://trs005.tusrecibos.com.py/rest/mtess/remesas/service.php", "prosegurws",
				"asRExrQQ5FaQWXcEg2tf", "proxy.emea.prosegur.local", 8080);
		Recibos rc = new Recibos();
		rc.iniciarMR("https://trs005.tusrecibos.com.py/rest/mtess/recibohaber/service.php", "prosegurws",
				"asRExrQQ5FaQWXcEg2tf", "proxy.emea.prosegur.local", 8080);
		rm.procesarDatos(28, "Remesa Prueba Gaston 10032021-13", fecha);
		try {
			rm.enviaJsonRemesaPost();
			rc.procesarDatos("RUC", "80094119-5", String.valueOf(rm.getIdRemesa()), "CI", "4791126", "LIQ", fecha, "/home/gaston/prueba.pdf");
			rc.enviaJsonRecibosPost();
			
			System.out.println("Remesa: " + rm.getIdRemesa());
			System.out.println("Cod Recibo: " + rc.getCodRecibo());
			
			assertNotEquals(rc.getCodRecibo(), 0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

}
