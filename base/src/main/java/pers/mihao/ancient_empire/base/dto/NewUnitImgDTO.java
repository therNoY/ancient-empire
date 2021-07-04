package pers.mihao.ancient_empire.base.dto;

import java.io.Serializable;

/**
 * @Author mihao
 * @Date 2021/5/2 17:55
 */
public class NewUnitImgDTO implements Serializable {

    private String img1;

    private String img2;

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    @Override
    public String toString() {
        return "NewUnitImgDTO{" +
            "img1='" + img1 + '\'' +
            ", img2='" + img2 + '\'' +
            '}';
    }
}
