package pers.mihao.ancient_empire.common.util.reflact;

import pers.mihao.ancient_empire.common.annotation.ValidatedBean;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\1\17 0017 19:49
 */
@ValidatedBean(notNull = {"name","id"})
public class ValidateUtilTestBean {
    private Integer id;
    private String name;
    private Integer age;

    @ValidatedBean(notNull = "*")
    private ValidateUtilTestBean validateUtilTestBean1;

    @ValidatedBean(notNull = "id")
    private ValidateUtilTestBean validateUtilTestBean2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public ValidateUtilTestBean getValidateUtilTestBean1() {
        return validateUtilTestBean1;
    }

    public void setValidateUtilTestBean1(ValidateUtilTestBean validateUtilTestBean1) {
        this.validateUtilTestBean1 = validateUtilTestBean1;
    }

    public ValidateUtilTestBean getValidateUtilTestBean2() {
        return validateUtilTestBean2;
    }

    public void setValidateUtilTestBean2(ValidateUtilTestBean validateUtilTestBean2) {
        this.validateUtilTestBean2 = validateUtilTestBean2;
    }
}
