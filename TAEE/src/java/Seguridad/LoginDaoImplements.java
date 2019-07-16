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

    ConnectionDB conexion;

    @Override
    public List<UsuarioVO> getUsuario(String usuario, String pass) {
        List<UsuarioVO> ListaUser = new ArrayList<>();

        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_autentification(?,?)}");
            query.setString(1, usuario);
            query.setString(2, pass);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            UsuarioVO us = null;
            while (respuesta.next()) {
                if (respuesta.getInt(1) != 0) {
                    us = new UsuarioVO();
                    us.setRol(respuesta.getInt(2));
                    us.setNombre(respuesta.getString(3));
                    us.setApellidos(respuesta.getString(4));
                    us.setCorreo(respuesta.getString(5));
                    us.setNombre_rol(respuesta.getString(6));
                    us.setNombre_empresa(respuesta.getString(7));
                    us.setDireccion(respuesta.getString(8));
                    us.setNombre_estado(respuesta.getString(9));
                    us.setNombre_ciudad(respuesta.getString(10));
                    us.setCodigo_postal(respuesta.getString(11));
                    us.setTelefono(respuesta.getString(12));
                    us.setConvenio(respuesta.getString(13));
                    us.setRfc(respuesta.getString(14));
                    us.setStatus(respuesta.getString(15));
                    us.setStatus_empresa(respuesta.getString(16));
                    us.setId_usuario(respuesta.getInt(17));
                    us.setId_empresa(respuesta.getInt(18));
                    ListaUser.add(us);
                }else{
                    return null;
                }
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
