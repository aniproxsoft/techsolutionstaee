/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AdminEmpresasEgresados;

import ConnectionDB.ConnectionDB;
import Seguridad.UsuarioVO;
import Services.ServiceEmpresas;
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
public class AdminEmpresasEgresadosDAOImplements implements AdminEmpresasEgresadosDAO {

    ConnectionDB conexion;

    @Override
    public List<UsuarioVO> getEmresasEgresados(Integer tipo) {
        List<UsuarioVO> empresas = new ArrayList<>();
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_empresas_egresados(?)}");
            query.setInt(1, tipo);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            UsuarioVO empresa = null;
            while (respuesta.next()) {
                empresa = new UsuarioVO();
                empresa.setId_empresa(respuesta.getInt("id_empresa"));
                empresa.setDireccion(respuesta.getString("direccion"));
                empresa.setNombre_empresa(respuesta.getString("nombre_empresa"));
                empresa.setId_estado(respuesta.getInt("id_estado"));
                empresa.setId_ciudad(respuesta.getInt("id_ciudad"));
                empresa.setCodigo_postal(respuesta.getString("codigo_postal"));
                empresa.setId_usuario(respuesta.getInt("id_usuario"));
                empresa.setTelefono(respuesta.getString("num_telefono"));
                empresa.setRfc(respuesta.getString("rfc"));
                empresa.setStatus_empresa(respuesta.getString("status_empresa"));
                empresa.setCorreo_empresa(respuesta.getString("correo_empresa"));
                empresa.setNombre(respuesta.getString("nombre_usuario"));
                empresa.setApellidos(respuesta.getString("apellidos"));
                empresa.setCorreo(respuesta.getString("email"));
                empresa.setRol(respuesta.getInt("id_rol"));
                empresa.setStatus(respuesta.getString("status_usuario"));
                empresa.setNombre_ciudad(respuesta.getString("nombre_ciudad"));
                empresa.setNombre_estado(respuesta.getString("nombre_estado"));
                if (empresa.getStatus().equals("1")) {
                    empresa.setActividad("Activo");
                } else {
                    empresa.setActividad("Inactivo");

                }
                empresa.setVacantes_publicadas(respuesta.getInt("vacantes_publicadas"));
                empresas.add(empresa);

            }
            return empresas;
        } catch (IOException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    @Override
    public boolean apruebaEmpresa(Integer empresa_id) {
        boolean flag = false;
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call aprobar_empresa(?)}");
            query.setInt(1, empresa_id);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            while (respuesta.next()) {
                if (respuesta.getBoolean("flag")) {
                    flag = true;
                } else {
                    flag = false;
                }
                return flag;
            }
        } catch (IOException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    @Override
    public boolean desapruebaEmpresa(Integer empresa_id) {
        boolean flag = false;
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call desaprobar_empresa(?)}");
            query.setInt(1, empresa_id);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            while (respuesta.next()) {
                if (respuesta.getBoolean("flag")) {
                    flag = true;
                } else {
                    flag = false;
                }
                return flag;
            }
        } catch (IOException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    @Override
    public List<UsuarioVO> searchEmresasEgresados(Integer empresa_id) {
        List<UsuarioVO> empresas = new ArrayList<>();
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_search_empresas_egresados(?)}");
            query.setInt(1, empresa_id);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            UsuarioVO empresa = null;
            while (respuesta.next()) {
                empresa = new UsuarioVO();
                empresa.setId_empresa(respuesta.getInt("id_empresa"));
                empresa.setDireccion(respuesta.getString("direccion"));
                empresa.setNombre_empresa(respuesta.getString("nombre_empresa"));
                empresa.setId_estado(respuesta.getInt("id_estado"));
                empresa.setId_ciudad(respuesta.getInt("id_ciudad"));
                empresa.setCodigo_postal(respuesta.getString("codigo_postal"));
                empresa.setId_usuario(respuesta.getInt("id_usuario"));
                empresa.setTelefono(respuesta.getString("num_telefono"));
                empresa.setRfc(respuesta.getString("rfc"));
                empresa.setStatus_empresa(respuesta.getString("status_empresa"));
                empresa.setCorreo_empresa(respuesta.getString("correo_empresa"));
                empresa.setNombre(respuesta.getString("nombre_usuario"));
                empresa.setApellidos(respuesta.getString("apellidos"));
                empresa.setCorreo(respuesta.getString("email"));
                empresa.setRol(respuesta.getInt("id_rol"));
                empresa.setStatus(respuesta.getString("status_usuario"));
                empresa.setNombre_ciudad(respuesta.getString("nombre_ciudad"));
                empresa.setNombre_estado(respuesta.getString("nombre_estado"));
                if (empresa.getStatus().equals("1")) {
                    empresa.setActividad("Activo");
                } else {
                    empresa.setActividad("Inactivo");

                }
                empresas.add(empresa);

            }
            return empresas;
        } catch (IOException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServiceEmpresas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
