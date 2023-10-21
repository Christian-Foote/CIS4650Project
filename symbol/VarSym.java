package symbol;

public class VarSym extends Symbol{

    public int offset;

    public VarSym(String id, int type){
        super(id, type);
    }

    public VarSym(String id, int type, int offset){
        super(id, type);
        this.offset = offset;
    }
}