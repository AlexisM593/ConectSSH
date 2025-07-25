package com.programacion.Protocol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecureShell_DB extends Protocol {
    private  List<String> hwiDs;

    public SecureShell_DB(String usuarioItem, String ip, String vigencia, String fechaCreacion, String owner,  boolean estado, List<String> hwiDs) {
        super(usuarioItem, ip, vigencia, fechaCreacion, owner, estado);
        this.hwiDs = hwiDs;
    }


       public List<String> gethwiDs() {
        return hwiDs;
    }

        public void sethwiDs(List<String> hwiDs) {
        this.hwiDs = hwiDs;
    }

    
    public static List<SecureShell_DB> getData() {
    List<SecureShell_DB> lista = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:src\\main\\resources\\Protocol.db")) {
        String query = "SELECT Protocol.usuarioItem, ip, vigencia, fechaCreacion, owner, estado, hwiDs " +
                       "FROM Protocol INNER JOIN SecureShell ON Protocol.usuarioItem = SecureShell.usuarioItem";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String usuario = rs.getString("usuarioItem");
                String ip = rs.getString("ip");
                String vigencia = rs.getString("vigencia");
                String fecha = rs.getString("fechaCreacion");
                String owner = rs.getString("owner");
                boolean estado = rs.getInt("estado") == 1;
                String hwiDsTexto = rs.getString("hwiDs");
                List<String> hwiDs = Arrays.asList(hwiDsTexto.split(","));

                lista.add(new SecureShell_DB(usuario, ip, vigencia, fecha, owner, estado, hwiDs));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return lista;
}

}
