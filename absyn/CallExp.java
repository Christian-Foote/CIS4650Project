package absyn;

public class CallExp extends Exp {
    public String func;
    public ExpList args;

    public CallExp (int pos, String func, ExpList args){
        this.pos = pos;
        this.func = func;
        this.args = args;
    }
    
    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }

    public int getNumParams(){
        int count = 0;
        ExpList temp = this.args;

        while(temp != null){
            temp = temp.tail;
            count++;
        }
        return count;
    }
}