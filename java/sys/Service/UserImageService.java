package sys.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sys.Session.Session;

public class UserImageService {
    private static final File FACE_DIR;

    private static final String SUFFIX = "jpg";

    static {
        // 构建项目根目录下的 faces 文件夹路径
        FACE_DIR = new File("src/main/resources/faces");
        if (!FACE_DIR.exists()) {
            if (!FACE_DIR.mkdirs()) {
                throw new RuntimeException("无法创建 faces 文件夹");
            }
        }
    }

    public static Map<String, BufferedImage> loadUserImage() {
        Map<String, BufferedImage> map = new HashMap<>();
        File[] files = FACE_DIR.listFiles();

        if (files != null) {
            for (File file : files) {
                try {
                    BufferedImage img = ImageIO.read(file);
                    String fileName = file.getName();// 文件名
                    // 截取文件名，去掉后缀名
                    String code = fileName.substring(0, fileName.indexOf('.'));
                    // 将人脸图像添加到全局会话中
                    Session.IMAGE_HASH_MAP.put(code, img);
                    map.put(code, img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    public static void saveFaceImage(BufferedImage image, String code) {
        try {
            // 将图像按照 SUFFIX 格式写入到文件夹中
            ImageIO.write(image, SUFFIX, new File(FACE_DIR, code + "." + SUFFIX));
            // 将人脸图像添加到全局会话中
            Session.IMAGE_HASH_MAP.put(code, image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFaceImage(String code) {
        Session.IMAGE_HASH_MAP.remove(code);// 在全局会话中删除该员工的图像
        // 创建该员工人脸图像文件对象
        File image = new File(FACE_DIR, code + "." + SUFFIX);
        if (image.exists()) {// 如果此文件存在
            image.delete();// 删除文件
            // 提示删除文件成功
            System.out.println(image.getAbsolutePath() + " ---已删除");
        }
    }
}