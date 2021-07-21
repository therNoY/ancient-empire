package pers.mihao.ancient_empire.base;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;
import pers.mihao.ancient_empire.common.util.ImgUtil;

/**
 * @Author mihao
 * @Date 2021/5/2 19:35
 */
public class ImgUtilTest {

    @Test
    public void test1() throws IOException {
        BufferedImage bi = ImageIO.read(new File("C:\\Users\\hspcadmin\\Desktop\\paladin.png"));
        int width = bi.getWidth(), height = bi.getHeight(), minx = bi.getMinX(), miny = bi.getMinY();

        int[] rgb = new int[3];
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = bi.getRGB(i, j);
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);
                System.out.println(ImgUtil.rgb2Hex(rgb[0], rgb[1], rgb[2]));
                System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + ","
                    + rgb[1] + "," + rgb[2] + ")");
            }
        }
    }

    @Test
    public void test2() {
        try {
            ImgUtil.changeImg2Bw(new File("C:\\Users\\hspcadmin\\Desktop\\3.png"),
                "C:\\Users\\hspcadmin\\Desktop\\2_3.png");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void name() {
        System.out.println( (-1 << 1) >> 1);
        System.out.println( (1 << 1) >> 1);
    }
}
