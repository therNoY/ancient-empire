package pers.mihao.ancient_empire.common.jdbc.mongo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import pers.mihao.ancient_empire.common.annotation.Util;
import pers.mihao.ancient_empire.common.jdbc.PageModel;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\9\20 0020 10:24
 */
@Util
public class MongoUtil {

    private static MongoUtil instance;

    private static MongodbConverter converter;

    @PostConstruct
    public void init() {
        instance = this;
        instance.mongoTemplate = this.mongoTemplate;
        converter = new MongodbConverter();
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 批量保存
     *
     * @param tList 实体名称
     */
    public static <T> Integer saveAll(List<T> tList) {
        for (T obj : tList) {
            instance.mongoTemplate.save(obj);
        }
        return tList.size();
    }

    /**
     * 保存单个对象到对象映射的实例类上
     *
     * @param objectToSave 实体名称
     */
    public static <T> T save(T objectToSave) {
        return instance.mongoTemplate.save(objectToSave);
    }

    /**
     * 保存单个对象到指定集合中
     *
     * @param collName     集合名称
     * @param objectToSave 实体名称
     */
    public static <T> T save(T objectToSave, String collName) {
        return instance.mongoTemplate.save(objectToSave);
    }


    /**
     * 指定ID查询
     * @param id
     * @param entityClass
     * @param <T>
     * @return
     */
    public static <T> T findById(Object id, Class<T> entityClass) {
        return instance.mongoTemplate.findById(id, entityClass);
    }

    /**
     * 指定ID查询
     * @param criteria
     * @param returnClass
     * @param <T>
     * @return
     */
    public static <T> List<T> findByCriteria(Criteria criteria, Class<T> returnClass) {
        Query query = new Query(criteria);
        List<String> fields = converter.getQueryFieldByClass(returnClass);
        for (String field : fields) {
            query.fields().include(field);
        }
        return instance.mongoTemplate.find(query, returnClass);
    }

    /**
     * 查询返回指定字段
     *
     * @param fields   需要返回的指定字段
     * @param clazz    数据实体类class
     * @param collName 集合名称
     * @param map      Map<查询条件key,查询条件value>
     *                 <p>
     *                 返回字段的时候id默认为返回，不返回id则field设置  fieldObj.put("id",false)
     * @return
     */
    public static Object findDesignField(List<String> fields, Map<String, Object> map, Class<?> clazz, String collName, boolean returnId) {
        Criteria criteria = null;
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件不能为null");
        }
        for (String key : map.keySet()) {
            criteria = Criteria.where(key).is(map.get(key));
        }
        Query query = new Query(criteria);
        for (String field : fields) {
            query.fields().include(field);
        }
        if (!returnId) {
            query.fields().exclude("id");
        }
        return instance.mongoTemplate.find(query, clazz, collName);
    }


