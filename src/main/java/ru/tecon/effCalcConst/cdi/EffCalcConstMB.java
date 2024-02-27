package ru.tecon.effCalcConst.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import ru.tecon.effCalcConst.SystemParamException;
import ru.tecon.effCalcConst.ejb.CheckUserSB;
import ru.tecon.effCalcConst.ejb.EffCalcConstSB;
import ru.tecon.effCalcConst.model.Const;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Контроллер для формы Справочник нормативных значений для расчета эффекта от применения алгоритмов
 */

@Named("effCalcConst")
@ViewScoped
public class EffCalcConstMB implements Serializable {

    List<Const> tariff = new ArrayList<>();
    List<Const> wage = new ArrayList<>();
    List<Const> alg3_2_1 = new ArrayList<>();
    List<Const> alg3_2_2 = new ArrayList<>();
    List<Const> alg3_2_8 = new ArrayList<>();
    List<Const> alg3_2_10 = new ArrayList<>();

    Const selectedConst;

    private boolean write;
    private String ip;
    private String login;
    private static final Logger logger = Logger.getLogger(EffCalcConstMB.class.getName());


    @EJB
    private CheckUserSB checkUserSB;

    @EJB
    private EffCalcConstSB bean;

//    @Inject
//    private transient Logger logger;

    @PostConstruct
    private void init() {

        Map<String, String> request = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        String sessionId = request.get("sessionId");
        int formId = Integer.parseInt(request.get("formId"));

        login = checkUserSB.getUser(sessionId);
        ip = bean.getIP(sessionId);
        write = checkUserSB.checkSessionWrite(sessionId, formId);

        loadData();
    }

    /**
     * Метод для загрузки данных
     */
    private void loadData() {
        List <Const> constList = new ArrayList<>();
        constList = bean.getConst();

        for (Const temp : constList) {
            switch (temp.getConstGroupId()) {
                case (1):
                    tariff.add(temp);
                    break;
                case (2):
                    wage.add(temp);
                    break;
                case (3):
                    alg3_2_1.add(temp);
                    break;
                case (4):
                    alg3_2_2.add(temp);
                    break;
                case (5):
                    alg3_2_8.add(temp);
                    break;
                case (6):
                    alg3_2_10.add(temp);
                    break;
            }
        }
    }

    /**
     * Обработчик сохранения изменения строки
     *
     * @param event событие
     */
    public void onRowEdit(RowEditEvent<Const> event) {
        logger.info("update row " + event.getObject());

        try {
            bean.updConst(ip, login, String.valueOf(event.getObject().getId()), String.valueOf(event.getObject().getValue()));
            loadData();

        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }
//        PrimeFaces.current().ajax().update("effCalcConstForm");
    }



    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public Const getSelectedConst() {
        return selectedConst;
    }

    public void setSelectedConst(Const selectedConst) {
        this.selectedConst = selectedConst;
    }

    public List<Const> getTariff() {
        return tariff;
    }

    public void setTariff(List<Const> tariff) {
        this.tariff = tariff;
    }

    public List<Const> getWage() {
        return wage;
    }

    public void setWage(List<Const> wage) {
        this.wage = wage;
    }

    public List<Const> getAlg3_2_1() {
        return alg3_2_1;
    }

    public void setAlg3_2_1(List<Const> alg3_2_1) {
        this.alg3_2_1 = alg3_2_1;
    }

    public List<Const> getAlg3_2_2() {
        return alg3_2_2;
    }

    public void setAlg3_2_2(List<Const> alg3_2_2) {
        this.alg3_2_2 = alg3_2_2;
    }

    public List<Const> getAlg3_2_8() {
        return alg3_2_8;
    }

    public void setAlg3_2_8(List<Const> alg3_2_8) {
        this.alg3_2_8 = alg3_2_8;
    }

    public List<Const> getAlg3_2_10() {
        return alg3_2_10;
    }

    public void setAlg3_2_10(List<Const> alg3_2_10) {
        this.alg3_2_10 = alg3_2_10;
    }
}
