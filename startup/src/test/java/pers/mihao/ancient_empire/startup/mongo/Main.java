package pers.mihao.ancient_empire.startup.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.WriteConcern;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.DeleteResult;



/**
 * @version 1.0
 * @author mihao
 * @date 2020\9\20 0020 9:44
 */
public class Main {

    private static MongoClient mongoClient;

    static {
        System.out.println("===============MongoDBUtil初始化========================");
        String ip = "192.168.1.75";
        int port =27017;
        mongoClient = new MongoClient(ip, port);
        // 大部分用户使用mongodb都在安全内网下，但如果将mongodb设为安全验证模式，就需要在客户端提供用户名和密码：
        // boolean auth = db.authenticate(myUserName, myPassword);
        Builder options = new MongoClientOptions.Builder();
        options.cursorFinalizerEnabled(true);
        // options.autoConnectRetry(true);// 自动重连true
        // options.maxAutoConnectRetryTime(10); // the maximum auto connect retry time
        options.connectionsPerHost(300);// 连接池设置为300个连接,默认为100
        options.connectTimeout(30000);// 连接超时，推荐>3000毫秒
        options.maxWaitTime(5000); //
        options.socketTimeout(0);// 套接字超时时间，0无限制
        options.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.writeConcern(WriteConcern.SAFE);//
        options.build();
    }

