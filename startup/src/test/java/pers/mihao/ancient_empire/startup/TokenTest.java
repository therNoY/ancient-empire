package pers.mihao.ancient_empire.startup;

import org.junit.Test;
import pers.mihao.ancient_empire.common.util.JwtTokenUtil;

public class TokenTest {

    @Test
    public void name() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImNyZWF0ZWQiOjE1NjU0MzgzNzM4NDEsImV4cCI6MTU2NjA0MzE3M30.fntJCcCpc0LKNzxBELJWCXPUHx6ytRTaySmoOkQgFMDMLAsmzEEyPAeEw3oZdBay2gkJMFH4xuTXLqYxr3dewg";
        JwtTokenUtil.getEffectiveUserId(token);
    }
}
