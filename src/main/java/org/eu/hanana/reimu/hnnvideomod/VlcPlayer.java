package org.eu.hanana.reimu.hnnvideomod;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VlcPlayer {

    private final VideoDialog frame;
    public final EmbeddedMediaPlayerComponent mediaPlayerComponent;

    public VlcPlayer(Dialog dialog) {
        frame = new VideoDialog(dialog,this);
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

        frame.getVideo().add(mediaPlayerComponent);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                mediaPlayerComponent.release(); // 释放资源
            }
        });
        frame.setVisible(true);
    }

    public void play(String mediaPath) {
        // 获取媒体播放器
        mediaPlayerComponent.mediaPlayer().media().prepare(mediaPath);

        // 可选：在播放时监听事件，例如播放结束
        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                frame.timeChanged(newTime);
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                frame.stop();
            }
            @Override
            public void error(MediaPlayer mediaPlayer) {
                mediaPlayer.controls().stop();
            }
        });

        // 显示窗口
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VlcPlayer player = new VlcPlayer(null);
            // 替换为实际视频文件的路径
            player.play("C:\\Users\\a\\Downloads\\Video\\av12.mp4");
        });
    }
}
