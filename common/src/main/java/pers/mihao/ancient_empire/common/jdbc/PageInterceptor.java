package pers.mihao.ancient_empire.common.jdbc;


import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.common.dto.ApiPageDTO;
import pers.mihao.ancient_empire.common.util.ReflectUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * 分页拦截器
 */
@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class PageInterceptor implements Interceptor {

    public static final String AE_COUNT_NUM = "ae_count_num";

    /**
     * 动态注入分页
     * @param invoke
     * @return
     * @throws Throwable
     */
    public Object intercept(Invocation invoke) throws Throwable {
        Object target = invoke.getTarget();
        if (invoke.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) target;
            BaseStatementHandler delegate = (BaseStatementHandler) ReflectUtil.getValueByFieldName(statementHandler, "delegate");
            MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getValueByFieldName(delegate, "mappedStatement");

            if (mappedStatement != null && mappedStatement.getId().endsWith("WithPage")) {
                // 以withPage结尾的自动分页
                // 1.查询总和
                Long count = null;
                BoundSql boundSql = delegate.getBoundSql();
                if (boundSql.getParameterObject() instanceof ApiPageDTO) {
                    ApiPageDTO apiPageDTO = (ApiPageDTO) boundSql.getParameterObject();
                    Connection connection = (Connection)invoke.getArgs()[0];
                    PreparedStatement countStatement = connection.prepareStatement(getCountSql(boundSql.getSql()));
                    setStatementParms(mappedStatement, boundSql, countStatement);
                    ResultSet resultSet = countStatement.executeQuery();
                    if (resultSet.next()) {
                        count = resultSet.getLong(AE_COUNT_NUM);
                        // 查询数据 封装对象直接返回
                        String sql = getMysqlPageSql(apiPageDTO, boundSql.getSql());
                        apiPageDTO.setPageCount(count);
                        ReflectUtil.setValueByFieldName(boundSql, "sql", sql);
                    }

                }
            }
        }
        return invoke.proceed();
    }

    private void setStatementParms(MappedStatement mappedStatement, BoundSql boundSql, PreparedStatement countStatement) throws java.sql.SQLException {
        Configuration configuration = mappedStatement.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        MetaObject metaObject = boundSql.getParameterObject() == null ? null : configuration.newMetaObject(boundSql.getParameterObject());
        for (int i = 0; i < boundSql.getParameterMappings().size(); i++) {
            ParameterMapping parameterMapping = boundSql.getParameterMappings().get(i);
            if (parameterMapping.getMode() != ParameterMode.OUT) {
                Object value;
                String propertyName = parameterMapping.getProperty();
                PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                if (boundSql.getParameterObject() == null) {
                    value = null;
                } else if (typeHandlerRegistry.hasTypeHandler(boundSql.getParameterObject().getClass())) {
                    value = boundSql.getParameterObject();
                } else if (boundSql.hasAdditionalParameter(propertyName)) {
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName())) {
                    value = boundSql.getAdditionalParameter(prop.getName());
                    if (value != null) {
                        value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                    }
                } else {
                    value = metaObject == null ? null : metaObject.getValue(propertyName);
                }
                TypeHandler typeHandler = parameterMapping.getTypeHandler();
                if (typeHandler == null) {
                    throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                }
                typeHandler.setParameter(countStatement, i + 1, value, parameterMapping.getJdbcType());
            }
        }
    }

    private String getCountSql(String sql) {
        StringBuilder sb = new StringBuilder("select count(1) as ");
        sb.append(AE_COUNT_NUM).append(" from (");
        sb.append(sql)
                .append(") ae_count");
        return sb.toString();
    }

    /**
     * 获取分页数据
     * @param page
     * @param sql
     * @return
     */
    private String getMysqlPageSql(ApiPageDTO page, final String sql) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(sql).append(" limit ").append(page.getLimitStart()).append(",").append(page.getLimitCount());
        return sqlBuffer.toString();
    }


    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }


}
