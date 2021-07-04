package pers.mihao.ancient_empire.common.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.imageio.ImageIO;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;
import pers.mihao.ancient_empire.common.vo.AeException;

/**
 * @Author mihao
 * @Date 2021/1/18 10:03
 */
public class ImgUtil {

    /**
     * 替换一个文件的所有要素点 替换成另一个颜色 并保存文件
     *
     * @param sourceColors
     * @param newColorList
     * @param sourceFile
     * @param newFilePath
     */
    public static void replaceColor(Collection<String> sourceColors, Collection<String> newColorList,
        File sourceFile, String newFilePath) throws IOException {

        if (sourceColors.size() != newColorList.size()) {
            throw new AeException("需要替换颜色数量不新颜色数量不一致");
        }
        String[] sourceColorArray = new String[sourceColors.size()], newColorArray = new String[sourceColors.size()];
        sourceColors.toArray(sourceColorArray);
        newColorList.toArray(newColorArray);

        String oldColor, newColor;
        BufferedImage bi = ImageIO.read(sourceFile);
        int width = bi.getWidth(), height = bi.getHeight(), minx = bi.getMinX(), miny = bi.getMinY();

        Color color;
        int[] newColorRgb, rgb;
        for (int k = 0; k < sourceColorArray.length; k++) {
            oldColor = sourceColorArray[k];
            newColor = newColorArray[k];
            newColorRgb = hex2Rgb(newColor);
            color = new Color(newColorRgb[0], newColorRgb[1], newColorRgb[2]);
            rgb = new int[3];

            for (int i = minx; i < width; i++) {
                for (int j = miny; j < height; j++) {
                    int pixel = bi.getRGB(i, j);
                    // 下面三行代码将一个数字转换为RGB数字
                    rgb[0] = (pixel & 0xff0000) >> 16;
                    rgb[1] = (pixel & 0xff00) >> 8;
                    rgb[2] = (pixel & 0xff);
                    if (oldColor.equalsIgnoreCase(rgb2Hex(rgb[0], rgb[1], rgb[2]))) {
                        bi.setRGB(i, j, color.getRGB());
                    }
                }
            }
        }
        File file = new File(newFilePath);
        if (file.exists()) {
            file.delete();
            file.createNewFile();
        }
        ImageIO.write(bi, "PNG", file);
        bi.flush();
    }

    /**
     * 16进制颜色字符串转换成rgb
     *
     * @param hexStr
     * @return rgb
     */
    public static int[] hex2Rgb(String hexStr) {
        if (StringUtil.isNotBlack(hexStr) && hexStr.length() == 7) {
            int[] rgb = new int[3];
            rgb[0] = Integer.valueOf(hexStr.substring(1, 3), 16);
            rgb[1] = Integer.valueOf(hexStr.substring(3, 5), 16);
            rgb[2] = Integer.valueOf(hexStr.substring(5, 7), 16);
            return rgb;
        }
        throw new AeException("颜色格式错误");
    }

    /**
     * 将图片改成黑白的
     *
     * @param img
     * @param newFile
     */
    @KnowledgePoint("图片转成黑白, 保持浅的色调, 保存透明背景 需要转两次")
    public static void changeImg2Bw(File img, String newFile) throws IOException {
        BufferedImage image = ImageIO.read(img);
        int srcH = image.getHeight(), srcW = image.getWidth();
        int minx = image.getMinX(), miny = image.getMinY();
        // 记录透明的位置
        int[][] alphaArray = new int[srcW][srcH];
        for (int i = minx; i < srcW; i++) {
            for (int j = miny; j < srcH; j++) {
                if (((image.getRGB(i, j) >> 24) & 0xff) == 0) {
                    alphaArray[i][j] = 1;
                }
            }
        }

        BufferedImage bufferedImage = new BufferedImage(srcW, srcH, BufferedImage.TYPE_BYTE_GRAY);
        bufferedImage.getGraphics().drawImage(image, 0, 0, srcW, srcH, null);
        bufferedImage = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null)
            .filter(bufferedImage, null);

        File tempFile = new File(newFile);
        ImageIO.write(bufferedImage, "PNG", tempFile);
        bufferedImage.flush();

        changeAlpha(newFile, alphaArray);
    }

    /**
     * @param path       路径
     * @param alphaArray
     */
    private static void changeAlpha(String path, int[][] alphaArray) throws IOException {
        // 检查透明度是否越界
        BufferedImage image = ImageIO.read(new File(path));
        int weight = image.getWidth();
        int height = image.getHeight();

        BufferedImage output = new BufferedImage(weight, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        output = g2.getDeviceConfiguration().createCompatibleImage(weight, height, Transparency.TRANSLUCENT);
        g2.dispose();
        g2 = output.createGraphics();

        // 调制透明度
        int rgb;
        for (int i = output.getMinY(); i < output.getHeight(); i++) {
            for (int j = output.getMinX(); j < output.getWidth(); j++) {
                // 对当前颜色判断是否在指定区间内
                if (alphaArray[i][j] != 1) {
                    rgb = output.getRGB(i, j);
                    rgb = ((10 * 255 / 10) << 24) | (rgb & 0x00ffffff);
                    output.setRGB(i, j, rgb);
                }
            }
        }
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(image, 0, 0, weight, height, null);
        g2.dispose();
        ImageIO.write(output, "png", new File(path));

    }

    /**
     * rgb转换成16进制
     *
     * @param r
     * @param g
     * @param b
     * @return
     */
    public static String rgb2Hex(int r, int g, int b) {
        return String.format("#%02X%02X%02X", r, g, b);
    }

}
