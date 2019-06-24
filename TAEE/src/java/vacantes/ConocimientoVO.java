/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vacantes;

/**
 *
 * @author ANIPROXTOART
 */
public class ConocimientoVO {
    	
private Integer id_conocimiento;
private String conoc_desc;
private Integer id_perfil;

    public Integer getId_conocimiento() {
        return id_conocimiento;
    }

    public void setId_conocimiento(Integer id_conocimiento) {
        this.id_conocimiento = id_conocimiento;
    }

    public String getConoc_desc() {
        return conoc_desc;
    }

    public void setConoc_desc(String conoc_desc) {
        this.conoc_desc = conoc_desc;
    }

    public Integer getId_perfil() {
        return id_perfil;
    }

    public void setId_perfil(Integer id_perfil) {
        this.id_perfil = id_perfil;
    }
}
