package com.formacionbdi.microservicios.app.alumnos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.formacionbdi.microservicios.app.alumnos.models.repository.AlumnoRepository;
import com.formacionbdi.microservicios.commons.alumnos.models.entity.Alumno;
import com.formacionbdi.microservicios.commons.services.CommonServiceImpl;

@Service
public class AlumnoServiceImpl extends CommonServiceImpl<Alumno, AlumnoRepository> implements AlumnoService {

	@Override
	public List<Alumno> findByNombreOrApellido(String term) {		
		return repository.findByNombreOrApellido(term);
	}
}
