/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bulkLoad;

import java.util.List;
import Seguridad.RolVO;
import empresa.CiudadVO;
import empresa.EstadoVO;
/**
 *
 * @author ANIPROXTOART
 */
public interface BulkLoadDao {
    public List<EstadoVO>getEstados();
    public List<CiudadVO>getCiudades();
    public List<BulkLoadVO>bulkLoad(String jsonBien,String jsonComplete);
}
