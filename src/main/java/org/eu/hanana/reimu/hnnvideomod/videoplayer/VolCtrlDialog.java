/*
 * Created by JFormDesigner on Fri Dec 22 20:45:24 CST 2023
 */

package org.eu.hanana.reimu.hnnvideomod.videoplayer;

import org.eu.hanana.reimu.hnnvideomod.VideoDialog;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

/**
 * @author administer
 */
public class VolCtrlDialog extends JDialog {
    VideoDialog owner;
    public VolCtrlDialog(VideoDialog owner) {
        super(owner);
        initComponents();
        this.owner=owner;
        slider1.setValue(owner.player.mediaPlayerComponent.mediaPlayer().audio().volume());
        label1.setText(String.valueOf(owner.player.mediaPlayerComponent.mediaPlayer().audio().volume()));
        slider1.addChangeListener(this::slider1Change);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                toFront();
            }
        });
        slider1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                dispose();
            }
        });
    }

    private void slider1Change(ChangeEvent e)
    {
        owner.player.mediaPlayerComponent.mediaPlayer().audio().setVolume(slider1.getValue());
        if (owner.player.mediaPlayerComponent.mediaPlayer().audio().volume()==-1)
            label1.setText("视频没有开始\n现在不是调音量的时候");
        else
            label1.setText(String.valueOf(owner.player.mediaPlayerComponent.mediaPlayer().audio().volume()));
    }

    private void slider1VetoableChange(PropertyChangeEvent e)
        throws PropertyVetoException
    {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        slider1 = new JSlider();
        label1 = new JTextArea();

        //======== this ========
        setTitle("\u97f3\u91cf");
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //---- slider1 ----
        slider1.setOrientation(SwingConstants.VERTICAL);
        slider1.setMaximum(200);
        slider1.addVetoableChangeListener(e -> slider1VetoableChange(e));
        contentPane.add(slider1, BorderLayout.CENTER);

        //---- label1 ----
        label1.setEditable(false);
        label1.setText("grtg");
        label1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        contentPane.add(label1, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JSlider slider1;
    private JTextArea label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
