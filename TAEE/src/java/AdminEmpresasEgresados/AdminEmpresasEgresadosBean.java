/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AdminEmpresasEgresados;

import Seguridad.UsuarioVO;
import SendMail.SendMailer;
import Services.ServiceEmpresas;
import empresa.EmpresaVO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author ANIPROXTOART
 */
@ManagedBean(name = "beanAdminEmpresasEgresados")
@ViewScoped
public class AdminEmpresasEgresadosBean implements Serializable {

    private List<UsuarioVO> empresas;
    private List<UsuarioVO> empresasAprobadas;
    AdminEmpresasEgresadosDAO dao;
    SendMailer enviar;
    private UsuarioVO empresa;
    private boolean renderForm;
    private EmpresaVO selectedEmpresa;
    private ServiceEmpresas service;
    private List<EmpresaVO> empresasList;
    private List<EmpresaVO> empresasListAprobadas;

    public AdminEmpresasEgresadosBean() {
    }

    @PostConstruct
    public void init() {
        dao = new AdminEmpresasEgresadosDAOImplements();
        empresas = dao.getEmresasEgresados(1);
        empresasAprobadas = dao.getEmresasEgresados(2);
        enviar = new SendMailer();
        empresa = new UsuarioVO();
        service = new ServiceEmpresas();
        selectedEmpresa = new EmpresaVO();
        empresasListAprobadas = service.empresasEstadía(2);
        empresasList = service.empresasEstadía(1);

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
        if (selectedEmpresa != null) {
            if (selectedEmpresa.getId_empresa() > 0) {
                System.out.println("id: " + selectedEmpresa.getId_empresa());
                empresas = dao.searchEmresasEgresados(selectedEmpresa.getId_empresa());
                RequestContext.getCurrentInstance().update("formulario:data_table");
            }
        }
    }
    public void buscarAprob() {

//        System.out.println("c: " + selectedEmpresa.getId_empresa());
        if (selectedEmpresa != null) {
            if (selectedEmpresa.getId_empresa() > 0) {
                System.out.println("id: " + selectedEmpresa.getId_empresa());
                empresasAprobadas = dao.searchEmresasEgresados(selectedEmpresa.getId_empresa());
                RequestContext.getCurrentInstance().update("formulario:data_table");
            }
        }
    }

    public EmpresaVO retrieveEmpresaAprobadaByName(String name) {
        Iterator<EmpresaVO> it = this.empresasListAprobadas.iterator();
        while (it.hasNext()) {
            EmpresaVO emp = it.next();
            if (name.trim().equals(emp.getNombre_empresa().trim() + "")) {
                return emp;
            }
        }
        return null;

    }

