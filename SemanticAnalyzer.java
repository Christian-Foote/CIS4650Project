import absyn.*;
import symbol.*;

import java.util.ArrayList;

public class SemanticAnalyzer{

    private SymbolTable symbolTable;
    private DecList program;
    private int returnType;

    private boolean haveMain;
    private boolean haveLastMain;
    private boolean haveError;

    public SemanticAnalyzer(boolean showScope, DecList program){
        symbolTable = new SymbolTable(showScope);
        this.program = program;

        haveMain = false;
        haveLastMain = false;
        haveError = false;

        typeCheck(program);
    }

    // Setters
    private void setError(){
        this.haveError = true;
    }

    private void setMain(){
        this.haveMain = true;
    }

    private void setLastMain(boolean lastMain){
        this.haveLastMain = lastMain;
    }

    private void setReturn(int returnType){
        this.returnType = returnType;
    }

    // typeCheck methods for all Absyn classes

    // DECLIST
    private void typeCheck(DecList decList){

        symbolTable.newScope();

        // Input Function
        Symbol inFun = new FunSym("input", NameTy.INT, new ArrayList<Symbol>());
        symbolTable.addSymbol("input", inFun);

        // Output Function
        ArrayList<Symbol> params = new ArrayList<Symbol>();
        params.add(new VarSym("", NameTy.INT));
        Symbol outFun = new FunSym("output", NameTy.VOID, params);
        symbolTable.addSymbol("output", outFun);

        while(decList != null){
            if(decList.head != null){
                typeCheck(decList.head);
            }
            decList = decList.tail;
        }

        if(!this.haveMain){

            System.err.println("Error: Program must have a main function.");
            setError();

        } else if(!this.haveLastMain){

            System.err.println("Error: Main must be the last function.");
            setError();
        }

        symbolTable.removeScope();
    }

    // Abstract classes
    public void typeCheck(Dec dec){
        if(dec instanceof VarDec){
            typeCheck((VarDec)dec);
        } else if(dec instanceof FunctionDec){
            typeCheck((FunctionDec)dec);
        }
    }

    public void typeCheck(Var var){
        if(var instanceof SimpleVar){
            typeCheck((SimpleVar)var);
        } else if(var instanceof IndexVar){
            typeCheck((IndexVar)var);
        }
    }

    public void typeCheck(VarDec varDec){
        if(varDec instanceof SimpleDec){
            typeCheck((SimpleDec)varDec);
        } else if(varDec instanceof ArrayDec){
            typeCheck((ArrayDec)varDec);
        }
    }

    public void typeCheck(Exp exp){
        if(exp instanceof IfExp){
            typeCheck((IfExp) exp);
        } else if(exp instanceof WhileExp){
            typeCheck((WhileExp) exp);
        } else if(exp instanceof ReturnExp){
            typeCheck((ReturnExp) exp);
        } else if(exp instanceof CompoundExp){
            typeCheck((CompoundExp) exp);
        } else if(exp instanceof AssignExp){
            typeCheck((AssignExp) exp);
        } else if(exp instanceof OpExp){
            typeCheck((OpExp) exp);
        } else if(exp instanceof CallExp){
            typeCheck((CallExp) exp);
        } else if(exp instanceof IntExp){
            typeCheck((IntExp) exp);
        } else if(exp instanceof VarExp){
            typeCheck((VarExp) exp);
        } else if(exp instanceof BoolExp){
            typeCheck((BoolExp) exp);
        } else if(exp instanceof NilExp){
            typeCheck((NilExp) exp);
        }
    }


    // Extended classes

