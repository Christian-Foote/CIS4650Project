package symbol;

public class ArrSym extends Symbol{

    public int offset;
    public int size;

    public ArrSym(String id, int type, int size, int offset){
        super(id, type);
        this.size = size;
        this.offset = offset;
    }

    public ArrSym(String id, int type, int size){
        super(id, type);
        this.size = size;
    }
}