/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AdminEmpresasEgresados;

import Seguridad.UsuarioVO;
import java.util.List;

/**
 *
 * @author ANIPROXTOART
 */
public interface AdminEmpresasEgresadosDAO {

    public List<UsuarioVO> getEmresasEgresados(Integer tipo);

    public List<UsuarioVO> searchEmresasEgresados(Integer tipo);

    public boolean apruebaEmpresa(Integer empresa_id);

    public boolean desapruebaEmpresa(Integer empresa_id);

}
