/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EmpresaEstadia;

import empresa.CiudadVO;
import empresa.EmpresaVO;
import empresa.EstadoVO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

/**
 *
 * @author ferna
 */
@ManagedBean(name = "beanEmpresaEstadia")
@ViewScoped
public class EmpresaEstadiaBean implements Serializable {

    private boolean form = false;
    private EmpresaVO empresa;
    private EmpresaEstadiaDAO dao;
    private List<EmpresaVO> empresaList;
    private List<EstadoVO> estados;
    private List<CiudadVO> ciudades;
    private int cve_estado;
    private int cve_municipio;

    @PostConstruct
    public void init() {
        empresa = new EmpresaVO();
        dao = new EmpresaEstadiaDAOImplements();
        mostrarEmpresas();
        estados = dao.getEstados();

    }

    public void insetarEmpresa() {
        if (empresa.getId_empresa() != null) {
            empresa.setId_estado(cve_estado);
            empresa.setId_ciudad(cve_municipio);
            dao.insertUpdate(empresa, 1);
            RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
            cancelar();
        }else if(empresa.getId_empresa()== null){
            empresa.setId_estado(cve_estado);
            empresa.setId_ciudad(cve_municipio);
            empresa.setStatus("3");
            empresa.setId_empresa(0);
            empresa.setId_usuario(0);
            dao.insertUpdate(empresa, 0);
            RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
            cancelar();
            mostrarEmpresas();
        }
            
//        

    }

    public void mostrarMunicipio() {
        ciudades = new ArrayList<>();
        ciudades = dao.getMunicipios(cve_estado);
    }

    public void mostrarEmpresas() {
        empresaList = new ArrayList<>();
        empresaList = dao.getEmpresas(3);
    }

    public void datosEmpres(EmpresaVO seleccionada) {
        empresa = seleccionada;
        setForm(true);
        cve_estado = seleccionada.getId_estado();
        mostrarMunicipio();
        cve_municipio = seleccionada.getId_ciudad();
        System.out.println("id.." + empresa.getId_empresa());
        RequestContext.getCurrentInstance().update("formulario");
    }

    public void mostrarFormulario() {
        setForm(true);
        empresa = new  EmpresaVO();
        cve_estado = 0;
        cve_municipio = 0;
        RequestContext.getCurrentInstance().update("formulario"); 
    }

    public void cancelar() {
        setForm(false);
        empresa = null;
        cve_estado = 0;
        cve_municipio = 0;
        RequestContext.getCurrentInstance().update("formulario");
    }

    public void EliminarEmpresaEstadia(EmpresaVO seleccionada) {
        empresa = seleccionada;
        dao.deleteEmpresa(empresa.getId_empresa());
        empresaList.remove(seleccionada);
        RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
        RequestContext.getCurrentInstance().update("formulario");

    }

    public boolean isForm() {
        return form;
    }

    public void setForm(boolean form) {
        this.form = form;
    }

    public List<EmpresaVO> getEmpresaList() {
        return empresaList;
    }

    public void setEmpresaList(List<EmpresaVO> empresaList) {
        this.empresaList = empresaList;
    }

    public EmpresaVO getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaVO empresa) {
        this.empresa = empresa;
    }

    public EmpresaEstadiaDAO getDao() {
        return dao;
    }

    public void setDao(EmpresaEstadiaDAO dao) {
        this.dao = dao;
    }

    public List<EstadoVO> getEstados() {
        return estados;
    }

    public void setEstados(List<EstadoVO> estados) {
        this.estados = estados;
    }

    public List<CiudadVO> getCiudades() {
        return ciudades;
    }

    public void setCiudades(List<CiudadVO> ciudades) {
        this.ciudades = ciudades;
    }

    public int getCve_estado() {
        return cve_estado;
    }

    public void setCve_estado(int cve_estado) {
        this.cve_estado = cve_estado;
    }

    public int getCve_municipio() {
        return cve_municipio;
    }

    public void setCve_municipio(int cve_municipio) {
        this.cve_municipio = cve_municipio;
    }

}
