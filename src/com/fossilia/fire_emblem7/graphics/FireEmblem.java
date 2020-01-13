package com.fossilia.fire_emblem7.graphics;

//import java.awt.*;
import com.fossilia.fire_emblem7.Global;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import java.awt.MouseInfo;
//import java.util.*;
import javax.swing.Timer;
import javax.imageio.ImageIO;
//import java.awt.geom.Point2D;


public class FireEmblem extends JFrame implements ActionListener, Global {
    Timer myTimer;
    GamePanel game;

    public FireEmblem() {
        super("Fire Emblem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(240*3+6,160*3+29);

        myTimer = new Timer(10, this);  // trigger every 10 ms

        ImageIcon img = new ImageIcon("images/icon.jpg");
        setIconImage(img.getImage());

        game = new GamePanel(this);
        add(game);

        setResizable(false);
        setVisible(true);
    }

    public void start(){
        myTimer.start();
    }

    public void actionPerformed(ActionEvent evt){
        game.move();
        game.repaint();
    }
}