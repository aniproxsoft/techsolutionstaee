/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Seguridad;

import java.io.Serializable;
import java.util.ArrayList;
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
@ManagedBean(name = "beanLogin")
@ViewScoped
public class LoginBean implements Serializable {

    //Variables
    private UsuarioVO user;
    private LoginDao dao;
    List<UsuarioVO> users;

    @PostConstruct
    public void init() {
        user = new UsuarioVO();

    }

    public LoginBean() {
    }

    public String iniciarSesion() {
        users = new ArrayList<>();
        String redireccion = null;
        try {
//            System.out.println("******" + user.getCorreo() + "/" + user.getContraseña());

            dao = new LoginDaoImplements();
            users = dao.getUsuario(user.getCorreo(), user.getContraseña());
            UsuarioVO u = null;

            if (users != null) {
                u = users.get(0);
            } else {
                u = null;
            }
            //System.out.println(u);
            if (u != null) {

                if (u.getRol() == 2) {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", u);
                    redireccion = "/vistas/iniciar_sesion/empresas_egresados.xhtml?faces-redirect=true";
                } else if (u.getRol() == 1) {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", u);
                    redireccion = "/vistas/iniciar_sesion/empresas_estadias.xhtml?faces-redirect=true";
                }else if(u.getRol() == 3){
                    redireccion = "/vistas/iniciar_sesion/empresario_index.xhtml?faces-redirect=true";
                }

            } else {
                //System.out.println("entra else");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "error usuario y/o contraseña incorrecta"));
                RequestContext.getCurrentInstance().update("alertaLogin");
                RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "error" + e));
        }
        //System.out.println("--------------r: " + redireccion);
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
