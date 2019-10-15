/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VacantesEmpresario;

import SearchEstadias.BuscaEstadiasDAO;
import SearchEstadias.BuscaEstadiasDAOImplements;
import SearchEstadias.VacanteVO;
import Seguridad.UsuarioVO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import vacantes.CarreraVO;
import vacantes.ConocimientoVO;
import vacantes.HabilidadVO;
import vacantes.NivelAcademicoVO;
import vacantes.PerfilVO;

/**
 *
 * @author ANIPROXTOART
 */
@ManagedBean(name = "beanVacantesEmpresario")
@ViewScoped
public class vacantesEmpresarioBean implements Serializable {

    private List<VacanteVO> vacantes;
    VacantesEmpresarioDAO dao;
    private boolean renderVacantes;
    boolean renderNovacantes;
    UsuarioVO user;
    private VacanteVO vacante;
    private BuscaEstadiasDAO serviceDao;
    private Integer cve_nivel;
    private Integer cve_carrera;
    private Integer cve_perfil;
    private int cves_conocimientos[];
    private List<NivelAcademicoVO> niveles;
    private List<CarreraVO> carreras;
    private List<PerfilVO> perfiles;
    private List<ConocimientoVO> conocimientos;
    private List<HabilidadVO> habilidades;
    private int cves_habilidades[];
    private boolean renderAddEdit;
    private List<String> hora1;
    private List<String> hora2;
    private String tipoHora1;
    private String tipoHora2;
    Integer id_empresa;
    int opcion;
    private boolean renderAdd;

    public boolean isRenderNovacantes() {
        return renderNovacantes;
    }

    public void setRenderNovacantes(boolean renderNovacantes) {
        this.renderNovacantes = renderNovacantes;
    }

    public vacantesEmpresarioBean() {
    }

    @PostConstruct
    public void init() {
        dao = new VacantesEmpresarioDAOImplements();
        serviceDao = new BuscaEstadiasDAOImplements();
        FacesContext context = FacesContext.getCurrentInstance();
        UsuarioVO user = (UsuarioVO) context.getExternalContext().getSessionMap().get("user");
//        System.out.println("id: " + user.getId_empresa());
        validaUsuario(user.getStatus_empresa(), user.getId_empresa());
        vacante = new VacanteVO();
        niveles = serviceDao.getNiveles();
        habilidades = serviceDao.getHabilidades();
        hora1 = new ArrayList<>();
        hora2 = new ArrayList<>();
        hora1.add("am");
        hora1.add("pm");
        hora2.add("am");
        hora2.add("pm");
    }

    public void buscaCarreras() {
        carreras = new ArrayList<>();
        carreras.add(serviceDao.getCarreras(cve_nivel).get(0));
    }

    public void buscaPerfiles() {
        perfiles = serviceDao.getPerfiles(cve_carrera);
    }

    public void buscaConocimientos() {
        conocimientos = serviceDao.getConocimientos(cve_perfil);
    }

    public void validaUsuario(String status, int id_empresa) {
        if (status != null) {
            if (status.equals("1") || status.equals("0") || status.equals("3")) {
                renderNovacantes = true;
                renderVacantes = false;
            } else if (status.equals("2")) {
                renderNovacantes = false;
                renderVacantes = true;
                renderAdd = true;
//                System.out.println("oid: "+user.getId_empresa());
                vacantes = dao.getVacantesEmpresario(id_empresa);
                this.id_empresa = id_empresa;
            }
        } else {
            renderNovacantes = true;
            renderVacantes = false;
        }
        RequestContext.getCurrentInstance().update("formulario");
    }

