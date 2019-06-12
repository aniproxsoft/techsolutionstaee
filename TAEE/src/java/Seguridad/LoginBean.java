/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Seguridad;

import java.io.Serializable;
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
@ManagedBean(name = "beanLogin")
@ViewScoped
public class LoginBean implements Serializable {

    //Variables
    private UsuarioVO user;
    private LoginDao dao;

    @PostConstruct
    public void init() {
        user = new UsuarioVO();
    }

    public LoginBean() {
    }

    public String iniciarSesion() {

        String redireccion = null;
        try {
            System.out.println("******" + user.getCorreo() + "/" + user.getContraseña());
            UsuarioVO u;
            dao = new LoginDaoImplements();
            u = dao.getUsuario(user.getCorreo(), user.getContraseña()).get(0);
            System.out.println(u);
            if (u != null) {

                if (u.getRol() == 2) {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", u);
                    redireccion = "empresas_egresados?faces-redirect=true";
                } else if (u.getRol() == 1) {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", u);
                    redireccion = "empresas_estadias?faces-redirect=true";
                }

            } else {
                System.out.println(u);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "error usuario y/o contraseña incorrecta"));
                RequestContext.getCurrentInstance().update("alertaLogin");
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "error" + e));
        }
        System.out.println("--------------" + redireccion);
        return redireccion;
    }

    public UsuarioVO getUser() {
        return user;
    }

    public void setUser(UsuarioVO user) {
        this.user = user;
    }

    public LoginDao getDao() {
        return dao;
    }

    public void setDao(LoginDao dao) {
        this.dao = dao;
    }

}
