package absyn;

public interface AbsynVisitor {

  // This file just has calls to visit the other Absyn objects


  // NameTy extends Absyn
  public void visit(NameTy exp, int level);



  // Abstract Var and extensions of Var
  public void visit(Var exp, int level);

  public void visit(SimpleVar exp, int level);

  public void visit(IndexVar exp, int level);



  // Abstract Exp and extensions of Exp
  public void visit(Exp exp, int level);

  public void visit(NilExp exp, int level);

  public void visit(IntExp exp, int level);

  public void visit(BoolExp exp, int level);

  public void visit(VarExp exp, int level);

  public void visit(CallExp exp, int level);

  public void visit(OpExp exp, int level);

  public void visit(AssignExp exp, int level);

  public void visit(IfExp exp, int level);

  public void visit(WhileExp exp, int level);

  public void visit(ReturnExp exp, int level);

  public void visit(CompoundExp exp, int level);



  // Miscellaneous classes
  public void visit(DecList exp, int level);

  public void visit(VarDecList exp, int level);

  public void visit(ExpList exp, int level);



  // Abstract Dec and extensions of Dec
  public void visit(Dec exp, int level);

  public void visit(FunctionDec exp, int level);
  // VarDec extends Dec and is extended by SimpleDec and ArrayDec
  public void visit(VarDec exp, int level);

  public void visit(SimpleDec exp, int level);  

  public void visit(ArrayDec exp, int level);






}
