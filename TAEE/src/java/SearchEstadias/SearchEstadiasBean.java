/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchEstadias;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import vacantes.CarreraVO;
import vacantes.ConocimientoVO;
import vacantes.HabilidadVO;
import vacantes.NivelAcademicoVO;
import vacantes.PerfilVO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.context.RequestContext;

/**
 *
 * @author ANIPROXTOART
 */
@ManagedBean(name = "beanSearchEstadias")
@ViewScoped
public class SearchEstadiasBean implements Serializable {

    private List<NivelAcademicoVO> niveles;
    private List<CarreraVO> carreras;
    private List<PerfilVO> perfiles;
    private List<ConocimientoVO> conocimientos;
    private List<HabilidadVO> habilidades;
    private List<VacanteVO> vacantes;
    private List<VacanteVO> favoritas;
    private BuscaEstadiasDAO dao;
    private Integer cve_nivel;
    private Integer cve_carrera;
    private Integer cve_perfil;
    private int cves_conocimientos[];
    private VacanteVO vacanteDetail;
    private boolean renderView;
    private int cves_habilidades[];

    /**
     * Creates a new instance of SearchEstadiasBean
     */
    @PostConstruct
    public void init() {
        dao = new BuscaEstadiasDAOImplements();
        niveles = dao.getNiveles();
        habilidades = dao.getHabilidades();
        vacantes = new ArrayList<>();

    }

    public void buscaCarreras() {
        carreras = new ArrayList<>();
        carreras.add(dao.getCarreras(cve_nivel).get(0));
    }

    public void buscaPerfiles() {
        perfiles = dao.getPerfiles(cve_carrera);
    }

    public void buscaConocimientos() {
        conocimientos = dao.getConocimientos(cve_perfil);
    }

    public SearchEstadiasBean() {

    }

    public void buscaEstadias() {
        favoritas = new ArrayList<>();
        JsonArray jarray_conoc = new JsonArray();
        JsonArray jarray_habil = new JsonArray();
        String jsonHabilidades = "";
        String jsonConocimientos = "";
        JsonObject jobject = null;
        for (int i = 0; i < cves_conocimientos.length; i++) {
            jobject = new JsonObject();
            jobject.addProperty("id_conocimiento", cves_conocimientos[i]);
            jarray_conoc.add(jobject);
            jsonConocimientos = jarray_conoc.getAsJsonArray().toString();
        }
        if (cves_habilidades.length == 0) {
//            System.out.println("entra");
            jsonHabilidades = jarray_habil.getAsJsonArray().toString();

        } else {
            for (int i = 0; i < cves_habilidades.length; i++) {
                jobject = new JsonObject();
                jobject.addProperty("id_habilidad", cves_habilidades[i]);
                jarray_habil.add(jobject);
                jsonHabilidades = jarray_habil.getAsJsonArray().toString();

            }
        }
//        System.out.println("h: " + jsonHabilidades);
//        System.out.println("c: " + jsonConocimientos);
//        System.out.println("perfil: " + cve_perfil);
        vacantes = new ArrayList<>();
        vacantes = dao.searchVacantes(cve_perfil, jsonHabilidades, jsonConocimientos);
        if (vacantes.size() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Aún no hay vacantes para este perfil"));
            RequestContext.getCurrentInstance().update("mensajes");
            RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
        } else {
            RequestContext.getCurrentInstance().update("vacantes");

        }
//        System.out.println("res" + vacantes.size());
        RequestContext.getCurrentInstance().update("vacantes");

    }

    public void showDetail(VacanteVO selectedVacante) {
        if (selectedVacante != null) {
            vacanteDetail = selectedVacante;
        }
        Gson gson = new Gson();
        renderView = true;
//        System.out.println("objeto seleccionado: " + gson.toJson(vacanteDetail));
        RequestContext.getCurrentInstance().update("formulario");
    }

    public void addOrDeleteFav(VacanteVO favorita) {
        if (favorita.isCheck()) {
            favoritas.add(favorita);
        }else{
            favoritas.remove(favorita);
        }
//        System.out.println("tam: "+favoritas.size());
    }

    public void generaReporte() throws IOException, JRException {
        if (favoritas.size() > 0) {
            File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/vacantesFavoritas.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasper.getPath(), null, new JRBeanCollectionDataSource(favoritas));
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.addHeader("Content-disposition", "attachment; filename=vacantes_favoritas.pdf");
            ServletOutputStream stream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(print, stream);
            stream.flush();
            stream.close();
            FacesContext.getCurrentInstance().responseComplete();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Debe seleccionar al menos una vacante favorita"));
            RequestContext.getCurrentInstance().update("mensajes");
            RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
        }

    }

    public List<NivelAcademicoVO> getNiveles() {
        return niveles;
    }

    public void setNiveles(List<NivelAcademicoVO> niveles) {
        this.niveles = niveles;
    }

    public Integer getCve_nivel() {
        return cve_nivel;
    }

    public void setCve_nivel(Integer cve_nivel) {
        this.cve_nivel = cve_nivel;
    }

    public List<CarreraVO> getCarreras() {
        return carreras;
    }

    public void setCarreras(List<CarreraVO> carreras) {
        this.carreras = carreras;
    }

    public List<PerfilVO> getPerfiles() {
        return perfiles;
    }

    public void setPerfiles(List<PerfilVO> perfiles) {
        this.perfiles = perfiles;
    }

    public List<ConocimientoVO> getConocimientos() {
        return conocimientos;
    }

    public void setConocimientos(List<ConocimientoVO> conocimientos) {
        this.conocimientos = conocimientos;
    }

    public List<HabilidadVO> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<HabilidadVO> habilidades) {
        this.habilidades = habilidades;
    }

    public Integer getCve_carrera() {
        return cve_carrera;
    }

    public void setCve_carrera(Integer cve_carrera) {
        this.cve_carrera = cve_carrera;
    }

    public Integer getCve_perfil() {
        return cve_perfil;
    }

    public void setCve_perfil(Integer cve_perfil) {
        this.cve_perfil = cve_perfil;
    }

    public int[] getCves_habilidades() {
        return cves_habilidades;
    }

    public void setCves_habilidades(int[] cves_habilidades) {
        this.cves_habilidades = cves_habilidades;
    }

    public int[] getCves_conocimientos() {
        return cves_conocimientos;
    }

    public void setCves_conocimientos(int[] cves_conocimientos) {
        this.cves_conocimientos = cves_conocimientos;
    }

    public List<VacanteVO> getVacantes() {
        return vacantes;
    }

    public void setVacantes(List<VacanteVO> vacantes) {
        this.vacantes = vacantes;
    }

    public BuscaEstadiasDAO getDao() {
        return dao;
    }

    public void setDao(BuscaEstadiasDAO dao) {
        this.dao = dao;
    }

    public VacanteVO getVacanteDetail() {
        return vacanteDetail;
    }

    public void setVacanteDetail(VacanteVO vacanteDetail) {
        this.vacanteDetail = vacanteDetail;
    }

    public boolean isRenderView() {
        return renderView;
    }

    public void setRenderView(boolean renderView) {
        this.renderView = renderView;
    }

}
