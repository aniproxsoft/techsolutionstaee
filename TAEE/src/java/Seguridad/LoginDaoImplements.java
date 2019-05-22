/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Seguridad;

import ConnectionDB.ConnectionDB;
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
public class LoginDaoImplements implements LoginDao {

    ConnectionDB conexion ;

    @Override
    public List<UsuarioVO> getUsuario(String usuario, String pass) {
        List<UsuarioVO> ListaUser = new ArrayList<>();

        Connection conn = null;
        try {
            conexion= new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_autentification(?,?)}");
            query.setString(1, usuario);
            query.setString(2, pass);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            UsuarioVO us=null;
            while (respuesta.next()) {
                us = new UsuarioVO();
                us.setCorreo(respuesta.getString(5));
                us.setNombre(respuesta.getString(3));
                us.setNombre_rol(respuesta.getString(6));
                ListaUser.add(us);
            }
            return ListaUser;

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
