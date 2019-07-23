/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VacantesEmpresario;

import SearchEstadias.VacanteVO;
import java.util.List;

/**
 *
 * @author ANIPROXTOART
 */
public interface VacantesEmpresarioDAO {

    public List<VacanteVO> getVacantesEmpresario(int empresa_id);

    public boolean insertUpdateVcanteEmpresario(String json_vacante, String json_conocimiento, String json_habilidades, int opcion);

    public boolean deleteVacanteEmpresario(int vacante_id);

}
