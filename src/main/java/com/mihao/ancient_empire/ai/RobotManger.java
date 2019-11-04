package com.mihao.ancient_empire.ai;

import com.mihao.ancient_empire.ai.constant.AiActiveEnum;
import com.mihao.ancient_empire.ai.dto.UnitActionResult;
import com.mihao.ancient_empire.ai.dto.ActiveResult;
import com.mihao.ancient_empire.ai.dto.SelectUnitResult;
import com.mihao.ancient_empire.ai.handle.AiActiveHandle;
import com.mihao.ancient_empire.ai.handle.AiMoveHandle;
import com.mihao.ancient_empire.ai.handle.AiSelectUnitHandle;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Site;
import com.mihao.ancient_empire.dto.SiteSquare;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 管理缓存和任务调度
 * static 是共有的 其他属性是根据每个record 生成一个
 */
public class RobotManger {

    static Logger log = LoggerFactory.getLogger(RobotManger.class);

    private static UserRecordService userRecordService;
    private static AiMessageSender aiMessageSender;
    private static Map<String, RobotManger> selfManger = new HashMap<>();
    private static ThreadPoolExecutor executor;
    private static Map<String, SelectUnitResult> selectUnitResultMap = null;
    private static Map<String, UserRecord> recordMap;


    private UserRecord record;
    /* 维持一个保存有map 和map 对应状态的类 */
    private AiMoveHandle aiMoveHandle; /*每一个record 生成一个*/
    private AiSelectUnitHandle aiSelectUnitHandle; /*每一个record 生成一个*/

    private List<Site> dangerCastle = new ArrayList<>(); // 有危险的城堡
    private List<Site> dangerVillage = new ArrayList<>(); // 有危险的村庄
    List<SiteSquare> castleSite = new ArrayList<>();
    List<SiteSquare> villageSite = new ArrayList<>();

    static {
        executor = new ThreadPoolExecutor(3,
                10,
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(9),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        selectUnitResultMap = new HashMap<>();
        recordMap = new HashMap<>();
        aiMessageSender = ApplicationContextHolder.getBean(AiMessageSender.class);
        userRecordService = ApplicationContextHolder.getBean(UserRecordService.class);
    }

    private RobotManger(UserRecord record) {
        this.record = record;
        for (int i = 0; i < record.getInitMap().getRegions().size(); i++) {
            BaseSquare square = record.getInitMap().getRegions().get(i);
            if (square.getType().equals(RegionEnum.CASTLE.type())) {
                castleSite.add(new SiteSquare(square, AppUtil.getSiteByMapIndex(i, record.getInitMap().getColumn())));
            } else if (square.getType().equals(RegionEnum.TOWN.type())) {
                villageSite.add(new SiteSquare(square, AppUtil.getSiteByMapIndex(i, record.getInitMap().getColumn())));
            }
        }
        aiMoveHandle = new AiMoveHandle();
        aiSelectUnitHandle = new AiSelectUnitHandle();
        log.error("RobotManger 创建一次》》》》》》》》》》》》》》》》》》");
    }

//    public static RobotManger getInstance(String recordId) {
//        UserRecord record;
//        if ((record = getRecordCatch(recordId)) == null) {
//            record = userRecordService.getRecordById(recordId);
//            saveRecord(record);
//        }
//
//        if (selfManger.get(recordId) == null) {
//            synchronized (RobotManger.class) {
//                RobotManger robotManger = new RobotManger(record);
//                selfManger.put(recordId, robotManger);
//            }
//        }
//
//        return selfManger.get(record.getUuid());
//    }

    public static RobotManger getInstance(UserRecord record) {
        if (selfManger.get(record.getUuid()) == null) {
            synchronized (RobotManger.class) {
                RobotManger robotManger = new RobotManger(record);
                selfManger.put(record.getUuid(), robotManger);
            }
        }
        if (getRecordCatch(record.getUuid()) == null) {
            saveRecord(record);
        }
        return selfManger.get(record.getUuid());
    }


    /**
     * 处理执行完任务的方法
     *
     * @param active
     */
    public static void submitActive(RobotActive active) {

        CompletableFuture<ActiveResult> future = CompletableFuture.supplyAsync(active, executor);

        // 对结果的处理
        future.thenAccept(ar -> {
            if (ar instanceof SelectUnitResult) {

                SelectUnitResult selectUnitResult = (SelectUnitResult) ar;
                log.info("执行了单位选择操作 最终选择位置：{}", selectUnitResult.getSite());
                aiMessageSender.sendSelectUnit(selectUnitResult);
                // 选择单位结束准备延迟执行被选择单位的移动
                sleep(100);
                RobotManger.addSelectResult(selectUnitResult);
                RobotManger.submitActive(new RobotActive(selectUnitResult.getRecordId(), AiActiveEnum.MOVE_UNIT));
                log.info("选择完毕 提交准备移动单位的任务");

            }else if (ar instanceof UnitActionResult) {
                UnitActionResult unitActionResult = (UnitActionResult) ar;
                log.info("执行了单位行动类型{}", unitActionResult.getResultEnum());
                // 根据action 的类型进行选择
                switch (unitActionResult.getResultEnum()) {
                    case REPAIR:
                        break;
                    case MOVE_UNIT:
                        doSendMoveUnitMes(unitActionResult);
                        break;
                }
            }

        });

        // 对错误结果的处理
        future.exceptionally(throwable -> {
            log.error("", throwable);
            return new ActiveResult();
        });

    }


    /**
     * 单位没有实际的可选行动
     * @param unitActionResult
     */
    private static void doSendMoveUnitMes(UnitActionResult unitActionResult) {
        aiMessageSender.sendMoveUnit(recordMap.get(unitActionResult.getRecordId()), selectUnitResultMap.get(unitActionResult.getRecordId()), unitActionResult);
    }


    /**
     * 延迟执行
     * @param time
     */
    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存选择单位的记录
     *
     * @param selectUnitResult
     */
    public static void addSelectResult(SelectUnitResult selectUnitResult) {
        selectUnitResultMap.put(selectUnitResult.getRecordId(), selectUnitResult);
    }

    /**
     * 本地缓存记录
     *
     * @param record
     */
    public static void saveRecord(UserRecord record) {
        recordMap.put(record.getUuid(), record);
    }

    public static UserRecord getRecordCatch(String recordId) {
        return recordMap.get(recordId);
    }

    /**
     * 获取选择结果缓存
     *
     * @param uuid
     */
    public static SelectUnitResult getSelectResult(String uuid) {
        return selectUnitResultMap.get(uuid);
    }

    public boolean isThreatened(Site currSite) {

        return dangerCastle.contains(currSite) || dangerVillage.contains(currSite);
    }

    public void addDangerVillage(Site nearestVillagePosition) {
        this.dangerVillage.add(nearestVillagePosition);
    }

    public List<SiteSquare> getCastleSite() {
        return castleSite;
    }

    public List<SiteSquare> getVillageSite() {
        return villageSite;
    }

    public List<Site> getDangerCastle() {
        return dangerCastle;
    }

    public void setDangerCastle(List<Site> dangerCastle) {
        this.dangerCastle = dangerCastle;
    }

    public List<Site> getDangerVillage() {
        return dangerVillage;
    }

    public void setDangerVillage(List<Site> dangerVillage) {
        this.dangerVillage = dangerVillage;
    }

    public AiActiveHandle getSelectUnitHandle() {
        return this.aiSelectUnitHandle;
    }

    public AiActiveHandle getMoveUnitHandle() {
        return this.aiMoveHandle;
    }
}
