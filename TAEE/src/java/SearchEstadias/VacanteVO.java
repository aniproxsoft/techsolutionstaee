/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchEstadias;

import java.util.List;
import javax.faces.context.FacesContext;
import vacantes.ConocimientoVO;
import vacantes.HabilidadVO;

/**
 *
 * @author ANIPROXTOART
 */
public class VacanteVO {

    private Integer id_vacante;
    private String titulo;
    private String vacante_desc;
    private String nombre_nivel;
    private String carrera_desc;
    private Integer id_perfil;
    private String nombre_perfil;
    private Integer edad_min;
    private Integer edad_max;
    private Double salario_min;
    private Double salario_max;
    private String hora_inicial;
    private String hora_final;
    private String experiencia;
    private Integer id_empresa;
    private String nombre;
    private String status;
    private String direccion;
    private String vacante_descCorta;
    private boolean renderSalario = false;
    private List<HabilidadVO> habilidades;
    private List<ConocimientoVO> conocimientos;
    private String num_telefono;
    private String correo_empresa;
    private boolean renderEdad;
    private boolean renderExperiencia;
    private boolean renderHora;
    private boolean renderHabilidades;
    private boolean renderEdad2;
    private boolean ayuda_economica;
    private String ayuda;
    private boolean check;
    private int id_nivel;
    private int id_carrera;
    private int num_vacantes;
    private int num_postulaciones;
    
    private String imagen_utn=FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/utn.jpg");

    public Integer getId_vacante() {
        return id_vacante;
    }

    public void setId_vacante(Integer id_vacante) {
        this.id_vacante = id_vacante;
    }

    public String getTitulo() {
        return titulo; 
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getVacante_desc() {
        return vacante_desc;
    }

    public void setVacante_desc(String vacante_desc) {
        this.vacante_desc = vacante_desc;
    }

    public String getNombre_nivel() {
        return nombre_nivel;
    }

    public void setNombre_nivel(String nombre_nivel) {
        this.nombre_nivel = nombre_nivel;
    }

    public String getCarrera_desc() {
        return carrera_desc;
    }

    public void setCarrera_desc(String carrera_desc) {
        this.carrera_desc = carrera_desc;
    }

    public Integer getId_perfil() {
        return id_perfil;
    }

    public void setId_perfil(Integer id_perfil) {
        this.id_perfil = id_perfil;
    }

    public String getNombre_perfil() {
        return nombre_perfil;
    }

    public void setNombre_perfil(String nombre_perfil) {
        this.nombre_perfil = nombre_perfil;
    }

    public Integer getEdad_min() {
        return edad_min;
    }

    public void setEdad_min(Integer edad_min) {
        this.edad_min = edad_min;
    }

    public Integer getEdad_max() {
        return edad_max;
    }

    public void setEdad_max(Integer edad_max) {
        this.edad_max = edad_max;
    }

    public Double getSalario_min() {
        return salario_min;
    }

    public void setSalario_min(Double salario_min) {
        this.salario_min = salario_min;
    }

    public Double getSalario_max() {
        return salario_max;
    }

    public void setSalario_max(Double salario_max) {
        this.salario_max = salario_max;
    }

    public String getHora_inicial() {
        return hora_inicial;
    }

    public void setHora_inicial(String hora_inicial) {
        this.hora_inicial = hora_inicial;
    }

    public String getHora_final() {
        return hora_final;
    }

    public void setHora_final(String hora_final) {
        this.hora_final = hora_final;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public Integer getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(Integer id_empresa) {
        this.id_empresa = id_empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getVacante_descCorta() {
        return vacante_descCorta;
    }

    public void setVacante_descCorta(String vacante_descCorta) {
        this.vacante_descCorta = vacante_descCorta;
    }

    public boolean isRenderSalario() {
        return renderSalario;
    }

    public void setRenderSalario(boolean renderSalario) {
        this.renderSalario = renderSalario;
    }

    public List<HabilidadVO> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<HabilidadVO> habilidades) {
        this.habilidades = habilidades;
    }

    public List<ConocimientoVO> getConocimientos() {
        return conocimientos;
    }

    public void setConocimientos(List<ConocimientoVO> conocimientos) {
        this.conocimientos = conocimientos;
    }

    public String getNum_telefono() {
        return num_telefono;
    }

    public void setNum_telefono(String num_telefono) {
        this.num_telefono = num_telefono;
    }

    public String getCorreo_empresa() {
        return correo_empresa;
    }

    public void setCorreo_empresa(String correo_empresa) {
        this.correo_empresa = correo_empresa;
    }

    public boolean isRenderEdad() {
        return renderEdad;
    }

    public void setRenderEdad(boolean renderEdad) {
        this.renderEdad = renderEdad;
    }

    public boolean isRenderExperiencia() {
        return renderExperiencia;
    }

    public void setRenderExperiencia(boolean renderExperiencia) {
        this.renderExperiencia = renderExperiencia;
    }

    public boolean isRenderHora() {
        return renderHora;
    }

    public void setRenderHora(boolean renderHora) {
        this.renderHora = renderHora;
    }

    public boolean isRenderHabilidades() {
        return renderHabilidades;
    }

    public void setRenderHabilidades(boolean renderHabilidades) {
        this.renderHabilidades = renderHabilidades;
    }

    public boolean isRenderEdad2() {
        return renderEdad2;
    }

    public void setRenderEdad2(boolean renderEdad2) {
        this.renderEdad2 = renderEdad2;
    }

    public boolean isAyuda_economica() {
        return ayuda_economica;
    }

    public void setAyuda_economica(boolean ayuda_economica) {
        this.ayuda_economica = ayuda_economica;
    }

    public String getAyuda() {
        return ayuda;
    }

    public void setAyuda(String ayuda) {
        this.ayuda = ayuda;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getImagen_utn() {
        return imagen_utn;
    }

    public void setImagen_utn(String imagen_utn) {
        this.imagen_utn = imagen_utn;
    }

    public int getId_nivel() {
        return id_nivel;
    }

    public void setId_nivel(int id_nivel) {
        this.id_nivel = id_nivel;
    }

    public int getId_carrera() {
        return id_carrera;
    }

    public void setId_carrera(int id_carrera) {
        this.id_carrera = id_carrera;
    }
    
    public int getNum_vacantes(){
        return num_vacantes;
    }
    
    public void setNum_vacantes(int num_vacantes) {
        this.num_vacantes = num_vacantes;
    }
    
    public int getNum_postulaciones() {
        return num_postulaciones;
    }

    public void setNum_postulaciones(int num_postulaciones) {
        this.num_postulaciones = num_postulaciones = (this.num_vacantes * 2) ;
    }
}
