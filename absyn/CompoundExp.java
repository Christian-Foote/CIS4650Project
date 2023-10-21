package absyn;

public class CompoundExp extends Exp
{
    public VarDecList decList;
    public ExpList exp;

    public CompoundExp (int pos, VarDecList decList, ExpList exp)
    {
        this.pos = pos;
        this.decList = decList;
        this.exp = exp;
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit( this, level );
    }
}