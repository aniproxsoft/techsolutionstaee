/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EmpresaEstadia;

import Services.ServiceEmpresas;
import empresa.CiudadVO;
import empresa.EmpresaVO;
import empresa.EstadoVO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
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
    private ServiceEmpresas service;
    private List<EmpresaVO> empresasList;
    private EmpresaVO selectEmpresa;
    private List<EmpresaVO> empresas;

    @PostConstruct
    public void init() {
        empresa = new EmpresaVO();
        selectEmpresa = new EmpresaVO();
        dao = new EmpresaEstadiaDAOImplements();
        mostrarEmpresas();
        estados = dao.getEstados();
        service = new ServiceEmpresas();
        empresasList = service.empresasEstadía(3);

    }

    public void insetarEmpresa() {
        if (empresa.getId_empresa() != null) {
            empresa.setId_estado(cve_estado);
            empresa.setId_ciudad(cve_municipio);
            dao.insertUpdate(empresa, 1);
            RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
            cancelar();
        } else if (empresa.getId_empresa() == null) {
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

    public List<EmpresaVO> muestraEmpresas(String query) {
        List<EmpresaVO> allEmpresas = service.empresasEstadía(3);

        List<EmpresaVO> filteredEmpresas = new ArrayList<EmpresaVO>();

        for (int i = 0; i < allEmpresas.size(); i++) {
            EmpresaVO skin = allEmpresas.get(i);
            if (skin.getNombre_empresa().toLowerCase().contains(query.toLowerCase())) {
                filteredEmpresas.add(skin);
                System.out.println("s:" + skin.getId_empresa());
            }

        }

        return filteredEmpresas;
    }

    public EmpresaVO retrieveEmpresaByName(String name) {
        Iterator<EmpresaVO> it = this.empresasList.iterator();
        while (it.hasNext()) {
            EmpresaVO emp = it.next();
            if (name.equals(emp.getNombre_empresa() + "")) {
                return emp;
            }
        }
        return null;

    }

    public void buscar() {
//        System.out.println("c: " + selectedEmpresa.getId_empresa());
        if (selectEmpresa != null) {
            if (selectEmpresa.getId_empresa() > 0) {
                System.out.println("id: " + selectEmpresa.getId_empresa());
                empresaList = dao.searchEmresasEgresados(3, selectEmpresa.getId_empresa());
                RequestContext.getCurrentInstance().update("formulario:tabla1");

            }
        } else {
            System.out.println("perros");
            init();
            RequestContext.getCurrentInstance().update("formulario:tabla1");
            selectEmpresa = null;
        }

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
        empresa = new EmpresaVO();
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

    public ServiceEmpresas getService() {
        return service;
    }

    public void setService(ServiceEmpresas service) {
        this.service = service;
    }

    public List<EmpresaVO> getEmpresasList() {
        return empresasList;
    }

    public void setEmpresasList(List<EmpresaVO> empresasList) {
        this.empresasList = empresasList;
    }

    public EmpresaVO getSelectEmpresa() {
        return selectEmpresa;
    }

    public void setSelectEmpresa(EmpresaVO selectEmpresa) {
        this.selectEmpresa = selectEmpresa;
    }

    public List<EmpresaVO> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<EmpresaVO> empresas) {
        this.empresas = empresas;
    }

}
