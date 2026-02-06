package com.devcaotics.airBnTruta.controllers.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.devcaotics.airBnTruta.model.entities.Hospedagem;
import com.devcaotics.airBnTruta.model.repositories.Facade;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;






@RestController
@RequestMapping("/rest/hospedagem")
public class HospedagemRestController {

    @Autowired
    private Facade facade;

    @GetMapping
    public ResponseEntity<List<Hospedagem>> getHospedagens() {

        List<Hospedagem> hospedagens = null;

        try {
            hospedagens = facade.readAllHospedeiro();
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro!");
        }

        return ResponseEntity.ok(hospedagens);
    }

    @PostMapping
    public ResponseEntity<?> postNewHospedagem(@RequestBody Hospedagem hospedagem) {
        //TODO: process POST request

        try {
            facade.create(hospedagem);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao cadastrar!");
        
        }
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hospedagem> getHospedagem(@PathVariable int id) {

        Hospedagem h;
        try {
            h = facade.readHospedagem(id);
            if(h != null)
                return ResponseEntity.ok(h);
            else{
                return ResponseEntity.notFound().build();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        
    }
    
    
    

}
