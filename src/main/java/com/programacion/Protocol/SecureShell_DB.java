package com.programacion.Protocol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SecureShell_DB extends Protocol {
    private  List<String> hwiDs;

    public SecureShell_DB(String usuarioItem, String ip, String vigencia, String fechaCreacion, String owner,  boolean estado, List<String> hwiDs) {
        super(usuarioItem, ip, vigencia, fechaCreacion, owner, estado);
        this.hwiDs = hwiDs;
    }

    public SecureShell_DB(String owner){
      super(owner);
    }

       public List<String> gethwiDs() {
        return hwiDs;
    }

        public void sethwiDs(List<String> hwiDs) {
        this.hwiDs = hwiDs;
    }

    
    public static List<SecureShell_DB> getData() {
    List<SecureShell_DB> lista = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:src\\main\\resources\\dataBase\\Protocol.db")) {
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


public static boolean existeUsuario(String usuarioItem) {
    String query = "SELECT 1 " +
                   "FROM Protocol " +
                   "INNER JOIN SecureShell ON Protocol.usuarioItem = SecureShell.usuarioItem " +
                   "WHERE Protocol.usuarioItem = ? LIMIT 1";

    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:src\\main\\resources\\dataBase\\Protocol.db");
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, usuarioItem);

        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next(); // Si hay al menos una fila, el usuario existe
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return false; // Si ocurre un error, consideramos que no existe
    }
}


public static int contarItemsPorOwner(String owner) {
    String query = "SELECT COUNT(*) AS total " +
                   "FROM Protocol " +
                   "INNER JOIN SecureShell ON Protocol.usuarioItem = SecureShell.usuarioItem " +
                   "WHERE Protocol.owner = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:src\\main\\resources\\dataBase\\Protocol.db");
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, owner);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total"); // Devuelve el número de registros
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0; // En caso de error o sin resultados
}


public static SecureShell_DB getItemByUsuarioItem(String usuarioItem) {
    String url = "jdbc:sqlite:src\\main\\resources\\dataBase\\Protocol.db";
    String query = "SELECT Protocol.usuarioItem, ip, vigencia, fechaCreacion, owner, estado, hwiDs " +
                   "FROM Protocol INNER JOIN SecureShell ON Protocol.usuarioItem = SecureShell.usuarioItem " +
                   "WHERE Protocol.usuarioItem = ?";

    try (Connection conn = DriverManager.getConnection(url);
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, usuarioItem);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String usuario = rs.getString("usuarioItem");
                String ip = rs.getString("ip");
                String vigencia = rs.getString("vigencia");
                String fecha = rs.getString("fechaCreacion");
                String owner = rs.getString("owner");
                boolean estado = rs.getInt("estado") == 1;
                String hwiDsTexto = rs.getString("hwiDs");
                List<String> hwiDs = Arrays.asList(hwiDsTexto.split(","));

                return new SecureShell_DB(usuario, ip, vigencia, fecha, owner, estado, hwiDs);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null; // Si no se encontró nada
}


public static List<Map<String, Object>> verificarHWIDsinDuplicadosPorIP(List<String> hwidArray, List<String> ips) {
    List<SecureShell_DB> allData = SecureShell_DB.getData();
    List<Map<String, Object>> resultsQuery = new ArrayList<>();

    if (hwidArray == null || hwidArray.isEmpty()) {
        throw new IllegalArgumentException("hwidArray debe ser un array no vacío.");
    }

    // Filtrar elementos con HWID coincidentes
    List<SecureShell_DB> foundItems = allData.stream()
        .filter(item -> item.gethwiDs() != null && item.gethwiDs().stream().anyMatch(hwidArray::contains))
        .collect(Collectors.toList());


    for (String ip : ips) {
        if (!"0.0.0.0".equals(ip)) {
            List<SecureShell_DB> matchingUsers = allData.stream()
                .filter(item -> foundItems.contains(ip.equals(item.getIp())))
                .collect(Collectors.toList());

            if (!matchingUsers.isEmpty()) {
                Set<String> hwids = matchingUsers.stream()
                    .flatMap(item -> item.gethwiDs().stream())
                    .collect(Collectors.toSet());

                List<String> hwidsDef = hwids.stream()
                    .filter(hwidArray::contains)
                    .collect(Collectors.toList());

                if (!hwidsDef.isEmpty()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("result", true);
                    result.put("ip", ip);
                    result.put("users", hwidsDef);
                    result.put("toString", "IP/Ubicación: " + ip + "\nUsuarios/HWID:\n" +
                            String.join("\n", hwidsDef) + "\n_________________");

                    resultsQuery.add(result);
                }
            }
        }
    }

    return resultsQuery;
}



