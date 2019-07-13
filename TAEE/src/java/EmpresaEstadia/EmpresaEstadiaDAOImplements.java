/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EmpresaEstadia;

import ConnectionDB.ConnectionDB;
import com.mysql.jdbc.CallableStatement;
import empresa.CiudadVO;
import empresa.EmpresaVO;
import empresa.EstadoVO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.omg.Dynamic.Parameter;
import org.primefaces.context.RequestContext;

/**
 *
 * @author ferna
 */
public class EmpresaEstadiaDAOImplements implements EmpresaEstadiaDAO {

    ConnectionDB conexion;

    @Override
    public List<EmpresaVO> getEmpresas(Integer clave) {
        List<EmpresaVO> empresas = new ArrayList<>();
        Connection con = null;
        try {
            conexion = new ConnectionDB();
            con = conexion.conectarBD();
            java.sql.CallableStatement query = con.prepareCall("{call sp_get_empresas(?)}");
            query.setInt(1, clave);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            EmpresaVO empresa = null;
            while (respuesta.next()) {
                empresa = new EmpresaVO();
                empresa.setId_empresa(respuesta.getInt("id_empresa"));
                empresa.setDireccion(respuesta.getString("direccion"));
                empresa.setNombre_empresa(respuesta.getString("nombre"));
                empresa.setId_estado(respuesta.getInt("id_estado"));
                empresa.setId_ciudad(respuesta.getInt("id_ciudad"));
                empresa.setCodigo_postal(respuesta.getString("codigo_postal"));
                empresa.setId_usuario(respuesta.getInt("id_usuario"));
                empresa.setTelefono(respuesta.getString("num_telefono"));
                empresa.setConvenio(respuesta.getString("folio_convenio"));
                empresa.setRfc(respuesta.getString("rfc"));
                empresa.setStatus(respuesta.getString("status"));
                empresa.setCorreo_empresa(respuesta.getString("correo_empresa"));
                empresas.add(empresa);
            }
            return empresas;
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
        return null;

    }

    @Override
    public List<EstadoVO> getEstados() {
        List<EstadoVO> estados = new ArrayList<>();
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = (CallableStatement) conn.prepareCall("{call sp_get_estados()}");
            query.execute();
            ResultSet respuesta = query.getResultSet();
            EstadoVO estado = null;
            while (respuesta.next()) {
                estado = new EstadoVO();
                estado.setId_estado(respuesta.getInt("id_estado"));
                estado.setNombre_estado(respuesta.getString("nombre_estado"));
                estados.add(estado);
            }
            return estados;
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
        return null;
    }

    @Override
    public List<CiudadVO> getMunicipios(Integer clave) {
        List<CiudadVO> municipios = new ArrayList<>();
        Connection conn = null;
        try {
            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = (CallableStatement) conn.prepareCall("{call sp_get_municipios(?)}");
            query.setInt(1, clave);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            CiudadVO municipio = null;
            while (respuesta.next()) {
                municipio = new CiudadVO();
                municipio.setId_estado(respuesta.getInt("id_estado"));
                municipio.setId_ciudad(respuesta.getInt("id_ciudad"));
                municipio.setNombre_ciudad(respuesta.getString("nombre_ciudad"));
                municipios.add(municipio);
            }
            return municipios;
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
        return null;
    }

    @Override
    public boolean deleteEmpresa(Integer clave) {
        Connection con = null;
        try {
            con = conexion.conectarBD();
            CallableStatement query = (CallableStatement) con.prepareCall("{call sp_delete_empresa_estadia(?)}");
            query.setInt(1, clave);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            if (respuesta.next()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", respuesta.getString(1)));
                RequestContext.getCurrentInstance().update("mensajes");
                RequestContext.getCurrentInstance().update("tabla1");
                return true;
            }
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

    @Override
    public boolean insertUpdate(EmpresaVO empresa, Integer editar) {
        Connection con = null;
        try {
            con = conexion.conectarBD();
            CallableStatement query = (CallableStatement) con.prepareCall("{call sp_insert_update_empresaEst(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            query.setInt(1, editar);
            query.setInt(2, empresa.getId_empresa());
            query.setString(3, empresa.getDireccion());
            query.setString(4, empresa.getNombre_empresa());
            query.setInt(5, empresa.getId_estado());
            query.setInt(6, empresa.getId_ciudad());
            query.setString(7, empresa.getCodigo_postal());
            query.setInt(8, empresa.getId_usuario());
            query.setString(9, empresa.getTelefono());
            query.setString(10, empresa.getConvenio());
            query.setString(11, empresa.getRfc());
            query.setString(12, empresa.getStatus());
            query.setString(13, empresa.getCorreo_empresa());
            query.execute();

            ResultSet respuesta = query.getResultSet();
            if (respuesta.next() && respuesta.getInt(2) == 1) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", respuesta.getString(1)));
                RequestContext.getCurrentInstance().update("mensajes");
                RequestContext.getCurrentInstance().update("tabla1");
                return true;
            } else 
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", respuesta.getString(1)));
                RequestContext.getCurrentInstance().update("mensajes");
                RequestContext.getCurrentInstance().update("tabla1");
        }catch (ClassNotFoundException ex) {
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
