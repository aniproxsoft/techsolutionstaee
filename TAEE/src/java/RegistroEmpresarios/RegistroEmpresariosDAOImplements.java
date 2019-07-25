/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RegistroEmpresarios;

import ConnectionDB.ConnectionDB;
import EmpresaEstadia.EmpresaEstadiaDAOImplements;
import Seguridad.UsuarioVO;
import com.mysql.jdbc.CallableStatement;
import empresa.EstadoVO;
import java.io.IOException;
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
public class RegistroEmpresariosDAOImplements implements RegistroEmpresariosDAO {

    ConnectionDB conexion;

    @Override
    public boolean insertUsuario(UsuarioVO user) {
        boolean flag = false;
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = (CallableStatement) conn.prepareCall("{call sp_registrar_user(?,?,?,?,?,?,?,?,?,?,?,?)}");
            query.setString(1, user.getNombre());
            query.setString(2, user.getApellidos());
            query.setString(3, user.getCorreo());
            query.setString(4, user.getContraseña());
            query.setString(5, user.getDireccion());
            query.setString(6, user.getNombre_empresa());
            query.setInt(7, user.getId_estado());
            query.setInt(8, user.getId_ciudad());
            query.setInt(9, Integer.parseInt(user.getCodigo_postal()));
            query.setString(10, user.getTelefono());
            query.setString(11, user.getRfc());
            query.setString(12, user.getCorreo_empresa());
            query.execute();
            ResultSet respuesta = query.getResultSet();

            while (respuesta.next()) {
                if(respuesta.getBoolean("flag")==true){
                    flag=true;
                }else{
                    flag=false;
                }
            }
            return flag;
        } catch (IOException ex) {
            Logger.getLogger(EmpresaEstadiaDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EmpresaEstadiaDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(EmpresaEstadiaDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(EmpresaEstadiaDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EmpresaEstadiaDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
