// ---------------------------------------------------------------------------
// This software is in the public domain, furnished "as is", without technical
// support, and with no warranty, express or implied, as to its usefulness for
// any purpose.
//
// functions.H
// A library of helper functions for the verilog parser
//
// Author: David Kebo Houngninou
// ---------------------------------------------------------------------------
#ifndef _functions_H
#define _functions_H

#ifdef IDENT_H
static const char* const functions_H_Id =
    "$Id$";
#endif


#define TOKENSIZE 999	/* Maximum length of a token.*/
#define LINESIZE 9999    /* Maximum length of each input line read.*/
#define BUFSIZE 99999	/* Maximum length of a buffer.*/
#define SIZE 9999
#define INPUT	0
#define AND	1
#define NAND	2
#define OR	3
#define NOR	4
#define XOR	5
#define XNOR	6
#define BUF	7
#define NOT	8
#define INV	8
#define I	9
#define	RESERVEDNUM 107
#define	NO_OUT 0

typedef enum { false, true } bool;
typedef char * string;

struct wire_    {
    int id;		/*Wire ID number*/
    char *name;	/*Name of this wire*/
    char *type; 	/*Type of gate driving this wire*/
    int inputcount;		/*Number of wire inputs*/
    int inputs[LINESIZE];	/*Array of inputs*/
    int outputcount;
    int outputs[LINESIZE];	/*Array of outputs.*/
    bool primary;		/*Primary input flag*/
};
typedef struct wire_ *wire;

struct node_ {
    char type[99];	/*input, output, wire, regs*/
    char name[99];	/*node name*/
    int id;	/*node number*/
};
typedef struct node_ *node;

struct circuit_  {
    wire *wires;					/*Array of all wires */
    node *nodes;					/*Array of nodes*/
    char *name;					/*Name of the circuit. */
    int inputcount, outputcount;	   	/*Count of primary inputs and primary outputs. */
    int gatecount, wirecount, nodecount;				/*Number of wires, (gates)*/
    char *inputs[LINESIZE], *outputs[LINESIZE];	/*List of inputs and outputs in the netlist*/
    int size, id;		        		/*Circuit size and identifier*/
};
typedef struct circuit_ *circuit;

struct module_  {
    char *name;					/*Name of the module*/
    int inputcount, outputcount;	   		/*Count of primary inputs and primary outputs. */
    int wirecount, regcount, gatecount;	        /*Count of wires ,regs, gates*/
    char *inputs [LINESIZE], *outputs [LINESIZE];	/*List of inputs and outputs in the netlist*/
    char *wires [LINESIZE], *regs [LINESIZE]; 		/*List of wires, regs, gates in the netlist*/
    char *gates [LINESIZE];
    int id;
};
typedef struct module_ *module;


#endif

