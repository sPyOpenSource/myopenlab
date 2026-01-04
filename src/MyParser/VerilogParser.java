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
import org.json.JSONArray;

public class VerilogParser{
    int TOKENSIZE = 999;    /* Maximum length of a token.*/
    int LINESIZE = 9999;    /* Maximum length of each input line read.*/
    int BUFSIZE = 99999;    /* Maximum length of a buffer.*/
    int SIZE = 9999;
    int INPUT = 0;
    int AND  = 1;
    int NAND = 2;
    int OR   = 3;
    int NOR  = 4;
    int XOR  = 5;
    int XNOR = 6;
    int BUF = 7;
    int NOT = 8;
    int INV = 8;
    int I   = 9;
    int RESERVEDNUM = 107;
    int NO_OUT = 0;

    public static void Parser(String path){
        //String code = "module mux (out, select, in0, in1, in2, in3);\noutput out;\ninput [1:0] select;\ninput in0, in1, in2, in3;\nendmodule";
        String code = "";
        File file = new File(path);
        try (FileReader in = new FileReader(file);
            BufferedReader reader = new BufferedReader(new BufferedReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("//");
                if(parts.length > 1)
                    line = parts[0];
                code += "{type:\"" + line + "\"},";
            }
            System.out.println(code);
            code = code.replace("       ", " ");
            code = code.replace("    ", " ");
            code = code.replace("   ", " ");
            code = code.replace("  ", " ");
            code = code.replace("\n", "");
            code = code.replace("endmodule", "]");
            code = code.replace("{type:\"module ", "[{module:\"");
            code = code.replace("type:\"always @", "always:\"");
            code = code.replace("\"output ", "output, names:\"");
            code = code.replace("\"input ", "input, names:\"");
            code = code.replace("\"parameter", "parameter, code:\"");
            code = code.replace("\"assign", "assign, code:\"");
            code = code.replace("\"reg ", "reg, names:\"");
            code = code.replace("\"wire", "wire, names:\"");
            code = code.replace("begin", "[");
            code = code.replace("end", "]");
            code = code.replace("type:\" if", "if:\"");
            code = code.replace(";", "\"},");
            //code = code.replace("(", ", arguments: [");
            //code = code.replace(")", "]");
            code = code.replace("]\"", "]");
            code = code.replace("\"},\"}", "\"}");
            code = code.replace("\"},\"}", "\"}");
            code = code.replace("}, \"}", "}");
            code = code.replace("}, \"}", "}");
            code = code.replace("}, \"}", "}");
            code = code.replace("},\"}", "}");
            code = code.replace("[\"},", "[");
            code = code.replace("{type:\"\"},", "");
            code = code.replace("{type:\" \"},", "");
            code = code.replace("{\" [", "{block:[");
            code = code.replace(",{type:\" ]", "]");
            code = code.replace(",{type:\"]},", "]");
            code = code.replace("\" [", "[");
            System.out.println(code);
            JSONArray object = new JSONArray(code);
            System.out.println(object.toString(4));
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    class Wire {
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
        
        /**
         * Set values of a node
         * @param the node object, the type of node, the name of the node, the node id
         */
        void setNode(String type, String name, int id)
        {
            this.type = type;
            this.name = name;
            this.id = id;			/*Store node id*/
        }
    };

    class Circuit {
        Wire[] wires;					/*Array of all wires */
        Node[] nodes;					/*Array of nodes*/
        String name;					/*Name of the circuit. */
        int inputcount, outputcount;                    /*Count of primary inputs and primary outputs. */
        int gatecount, wirecount, nodecount;		/*Number of wires, (gates)*/
        String inputs[] = new String[LINESIZE];
        String outputs[] = new String[LINESIZE];	/*List of inputs and outputs in the netlist*/
        int size, id;		        		/*Circuit size and identifier*/
        
        /**
         * Get the id of a wire
         * @param the signal name, the circuit's name
         * The id of the wire
         */
        int getID(String name)
        {
            for(int i = 0; i < size; i++) {
                if (name.equals(nodes[i].name)) { // If node is found in the circuit, get its index
                    return nodes[i].id;
                }
            }
            return 0;
        }

        /**
         * Get a wire by id
         * @param the signal name, the circuit's name
         */
        Wire getWire(int id)
        {
            int i = 0;
            while (i < wirecount && wires[i] != null) {
                if (wires[i].id == id) // If node is found in the circuit
                    return wires[i];
                i++;
            }
            return null;
        }

        /**
         * Get a wire by name
         * @param the signal name, the circuit's name
         */
        Wire getWireByName(String name)
        {
            int i = 0;
            while (wires[i] != null) {
                if (name.equals(wires[i].name)) // If node is found in the circuit
                    return wires[i];
                i++;
            }
            return null;
        }
    
        /**
         * Prints the summary of a circuit - Statistical information
         * @param the circuit object
         */
        @Override
        public String toString()
        {
           int i,j;
           System.out.print("\n************** Circuit %s statistical results *************\n");//, c.name);
           System.out.print("Circuit size: %d\n");//, c.size);
           System.out.print("Number of primary inputs: %d\n");//, c.inputcount);
           for(i = 0; i < inputcount; i++)
               System.out.print("%s ");//, c.inputs[i]);

           System.out.print("\n\nNumber of outputs: %d\n");//, c.outputcount);
           for(i = 0; i < outputcount; i++)
               System.out.print("%s ");//, c.outputs[i]);

           System.out.print("\n\nNumber of gates: %d\n");//, c.gatecount);

           System.out.print("\n\n");
           i = 0;

           while (i < wirecount && wires[i] != null) {
               System.out.print ("c->wire[%d]->type: %s, ");//,i, c.wires[i].type);
               System.out.print ("ID: %d,  ");//, c.wires[i].id);
               System.out.print ("name: %s, ");//, c.wires[i].name);

               System.out.print ("\nInputs (%d): ");//, c.wires[i].inputcount);/*Wire inputs*/
               for(j = 0; j < wires[i].inputcount; j++)
                   System.out.print ("%d ");//,c.wires[i].inputs[j]);

               System.out.print ("\nOutputs (%d): ");//, c.wires[i].outputcount);/*Wire outputs*/
               for(j = 0; j < wires[i].outputcount; j++)
                   System.out.print ("%d ");//,c.wires[i].outputs[j]);

               i++;
               System.out.print ("\n");
           }
           System.out.print("*************************** END **************************\n");
           return name;
        }
        
        /**
         * Create a wire
         * @param the circuit object, the wire object, the wire type, the wire name
         */
        void buildWire(Wire w, String type, String name)
        {
            w.id = getID(name); /*Wire ID*/
            w.type = type;      /*Wire type*/
            w.name = name;      /*Wire name*/
            w.inputcount = 0;	/*Initial number of inputs*/
            w.outputcount = 0;	/*Initial number of outputs*/

            for(int i = 0; i < inputcount; i++) { /*Circuit primary inputs*/
                if (w.name.equals(inputs[i])) {
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
        boolean isDefined(String name)
        {
            int i = 0;
            while (wires[i] != null) {
                if (wires[i].name.equals(name))
                    return true;
                i++;
            }
            return false;
        }
    };

    class Module {
        String name;					/*Name of the module*/
        int inputcount, outputcount;	   		/*Count of primary inputs and primary outputs. */
        int wirecount, regcount, gatecount;	        /*Count of wires ,regs, gates*/
        String inputs[] = new String [LINESIZE];
        String outputs[] = new String [LINESIZE];	/*List of inputs and outputs in the netlist*/
        String wires[] = new String [LINESIZE];
        String regs[] = new String [LINESIZE]; 		/*List of wires, regs, gates in the netlist*/
        String gates[] = new String [LINESIZE];
        int id;
        
        /**
         * Prints the summary of a module - Statistical information
         * @param the module object
         */
        @Override
        public String toString()
        {
           int i;
           System.out.print("\n************** Module %s statistical results *************\n");//, m.name);
           System.out.print("Number of inputs: %d\n");//, m.inputcount);
           for(i = 0; i < inputcount; i++)
               System.out.print("%s ");//, m.inputs[i]);

           System.out.print("\n\nNumber of outputs: %d\n");//, m.outputcount);
           for(i = 0; i < outputcount; i++)
               System.out.print("%s ");//, m.outputs[i]);

           System.out.print("\n\nNumber of gates: %d\n");//, m.gatecount);
           for(i = 0; i < gatecount; i++)
               System.out.print("%s ");//, m.gates[i]);

           System.out.print("\n\nNumber of wires: %d\n");//, m.wirecount);
           for(i = 0; i < wirecount; i++)
               System.out.print("%s ");//, m.wires[i]);

           System.out.print("\n\nNumber of regs: %d\n");//, m.regcount);
           for(i = 0; i < regcount; i++)
               System.out.print("%s ");//, m.regs[i]);
           System.out.print("*************************** END **************************\n");
           return name;
        }
    };

    /**
     * Determines if a string is reserved keyword
     * @param the string to compare
     * @return whether the string is reserved or not
     */
    boolean isReserved(String word)
    {
        for (int i = 0; i < RESERVEDNUM; i++)
            if (word.equals(Keywords.reserved_word[i]) || "endmodule".equals(word))
                return true;
        return false;
    }

    /**
     * Determines if a string is gate
     * @param the string to check
     * @return whether the string is a gate or not
     */
    boolean isGate(String word)
    {
        for (String gate_name : Keywords.gate_name) {
            if (word.equals(gate_name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a wire is an output
     * @param the string to check
     * @return whether the string is a an ouput or not
     */
    boolean isFinalOutput(Wire w, Circuit c)
    {
        for(int i = 0; i < c.outputcount; i++)
            if (w.name.equals(c.outputs[i]))
                return true;
        return false;
    }

    /**
     * Determines if a string is a vector of signals
     * @param the string to check
     * @return whether the string is a vector of signals or not
     */
    boolean isSignalVector(String word)
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
    int Convert(String gate)
    {
        if (null == gate)
            return 10;
        else switch (gate) {
            case "INPUT":
                return 0;
            case "AND":
                return 1;
            case "NAND":
                return 2;
            case "OR":
                return 3;
            case "NOR":
                return 4;
            case "XOR":
                return 5;
            case "XNOR":
                return 6;
            case "BUF":
                return 7;
            case "NOT":
            case "INV":
                return 8;
            case "I":
                return 9;
            default:
                return 10;
        }
    }
}
