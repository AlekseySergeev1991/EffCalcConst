package ru.tecon.effCalcConst.cdi;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.tecon.effCalcConst.SystemParamException;
import ru.tecon.effCalcConst.TeconMessage;
import ru.tecon.effCalcConst.ejb.CheckUserSB;
import ru.tecon.effCalcConst.ejb.EffCalcConstSB;
import ru.tecon.effCalcConst.model.Const;
import ru.tecon.effCalcConst.model.ObjProp;
import ru.tecon.effCalcConst.model.ParamHistory;
import ru.tecon.effCalcConst.model.StructTree;


import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
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

    private List<ParamHistory> tableData;
    private String name;
    private int id;
    private boolean write;
    private String ip;
    private String login;

    private TreeNode<StructTree> root;
    private DefaultTreeNode<StructTree> selectedStructNode;
    private List<StructTree> structTreeList = new ArrayList<>();
    private List<ObjProp> propList;
    private ObjProp selectedProp = new ObjProp();

    private String prevFilterWord = "";
    private String filterWord = "";
    private String redirect;
    private String struct;
    private boolean inIframe;
    private boolean structTreeListIsEmpty;
    private Const clearConst;
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

        root = new DefaultTreeNode<>(new StructTree(), null);
        loadStructTree(login, selectedProp.getPropId(), filterWord);
        loadProperty();

        loadData();
        struct = bean.getStruct(545);

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
        constList = bean.getConst(selectedStructNode.getData().getMyId());

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

        if (selectedStructNode == null) {
            selectedStructNode = new DefaultTreeNode<>(structTreeList.get(0), null);
        }

        try {
            bean.updConst(ip, login, String.valueOf(event.getObject().getId()),
                    String.valueOf(event.getObject().getValue()), selectedStructNode.getData().getMyId());
        } catch (SystemParamException e) {
            if (inIframe) {
                new TeconMessage(TeconMessage.SEVERITY_ERROR, "Ошибка сохранения", e.getMessage()).send();
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка сохранения", e.getMessage()));
            }
            PrimeFaces.current().ajax().update("growl");
        }
        loadData();
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable1");
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable2");
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable3");
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable4");
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable5");
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable6");

    }


    /**
     * Метод для загрузки истории изменения параметра
     */
    public void loadHistData(String parName, int constId) {
        id = constId;
        name = parName;

        if (selectedStructNode == null) {
            selectedStructNode = new DefaultTreeNode<>(structTreeList.get(0), null);
        }
        tableData = bean.getParamHistory(constId, selectedStructNode.getData().getMyId());

    }

    /**
     * Метод для загрузки дерева объектов
     */
    private void loadStructTree(String user, int propId, String filterWord) {
        if (!structTreeList.isEmpty()) {
            structTreeList.clear();
            root.getChildren().clear();
        }
        structTreeList = bean.getTreeParam(user, propId, filterWord);
        if (structTreeList.isEmpty()) {
            structTreeListIsEmpty = true;
        } else {
            structTreeListIsEmpty = false;
        }
        Map<String, TreeNode<StructTree>> nodes = new HashMap<>();
        nodes.put(null, root);

        if (!structTreeList.isEmpty()) {
            for (StructTree structNode : structTreeList) {
                if (structNode.getParent().equals("S")) {
                    structNode.setParent(null);
                    TreeNode<StructTree> parent = nodes.get(structNode.getParent());
                    DefaultTreeNode<StructTree> treeNode = new DefaultTreeNode<>(structNode, parent);
                    treeNode.setExpanded(true);
                    nodes.put(structNode.getId(), treeNode);
                } else {
                    TreeNode<StructTree> parent = nodes.get(structNode.getParent());
                    DefaultTreeNode<StructTree> treeNode = new DefaultTreeNode<>(structNode, parent);
                    if (!Objects.equals(filterWord, "")) {
                        treeNode.setExpanded(true);
                    }
                    nodes.put(structNode.getId(), treeNode);
                }
            }

            if (selectedStructNode == null) {
                selectedStructNode = new DefaultTreeNode<>(structTreeList.get(0), null);
            }
        }
    }

    /**
     * Метод логирует выбор свойства объекта
     */
    public void selectProp() {
        logger.log(Level.INFO, "selected property for filter - " + selectedProp.getPropName());
    }

    /**
     * Метод для выбора объекта в дереве и обновления таблиц в зависимости от этого выбора
     */
    public void selectNode() {
        loadData();
        struct = bean.getStruct(selectedStructNode.getData().getMyId());
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable1");
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable2");
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable3");
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable4");
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable5");
        PrimeFaces.current().ajax().update("effCalcConstForm:constTable6");
        PrimeFaces.current().ajax().update("effCalcConstForm:struct");

    }

    /**
     * Метод для фильтрации дерева объектов
     */
    public void filtering() {

        if (!prevFilterWord.equals(filterWord)) {
            loadStructTree(login, selectedProp.getPropId(), filterWord);
            PrimeFaces.current().ajax().update("effCalcConstForm:treeTableId");
            prevFilterWord = filterWord;
            PrimeFaces.current().ajax().update("effCalcConstForm");
        }
    }

    /**
     * Метод для загрузки отчета при нажатии на кнопку отчет
     */
    public void createReport() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
        if (selectedStructNode == null) {
            selectedStructNode = new DefaultTreeNode<>(structTreeList.get(0), null);
        }
        if (selectedStructNode.getData().getMyIcon().equals("fa fa-cubes cubesIcon")) {
            redirect = url + "loadReport?id=0&objId=" + selectedStructNode.getData().getMyId() + "&repType=0" + "&amp;";
        } else {
            redirect = url + "loadReport?id=0&objId=" + selectedStructNode.getData().getMyId() + "&repType=1" + "&amp;";
        }
        if (inIframe) {
            PrimeFaces.current().executeScript("window.parent.postMessage({fileUrl: '" + redirect + "'}, '*');");
        } else {
            PrimeFaces.current().executeScript("window.open('" + redirect + "', '_blank').focus();");
        }
        FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    /**
     * Метод для загрузки отчета при нажатии на кнопку отчет
     */
    public void createReportParam() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
        if (selectedStructNode == null) {
            selectedStructNode = new DefaultTreeNode<>(structTreeList.get(0), null);
        }
        if (selectedStructNode.getData().getMyIcon().equals("fa fa-cubes cubesIcon")) {
            redirect = url + "loadReport?id=" + id + "&objId=" + selectedStructNode.getData().getMyId() + "&repType=2" + "&amp;";
        } else {
            redirect = url + "loadReport?id=" + id + "&objId=" + selectedStructNode.getData().getMyId() + "&repType=3" + "&amp;";
        }
        if (inIframe) {
            PrimeFaces.current().executeScript("window.parent.postMessage({fileUrl: '" + redirect + "'}, '*');");
        } else {
            PrimeFaces.current().executeScript("window.open('" + redirect + "', '_blank').focus();");

        }
    }

    public void fullClear(String parName, int constId) {
        id = constId;
        name = parName;
        List <Const> constList;
        if (selectedStructNode == null) {
            selectedStructNode = new DefaultTreeNode<>(structTreeList.get(0), null);
        }
        constList = bean.getConst(selectedStructNode.getData().getMyId());
        for (Const temp:constList) {
            if (temp.getId() == constId) {
                clearConst = temp;
            }
        }
    }

    public void confirmClear() throws SystemParamException {
        bean.clearConst(ip, login, String.valueOf(clearConst.getId()), selectedStructNode.getData().getMyId());
    }

    /**
     * Метод для проверки, находится ли страница во фрейме
     */
    public void changeInIframe() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        inIframe = Boolean.parseBoolean(params.get("inIframe"));
    }

    private void loadProperty() {
        propList = bean.getProp();
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
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

    public TreeNode<StructTree> getRoot() {
        return root;
    }

    public void setRoot(TreeNode<StructTree> root) {
        this.root = root;
    }

    public DefaultTreeNode<StructTree> getSelectedStructNode() {
        return selectedStructNode;
    }

    public void setSelectedStructNode(DefaultTreeNode<StructTree> selectedStructNode) {
        this.selectedStructNode = selectedStructNode;
    }

    public List<StructTree> getStructTreeList() {
        return structTreeList;
    }

    public void setStructTreeList(List<StructTree> structTreeList) {
        this.structTreeList = structTreeList;
    }

    public List<ObjProp> getPropList() {
        return propList;
    }

    public void setPropList(List<ObjProp> propList) {
        this.propList = propList;
    }

    public ObjProp getSelectedProp() {
        return selectedProp;
    }

    public void setSelectedProp(ObjProp selectedProp) {
        this.selectedProp = selectedProp;
    }

    public String getFilterWord() {
        return filterWord;
    }

    public void setFilterWord(String filterWord) {
        this.filterWord = filterWord;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getStruct() {
        return struct;
    }

    public void setStruct(String struct) {
        this.struct = struct;
    }

    public boolean isStructTreeListIsEmpty() {
        return structTreeListIsEmpty;
    }

    public void setStructTreeListIsEmpty(boolean structTreeListIsEmpty) {
        this.structTreeListIsEmpty = structTreeListIsEmpty;
    }

    public Const getClearConst() {
        return clearConst;
    }

    public void setClearConst(Const clearConst) {
        this.clearConst = clearConst;
    }
}
