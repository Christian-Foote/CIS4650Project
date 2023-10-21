package symbol;

public abstract class Symbol{

    public String id;
    public int type;

    public Symbol(String id, int type){
        this.id = id;
        this.type = type;
    }
}