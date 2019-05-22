/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ConnectionDB;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ANIPROXTOART
 */
public class ConnectionDB {

    private Connection cnx;
    private String ipHost;
    private String puerto;
    private String baseDatos;
    private String usuario;
    private String contrasena;
    private String cadenaConexion;
    private String gestorBD;
    private String driver;

    public ConnectionDB() throws IOException {
        Ruta src = new Ruta();
        ReadProperties rdMysql = new ReadProperties(src.getFileDbMysql());

        this.ipHost = rdMysql.getIpHost();
        this.puerto = rdMysql.getPuerto();
        this.baseDatos = rdMysql.getBasedatos();
        this.usuario = rdMysql.getUsuario();
        this.contrasena = rdMysql.getContrasena();
        this.gestorBD = rdMysql.getGestor();
        if (gestorBD.equalsIgnoreCase("MYSQL")) {
            this.cadenaConexion = "jdbc:mysql://" + ipHost + ":" + puerto + "/" + baseDatos;
            this.driver = "com.mysql.jdbc.Driver";

        } else {
            System.err.println("No existe implementacion para ese gestor");
        }
    }

    public Connection conectarBD() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        try {
            Connection cnx;
            Class.forName(this.driver);
            cnx = DriverManager.getConnection(cadenaConexion, usuario, contrasena);
            cnx.setAutoCommit(false);
            return cnx;
        } catch (ClassNotFoundException ex) {
            System.err.println("causa: " + ex.getCause());
            System.err.println("Mensaje: " + ex.getMessage());
            System.err.println("Localizacion: " + ex.getLocalizedMessage());
            System.err.println("Traza: " + ex.getStackTrace());
            throw ex;
        } catch (SQLException ex) {
            System.err.println("causa: " + ex.getCause());
            System.err.println("Mensaje: " + ex.getMessage());
            System.err.println("Localizacion: " + ex.getLocalizedMessage());
            System.err.println("Traza: " + ex.getStackTrace());
            throw ex;
        }
    }

    public boolean desconectarBD() throws SQLException {
        try {
            cnx.close();
            return true;
        } catch (SQLException ex) {
            System.err.println("causa: " + ex.getCause());
            System.err.println("Mensaje: " + ex.getMessage());
            System.err.println("Localizacion: " + ex.getLocalizedMessage());
            System.err.println("Traza: " + ex.getStackTrace());
            throw ex;
        }
    }

    public boolean confirmar() throws SQLException {
        try {
            cnx.commit();
            return true;
        } catch (SQLException ex) {
            System.err.println("causa: " + ex.getCause());
            System.err.println("Mensaje: " + ex.getMessage());
            System.err.println("Localizacion: " + ex.getLocalizedMessage());
            System.err.println("Traza: " + ex.getStackTrace());
            throw ex;
        }
    }

    public boolean deshacer() throws SQLException {
        try {
            cnx.rollback();
            return true;
        } catch (SQLException ex) {
            System.err.println("causa: " + ex.getCause());
            System.err.println("Mensaje: " + ex.getMessage());
            System.err.println("Localizacion: " + ex.getLocalizedMessage());
            System.err.println("Traza: " + ex.getStackTrace());
            throw ex;
        }
    }

    public Connection getCnx() {
        return cnx;
    }

    public void setCnx(Connection cnx) {
        this.cnx = cnx;
    }

    public String getIpHost() {
        return ipHost;
    }

    public void setIpHost(String ipHost) {
        this.ipHost = ipHost;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public String getBaseDatos() {
        return baseDatos;
    }

    public void setBaseDatos(String baseDatos) {
        this.baseDatos = baseDatos;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getCadenaConexion() {
        return cadenaConexion;
    }

    public void setCadenaConexion(String cadenaConexion) {
        this.cadenaConexion = cadenaConexion;
    }

    public String getGestorBD() {
        return gestorBD;
    }

    public void setGestorBD(String gestorBD) {
        this.gestorBD = gestorBD;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
