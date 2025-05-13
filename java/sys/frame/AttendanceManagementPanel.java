package sys.frame;

import sys.Session.Session;
import sys.message.work_time;
import sys.Service.HRService;
import sys.tool.DateTimeSet;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;


public class AttendanceManagementPanel extends JPanel{
    //初始
    private MainFrame parent;
    private JToggleButton dayRecordButton;
    private JToggleButton monthRecordButton;
    private JToggleButton workTimeButton;
    private JButton back;// 返回按钮
    private JButton flushD, flushM;// 分别在日报和月报面板中的刷新按钮

    private JPanel panel;
    private CardLayout cardLayout;
    //每日打卡报表
    private JPanel dayRecordPanel;
    private JTextArea textArea;
    private JComboBox<Integer> yearComboBoxD, monthComboBoxD, dayComboBoxD;
    private DefaultComboBoxModel<Integer> yearModelD, monthModelD, dayModelD;
    //每月报表
    private JPanel monthRecordPanel;
    private JTable table;// 月报面板里的表格
    private DefaultTableModel model;
    private JComboBox<Integer> yearComboBoxM, monthComboBoxM;
    // 年、月下拉列表使用的数据模型
    private DefaultComboBoxModel<Integer> yearModelM, monthModelM;
    //工作时间面板
    private JPanel workTimePanel;
    private JTextField hourMS, minuteMS, secondMS;
    private JTextField hourAS, minuteAS, secondAS;
    private JTextField hourME, minuteME, secondME;
    private JTextField hourAE, minuteAE, secondAE;
    private JButton updateWorkTime;

