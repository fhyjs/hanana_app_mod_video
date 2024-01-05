/*
 * Created by JFormDesigner on Fri Dec 15 22:46:46 CST 2023
 */

package org.eu.hanana.reimu.hnnvideomod;

import org.eu.hanana.reimu.hnnvideomod.videoplayer.*;
import uk.co.caprica.vlcj.player.embedded.OverlayApi;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author administer
 */
public class VideoDialog extends JDialog {
    public final VlcPlayer player;
    public int huiLastTime;
    public String danmakuStr;
    public IDanmaku danmaku;

    public VideoDialog(JFrame owner, VlcPlayer player,String danmakuStr) {
        super(owner);
        // 设置无边框
        //setUndecorated(true);
        initComponents();
        player.videoComponent = getVideo().add(player.mediaPlayerComponent,99);
        getVideo().remove(danmakuPanel);
        getVideo().add(danmakuPanel,50);
        getVideo().remove(hud);
        getVideo().add(hud,0);
        play.setText("播放");
        this.player=player;
        slider1.setValue(0);
        slider1.setMaximum(1000000000);
        dimension=getSize();
        //setResizable(false);
        setTitle("VLC视频播放器|LWJGL3弹幕渲染");
        this.danmakuStr=danmakuStr;


        danmaku=new GlDanmaku(this);
        if (danmaku.getWindow()!=null)
            danmaku.getWindow().setVisible(false);
        player.mediaPlayerComponent.mediaPlayer().overlay().set(danmaku.getWindow());

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                new Thread(()->{
                    while (true){
                        if (player.videoComponent!=null) {
                            init();
                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }).start();

            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                player.mediaPlayerComponent.mediaPlayer().overlay().enable(true);
                btnDanmaku.setText("弹幕:开");
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

    public JPanel getDanmakuPanel() {
        return danmakuPanel;
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
    public JLayeredPane getVideo() {
        return video;
    }
    public void setWSize(Dimension dimension){
        this.dimension=dimension;
        super.setSize(dimension);
    }
    private void playMouseClicked(MouseEvent e) {
        if (!danmaku.isReady()||!player.ready) {
            JOptionPane.showMessageDialog(this,"播放器没有加载完成!");
            return;
        }
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
        danmaku.timeChanged(newTime);
    }
    public void stop() {
        play.setText("播放");
    }
    private void slider1MouseDragged(MouseEvent e) {
        player.mediaPlayerComponent.mediaPlayer().controls().setTime((long) (slider1.getValue()/(float)slider1.getMaximum()*player.mediaPlayerComponent.mediaPlayer().media().info().duration()));
        danmaku.onSeek();
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

    public JPanel getFullvideo() {
        return fullvideo;
    }

    public void init() {
        getDanmakuPanel().setLocation(0,0);
        getDanmakuPanel().setSize(getFullvideo().getSize());
        player.videoComponent.setSize(fullvideo.getSize());
        player.mediaPlayerComponent.videoSurfaceComponent().setSize(player.videoComponent.getWidth()-100,player.videoComponent.getHeight()-100);
        hud.setSize((int) player.mediaPlayerComponent.getSize().getWidth(), (int) (player.mediaPlayerComponent.getSize().getHeight()*0.1));
        hud.setLocation(0,player.mediaPlayerComponent.getHeight()-hud.getHeight());
        danmaku.reSize();
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

    private void btnspeed(ActionEvent e) {
        JDialog dialog = new SpeedCtrlDialog(this);
        dialog.setVisible(true);
    }

    private void btnDanmaku(ActionEvent e) {
        if (player.mediaPlayerComponent.mediaPlayer().overlay().enabled()){
            player.mediaPlayerComponent.mediaPlayer().overlay().enable(false);
            danmaku.disable();
            btnDanmaku.setText("弹幕:关");
            getWindowFocusListeners();
        }else {
            player.mediaPlayerComponent.mediaPlayer().overlay().enable(true);
            danmaku.enable();
            btnDanmaku.setText("弹幕:开");

        }
    }

    private void aboutBtn(ActionEvent e) {
        if(danmaku instanceof GlDanmaku){
            ((GlDanmaku) danmaku).about();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menuItem2 = new JMenuItem();
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        fullvideo = new JPanel();
        video = new JLayeredPane();
        hud = new JPanel();
        btnVol = new JButton();
        btnspeed = new JButton();
        btnDanmaku = new JButton();
        button6 = new JButton();
        danmakuPanel = new JPanel();
        panel2 = new JPanel();
        slider1 = new JSlider();
        panel1 = new JPanel();
        play = new JButton();
        button2 = new JButton();
        button3 = new JButton();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText("\u89c6\u9891\u64ad\u653e\u5668");

                //---- menuItem2 ----
                menuItem2.setText("\u5173\u4e8e");
                menuItem2.addActionListener(e -> aboutBtn(e));
                menu1.add(menuItem2);
            }
            menuBar1.add(menu1);
        }
        setJMenuBar(menuBar1);

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

                        //======== hud ========
                        {
                            hud.setLayout(new GridLayout(1, 4));

                            //---- btnVol ----
                            btnVol.setText("\u97f3\u91cf");
                            btnVol.addActionListener(e -> btnVol(e));
                            hud.add(btnVol);

                            //---- btnspeed ----
                            btnspeed.setText("\u500d\u901f");
                            btnspeed.addActionListener(e -> btnspeed(e));
                            hud.add(btnspeed);

                            //---- btnDanmaku ----
                            btnDanmaku.setText("\u5f39\u5e55");
                            btnDanmaku.addActionListener(e -> btnDanmaku(e));
                            hud.add(btnDanmaku);

                            //---- button6 ----
                            button6.setText("text");
                            hud.add(button6);
                        }
                        video.add(hud, JLayeredPane.DEFAULT_LAYER);
                        hud.setBounds(0, 0, 375, 55);

                        //======== danmakuPanel ========
                        {
                            danmakuPanel.setLayout(new GridLayout(1, 1));
                        }
                        video.add(danmakuPanel, JLayeredPane.DEFAULT_LAYER);
                        danmakuPanel.setBounds(0, -5, 375, 240);
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
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuItem2;
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel fullvideo;
    private JLayeredPane video;
    private JPanel hud;
    private JButton btnVol;
    private JButton btnspeed;
    private JButton btnDanmaku;
    private JButton button6;
    private JPanel danmakuPanel;
    private JPanel panel2;
    private JSlider slider1;
    private JPanel panel1;
    private JButton play;
    private JButton button2;
    private JButton button3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
