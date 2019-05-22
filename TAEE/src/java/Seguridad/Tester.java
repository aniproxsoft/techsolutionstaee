/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Seguridad;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ANIPROXTOART
 */
public class Tester {
    public static void main(String arg[]){
        LoginDao login=new LoginDaoImplements();
        List<UsuarioVO> listaUser= new ArrayList<>();
        listaUser=login.getUsuario("antonio.01yea@gmail.com", "123456");
        UsuarioVO user=null;
        user= listaUser.get(0);
        System.out.println("Nombre: "+user.getNombre());
        System.out.println("Correo: "+user.getCorreo());
        System.out.println("Rol: "+user.getNombre_rol());
    }
}