    // Dec Subclasses
    public void typeCheck(FunctionDec funDec){

        String id = funDec.func;
        int type = funDec.result.typ;
        VarDecList paramList = funDec.params;
        ArrayList<Symbol> params = new ArrayList<Symbol>();

        // Convert the VarDecList of params to an ArrayList with ArrSym and VarSym
        while(paramList != null){
            if(paramList.head instanceof SimpleDec){
                SimpleDec temp = (SimpleDec)paramList.head;
                params.add(new VarSym(temp.name, temp.typ.typ));
            } else if(paramList.head instanceof ArrayDec){
                ArrayDec temp = (ArrayDec)paramList.head;
                params.add(new ArrSym(temp.name, temp.typ.typ, 0));
            }

            paramList = paramList.tail;
        }

        // Create new symbol and new scope
        Symbol sym = new FunSym(id, type, params);
        symbolTable.addSymbol(id, sym);
        setReturn(type);
        symbolTable.newScope();
        

        // Check is this a function after main that is not named main
        if(this.haveMain){
            setLastMain(false);
        }

        // Check if it is main function
        if(id.equals("main")){
            setMain();
            setLastMain(true);
        }

        // Finally, typeCheck the params list and the function body
        typeCheck(funDec.params);
        typeCheck(funDec.body);
    }


    // Var Subclasses
    public void typeCheck(SimpleVar var){

        if(symbolTable.symExists(var.name) == -1){

            System.err.println("Error: Undefined variable '" + var.name + "' on line " + var.pos);
            setError();

        } else {

            Symbol sym = symbolTable.getSymbol(var.name);
            // Checking that it is type VarSymbol
            if(sym instanceof VarSym){

                // Next, check that it is not void
                if(sym.type == NameTy.VOID){

                    System.err.println("Error: Use of void variable '" + var.name + "' on line " + var.pos);
                    setError();

                }

            } else {

                System.err.println("Error: Cannot convert array '" + var.name + "' to variable type on line " + var.pos);
                setError();
            }
        }
    }

    public void typeCheck(IndexVar var){

        if(var.index instanceof IntExp){
            System.err.println("Error: Array index of variable '" + var.name + "' must have type int on line " + var.pos);
            setError();
        }

        if(symbolTable.symExists(var.name) == -1){

            System.err.println("Error: Undefined variable '" + var.name + "' on line " + var.pos);
            setError();

        } else {

            Symbol sym = symbolTable.getSymbol(var.name);
            // Checking that it is type ArrSym
            if(!(sym instanceof ArrSym)){
                System.err.println("Error: '" + var.name +  "' referenced as array but is not an array on line " + var.pos);
                setError();
            }
        }

        typeCheck(var.index);
    }

    // VarDec Subclasses
    public void typeCheck(SimpleDec dec){

        String id = dec.name;
        int type = dec.typ.typ;

        if(symbolTable.symExists(id) == -1){

            if(type == NameTy.VOID){
                System.err.println("Error: Variable '" + id + "' is void on line " + dec.pos);
                setError();
            } else {
                Symbol sym = new VarSym(id, type);
                symbolTable.addSymbol(id, sym);
            }

        } else {
            System.err.println("Error: Cannot declare variable '" + id + "' on line " + dec.pos + " more than once");
            setError();
        }
    }

    public void typeCheck(ArrayDec dec){

        String id = dec.name;
        int type = dec.typ.typ;
        int arrSize = dec.size;

        if(symbolTable.symExists(id) == -1){

            if(type == NameTy.VOID){
                System.err.println("Error: Variable '" + id + "' is void on line " + dec.pos);
                setError();
            } else {
                Symbol sym = new ArrSym(id, type, arrSize);
                symbolTable.addSymbol(id, sym);
            }
        } else {
            System.err.println("Error: Cannot declare variable '" + id + "' on line " + dec.pos + " more than once");
            setError();
        }
    }

    

    // Exp Subclasses
    public void typeCheck(IfExp exp){
        typeCheck(exp.test);
        typeCheck(exp.thenpart);

        if(exp.elsepart != null){
            typeCheck(exp.elsepart);
        }
    }

    public void typeCheck(WhileExp exp){
        typeCheck(exp.test);
        typeCheck(exp.body);
    }

    public void typeCheck(ReturnExp exp){

        if(returnType == NameTy.VOID){
            if(exp.exp != null){
                System.err.println("Error: Funcion with VOID return type returns value on line " + (exp.pos + 1));
                setError();
            }
        } else {
            if(exp.exp == null){
                System.err.println("Error: Function with non-VOID return type returns nothing on line " + (exp.pos + 1));
                setError();
            } else {
                typeCheck(exp.exp);
            }
        }
    }


