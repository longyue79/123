package sys.Service;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import sys.Session.Session;

import javax.naming.ConfigurationException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class FaceService {
    private static String appId = null;
    private static String sdkKey = null;
    private static FaceEngine faceEngine = null;
    private static String ENGINE = "ArcFace/WIN64";

    static {
        Properties prop = new Properties();
        try {
            InputStream input = FaceService.class.getClassLoader().getResourceAsStream("ArcFace.properties");
            if(input == null){
                throw new FileNotFoundException("property file ArcFace.properties not found");
            }
            prop.load(input);
            appId = prop.getProperty("app_id");
            sdkKey = prop.getProperty("sdk_key");
            if(appId == null || sdkKey == null) {
                throw new ConfigurationException("ArcFace.properties file not found");
            }

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (ConfigurationException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        File path = new File(ENGINE);
        faceEngine = new FaceEngine(path.getAbsolutePath());
        int errorCode =  faceEngine.activeOnline(appId, sdkKey);
        if(errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()){
            System.err.println("ERROR: ArcFace引擎激活失败，请检查授权码，或尝试重新联网激活。");
        }

        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceScaleVal(16);
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);

        errorCode = faceEngine.init(engineConfiguration);
        if(errorCode != ErrorInfo.MOK.getValue()){
            System.err.println("ERROR:ArcFace引擎初始化失败");
        }
    }

    public static FaceFeature getFaceFeature(BufferedImage image){
        if(image == null){
            throw new NullPointerException("人脸图像为null");
        }

        BufferedImage face = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_BGR);
        face.setData(image.getData());
        ImageInfo info = ImageFactory.bufferedImage2ImageInfo(face);
        List<FaceInfo> faceInfos = new ArrayList<>();
        faceEngine.detectFaces(info.getImageData(), info.getWidth(), info.getHeight(), info.getImageFormat(), faceInfos);

        if(faceInfos.isEmpty()){
            return null;
        }
        FaceFeature faceFeature = new FaceFeature();
        faceEngine.extractFaceFeature(info.getImageData(), info.getWidth(), info.getHeight(), info.getImageFormat(),
                faceInfos.get(0), faceFeature);
        return faceFeature;
    }

    public static void loadAllFaceFeature(){
        Set<String> sets  = Session.IMAGE_HASH_MAP.keySet();
        for(String code : sets){
            BufferedImage image = Session.IMAGE_HASH_MAP.get(code);
            FaceFeature faceFeature = getFaceFeature(image);
            Session.FACE_FEATURE_HASH_MAP.put(code, faceFeature);
        }
    }

    public static String detectFaceFeature(FaceFeature faceFeature){
        if(faceFeature == null){
            return null;
        }

        Set<String> sets  = Session.FACE_FEATURE_HASH_MAP.keySet();
        float score = 0;
        String resultCode = null;
        for(String code : sets){
            FaceFeature feature  = Session.FACE_FEATURE_HASH_MAP.get(code);
            FaceSimilar faceSimilar = new FaceSimilar();
            faceEngine.compareFaceFeature(faceFeature, feature, faceSimilar);
            if(faceSimilar.getScore() > score){
                score = faceSimilar.getScore();
                resultCode = code;
            }
        }
        if(score > 0.9){
            return resultCode;
        }
        return null;
    }

    public static void Resource(){
        faceEngine.unInit();
    }
}
