/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VacanteEstadia;

import SearchEstadias.BuscaEstadiasDAO;
import SearchEstadias.BuscaEstadiasDAOImplements;
import SearchEstadias.VacanteVO;
import Services.ServiceEmpresas;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import empresa.EmpresaVO;
import static java.nio.file.Files.list;
import java.util.ArrayList;
import java.util.Iterator;
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
 * @author ferna
 */
@ManagedBean(name = "beanVacanteEstadia")
@ViewScoped
public class VacanteEstadiaBean {

    private VacanteVO vacante;
    private Integer cve_nivel;
    private Integer cve_carrera;
    private Integer cve_perfil;
    private int cves_conocimientos[];
    private int cves_habilidades[];
    private boolean ayuda;
    private boolean ayudarender = false;
    private boolean renderform = false;
    private EmpresaVO selectedEmpresa;
    private List<VacanteVO> vacantes;
    VacanteEstadiaDAO dao;
    int opcion;
    private List<CarreraVO> carreras;
    private List<PerfilVO> perfiles;
    private List<ConocimientoVO> conocimientos;
    private BuscaEstadiasDAO serviceDao;
    private List<NivelAcademicoVO> niveles;
    private List<HabilidadVO> habilidades;
    private List<String> hora1;
    private List<String> hora2;
    private String tipoHora1;
    private String tipoHora2;
    private List<EmpresaVO> empresasList;
    private ServiceEmpresas service;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        vacante = new VacanteVO();
        selectedEmpresa = new EmpresaVO();
        dao = new VacanteEstadiaDAOImplements();
        serviceDao = new BuscaEstadiasDAOImplements();
        vacantes = dao.getVacantesEmpresario();
        niveles = serviceDao.getNiveles();
        habilidades = serviceDao.getHabilidades();
        hora1 = new ArrayList<>();
        hora2 = new ArrayList<>();
        hora1.add("am");
        hora1.add("pm");
        hora2.add("am");
        hora2.add("pm");
        service = new ServiceEmpresas();
        empresasList = service.empresasEstadía(3);
    }

    public void guardar() {
        FacesContext context = FacesContext.getCurrentInstance();
//        System.out.println("id de la empresa.." + selectedEmpresa.getId_empresa());
//        System.out.println("nombre de la empresa.." + selectedEmpresa.getNombre_empresa());
        vacante.setAyuda_economica(ayuda);
        vacante.setId_empresa(selectedEmpresa.getId_empresa());
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
            System.out.println("cc: " + vacante.getHora_inicial());
            JsonArray jarray_conoc = new JsonArray();
            JsonArray jarray_habil = new JsonArray();
            String jsonHabilidades = "";
            String jsonConocimientos = "";
            String jsonVacante = "";
            JsonObject jobject = null;

//        System.out.println("h1: "+hora1);
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
//            System.out.println("json: "+gson.toJson(vacante));
            if (dao.insertUpdateVcanteEmpresario(jsonVacante, jsonConocimientos, jsonHabilidades, opcion)) {
                if (opcion == 1) {
                    cancelar();
                    vacante.setId_vacante(0);
                    vacantes = dao.getVacantesEmpresario();
                    RequestContext.getCurrentInstance().execute("subir()");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "La vacante ha sido agregada correcatamente"));
                    RequestContext.getCurrentInstance().update("mensajes");
                    RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
                    RequestContext.getCurrentInstance().update("formulario");
                } else if (opcion == 2) {
                    cancelar();
                    vacantes = dao.getVacantesEmpresario();
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

    public void editar(VacanteVO vacante) {
        renderform = true;
        opcion = 2;
        if (vacante != null) {
            if (vacante.getId_vacante() > 0) {
                this.vacante = vacante;
                selectedEmpresa.setId_empresa(vacante.getId_empresa());
                selectedEmpresa.setNombre_empresa(vacante.getNombre());
                muestraEmpresas(selectedEmpresa.getNombre_empresa());
                cve_nivel = vacante.getId_nivel();
                buscaCarreras();
                cve_carrera = vacante.getId_carrera();
                buscaPerfiles();
                cve_perfil = vacante.getId_perfil();
                if (vacante.getConocimientos() != null) {
                    cves_conocimientos = new int[vacante.getConocimientos().size()];
                    conocimientos = serviceDao.getConocimientos(vacante.getId_perfil());
                    for (int i = 0; i < vacante.getConocimientos().size(); i++) {
                        cves_conocimientos[i] = vacante.getConocimientos().get(i).getId_conocimiento();
                    }
                    System.out.println("hola el conocimiento..." + vacante.getConocimientos().size());
                }
                if (vacante.getHabilidades() != null) {
                    habilidades = serviceDao.getHabilidades();
//                System.out.println("size: " + vacante.getHabilidades().size());
                    cves_habilidades = new int[vacante.getHabilidades().size()];
                    for (int i = 0; i < vacante.getHabilidades().size(); i++) {
                        cves_habilidades[i] = vacante.getHabilidades().get(i).getId_habilidad();
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
                    ayuda = vacante.isAyuda_economica();
                    mostrarAyudaEco();
                    RequestContext.getCurrentInstance().update("formulario");
                    RequestContext.getCurrentInstance().execute("subir()");

                }
            }
        }
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

    public List<EmpresaVO> muestraEmpresas(String query) {
        List<EmpresaVO> allEmpresas = service.empresasEstadía(3);

        List<EmpresaVO> filteredEmpresas = new ArrayList<EmpresaVO>();

        for (int i = 0; i < allEmpresas.size(); i++) {
            EmpresaVO skin = allEmpresas.get(i);
            if (skin.getNombre_empresa().toLowerCase().contains(query.toLowerCase())) {
                filteredEmpresas.add(skin);
                System.out.println("s:" + skin.getId_empresa());
            }

        }

        return filteredEmpresas;
    }

    public EmpresaVO retrieveEmpresaByName(String name) {
        Iterator<EmpresaVO> it = this.empresasList.iterator();
        while (it.hasNext()) {
            EmpresaVO emp = it.next();
            if (name.equals(emp.getNombre_empresa() + "")) {
                return emp;
            }
        }
        return null;

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

    public void cancelar() {
        renderform = false;
        ayuda=false;
        init();
        RequestContext.getCurrentInstance().update("formulario");
    }

    public void addVacante() {
        renderform = true;
        opcion = 1;
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
        RequestContext.getCurrentInstance().update("formulario");
        RequestContext.getCurrentInstance().execute("subir()");
    }

    public void mostrarAyudaEco() {
        System.out.println("la ayuda.." + ayuda);
        if (ayuda == true) {
            RequestContext.getCurrentInstance().execute("mostrar()");
        } else if (ayuda == false) {
            RequestContext.getCurrentInstance().execute("ocultar()");
        }
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

    public List<VacanteVO> getVacantes() {
        return vacantes;
    }

    public void setVacantes(List<VacanteVO> vacantes) {
        this.vacantes = vacantes;
    }

    public VacanteEstadiaDAO getDao() {
        return dao;
    }

    public void setDao(VacanteEstadiaDAO dao) {
        this.dao = dao;
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

    public VacanteVO getVacante() {
        return vacante;
    }

    public void setVacante(VacanteVO vacante) {
        this.vacante = vacante;
    }

    public int[] getCves_conocimientos() {
        return cves_conocimientos;
    }

    public void setCves_conocimientos(int[] cves_conocimientos) {
        this.cves_conocimientos = cves_conocimientos;
    }

    public int[] getCves_habilidades() {
        return cves_habilidades;
    }

    public void setCves_habilidades(int[] cves_habilidades) {
        this.cves_habilidades = cves_habilidades;
    }

    public boolean isAyuda() {
        return ayuda;
    }

    public void setAyuda(boolean ayuda) {
        this.ayuda = ayuda;
    }

    public boolean isAyudarender() {
        return ayudarender;
    }

    public void setAyudarender(boolean ayudarender) {
        this.ayudarender = ayudarender;
    }

    public boolean isRenderform() {
        return renderform;
    }

    public void setRenderform(boolean renderform) {
        this.renderform = renderform;
    }

    public EmpresaVO getSelectedEmpresa() {
        return selectedEmpresa;
    }

    public void setSelectedEmpresa(EmpresaVO selectedEmpresa) {
        this.selectedEmpresa = selectedEmpresa;
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

    public BuscaEstadiasDAO getServiceDao() {
        return serviceDao;
    }

    public void setServiceDao(BuscaEstadiasDAO serviceDao) {
        this.serviceDao = serviceDao;
    }

    public List<NivelAcademicoVO> getNiveles() {
        return niveles;
    }

    public void setNiveles(List<NivelAcademicoVO> niveles) {
        this.niveles = niveles;
    }

    public List<HabilidadVO> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<HabilidadVO> habilidades) {
        this.habilidades = habilidades;
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

    public List<EmpresaVO> getEmpresasList() {
        return empresasList;
    }

    public void setEmpresasList(List<EmpresaVO> empresasList) {
        this.empresasList = empresasList;
    }

    public ServiceEmpresas getService() {
        return service;
    }

    public void setService(ServiceEmpresas service) {
        this.service = service;
    }

}
