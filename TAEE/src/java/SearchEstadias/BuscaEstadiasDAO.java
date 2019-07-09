/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchEstadias;

import java.util.List;
import vacantes.CarreraVO;
import vacantes.ConocimientoVO;
import vacantes.HabilidadVO;
import vacantes.NivelAcademicoVO;
import vacantes.PerfilVO;

/**
 *
 * @author ANIPROXTOART
 */
public interface BuscaEstadiasDAO {

    public List<VacanteVO> searchVacantes(Integer perfil, String jsonHabilidades, String jsonConociminetos);

    public List<VacanteVO> searchVacantesPorEmpresa(Integer empresa);

    public List<NivelAcademicoVO> getNiveles();

    public List<CarreraVO> getCarreras(Integer clave);

    public List<PerfilVO> getPerfiles(Integer clave);

    public List<HabilidadVO> getHabilidades();

    public List<ConocimientoVO> getConocimientos(Integer clave);

    public List<ConocimientoVO> getConocimientoDetail(Integer claveVacante);

    public List<HabilidadVO> getHabilidadesDetail(Integer claveVacante);
}
