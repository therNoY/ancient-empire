package pers.mihao.ancient_empire.common.util;

public class IntegerUtil {


    public static int getRandomIn(int max) {

        return (int) (Math.round(Math.random() * (max - 0)) + 0);
    }

    public static boolean isNum(String numString) {
        try {
            Integer.parseInt(numString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static int getRandomIn(int min, int max) {
        return (int) (Math.round(Math.random() * (max - min)) + min);
    }
}
