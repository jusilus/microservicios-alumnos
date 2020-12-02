package com.formacionbdi.microservicios.app.alumnos.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.formacionbdi.microservicios.app.alumnos.services.AlumnoService;
import com.formacionbdi.microservicios.commons.alumnos.models.entity.Alumno;
import com.formacionbdi.microservicios.commons.controllers.CommonController;

@RestController //Marca esta clase como un controlador del tipo Rest para manejar un API restful. Por defecto JSON, aunque permite XML.
public class AlumnoController extends CommonController<Alumno, AlumnoService> {
	
	/* MÉTODOS GET */
	
	@GetMapping("/alumnos-por-curso")
	public ResponseEntity<?> buscarAlumnosPorCurso(@RequestParam List<Long> ids){
		List<Alumno> alumnos = null;
		if(ids != null) {
			alumnos = (List<Alumno>) this.commonService.findAllById(ids);
		}
		return ResponseEntity.ok(alumnos);
	}	
	
	@GetMapping("/filtrar/{term}")
	public ResponseEntity<?> buscarPorNombreOApellido(@PathVariable String term){		
		return ResponseEntity.ok(commonService.findByNombreOrApellido(term));		
	}
	
	@GetMapping("/uploads/img/{id}")
	public ResponseEntity<?> verFoto(@PathVariable Long id){
		Optional<Alumno> o = this.commonService.findById(id);
		if(!o.isPresent() || o.get().getFoto() == null) {
			return ResponseEntity.notFound().build();
		}
		Resource imagen = new ByteArrayResource(o.get().getFoto());
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imagen);		
	}
	
	/* MÉTODOS POST */
	
	@PostMapping("/crear-con-foto")
	public ResponseEntity<?> agregarConFoto(@Valid Alumno alumno, BindingResult result, @RequestParam MultipartFile archivo) throws IOException {
		if(!archivo.isEmpty()) {
			alumno.setFoto(archivo.getBytes());
		}
		return super.agregar(alumno, result);
	}
	
	/* MÉTODOS PUT */

	@PutMapping("/{id}") //Recibe información del cliente. Permite agregar dicha información para modificar un registro.
	//@RequestBody recoge los datos del JSON y los convierte al tipo Alumno
	//@PathVariable recoge la parte de la URL reconocida como {id} y la guarda en la variable del tipo Long.
	//Si la URL y la variable son diferentes se usa  (@PathVariable(name="nombreURL") Long nombreVariable)
	public ResponseEntity<?> modificar(@Valid @RequestBody Alumno alumno, BindingResult result, @PathVariable Long id){
		//commonService viene de la clase heredada
		if(result.hasErrors()) {
			return validar(result);
		}
		Optional<Alumno> o = commonService.findById(id);
		if(o.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Alumno alumnoDb = o.get();
		alumnoDb.setNombre(alumno.getNombre());
		alumnoDb.setApellido(alumno.getApellido());
		alumnoDb.setEmail(alumno.getEmail());		
		return ResponseEntity.status(HttpStatus.CREATED).body(commonService.save(alumnoDb));
	}

	@PutMapping("/modificar-con-foto/{id}")
	public ResponseEntity<?> modificarConFoto(@Valid Alumno alumno, BindingResult result, @PathVariable Long id, 
			@RequestParam MultipartFile archivo) throws IOException{	
		if(result.hasErrors()) {
			return validar(result);
		}		
		Optional<Alumno> o = commonService.findById(id);
		if(o.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Alumno alumnoDb = o.get();
		alumnoDb.setNombre(alumno.getNombre());
		alumnoDb.setApellido(alumno.getApellido());
		alumnoDb.setEmail(alumno.getEmail());
		if(!archivo.isEmpty()) {
			alumnoDb.setFoto(archivo.getBytes());	
		}		
		return ResponseEntity.status(HttpStatus.CREATED).body(commonService.save(alumnoDb));
	}
}