public static boolean insertarItem(SecureShell_DB item) {
    String urlDB = "jdbc:sqlite:src\\main\\resources\\dataBase\\Protocol.db";
    
    String insertProtocol = "INSERT INTO Protocol (usuarioItem, ip, vigencia, fechaCreacion, owner, estado) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
    String insertSecureShell = "INSERT INTO SecureShell (usuarioItem, hwiDs) VALUES (?, ?)";

    try (Connection conn = DriverManager.getConnection(urlDB)) {
        conn.setAutoCommit(false); // Para asegurar que ambas inserciones se hagan juntas

        try (
            PreparedStatement stmtProtocol = conn.prepareStatement(insertProtocol);
            PreparedStatement stmtSecure = conn.prepareStatement(insertSecureShell)
        ) {
            // Insertar en Protocol
            stmtProtocol.setString(1, item.getUsuarioItem());
            stmtProtocol.setString(2, item.getIp());
            stmtProtocol.setString(3, item.getVigencia());
            stmtProtocol.setString(4, item.getFechaCreacion());
            stmtProtocol.setString(5, item.getOwner());
            stmtProtocol.setInt(6, item.isEstado() ? 1 : 0);
            stmtProtocol.executeUpdate();

            // Insertar en SecureShell
            stmtSecure.setString(1, item.getUsuarioItem());
            stmtSecure.setString(2, String.join(",", item.gethwiDs())); // List → texto
            stmtSecure.executeUpdate();

            conn.commit(); // Confirmar ambas inserciones
            return true;

        } catch (SQLException e) {
            conn.rollback(); // Revertir si alguna inserción falla
            e.printStackTrace();
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}


public static boolean eliminarItemPorUsuario(String usuarioItem) {
    String urlDB = "jdbc:sqlite:src\\main\\resources\\dataBase\\Protocol.db";

    String deleteSecureShell = "DELETE FROM SecureShell WHERE usuarioItem = ?";
    String deleteProtocol = "DELETE FROM Protocol WHERE usuarioItem = ?";

    try (Connection conn = DriverManager.getConnection(urlDB)) {
        conn.setAutoCommit(false); // Para ejecutar ambas eliminaciones juntas

        try (
            PreparedStatement stmtSecure = conn.prepareStatement(deleteSecureShell);
            PreparedStatement stmtProtocol = conn.prepareStatement(deleteProtocol)
        ) {
            // Eliminar primero de SecureShell
            stmtSecure.setString(1, usuarioItem);
            stmtSecure.executeUpdate();

            // Luego eliminar de Protocol
            stmtProtocol.setString(1, usuarioItem);
            stmtProtocol.executeUpdate();

            conn.commit(); // Confirmar la transacción
            return true;

        } catch (SQLException e) {
            conn.rollback(); // Revertir si algo falla
            e.printStackTrace();
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}


public static boolean actualizarItem(SecureShell_DB item) {
    String urlDB = "jdbc:sqlite:src\\main\\resources\\dataBase\\Protocol.db";

    String updateProtocol = "UPDATE Protocol SET ip = ?, vigencia = ?, estado = ? WHERE usuarioItem = ?";
    String updateSecureShell = "UPDATE SecureShell SET hwiDs = ? WHERE usuarioItem = ?";

    try (Connection conn = DriverManager.getConnection(urlDB)) {
        conn.setAutoCommit(false);

        try (
            PreparedStatement stmtProtocol = conn.prepareStatement(updateProtocol);
            PreparedStatement stmtSecure = conn.prepareStatement(updateSecureShell)
        ) {
            // hwiDs como string separado por comas
            String hwiDsTexto = String.join(",", item.gethwiDs());

            // UPDATE Protocol (ip, vigencia, estado)
            stmtProtocol.setString(1, item.getIp());
            stmtProtocol.setString(2, item.getVigencia());
            stmtProtocol.setInt(3, item.isEstado() ? 1 : 0);
            stmtProtocol.setString(4, item.getUsuarioItem());
            stmtProtocol.executeUpdate();

            // UPDATE SecureShell (solo hwiDs)
            stmtSecure.setString(1, hwiDsTexto);
            stmtSecure.setString(2, item.getUsuarioItem());
            stmtSecure.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}




}
