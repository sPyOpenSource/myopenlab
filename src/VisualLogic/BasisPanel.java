/*
MyOpenLab by Carmelo Salafia www.myopenlab.de
Copyright (C) 2004  Carmelo Salafia cswi@gmx.de

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

package VisualLogic;

import java.awt.*;

/**
 *
 * @author Homer
 * sorgt dafuer das das VMObject dargestellt wird!
 */

public class BasisPanel extends javax.swing.JPanel
{
    public VMObject vmObject;
        
    public BasisPanel(VMObject vmObject)
    {
        this.vmObject=vmObject;
        this.setPreferredSize(new Dimension(vmObject.getWidth(), vmObject.getHeight()));
        this.setBackground(Color.GRAY);
            
        setDoubleBuffered(false);
        
        addMouseListener(vmObject);
        addMouseMotionListener(vmObject);
        
        this.setLayout(null);
        this.add(vmObject);
        vmObject.setLocation(0,0);
        vmObject.setLayout(null);
        
        this.setSize(500,500);
        this.setPreferredSize(new Dimension(2000,2000));             
    }
}
