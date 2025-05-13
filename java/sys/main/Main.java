package sys.main;

import sys.frame.MainFrame;
import sys.frame.MainPanel;

public class Main {
    public static void main(String[] args) {
        MainFrame f = new MainFrame();// 创建主窗体
        f.setPanel(new MainPanel(f));// 主窗体加载主面板
        f.setVisible(true);
    }
}
