
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

    String userLogin;

    String clavelogin;

    String idPais;

    String namePais;

    String urlImage;
    boolean status;

    public LocationDB(String ip, String clavelogin, String idPais, boolean status, String namePais, String urlImage) {
        this.ip = ip;
        this.clavelogin = clavelogin;
        this.idPais = idPais;
        this.status = status;
        this.namePais = namePais;
        this.urlImage = urlImage;
    }

    private static final String URL = "jdbc:sqlite:src\\main\\resources\\LocationDB.db";
    public static ArrayList<LocationDB> getAllLocations() {
        ArrayList<LocationDB> locationList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
    "SELECT l.ip, l.claveLogin, l.idPais, l.status, l.namePais, p.urlImage " +
    "FROM Locations l LEFT JOIN ImagePais p ON l.idPais = p.idPais"
)) {

            while (rs.next()) {
                LocationDB loc = new LocationDB(
                        rs.getString("ip"),
                        rs.getString("clavelogin"),
                        rs.getString("idPais"),
                        rs.getBoolean("status"),
                        rs.getString("namePais"),
                        rs.getString("urlImage")

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

}
