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

import javax.swing.DefaultListCellRenderer;
import java.awt.Color;
import javax.swing.JList;
import java.awt.Component;

public class ColorRenderer extends DefaultListCellRenderer
{
    
    /** Creates a new instance of ColorRenderer */
    public ColorRenderer()
    {
        
    }
    
    public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
    {
        
        super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
        
        if (value instanceof ColoredListCell)
        {
            ColoredListCell cell= (ColoredListCell)value;
           
            
           setForeground(cell.color);
           setBackground(new Color(200,200,200));
           setText(cell.text);
        }        
        
        return this;
    }
    
}