    /**
     * 模糊查询 根据 key 可以到 collName 中进行模糊查询 并排序
     *
     * @param param     匹配的参数
     * @param collName  集合名称
     * @param direction Direction.desc /asc 倒序/正序
     * @param sortField 排序字段
     * @return
     */
    public static Object findLikeByParam(String param, Class type, String collName, String sortField, Sort.Direction direction) {
        Pattern pattern = Pattern.compile("^.*" + param + ".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query(Criteria.where("name").regex(pattern)).with(Sort.by(direction, sortField));
        return instance.mongoTemplate.find(query, type, collName);
    }

    /**
     * 向指定集合设置索引
     *
     * @param collName  集合名称
     * @param indexName 索引名称
     * @param map       map.put("添加索引的字段",Direction.ASC/DESC)
     */
    public static void createIndex(String collName, String indexName, Map<String, Sort.Direction> map) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "添加索引的字段不能为null");
        }
        Index index = new Index().named(indexName);
        for (String key : map.keySet()) {
            index.on(key, map.get(key));
        }
        instance.mongoTemplate.indexOps(collName).ensureIndex(index);
    }

    /**
     * 获取指定集合中的索引信息
     *
     * @param collName 集合名称
     * @return
     */
    public static Object getIndexInfo(String collName) {
        return instance.mongoTemplate.indexOps(collName).getIndexInfo();
    }


    /**
     * 根据索引名称删除索引
     *
     * @param indexName 索引名称
     * @param collName  集合名称
     */
    public static void removeIndexByName(String collName, String indexName) {
        instance.mongoTemplate.indexOps(collName).dropIndex(indexName);
    }


    /**
     * 删除指定集合中得所有索引
     *
     * @param collName 集合名称
     */
    public static void removeIndexByName(String collName) {
        instance.mongoTemplate.indexOps(collName).dropAllIndexes();
    }

    /**
     * 根据指定key 和value到指定collName集合中删除数据
     *
     * @param key
     * @param value
     * @param collName
     */
    public static void removeAllByParam(String key, String value, String collName) {
        Criteria criteria = Criteria.where(key).is(value);
        Query query = Query.query(criteria);
        instance.mongoTemplate.remove(query, collName);
    }


    /**
     * 根据指定条件查询 并排序
     *
     * @param obj      数据对象
     * @param map      Map<"查询条件key"，查询条件值> map
     * @param collName 集合名称
     * @return
     */
    public static List<? extends Object> findSortByParam(Object obj, String collName, Map<String, Object> map, String sortField, Sort.Direction direction) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        Criteria criteria = null;
        criteria = getCriteria(criteria, map);
        if (criteria == null) {
            return new ArrayList<>();
        }
        Query query = Query.query(criteria).with(Sort.by(direction, sortField));
        return instance.mongoTemplate.find(query, obj.getClass(), collName);
    }


    /**
     * 范围查询
     * <p>
     * 查询大于等于begin  小于等于end范围内条件匹配得数据并排序
     *
     * @param obj           数据对象
     * @param collName      集合名称
     * @param map           Map<"查询条件key"，查询条件值> map
     * @param sortField     排序字段
     * @param direction     排序方式  Direction.asc   / Direction.desc
     * @param rangeCriteria 示例： lt小于  lte 小于等于  gt大于  gte大于等于 eq等于 ne不等于
     *                      <p>
     *                      Criteria rangeCriteria=Criteria.where("createDate").gte(begin).lte(end));
     *                      <p>
     *                      createDate:数据库中的时间字段，gegin:起始时间  end:结束时间
     * @return
     */
    public static List<? extends Object> findRangeByParam(Object obj, String collName, Map<String, Object> map,
                                                          String sortField, Sort.Direction direction, Criteria rangeCriteria) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        Criteria criteria = null;
        criteria = getCriteria(criteria, map);
        if (criteria == null) {
            return new ArrayList<>();
        }
        Query query = new Query().addCriteria(rangeCriteria).addCriteria(criteria).with(Sort.by(direction, sortField));
        return instance.mongoTemplate.find(query, obj.getClass(), collName);
    }


    /**
     * 根据指定key value到指定集合中查询匹配得数量
     *
     * @param collName
     * @param key
     * @param value
     * @return
     */
    public static long count(String collName, String key, String value) {
        Query query = Query.query(Criteria.where(key).is(value));
        return instance.mongoTemplate.count(query, "goods");
    }


    /**
     * 在指定范围内查询匹配条件的数量
     *
     * @param clazz         数据实体类
     * @param collName      集合名称
     * @param map           查询条件map
     * @param rangeCriteria 范围条件  Criteria rangeCriteria= Criteria.where("数据库字段").gt/gte（起始范围）.lt/lte（结束范围）
     * @return
     */
    public static Long countRangeCondition(Class<?> clazz, String collName, Criteria rangeCriteria, Map<String, Object> map) {
        Criteria criteria = null;
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        for (String key : map.keySet()) {
            criteria = Criteria.where(key).is(map.get(key));
        }
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        query.addCriteria(rangeCriteria);
        return instance.mongoTemplate.count(query, clazz, collName);
    }


    /**
     * 指定集合 根据条件查询出符合的第一条数据
     *
     * @param clazz    数据对象
     * @param map      条件map  Map<条件key,条件value> map
     * @param collName 集合名
     * @return
     */
    public static Object findSortFirst(Class<?> clazz, Map<String, Object> map, String collName, String field, Sort.Direction direction) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        Criteria criteria = null;
        criteria = getCriteria(criteria, map);
        Query query = Query.query(criteria).with(Sort.by(direction, field));
        return instance.mongoTemplate.findOne(query, clazz, collName);
    }

    /**
     * 指定集合 修改数据，且修改所找到的所有数据
     *
     * @param accordingKey   修改条件 key
     * @param accordingValue 修改条件 value
     * @param map            Map<修改内容 key数组,修改内容 value数组>
     * @param collName       集合名
     * @param type           修改操作类型  1:修改第一条数据  0：修改所有匹配得数据
     */
    public static void updateMulti(String accordingKey, Object accordingValue, Map<String, Object> map,
                                   String collName, Integer type) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Query query = Query.query(criteria);
        Update update = new Update();

        for (String key : map.keySet()) {
            update.set(key, map.get(key));
        }
        if (type == 1) {
            instance.mongoTemplate.updateFirst(query, update, collName);
        } else {
            instance.mongoTemplate.updateMulti(query, update, collName);
        }
    }

    /**
     * 对某字段做sum求和
     *
     * @param clazz         数据实体类
     * @param map           Map<查询条件key,查询条件value> map
     * @param collName      集合名称
     * @param sumField      求和字段
     * @param rangeCriteria 范围条件
     * @return Criteria rangeCriteria = Criteria.where(字段).gt(起始范围).lt(结束范围)
     */
    public static Object findSum(Class<?> clazz, Map<String, Object> map, String collName, String sumField, Criteria rangeCriteria) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        Criteria criteria = null;
        MatchOperation match = null;
        for (String key : map.keySet()) {
            criteria = Criteria.where(key).is(map.get(key));
        }
        if (criteria != null) {
            match = Aggregation.match(criteria);
        }
        GroupOperation count = Aggregation.group().sum(sumField).as(sumField);
        return instance.mongoTemplate.aggregate(Aggregation.newAggregation(match, count), collName, clazz).getMappedResults();
    }

    /**
     * 分页查询
     *
     * @param clazz     数据实体类
     * @param collName  集合名称
     * @param map       Map<"查询条件key"，查询条件值> map 若 keys/values 为null,则查询集合中所有数据
     * @param pageNo    当前页
     * @param pageSize  当前页数据条数
     * @param direction Direction.Desc/ASC 排序方式
     * @param sortField 排序字段
     * @return
     */
    public static PageModel findSortPageCondition(Class<?> clazz, String collName, Map<String, Object> map,
                                                  int pageNo, int pageSize, Sort.Direction direction, String sortField) {

        Criteria criteria = getCriteria(new Criteria(), map);

        long count;

        if (criteria == null) {
            count = instance.mongoTemplate.count(new Query(), clazz, collName);
        } else {
            count = instance.mongoTemplate.count(new Query(criteria), clazz, collName);
        }
        int pages = (int) Math.ceil((double) count / (double) pageSize);
        if (pageNo <= 0 || pageNo > pages) {
            pageNo = 1;
        }
        int skip = pageSize * (pageNo - 1);
        Query query = new Query().skip(skip).limit(pageSize);
        query.with(Sort.by(direction, sortField));
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        List<?> list = instance.mongoTemplate.find(query, clazz, collName);
        PageModel pageModel = new PageModel();
        pageModel.setPageNow(pageNo);
        pageModel.setPageSize(pageSize);
        pageModel.setCount(count);
        pageModel.setResult(list);
        return pageModel;
    }

    private static void saveData(String collName, Object entry) {
        if (StringUtils.isEmpty(collName)) {
            instance.mongoTemplate.save(entry);
        } else {
            instance.mongoTemplate.save(entry, collName);
        }
    }

    private static Criteria getCriteria(Criteria criteria, Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        int i = 0;
        for (String key : map.keySet()) {
            if (i == 0) {
                criteria = Criteria.where(key).is(map.get(key));
                i++;
            } else {
                criteria.and(key).is(map.get(key));
            }
        }
        return criteria;
    }


}