    public void typeCheck(CompoundExp exp){

        symbolTable.newScope();
        typeCheck(exp.decList);
        typeCheck(exp.exp);
        symbolTable.removeScope();
    }

    public void typeCheck(AssignExp exp){
        typeCheck(exp.lhs);
        typeCheck(exp.rhs);
    }

    public void typeCheck(OpExp exp){

        Boolean checkOp1 = (exp.left instanceof BoolExp) && (exp.right instanceof IntExp);
        Boolean checkOp2 = (exp.left instanceof IntExp) && (exp.right instanceof BoolExp);

        if(checkOp1 || checkOp2){
            System.err.println("Error: Operation expression on line " + (exp.pos + 1) + " must be int (op) int or bool (op) bool");
            setError();
        } else {

            // Operation on two integers
            if(exp.left instanceof IntExp){
                if(exp.op == OpExp.OR || exp.op == OpExp.AND || exp.op == OpExp.NOT){ // Not is !() not !=
                    System.err.println("Error: Operator on line " + (exp.pos + 1) + " cannot be used with type int");
                    setError();
                }

            // Operation on two booleans
            } else if(exp.left instanceof BoolExp){
                if(!(exp.op == OpExp.OR || exp.op == OpExp.AND || exp.op == OpExp.NOT || exp.op == OpExp.EQEQ)){
                    System.err.println("Error: Operator on line " + (exp.pos + 1) + " cannot be used with type bool");
                    setError();
                }
            }
        }

        typeCheck(exp.left);
        typeCheck(exp.right);
    }

    public void typeCheck(CallExp exp){

        String id = exp.func;

        if(symbolTable.funcExists(id)){

            if(!(symbolTable.getFunc(id) instanceof FunSym)){
                System.err.println("Error: Symbol '" + id + "' not a function on line " + (exp.pos + 1));
                setError();
                return;
            }

            // Check return types
            /*
            if((retType == NameTy.VOID) && (symbolTable.getFunc(id).type != NameTy.VOID)){
                System.err.println("Error: Function '" + id + "' expects void but returns a value at line " + (exp.pos + 1));
                setError();
            }

            if((retType == NameTy.INT) && (symbolTable.getFunc(id).type != NameTy.INT)){
                System.err.println("Error: Function '" + id + "' expects INT but returns a different value at line " + (exp.pos + 1));
                setError();
            }

            if((retType == NameTy.BOOL) && (symbolTable.getFunc(id).type != NameTy.BOOL)){
                System.err.println("Error: Function '" + id + "' expects void but returns a different value at line " + (exp.pos + 1));
                setError();
            }
            */

            // Param validation
            FunSym funSym = (FunSym)symbolTable.getFunc(id);
            int callExpNum = exp.getNumParams();
            int funExpNum = symbolTable.getNumFuncParams(funSym.id);
            ExpList paramsCopy = exp.args;

            for(int i = 0; i < funExpNum; i++){
                Exp parameter = paramsCopy.head;
                Symbol sym = funSym.params.get(i);

                if(sym instanceof VarSym){
                    typeCheck(parameter);
                }
                paramsCopy = paramsCopy.tail;
            }

        } else {

            System.err.println("Error: Undefined function '" + id + "' on line " + (exp.pos + 1));
            setError();
        }
    }

    public void typeCheck(IntExp exp){

    }

    public void typeCheck(BoolExp exp){

    }

    public void typeCheck(VarExp exp){
        typeCheck(exp.variable);
    }

    public void typeCheck(NilExp exp){

    }


    // Extras
    private void typeCheck(VarDecList list){
        while(list != null){
            if(list.head != null){
                typeCheck(list.head);
            }
            list = list.tail;
        }
    }

    private void typeCheck(ExpList list){
        while(list != null){
            if(list.head != null){
                typeCheck(list.head);
            }
            list = list.tail;
        }
    }

}