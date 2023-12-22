/*
 * Created by JFormDesigner on Sat Dec 23 00:27:05 CST 2023
 */

package org.eu.hanana.reimu.hnnvideomod.videoplayer;

import java.awt.*;
import javax.swing.*;

/**
 * @author administer
 */
public class Danmaku extends JDialog {
    public Danmaku(Window owner) {
        super(owner);
        setUndecorated(true);  // 设置无边框
        initComponents();
        setBackground(new Color(0, 0, 0, 0));  // 设置背景透明
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        comboBox1 = new JComboBox();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(null);
        contentPane.add(comboBox1);
        comboBox1.setBounds(40, 35, 344, 110);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JComboBox comboBox1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
