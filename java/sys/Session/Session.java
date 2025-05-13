package sys.Session;

import com.arcsoft.face.FaceFeature;
import sys.Service.CameraService;
import sys.Service.FaceService;
import sys.Service.HRService;
import sys.Service.UserImageService;
import sys.message.Record_Except_id;
import sys.message.work_time;
import sys.people.Admin;
import sys.people.User;
import sys.people.UserLogin;
import sys.tool.JDBCSet;

import java.awt.image.BufferedImage;
import java.util.*;

public class Session {
    public static Admin admin = null;

    public static User eminformation = null;

    public static UserLogin UserLogin = null;

    public static work_time worktime = null;

    public static final HashSet<User> EMINFORMATIONS = new HashSet<>();

    public static final HashSet<Admin> ADMINS = new HashSet<>();

    public static final HashMap<String, FaceFeature> FACE_FEATURE_HASH_MAP = new HashMap<>();

    public static final HashMap<String, BufferedImage> IMAGE_HASH_MAP = new HashMap<>();

    public static final HashMap<Integer, Set<Record_Except_id>> TIME_HASH_MAP = new HashMap<>();

    public static void init() {
        UserImageService.loadUserImage();
        HRService.loadAllUsers();
        HRService.loadAllWorkTimes();
        HRService.loadAllClockRecord();
        FaceService.loadAllFaceFeature();
    }

    public static void dispose() {
        FaceService.Resource();
        CameraService.releaseCamera();
        JDBCSet.closeConnection();
    }
}
