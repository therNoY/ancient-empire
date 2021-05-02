package pers.mihao.ancient_empire.common.mybatis_plus_helper;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.common.util.ReflectUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展mybatis-plus 支持联合主键 的serviceImpl
 *
 * @author mihao
 * @version 1.0
 * @date 2021\1\16 0016 14:32
 */
public class ComplexKeyServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements
    ComplexKeyService<T> {

    private static Map<Class<?>, List<FieldInfo>> PRIMARY_KEY_CATCH = new ConcurrentHashMap<>(16);

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean saveOrUpdate(T entity) {
        if (null == entity) {
            return false;
        } else {
            QueryWrapper<T> queryWrapper = getQueryWrapper(entity);
            T obj = super.baseMapper.selectOne(queryWrapper);
            if (obj == null) {
                save(entity);
            } else {
                update(entity, queryWrapper);
            }
            return true;
        }
    }



    @Override
    public boolean updateById(T entity) {
        return saveOrUpdate(entity);
    }

    @Override
    public T selectByPrimaryKey(T t) {
        if (null == t) {
            return null;
        } else {
            QueryWrapper<T> queryWrapper = getQueryWrapper(t);
            T obj = super.baseMapper.selectOne(queryWrapper);
            return obj;
        }
    }



    @Override
    public int deleteByPrimaryKey(T t) {
        return super.baseMapper.delete(getQueryWrapper(t));
    }


    private QueryWrapper<T> getQueryWrapper(T t) {
        List<FieldInfo> primaryKeys = getFieldInfos(t);

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        Object value;
        for (FieldInfo fieldInfo : primaryKeys) {
            value = ReflectUtil.getValueByField(t, fieldInfo.field);
            Assert.notNull(value, "主键不能为空");
            queryWrapper.eq(fieldInfo.dataSourceFieldName, value);
        }
        return queryWrapper;
    }

    private List<FieldInfo> getFieldInfos(T entity) {
        Class<?> cls = entity.getClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!",
            new Object[0]);
        List<FieldInfo> primaryKeys;
        if ((primaryKeys = PRIMARY_KEY_CATCH.get(cls)) == null) {
            primaryKeys = getEntityKey(cls);
            PRIMARY_KEY_CATCH.put(cls, primaryKeys);
        }

        Assert.notEmpty(primaryKeys, "实体类不能没有主键", cls);
        return primaryKeys;
    }

    /**
     * 获取一个类的主键
     *
     * @param cls
     * @return
     */
    private List<FieldInfo> getEntityKey(Class<?> cls) {
        List<FieldInfo> list = new ArrayList<>();
        Field[] fieldArray = cls.getDeclaredFields();
        if (fieldArray != null) {
            TableId tableId;
            FieldInfo fieldInfo;
            for (Field field : fieldArray) {
                tableId = field.getAnnotation(TableId.class);
                if (tableId != null) {
                    fieldInfo = new FieldInfo();
                    if (StringUtil.isBlack(tableId.value())) {
                        fieldInfo.dataSourceFieldName = StringUtil.humpToUnderscore(field.getName());
                    } else {
                        fieldInfo.dataSourceFieldName = tableId.value();
                    }
                    fieldInfo.field = field;
                    list.add(fieldInfo);
                }
            }
        }
        return list;
    }

    static class FieldInfo {

        Field field;
        String dataSourceFieldName;
    }

}
