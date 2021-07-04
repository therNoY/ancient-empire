package pers.mihao.ancient_empire.common.log.invoke;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import pers.mihao.ancient_empire.common.log.AbstractLog;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.StringUtil;

/**
 * 持久化数据到表中
 *
 * @Author mihao
 * @Date 2021/6/30 13:40
 */
public class PersistentLogToDataBaseInvoke extends AbstractPersistentLogInvoke {


    /**
     * 返回task
     * @param paramMap
     * @param log
     * @return
     */
    @Override
    protected AbstractPersistentLogTask getPersistentLogTask() {
        return new PersistentLogTask();
    }

    /**
     * 获取sql
     *
     * @param paramMap
     * @param log
     * @return
     */
    private String getSqlStatement(Map<String, String> paramMap, AbstractLog log) {
        StringBuilder statement = new StringBuilder();
        statement.append("insert into ");
        if (StringUtil.isNotBlack(log.getDataSource())) {
            statement.append(log.getDataSource()).append(".");
        }
        statement.append(log.getTableName()).append("(");
        String field;
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            field = log.isUnderscore() ? StringUtil.humpToLowerUnderscore(entry.getKey()) : entry.getKey();
            statement.append(field).append(",");
        }
        // 去掉最后一个逗号
        statement.setLength(statement.length() - 1);
        statement.append(")").append(" values (");
        for (int i = 0; i < paramMap.size(); i++) {
            statement.append("?").append(",");
        }
        // 去掉最后一个逗号
        statement.setLength(statement.length() - 1);
        statement.append(")");
        return statement.toString();
    }

    /**
     * 持久化日志任务
     */
    class PersistentLogTask extends AbstractPersistentLogTask {

        @Override
        public void doTask(Map<String, String> params) {
            String statement = getSqlStatement(params, log);
            logger.info("记录日志开始 表{}， sql:{}, 参数:{}", log.getTableName(), statement, params);
            DataSource dataSource = ApplicationContextHolder.getBean(DataSource.class);
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                connection = dataSource.getConnection();
                preparedStatement = connection.prepareStatement(statement);
                String value;
                int index = 0;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    value = entry.getValue();
                    preparedStatement.setString(++index, value);
                }
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (preparedStatement != null && !preparedStatement.isClosed()) {
                        preparedStatement.close();
                    }
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
    }
}
