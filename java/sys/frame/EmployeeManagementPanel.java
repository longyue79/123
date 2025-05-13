package sys.frame;

import sys.people.User;
import sys.Service.HRService;
import sys.Session.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

/**
 * 员工管理面板
 * 
 * @author mingrisoft
 *
 */
public class EmployeeManagementPanel extends JPanel {
    private MainFrame parent;// 主窗体
    private JTable table;// 员工信息表格
    private DefaultTableModel model;// 表格的数据模型
    private JButton add;// 录入新员工按钮
    private JButton delete;// 删除员工按钮
    private JButton back;// 返回按钮

    public EmployeeManagementPanel(MainFrame parent) {
        this.parent = parent;
        init();// 组件初始化
        addListener();// 为组件添加监听
    }

    /**
     * 组件初始化
     */
    private void init() {
        parent.setTitle("员工管理");// 修改标题
        add = new JButton("录入新员工");
        delete = new JButton("删除员工");
        back = new JButton("返回");

        model = new DefaultTableModel();
        String columnName[] = { "员工编号", "员工名称", "性别", "手机号", "邮箱"};// 表格列头
        int count = Session.EMINFORMATIONS.size();// 员工人数
        String value[][] = new String[count][5];// 表格展示的数据

        // 创建员工结合的迭代器
        Iterator<User> it = Session.EMINFORMATIONS.iterator();
        for (int i = 0; it.hasNext(); i++) {// 遍历每个员工，并让i递增
            User eminformation = it.next();// 获取一个员工
            value[i][0] = String.valueOf(eminformation.getId());// 第一列为员工编号
            value[i][1] = eminformation.getName();// 第二列为员工姓名
            value[i][2] = eminformation.getSex().getSex();
            value[i][3] = eminformation.getPhone();
            value[i][4] = eminformation.getMail();
        }

        model.setDataVector(value, columnName);// 把数据和列头放入表格数据模型中

        table = new EmpTable(model);// 采用自定义的员工信息表格类
        JScrollPane scroll = new JScrollPane(table);// 表格放入滚动面板中

        setLayout(new BorderLayout());// 采用边界布局

        if (count == 0) {
            JLabel noEmployeesLabel = new JLabel("当前没有员工信息，请点击“录入新员工”按钮添加。", SwingConstants.CENTER);
            add(noEmployeesLabel, BorderLayout.CENTER);
        } else {
            add(scroll, BorderLayout.CENTER);// 滚动面板放中部
        }

        JPanel bottom = new JPanel();// 底部面板
        bottom.add(add);// 添加底部组件
        bottom.add(delete);
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * 为组件添加监听
     */
    private void addListener() {
        // 录入新员工按钮的事件
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 主窗体切换到添加新员工面板
                parent.setPanel(new AddUserPanel(parent));
            }
        });
        // 删除员工按钮的事件
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectRow = table.getSelectedRow();// 获取表格选中的行索引
                if (selectRow != -1) {// 如果有行被选中
                    // 弹出选择对话框，并记录用户选择
                    int deleteCode = JOptionPane.showConfirmDialog(parent, "确定删除该员工？", "提示！",
                            JOptionPane.YES_NO_OPTION);
                    if (deleteCode == JOptionPane.YES_OPTION) {// 如果用户选择确定
                        // 获取选中的员工编号
                        String id = (String) model.getValueAt(selectRow, 0);
                        HRService.deleteUser(Integer.parseInt(id));// 删除此员工
                        model.removeRow(selectRow);// 表格删除此行
                    }
                }
            }
        });
        // 返回按钮的事件
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 返回按钮的事件
                parent.setPanel(new MainPanel(parent));
            }
        });
    }

    /**
     * 自定义员工信息表格类
     * 
     * @author mingrisoft
     *
     */
    private class EmpTable extends JTable {

        public EmpTable(TableModel dm) {
            super(dm);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 只能单选
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;// 表格不可编辑
        }

        @Override
        public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
            // 获取单元格渲染对象
            DefaultTableCellRenderer cr = (DefaultTableCellRenderer) super.getDefaultRenderer(columnClass);
            // 表格文字居中显示
            cr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            return cr;
        }
    }
}
