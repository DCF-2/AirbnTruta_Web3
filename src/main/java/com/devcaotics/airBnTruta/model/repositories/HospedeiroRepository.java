package com.devcaotics.airBnTruta.model.repositories;

import com.devcaotics.airBnTruta.model.entities.Hospedeiro;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HospedeiroRepository implements Repository<Hospedeiro, Integer> {

    @Override
    public void create(Hospedeiro t) throws SQLException {
        String sql = "INSERT INTO hospedeiro (nome, vulgo, contato, senha) VALUES (?, ?, ?, ?)";
        
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, t.getNome());
        stmt.setString(2, t.getVulgo());
        stmt.setString(3, t.getContato());
        stmt.setString(4, t.getSenha());
        
        stmt.execute();
    }

    @Override
    public void update(Hospedeiro t) throws SQLException {
        String sql = "UPDATE hospedeiro SET nome=?, vulgo=?, contato=?, senha=? WHERE codigo=?";
        
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setString(1, t.getNome());
        stmt.setString(2, t.getVulgo());
        stmt.setString(3, t.getContato());
        stmt.setString(4, t.getSenha());
        stmt.setInt(5, t.getCodigo());
        
        stmt.execute();
    }

    @Override
    public Hospedeiro read(Integer k) throws SQLException {
        String sql = "SELECT * FROM hospedeiro WHERE codigo = ?";
        
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setInt(1, k);
        
        ResultSet rs = stmt.executeQuery();
        
        if(rs.next()){
            return loadFromResultSet(rs);
        }
        
        return null;
    }

    @Override
    public void delete(Integer k) throws SQLException {
        String sql = "DELETE FROM hospedagem WHERE hospedeiro_id = ?"; // Deleta hospedagens antes para não dar erro de FK
        PreparedStatement stmtfk = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmtfk.setInt(1, k);
        stmtfk.execute();

        sql = "DELETE FROM hospedeiro WHERE codigo = ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setInt(1, k);
        stmt.execute();
    }

    @Override
    public List<Hospedeiro> readAll() throws SQLException {
        String sql = "SELECT * FROM hospedeiro";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        
        List<Hospedeiro> lista = new ArrayList<>();
        while(rs.next()){
            lista.add(loadFromResultSet(rs));
        }
        return lista;
    }

    // --- CORREÇÃO: Método LOGIN que o Facade.java estava procurando ---
    public Hospedeiro login(String vulgo, String senha) throws SQLException {
        String sql = "SELECT * FROM hospedeiro WHERE vulgo = ? AND senha = ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setString(1, vulgo);
        stmt.setString(2, senha);
        
        ResultSet rs = stmt.executeQuery();
        
        if(rs.next()){
            return loadFromResultSet(rs);
        }
        return null;
    }

    // Método auxiliar para evitar repetição
    private Hospedeiro loadFromResultSet(ResultSet rs) throws SQLException {
        Hospedeiro h = new Hospedeiro();
        h.setCodigo(rs.getInt("codigo"));
        h.setNome(rs.getString("nome"));
        h.setVulgo(rs.getString("vulgo"));
        h.setContato(rs.getString("contato"));
        h.setSenha(rs.getString("senha"));
        return h;
    }
}