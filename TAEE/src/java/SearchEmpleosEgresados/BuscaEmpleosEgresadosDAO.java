/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchEmpleosEgresados;

import SearchEstadias.VacanteVO;
import java.util.List;
import vacantes.ConocimientoVO;
import vacantes.HabilidadVO;

/**
 *
 * @author ANIPROXTOART
 */
public interface BuscaEmpleosEgresadosDAO {

    public List<VacanteVO> searchVacantes(Integer perfil, String jsonHabilidades, String jsonConociminetos);

    public List<VacanteVO> searchVacantesPorEmpresa(Integer empresa);

    public List<ConocimientoVO> getConocimientoDetail(Integer claveVacante);

    public List<HabilidadVO> getHabilidadesDetail(Integer claveVacante);
}
