/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bulkLoad;

import ConnectionDB.ConnectionDB;
import Seguridad.LoginDaoImplements;
import Seguridad.RolVO;
import empresa.CiudadVO;
import empresa.EstadoVO;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANIPROXTOART
 */
public class BulkLoadDaoImplements implements BulkLoadDao {

    ConnectionDB conexion;

    @Override
    public List<CiudadVO> getCiudades() {
        List<CiudadVO> ciudades = new ArrayList<>();
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_ciudades()}");
            query.execute();
            ResultSet respuesta = query.getResultSet();
            CiudadVO ciudad = null;
            while (respuesta.next()) {
                ciudad = new CiudadVO();
                ciudad.setId_ciudad(respuesta.getInt(1));
                ciudad.setId_estado(respuesta.getInt(2));
                ciudad.setNombre_ciudad(respuesta.getString(3));
                ciudades.add(ciudad);
            }
            return ciudades;

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    @Override
    public List<BulkLoadVO> bulkLoad(String jsonBien, String jsonComplete) {
//        System.out.println("****************" + jsonBien);
//        System.out.println("************************" + jsonComplete);
        List<BulkLoadVO> cargas = new ArrayList<>();
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_bulkload(?,?)}");
            query.setString(1, jsonBien);
            query.setString(2, jsonComplete);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            BulkLoadVO bulk = null;
            while (respuesta.next()) {
                bulk = new BulkLoadVO();
                bulk.setLote(respuesta.getInt(1));
                bulk.setBulk_id(respuesta.getInt(2));
                bulk.setNombre_empresa(respuesta.getString(3));
                bulk.setDireccion(respuesta.getString(4));
                bulk.setNombre_estado(respuesta.getString(5));
                bulk.setNombre_ciudad(respuesta.getString(6));
                bulk.setCodigo_postal(respuesta.getString(7));
                bulk.setTelefono(respuesta.getString(8));
                bulk.setConvenio(respuesta.getString(9));
                bulk.setRfc(respuesta.getString(10));
                bulk.setObservaciones(respuesta.getString(11));
                bulk.setBulks(respuesta.getInt(12));
                bulk.setFails(respuesta.getInt(13));
                bulk.setUpdates(respuesta.getInt(14));
                bulk.setInserts(respuesta.getInt(15));
                System.out.println(respuesta.getString(16));
                bulk.setCorreo_empresa(respuesta.getString(17));
                cargas.add(bulk);
            }
            return cargas;

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<EstadoVO> getEstados() {
        List<EstadoVO> estados = new ArrayList<>();
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_estados()}");
            query.execute();
            ResultSet respuesta = query.getResultSet();
            EstadoVO estado = null;
            while (respuesta.next()) {
                estado = new EstadoVO();
                estado.setId_estado(respuesta.getInt(1));
                estado.setNombre_estado(respuesta.getString(2));
                estados.add(estado);
            }
            return estados;

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoginDaoImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
