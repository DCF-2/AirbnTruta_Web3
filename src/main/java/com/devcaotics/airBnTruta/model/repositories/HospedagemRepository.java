package com.devcaotics.airBnTruta.model.repositories;

import com.devcaotics.airBnTruta.model.entities.Hospedagem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HospedagemRepository implements Repository<Hospedagem, Integer> {

    @Override
    public void create(Hospedagem h) throws SQLException {
        String sql = "INSERT INTO hospedagem (descricao_curta, descricao_longa, localizacao, diaria, inicio, fim, hospedeiro_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        stmt.setString(1, h.getDescricaoCurta());
        stmt.setString(2, h.getDescricaoLonga());
        stmt.setString(3, h.getLocalizacao());
        stmt.setDouble(4, h.getDiaria());
        
        if (h.getInicio() != null) {
            stmt.setDate(5, new java.sql.Date(h.getInicio().getTime()));
        } else {
            stmt.setDate(5, new java.sql.Date(System.currentTimeMillis()));
        }
        
        if (h.getFim() != null) {
            stmt.setDate(6, new java.sql.Date(h.getFim().getTime()));
        } else {
            stmt.setNull(6, java.sql.Types.DATE);
        }

        stmt.setInt(7, h.getHospedeiro().getCodigo());
        stmt.execute();
    }

    @Override
    public void update(Hospedagem h) throws SQLException {
        String sql = "UPDATE hospedagem SET fugitivo_id = ? WHERE codigo = ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        
        if(h.getFugitivo() != null){
            stmt.setInt(1, h.getFugitivo().getCodigo());
        } else {
            stmt.setNull(1, java.sql.Types.INTEGER);
        }
        stmt.setInt(2, h.getCodigo());
        stmt.executeUpdate();
    }

    @Override
    public Hospedagem read(Integer k) throws SQLException {
        String sql = "SELECT * FROM hospedagem WHERE codigo = ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setInt(1, k);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) return loadFromResultSet(rs);
        return null;
    }

    @Override
    public void delete(Integer k) throws SQLException {
        // 1. Apagar interesses vinculados a essa casa
        String sqlInteresse = "DELETE FROM interesse WHERE hospedagem_id = ?";
        PreparedStatement stmtI = ConnectionManager.getCurrentConnection().prepareStatement(sqlInteresse);
        stmtI.setInt(1, k);
        stmtI.execute();

        // 2. Apagar vínculos de serviços (se houver)
        String sqlServico = "DELETE FROM hospedagem_servico WHERE hospedagem_id = ?";
        PreparedStatement stmtS = ConnectionManager.getCurrentConnection().prepareStatement(sqlServico);
        stmtS.setInt(1, k);
        stmtS.execute();

        // 3. Agora sim, apagar a hospedagem
        String sql = "DELETE FROM hospedagem WHERE codigo = ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setInt(1, k);
        stmt.execute();
    }

    @Override
    public List<Hospedagem> readAll() throws SQLException {
        return filterBy("SELECT * FROM hospedagem");
    }

    public List<Hospedagem> filterByAvailable() throws SQLException{
        return filterBy("SELECT * FROM hospedagem where fugitivo_id IS NULL OR fugitivo_id = 0");
    }

    public List<Hospedagem> filterByHospedeiro(int codigoHospedeiro) throws SQLException{
        String sql = "select * from hospedagem where hospedeiro_id = ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setInt(1, codigoHospedeiro);
        return loadList(stmt);
    }
    
    public List<Hospedagem> filterByDisponivelEPrecoELocal(double precoMax, String local) throws SQLException {
        String sql = "SELECT * FROM hospedagem WHERE (fugitivo_id IS NULL OR fugitivo_id = 0) " +
                     "AND diaria <= ? AND localizacao LIKE ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setDouble(1, precoMax);
        stmt.setString(2, "%" + local + "%");
        return loadList(stmt);
    }

    public void removeFugitivo(int idHospedagem) throws SQLException {
        String sql = "UPDATE hospedagem SET fugitivo_id = NULL WHERE codigo = ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setInt(1, idHospedagem);
        stmt.execute();
    }

    private List<Hospedagem> filterBy(String sql) throws SQLException {
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        return loadList(stmt);
    }

    private List<Hospedagem> loadList(PreparedStatement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery();
        List<Hospedagem> list = new ArrayList<>();
        while (rs.next()) list.add(loadFromResultSet(rs));
        return list;
    }

    // --- CORREÇÃO AQUI NO loadFromResultSet ---
    private Hospedagem loadFromResultSet(ResultSet rs) throws SQLException {
        Hospedagem h = new Hospedagem();
        h.setCodigo(rs.getInt("codigo"));
        h.setDescricaoCurta(rs.getString("descricao_curta"));
        h.setDescricaoLonga(rs.getString("descricao_longa"));
        h.setLocalizacao(rs.getString("localizacao"));
        h.setDiaria(rs.getDouble("diaria"));
        h.setInicio(rs.getDate("inicio"));
        h.setFim(rs.getDate("fim"));
        
        // 1. Carregar Fugitivo
        int idFugitivo = rs.getInt("fugitivo_id");
        if(idFugitivo > 0) h.setFugitivo(new FugitivoRepository().read(idFugitivo));
        
        // 2. Carregar Hospedeiro (A Correção!)
        int idHospedeiro = rs.getInt("hospedeiro_id");
        if(idHospedeiro > 0) h.setHospedeiro(new HospedeiroRepository().read(idHospedeiro));
        
        return h;
    }
}