package pers.mihao.ancient_empire.common.util.reflact;

import org.junit.Test;
import pers.mihao.ancient_empire.common.util.ValidateUtil;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\1\17 0017 19:48
 */
public class ValidateUtilTest {

    @Test
    public void name() {
        ValidateUtilTestBean validateUtilTestBean = new ValidateUtilTestBean();
        validateUtilTestBean.setId(1);
        try {
            ValidateUtil.validateBean(validateUtilTestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        validateUtilTestBean.setAge(1);
        try {
            ValidateUtil.validateBean(validateUtilTestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        validateUtilTestBean.setName("1");
        try {
            ValidateUtil.validateBean(validateUtilTestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

        validateUtilTestBean.setValidateUtilTestBean1(new ValidateUtilTestBean());

        try {
            ValidateUtil.validateBean(validateUtilTestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

        validateUtilTestBean.getValidateUtilTestBean1().setId(1);

        try {
            ValidateUtil.validateBean(validateUtilTestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

        validateUtilTestBean.getValidateUtilTestBean1().setName("1");

        try {
            ValidateUtil.validateBean(validateUtilTestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

        validateUtilTestBean.getValidateUtilTestBean1().setAge(1);

        try {
            ValidateUtil.validateBean(validateUtilTestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

        validateUtilTestBean.setValidateUtilTestBean2(new ValidateUtilTestBean());
        try {
            ValidateUtil.validateBean(validateUtilTestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

        validateUtilTestBean.getValidateUtilTestBean2().setId(1);

        try {
            ValidateUtil.validateBean(validateUtilTestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
