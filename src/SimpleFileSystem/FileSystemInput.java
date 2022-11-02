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


package SimpleFileSystem;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carmelo Salafia
 * FileSystemInput ist fr das einlesen der Datensaetze und
 * ihrer IndexListe verantwortlich
 * liest aus dem kuenstliches Dateisystem!
 */
public class FileSystemInput {
    
    private ArrayList liste = new ArrayList();
    private FileInputStream fis = null;    
    private BufferedReader br;
    
    public String readline(){
        try {
            return br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(FileSystemInput.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /** Liest aus der Datei die IndexListe
     *  in der alle DatensatzBeschreibungen stehen
     */
    public FileSystemInput(String filename)
    {
        try {
         //System.out.println("FileInput-Received: "+filename);   
         //filename = filename.replace("\\", File.separator);
         //filename = filename.replace("/", File.separator);
         //filename = filename.replace("/\\", File.separator);
         //filename = filename.replace("//", File.separator);
         //System.out.println("Modified: "+filename);
         
         fis = new FileInputStream(new File(filename));
         br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
         DataInputStream dis = new DataInputStream(fis);
         
         long indexPos = dis.readLong();
         fis.getChannel().position(indexPos);
         
         long indexSize = dis.readLong();
         
         for (int i = 0; i < indexSize; i++)
         {
           SFileDescriptor dt= new SFileDescriptor();
           
           byte strLen = dis.readByte();
           dt.filename = "";
           for (int j = 0; j < strLen; j++)
           {
             dt.filename += dis.readChar();
           }          
           
           dt.position = dis.readLong();
           dt.size = dis.readLong();
         
           liste.add(dt);
         }
          
        } catch(IOException ex) {
            System.out.println("Error in Methode loadIndexList()"+ex);
        }        
    }
    
    /* liefert die groesse der IndexListe
     */
    public long indexListSize()
    {
        return liste.size()  ;
    }
    
    public SFileDescriptor[] getAllBeginsWith(String str)
    {
        ArrayList lst = new ArrayList();
        for (int i = 0; i < liste.size(); i++)
        {
            SFileDescriptor dt=getFileDescriptor(i);
            if (dt.filename.contains(str)) {
              lst.add(dt);  
            }            
        }
        
        SFileDescriptor[] descriptoren= new SFileDescriptor[lst.size()];
        
        for (int i = 0; i < lst.size(); i++)
        {
            descriptoren[i] = (SFileDescriptor)lst.get(i);
        }
        
        return descriptoren;
    }
    
    
    /* Springt anhand des FileDescriptor-Index innerhalb 
     * der Datei auf den jeweiligen Datensatz
     */
    public FileInputStream gotoItem(int index)
    {
        SFileDescriptor dt = getFileDescriptor(index);
        try {
          fis.getChannel().position(dt.position);
        } catch(IOException ex) {
            
        }
        return fis;
    }
            
    
    /* liefert anhand des index den dazugehoerenden FileDescriptor
     */    
    public SFileDescriptor getFileDescriptor(int index)
    {
        return (SFileDescriptor)liste.get(index);
    }
           
    
    /* sorgt dafr das die Streams geschlossen werden
     */
    public void close()
    {
        try {
          fis.close();          
        } catch(IOException ex) {
            System.out.println("Error in Methode close()"+ex.toString());
        }
    }
 
}