    public void aprobarEmpresa(UsuarioVO selectedEmpresa) {
        //        System.out.println("c: "+selectedEmpresa.getCorreo());
        if (dao.apruebaEmpresa(selectedEmpresa.getId_empresa())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "La empresa: " + selectedEmpresa.getNombre_empresa() + " Ha sido aprobada"));
            RequestContext.getCurrentInstance().update("mensajes");
            RequestContext.getCurrentInstance().execute("ocultaMsj(4000)");
            empresas.remove(selectedEmpresa);
            RequestContext.getCurrentInstance().update("formulario:data_table");
            enviar.sendMail(selectedEmpresa.getCorreo(), selectedEmpresa.getNombre_empresa(), selectedEmpresa.getNombre());

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ocurrio un error al tratar de aprobarla"));
            RequestContext.getCurrentInstance().update("mensajes");
            RequestContext.getCurrentInstance().execute("ocultaMsj(4000)");
        }

    }

    public List<EmpresaVO> muestraEmpresas(String query) {
        List<EmpresaVO> allEmpresas = service.empresasEstadía(1);

        List<EmpresaVO> filteredEmpresas = new ArrayList<EmpresaVO>();

        for (int i = 0; i < allEmpresas.size(); i++) {
            EmpresaVO skin = allEmpresas.get(i);
            if (skin.getNombre_empresa().toLowerCase().contains(query.toLowerCase())) {
                filteredEmpresas.add(skin);
//                System.out.println("s:" + skin.getId_empresa());
            }

        }

        return filteredEmpresas;
    }

    public List<EmpresaVO> muestraEmpresasAprobadas(String query) {
        List<EmpresaVO> allEmpresas = service.empresasEstadía(2);

        List<EmpresaVO> filteredEmpresas = new ArrayList<EmpresaVO>();

        for (int i = 0; i < allEmpresas.size(); i++) {
            EmpresaVO skin = allEmpresas.get(i);
            if (skin.getNombre_empresa().toLowerCase().contains(query.toLowerCase())) {
                filteredEmpresas.add(skin);
//                System.out.println("s:" + skin.getId_empresa());
            }

        }

        return filteredEmpresas;
    }

    public void desaprobarEmpresa(UsuarioVO selectedEmpresa) {
        //        System.out.println("c: "+selectedEmpresa.getCorreo());
        if (dao.desapruebaEmpresa(selectedEmpresa.getId_empresa())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "La empresa: " + selectedEmpresa.getNombre_empresa() + " Ha sido desaprobada"));
            RequestContext.getCurrentInstance().update("mensajes");
            RequestContext.getCurrentInstance().execute("ocultaMsj(4000)");
            empresasAprobadas.remove(selectedEmpresa);
            RequestContext.getCurrentInstance().update("formulario:data_table");
            enviar.sendMailNoAprobado(selectedEmpresa.getCorreo(), selectedEmpresa.getNombre_empresa(), selectedEmpresa.getNombre());

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ocurrio un error al tratar de desaprobarla"));
            RequestContext.getCurrentInstance().update("mensajes");
            RequestContext.getCurrentInstance().execute("ocultaMsj(4000)");
        }

    }

    public void cancelar() {
        System.out.println("d");
        empresa = new UsuarioVO();
        renderForm = false;
        RequestContext.getCurrentInstance().update("formulario");
    }

    public void ver(UsuarioVO selectedEmpresa) {
        if (selectedEmpresa != null) {
            if (selectedEmpresa.getId_empresa() > 0) {
                empresa = selectedEmpresa;
                renderForm = true;
                RequestContext.getCurrentInstance().execute("subir()");
                RequestContext.getCurrentInstance().update("formulario");

            }
        }
    }

    public List<UsuarioVO> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<UsuarioVO> empresas) {
        this.empresas = empresas;
    }

    public List<UsuarioVO> getEmpresasAprobadas() {
        return empresasAprobadas;
    }

    public void setEmpresasAprobadas(List<UsuarioVO> empresasAprobadas) {
        this.empresasAprobadas = empresasAprobadas;
    }

    public UsuarioVO getEmpresa() {
        return empresa;
    }

    public void setEmpresa(UsuarioVO empresa) {
        this.empresa = empresa;
    }

    public boolean isRenderForm() {
        return renderForm;
    }

    public void setRenderForm(boolean renderForm) {
        this.renderForm = renderForm;
    }

    public List<EmpresaVO> getEmpresasList() {
        return empresasList;
    }

    public void setEmpresasList(List<EmpresaVO> empresasList) {
        this.empresasList = empresasList;
    }

    public List<EmpresaVO> getEmpresasListAprobadas() {
        return empresasListAprobadas;
    }

    public void setEmpresasListAprobadas(List<EmpresaVO> empresasListAprobadas) {
        this.empresasListAprobadas = empresasListAprobadas;
    }

    public EmpresaVO getSelectedEmpresa() {
        return selectedEmpresa;
    }

    public void setSelectedEmpresa(EmpresaVO selectedEmpresa) {
        this.selectedEmpresa = selectedEmpresa;
    }

}
