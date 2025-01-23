package ru.tecon.effCalcConst.ejb;

import jakarta.annotation.Resource;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import org.postgresql.util.PSQLException;
import ru.tecon.effCalcConst.SystemParamException;
import ru.tecon.effCalcConst.model.Const;
import ru.tecon.effCalcConst.model.ObjProp;
import ru.tecon.effCalcConst.model.ParamHistory;
import ru.tecon.effCalcConst.model.StructTree;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class EffCalcConstSB {

    private static final Logger logger = Logger.getLogger(EffCalcConstSB.class.getName());

    private static final String GET_CONS_DATA = "select * from eff_calc_001.const_view(?)";
    private static final String UPDATE_CONST = "call eff_calc_001.update_constant_p (?,?,?,?,?)";
    private static final String GET_IP = "select eff_calc_001.get_active_session_ip(?)";
    private static final String HISTORY = "SELECT * FROM eff_calc_001.const_history_f(?,?)";
    private static final String SELECT_STRUCT_TREE = "select * from eff_calc.filter_view(?,?,?)";

    private static final String SELECT_OBJ_TYPE_PROP = "select * from dsp_0032t.get_obj_type_props(1)";
    private static final String GET_OBJ_NAME = "select * from eff_calc.get_name_from_id(?)";
    private static final String GET_STRUCT = "select * from eff_calc.text_struct_no_user(?)";
    private static final String GET_CONST_NAME = "select * from eff_calc_001.get_name_from_const_id(?)";
    private static final String CLEAR_CHILDREN_CONST = "call eff_calc_001.reset_constant(?,?,?,?)";

    @Resource(name = "jdbc/DataSource")
    private DataSource dsRw;

    @Resource(name = "jdbc/DataSourceR")
    private DataSource dsR;

    /**
     * Получение данных для формы Справочник нормативных значений для расчета эффекта от применения алгоритмов
     */
    public List<Const> getConst (int id) {
        List<Const> result = new ArrayList<>();
        try (Connection connect = dsR.getConnection();
             PreparedStatement stm = connect.prepareStatement(GET_CONS_DATA)) {
            stm.setInt(1, id);
            ResultSet res = stm.executeQuery();
            int i = 1;
            while (res.next()) {
                Const temp = new Const(i, res.getInt("const_id"), res.getString("name_const"),
                        res.getString("short_name_const"), res.getString("measure"),
                        res.getString("value"), res.getString("last_edit_by"),
                        res.getString("const_group_name"), res.getInt("const_group_id"),
                        res.getString("private"));
                result.add(temp);
                i++;
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load const data");
        }
        return result;
    }

    /**
     * Метод для обновления значения константы
     * @param ip_addr - ip
     * @param user_id - id юзера
     * @param const_id - id константы
     * @param new_value - значение
     */
    public void updConst (String ip_addr, String user_id, String const_id, String new_value, int obj_id) throws SystemParamException {
        try (Connection connect = dsRw.getConnection();
             PreparedStatement stm = connect.prepareStatement(UPDATE_CONST)) {
            stm.setString(1, ip_addr);
            stm.setString(2, user_id);
            stm.setString(3, const_id);
            stm.setString(4, new_value);
            stm.setInt(5, obj_id);
            stm.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "saving error Const", e);
            if (e.getSQLState().equals("11111")) {
                if (e instanceof PSQLException) {
                    PSQLException exception = (PSQLException)e;
                    String ex = exception.getServerErrorMessage().getMessage();
                    throw new SystemParamException(ex);
                }
            } else throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получение ip
     * @param sessionID - id сессии
     * @return ip в строковом представлении
     */
    public String getIP (String sessionID) {
        try (Connection connection = dsR.getConnection();
             PreparedStatement stm = connection.prepareStatement(GET_IP)) {
            stm.setString(1, sessionID);

            ResultSet res = stm.executeQuery();
            if (res.next() && (res.getString(1) != null)) {
                return res.getString(1);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "get IP error", e);
        }
        return null;
    }

    /**
     * Получение списка изменений показателей
     * @param constId - id показателя
     * @return ip в строковом представлении
     */
    public List<ParamHistory> getParamHistory(int constId, int obj_id) {
        List<ParamHistory> result = new ArrayList<>();
        try (Connection connect = dsR.getConnection();
             PreparedStatement stm = connect.prepareStatement(HISTORY)) {
            stm.setString(1, String.valueOf(constId));
            stm.setString(2, String.valueOf(obj_id));

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                LocalDateTime dateLDT = res.getTimestamp("date_time").toLocalDateTime();
                String date = dateLDT.format(dtf);
                ParamHistory paramHistory = new ParamHistory(date, res.getString("new_value"),
                        res.getString("user_name"), res.getString("const_name"), res.getString("old_value"));

                result.add(paramHistory);
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "error getting param history");
        }
        return result;
    }

    /**
     * @return дерево для формы
     */
    public List<StructTree> getTreeParam(String user, int filterId, String filter) {
        List<StructTree> result = new ArrayList<>();
        try (Connection connect = dsR.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_STRUCT_TREE)) {
            stm.setString(1, user);
            stm.setInt(2, filterId);
            stm.setString(3, filter);
            ResultSet res = stm.executeQuery();

            while (res.next()) {
                StructTree structTree = new StructTree(res.getString("id"),
                        res.getString("name"),
                        res.getString("parent"),
                        res.getInt("my_id"),
                        res.getString("my_type"),
                        res.getString("my_icon"));

                if (structTree.getMyIcon().equals("L")) {
                    structTree.setMyIcon("fa fa-link fa-rotate-90 linkIcon");
                } else {
                    structTree.setMyIcon("fa fa-cubes cubesIcon");
                }
                result.add(structTree);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * @return список свойств объета
     */
    public List<ObjProp> getProp() {
        List<ObjProp> result = new ArrayList<>();
        try (Connection connect = dsR.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_OBJ_TYPE_PROP)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {

                ObjProp objProp = new ObjProp(res.getInt("obj_prop_id"),
                        res.getString("obj_prop_name"));
                result.add(objProp);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * @return имя объекта
     */
    public String getObjName(int id) {
        String result = "";
        try (Connection connect = dsR.getConnection();
             PreparedStatement stm = connect.prepareStatement(GET_OBJ_NAME)) {
            stm.setInt(1, id);
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result = res.getString("name");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * @return структуру объекта
     */
    public String getStruct(int id) {
        String result = "";
        try (Connection connect = dsR.getConnection();
             PreparedStatement stm = connect.prepareStatement(GET_STRUCT)) {
            stm.setInt(1, id);
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result = res.getString("text_struct_no_user");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * @param id - id константы
     * @return имя константы
     */
    public String getConstName(int id) {
        String result = "";
        try (Connection connect = dsR.getConnection();
             PreparedStatement stm = connect.prepareStatement(GET_CONST_NAME)) {
            stm.setInt(1, id);
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result = res.getString("name");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Метод для сброса значения константы
     * @param ip_addr - ip
     * @param user_id - id юзера
     * @param const_id - id константы
     */
    public void clearConst (String ip_addr, String user_id, String const_id, int obj_id) throws SystemParamException {
        try (Connection connect = dsRw.getConnection();
             PreparedStatement stm = connect.prepareStatement(CLEAR_CHILDREN_CONST)) {
            stm.setString(1, ip_addr);
            stm.setString(2, user_id);
            stm.setString(3, const_id);
            stm.setInt(4, obj_id);
            stm.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "clear Const error", e);
            if (e.getSQLState().equals("11111")) {
                if (e instanceof PSQLException) {
                    PSQLException exception = (PSQLException)e;
                    String ex = exception.getServerErrorMessage().getMessage();
                    throw new SystemParamException(ex);
                }
            } else throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
