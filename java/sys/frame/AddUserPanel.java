package sys.frame;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.arcsoft.face.FaceFeature;
import sys.Service.CameraService;
import sys.Service.FaceService;
import sys.Service.HRService;
import sys.Service.UserImageService;
import sys.people.User;
import sys.tool.Sex;
import sys.Session.Session;

/**
 * 添加新员工面板
 */
public class AddUserPanel extends JPanel {
    private MainFrame parent;
    private JLabel message;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JRadioButton maleRadio;
    private JRadioButton femaleRadio;
    private JButton submit;
    private JButton back;
    private JButton confirmInfo; // 确认信息按钮
    private JPanel center;
    private JPanel infoPanel; // 员工信息输入面板
    private JPanel cameraPanelContainer; // 摄像头面板容器

    private String employeeName;
    private Sex gender;
    private String phone;
    private String email;

    public AddUserPanel(MainFrame parent) {
        this.parent = parent;
        init();
        addListener();
    }

    private void init() {
        parent.setTitle("录入新员工");
        setLayout(new BorderLayout());

        // 信息输入面板
        infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JLabel nameLabel = new JLabel("员工姓名:", JLabel.LEFT);
        nameField = new JTextField(15);

        JLabel genderLabel = new JLabel("性别:", JLabel.LEFT);
        JPanel genderPanel = new JPanel();
        maleRadio = new JRadioButton(Sex.MALE.getSex(), true); // 使用枚举的显示名称
        femaleRadio = new JRadioButton(Sex.FEMALE.getSex());
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);

        JLabel phoneLabel = new JLabel("电话号码:", JLabel.LEFT);
        phoneField = new JTextField(15);

        JLabel emailLabel = new JLabel("电子邮箱:", JLabel.LEFT);
        emailField = new JTextField(15);

        infoPanel.add(nameLabel);
        infoPanel.add(nameField);
        infoPanel.add(genderLabel);
        infoPanel.add(genderPanel);
        infoPanel.add(phoneLabel);
        infoPanel.add(phoneField);
        infoPanel.add(emailLabel);
        infoPanel.add(emailField);

        confirmInfo = new JButton("确认信息");
        infoPanel.add(new JLabel()); // 空标签占位
        infoPanel.add(confirmInfo);

        // 中部面板（初始显示信息输入）
        center = new JPanel(new BorderLayout());
        center.add(infoPanel, BorderLayout.CENTER);

        // 摄像头面板容器（初始为空，确认信息后显示）
        cameraPanelContainer = new JPanel();
        cameraPanelContainer.setLayout(null);

        message = new JLabel("请先填写员工信息", SwingConstants.CENTER);
        message.setFont(new Font("黑体", Font.BOLD, 30));
        center.add(message, BorderLayout.NORTH);

        // 底部按钮面板
        JPanel bottom = new JPanel();
        back = new JButton("返回");
        submit = new JButton("拍照并录入");
        submit.setEnabled(false); // 初始不可用
        bottom.add(back);
        bottom.add(submit);

        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addListener() {
        // 确认信息按钮事件
        confirmInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                employeeName = nameField.getText().trim();
                if (maleRadio.isSelected()) {
                    gender = Sex.MALE;
                } else {
                    gender = Sex.FEMALE;
                }
                phone = phoneField.getText().trim();
                email = emailField.getText().trim();

                if (employeeName.isEmpty()) {
                    JOptionPane.showMessageDialog(parent, "员工姓名不能为空！");
                    return;
                }

                if (!phone.matches("\\d{11}")) {
                    JOptionPane.showMessageDialog(parent, "请输入有效的11位手机号码！");
                    return;
                }

                if (!email.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
                    JOptionPane.showMessageDialog(parent, "请输入有效的电子邮箱地址！");
                    return;
                }

                // 验证通过，切换到摄像头界面
                infoPanel.setVisible(false);
                center.remove(infoPanel);

                message.setText("正在打开摄像头......");

                // 启动摄像头线程
                Thread cameraThread = new Thread() {
                    public void run() {
                        if (CameraService.startCamera()) {
                            message.setText("请正面面向摄像头");
                            JPanel cameraPanel = CameraService.getCameraPanel();
                            // 设置摄像头面板的位置和大小与黑色背景面板一致
                            cameraPanel.setBounds(150, 75, 320, 240);
                            cameraPanelContainer.add(cameraPanel);
                            center.add(cameraPanelContainer, BorderLayout.CENTER);
                            submit.setEnabled(true); // 启用拍照按钮
                        } else {
                            JOptionPane.showMessageDialog(parent, "未检测到摄像头！");
                            back.doClick();
                        }
                    }
                };
                cameraThread.start();
            }
        });

        // 提交按钮事件
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!CameraService.cameraIsOpen()) {
                    JOptionPane.showMessageDialog(parent, "摄像头尚未开启，请稍后。");
                    return;
                }

                BufferedImage image = CameraService.getCameraFrame();
                FaceFeature ff = FaceService.getFaceFeature(image);

                if (ff == null) {
                    JOptionPane.showMessageDialog(parent, "未检测到有效人脸信息");
                    return;
                }

                // 创建员工对象并保存所有信息
                User eminformation = HRService.addUser(employeeName, gender, phone, email, image);

                UserImageService.saveFaceImage(image, eminformation.getCode());
                Session.FACE_FEATURE_HASH_MAP.put(eminformation.getCode(), ff);

                JOptionPane.showMessageDialog(parent,
                        "员工添加成功！\n姓名: " + employeeName +
                                "\n工号: " + eminformation.getCode() +
                                "\n电话: " + phone);

                back.doClick();
            }
        });

        // 返回按钮事件
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CameraService.releaseCamera();
                parent.setPanel(new EmployeeManagementPanel(parent));
            }
        });
    }
}