//*****************************************************************************
//* Element of MyOpenLab Library                                              *
//*                                                                           *
//* Copyright (C) 2004  Carmelo Salafia (cswi@gmx.de)                         *
//*                                                                           *
//* This library is free software; you can redistribute it and/or modify      *
//* it under the terms of the GNU Lesser General Public License as published  *
//* by the Free Software Foundation; either version 2.1 of the License,       *
//* or (at your option) any later version.                                    *
//* http://www.gnu.org/licenses/lgpl.html                                     *
//*                                                                           *
//* This library is distributed in the hope that it will be useful,           *
//* but WITHOUTANY WARRANTY; without even the implied warranty of             *
//* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                      *
//* See the GNU Lesser General Public License for more details.               *
//*                                                                           *
//* You should have received a copy of the GNU Lesser General Public License  *
//* along with this library; if not, write to the Free Software Foundation,   *
//* Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA                  *
//*****************************************************************************

import VisualLogic.*;
import VisualLogic.variables.*;
import tools.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;



public class Delay extends MainFlow
{
  private Image image;
  public VSFlowInfo in;
  public VSFlowInfo out= new VSFlowInfo();
  private Color color=Color.BLACK;
  public boolean started=false;
  private VSObject vv=null;
  long time1=0;
  long time2=0;



  public void onDispose()
  {
    if (image!=null)
    {
      image.flush();
      image=null;
    }

  }


  public int getValue()
  {
    int newValue=0;
    try
    {
      newValue=Integer.valueOf(variable.getValue());
    } catch(Exception ex)
    {
      variable.setValue("0");
    }
    return newValue;
  }

  public void paint(java.awt.Graphics g)
  {
  if (element!=null)
     {
        Rectangle bounds=element.jGetBounds();
        Graphics2D g2 = (Graphics2D) g;

        g2.setFont(font);

        int mitteX=bounds.x+(bounds.width)/2;
        int mitteY=bounds.y+(bounds.height)/2;

        int distanceY=10;

        g2.setColor(new Color(204,204,255));
        g2.fillRect(bounds.x,mitteY-distanceY,bounds.width,2*distanceY);
        g2.setColor(Color.BLACK);
        g2.drawRect(bounds.x,mitteY-distanceY,bounds.width,2*distanceY);

        String caption=" wait("+getValue()+")ms";

        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(caption,g2);

        g2.setColor(Color.BLACK);
        g.drawString(caption,mitteX-(int)(r.getWidth()/2),(int)(mitteY+fm.getHeight()/2)-3);
        g.drawImage(image,10,16,null);

     }    //drawImageCentred(g,image);
    
        super.paint(g);
  }

  public void init()
  {
    standardWidth=130;
    width=standardWidth;
    height=40;
    toInclude="----wait()ms";

    initPins(1,0,1,0);
    setSize(width,height);
    element.jSetInnerBorderVisibility(false);

    image=element.jLoadImage(element.jGetSourcePath()+"image.png");

    
    element.jInitPins();
    
    setPin(1,ExternalIF.C_FLOWINFO,element.PIN_OUTPUT);
    setPin(0,ExternalIF.C_FLOWINFO,element.PIN_INPUT);
    
    variable.setValue("500");
    setName("#FLOWCHART_ProcessDelay#");

  }
  
  public void xOnInit()
  {
    super.xOnInit();

  }
  
  public void start()
  {
    started=false;
    element.jNotifyMeForClock();
  }
  public void stop()
  {

  }
  
  public boolean isStarted()
  {
    return started;
  }
  
  public void setStarted()
  {
    started=true;
  }

  public void reset()
  {
    out.copyValueFrom(in);
    element.notifyPin(1);

  }


  public void elementActionPerformed(ElementActionEvent evt)
  {
    if (evt.getSourcePinIndex()==0)
    {
      started=true;
      time1 = System.nanoTime();
    }
  }
  
  
  public void processClock()
  {
    if (started)
    {
      time2 = System.nanoTime();
      long diff=time2-time1;
      if (diff>=getValue()*1000000)
      {
        reset();
        started=false;
      }
    }
  }



  public void initInputPins()
  {
    in=(VSFlowInfo)element.getPinInputReference(0);
    
    if (in==null) in= new VSFlowInfo();
  }

  public void initOutputPins()
  {
    element.setPinOutputReference(1,out);
  }
  
  
}


