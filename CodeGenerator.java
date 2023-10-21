import absyn.*;
import symbol.*;
import java.io.*;
import java.util.ArrayList;

public class CodeGenerator{
	
	public static final int FP = 5;
	public static final int GP = 6;
	public static final int PC = 7;
	public static final int AC = 0;


	public int emitLoc = 0; // Point to current instruction location
	public int highEmitLoc = 0; // Point to next available location
	public int globalOffset = 0; // Point to bottom of global stack
	public String fName;


	private DecList program;
	private SymbolTable symTable;


	public CodeGenerator(String fName, DecList program){
		this.program = program;
		this.fName = fName;
		this.symTable = new SymbolTable(false);

		visit(program);
	}


	// Functions to maintain the code

	public int emitSkip(int dist){
		int count = emitLoc;
		emitLoc += dist;

		if(highEmitLoc < emitLoc){
			highEmitLoc = emitLoc;
		}

		return count;
	}

	public void emitBackup(int loc){
		if(loc > highEmitLoc){
			emitComment("BUG in emitBackup");
		}
		emitLoc = loc;
	}

	public void emitRestore(){
		emitLoc = highEmitLoc;
	}

	public void emitComment(String comment){
		System.out.println("* " + comment);
	}

	public void emitRM(String op, int r, int offset, int r1, String comment){
		String s = emitLoc + ": " + op + " " + r + "," + offset + "(" + r1 + ")";

		writeCode(s);
		++emitLoc;
		writeCode("\t" + comment + "\n");

		if(highEmitLoc < emitLoc){
			highEmitLoc = emitLoc;
		}
	}

	public void emitRMAbs(String op, int r, int a, String comment){
		String s = emitLoc + ": " + op + " " + r + "," + (a - (emitLoc + 1)) + "(" + PC + ")";

		writeCode(s);
		++emitLoc;
		writeCode("\t" + comment + "\n");

		if(highEmitLoc < emitLoc){
			highEmitLoc = emitLoc;
		}
	}

	public void emitOp(String op, int dest, int r, int r1, String comment){
		String s = emitLoc + ": " + op + " " + dest + "," + r + "," + r1;

		writeCode(s);
		++emitLoc;
		writeCode("\t" + comment + "\n");
	}


	// Write code to PrintWriter which is set to the file