    // ------------------------------------共用方法---------------------------------------------------
    /**
     * 获取DB实例 - 指定DB
     *
     * @param dbName
     * @return
     */
    public static MongoDatabase getDB(String dbName) {
        if (dbName != null && !"".equals(dbName)) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            return database;
        }
        return null;
    }

    /**
     * 获取collection对象 - 指定Collection
     *
     * @param collName
     * @return
     */
    public static MongoCollection<Document> getCollection(String dbName, String collName) {
        if (null == collName || "".equals(collName)) {
            return null;
        }
        if (null == dbName || "".equals(dbName)) {
            return null;
        }
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collName);
        return collection;
    }

    /**
     * 查询DB下的所有表名
     */
    public static List<String> getAllCollections(String dbName) {
        MongoIterable<String> colls = getDB(dbName).listCollectionNames();
        List<String> _list = new ArrayList<String>();
        for (String s : colls) {
            _list.add(s);
        }
        return _list;
    }

    /**
     * 获取所有数据库名称列表
     *
     * @return
     */
    public static MongoIterable<String> getAllDBNames() {
        MongoIterable<String> s = mongoClient.listDatabaseNames();
        return s;
    }

    /**
     * 删除一个数据库
     */
    public static void dropDB(String dbName) {
        getDB(dbName).drop();
    }

    /**
     * 查找对象 - 根据主键_id
     *
     * @param collection
     * @param id
     * @return
     */
    public static Document findById(MongoCollection<Document> coll, String id) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Document myDoc = coll.find(Filters.eq("_id", _idobj)).first();
        return myDoc;
    }

    /** 统计数 */
    public static int getCount(MongoCollection<Document> coll) {
        int count = (int) coll.count();
        return count;
    }

    /** 条件查询 */
    public MongoCursor<Document> find(MongoCollection<Document> coll, Bson filter) {
        return coll.find(filter).iterator();
    }

    /** 分页查询 */
    public static MongoCursor<Document> findByPage(MongoCollection<Document> coll, Bson filter, int pageNo, int pageSize) {
        Bson orderBy = new BasicDBObject("_id", 1);
        return coll.find(filter).sort(orderBy).skip((pageNo - 1) * pageSize).limit(pageSize).iterator();
    }


    /**
     * 通过ID删除
     *
     * @param coll
     * @param id
     * @return
     */
    public static int deleteById(MongoCollection<Document> coll, String id) {
        int count = 0;
        ObjectId _id = null;
        try {
            _id = new ObjectId(id);
        } catch (Exception e) {
            return 0;
        }
        Bson filter = Filters.eq("_id", _id);
        DeleteResult deleteResult = coll.deleteOne(filter);
        count = (int) deleteResult.getDeletedCount();
        return count;
    }

    /**
     * FIXME
     *
     * @param coll
     * @param id
     * @param newdoc
     * @return
     */
    public static Document updateById(MongoCollection<Document> coll, String id, Document newdoc) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Bson filter = Filters.eq("_id", _idobj);
        // coll.replaceOne(filter, newdoc); // 完全替代
        coll.updateOne(filter, new Document("$set", newdoc));
        return newdoc;
    }

    public static void dropCollection(String dbName, String collName) {
        getDB(dbName).getCollection(collName).drop();
    }

    /**
     * 关闭Mongodb
     */
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }

    /**
     * 测试入口
     *
     * @param args
     * @throws CloneNotSupportedException
     */
    public static void main(String[] args) {

        String dbName = "test";
        String collName = "wd_paper_scie";
        MongoCollection<Document> coll = Main.getCollection(dbName, collName);
        //coll.createIndex(new Document("validata",1));//创建索引
        //coll.createIndex(new Document("id",1));
        // coll.createIndex(new Document("ut_wos",1),new IndexOptions().unique(true));//创建唯一索引
        //coll.dropIndexes();//删除索引
        //coll.dropIndex("validata_1");//根据索引名删除某个索引
        ListIndexesIterable<Document> list = coll.listIndexes();//查询所有索引
        for (Document document : list) {
            System.out.println(document.toJson());
        }
        coll.find(Filters.and(Filters.eq("x", 1), Filters.lt("y", 3)));
        coll.find(and(eq("x", 1), lt("y", 3)));
        coll.find().sort(ascending("title"));
        coll.find().sort(new Document("id",1));
        coll.find(new Document("$or", Arrays.asList(new Document("owner", "tom"), new Document("words", new Document("$gt", 350)))));
        coll.find().projection(fields(include("title", "owner"), excludeId()));
        // coll.updateMany(Filters.eq("validata", 1), Updates.set("validata", 0));
        //coll.updateMany(Filters.eq("validata", 1), new Document("$unset", new Document("jigou", "")));//删除某个字段
        //coll.updateMany(Filters.eq("validata", 1), new Document("$rename", new Document("affiliation", "affiliation_full")));//修改某个字段名
        //coll.updateMany(Filters.eq("validata", 1), new Document("$rename", new Document("affiliationMeta", "affiliation")));
        //coll.updateMany(Filters.eq("validata", 1), new Document("$set", new Document("validata", 0)));//修改字段值
//        MongoCursor<Document> cursor1 =coll.find(Filters.eq("ut_wos", "WOS:000382970200003")).iterator();
//        while(cursor1.hasNext()){
//            Document sd=cursor1.next();
//            System.out.println(sd.toJson());
//            coll.insertOne(sd);
//        }

//        MongoCursor<Document> cursor1 =coll.find(Filters.elemMatch("affInfo", Filters.eq("firstorg", 1))).iterator();
//        while(cursor1.hasNext()){
//            Document sd=cursor1.next();
//            System.out.println(sd.toJson());
//        }
        //查询只返回指定字段
        // MongoCursor<Document> doc= coll.find().projection(Projections.fields(Projections.include("ut_wos","affiliation"),Projections.excludeId())).iterator();
        //"ut_wos" : "WOS:000382970200003"
        //coll.updateMany(Filters.eq("validata", 1), new Document("$set", new Document("validata", 0)));
        //coll.updateMany(Filters.eq("validata", 0), new Document("$rename", new Document("sid", "ssid")), new UpdateOptions().upsert(false));
        //coll.updateOne(Filters.eq("ut_wos", "WOS:000382970200003"), new Document("$set", new Document("validata", 0)));
        //long isd=coll.count(Filters.elemMatch("affInfo", Filters.elemMatch("affiliationJGList", Filters.eq("sid", 0))));
        // System.out.println(isd);
        //MongoCursor<Document> doc= coll.find(Filters.elemMatch("affInfo", Filters.elemMatch("affiliationJGList", Filters.ne("sid", 0)))).projection(Projections.fields(Projections.elemMatch("affInfo"),Projections.excludeId())).iterator();
//       MongoCursor<Document> doc= coll.find().projection(Projections.include("affInfo","ssid")).iterator();
//       while(doc.hasNext()){
//            Document sd=doc.next();
//            System.out.println(sd.toJson());
//
//        }
        Main.close();
        // 插入多条
//         for (int i = 1; i <= 4; i++) {
//         Document doc = new Document();
//         doc.put("name", "zhoulf");
//         doc.put("school", "NEFU" + i);
//         Document interests = new Document();
//         interests.put("game", "game" + i);
//         interests.put("ball", "ball" + i);
//         doc.put("interests", interests);
//         coll.insertOne(doc);
//         }
//
       /* MongoCursor<Document> sd =coll.find().iterator();
        while(sd.hasNext()){
            Document doc = sd.next();
            String id =  doc.get("_id").toString();
            List<AffiliationJG> list = new ArrayList<AffiliationJG>();
            AffiliationJG jg = new AffiliationJG();
            jg.setAddress("123");
            jg.setCid(2);
            jg.setCname("eeee");
            jg.setSid(3);
            jg.setSname("rrrr");
            AffiliationJG jg2 = new AffiliationJG();
            jg2.setAddress("3242");
            jg2.setCid(2);
            jg2.setCname("ers");
            jg2.setSid(3);
            jg2.setSname("rasdr");
            list.add(jg);
            list.add(jg2);
            AffiliationList af = new AffiliationList();
            af.setAffiliationAuthos("33333");
            af.setAffiliationinfo("asdsa");
            af.setAffiliationJGList(list);
            JSONObject json = JSONObject.fromObject(af);
            doc.put("affInfo", json);
            MongoDBUtil..updateById(coll, id, doc);
        }*/

//        Bson bs = Filters.eq("name", "zhoulf");
//        coll.find(bs).iterator();
        // // 根据ID查询
        // String id = "556925f34711371df0ddfd4b";
        // Document doc = MongoDBUtil2..findById(coll, id);
        // System.out.println(doc);

        // 查询多个
        // MongoCursor<Document> cursor1 = coll.find(Filters.eq("name", "zhoulf")).iterator();
        // while (cursor1.hasNext()) {
        // org.bson.Document _doc = (Document) cursor1.next();
        // System.out.println(_doc.toString());
        // }
        // cursor1.close();

        // 查询多个
//         MongoCursor<WdPaper> cursor2 = coll.find(WdPaper.class).iterator();
//         while(cursor2.hasNext()){
//             WdPaper doc = cursor2.next();
//             System.out.println(doc.getUt_wos());
//         }
        // 删除数据库
        // MongoDBUtil2..dropDB("testdb");

        // 删除表
        // MongoDBUtil2..dropCollection(dbName, collName);

        // 修改数据
        // String id = "556949504711371c60601b5a";
        // Document newdoc = new Document();
        // newdoc.put("name", "时候");
        // MongoDBUtil..updateById(coll, id, newdoc);

        // 统计表
        //System.out.println(MongoDBUtil..getCount(coll));

        // 查询所有
//        Bson filter = Filters.eq("count", 0);
//        MongoDBUtil..find(coll, filter);

    }

}