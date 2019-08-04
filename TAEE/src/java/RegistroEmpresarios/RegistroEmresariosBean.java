/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RegistroEmpresarios;

import EmpresaEstadia.EmpresaEstadiaDAO;
import EmpresaEstadia.EmpresaEstadiaDAOImplements;
import Seguridad.UsuarioVO;
import empresa.CiudadVO;
import empresa.EmpresaVO;
import empresa.EstadoVO;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author ANIPROXTOART
 */
@ManagedBean(name = "beanRegistroEmresarios")
@ViewScoped
public class RegistroEmresariosBean implements Serializable {

    private RegistroEmpresariosDAO dao;
    private int cve_estado;
    private int cve_municipio;
    private UsuarioVO user;
    private String contraseña2;
    private List<CiudadVO> ciudades;
    private EmpresaEstadiaDAO dao2;

    public RegistroEmresariosBean() {
    }

    @PostConstruct
    public void init() {
        dao = new RegistroEmpresariosDAOImplements();
        dao2 = new EmpresaEstadiaDAOImplements();
        user = new UsuarioVO();
    }

    public void registrarUsuario() {
        if (user != null) {

            if (user.getContraseña().equals(contraseña2)) {
                user.setId_estado(cve_estado);
                user.setId_ciudad(cve_municipio);
                if (dao.insertUsuario(user)) {
                    RequestContext.getCurrentInstance().execute("subir()");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Se ha registrado correctamente ya puede iniciar sesión"));
                    RequestContext.getCurrentInstance().update("mensajes");
                    RequestContext.getCurrentInstance().execute("ocultaMsj(4000)");
                    FacesContext fc = FacesContext.getCurrentInstance();
                    NavigationHandler navigationHandler = fc.getApplication().getNavigationHandler();
                    navigationHandler.handleNavigation(fc, null, "/vistas/iniciar_sesion/iniciar_sesion.xhtml");
                    fc.renderResponse();
                } 
            } else {
                RequestContext.getCurrentInstance().execute("subir()");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Las contraseñas no coinsiden"));
                RequestContext.getCurrentInstance().update("mensajes");
                RequestContext.getCurrentInstance().execute("ocultaMsj(4000)");
            }
        }

    }

    public void mostrarMunicipio() {

        ciudades = dao2.getMunicipios(cve_estado);
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

    public UsuarioVO getUser() {
        return user;
    }

    public void setUser(UsuarioVO user) {
        this.user = user;
    }

    public String getContraseña2() {
        return contraseña2;
    }

    public void setContraseña2(String contraseña2) {
        this.contraseña2 = contraseña2;
    }

    public List<CiudadVO> getCiudades() {
        return ciudades;
    }

    public void setCiudades(List<CiudadVO> ciudades) {
        this.ciudades = ciudades;
    }
}
