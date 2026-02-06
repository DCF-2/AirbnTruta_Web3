package com.devcaotics.airBnTruta.controllers;

import com.devcaotics.airBnTruta.model.entities.Fugitivo;
import com.devcaotics.airBnTruta.model.entities.Hospedagem; // Faltava este
import com.devcaotics.airBnTruta.model.entities.Interesse;
import com.devcaotics.airBnTruta.model.repositories.FugitivoRepository;
import com.devcaotics.airBnTruta.model.repositories.HospedagemRepository;
import com.devcaotics.airBnTruta.model.repositories.InteresseRepository;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Faltava este
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/fugitivo")
public class FugitivoController {

    private FugitivoRepository repoFugitivo = new FugitivoRepository();
    private HospedagemRepository repoHospedagem = new HospedagemRepository();
    private InteresseRepository repoInteresse = new InteresseRepository();

    // 1. TELA DE LOGIN
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("fugitivo", new Fugitivo());
        return "fugitivo/login";
    }

    // 2. PROCESSAR LOGIN
    @PostMapping("/login")
    public String logar(String vulgo, String senha, HttpSession session, Model model) {
        try {
            Fugitivo logado = repoFugitivo.login(vulgo, senha);
            if (logado != null) {
                session.setAttribute("fugitivoLogado", logado);
                return "redirect:/fugitivo/index";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        model.addAttribute("msg", "Vulgo ou senha incorretos!");
        model.addAttribute("fugitivo", new Fugitivo());
        return "fugitivo/login";
    }

    // 3. TELA DE CADASTRO
    @GetMapping("/new")
    public String register(Model model) {
        model.addAttribute("fugitivo", new Fugitivo());
        return "fugitivo/register";
    }

    // 4. PROCESSAR CADASTRO
    @PostMapping("/create")
    public String create(Fugitivo f) {
        try {
            repoFugitivo.create(f);
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/fugitivo/new?erro=true";
        }
        return "redirect:/fugitivo/login";
    }

    // 5. DASHBOARD (Busca de Casas)
    @GetMapping({"/index", "/", ""})
    public String index(Model model, HttpSession session,
                        @RequestParam(required = false) Double precoMax,
                        @RequestParam(required = false) String local) {
        
        Fugitivo logado = (Fugitivo) session.getAttribute("fugitivoLogado");
        if (logado == null) return "redirect:/fugitivo/login";

        try {
            List<Hospedagem> lista;
            
            // Lógica do Filtro
            if (precoMax != null || (local != null && !local.isEmpty())) {
                double p = (precoMax != null) ? precoMax : 999999.0;
                String l = (local != null) ? local : "";
                lista = repoHospedagem.filterByDisponivelEPrecoELocal(p, l);
            } else {
                lista = repoHospedagem.filterByAvailable();
            }

            model.addAttribute("hospedagens", lista);
            model.addAttribute("fugitivo", logado);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "fugitivo/index";
    }

    // 6. REGISTRAR INTERESSE
    @GetMapping("/interesse/{idHospedagem}")
    public String registrarInteresse(@PathVariable("idHospedagem") int idHospedagem, HttpSession session) {
        Fugitivo logado = (Fugitivo) session.getAttribute("fugitivoLogado");
        if (logado == null) return "redirect:/fugitivo/login";

        try {
            Hospedagem h = new Hospedagem();
            h.setCodigo(idHospedagem);

            Interesse i = new Interesse();
            i.setInteresse(h);
            i.setInteressado(logado);
            i.setProposta("Solicitação de aluguel enviada pelo sistema.");
            i.setTempoPermanencia(7);
            i.setRealizado(System.currentTimeMillis());

            repoInteresse.create(i);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/fugitivo/meus-interesses";
    }

    // 7. MEUS INTERESSES
    @GetMapping("/meus-interesses")
    public String meusInteresses(Model model, HttpSession session) {
        Fugitivo logado = (Fugitivo) session.getAttribute("fugitivoLogado");
        if (logado == null) return "redirect:/fugitivo/login";

        try {
            List<Interesse> lista = repoInteresse.filterByFugitivo(logado.getCodigo());
            model.addAttribute("interesses", lista);
            model.addAttribute("fugitivo", logado);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "fugitivo/interesses";
    }
    
    // 8. CHECKOUT (SAIR DA CASA) - O Novo Método
    @GetMapping("/checkout/{idHospedagem}")
    public String checkout(@PathVariable("idHospedagem") int idHospedagem, HttpSession session) {
        Fugitivo logado = (Fugitivo) session.getAttribute("fugitivoLogado");
        if (logado == null) return "redirect:/fugitivo/login";

        try {
            Hospedagem h = repoHospedagem.read(idHospedagem);
            
            // Só deixa sair se a casa tiver alguém E esse alguém for o usuário logado
            if (h.getFugitivo() != null && h.getFugitivo().getCodigo() == logado.getCodigo()) {
                repoHospedagem.removeFugitivo(idHospedagem);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/fugitivo/meus-interesses";
    }
    
    // 9. LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/fugitivo/login";
    }

    // 10. VER DETALHES DA CASA (Visão do Fugitivo)
    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable("id") int id, Model model, HttpSession session) {
        Fugitivo logado = (Fugitivo) session.getAttribute("fugitivoLogado");
        if (logado == null) return "redirect:/fugitivo/login";

        try {
            Hospedagem h = repoHospedagem.read(id);
            model.addAttribute("hospedagem", h);
            model.addAttribute("fugitivo", logado);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "fugitivo/detalhes"; // Vamos criar essa tela
    }
}