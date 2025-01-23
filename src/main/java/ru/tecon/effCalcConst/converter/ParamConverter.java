package ru.tecon.effCalcConst.converter;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import ru.tecon.effCalcConst.cdi.EffCalcConstMB;
import ru.tecon.effCalcConst.model.ObjProp;



/**
 * Конвертер для выбора свойства объекта
 *
 * @author Aleksey Sergeev
 */
@FacesConverter("paramConverter")
public class ParamConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{effCalcConst}", EffCalcConstMB.class);

        EffCalcConstMB defaultValues = (EffCalcConstMB) vex.getValue(context.getELContext());

        return defaultValues.getPropList().stream()
                .filter(propType -> propType.getPropId() == Integer.parseInt(value))
                .findFirst()
                .orElse(null);

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((ObjProp) value).getPropId());
    }
}