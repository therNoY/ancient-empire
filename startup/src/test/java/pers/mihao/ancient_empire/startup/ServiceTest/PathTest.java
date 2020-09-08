package pers.mihao.ancient_empire.startup.ServiceTest;

import com.mihao.ancient_empire.common.util.JacksonUtil;
import pers.mihao.ancient_empire.common.bo.ws_dto.ReqMoveDto;
import org.junit.Test;

import java.io.File;

public class PathTest {

    @Test
    public void getPath() {
//        WebSocketService ws = new WebSocketService();

        String filePath = "D:\\Users\\Administrator\\Idea\\ancient-empire\\src\\test\\java\\com\\mihao\\ancient_empire\\ServiceTest/package.json";
        File file = new File(filePath);
        ReqMoveDto moveDto = JacksonUtil.fileToBean(file, ReqMoveDto.class);
//        Position aimP = moveDto.getAimPoint();
//        Position cP = moveDto.getCurrentPoint();
//        List<Position> positions = moveDto.getMoveArea();
//        List<Position> path = ws.getMovePath(moveDto);
//        System.out.println(path.size());
//        System.out.println(path.size());

//        Position okPosition = null;
//        cP.setDirection(-1);
//        List<Position> isVisited = new ArrayList<>();
//        ws.getOkPosition(okPosition, cP, aimP, positions, isVisited);
//        System.out.println(okPosition);
//
//        Boolean b = ws.isHorizonPath(cP, aimP, positions);
//        System.out.println(b);
    }




}