    public void guardar() {
        FacesContext context = FacesContext.getCurrentInstance();
        UsuarioVO user = (UsuarioVO) context.getExternalContext().getSessionMap().get("user");
        this.id_empresa = user.getId_empresa();
        vacante.setId_empresa(id_empresa);
        vacante.setId_perfil(cve_perfil);
               
        if (vacante.getExperiencia() == null) {
            vacante.setExperiencia("0");
        } else if (vacante.getExperiencia().equals("")) {
            vacante.setExperiencia("0");
        }
        String aux = "";
        if (vacante.getHora_inicial() != null) {
            if (vacante.getHora_inicial() != "") {
                aux = vacante.getHora_inicial().replace(":", "");
            } else {
                aux = "0";
            }

        } else {
            aux = "0";
        }

        int aux2 = Integer.parseInt(aux);
        if (vacante.getHora_final() != null) {
            if (vacante.getHora_final() != "") {
                aux = vacante.getHora_final().replace(":", "");
            } else {
                aux = "0";
            }

        } else {
            aux = "0";
        }
        int aux3 = Integer.parseInt(aux);

        if (aux2 > 1259) {
            RequestContext.getCurrentInstance().execute("subir()");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ingrese una hora inicial  correcta"));
            RequestContext.getCurrentInstance().update("mensajes");
            RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");

        } else if (aux3 > 1259) {
            RequestContext.getCurrentInstance().execute("subir()");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ingrese una hora final  correcta"));
            RequestContext.getCurrentInstance().update("mensajes");
            RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
        } else {
            
            if(vacante.getNum_vacantes() <= 0){
                RequestContext.getCurrentInstance().execute("subir()");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ingrese el numero de vacantes"));
                RequestContext.getCurrentInstance().update("mensajes");
                RequestContext.getCurrentInstance().execute("ocultaMsj(8000)");
            }else if(vacante.getNum_vacantes() > 10){
                RequestContext.getCurrentInstance().execute("subir()");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Excede el numero de vacantes"));
                RequestContext.getCurrentInstance().update("mensajes");
                RequestContext.getCurrentInstance().execute("ocultaMsj(8000)");
            }else {
                System.out.println("cc: " + vacante.getHora_inicial());
                JsonArray jarray_conoc = new JsonArray();
                JsonArray jarray_habil = new JsonArray();
                String jsonHabilidades = "";
                String jsonConocimientos = "";
                String jsonVacante = "";
                JsonObject jobject = null;
                
                System.out.println("h1: "+hora1);
            if (vacante.getHora_inicial() != null) {
                if (vacante.getHora_inicial() != "") {
                    aux = vacante.getHora_inicial() + " " + tipoHora1;
                } else {
                    aux = null;
                }

                vacante.setHora_inicial(aux);
            }
            if (vacante.getHora_final() != null) {
                if (vacante.getHora_final() != "") {
                    aux = vacante.getHora_final() + " " + tipoHora2;
                } else {
                    aux = null;
                }

                vacante.setHora_final(aux);

            }
            System.out.println(vacante.getHora_final() + "-" + vacante.getHora_inicial());
            for (int i = 0; i < cves_conocimientos.length; i++) {
                jobject = new JsonObject();
                jobject.addProperty("id_conocimiento", cves_conocimientos[i]);
                jarray_conoc.add(jobject);
                jsonConocimientos = jarray_conoc.getAsJsonArray().toString();

            }
            System.out.println(jarray_conoc.getAsJsonArray().toString());
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
            Gson gson = new Gson();
            jsonVacante = gson.toJson(vacante);
//          System.out.println("json: "+gson.toJson(vacante));
//          System.out.println("jasonVacante: " + jsonVacante);
//          System.out.println("jsonConocimientos: " + jsonConocimientos);
//          System.out.println("jsonHabilidades: " + jsonHabilidades);
            if (dao.insertUpdateVcanteEmpresario(jsonVacante, jsonConocimientos, jsonHabilidades, opcion)) {
                if (opcion == 1) {
                    renderAdd = true;
                    vacantes = dao.getVacantesEmpresario(id_empresa);
                    renderAddEdit = false;
                    RequestContext.getCurrentInstance().execute("subir()");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "La vacante ha sido agregada correcatamente"));
                    RequestContext.getCurrentInstance().update("mensajes");
                    RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
                    RequestContext.getCurrentInstance().update("formulario");
                } else if (opcion == 2) {
                    renderAdd = true;
                    vacantes = dao.getVacantesEmpresario(id_empresa);
                    renderAddEdit = false;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "La vacante ha sido modificada correcatamente"));
                    RequestContext.getCurrentInstance().execute("subir()");
                    RequestContext.getCurrentInstance().update("mensajes");
                    RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
                    RequestContext.getCurrentInstance().update("formulario");
                }

                } else {
                RequestContext.getCurrentInstance().execute("subir()");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ocurrio un error al tratar de guardarla"));
                RequestContext.getCurrentInstance().update("mensajes");
                RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");

                }
            }    
//       
        }
