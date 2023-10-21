package absyn;

public class NameTy extends Absyn {

    public int typ;
    public final static int BOOL = 0;
    public final static int INT = 1;
    public final static int VOID = 2;

    public NameTy(int pos, int typ){
        this.pos = pos;
        this.typ = typ;
    }

    public void accept(AbsynVisitor visitor, int level){
        visitor.visit(this, level);
    }
}