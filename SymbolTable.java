import absyn.*;
import symbol.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

public class SymbolTable{

    final static int SPACES = 4;

    public ArrayList<HashMap<String, Symbol>> symbolTable;
    public boolean showScope;

    // Constructor to init symbolTable
    public SymbolTable(boolean showScope){
        symbolTable = new ArrayList<HashMap<String, Symbol>>();
        this.showScope = showScope;
    }

    // Calculates and returns a string carrying the proper indent
    private String indent( int level ) {
        String indStr = "";
        for(int i = 0; i < level * SPACES; i++){
            indStr += (" ");
        }

        return indStr;
    }

    // Returns the type, either void or int
    // Gets type from NameTy in absyn
    private String getType(int type){
        if(type == NameTy.BOOL){
            return "BOOL";
        } else if(type == NameTy.INT){
            return "INT";
        } else {
            return "VOID";
        }
    }

    // Returns the current size of the symbol table
    private int getSize(){
        return symbolTable.size();
    }

    // Helper function that gets the current scope of the table
    private HashMap<String, Symbol> getCurrentScope(int id){
        return symbolTable.get(id);
    }

    // Adds the symbol to the current node in the symbol table
    public void addSymbol(String id, Symbol sym){
        HashMap<String, Symbol> currNode = symbolTable.get(getSize() - 1);
        //currNode.put(id, sym);
        getCurrentScope(getSize() - 1).put(id, sym);
    }

    // Searches the hashmap for the symbol and returns it if found
    public Symbol getSymbol(String sym){

        for(int i = getSize() - 1; i >= 0; i--){
            if(symbolTable.get(i).containsKey(sym)){
                return symbolTable.get(i).get(sym);
            }
        }

        return null;
    }

    // Print the current scope
    public void printScope(int level){
        
        Set<String> tableKeys = symbolTable.get(getSize() - 1).keySet();

        for(String key: tableKeys){
            // Get the current symbol
            Symbol sym = symbolTable.get(getSize() - 1).get(key);

            // Print Variable Symbols
            if(sym instanceof VarSym){

                System.out.println(indent(level) + "Variable: " + getType(sym.type) + " " + key);

            // Print Array Symbols
            } else if(sym instanceof ArrSym){

                ArrSym arraySym = (ArrSym)sym;
                System.out.println(indent(level) + "Array: " + getType(sym.type) + " " + key + "[" + arraySym.size + "]");

            // Print Function Symbols and also parse their parameters lists
            } else if(sym instanceof FunSym){

                System.out.print(indent(level) + "Function: " + getType(sym.type) + " " + key);
                String paramString = "(";
                int count = 0;
                // Parse through params array
                for(Symbol paramSym : ((FunSym)sym).params){

                    // Adds a comma w/ space only if its not before the first parameter
                    if(count != 0){
                        paramString += ", ";
                    } else {
                        count++;
                    }

                    if(paramSym instanceof VarSym){

                        if(paramSym.type == NameTy.BOOL){
                            paramString += "BOOL";
                        } else if(paramSym.type == NameTy.INT){
                            paramString += "INT";
                        } else {
                            paramString += "VOID";
                        }

                    } else if(paramSym instanceof ArrSym){

                        paramString += getType(paramSym.type) + "[]";
                    }

                }

                paramString += ")";
                System.out.println(paramString);
            }
        }
    }



    // Creates a new HashMap and appends it to the existing SymbolTable
    public void newScope(){

        symbolTable.add(new HashMap<String, Symbol>());

        if(showScope){
            System.out.println(indent(getSize() - 1) + "Entering scope " + (getSize() - 1));
        }
    }

    // Removes and exits the current scope
    public void removeScope(){

        if(showScope){
            System.out.println(indent(getSize() - 1) + "Exiting scope " + (getSize() - 1));
            printScope(getSize());
        }

        if(getSize() > 0){
            symbolTable.remove(getSize() - 1);
        }
    }

    // Helper functions for SemanticAnalyzer

    // Parse whole symbol table for given symbol and return it's location
    public int symExists(String sym){
        for(int i = getSize() - 1; i >= 0; i--){
            if(symbolTable.get(i).containsKey(sym)){
                return i;
            }
        }
        return -1;
    }

    // Returns whether or not the symbol is in the current scope
    public boolean symCurrentScope(String sym){
        return getCurrentScope(getSize() - 1).containsKey(sym);
    }

    // Returns whether or not a function exists
    public boolean funcExists(String sym){
        return symbolTable.get(0).containsKey(sym);
    }

    public Symbol getFunc(String sym){
        return symbolTable.get(0).get(sym);
    }

    public int getNumFuncParams(String sym){
        FunSym func = (FunSym)getFunc(sym);

        if(func != null){
            return func.params.size();
        } else {
            return 0;
        }
    }

}
