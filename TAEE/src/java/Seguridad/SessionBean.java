/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Seguridad;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author ANIPROXTOART
 */
@ManagedBean(name = "beanSession")
@ViewScoped
public class SessionBean {

    public void verificarSession(Integer rol) {
        //System.out.println("**************Entra");

        try {
//            System.out.println("Entra**************");
            FacesContext context = FacesContext.getCurrentInstance();
            UsuarioVO user = (UsuarioVO) context.getExternalContext().getSessionMap().get("user");
            if (user == null) {
//                System.out.println("entra if+++++++++++++++++++++++++++++");
                context.getExternalContext().redirect("iniciar_sesion.xhtml");
            } else if (rol !=user.getRol()) {
                context.getExternalContext().redirect("protejido.xhtml");

            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
