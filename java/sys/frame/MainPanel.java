package sys.frame;

import sys.Service.CameraService;
import sys.Service.FaceService;
import sys.Service.HRService;
import sys.Session.Session;
import sys.people.User;
import sys.tool.DateTimeSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class MainPanel extends JPanel {
    private MainFrame frame;
    private JToggleButton recordButton;
    private JTextArea textArea;
    private JPanel panel;
    private JButton addEmployee;
    private JButton kaoqin;
    private JButton yuangong;
    private DetectFaceThread dfThread;

    public MainPanel(MainFrame frame) {
        this.frame = frame;
        init();
        addListeners();
    }

    private void init() {
        frame.setTitle("公司人脸识别打卡系统");

        panel = new JPanel();// 中部面板
        panel.setLayout(null);// 采用绝对布局

        textArea = new JTextArea();
        textArea.setEditable(false);// 文本域不可编辑
        textArea.setFont(new Font("黑体", Font.BOLD, 18));// 字体
        JScrollPane scroll = new JScrollPane(textArea);// 文本域放入滚动面板
        scroll.setBounds(0, 0, 275, 380);// 滚动面板的坐标与宽高
        panel.add(scroll);

        recordButton = new JToggleButton("打  卡");
        recordButton.setFont(new Font("黑体", Font.BOLD, 40));// 字体
        recordButton.setBounds(330, 300, 240, 70);// 打卡按钮的坐标与宽高
        panel.add(recordButton);

        JPanel blakPanel = new JPanel();// 纯黑面板
        blakPanel.setBounds(286, 16, 320, 240);// 黑色面板的坐标与宽高
        blakPanel.setBackground(Color.BLACK);// 黑色背景
        panel.add(blakPanel);

        setLayout(new BorderLayout());// 主面板采用边界布局
        add(panel, BorderLayout.CENTER);


        JPanel bottom = new JPanel(new GridLayout(1, 3, 10, 0)); // 改为网格布局
        kaoqin = new JButton("考勤报表");
        yuangong = new JButton("员工管理");
        addEmployee = new JButton("添加员工"); // 新增按钮
        bottom.add(kaoqin);
        bottom.add(yuangong);
        bottom.add(addEmployee);
        add(bottom, BorderLayout.SOUTH);

    }

    private void addListeners(){
        // 打卡按钮的事件
        recordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (recordButton.isSelected()) {// 如果打卡按钮是选中状态
                    // 文本域添加提示信息
                    textArea.append("正在开启摄像头，请稍后.......\n");
                    recordButton.setEnabled(false);// 打卡按钮不可用
                    recordButton.setText("关闭摄像头");// 更改打卡按钮的文本
                    // 创建启动摄像头的临时线程
                    Thread cameraThread = new Thread() {
                        public void run() {
                            // 如果摄像头可以正常开启
                            if (CameraService.startCamera()) {
                                textArea.append("请面向摄像头打卡。\n");// 添加提示
                                recordButton.setEnabled(true);// 打卡按钮可用
                                // 获取摄像头画面面板
                                JPanel cameraPanel = CameraService.getCameraPanel();
                                // 设置面板的坐标与宽高
                                cameraPanel.setBounds(286, 16, 320, 240);
                                panel.add(cameraPanel);// 放到中部面板当中
                            } else {
                                // 弹出提示
                                JOptionPane.showMessageDialog(frame, "未检测到摄像头！");
                                releaseCamera();// 释放摄像头资源
                                return;// 停止方法
                            }
                        }
                    };
                    cameraThread.start();// 启动临时线程
                    dfThread = new DetectFaceThread();// 创建人脸识别线程
                    dfThread.start();// 启动人脸识别线程
                } else {// 如果打卡按钮不是选中状态
                    releaseCamera();// 释放摄像头资源
                }
            }
        });

        addEmployee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Session.admin == null) {
                    LoginDialog ld = new LoginDialog(frame);
                    ld.setVisible(true);
                }
                if (Session.admin != null) {
                    frame.setPanel(new AddUserPanel(frame));
                    releaseCamera();
                }
            }
        });

        yuangong.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Session.admin == null) {
                    LoginDialog ld = new LoginDialog(frame);
                    ld.setVisible(true);
                }
                if (Session.admin != null) {
                    frame.setPanel(new EmployeeManagementPanel(frame));
                    releaseCamera();
                }
            }
        });

        kaoqin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Session.admin == null) {
                    LoginDialog ld = new LoginDialog(frame);
                    ld.setVisible(true);
                }
                if (Session.admin != null) {
                    frame.setPanel(new AttendanceManagementPanel(frame));
                    releaseCamera();
                }
            }
        });
    }

    private class DetectFaceThread extends Thread {
        boolean work = true;

        public void run() {
            while (work){
                if(CameraService.cameraIsOpen()){
                    BufferedImage image = CameraService.getCameraFrame();
                    if(image!=null){
                        String code = FaceService.detectFaceFeature(FaceService.getFaceFeature(image));
                        if(code!=null){
                            User eminformation = HRService.getUser(code);
                            HRService.addClockRecord(eminformation);
                            textArea.append("\n" + DateTimeSet.timeSet() + "\n");
                            textArea.append(eminformation.getName() + " 打卡成功。\n");
                            releaseCamera();
                        }
                    }
                }
            }
        }
        public synchronized void stopThread(){
            work = false;
        }
    }


    private void releaseCamera() {
        CameraService.releaseCamera();
        textArea.append("摄像头已关闭。\n");
        if(dfThread != null){
            dfThread.stopThread();
        }
        recordButton.setText("打卡");
        recordButton.setSelected(false);
        recordButton.setEnabled(true);
    }
}