    public AttendanceManagementPanel(MainFrame parent) {
        this.parent = parent;
        init();
        addListener();
    }
    private void init() {
        work_time worktime = Session.worktime;
        parent.setTitle("考勤报表（上午上班时间：" + worktime.getMorningStartTime() + ",上午下班时间：" + worktime.getMorningEndTime() +
                ",下午上班时间：" + worktime.getAfternoonStartTime()  + ",下午下班时间：" + worktime.getAfternoonEndTime() + ")");
        dayRecordButton = new JToggleButton("日报");
        dayRecordButton.setSelected(true);
        monthRecordButton = new JToggleButton("月报");
        workTimeButton = new JToggleButton("作息时间设置");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(dayRecordButton);
        buttonGroup.add(monthRecordButton);
        buttonGroup.add(workTimeButton);

        back = new JButton("返回");
        flushD = new JButton("刷新报表");
        flushM = new JButton("刷新报表");

        ComboBoInit();
        dayRecordInit();
        monthRecordInit();
        workTimeInit();

        cardLayout = new CardLayout();
        panel = new JPanel(cardLayout);
        panel.add("dayRecord", dayRecordPanel);
        panel.add("monthRecord", monthRecordPanel);
        panel.add("workTime", workTimePanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(dayRecordButton);
        buttonPanel.add(monthRecordButton);
        buttonPanel.add(workTimeButton);
        buttonPanel.add(back);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListener() {
        dayRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panel, "dayRecord");
            }
        });

        monthRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panel, "monthRecord");
            }
        });

        workTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panel, "workTime");
            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.setPanel(new MainPanel(parent));
            }
        });

        flushD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDayRecord();
            }
        });

        flushM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        updateWorkTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hMS = hourMS.getText().trim();
                String mMS = minuteMS.getText().trim();
                String sMS = secondMS.getText().trim();
                String hME = hourME.getText().trim();
                String mME = minuteME.getText().trim();
                String sME = secondME.getText().trim();
                String hAS = hourAS.getText().trim();
                String mAS = minuteAS.getText().trim();
                String sAS = secondAS.getText().trim();
                String hAE = hourAE.getText().trim();
                String mAE = minuteAE.getText().trim();
                String sAE = minuteAE.getText().trim();

                boolean check = true;
                String morningStartInput = hMS + ":" + mMS + ":" + sMS;
                String morningEndInput = hME + ":" + mME + ":" + sME;
                String afternoonStartInput = hAS + ":" + mAS + ":" + sAS;
                String afternoonEndInput = hAE + ":" + mAE + ":" + sAE;

                if(!DateTimeSet.checkTimeStr(morningStartInput)){
                    check = false;
                    JOptionPane.showMessageDialog(parent, "上午上班时间的格式不正确");
                }

                if(!DateTimeSet.checkTimeStr(morningEndInput)){
                    check = false;
                    JOptionPane.showMessageDialog(parent, "上午下班时间的格式不正确");
                }

                if (!DateTimeSet.checkTimeStr(afternoonStartInput)){
                    check = false;
                    JOptionPane.showMessageDialog(parent, "下午上班时间的格式不正确");
                }

                if(!DateTimeSet.checkTimeStr(afternoonEndInput)){
                    check = false;
                    JOptionPane.showMessageDialog(parent, "下午下班时间的格式不正确");
                }

                if(check){
                    int confirmation = JOptionPane.showConfirmDialog(parent,
                            "确定做出以下设置？\n上午上班时间：" + morningStartInput + "\n上午下班时间：" + morningEndInput + "\n上班时间：" + afternoonStartInput + "\n下午下班时间：" + afternoonEndInput, "提示！", JOptionPane.YES_NO_OPTION);
                    if (confirmation == JOptionPane.YES_OPTION) {// 如果用户选择确定
                        work_time input = new work_time(morningStartInput, morningEndInput, afternoonStartInput, afternoonEndInput);
                        HRService.updateWorkTime(input);// 更新作息时间
                        // 修改标题
                        parent.setTitle("考勤报表 (上班时间：" + morningStartInput + ",下班时间：" + morningEndInput + ",下午上班时间：" + afternoonStartInput + ",下午下班时间：" + afternoonEndInput + ")");
                    }
                }
            }
        });

        ActionListener dayD_Listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDayRecord();// 更新日报
            }
        };
        dayComboBoxD.addActionListener(dayD_Listener);// 添加监听

        // 日报面板中的年份、月份下拉列表使用的监听对象
        ActionListener yearD_monthD_Listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 删除日期下拉列表使用的监听对象，防止日期改变后自动触发此监听
                dayComboBoxD.removeActionListener(dayD_Listener);
                updateDayModel();// 更新日下拉列表中的天数
                updateDayRecord();// 更新日报
                // 重新为日期下拉列表添加监听对象
                dayComboBoxD.addActionListener(dayD_Listener);
            }
        };

        yearComboBoxD.addActionListener(yearD_monthD_Listener);// 添加监听
        monthComboBoxD.addActionListener(yearD_monthD_Listener);

        // 月报面板中的年份、月份下拉列表使用的监听对象
       /* ActionListener yearM_monthM_Listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //updateMonthRecord();// 更新月报
            }
        };

        yearComboBoxM.addActionListener(yearM_monthM_Listener);// 添加监听
        monthComboBoxM.addActionListener(yearM_monthM_Listener);
*/
    }

    private void workTimeInit() {
        workTimePanel = new JPanel();
        workTimePanel.setLayout(null);

        work_time worktime = Session.worktime;
        String MorningStartTime[] = worktime.getMorningStartTime().split(":");
        String MorningEndTime[] = worktime.getMorningEndTime().split(":");
        String AfternoonStartTime[] = worktime.getAfternoonStartTime().split(":");
        String AfternoonEndTime[] = worktime.getAfternoonEndTime().split(":");

        Font labelFont = new Font("黑体", Font.BOLD, 20);

        JPanel top = new JPanel();

        JLabel startLabel = new JLabel("上午上班时间:");
        startLabel.setFont(labelFont);
        top.add(startLabel);

        hourMS = new JTextField(3);
        hourMS.setText(MorningStartTime[0]);
        top.add(hourMS);

        JLabel colon1 = new JLabel(":");
        colon1.setFont(labelFont);
        top.add(colon1);

        minuteMS = new JTextField(3);
        minuteMS.setText(MorningStartTime[1]);
        top.add(minuteMS);

        JLabel colon2 = new JLabel(":");
        colon2.setFont(labelFont);
        top.add(colon2);

        secondMS = new JTextField(3);
        secondMS.setText(MorningStartTime[2]);
        top.add(secondMS);

        JPanel bottom = new JPanel();

        JLabel endLabel = new JLabel("上午下班时间:");
        endLabel.setFont(labelFont);
        bottom.add(endLabel);

        hourME = new JTextField(3);
        hourME.setText(MorningEndTime[0]);
        bottom.add(hourME);

        JLabel colon3 = new JLabel(":");
        colon3.setFont(labelFont);
        bottom.add(colon3);

        minuteME = new JTextField(3);
        minuteME.setText(MorningEndTime[1]);
        bottom.add(minuteME);

        JLabel colon4 = new JLabel(":");
        colon4.setFont(labelFont);
        bottom.add(colon4);

        secondME = new JTextField(3);
        secondME.setText(MorningEndTime[2]);
        bottom.add(secondME);

        JPanel top1 = new JPanel();// 底部面板

        JLabel startLabel1 = new JLabel("下午上班时间:");
        startLabel.setFont(labelFont);
        top1.add(startLabel1);

        hourAS = new JTextField(3);
        hourAS.setText(AfternoonStartTime[0]);
        top1.add(hourAS);

        JLabel colon5 = new JLabel(":");
        colon5.setFont(labelFont);
        top1.add(colon5);

        minuteAS = new JTextField(3);
        minuteAS.setText(AfternoonStartTime[1]);
        top1.add(minuteAS);

        JLabel colon6 = new JLabel(":");
        colon6.setFont(labelFont);
        top1.add(colon6);

        secondAS = new JTextField(3);
        secondAS.setText(AfternoonStartTime[2]);
        top1.add(secondAS);

        JPanel bottom1 = new JPanel();

        JLabel endLabel1 = new JLabel("下午下班时间:");
        endLabel1.setFont(labelFont);
        bottom1.add(endLabel1);

        hourAE = new JTextField(3);
        hourAE.setText(AfternoonEndTime[0]);
        bottom1.add(hourAE);

        JLabel colon7 = new JLabel(":");
        colon7.setFont(labelFont);
        bottom1.add(colon7);

        minuteAE = new JTextField(3);
        minuteAE.setText(AfternoonEndTime[1]);
        bottom1.add(minuteAE);

        JLabel colon8 = new JLabel(":");
        colon8.setFont(labelFont);
        bottom1.add(colon8);

        secondAE = new JTextField(3);
        secondAE.setText(AfternoonEndTime[2]);
        bottom1.add(secondAE);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 1));
        centerPanel.add(top);
        centerPanel.add(bottom);
        centerPanel.add(top1);
        centerPanel.add(bottom1);

        centerPanel.setBounds(100, 60, 400, 150);
        workTimePanel.add(centerPanel);

        updateWorkTime = new JButton("替换作息时间");
        updateWorkTime.setFont(new Font("黑体", Font.BOLD, 20));
        updateWorkTime.setBounds(220, 320, 170, 55);
        workTimePanel.add(updateWorkTime);
    }

    private void dayRecordInit() {
        textArea = new JTextArea();
        textArea.setEditable(false);// 文本域不可编辑
        textArea.setFont(new Font("宋体", Font.BOLD, 24));
        JScrollPane scroll = new JScrollPane(textArea);// 文本域放到滚动面板中

        dayRecordPanel = new JPanel();
        dayRecordPanel.setLayout(new BorderLayout());// 日报面板采用边界布局
        dayRecordPanel.add(scroll, BorderLayout.CENTER);// 滚动面板在中部显示

        JPanel top = new JPanel();// 顶部面板
        top.setLayout(new FlowLayout());// 采用流布局
        top.add(yearComboBoxD);// 年下拉列表
        top.add(new JLabel("年"));// 文本标签
        top.add(monthComboBoxD);// 月下拉列表
        top.add(new JLabel("月"));
        top.add(dayComboBoxD);// 日下拉列表
        top.add(new JLabel("日"));
        top.add(flushD);// 日报面板的刷新按钮
        dayRecordPanel.add(top, BorderLayout.NORTH);

        updateDayRecord();// 更新日报
    }

    private void monthRecordInit() {
        JPanel top = new JPanel();// 顶部面板
        top.add(yearComboBoxM);// 年下拉列表
        top.add(new JLabel("年"));
        top.add(monthComboBoxM);// 年下拉列表
        top.add(new JLabel("月"));
        top.add(flushM);// 月报面板的刷新按钮

        monthRecordPanel = new JPanel();
        monthRecordPanel.setLayout(new BorderLayout());// 月报面板采用边界布局
        monthRecordPanel.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel();// 表格数据模型
        table = new JTable(model);// 表格采用数模型
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);// 关闭自动调整宽度
        JScrollPane tableScroll = new JScrollPane(table);// 表格放入滚动面板
        monthRecordPanel.add(tableScroll, BorderLayout.CENTER);
    }

    private void ComboBoInit(){
        yearModelD = new DefaultComboBoxModel<>();
        monthModelD = new DefaultComboBoxModel<>();
        dayModelD = new DefaultComboBoxModel<>();
        yearModelM = new DefaultComboBoxModel<>();
        monthModelM = new DefaultComboBoxModel<>();

        // 获取当前时间的年、月、日、时、分、秒数组
        Integer now[] = DateTimeSet.now();

        // 获取当前时间前后十年的年份，添加到年下拉列表的数据模型中
        for (int i = now[0] - 10; i <= now[0] + 10; i++) {
            yearModelD.addElement(i);
            yearModelM.addElement(i);
        }
        yearComboBoxD = new JComboBox<>(yearModelD);// 日报的年下拉列表
        yearComboBoxD.setSelectedItem(now[0]);// 默认选中今年
        yearComboBoxM = new JComboBox<>(yearModelM);// 月报的年下拉列表
        yearComboBoxM.setSelectedItem(now[0]);// 默认选中今年

        // 遍历12个月,，添加到月下拉列表的数据模型中
        for (int i = 1; i <= 12; i++) {
            monthModelD.addElement(i);
            monthModelM.addElement(i);
        }
        monthComboBoxD = new JComboBox<>(monthModelD);// 日报的月下拉列表
        monthComboBoxD.setSelectedItem(now[1]);// 默认选中本月
        monthComboBoxM = new JComboBox<>(monthModelM);// 日报的月下拉列表
        monthComboBoxM.setSelectedItem(now[1]);// 默认选中本月

        updateDayModel();// 更新日下拉列表中的天数
        dayComboBoxD = new JComboBox<>(dayModelD);// 日报的日下拉列表
        dayComboBoxD.setSelectedItem(now[2]);// 默认选中今天
    }

    private void updateDayRecord() {
        // 获取日报面板中选中的年、月、日
        int year = (int) yearComboBoxD.getSelectedItem();
        int month = (int) monthComboBoxD.getSelectedItem();
        int day = (int) dayComboBoxD.getSelectedItem();
        // 获取日报报表
        String report = HRService.getDayReport(year, month, day);
        textArea.setText(report);// 日报报表覆盖到文本域中
    }

    /*private void updateMonthRecord(){
        int year = (int) yearComboBoxM.getSelectedItem();
        int month = (int) monthComboBoxM.getSelectedItem();

        int lastDay = DateTimeSet.getLastDay(year, month);// 此月最大天数

        String tatle[] = new String[lastDay + 1];// 表格列头
        tatle[0] = "员工姓名";// 第一列是员工姓名
        // 后面n为选中月份的每一天日期
        for (int day = 1; day <= lastDay; day++) {
            tatle[day] = year + "年" + month + "月" + day + "日";
        }
        // 获取月报数据
        //String values[][] = HRService.getMonthReport(year, month);
        model.setDataVector(values, tatle);// 将数据和列头放入表格数据模型中
        int columnCount = table.getColumnCount();// 获取表格中的所有列数
        for (int i = 1; i < columnCount; i++) {// 遍历每一列
            // 从第2列开始，没一列都设为100宽度
            table.getColumnModel().getColumn(i).setPreferredWidth(100);
        }
    }
*/
    private void updateDayModel() {
        // 获取年下拉列表选中的值
        int year = (int) yearComboBoxD.getSelectedItem();
        // 获取月下拉列表选中的值
        int month = (int) monthComboBoxD.getSelectedItem();
        // 获取选中月份的最大天数
        int lastDay = DateTimeSet.getLastDay(year, month);
        dayModelD.removeAllElements();// 清除已有元素
        for (int i = 1; i <= lastDay; i++) {
            dayModelD.addElement(i);// 将每一天都添加到日下拉列表数据模型中
        }
    }
}
