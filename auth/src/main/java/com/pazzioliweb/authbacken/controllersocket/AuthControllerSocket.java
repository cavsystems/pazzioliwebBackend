package com.pazzioliweb.authbacken.controllersocket;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import com.pazzioliweb.authbacken.dtos.UsuarioRequestDTOauth;
import com.pazzioliweb.empresaauth.service.EmpresaService;
import com.pazzioliweb.empresaback.dtos.EmpresaResponseauth;
import com.pazzioliweb.empresasback.entity.Empresas;
import com.pazzioliweb.empresasback.repositori.EmpresaRepository;
@Controller
public class AuthControllerSocket {
	
	    @Autowired
	    private EmpresaService empresaService;

	@MessageMapping("/empresa")
	@SendTo("/topic/auth")
	public List<EmpresaResponseauth>  enpresa(@Payload UsuarioRequestDTOauth request) {
		List<EmpresaResponseauth> resultados = empresaService.buscarNombreconexion(request.getIdentificacion());

		
		   
				return empresaService.buscarNombreconexion(request.getIdentificacion());
		
	  	}}
