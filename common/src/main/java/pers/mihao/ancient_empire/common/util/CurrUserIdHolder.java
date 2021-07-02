package pers.mihao.ancient_empire.common.util;

/**
 * @Author mh32736
 * @Date 2021/6/30 14:26
 */
public class CurrUserIdHolder {

    private static ThreadLocal<Integer> authId = new ThreadLocal<>();

    public static Integer getUserId() {
        return authId.get();
    }

    public static void setUserId(Integer userId){
        authId.set(userId);
    }

    public static void clean(){
        authId.remove();
    }
}
