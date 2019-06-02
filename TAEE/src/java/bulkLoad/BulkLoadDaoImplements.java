/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bulkLoad;

import ConnectionDB.ConnectionDB;
import Seguridad.LoginDaoImplements;
import Seguridad.RolVO;
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
    public List<RolVO> getRoles() {
        List<RolVO> roles = new ArrayList<>();
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_roles()}");
            query.execute();
            ResultSet respuesta = query.getResultSet();
            RolVO rol = null;
            while (respuesta.next()) {
                rol = new RolVO();
                rol.setId_rol(respuesta.getInt(1));
                rol.setNombre_rol(respuesta.getString(2));
                roles.add(rol);
            }
            return roles;

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
        System.out.println("****************"+jsonBien);
        System.out.println("************************"+jsonComplete);
        List<BulkLoadVO> cargas = new ArrayList<>();
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_bulkload(?,?)}");
            query.setString(1, jsonBien);
            query.setString(2,jsonComplete);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            BulkLoadVO bulk = null;
            while (respuesta.next()) {
                bulk = new BulkLoadVO();
                bulk.setLote(respuesta.getInt(1));
                bulk.setBulk_id(respuesta.getInt(2));
                bulk.setNombre(respuesta.getString(3));
                bulk.setApellidos(respuesta.getString(4));
                bulk.setCorreo(respuesta.getString(5));
                bulk.setContraseña(respuesta.getString(6));
                bulk.setNombre_rol(respuesta.getString(7));
                bulk.setObservaciones(respuesta.getString(8));
                bulk.setBulks(respuesta.getInt(9));
                bulk.setFails(respuesta.getInt(10));
                bulk.setUpdates(respuesta.getInt(11));
                bulk.setInserts(respuesta.getInt(12));
                System.out.println(respuesta.getString(13));
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

}
