/*
 * Created by JFormDesigner on Fri Dec 22 23:18:05 CST 2023
 */

package org.eu.hanana.reimu.hnnvideomod.videoplayer;

import java.awt.event.*;
import org.eu.hanana.reimu.hnnvideomod.VideoDialog;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * @author administer
 */
public class SpeedCtrlDialog extends JDialog {
    VideoDialog owner;
    float rate;
    public SpeedCtrlDialog(VideoDialog owner) {
        super(owner);
        initComponents();
        this.owner=owner;
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                toFront();
            }
        });
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                super.windowLostFocus(e);
                dispose();
            }
        });
        label1.setText(String.valueOf(rate=owner.player.mediaPlayerComponent.mediaPlayer().status().rate()));
    }

    private void up(ActionEvent e) {
        rate+=0.5f;
        owner.player.mediaPlayerComponent.mediaPlayer().controls().setRate(rate);
        label1.setText(String.valueOf(rate=owner.player.mediaPlayerComponent.mediaPlayer().status().rate()));
    }

    private void down(ActionEvent e) {
        rate-=0.5f;
        owner.player.mediaPlayerComponent.mediaPlayer().controls().setRate(rate);
        label1.setText(String.valueOf(rate=owner.player.mediaPlayerComponent.mediaPlayer().status().rate()));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        up = new JButton();
        label1 = new JLabel();
        down = new JButton();

        //======== this ========
        setTitle("\u500d\u901f");
        var contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(3, 0));

        //---- up ----
        up.setText("+");
        up.setFont(up.getFont().deriveFont(up.getFont().getSize() + 4f));
        up.addActionListener(e -> up(e));
        contentPane.add(up);

        //---- label1 ----
        label1.setText("text");
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 4f));
        contentPane.add(label1);

        //---- down ----
        down.setText("-");
        down.setFont(down.getFont().deriveFont(down.getFont().getSize() + 4f));
        down.addActionListener(e -> down(e));
        contentPane.add(down);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JButton up;
    private JLabel label1;
    private JButton down;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
