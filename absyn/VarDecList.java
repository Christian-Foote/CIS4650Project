package absyn;

public class VarDecList {
	public VarDec head;
	public VarDecList tail;

	public VarDecList (VarDec head, VarDecList tail){
		this.head = head;
		this.tail = tail;
	}

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}