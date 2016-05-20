package com.heaven.osmPathFinder.UI;

import com.heaven.osmPathFinder.UI.Action.FindPathAction;
import com.heaven.osmPathFinder.UI.Action.FindPathSbSAction;
import com.heaven.osmPathFinder.UI.Action.LoadMapAction;

import javax.swing.*;
import java.awt.*;

/**
 * Created by chenjie3 on 2016/5/20.
 */
public class MainFrame extends JFrame {

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */

    public void init(){
        setTitle("osmPathFinder");
        setPreferredSize(new Dimension(1000, 1000));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createSubComponents();
    }

    public void createSubComponents(){
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.DARK_GRAY);
        this.getContentPane().add(mainPanel);

        DoubleImageLayerPanel mapPanel = new DoubleImageLayerPanel();
        mapPanel.setBackground(Color.blue);
        mapPanel.setPreferredSize(new Dimension(900, 900));
        mainPanel.add(mapPanel);

        JButton loadMapButton = new JButton("load map");
        loadMapButton.setPreferredSize(new Dimension(100, 50));
        mainPanel.add(loadMapButton);
        loadMapButton.addActionListener(new LoadMapAction(mapPanel));

        JButton findPathButton = new JButton("find path");
        findPathButton.setPreferredSize(new Dimension(100, 50));
        mainPanel.add(findPathButton);
        findPathButton.addActionListener(new FindPathAction(mapPanel));

        JButton findPathButtonSbS = new JButton("find path step by step");
        findPathButtonSbS.setPreferredSize(new Dimension(100, 50));
        mainPanel.add(findPathButtonSbS);
        findPathButtonSbS.addActionListener(new FindPathSbSAction(mapPanel));


        //Display the window.
        this.pack();
        this.setLocationRelativeTo(null); // center it. MUST be call *after* pack()
    }
}
