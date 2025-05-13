package sys.frame;

import sys.Session.Session;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        Session.init();
        addListener();
        setSize(640, 480);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
    }

    private void addListener() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int closeCode = JOptionPane.showConfirmDialog(MainFrame.this,
                        "是否要退出打卡程序？", "提示!", JOptionPane.YES_NO_OPTION);
                if (closeCode == JOptionPane.YES_OPTION) {
                    Session.dispose();
                    System.exit(0);
                }
            }
        });
    }

    public void setPanel(JPanel panel) {
        Container contentPane = getContentPane();
        contentPane.removeAll();
        contentPane.add(panel);
        contentPane.validate();
    }
}
