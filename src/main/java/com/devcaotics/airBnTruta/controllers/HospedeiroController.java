package com.devcaotics.airBnTruta.controllers;

import com.devcaotics.airBnTruta.model.entities.Hospedagem;
import com.devcaotics.airBnTruta.model.entities.Hospedeiro;
import com.devcaotics.airBnTruta.model.entities.Interesse;
import com.devcaotics.airBnTruta.model.repositories.HospedagemRepository;
import com.devcaotics.airBnTruta.model.repositories.HospedeiroRepository; 
import com.devcaotics.airBnTruta.model.repositories.InteresseRepository;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // Importante
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.format.annotation.DateTimeFormat;

@Controller
@RequestMapping("/hospedeiro")
public class HospedeiroController {

    private HospedagemRepository repoHospedagem = new HospedagemRepository();
    private InteresseRepository repoInteresse = new InteresseRepository();
    private HospedeiroRepository repoHospedeiro = new HospedeiroRepository();

    // 1. Tela Inicial (Dashboard)
    @GetMapping({"", "/", "/index"})
    public String index(Model model, HttpSession session) {
        
        Hospedeiro hospedeiroLogado = (Hospedeiro) session.getAttribute("hospedeiroLogado");
        
        if (hospedeiroLogado == null) {
            return "redirect:/hospedeiro/login";
        }

        try {
            List<Hospedagem> lista = repoHospedagem.filterByHospedeiro(hospedeiroLogado.getCodigo());
            
            // Lógica do "Sino de Notificação"
            for (Hospedagem h : lista) {
                List<Interesse> interesses = repoInteresse.filterByHospedagem(h.getCodigo());
                if (!interesses.isEmpty()) {
                    h.setTemInteresse(true);
                }
            }
            
            model.addAttribute("hospedagens", lista);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "hospedeiro/index";
    }
    
    // 2. Tela de Login (GET)
    @GetMapping("/login")
    public String login(Model model) {
        // Envia um objeto vazio para o modal de cadastro não quebrar
        model.addAttribute("hospedeiro", new Hospedeiro());
        return "hospedeiro/login";
    }

    // 3. Processar Login (POST) - CORRIGIDO
    @PostMapping("/login")
    public String logar(String vulgo, String senha, HttpSession session, Model model) {
        try {
            // Como não temos um método 'login' no repositório, buscamos todos e checamos
            // (Para um projeto acadêmico, isso serve. Em produção, faríamos SQL específico)
            List<Hospedeiro> todos = repoHospedeiro.readAll();
            
            for (Hospedeiro h : todos) {
                if (h.getVulgo().equals(vulgo) && h.getSenha().equals(senha)) {
                    session.setAttribute("hospedeiroLogado", h);
                    return "redirect:/hospedeiro/index";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Se chegou aqui, errou a senha
        model.addAttribute("msg", "Vulgo ou senha incorretos!");
        model.addAttribute("hospedeiro", new Hospedeiro()); // Precisa reenviar o objeto pro modal
        return "hospedeiro/login";
    }

    // 4. Cadastrar Novo Hospedeiro (POST) - ADICIONADO
    @PostMapping("/save")
    public String save(Hospedeiro h) {
        try {
            repoHospedeiro.create(h);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/hospedeiro/login";
    }

    // 5. Ver Detalhes
    @GetMapping("/viewhospedagem/{id}")
    public String viewHospedagem(@PathVariable("id") int id, Model model) {
        try {
            Hospedagem h = repoHospedagem.read(id);
            List<Interesse> interesses = repoInteresse.filterByHospedagem(id);
            
            model.addAttribute("hospedagem", h);
            model.addAttribute("interesses", interesses);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "hospedeiro/detalhes";
    }
    
    // 6. Aceitar Proposta
    @GetMapping("/aceitar/{idInteresse}")
    public String aceitarProposta(@PathVariable("idInteresse") int idInteresse) {
        try {
            Interesse i = repoInteresse.read(idInteresse);
            
            Hospedagem h = i.getInteresse(); 
            // Garante que a hospedagem foi carregada
            if(h.getDescricaoCurta() == null) h = repoHospedagem.read(i.getInteresse().getCodigo());

            h.setFugitivo(i.getInteressado());
            
            repoHospedagem.update(h);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "redirect:/hospedeiro/index";
    }
    
    // 7. Tela de Nova Hospedagem
    @GetMapping("/new")
    public String newHospedagem(Model model) {
        model.addAttribute("hospedagem", new Hospedagem());
        return "hospedeiro/newhospedagem";
    }
    
    // 8. Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/hospedeiro/login";
    }

    // 10. EXCLUIR HOSPEDAGEM
    @GetMapping("/delete/{id}")
    public String deleteHospedagem(@PathVariable("id") int id, HttpSession session) {
        // Verifica se tá logado pra ninguém excluir via URL
        if (session.getAttribute("hospedeiroLogado") == null) return "redirect:/hospedeiro/login";

        try {
            repoHospedagem.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/hospedeiro/index";
    }

    @PostMapping("/createHospedagem")
    public String createHospedagem(Hospedagem h, HttpSession session) {
        // Pega o chefe logado
        Hospedeiro logado = (Hospedeiro) session.getAttribute("hospedeiroLogado");
        
        if (logado == null) return "redirect:/hospedeiro/login";

        try {
            // VINCULA A HOSPEDAGEM AO HOSPEDEIRO LOGADO
            h.setHospedeiro(logado);
            
            // Define datas padrão se vierem vazias (opcional)
            if(h.getInicio() == null) h.setInicio(new java.util.Date());

            repoHospedagem.create(h);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "redirect:/hospedeiro/index";
    }
}