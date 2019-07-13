/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EmpresaEstadia;
import empresa.CiudadVO;
import empresa.EmpresaVO;
import empresa.EstadoVO;
import java.util.List;
/**
 *
 * @author ferna
 */
public interface EmpresaEstadiaDAO {
    public List<EmpresaVO> getEmpresas(Integer clave);
    public List<EstadoVO> getEstados();
    public List<CiudadVO> getMunicipios(Integer clave);
    public boolean deleteEmpresa(Integer clave);
    public boolean insertUpdate(EmpresaVO empresa,Integer editar);
}
