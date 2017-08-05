/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design.panel;

/**
 *
 * @author rachmad
 */
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class TitleBorder extends JPanel {

  public TitleBorder(String title) {
    super(true);
    this.setLayout(new GridLayout(1, 1, 5, 5));
    TitledBorder titled = new TitledBorder(title);
  }

  public static void main(String s[]) {
    JFrame frame = new JFrame("Borders");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(200, 100);
    frame.setContentPane(new TitleBorder("Title"));
    frame.setVisible(true);
  }
}
