/*
 * Created by JFormDesigner on Fri Dec 15 22:46:46 CST 2023
 */

package org.eu.hanana.reimu.hnnvideomod;

import org.eu.hanana.reimu.hnnvideomod.videoplayer.VolCtrlDialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author administer
 */
public class VideoDialog extends JDialog {
    public final VlcPlayer player;
    public int huiLastTime;

    public VideoDialog(JFrame owner, VlcPlayer player) {
        super(owner);
        // 设置无边框
        //setUndecorated(true);
        initComponents();
        play.setText("播放");
        this.player=player;
        slider1.setValue(0);
        slider1.setMaximum(1000000000);
        dimension=getSize();
        //setResizable(false);
        setTitle("视频播放器(网页后退关闭)");
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                init();
            }
        });
        Thread s1ticker = new Thread(() -> {
            try {
                s1Ticker();
            } catch (InterruptedException ignored) {
            }
        });
        s1ticker.start();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                s1ticker.interrupt();
            }
        });
        player.mediaPlayerComponent.videoSurfaceComponent().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                showHud();
            }
        });
        player.mediaPlayerComponent.videoSurfaceComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                showHud();
            }
        });
    }
    private void s1Ticker() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()){
            if (huiLastTime>0) huiLastTime--;
            if (huiLastTime>0){
                hud.setVisible(true);
                init();
                validate();
            }else {
                hud.setVisible(false);

                validate();
            }
            Thread.sleep(1000);
        }
    }
    public JPanel getVideo() {
        return video;
    }
    public void setWSize(Dimension dimension){
        this.dimension=dimension;
        super.setSize(dimension);
    }
    private void playMouseClicked(MouseEvent e) {
        if (!player.mediaPlayerComponent.mediaPlayer().status().isPlaying()) {
            player.mediaPlayerComponent.mediaPlayer().controls().play();
            play.setText("暂停");
        }else {
            player.mediaPlayerComponent.mediaPlayer().controls().pause();
            play.setText("播放");
        }
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        button3.setText(full?"退出全屏":"全屏");
    }

    private void stopMouseClicked(MouseEvent e) {
        player.mediaPlayerComponent.mediaPlayer().controls().stop();
        play.setText("播放");
    }
    public void timeChanged(long newTime) {
        slider1.setValue((int) ((float)newTime/(float) player.mediaPlayerComponent.mediaPlayer().media().info().duration()*slider1.getMaximum()));
    }
    public void stop() {
        play.setText("播放");
    }
    private void slider1MouseDragged(MouseEvent e) {
        player.mediaPlayerComponent.mediaPlayer().controls().setTime((long) (slider1.getValue()/(float)slider1.getMaximum()*player.mediaPlayerComponent.mediaPlayer().media().info().duration()));
    }
    boolean full=false;
    Dimension dimension;
    private void fullMouseClicked(MouseEvent e) {
        // 获取屏幕大小
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();

        // 获取当前鼠标所在的屏幕
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        GraphicsDevice currentDevice = null;

        for (GraphicsDevice device : devices) {
            GraphicsConfiguration config = device.getDefaultConfiguration();
            Rectangle bounds = config.getBounds();
            if (bounds.contains(mouseLocation)) {
                currentDevice = device;
                break;
            }
        }
        if (!full) {
            if (currentDevice != null) {
                full = true;
                setSize(currentDevice.getDefaultConfiguration().getBounds().getSize());
                setLocation(0, 0);
                setAlwaysOnTop(true);
            }
        }else {
            full=false;
            setSize(dimension);
        }
        init();
    }
    public void init() {
        player.videoComponent.setSize(fullvideo.getSize());
        player.mediaPlayerComponent.videoSurfaceComponent().setSize(player.videoComponent.getSize());
        hud.setSize((int) player.mediaPlayerComponent.getSize().getWidth(), (int) (player.mediaPlayerComponent.getSize().getHeight()*0.1));
        hud.setLocation(0,player.mediaPlayerComponent.getHeight()-hud.getHeight());
        validate();
    }
    private void showHud(){
        huiLastTime=4;
    }
    private void videoMouseMoved(MouseEvent e) {
        showHud();
    }

    private void videoMouseClicked(MouseEvent e) {
        showHud();
    }

    private void btnVol(ActionEvent e) {
        JDialog dialog = new VolCtrlDialog(this);
        dialog.setVisible(true);
    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        fullvideo = new JPanel();
        video = new JPanel();
        hud = new JPanel();
        btnVol = new JButton();
        button4 = new JButton();
        button5 = new JButton();
        button6 = new JButton();
        panel2 = new JPanel();
        slider1 = new JSlider();
        panel1 = new JPanel();
        play = new JButton();
        button2 = new JButton();
        button3 = new JButton();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridLayout());

                //======== fullvideo ========
                {
                    fullvideo.setLayout(new GridLayout());

                    //======== video ========
                    {
                        video.addMouseMotionListener(new MouseMotionAdapter() {
                            @Override
                            public void mouseMoved(MouseEvent e) {
                                videoMouseMoved(e);
                            }
                        });
                        video.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                videoMouseClicked(e);
                            }
                        });
                        video.setLayout(null);

                        //======== hud ========
                        {
                            hud.setLayout(new GridLayout(1, 4));

                            //---- btnVol ----
                            btnVol.setText("\u97f3\u91cf");
                            btnVol.addActionListener(e -> btnVol(e));
                            hud.add(btnVol);

                            //---- button4 ----
                            button4.setText("text");
                            hud.add(button4);

                            //---- button5 ----
                            button5.setText("text");
                            hud.add(button5);

                            //---- button6 ----
                            button6.setText("text");
                            hud.add(button6);
                        }
                        video.add(hud);
                        hud.setBounds(0, 180, 375, 55);

                        {
                            // compute preferred size
                            Dimension preferredSize = new Dimension();
                            for(int i = 0; i < video.getComponentCount(); i++) {
                                Rectangle bounds = video.getComponent(i).getBounds();
                                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                            }
                            Insets insets = video.getInsets();
                            preferredSize.width += insets.right;
                            preferredSize.height += insets.bottom;
                            video.setMinimumSize(preferredSize);
                            video.setPreferredSize(preferredSize);
                        }
                    }
                    fullvideo.add(video);
                }
                contentPanel.add(fullvideo);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== panel2 ========
            {
                panel2.setLayout(new GridLayout(2, 1));

                //---- slider1 ----
                slider1.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        slider1MouseDragged(e);
                    }
                });
                panel2.add(slider1);

                //======== panel1 ========
                {
                    panel1.setLayout(new GridLayout(1, 2));

                    //---- play ----
                    play.setText("\u64ad\u653e");
                    play.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            playMouseClicked(e);
                        }
                    });
                    panel1.add(play);

                    //---- button2 ----
                    button2.setText("\u505c\u6b62");
                    button2.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            stopMouseClicked(e);
                        }
                    });
                    panel1.add(button2);

                    //---- button3 ----
                    button3.setText("\u5168\u5c4f");
                    button3.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            fullMouseClicked(e);
                        }
                    });
                    panel1.add(button3);
                }
                panel2.add(panel1);
            }
            dialogPane.add(panel2, BorderLayout.PAGE_END);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel fullvideo;
    private JPanel video;
    private JPanel hud;
    private JButton btnVol;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JPanel panel2;
    private JSlider slider1;
    private JPanel panel1;
    private JButton play;
    private JButton button2;
    private JButton button3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
