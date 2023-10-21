package symbol;

import java.util.ArrayList;

public class FunSym extends Symbol{
    public ArrayList<Symbol> params;
    public int address;

    public FunSym(String id, int type, ArrayList<Symbol> params, int address){
        super(id, type);
        this.params = params;
        this.address = address;
    }

    public FunSym(String id, int type, ArrayList<Symbol> params){
        super(id, type);
        this.params = params;
    }
}

