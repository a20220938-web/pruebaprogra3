package pe.edu.pucp.cineflow.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public abstract class DefaultBaseDao<M> extends BaseDao<M, Integer> {

    @Override
    protected Integer extraerIdDespuesDeCrear(PreparedStatement cmd, Connection conn) throws SQLException {
        if (cmd instanceof CallableStatement callableCmd) {
            return callableCmd.getInt("p_id");
        }
        try (ResultSet rs = cmd.getGeneratedKeys()) {
            return rs.next() ? rs.getInt(1) : null;
        }
    }
}