	public void writeCode(String s){
		PrintWriter output = null;

		try{
			output = new PrintWriter(new FileOutputStream(this.fName, true));
			output.printf(s);
			output.close();
			
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}




	// Visit functions


	// ExpList
	public void visit(ExpList e, int offset){
		while(e != null){
			if(e.head != null){
				visit(e.head, offset, false);
			}
		}
	}


	// Assign
	public void visit(AssignExp e, int offset){
		emitComment("-> Op");

		
		if(e.lhs.variable instanceof SimpleVar){

			visit((SimpleVar)e.lhs.variable, offset, true);
			emitRM("ST", AC, offset--, FP, "Op: Push left");

		} else if(e.lhs.variable instanceof IndexVar){

			visit((IndexVar)e.lhs.variable, offset--, false);

		}


		if(e.rhs instanceof IntExp){
			visit(e.rhs, offset, false);

		} else if(e.rhs instanceof VarExp){
			visit(e.rhs, offset, false);

		} else if(e.rhs instanceof CallExp){
			visit(e.rhs, offset, false);

		} else if(e.rhs instanceof OpExp){
			visit(e.rhs, offset, false);

		} else if(e.rhs instanceof BoolExp){
			visit(e.rhs, offset, false);

		}

		emitRM("LD", 1, ++offset, FP, "Op: Load left");
		emitRM("ST", AC, 0, 1, "Assign: Store value");
		emitComment("<- Op");
	}


	// IfExp
	public void visit(IfExp e, int offset){

		emitComment("-> If");
		symTable.newScope();

		visit(e.test, offset, false);
		int loc1 = emitSkip(1);

		visit(e.thenpart, offset, false);
		int loc2 = emitSkip(0);

		emitBackup(loc1);
		emitRMAbs("JEQ", 0, loc2, "If: Jump to else");
		emitRestore();

		visit(e.elsepart, offset, false);

		symTable.removeScope();
		emitComment("<- If");
	}


	// OpExp
	public void visit(OpExp e, int offset){

		emitComment("->Op");
		
		// VarExp Handling
		if(e.left instanceof VarExp){
			VarExp v = (VarExp)e.left;

			// SimpleVar
			if(v.variable instanceof SimpleVar){
				visit(v, offset, false);
				emitRM("ST", AC, offset--, FP, "Op: Push left");
			} else {
				visit(v, offset--, true);
			}
		
		// IntExp Handling
		} else if(e.left instanceof IntExp){
			visit(e.left, offset, false);
			emitRM("ST", AC, offset--, FP, "Op: Push left");

		// OpExp Handling
		} else if(e.left instanceof OpExp){
			visit(e.left, offset, false);
			emitRM("ST", AC, offset--, FP, "");

		// CallExp Handling
		} else if(e.left instanceof CallExp){
			visit(e.left, offset, false);

		// BoolExp Handling
		} else if(e.left instanceof BoolExp){
			visit(e.left, offset, false);
			emitRM("ST", AC, offset--, FP, "Op: Push left");
		}


		// VarExp Handling
		if(e.right instanceof VarExp){
			VarExp v = (VarExp)e.right;

			// SimpleVar
			if(v.variable instanceof SimpleVar){
				visit(v, offset, false);
			} else {
				visit(v, offset, true);
			}
		
		// IntExp Handling
		} else if(e.right instanceof IntExp){
			visit(e.right, offset, false);

		// OpExp Handling
		} else if(e.right instanceof OpExp){
			visit(e.right, offset, false);

		// CallExp Handling
		} else if(e.right instanceof CallExp){
			visit(e.right, offset, false);

		// BoolExp Handling
		} else if(e.right instanceof BoolExp){
			visit(e.right, offset, false);
		}




		emitRM("LD", 1, ++offset, FP, "Op: Load left");

		switch(e.op){
			case OpExp.PLUS:
				emitOp("ADD", AC, 1, AC, "Op +");
				break;

			case OpExp.MINUS:
				emitOp("SUB", AC, 1, AC, "Op -");
				break;

			case OpExp.MUL:
				emitOp("MUL", AC, 1, AC, "Op *");
				break;

			case OpExp.DIV:
				emitOp("DIV", AC, 1, AC, "Op /");
				break;

			case OpExp.EQ:
				emitOp("EQU", AC, 1, AC, "Op =");
				break;

			case OpExp.EQEQ:
				emitOp("SUB", AC, 1, AC, "Op ==");
				emitRM("JEQ", AC, 2, PC, " ");
				emitRM("LDC", AC, 0, 0, "False case");
				emitRM("LDA", PC, 1, PC, "Unconditional jump");
				emitRM("LDC", AC, 1, 0, "True case");
				break;

			case OpExp.LT:
				emitOp("SUB", AC, 1, AC, "Op <");
				emitRM("JLT", AC, 2, PC, " ");
				emitRM("LDC", AC, 0, 0, "False case");
				emitRM("LDA", PC, 1, PC, "Unconditional jump");
				emitRM("LDC", AC, 1, 0, "True case");
				break;

			case OpExp.GT:
				emitOp("SUB", AC, 1, AC, "Op ==");
				emitRM("JGT", AC, 2, PC, " ");
				emitRM("LDC", AC, 0, 0, "False case");
				emitRM("LDA", PC, 1, PC, "Unconditional jump");
				emitRM("LDC", AC, 1, 0, "True case");
				break;

			case OpExp.NE:
				emitOp("SUB", AC, 1, AC, "Op !=");
				emitRM("JNE", AC, 2, PC, " ");
				emitRM("LDC", AC, 0, 0, "False case");
				emitRM("LDA", PC, 1, PC, "Unconditional jump");
				emitRM("LDC", AC, 1, 0, "True case");
				break;

			case OpExp.LE:
				emitOp("SUB", AC, 1, AC, "Op <=");
				emitRM("JLE", AC, 2, PC, " ");
				emitRM("LDC", AC, 0, 0, "False case");
				emitRM("LDA", PC, 1, PC, "Unconditional jump");
				emitRM("LDC", AC, 1, 0, "True case");
				break;

			case OpExp.GE:
				emitOp("SUB", AC, 1, AC, "Op >=");
				emitRM("JGE", AC, 2, PC, " ");
				emitRM("LDC", AC, 0, 0, "False case");
				emitRM("LDA", PC, 1, PC, "Unconditional jump");
				emitRM("LDC", AC, 1, 0, "True case");
				break;
			
		}

		emitComment("<- Op");
	}

	// CallExp
	public void visit(CallExp e, int offset){

		FunSym funSym = (FunSym)symTable.getFunc(e.func);

		emitComment("-> Call");
		emitComment("Call of function: " + e.func);
		int count = -2;

		while(e.args != null){
			if(e.args.head != null){
				visit(e.args.head, offset, false);
				emitRM("ST", AC, offset + count, FP, "Op: Push left");
				count--;
			}
			e.args = e.args.tail;
		}

		emitRM("ST", FP, offset, FP, "Push OFP");
		emitRM("LDA", FP, offset, FP, "Push frame");
		emitRM("LDA", 0, 1, PC, "Load AC with return pointer");
		emitRMAbs("LDA", PC, funSym.address, "Jump to function location");
		emitRM("LD", FP, 0, FP, "Pop frame");
		emitComment("<- Call");
	}


	// DecList
	public void visit(DecList d){

		try {
			PrintWriter output = new PrintWriter(this.fName);
			output.close();

		} catch (FileNotFoundException e){
			e.printStackTrace();
		}

		symTable.newScope();

		FunSym input = new FunSym("input", NameTy.INT, new ArrayList<Symbol>());
		symTable.addSymbol("input", input);

		ArrayList<Symbol> params = new ArrayList<Symbol>();
		params.add(new VarSym("", NameTy.INT));

		FunSym output = new FunSym("output", NameTy.VOID, params);
		symTable.addSymbol("output", output);


		// Generate prelude
		emitComment("Prelude");
    	emitRM("LD", GP, 0, AC, "Load GP with max address");
    	emitRM("LDA", FP, 0, GP, "Copy GP to FP");
    	emitRM("ST", 0, 0, 0, "Clear location 0");
    	int loc = emitSkip(1);

    	// IO Functions

		// Input
    	emitComment("Jump around I/O functions");
    	emitComment("Code for input routine");
    	emitRM("ST", 0, -1, FP, "Store return");
    	emitOp("IN", 0, 0, 0, "Input");
    	emitRM("LD", PC, -1, FP, "Return caller");

    	// Output
    	emitComment("Code for output routine");
    	emitRM("ST", 0, -1, FP, "Store return");
    	emitRM("LD", 0, -2, FP, "Load output value");
    	emitOp("OUT", 0, 0, 0, "Output");
    	emitRM("LD", 7, -1, FP, "Return caller");

    	int loc2 = emitSkip(0);


    	emitBackup(loc);
    	emitRMAbs("LDA", PC, loc2, "Jump around I/O functions");
    	emitRestore();
		emitComment("End prelude");


    	// Recursive code gen.
    	while (d != null){
			if (d.head != null){
				visit(d.head);
			}
			d = d.tail;
		}

    	FunSym funSymMain = (FunSym)symTable.getFunc("main");


    	// Generate finale
		emitComment("Finale");
    	emitRM("ST", FP, globalOffset, FP, "Push old frame pointer");
    	emitRM("LDA", FP, globalOffset, FP, "Push frame");
    	emitRM("LDA", 0, 1, PC, "Load AC with return pointer");

    	// Retrieve main function address
    	emitRMAbs("LDA", PC, funSymMain.address, "Jump to main location");
    	emitRM("LD", FP, 0, FP, "Pop frame");
    	emitOp("HALT", 0, 0, 0, "Halt");

    	symTable.removeScope();

  		

	}


	// Dec
	public void visit(Dec d){
		if(d instanceof FunctionDec){
      		visit((FunctionDec) d);

		// VarDec
    	} else if (d instanceof VarDec){
      		VarDec var = (VarDec)d;

			// SimpleDec
      		if(var instanceof SimpleDec){
        		SimpleDec s = (SimpleDec)var;
        		VarSym sym = new VarSym(s.name, s.typ.typ, globalOffset);
        		symTable.addSymbol(s.name, sym);
        		emitComment("Allocating global var: " + s.name);
        		emitComment("<- VarDec");
        		globalOffset--;

			// ArrayDec
      		} else if(var instanceof ArrayDec){
        		ArrayDec a = (ArrayDec)var;
        		ArrSym sym = new ArrSym(a.name, a.typ.typ, a.size, globalOffset - (a.size - 1));
        		symTable.addSymbol(a.name, sym);
        		emitComment("Allocating global var: " + a.name);
        		emitComment("<- VarDec");
        		globalOffset--;
      		}
    	}
	}


	// Exp
	public int visit(Exp e, int offset, boolean isAddress){

        if(e instanceof IfExp){
            visit((IfExp) e, offset);
        } else if(e instanceof WhileExp){
            visit((WhileExp) e, offset);
        } else if(e instanceof ReturnExp){
            visit((ReturnExp) e, offset);
        } else if(e instanceof CompoundExp){
            visit((CompoundExp) e, offset);
        } else if(e instanceof AssignExp){
            visit((AssignExp) e, offset);
        } else if(e instanceof OpExp){
            visit((OpExp) e, offset);
        } else if(e instanceof CallExp){
            visit((CallExp) e, offset);
        } else if(e instanceof IntExp){
            visit((IntExp) e);
        } else if(e instanceof VarExp){
            visit((VarExp) e, offset, isAddress);
        } else if(e instanceof BoolExp){
            visit((BoolExp) e);
        } else if(e instanceof NilExp){
            //visit((NilExp) e);
        }

		return offset;
	}


	// IntExp
	public void visit(IntExp e){
		emitComment("-> Constant");
		emitRM("LDC", AC, e.value, 0, "Load constant");
		emitComment("<- Constant");
	}

	// BoolExp
	public void visit(BoolExp e){
		emitComment("-> Bool");
		emitRM("LDC", AC, 100, 0, "Load constant");
		emitComment("<- Bool");
	}


	// FunctionDec
	public void visit(FunctionDec d){
		emitComment("-> FunctionDec");
		emitComment("Processing Function: " + d.func);
		emitComment("Jump around function body");

	    int offset = -2;
		int loc = emitSkip(1);

    	FunSym funSym = new FunSym(d.func, d.result.typ, null, emitLoc);
    	symTable.addSymbol(d.func, funSym);
    	symTable.newScope();

    	emitRM("ST", 0, -1, FP, "Store return");
    	offset = visit(d.params, offset,  true);
    	offset = visit(d.body, offset, false);
    	emitRM("LD", PC, -1, FP, "Return caller");

    	int loc2 = emitSkip(0);
    	emitBackup(loc);
    	emitRMAbs("LDA", PC, loc2, "Jump around function body");
    	emitRestore();
    	emitComment("<- FunctionDec");
    	symTable.removeScope();
	}


	// CompoundExp
	public int visit(CompoundExp e, int offset){
		emitComment("-> CompoundExp");
   		offset = visit(e.decList, offset, false);
   		visit(e.exp, offset);
   		emitComment("<- CompoundExp");
   		return offset;
	}


	// IndexVar
	public void visit(IndexVar e, int offset, boolean isAddress){

		IndexVar var = (IndexVar)e;
    	ArrSym a = (ArrSym)symTable.getSymbol(var.name);
    	emitComment("-> Subs");

    	if(symTable.symExists(var.name) == 0){
    	  emitRM("LD", AC, a.offset, GP, "Load ID value");
    	  emitRM("ST", AC, offset--, GP, "Store array address");

    	}
    	else{
    	  emitRM("LD", AC, a.offset, FP, "Load ID value");
    	  emitRM("ST", AC, offset--, FP, "store array address");

    	}

		visit(var.index, offset, false);
    	emitComment("<- Subs");
	}


	// ReturnExp
	public void visit(ReturnExp e, int offset){
		emitComment("-> ReturnExp");
		visit(e.exp, offset, false);
		emitRM("LD", PC, -1, FP, "Return to caller");
		emitComment("<- Return");
	}


	// SimpleVar
	public void visit(SimpleVar v, int offset, boolean isAddress){
		
		VarSym sym = (VarSym)symTable.getSymbol(v.name);

		emitComment("-> ID");
		emitComment("Looking up ID: " + v.name);

		if(symTable.symExists(v.name) == 0){
			if(isAddress){
				emitRM("LDA", 0, sym.offset, GP, "Load ID address");
			} else {
				emitRM("LD", 0, sym.offset, GP, "Load ID value");
			}
		} else {
			if(isAddress){
				emitRM("LDA", 0, sym.offset, GP, "Load ID address");
			} else {
				emitRM("LD", 0, sym.offset, GP, "Load ID value");
			}
		}

		emitComment("<- ID");
	}


	// VarDec
	public int visit(VarDec d, int offset, boolean isParam){

		if(isParam){
			if(d instanceof ArrayDec){
				ArrayDec dec = (ArrayDec)d;
				ArrSym sym = new ArrSym(dec.name, dec.typ.typ, 1, offset--);
				symTable.addSymbol(dec.name, sym);

			} else if(d instanceof SimpleDec){
				SimpleDec dec = (SimpleDec)d;
				VarSym sym = new VarSym(dec.name, dec.typ.typ, offset--);
				symTable.addSymbol(dec.name, sym);
			}

		} else {
			if(d instanceof ArrayDec){
				ArrayDec dec = (ArrayDec)d;
				offset -= (dec.size - 1);
				ArrSym sym = new ArrSym(dec.name, dec.typ.typ, dec.size, offset--);

				symTable.addSymbol(dec.name, sym);
				emitComment("Processing local variable: " + dec.name);

			} else if(d instanceof SimpleDec){
				SimpleDec dec = (SimpleDec)d;
				VarSym sym = new VarSym(dec.name, dec.typ.typ, offset--);

				symTable.addSymbol(dec.name, sym);
				emitComment("Processing local variable: " + dec.name);
			}
		}
		return offset;
	}


	// VarDecList
	public int visit(VarDecList d, int offset, boolean isParam){

		while(d != null){
			if(d.head != null){
				offset = visit(d.head, offset, isParam);
			}
			d = d.tail;
		}
		return offset;
	}


	// VarExp
	public void visit(VarExp e, int offset, boolean isAddress){

		if(e.variable instanceof IndexVar){
			IndexVar var = (IndexVar)e.variable;
			ArrSym a = (ArrSym) symTable.getSymbol(var.name);

			emitComment("-> Subs");

			if(symTable.symExists(var.name) == 0){
				emitRM("LD", AC, a.offset, GP, "Load ID Value");
				emitRM("ST", AC, offset--, GP, "Store array address");

			} else {

				emitRM("LD", AC, a.offset, FP, "Load ID Value");
				emitRM("ST", AC, offset--, FP, "Store array address");
			}

			visit(var.index, offset, false);
			emitComment("<- Subs");
		}
	}


	// WhileExp
	public void visit(WhileExp e, int offset){
		emitComment("-> While");
		symTable.newScope();

		emitComment("While Jump after body comes back here");
		int loc = emitSkip(0);

		visit(e.test, offset, false);
		int loc2 = emitSkip(1);

		visit(e.body, offset, false);
		emitRMAbs("LDA", PC, loc, "While: Absolute jump to test");
		int loc3 = emitSkip(0);

		emitBackup(loc2);
		emitRMAbs("JEQ", 0, loc3, "While: Jump to end");

		symTable.removeScope();
		emitComment("<-While");
	}


}
