/*
 * Created by JFormDesigner on Fri Dec 15 22:46:46 CST 2023
 */

package org.eu.hanana.reimu.hnnvideomod;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author administer
 */
public class VideoDialog extends JDialog {
    private final VlcPlayer player;

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
        setResizable(false);
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
    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        video = new JPanel();
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

                //======== video ========
                {
                    video.setLayout(new GridLayout(1, 1));
                }
                contentPanel.add(video);
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
    private JPanel video;
    private JPanel panel2;
    private JSlider slider1;
    private JPanel panel1;
    private JButton play;
    private JButton button2;
    private JButton button3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
