/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Seguridad;

import java.io.IOException;
import java.io.Serializable;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author ANIPROXTOART
 */
@ManagedBean(name = "beanSession")
@ViewScoped
public class SessionBean implements Serializable {

    public void verificarSession(Integer rol) {
        //System.out.println("**************Entra");

        try {
//            System.out.println("Entra**************");
            FacesContext context = FacesContext.getCurrentInstance();
            UsuarioVO user = (UsuarioVO) context.getExternalContext().getSessionMap().get("user");
            if (user == null) {
//                System.out.println("entra if+++++++++++++++++++++++++++++");
                //context.getExternalContext().redirect("iniciar_sesion.xhtml?=faces-redirect=true");
                FacesContext fc = FacesContext.getCurrentInstance();
                NavigationHandler navigationHandler = fc.getApplication().getNavigationHandler();
                navigationHandler.handleNavigation(fc, null, "/vistas/iniciar_sesion/iniciar_sesion.xhtml");
                fc.renderResponse();
            } else if (rol != user.getRol()) {
                //context.getExternalContext().redirect("protejido.xhtml?faces-redirect=true");
                FacesContext fc = FacesContext.getCurrentInstance();
                NavigationHandler navigationHandler = fc.getApplication().getNavigationHandler();
                navigationHandler.handleNavigation(fc, null, "/vistas/iniciar_sesion/protejido.xhtml");
                fc.renderResponse();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cerrarSesion() {
//        System.out.println("entra logout");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }

    public String volver() {
        UsuarioVO u = null;
        String redireccion = "";
        u = (UsuarioVO) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user");
        if (u != null) {

            switch (u.getRol()) {
                case 2:

                    redireccion = "/vistas/iniciar_sesion/empresas_egresados.xhtml?faces-redirect=true";
                    break;
                case 1:

                    redireccion = "/vistas/iniciar_sesion/empresas_estadias.xhtml?faces-redirect=true";
                    break;
                case 3:

                    redireccion = "/vistas/iniciar_sesion/empresario_index.xhtml?faces-redirect=true";
                    break;
                default:
                    break;
            }

        }
//        System.out.println("driec: "+redireccion);
        return redireccion;
    }
}
