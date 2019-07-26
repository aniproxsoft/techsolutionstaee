/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import EmpresaEstadia.EmpresaEstadiaBean;
import empresa.EmpresaVO;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author ferna
 */
@FacesConverter("empresaConverterAdminEstadia")
public class ConverterEmpresaAdminEstadia implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            ValueExpression vex
                    = fc.getApplication().getExpressionFactory()
                            .createValueExpression(fc.getELContext(),
                                    "#{beanEmpresaEstadia}", EmpresaEstadiaBean.class);

            EmpresaEstadiaBean bean = (EmpresaEstadiaBean) vex.getValue(fc.getELContext());
            System.out.println("v: " + value);

            Object o = bean.retrieveEmpresaByName(value);
//            System.out.println("obj: "+o.getClass());
            return o;
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o != null) {
            String s = ((EmpresaVO) o).getNombre_empresa() + "";
            return s;
        } else {
            return "";
        }
    }

}
