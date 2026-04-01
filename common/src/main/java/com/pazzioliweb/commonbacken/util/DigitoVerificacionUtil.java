package com.pazzioliweb.commonbacken.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping("/api/digitoverificacion")
public final class DigitoVerificacionUtil{
	private static final int[] FACTORES = {71, 67, 59, 53, 47, 43, 41, 37, 29, 23, 19, 17, 13, 7, 3};
	private DigitoVerificacionUtil() {
		
	}
	 
	@RequestMapping("/CalcularDv")
	public  ResponseEntity<Map<String, String>> calcularDigitoVerificacion(@RequestParam (defaultValue ="") String  numeroIdentificacion ){
		  Map<String,String> response = new HashMap<>();
		if (numeroIdentificacion == null || numeroIdentificacion.isEmpty() || !numeroIdentificacion.matches("\\d+")) {
			response.put("digito", "");
            return ResponseEntity.ok().body(response);
        }else {
        	  // Aseguramos que tenga 15 caracteres rellenando con ceros a la izqunierda
            String numeroPadded = String.format("%15s", numeroIdentificacion).replace(' ', '0');

            int suma = 0;
            for (int i = 0; i < FACTORES.length; i++) {
                int digito = Character.getNumericValue(numeroPadded.charAt(i));
                suma += digito * FACTORES[i];
            }

            int residuo = suma % 11;
            int dv = (residuo == 0) ? 0 : (residuo == 1 ? 1 : 11 - residuo);

            response.put("digito", String.valueOf(dv));
            return ResponseEntity.ok().body(response);
        }

      
    }
}