/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import empresa.EmpresaVO;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author ANIPROXTOART
 */
@ManagedBean(name = "empresaService", eager = true)
@ViewScoped
public class ServiceEmpresas {

    ConnectionDB conexion;

    public List<EmpresaVO> empresasEstadía(Integer tipo) {
        List<EmpresaVO> empresas = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_empresas(?)}");
            query.setInt(1, tipo);
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
