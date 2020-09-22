package pers.mihao.ancient_empire.common.util.reflact;

import org.junit.Test;
import pers.mihao.ancient_empire.common.jdbc.mongo.MongodbConverter;

import java.util.List;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\9\20 0020 11:16
 */
public class ReflectUtilTest {

    @Test
    public void testGetField(){
        List<String> res = new MongodbConverter().getQueryFieldByClass(BaseMapInfoTestVO.class);
    }
}
