package com.devcaotics.airBnTruta.model.repositories;

import com.devcaotics.airBnTruta.model.entities.Interesse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class InteresseRepository implements Repository<Interesse,Integer>{

    public InteresseRepository(){}

    @Override
    public void create(Interesse i) throws SQLException {
        // Verifica se já existe antes de criar
        if(exists(i.getInteressado().getCodigo(), i.getInteresse().getCodigo())){
            return; // Já demonstrou interesse, não faz nada
        }

        String sql = "INSERT INTO interesse (realizado, proposta, tempo_permanencia, fugitivo_id, hospedagem_id) "
                   + "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setLong(1, i.getRealizado());
        stmt.setString(2, i.getProposta());
        stmt.setInt(3, i.getTempoPermanencia());
        stmt.setInt(4, i.getInteressado().getCodigo());
        stmt.setInt(5, i.getInteresse().getCodigo());

        stmt.execute();
    }

    @Override
    public void update(Interesse c) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'update' - Interesse não costuma ser alterado");
    }

    @Override
    public Interesse read(Integer k) throws SQLException {
        String sql = "SELECT * FROM interesse WHERE codigo = ?";
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
        String sql = "DELETE FROM interesse WHERE codigo = ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setInt(1, k);
        stmt.execute();
    }

    @Override
    public List<Interesse> readAll() throws SQLException {
        String sql = "SELECT * FROM interesse";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        return loadList(stmt);
    }

    // LISTAR POR HOSPEDAGEM (Para o Hospedeiro ver)
    public List<Interesse> filterByHospedagem(int idHospedagem) throws SQLException {
        String sql = "SELECT * FROM interesse WHERE hospedagem_id = ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setInt(1, idHospedagem);
        return loadList(stmt);
    }

    // LISTAR POR FUGITIVO (Para "Meus Interesses")
    public List<Interesse> filterByFugitivo(int idFugitivo) throws SQLException {
        String sql = "SELECT * FROM interesse WHERE fugitivo_id = ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setInt(1, idFugitivo);
        return loadList(stmt);
    }
    
    // VERIFICAR DUPLICIDADE
    public boolean exists(int idFugitivo, int idHospedagem) throws SQLException {
        String sql = "SELECT count(*) as total FROM interesse WHERE fugitivo_id = ? AND hospedagem_id = ?";
        PreparedStatement stmt = ConnectionManager.getCurrentConnection().prepareStatement(sql);
        stmt.setInt(1, idFugitivo);
        stmt.setInt(2, idHospedagem);
        ResultSet rs = stmt.executeQuery();
        if(rs.next()){
            return rs.getInt("total") > 0;
        }
        return false;
    }

    private List<Interesse> loadList(PreparedStatement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery();
        List<Interesse> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(loadFromResultSet(rs));
        }
        return lista;
    }

    private Interesse loadFromResultSet(ResultSet rs) throws SQLException {
        Interesse i = new Interesse();
        i.setCodigo(rs.getInt("codigo"));
        i.setRealizado(rs.getLong("realizado"));
        i.setProposta(rs.getString("proposta"));
        i.setTempoPermanencia(rs.getInt("tempo_permanencia"));
        
        // Carrega os objetos relacionados
        i.setInteressado(new FugitivoRepository().read(rs.getInt("fugitivo_id")));
        // Aqui podemos ter um loop se Hospedagem carregar Interesse e Interesse carregar Hospedagem.
        // Por segurança, carregamos a hospedagem normalmente, mas ter cuidado no toString ou JSON.
        i.setInteresse(new HospedagemRepository().read(rs.getInt("hospedagem_id")));
        
        return i;
    }

}