package com.devcaotics.airBnTruta.controllers.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devcaotics.airBnTruta.model.entities.Servico;
import com.devcaotics.airBnTruta.model.repositories.Facade;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/rest/servico")
@CrossOrigin("*")
public class ServicoRestController {
    @Autowired
    private Facade facade;

    @PostMapping
    public String newServico(@RequestBody Servico s) {
        
        try {
            facade.create(s);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return "Cadastrado com sucesso!";
    }

    @GetMapping
    public List<Servico> getServicos() {

        try {
            List<Servico> servicos = facade.readAllServico();
            return servicos;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @PutMapping
    public void putMethodName(@RequestBody Servico s) {
        
        try {
            facade.update(s);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    @GetMapping("/{id}")
    public Servico getMethodName(@PathVariable int id) {

        Servico s;
        try {
            s = facade.readServico(id);
            return s;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id){

        try {
            facade.deleteServico(id);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    
    

}
