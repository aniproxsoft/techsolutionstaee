/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Seguridad;

import java.util.List;

/**
 *
 * @author ANIPROXTOART
 */
public interface LoginDao {
        public List<UsuarioVO> getUsuario(String usuario, String pass);
    
}