//       
    }

    public void editar(VacanteVO vacante) {
        renderAdd = false;
//        System.out.println("id editar: " + vacante.getId_vacante());
        opcion = 2;
        if (vacante != null) {
            if (vacante.getId_vacante() > 0) {
                this.vacante = vacante;
                cve_nivel = vacante.getId_nivel();
                buscaCarreras();
                cve_carrera = vacante.getId_carrera();
                buscaPerfiles();
                cve_perfil = vacante.getId_perfil();
                if (vacante.getConocimientos() != null) {
                    cves_conocimientos = new int[vacante.getConocimientos().size()];
                    buscaConocimientos();
                    for (int i = 0; i < vacante.getConocimientos().size(); i++) {
                        cves_conocimientos[i] = vacante.getConocimientos().get(i).getId_conocimiento();
                    }
                }
                if (vacante.getHabilidades() != null) {
                    habilidades = serviceDao.getHabilidades();
//                System.out.println("size: " + vacante.getHabilidades().size());
                    cves_habilidades = new int[vacante.getHabilidades().size()];
                    for (int i = 0; i < vacante.getHabilidades().size(); i++) {
                        cves_habilidades[i] = vacante.getHabilidades().get(i).getId_habilidad();
                    }
                }
                if (vacante.getHora_inicial() != null) {
                    if (vacante.getHora_inicial().contains("am")) {
                        tipoHora1 = "am";
                    } else if (vacante.getHora_inicial().contains("pm")) {
                        tipoHora1 = "pm";
                    }
                }
                if (vacante.getHora_final() != null) {
                    if (vacante.getHora_final().contains("am")) {
                        tipoHora2 = "am";
                    } else if (vacante.getHora_final().contains("pm")) {
                        tipoHora2 = "pm";
                    }

                }

                renderAddEdit = true;
                RequestContext.getCurrentInstance().update("formulario");
            }
        }
    }

    public void cancelar() {
        renderAdd = true;
        renderAddEdit = false;
        init();
        RequestContext.getCurrentInstance().update("formulario");
    }

    public void eliminar(VacanteVO vacante) {
//        System.out.println("id eliminar: " + vacante.getId_vacante());
        if (vacante != null) {
            if (vacante.getId_empresa() > 0) {

                if (dao.deleteVacanteEmpresario(vacante.getId_vacante())) {
                    vacantes.remove(vacante);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "La vacante ha sido eliminada"));
                    RequestContext.getCurrentInstance().update("mensajes");
                    RequestContext.getCurrentInstance().execute("ocultaMsj(4000)");
                    RequestContext.getCurrentInstance().update("formulario:data_table");
                }

            }
        }
    }

    public void addVacante() {
        opcion = 1;
        renderAdd = false;
        niveles = serviceDao.getNiveles();
        carreras = new ArrayList<>();
        perfiles = new ArrayList<>();
        conocimientos = new ArrayList<>();
        habilidades = new ArrayList<>();
        vacante = new VacanteVO();
        cve_carrera = null;
        cve_nivel = null;
        cve_perfil = null;
        cves_habilidades = null;
        renderAddEdit = true;
        RequestContext.getCurrentInstance().update("formulario");
        RequestContext.getCurrentInstance().execute("subir()");
    }

    public List<VacanteVO> getVacantes() {
        return vacantes;
    }

    public void setVacantes(List<VacanteVO> vacantes) {
        this.vacantes = vacantes;
    }

    public boolean isRenderVacantes() {
        return renderVacantes;
    }

    public void setRenderVacantes(boolean renderVacantes) {
        this.renderVacantes = renderVacantes;
    }

    public VacanteVO getVacante() {
        return vacante;
    }

    public void setVacante(VacanteVO vacante) {
        this.vacante = vacante;
    }

    public Integer getCve_nivel() {
        return cve_nivel;
    }

    public void setCve_nivel(Integer cve_nivel) {
        this.cve_nivel = cve_nivel;
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

    public int[] getCves_conocimientos() {
        return cves_conocimientos;
    }

    public void setCves_conocimientos(int[] cves_conocimientos) {
        this.cves_conocimientos = cves_conocimientos;
    }

    public List<NivelAcademicoVO> getNiveles() {
        return niveles;
    }

    public void setNiveles(List<NivelAcademicoVO> niveles) {
        this.niveles = niveles;
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

    public int[] getCves_habilidades() {
        return cves_habilidades;
    }

    public void setCves_habilidades(int[] cves_habilidades) {
        this.cves_habilidades = cves_habilidades;
    }

    public boolean isRenderAddEdit() {
        return renderAddEdit;
    }

    public void setRenderAddEdit(boolean renderAddEdit) {
        this.renderAddEdit = renderAddEdit;
    }

    public List<String> getHora1() {
        return hora1;
    }

    public void setHora1(List<String> hora1) {
        this.hora1 = hora1;
    }

    public List<String> getHora2() {
        return hora2;
    }

    public void setHora2(List<String> hora2) {
        this.hora2 = hora2;
    }

    public String getTipoHora1() {
        return tipoHora1;
    }

    public void setTipoHora1(String tipoHora1) {
        this.tipoHora1 = tipoHora1;
    }

    public String getTipoHora2() {
        return tipoHora2;
    }

    public void setTipoHora2(String tipoHora2) {
        this.tipoHora2 = tipoHora2;
    }

    public boolean isRenderAdd() {
        return renderAdd;
    }

    public void setRenderAdd(boolean renderAdd) {
        this.renderAdd = renderAdd;
    }

}
