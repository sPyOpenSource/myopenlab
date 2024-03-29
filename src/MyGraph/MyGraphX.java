/*
MyOpenLab by Carmelo Salafia www.myopenlab.de
Copyright (C) 2004  Carmelo Salafia cswi@gmx.de
Copyright (C) 2017  Javier Velsquez (javiervelasquez125@gmail.com)
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package MyGraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

/**
 *
 * @author  Homer
 */
public class MyGraphX extends javax.swing.JPanel
{    
    public Image image = null;
    public double alpha = 0;
    public BackGraphXY back= new BackGraphXY(this);
    
    public boolean autoscroll = false;
    public boolean coordinatesVisible = true;
    
    public XAxis xaxis = new XAxis(this, back, 50, 100, 200, 10.0);
    public YAxis yaxis = new YAxis(this, 50, 100, 200, 10.0);
    
    public static final int XAXIS_BOTTOM = 0;    
    public static final int XAXIS_TOP = 1;
    
    public static final int YAXIS_LEFT  = 0;    
    public static final int YAXIS_RIGHT = 1;
           
    public GraphRenderer[] graphRenderer = null;
    
    public boolean xAxisVisible = true;
    public boolean yAxisVisible = true;
    
    public boolean xyAxisVisible = true;
    public int bufferLen = 600;
    
    
    public void setbufferLen(Integer Interval){
     this.bufferLen = Interval;
     back.setAutoScaleInterval(this.bufferLen);
    }
    public Integer getbufferLen(){
      return this.bufferLen;  
    }
    public void setXYAxisVisible(boolean value)
    {
        xyAxisVisible = value;        
    }
 
    
    public void setAutoZoomX(boolean value)
    {
        back.autoScaleX = value;        
        back.init();
    }
    
    public void setAutoZoomY(boolean value)
    {
        back.autoScaleY = value;
        back.init();
    }
    
        
    public Point p1 = new Point(0,0);
    public Point p2 = new Point(0,0);
    
    private void setXAxisTextOrientation()
    {
        xaxis.setTextOrientation(xaxis.BOTTOM);                
        
        xaxis.setStartPoint(p1.x, getHeight() - 5 - xaxis.getHeight());                
        xaxis.setWidth(p2.x);
    }
    
    
    private void setYAxisTextOrientation()
    {
        yaxis.setTextOrientation(yaxis.LEFT);        
        yaxis.setStartPoint(p1.x, p1.y);
        yaxis.setHeight(p2.y - p1.y);
    }
    
    public void generateGraphs(int size)
    {
        if (graphRenderer != null)
        {
            for (GraphRenderer graphRenderer1 : graphRenderer) {
                remove(graphRenderer1);
            }
        }
        
        graphRenderer = new GraphRenderer[size];

        for (int i = 0; i < size; i++)
        {
            graphRenderer[i] = new GraphRenderer(back);
                                           
            this.add(graphRenderer[i], 0);
        }
        init();
    }
    
    public void init()
    {       
                
        xaxis.init();
        yaxis.init();                        
               
        if (xyAxisVisible)
        {
            p1.y = yaxis.getD1();
            p2.y = getHeight() - xaxis.getHeight() - 5;
            if (xaxis.getD1() > yaxis.getWidth())
            {
                p1.x = xaxis.getD1();                        
            } else
            {
                p1.x = yaxis.getWidth();                        
            }

            p2.x = getWidth() - p1.x - xaxis.getD2();
                    
        } else
        {
            p1.x = 0;
            p1.y = 0;
            p2.x = getWidth();
            p2.y = getHeight();            
        }
                            
        setXAxisTextOrientation();
        setYAxisTextOrientation();
        xaxis.init();
        yaxis.init();
                        
        back.setLocation(p1.x, p1.y);
        back.setSize(p2.x, p2.y - p1.y + 1);
        back.setStepXInProzent(xaxis.getStepInProzent());
        back.setStepYInProzent(yaxis.getStepInProzent());
        
        back.minX = xaxis.min;
        back.maxX = xaxis.max;
        
        back.minY = yaxis.min;
        back.maxY = yaxis.max;
        back.init();
        
        if (graphRenderer != null)
        {
            for (GraphRenderer graphRenderer1 : graphRenderer) {
                if (graphRenderer1 != null) {
                    graphRenderer1.setLocation(p1.x, p1.y);
                    graphRenderer1.setSize(p2.x, p2.y - p1.y + 1);
                    graphRenderer1.init();                
                }
            }
        }
        
    }
        
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        if (xyAxisVisible)
        {
            if (xAxisVisible) xaxis.paint(g);
            if (yAxisVisible) yaxis.paint(g);
        }
    }
    
    /** Creates new form MyGraph */
    public MyGraphX()
    {
        initComponents();
                        
        setBackground(Color.WHITE);
                
        xaxis.setTextOrientation(xaxis.BOTTOM);
        xaxis.setFormatString("#0.0");
        xaxis.setStartPoint(50,410);
        xaxis.setWidth(400);
        xaxis.setStepInProzent(10.0);
        xaxis.setMin(-100);
        xaxis.setMax(100);
        xaxis.init();
                
        yaxis.setTextOrientation(yaxis.LEFT);
        yaxis.setFormatString("#000.0");
        yaxis.setStartPoint(50, 10);
        yaxis.setHeight(400);
        yaxis.setStepInProzent(10.0);
        yaxis.setMin(-100);
        yaxis.setMax(100);
        yaxis.init();
                
        add(back);
        
        
        generateGraphs(3);
        init();
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setOpaque(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 437, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 421, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentResized
    {//GEN-HEADEREND:event_formComponentResized
        init();
        updateUI();
    }//GEN-LAST:event_formComponentResized
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
