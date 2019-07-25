/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchEmpleosEgresados;

import ConnectionDB.ConnectionDB;
import SearchEstadias.BuscaEstadiasDAOImplements;
import SearchEstadias.VacanteVO;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vacantes.ConocimientoVO;
import vacantes.HabilidadVO;

/**
 *
 * @author ANIPROXTOART
 */
public class BuscaEmpleosEgresadosDAOImplemets implements BuscaEmpleosEgresadosDAO{
    ConnectionDB conexion;

    @Override
    public List<VacanteVO> searchVacantes(Integer perfil, String jsonHabilidades, String jsonConociminetos) {
        List<VacanteVO> vacantes = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_vacantes_egresados(?,?,?)}");
            query.setInt(1, perfil);
            query.setString(2, jsonConociminetos);
            query.setString(3, jsonHabilidades);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            VacanteVO vacante = null;
            while (respuesta.next()) {
                vacante = new VacanteVO();
                vacante.setId_vacante(respuesta.getInt(1));
                vacante.setTitulo(respuesta.getString(2));
                vacante.setVacante_desc(respuesta.getString(3));
                if (vacante.getVacante_desc().length() > 40) {
                    Integer num = 0;
                    num = vacante.getVacante_desc().length() / 2;
                    String s = vacante.getVacante_desc().substring(0, vacante.getVacante_desc().length() - num);
                    vacante.setVacante_descCorta(s);
                } else {
                    vacante.setVacante_descCorta(vacante.getVacante_desc());
                }
                vacante.setNombre_nivel(respuesta.getString(4));
                vacante.setCarrera_desc(respuesta.getString(5));
                vacante.setId_perfil(respuesta.getInt(6));
                vacante.setNombre_perfil(respuesta.getString(7));
                vacante.setEdad_min(respuesta.getInt(8));
                vacante.setEdad_max(respuesta.getInt(9));
                if(vacante.getEdad_min()>0){
                    vacante.setRenderEdad(true);
                }
                if(vacante.getEdad_max()>0){
                    vacante.setRenderEdad2(true);
                }
                vacante.setSalario_min(respuesta.getDouble(10));
                vacante.setSalario_max(respuesta.getDouble(11));
                if(vacante.getSalario_max()>0.0 || vacante.getSalario_min()>0.0){
                    vacante.setRenderSalario(true);
                }
                vacante.setHora_inicial(respuesta.getString(12));
                vacante.setHora_final(respuesta.getString(13));
                if((vacante.getHora_inicial()!=null||vacante.getHora_final()!=null)){
                    vacante.setRenderHora(true);
                }
                vacante.setExperiencia(respuesta.getString(14));
                if(vacante.getExperiencia()!=null){
                    vacante.setRenderExperiencia(true);
                }
                vacante.setId_empresa(respuesta.getInt(15));
                vacante.setNombre(respuesta.getString(16));
                vacante.setStatus(respuesta.getString(17));
                vacante.setDireccion(respuesta.getString(18));
                vacante.setConocimientos(getConocimientoDetail(vacante.getId_vacante()));
                vacante.setHabilidades(getHabilidadesDetail(vacante.getId_vacante()));
                if(vacante.getHabilidades().size()>0){
                    vacante.setRenderHabilidades(true);
                }
                vacante.setNum_telefono(respuesta.getString(19));
                vacante.setCorreo_empresa(respuesta.getString(20));
                vacantes.add(vacante);

            }
            return vacantes;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }
     @Override
    public List<ConocimientoVO> getConocimientoDetail(Integer claveVacante) {
        List<ConocimientoVO> conocimientos = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_conco_habil(?,?)}");
            query.setInt(1, claveVacante);
            query.setInt(2, 1);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            ConocimientoVO conocimiento = null;
            while (respuesta.next()) {
                conocimiento = new ConocimientoVO();
                conocimiento.setId_conocimiento(respuesta.getInt(2));
                conocimiento.setConoc_desc(respuesta.getString(3));

                conocimientos.add(conocimiento);

            }
            return conocimientos;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<HabilidadVO> getHabilidadesDetail(Integer claveVacante) {
         List<HabilidadVO> habilidades = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_conco_habil(?,?)}");
            query.setInt(1, claveVacante);
            query.setInt(2, 2);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            HabilidadVO habilidad = null;
            while (respuesta.next()) {
                habilidad = new HabilidadVO();
                habilidad.setId_habilidad(respuesta.getInt(2));
                habilidad.setHabilidad_desc(respuesta.getString(3));
                habilidades.add(habilidad);

            }
            return habilidades;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    @Override
    public List<VacanteVO> searchVacantesPorEmpresa(Integer empresa) {
        List<VacanteVO> vacantes = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_vacantes_egresados_por_empresa(?)}");
            query.setInt(1, empresa);
            
            query.execute();
            ResultSet respuesta = query.getResultSet();
            VacanteVO vacante = null;
            while (respuesta.next()) {
                vacante = new VacanteVO();
                vacante.setId_vacante(respuesta.getInt(1));
                vacante.setTitulo(respuesta.getString(2));
                vacante.setVacante_desc(respuesta.getString(3));
                if (vacante.getVacante_desc().length() > 40) {
                    Integer num = 0;
                    num = vacante.getVacante_desc().length() / 2;
                    String s = vacante.getVacante_desc().substring(0, vacante.getVacante_desc().length() - num);
                    vacante.setVacante_descCorta(s);
                } else {
                    vacante.setVacante_descCorta(vacante.getVacante_desc());
                }
                vacante.setNombre_nivel(respuesta.getString(4));
                vacante.setCarrera_desc(respuesta.getString(5));
                vacante.setId_perfil(respuesta.getInt(6));
                vacante.setNombre_perfil(respuesta.getString(7));
                vacante.setEdad_min(respuesta.getInt(8));
                vacante.setEdad_max(respuesta.getInt(9));
                if(vacante.getEdad_min()>0||vacante.getEdad_max()>0){
                    vacante.setRenderEdad(true);
                }
                vacante.setSalario_min(respuesta.getDouble(10));
                vacante.setSalario_max(respuesta.getDouble(11));
                if(vacante.getSalario_max()>0.0 || vacante.getSalario_min()>0.0){
                    vacante.setRenderSalario(true);
                }
                vacante.setHora_inicial(respuesta.getString(12));
                vacante.setHora_final(respuesta.getString(13));
                if(vacante.getHora_inicial()!=null||vacante.getHora_final()!=null){
                    vacante.setRenderHora(true);
                }
                vacante.setExperiencia(respuesta.getString(14));
                if(vacante.getExperiencia()!=null || vacante.getExperiencia()!=""){
                    vacante.setRenderExperiencia(true);
                }
                vacante.setId_empresa(respuesta.getInt(15));
                vacante.setNombre(respuesta.getString(16));
                vacante.setStatus(respuesta.getString(17));
                vacante.setDireccion(respuesta.getString(18));
                vacante.setConocimientos(getConocimientoDetail(vacante.getId_vacante()));
                vacante.setHabilidades(getHabilidadesDetail(vacante.getId_vacante()));
                if(vacante.getHabilidades().size()>0){
                    vacante.setRenderHabilidades(true);
                }
                vacante.setNum_telefono(respuesta.getString(19));
                vacante.setCorreo_empresa(respuesta.getString(20));
                vacante.setAyuda_economica(respuesta.getBoolean(21));
                if(vacante.isAyuda_economica()==true){
                    vacante.setAyuda("Si");
                }else{
                    vacante.setAyuda("No");
                }
                vacantes.add(vacante);

            }
            return vacantes;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }


}
