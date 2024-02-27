package ru.tecon.effCalcConst.ejb;

import org.postgresql.util.PSQLException;
import ru.tecon.effCalcConst.SystemParamException;
import ru.tecon.effCalcConst.cdi.EffCalcConstMB;
import ru.tecon.effCalcConst.model.Const;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class EffCalcConstSB {

    private static final Logger logger = Logger.getLogger(EffCalcConstSB.class.getName());

    private static final String GET_CONS_DATA = "select id_const, name_const, short_name_const, short_name as measure,\n" +
            "       value, last_edit_by , name_group as const_group_name,\n" +
            "       id as const_group_id\n" +
            "from eff_calc.eff_constant\n" +
            "         left join admin.sys_measure on measure = measure_id\n" +
            "         left join eff_calc.sp_const_group scg  on const_group = id\n" +
            "order by  const_group_id, id_const";
    private static final String UPDATE_CONST = "call eff_calc_001.update_constant_p (?,?,?,?)";
    private static final String GET_IP = "select eff_calc_001.get_active_session_ip(?)";

//    @Inject
//    Logger logger;

    @Resource(name = "jdbc/DataSourceR")
    private DataSource dsR;

    @Resource(name = "jdbc/DataSource")
    private DataSource dsRW;

    /**
     * Получение данных для формы Справочник нормативных значений для расчета эффекта от применения алгоритмов
     */
    public List<Const> getConst () {
        List<Const> result = new ArrayList<>();
        try (Connection connect = dsR.getConnection();
             PreparedStatement stm = connect.prepareStatement(GET_CONS_DATA)) {
            ResultSet res = stm.executeQuery();
            int i = 1;
            while (res.next()) {
                Const temp = new Const(i, res.getInt("id_const"), res.getString("name_const"),
                        res.getString("short_name_const"), res.getString("measure"),
                        res.getString("value"), res.getString("last_edit_by"),
                        res.getString("const_group_name"), res.getInt("const_group_id"));
                result.add(temp);
                i++;
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load analog data");
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
    public void updConst (String ip_addr, String user_id, String const_id, String new_value) throws SystemParamException {
        try (Connection connect = dsRW.getConnection();
             PreparedStatement stm = connect.prepareStatement(UPDATE_CONST)) {
            stm.setString(1, ip_addr);
            stm.setString(2, user_id);
            stm.setString(3, const_id);
            stm.setString(4, new_value);
            System.out.println("Что передаем " + ip_addr + " " + user_id + " " + const_id + " " + new_value);
            stm.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "saving error Enum Param", e);
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
}
