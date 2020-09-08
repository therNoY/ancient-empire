package pers.mihao.ancient_empire.startup;

import com.mihao.ancient_empire.common.util.JwtTokenHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenTest {

    @Autowired
    JwtTokenHelper jwtTokenHelper;

    @Test
    public void name() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImNyZWF0ZWQiOjE1NjU0MzgzNzM4NDEsImV4cCI6MTU2NjA0MzE3M30.fntJCcCpc0LKNzxBELJWCXPUHx6ytRTaySmoOkQgFMDMLAsmzEEyPAeEw3oZdBay2gkJMFH4xuTXLqYxr3dewg";
        jwtTokenHelper.getUserNameFromToken(token);
    }
}
