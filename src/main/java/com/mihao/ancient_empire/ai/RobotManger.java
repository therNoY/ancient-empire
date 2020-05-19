package com.mihao.ancient_empire.ai;

import com.mihao.ancient_empire.ai.constant.AiActiveEnum;
import com.mihao.ancient_empire.ai.dto.*;
import com.mihao.ancient_empire.ai.handle.AiActiveHandle;
import com.mihao.ancient_empire.ai.handle.AiMoveHandle;
import com.mihao.ancient_empire.ai.handle.AiSelectUnitHandle;
import com.mihao.ancient_empire.constant.ArmyEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.constant.WSPath;
import com.mihao.ancient_empire.constant.WsMethodEnum;
import com.mihao.ancient_empire.dto.*;
import com.mihao.ancient_empire.dto.ws_dto.*;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.handle.move_path.MovePathHandle;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.ApplicationContextHolder;
import com.mihao.ancient_empire.util.WsRespHelper;
import com.mihao.ancient_empire.websocket.service.WsEndRoundService;
import com.mihao.ancient_empire.websocket.service.WsMoveAreaService;
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
    private static WsEndRoundService endRoundService;
    private static WsMoveAreaService moveAreaService;
    private static Map<String, RobotManger> selfManger = new HashMap<>();
    private static ThreadPoolExecutor executor;
    private static ScheduledExecutorService scheduledExecutorService;
    private static Map<String, SelectUnitResult> selectUnitResultMap = null;
    private static Map<String, UserRecord> recordMap; /* 对地图的缓存 */


    /* 维持一个保存有map 和map 对应状态的类 */
    private AiMoveHandle aiMoveHandle; /*每一个record 生成一个*/
    private AiSelectUnitHandle aiSelectUnitHandle; /*每一个record 生成一个*/

    private List<Site> dangerCastle = new ArrayList<>(); // 有危险的城堡
    private List<Site> dangerVillage = new ArrayList<>(); // 有危险的村庄
    private List<PathPosition> pathPositions = null; /*上一个单位移动的路径*/
    private Map<Integer, List<Site>> aimSite = new HashMap<>(); // 当前单位想要占领的或者修复的点
    List<SiteSquare> castleSite = new ArrayList<>(); // 所有的城堡
    List<SiteSquare> villageSite = new ArrayList<>(); // 所有的村庄
    List<SiteSquare> ruinsSite = new ArrayList<>(); // 当前地图所有的废墟

    static {
        executor = new ThreadPoolExecutor(3,
                10,
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(9),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        scheduledExecutorService = Executors.newScheduledThreadPool(30);
        selectUnitResultMap = new HashMap<>();
        recordMap = new HashMap<>();
        aiMessageSender = ApplicationContextHolder.getBean(AiMessageSender.class);
        userRecordService = ApplicationContextHolder.getBean(UserRecordService.class);
        endRoundService = ApplicationContextHolder.getBean(WsEndRoundService.class);
        moveAreaService = ApplicationContextHolder.getBean(WsMoveAreaService.class);
    }

    private RobotManger(UserRecord record) {
        for (int i = 0; i < record.getInitMap().getRegions().size(); i++) {
            BaseSquare square = record.getInitMap().getRegions().get(i);
            if (square.getType().equals(RegionEnum.CASTLE.type())) {
                castleSite.add(new SiteSquare(square, AppUtil.getSiteByMapIndex(i, record.getInitMap().getColumn())));
            } else if (square.getType().equals(RegionEnum.TOWN.type())) {
                villageSite.add(new SiteSquare(square, AppUtil.getSiteByMapIndex(i, record.getInitMap().getColumn())));
            }else if (square.getType().equals(RegionEnum.RUINS.type())) {
                ruinsSite.add(new SiteSquare(square, AppUtil.getSiteByMapIndex(i, record.getInitMap().getColumn())));
            }
        }
        aiMoveHandle = new AiMoveHandle(record.getUuid());
        aiSelectUnitHandle = new AiSelectUnitHandle(record.getUuid());
        log.warn("每个不同的record 创建一个实例对象id = {}", record.getUuid());
    }

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
            try {
                if (ar instanceof SelectUnitResult) {
                    // 1.准备发送给前端
                    SelectUnitResult selectUnitResult = (SelectUnitResult) ar;
                    log.info("执行了单位选择操作 最终选择位置：{}", selectUnitResult.getSite());
                    aiMessageSender.sendSelectUnit(selectUnitResult);

                    // 2. 选择呢单位后提交移动单位任务
                    log.info("[ 提交准备移动单位的任务 ]");
                    RobotManger.addSelectResult(selectUnitResult);
                    RobotManger.submitActive(new RobotActive(selectUnitResult.getRecordId(), AiActiveEnum.MOVE_UNIT));

                } else if (ar instanceof UnitActionResult) {
                    UnitActionResult unitActionResult = (UnitActionResult) ar;
                    log.info("执行了单位行动类型{}", unitActionResult.getResultEnum());
                    // 根据action 的类型进行选择
                    switch (unitActionResult.getResultEnum()) {
                        case REPAIR:
                            doSendRepair(unitActionResult);
                            break;
                        case OCCUPIED:
                            doSendOccupied(unitActionResult);
                            break;
                        case MOVE_UNIT:
                            doSendMoveUnitMes(unitActionResult);
                            break;
                    }
                } else if (ar instanceof BuyUnitResult) {
                    // 1. 准备发送给前端
                    BuyUnitResult buyUnitResult = (BuyUnitResult) ar;
                    Unit unit = new Unit(buyUnitResult.getUnitMes().getType(), buyUnitResult.getSite().getRow(), buyUnitResult.getSite().getColumn());
                    UserRecord record = recordMap.get(ar.getRecordId());
                    int currentArmy = AppUtil.getCurrentArmyIndex(record);
                    buyUnitResult.setArmyIndex(currentArmy);
                    buyUnitResult.setUnit(unit);
                    aiMessageSender.sendByUnit(buyUnitResult);

                    // 2.更新 record 的信息
                    Army army = record.getArmyList().get(currentArmy);
                    army.getUnits().add(unit);
                    army.setPop(army.getPop() + buyUnitResult.getUnitMes().getPopulation());
                    army.setMoney(army.getMoney() - buyUnitResult.getUnitMes().getPrice());

                    // 3. 准备提交选择下一个单位的命令
                    RobotActive newAction = new RobotActive(record, AiActiveEnum.SELECT_UNIT);
                    RobotManger.getInstance(record).submitActive(newAction);
                    log.info("[ 开始新的一轮提交选择单位任务 ]");
                }else if (ar instanceof EndTurnResult) {
                    EndTurnResult endTurnResult = (EndTurnResult) ar;
                    RespNewRoundDto newRoundDto = endRoundService.getNewRound(recordMap.get(endTurnResult.getRecordId()));
                    InitMap map = newRoundDto.getRecord().getInitMap();
                    newRoundDto.getRecord().setInitMap(null);
                    aiMessageSender.sendEndTurnResult(newRoundDto);

                    // 判断新的回合是不是机器人
                    Army nextArmy = AppUtil.getCurrentArmy(newRoundDto.getRecord());
                    if (nextArmy.getType().equals(ArmyEnum.AI.type())) {
                        newRoundDto.getRecord().setInitMap(map);
                        RobotActive newTurnAction = new RobotActive(newRoundDto.getRecord(), AiActiveEnum.SELECT_UNIT);
                        RobotManger robotManger = RobotManger.getInstance(newRoundDto.getRecord());
                        robotManger.saveRecord(newRoundDto.getRecord());
                        log.info("\n==================[ 准备提交新的Ai行动 ]======================");
                        log.info("颜色：{}", nextArmy.getColor());
                        addTimerTask(()->robotManger.submitActive(newTurnAction), 100);
                    }

                }
            } catch (Exception e) {
                log.error("", e);
                throw e;
            }


        });

        // 对错误结果的处理
        future.exceptionally(throwable -> {
            log.error("", throwable);
            return new ActiveResult();
        });

    }




    /**
     * 对一些延迟任务的处理
     *
     * @param runnable
     * @param time
     */
    public static void addTimerTask(Runnable runnable, long time) {
        scheduledExecutorService.schedule(runnable, time, TimeUnit.MILLISECONDS);
    }

    /**
     * 单位没有实际的可选行动
     *
     * @param unitActionResult
     */
    private static void doSendMoveUnitMes(UnitActionResult unitActionResult) {
        // 1.发送单位移动命令
        UserRecord record = recordMap.get(unitActionResult.getRecordId());
        aiMessageSender.sendMoveUnit(record, selectUnitResultMap.get(unitActionResult.getRecordId()), unitActionResult);

        // 2.准备同时发送endAction 命令
        RespEndResultDto endResultDto = AiActiveHandle.getEndDto(record, unitActionResult.getUnit());
        EndUnitResult endUnitResult = new EndUnitResult(record.getUuid(), unitActionResult.getSite(), endResultDto.getLifeChanges());
        int time = unitActionResult.getLength() * 250 + 200;
        log.info("准备提交结束移动命令{}, 延迟{} ms", endResultDto, time);
        aiMessageSender.sendEndUnit(endUnitResult, time);

        // 3.更新record 移动后的状态
        unitActionResult.getUnit().setRow(unitActionResult.getSite().getRow());
        unitActionResult.getUnit().setColumn(unitActionResult.getSite().getColumn());
        unitActionResult.getUnit().setDone(true);


        // 4.准备提交选择下一个单位的命令
        addTimerTask(() -> {
            RobotActive active = new RobotActive(record, AiActiveEnum.SELECT_UNIT);
            RobotManger.getInstance(record).submitActive(active);
            log.info("[ 开始新的一轮提交选择单位任务 ]");
        }, time + 100);
    }

    private static void doSendRepair(UnitActionResult actionResult) {

        // 1 设置修复
        UserRecord record = recordMap.get(actionResult.getRecordId());
        Integer regionIndex = AppUtil.getRegionIndex(record, actionResult.getSite());
        RespRepairOcpResult result = new RespRepairOcpResult();
        result.setRecordId(record.getUuid());
        result.setRegionIndex(regionIndex);
        BaseSquare region = record.getInitMap().getRegions().get(regionIndex);
        region.setType(RegionEnum.TOWN.type());
        result.setSquare(region);

        record.getInitMap().getRegions().get(result.getRegionIndex()).setType(RegionEnum.TOWN.type());

        doRepairOrOccupied(actionResult, result);
    }

    /**
     * 单位选择占领
     * @param actionResult
     */
    private static void doSendOccupied(UnitActionResult actionResult) {
        // 1 设置修复
        UserRecord record = recordMap.get(actionResult.getRecordId());
        Integer regionIndex = AppUtil.getRegionIndex(record, actionResult.getSite());
        RespRepairOcpResult result = new RespRepairOcpResult();
        result.setRecordId(record.getUuid());
        result.setRegionIndex(regionIndex);
        BaseSquare region = record.getInitMap().getRegions().get(regionIndex);
        region.setColor(record.getCurrColor());
        result.setSquare(region);

        record.getInitMap().getRegions().get(result.getRegionIndex()).setColor(record.getCurrColor());

        doRepairOrOccupied(actionResult, result);
    }

    /**
     * 处理修复或者占领
     * @param actionResult
     * @param result
     */
    private static void doRepairOrOccupied(UnitActionResult actionResult, RespRepairOcpResult result){
        UserRecord record = recordMap.get(actionResult.getRecordId());

        // 2 设置二次移动结果
        SecondMoveDto secondMoveDto = moveAreaService.getSecondMove(actionResult.getSelectUnit(), record, RobotManger.getInstance(record).getPathPositions());
        result.setSecondMove(secondMoveDto);


        ReqMoveDto reqMoveDto = new ReqMoveDto(AppUtil.getPosition(actionResult.getSelectUnit()), (Position) actionResult.getSite(), actionResult.getMoveArea());
        List<PathPosition> pathPositions = moveAreaService.getMovePath(reqMoveDto);
        result.setPathPositions(pathPositions);
        aiMessageSender.sendOccupiedResult(record,actionResult, result);

        int time = pathPositions.size() * 250 + 200;
        // 3.准备同时发送endAction 命令
        RespEndResultDto endResultDto = AiActiveHandle.getEndDto(record, actionResult.getSelectUnit());
        EndUnitResult endUnitResult = new EndUnitResult(record.getUuid(), actionResult.getSite(), endResultDto.getLifeChanges());
        log.info("准备提交结束移动命令{}, 延迟{} ms", endResultDto, time);
        aiMessageSender.sendEndUnit(endUnitResult, time);

        actionResult.getSelectUnit().setDone(true);
        actionResult.getSelectUnit().setColumn(actionResult.getSite().getColumn());
        actionResult.getSelectUnit().setRow(actionResult.getSite().getRow());


        // 4.准备提交选择下一个单位的命令
        addTimerTask(() -> {
            RobotActive active = new RobotActive(record, AiActiveEnum.SELECT_UNIT);
            RobotManger.getInstance(record).submitActive(active);
            log.info("[ 开始新的一轮提交选择单位任务 ]");
        }, time + 100);
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


    public List<SiteSquare> getRuinsSite() {
        return ruinsSite;
    }

    public Map<Integer, List<Site>> getAimSite() {
        return aimSite;
    }

    public List<PathPosition> getPathPositions() {
        return pathPositions;
    }

    public void setPathPositions(List<PathPosition> pathPositions) {
        this.pathPositions = pathPositions;
    }
}
