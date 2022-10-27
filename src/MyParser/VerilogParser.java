/* ---------------------------------------------------------------------------
** This software is in the public domain, furnished "as is", without technical
** support, and with no warranty, express or implied, as to its usefulness for
** any purpose.
**
** functions.c
** A library of helper functions for the verilog parser
**
** Author: David Kebo Houngninou
** -------------------------------------------------------------------------*/
package MyParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class VerilogParser{
    int TOKENSIZE = 999;	/* Maximum length of a token.*/
    int LINESIZE = 9999;    /* Maximum length of each input line read.*/
    int BUFSIZE = 99999;	/* Maximum length of a buffer.*/
    int SIZE = 9999;
    int INPUT = 0;
    int AND = 1;
    int NAND = 2;
    int OR = 3;
    int NOR = 4;
    int XOR = 5;
    int XNOR = 6;
    int BUF = 7;
    int NOT = 8;
    int INV = 8;
    int I = 9;
    int RESERVEDNUM = 107;
    int NO_OUT = 0;

    public static void main(String args[]){
        System.out.println("parser");
        File file = new File("/home/spy/Source/Verilog/HelloWorld/hello.v");
        try (FileReader in = new FileReader(file);
            BufferedReader reader = new BufferedReader(new BufferedReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    private int strcasecmp(String gate, String input) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int strlen(String source) {
        return source.length();
    }

    private int strcmp(String word, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Object strstr(String word, String endmodule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class wire   {
        int id;		/*Wire ID number*/
        String name;	/*Name of this wire*/
        String type; 	/*Type of gate driving this wire*/
        int inputcount;		/*Number of wire inputs*/
        int inputs[] = new int[LINESIZE];	/*Array of inputs*/
        int outputcount;
        int outputs[] = new int[LINESIZE];	/*Array of outputs.*/
        boolean primary;		/*Primary input flag*/
    };

    class node {
        String type;	/*input, output, wire, regs*/
        String name;	/*node name*/
        int id;	/*node number*/
    };

    class circuit  {
        wire[] wires;					/*Array of all wires */
        node[] nodes;					/*Array of nodes*/
        String name;					/*Name of the circuit. */
        int inputcount, outputcount;	   	/*Count of primary inputs and primary outputs. */
        int gatecount, wirecount, nodecount;				/*Number of wires, (gates)*/
        String inputs[] = new String[LINESIZE];
        String outputs[] = new String[LINESIZE];	/*List of inputs and outputs in the netlist*/
        int size, id;		        		/*Circuit size and identifier*/
    };

    class module  {
        String name;					/*Name of the module*/
        int inputcount, outputcount;	   		/*Count of primary inputs and primary outputs. */
        int wirecount, regcount, gatecount;	        /*Count of wires ,regs, gates*/
        String inputs[] = new String [LINESIZE];
        String outputs[] = new String [LINESIZE];	/*List of inputs and outputs in the netlist*/
        String wires[] = new String [LINESIZE];
        String regs[] = new String [LINESIZE]; 		/*List of wires, regs, gates in the netlist*/
        String gates[] = new String [LINESIZE];
        int id;
    };

    /**
     * Determines if a string is reserved keyword
     * @param the string to compare
     * @return whether the string is reserved or not
     */
    boolean reserved (String word)
    {
        int i;
        for (i = 0; i < RESERVEDNUM; i++)
            if (strcmp(word, keywords.reserved_word[i])==0 || strstr(word, "endmodule")!= null)
                return true;
        return false;
    }

    /**
     * Determines if a string is gate
     * @param the string to check
     * @return whether the string is a gate or not
     */
    boolean gate (String word)
    {
        int i;
        for (i = 0; i < keywords.gate_name.length; i++)
            if (strcmp(word, keywords.gate_name[i])==0)
                return true;
        return false;
    }

    /**
     * Determines if a wire is an output
     * @param the string to check
     * @return whether the string is a an ouput or not
     */
    boolean isFinalOutput (wire w, circuit c)
    {
        int i;
        for(i = 0; i < c.outputcount; i++)
            if (strcmp(w.name, c.outputs[i])==0)
                return true;
        return false;
    }

    /**
     * Determines if a string is a vector of signals
     * @param the string to check
     * @return whether the string is a vector of signals or not
     */
    boolean signal_vector (String word)
    {
        int i;
        for (i = 0; i < strlen(word); i++)
            if (word.charAt(i) == ':')
                return true;
        return false;
    }

    /**
     * Convert a gate name to an integer value
     * @param the string gate name
     * @return the gate integer value
     */
    int convert (String gate)
    {
        if (strcasecmp(gate, "INPUT")==0)
            return 0;
        else if (strcasecmp(gate, "AND")==0)
            return 1;
        else if (strcasecmp(gate, "NAND")==0)
            return 2;
        else if (strcasecmp(gate, "OR")==0)
            return 3;
        else if (strcasecmp(gate, "NOR")==0)
            return 4;
        else if (strcasecmp(gate, "XOR")==0)
            return 5;
        else if (strcasecmp(gate, "XNOR")==0)
            return 6;
        else if (strcasecmp(gate, "BUF")==0)
            return 7;
        else if (strcasecmp(gate, "NOT")==0 || strcasecmp(gate, "INV")==0)
            return 8;
        else if (strcasecmp(gate, "I")==0)
            return 9;
        else
            return 10;
    }

    /**
     * Prints the summary of a module - Statistical information
     * @param the module object
     */
    void print_module_summary (module m)
    {
        int i;
        System.out.print("\n************** Module %s statistical results *************\n");//, m.name);
        System.out.print("Number of inputs: %d\n");//, m.inputcount);
        for(i = 0; i < m.inputcount; i++)
            System.out.print("%s ");//, m.inputs[i]);

        System.out.print("\n\nNumber of outputs: %d\n");//, m.outputcount);
        for(i = 0; i < m.outputcount; i++)
            System.out.print("%s ");//, m.outputs[i]);

        System.out.print("\n\nNumber of gates: %d\n");//, m.gatecount);
        for(i = 0; i < m.gatecount; i++)
            System.out.print("%s ");//, m.gates[i]);

        System.out.print("\n\nNumber of wires: %d\n");//, m.wirecount);
        for(i = 0; i < m.wirecount; i++)
            System.out.print("%s ");//, m.wires[i]);

        System.out.print("\n\nNumber of regs: %d\n");//, m.regcount);
        for(i = 0; i < m.regcount; i++)
            System.out.print("%s ");//, m.regs[i]);
        System.out.print("*************************** END **************************\n");
    }

    /**
     * Prints the summary of a circuit - Statistical information
     * @param the circuit object
     */
    void print_circuit_summary (circuit c)
    {
        int i,j,row,col;
        System.out.print("\n************** Circuit %s statistical results *************\n");//, c.name);
        System.out.print("Circuit size: %d\n");//, c.size);
        System.out.print("Number of primary inputs: %d\n");//, c.inputcount);
        for(i = 0; i < c.inputcount; i++)
            System.out.print("%s ");//, c.inputs[i]);

        System.out.print("\n\nNumber of outputs: %d\n");//, c.outputcount);
        for(i = 0; i < c.outputcount; i++)
            System.out.print("%s ");//, c.outputs[i]);

        System.out.print("\n\nNumber of gates: %d\n");//, c.gatecount);

        System.out.print("\n\n");
        i = 0;

        while (i < c.wirecount && c.wires[i] != null) {
            System.out.print ("c->wire[%d]->type: %s, ");//,i, c.wires[i].type);
            System.out.print ("ID: %d,  ");//, c.wires[i].id);
            System.out.print ("name: %s, ");//, c.wires[i].name);

            System.out.print ("\nInputs (%d): ");//, c.wires[i].inputcount);/*Wire inputs*/
            for(j = 0; j < c.wires[i].inputcount; j++)
                System.out.print ("%d ");//,c.wires[i].inputs[j]);

            System.out.print ("\nOutputs (%d): ");//, c.wires[i].outputcount);/*Wire outputs*/
            for(j = 0; j < c.wires[i].outputcount; j++)
                System.out.print ("%d ");//,c.wires[i].outputs[j]);

            i++;
            System.out.print ("\n");
        }
        System.out.print("*************************** END **************************\n");
    }

    /**
     * Get the id of a wire
     * @param the signal name, the circuit's name
     * The id of the wire
     */
    int getID (String name, circuit c)
    {
        int i;
        for(i = 0; i < c.size; i++) {
            if (strcmp(name, c.nodes[i].name) == 0) { // If node is found in the circuit, get its index
                return c.nodes[i].id;
            }
        }
        return 0;
    }

    /**
     * Get a wire by id
     * @param the signal name, the circuit's name
     */
    wire getWire (int id, circuit c)
    {
        int i = 0;
        while (i < c.wirecount && c.wires[i] != null) {
            if (c.wires[i].id == id) // If node is found in the circuit
                return c.wires[i];
            i++;
        }
        return null;
    }

    /**
     * Get a wire by name
     * @param the signal name, the circuit's name
     */
    wire getWireByName (String name, circuit c)
    {
        int i = 0;
        while (c.wires[i] != null) {
            if (strcmp(name, c.wires[i].name)==0) // If node is found in the circuit
                return c.wires[i];
            i++;
        }
        return null;
    }

    /**
     * Set values of a node
     * @param the node object, the type of node, the name of the node, the node id
     */
    void setNode (node n, String type, String name, int id)
    {
        n.type = type;
        n.name = name;
        n.id = id;			/*Store node id*/
    }

    /**
     * Create a wire
     * @param the circuit object, the wire object, the wire type, the wire name
     */
    void build_wire (circuit c, wire w, String type, String name)
    {
        int i;
        w.id = getID (name, c);   /*Wire ID*/
        w.type = type;		/*Wire type*/
        w.name = name;		/*Wire name*/
        w.inputcount = 0;			/*Initial number of inputs*/
        w.outputcount = 0;			/*Initial number of outputs*/

        for(i = 0; i < c.inputcount; i++) { /*Circuit primary inputs*/
            if (strcmp(w.name, c.inputs[i])==0) {
                w.primary = true;
            }
        }

        System.out.print ("Creating wire: %s, id: %d\n");//, w.name, w.id);
    }

    /**
     * Determines if a wire is already created
     * @param the circuit object, the wire name
     * @return whether the wire is already created or not
     */
    boolean defined (circuit c, String name)
    {
        int i = 0;
        while (c.wires[i] != null) {
            if (strcmp(c.wires[i].name, name) == 0)
                return true;
            i++;
        }
        return false;
    }
}
