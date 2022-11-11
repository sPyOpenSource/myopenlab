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
    int TOKENSIZE = 999;    /* Maximum length of a token.*/
    int LINESIZE = 9999;    /* Maximum length of each input line read.*/
    int BUFSIZE = 99999;    /* Maximum length of a buffer.*/
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
        File file = new File("/run/media/spy/home/ArchARM/home/spy/Source/FPGA/Verilog/HelloWorld/hello.v");
        try (FileReader in = new FileReader(file);
            BufferedReader reader = new BufferedReader(new BufferedReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    class Wire   {
        int id;		/*Wire ID number*/
        String name;	/*Name of this wire*/
        String type; 	/*Type of gate driving this wire*/
        int inputcount;		/*Number of wire inputs*/
        int inputs[] = new int[LINESIZE];	/*Array of inputs*/
        int outputcount;
        int outputs[] = new int[LINESIZE];	/*Array of outputs.*/
        boolean primary;		/*Primary input flag*/
    };

    class Node {
        String type;	/*input, output, wire, regs*/
        String name;	/*node name*/
        int id;	/*node number*/
    };

    class Circuit  {
        Wire[] wires;					/*Array of all wires */
        Node[] nodes;					/*Array of nodes*/
        String name;					/*Name of the circuit. */
        int inputcount, outputcount;                    /*Count of primary inputs and primary outputs. */
        int gatecount, wirecount, nodecount;		/*Number of wires, (gates)*/
        String inputs[] = new String[LINESIZE];
        String outputs[] = new String[LINESIZE];	/*List of inputs and outputs in the netlist*/
        int size, id;		        		/*Circuit size and identifier*/
    };

    class Module  {
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
    boolean isReserved (String word)
    {
        for (int i = 0; i < RESERVEDNUM; i++)
            if (word==Keywords.reserved_word[i] || word=="endmodule")
                return true;
        return false;
    }

    /**
     * Determines if a string is gate
     * @param the string to check
     * @return whether the string is a gate or not
     */
    boolean isGate (String word)
    {
        int i;
        for (i = 0; i < Keywords.gate_name.length; i++)
            if (word==Keywords.gate_name[i])
                return true;
        return false;
    }

    /**
     * Determines if a wire is an output
     * @param the string to check
     * @return whether the string is a an ouput or not
     */
    boolean isFinalOutput (Wire w, Circuit c)
    {
        for(int i = 0; i < c.outputcount; i++)
            if (w.name==c.outputs[i])
                return true;
        return false;
    }

    /**
     * Determines if a string is a vector of signals
     * @param the string to check
     * @return whether the string is a vector of signals or not
     */
    boolean isSignalVector (String word)
    {
        for (int i = 0; i < word.length(); i++)
            if (word.charAt(i) == ':')
                return true;
        return false;
    }

    /**
     * Convert a gate name to an integer value
     * @param the string gate name
     * @return the gate integer value
     */
    int Convert (String gate)
    {
        if (gate=="INPUT")
            return 0;
        else if (gate=="AND")
            return 1;
        else if (gate=="NAND")
            return 2;
        else if (gate=="OR")
            return 3;
        else if (gate=="NOR")
            return 4;
        else if (gate=="XOR")
            return 5;
        else if (gate=="XNOR")
            return 6;
        else if (gate=="BUF")
            return 7;
        else if (gate=="NOT" || gate=="INV")
            return 8;
        else if (gate=="I")
            return 9;
        else
            return 10;
    }

    /**
     * Prints the summary of a module - Statistical information
     * @param the module object
     */
    void print_module_summary (Module m)
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
    void print_circuit_summary (Circuit c)
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
    int getID (String name, Circuit c)
    {
        int i;
        for(i = 0; i < c.size; i++) {
            if (name==c.nodes[i].name) { // If node is found in the circuit, get its index
                return c.nodes[i].id;
            }
        }
        return 0;
    }

    /**
     * Get a wire by id
     * @param the signal name, the circuit's name
     */
    Wire getWire (int id, Circuit c)
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
    Wire getWireByName (String name, Circuit c)
    {
        int i = 0;
        while (c.wires[i] != null) {
            if (name==c.wires[i].name) // If node is found in the circuit
                return c.wires[i];
            i++;
        }
        return null;
    }

    /**
     * Set values of a node
     * @param the node object, the type of node, the name of the node, the node id
     */
    void setNode (Node n, String type, String name, int id)
    {
        n.type = type;
        n.name = name;
        n.id = id;			/*Store node id*/
    }

    /**
     * Create a wire
     * @param the circuit object, the wire object, the wire type, the wire name
     */
    void buildWire (Circuit c, Wire w, String type, String name)
    {
        int i;
        w.id = getID (name, c);   /*Wire ID*/
        w.type = type;		/*Wire type*/
        w.name = name;		/*Wire name*/
        w.inputcount = 0;			/*Initial number of inputs*/
        w.outputcount = 0;			/*Initial number of outputs*/

        for(i = 0; i < c.inputcount; i++) { /*Circuit primary inputs*/
            if (w.name==c.inputs[i]) {
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
    boolean isDefined (Circuit c, String name)
    {
        int i = 0;
        while (c.wires[i] != null) {
            if (c.wires[i].name==name)
                return true;
            i++;
        }
        return false;
    }
}
