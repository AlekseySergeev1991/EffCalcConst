package ru.tecon.effCalcConst.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import ru.tecon.effCalcConst.SystemParamException;
import ru.tecon.effCalcConst.TeconMessage;
import ru.tecon.effCalcConst.ejb.CheckUserSB;
import ru.tecon.effCalcConst.ejb.EffCalcConstSB;
import ru.tecon.effCalcConst.model.Const;
import ru.tecon.effCalcConst.model.ParamHistory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
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

    private List<Const> tariff = new ArrayList<>();
    private List<Const> wage = new ArrayList<>();
    private List<Const> alg3_2_1 = new ArrayList<>();
    private List<Const> alg3_2_2 = new ArrayList<>();
    private List<Const> alg3_2_8 = new ArrayList<>();
    private List<Const> alg3_2_10 = new ArrayList<>();

    private Const selectedConst;
    private List<ParamHistory> tableData;
    private String name;
    private int id;
    private boolean write;
    private String ip;
    private String login;
    private static final Logger logger = Logger.getLogger(EffCalcConstMB.class.getName());


    @EJB
    private CheckUserSB checkUserSB;

    @EJB
    private EffCalcConstSB bean;

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
        tariff.clear();
        wage.clear();
        alg3_2_1.clear();
        alg3_2_2.clear();
        alg3_2_8.clear();
        alg3_2_10.clear();
        List <Const> constList;
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
                    if (temp.getId() == 7 || temp.getId() == 8) {
                        temp.setBool(true);
                    } else {
                        temp.setBool(false);
                    }
                    alg3_2_1.add(temp);
                    break;
                case (4):
                    if (temp.getId() == 11) {
                        temp.setBool(true);
                    } else {
                        temp.setBool(false);
                    }
                    alg3_2_2.add(temp);
                    break;
                case (5):
                    if (temp.getId() == 14 || temp.getId() == 15 || temp.getId() == 16) {
                        temp.setBool(true);
                    } else {
                        temp.setBool(false);
                    }
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

        } catch (SystemParamException e) {
            new TeconMessage(TeconMessage.SEVERITY_ERROR, "Ошибка сохранения", e.getMessage()).send();
            PrimeFaces.current().ajax().update("growl");

        }
        loadData();
    }


    /**
     * Метод для загрузки истории изменения параметра
     */
    public void loadHistData(String parName, int constId) {
        id = constId;
        name = parName;
        tableData = bean.getParamHistory(constId);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParamHistory> getTableData() {
        return tableData;
    }

    public void setTableData(List<ParamHistory> tableData) {
        this.tableData = tableData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
