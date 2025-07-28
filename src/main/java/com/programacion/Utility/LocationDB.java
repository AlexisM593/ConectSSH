
package com.programacion.Utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class LocationDB {

    ArrayList<LocationDB> locationDBs = new ArrayList<>();

    public String ip;

    private String userLogin;

    private String clavelogin;

    private String idPais;

    private String namePais;

    private String urlImage;
    private boolean status;
    private String urlServer;

    public LocationDB(String ip, String clavelogin, String idPais, boolean status, String namePais, String urlImage,
            String userLogin, String urlServer) {
        this.ip = ip;
        this.clavelogin = clavelogin;
        this.idPais = idPais;
        this.status = status;
        this.namePais = namePais;
        this.urlImage = urlImage;
        this.userLogin = userLogin;
        this.urlServer = urlServer;
    }

    private static final String URL = "jdbc:sqlite:src\\main\\resources\\dataBase\\LocationDB.db";

    public static ArrayList<LocationDB> getAllLocations() {
        ArrayList<LocationDB> locationList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT l.ip, l.claveLogin, l.idPais, l.status, l.namePais, l.userLogin, l.urlServer, p.urlImage " +
                                "FROM Locations l LEFT JOIN ImagePais p ON l.idPais = p.idPais")) {

            while (rs.next()) {
                LocationDB loc = new LocationDB(
                        rs.getString("ip"),
                        rs.getString("clavelogin"),
                        rs.getString("idPais"),
                        rs.getBoolean("status"),
                        rs.getString("namePais"),
                        rs.getString("urlImage"),
                        rs.getString("userLogin"),
                        rs.getString("urlServer")
                        

                );
                locationList.add(loc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return locationList;
    }

    public static Connection connect() throws SQLException {

        Connection conn = DriverManager.getConnection(URL);
        conn.createStatement().execute("PRAGMA foreign_keys = ON"); // Habilitar claves foráneas
        return conn;
    }

    public static String getImageByIP(String ip) {
        String sql = "SELECT p.urlImage FROM Locations l JOIN ImagePais p ON l.idPais = p.idPais WHERE l.ip = ?";

        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ip);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("urlImage");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // no se encontró
    }

    public static String getSSHKeyFromDB(String ip) {
        String claveLogin = null;

        String query = "SELECT claveLogin FROM Locations WHERE ip = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ip);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                claveLogin = rs.getString("claveLogin");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return claveLogin;
    }

    public static String getUserLoginFromDB(String ip) {
        String userLogin = null;
        String query = "SELECT userLogin FROM Locations WHERE ip = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ip);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userLogin = rs.getString("userLogin");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener userLogin de la IP: " + ip);
            e.printStackTrace();
        }

        return userLogin;
    }

    public String getIp() {
        return ip;
    }

     public String getUrlServer() {
        return urlServer;
    }


    public String getIdPais() {
        return idPais;
    }

    public String getNamePais() {
        return namePais;
    }

}
