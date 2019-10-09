/*
 * Created by JFormDesigner on Fri Jun 14 16:40:57 CST 2019
 */

package frame;

import utils.Base64Utils;
import utils.ZipUtils;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

/**
 * @author jdassd
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        initComponents();
    }

    private void button1MouseClicked(MouseEvent e) {
        //创建源文件按钮
        File fileTem = new File("");
        File file = new File(fileTem.getAbsolutePath()+ File.separator +"源文件.txt");
        //JOptionPane.showMessageDialog(null,file.getAbsolutePath(),"位置",JOptionPane.PLAIN_MESSAGE);
        if(file.exists()){
            JOptionPane.showMessageDialog(null,"源文件已存在！","提示",JOptionPane.PLAIN_MESSAGE);
        }else{
            //JOptionPane.showMessageDialog(null,"文件不存在","位置",JOptionPane.PLAIN_MESSAGE);
            try {
                boolean flag = file.createNewFile();
                if(!flag){
                    JOptionPane.showMessageDialog(null,"无法创建源文件！请用管理员模式运行！或检查磁盘空间是否足够！","错误",JOptionPane.ERROR_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null,"源文件创建成功！","提示",JOptionPane.PLAIN_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,"出现异常，请联系作者，错误码：MainFrame-1","错误",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void button2MouseClicked(MouseEvent e) {
        //生成随机文件按钮
        File fileTem = new File("");
        File file = new File(fileTem.getAbsolutePath()+ File.separator +"link");
        if(file.exists()){
            if(file.isDirectory()){
                File file1 = new File(file.getAbsolutePath() + "订阅地址.txt");
                if(file1.exists()){
                    file1.delete();
                }
                for(int i = 1 ; i <= 2000 ; i++){
                    File fileTemp = new File(file.getAbsolutePath() + File.separator + i + "-" + UUID.randomUUID() + ".txt");
                    try {
                        fileTemp.createNewFile();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,"无法创建随机文件！请用管理员模式运行！或检查磁盘空间是否足够！","错误",JOptionPane.ERROR_MESSAGE);
                        break;
                    }
                }
                //这里顺带生成订阅连接地址的txt文件
                StringBuffer sb = new StringBuffer();
                File[] files = file.listFiles();
                for(int i = 0 ; i < files.length ; i++){
                    if(files[i].isDirectory()){

                    }else{
                        String str = "http://www.ymjlb.club/link/" + files[i].getName()+"\r\n";
                        sb.append(str);
                    }
                }
                try {
                    file1.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file1);
                    fos.write(sb.toString().getBytes());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,"无法创建订阅地址.txt文件！或检查磁盘空间是否足够！","错误",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                file.delete();
                button2MouseClicked(e);
            }
        }else{
            file.mkdir();
            button2MouseClicked(e);
        }
    }

    private void button3MouseClicked(MouseEvent e) {
        //根据源文件加密所有随机文件并打包
        File fileTem = new File("");
        File srcFile = new File(fileTem.getAbsolutePath()+ File.separator +"源文件.txt");
        if(srcFile.exists()) {
            String base64 = "";
            try {
                base64 = Base64Utils.encodeBase64File(srcFile.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "出现异常，请联系作者，错误码：MainFrame-2", "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
            File fileDirectory = new File(fileTem.getAbsolutePath() + File.separator + "link");
            if (fileDirectory.exists()) {
                if (fileDirectory.isDirectory()) {
                    File zip = new File(fileDirectory.getAbsolutePath() + "订阅压缩包.zip");
                    if (zip.exists()) {
                        zip.delete();
                    }
                    File[] files = fileDirectory.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()) {

                        } else {
                            try {
                                FileOutputStream fos = new FileOutputStream(files[i]);
                                fos.write(replace(base64).getBytes());
                            } catch (FileNotFoundException ex) {
                                JOptionPane.showMessageDialog(null, "出现异常，请联系作者，错误码：MainFrame-3", "错误", JOptionPane.ERROR_MESSAGE);
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(null, "请用管理员方式运行程序！", "错误", JOptionPane.ERROR_MESSAGE);
                                JOptionPane.showMessageDialog(null, "出现异常，请联系作者，错误码：MainFrame-4", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    try {
                        ZipUtils.doCompress(fileDirectory, zip);
                        JOptionPane.showMessageDialog(null,"成功！","提示",JOptionPane.PLAIN_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "出现异常，请联系作者，错误码：MainFrame-5", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    fileDirectory.delete();
                    button3MouseClicked(e);
                }
            } else {
                JOptionPane.showMessageDialog(null, "请先生成随机文件！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "请先生成源文件！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 去除回车和换行符和空格
     * 实践证明，使用此方法会导致苹果设备订阅失败
     * 于是不使用此方法
     * @param str
     * @return
     */
    public static String replace(String str) {
        String destination = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            destination = m.replaceAll("");
        }
        return destination;
    }

    /**
     * 根据复制文件加密所有随机文件并打包
     * @param e
     */
    private void button4MouseClicked(MouseEvent e) {
        File fileTem = new File("");
        File srcFile = new File(fileTem.getAbsolutePath()+ File.separator +"复制功能源文件.txt");
        if(srcFile.exists()) {
            String str = "";
            try {
                FileInputStream is = new FileInputStream(srcFile);
                byte[] bytes = new byte[(int) srcFile.length()];
                is.read(bytes);
                str = new String(bytes);
            }catch (Exception ex){
                JOptionPane.showMessageDialog(null,"无法读取复制功能文件","错误",JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
            File fileDirectory = new File(fileTem.getAbsolutePath() + File.separator + "link");
            if (fileDirectory.exists()) {
                if (fileDirectory.isDirectory()) {
                    File zip = new File(fileDirectory.getAbsolutePath() + "订阅压缩包.zip");
                    if (zip.exists()) {
                        zip.delete();
                    }
                    File[] files = fileDirectory.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()) {

                        } else {
                            try {
                                FileOutputStream fos = new FileOutputStream(files[i]);
                                fos.write(str.getBytes());
                            } catch (FileNotFoundException ex) {
                                JOptionPane.showMessageDialog(null, "出现异常，请联系作者，错误码：MainFrame-2-3", "错误", JOptionPane.ERROR_MESSAGE);
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(null, "请用管理员方式运行程序！", "错误", JOptionPane.ERROR_MESSAGE);
                                JOptionPane.showMessageDialog(null, "出现异常，请联系作者，错误码：MainFrame-2-4", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    try {
                        ZipUtils.doCompress(fileDirectory, zip);
                        JOptionPane.showMessageDialog(null,"成功！","提示",JOptionPane.PLAIN_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "出现异常，请联系作者，错误码：MainFrame-2-5", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    fileDirectory.delete();
                    button3MouseClicked(e);
                }
            } else {
                JOptionPane.showMessageDialog(null, "请先生成随机文件！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "请先生成源文件！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        button1 = new JButton();
        button2 = new JButton();
        label1 = new JLabel();
        label2 = new JLabel();
        button3 = new JButton();
        label4 = new JLabel();
        button4 = new JButton();

        //======== this ========
        setVisible(true);
        setMinimumSize(new Dimension(625, 395));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("SSR\u94fe\u63a5\u7ba1\u7406\u5de5\u5177-Gsssrvip");
        setAutoRequestFocus(false);
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- button1 ----
        button1.setText("\u521b\u5efa\u6e90\u6587\u4ef6");
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                button1MouseClicked(e);
            }
        });
        contentPane.add(button1);
        button1.setBounds(330, 35, 110, button1.getPreferredSize().height);

        //---- button2 ----
        button2.setText("\u751f\u6210\u968f\u673a\u6587\u4ef6");
        button2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                button2MouseClicked(e);
            }
        });
        contentPane.add(button2);
        button2.setBounds(new Rectangle(new Point(330, 125), button2.getPreferredSize()));

        //---- label1 ----
        label1.setText("\u6e90\u6587\u4ef6\u5373SSR\u94fe\u63a5\u6587\u4ef6\uff0c\u4e00\u822c\u53ea\u7528\u4e00\u6b21\u6b64\u529f\u80fd");
        contentPane.add(label1);
        label1.setBounds(new Rectangle(new Point(265, 85), label1.getPreferredSize()));

        //---- label2 ----
        label2.setText("\u6700\u597d\u53ea\u7528\u4e00\u6b21\uff0c\u4ee5\u540e\u4e0d\u8981\u518d\u7528\uff0c\u9ed8\u8ba4\u751f\u62102000\u4e2a\u6587\u4ef6\uff0c\u968f\u673a\u6587\u4ef6\u547d\u540d\u683c\u5f0f\u4e3a\uff0c\u968f\u673aUUID+\u7528\u6237\u7f16\u53f7");
        contentPane.add(label2);
        label2.setBounds(new Rectangle(new Point(70, 175), label2.getPreferredSize()));

        //---- button3 ----
        button3.setText("\u6839\u636e\u6e90\u6587\u4ef6\u52a0\u5bc6\u6240\u6709\u968f\u673a\u6587\u4ef6\u5e76\u6253\u5305");
        button3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                button3MouseClicked(e);
            }
        });
        contentPane.add(button3);
        button3.setBounds(new Rectangle(new Point(130, 230), button3.getPreferredSize()));

        //---- label4 ----
        label4.setText("\u6839\u636e\u6e90\u6587\u4ef6\u751f\u6210BASE64\u7f16\u7801\uff0c\u5b58\u5165\u6240\u6709\u751f\u6210\u7684\u968f\u673a\u6587\u4ef6\u91cc\uff0c\u5e76\u4e14\u6253\u5305\u6240\u6709\u968f\u673a\u6587\u4ef6\uff08\u8be5\u529f\u80fd\u82f9\u679c\u8ba2\u9605\u4f1a\u51fa\u73b0\u95ee\u9898\uff09");
        contentPane.add(label4);
        label4.setBounds(new Rectangle(new Point(40, 285), label4.getPreferredSize()));

        //---- button4 ----
        button4.setText("\u6839\u636e\u590d\u5236\u6587\u4ef6\u52a0\u5bc6\u6240\u6709\u968f\u673a\u6587\u4ef6\u5e76\u6253\u5305");
        button4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                button4MouseClicked(e);
            }
        });
        contentPane.add(button4);
        button4.setBounds(new Rectangle(new Point(405, 230), button4.getPreferredSize()));

        contentPane.setPreferredSize(new Dimension(860, 335));
        setSize(860, 335);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JButton button1;
    private JButton button2;
    private JLabel label1;
    private JLabel label2;
    private JButton button3;
    private JLabel label4;
    private JButton button4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
