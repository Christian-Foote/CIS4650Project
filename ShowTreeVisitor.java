import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

  final static int SPACES = 4;

  private void indent( int level ) {
      for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
  }

  // NameTy extends Absyn
  public void visit(NameTy exp, int level){
    if(exp.typ == NameTy.BOOL) { 
      System.out.println("Type: BOOL");
    } else if(exp.typ == NameTy.INT) { 
      System.out.println("Type: INTEGER");
    } else if(exp.typ == NameTy.VOID) { 
      System.out.println("Type: VOID");
    }

  }

  // Abstract Var and extensions of Var
  public void visit(Var var, int level){
    if(var instanceof IndexVar){
      visit((IndexVar) var, level);
    } else if(var instanceof SimpleVar){
      visit((SimpleVar) var, level);
    } else {
      indent(level);
      System.out.println("Illegal expression: " + var.pos);
    }
  }

  public void visit(SimpleVar exp, int level){
      indent(level);
      System.out.println("SimpleVar: " + exp.name);
  }

  public void visit(IndexVar exp, int level){
      indent(level);
      System.out.println("IndexVar: " + exp.name);
      level++;
      /* exp.index.accept(this, level); */
      visit(exp.index, level);
  }


// Abstract Exp and extensions of Exp
  public void visit(Exp exp, int level){
    if(exp instanceof IfExp){
      visit((IfExp) exp, level);
    } else if(exp instanceof WhileExp){
      visit((WhileExp) exp, level);
    } else if(exp instanceof ReturnExp){
      visit((ReturnExp) exp, level);
    } else if(exp instanceof CompoundExp){
      visit((CompoundExp) exp, level);
    } else if(exp instanceof AssignExp){
      visit((AssignExp) exp, level);
    } else if(exp instanceof OpExp){
      visit((OpExp) exp, level);
    } else if(exp instanceof CallExp){
      visit((CallExp) exp, level);
    } else if(exp instanceof IntExp){
      visit((IntExp) exp, level);
    } else if(exp instanceof VarExp){
      visit((VarExp) exp, level);
    } else if(exp instanceof BoolExp){
      visit((BoolExp) exp, level);
    } else if(exp instanceof NilExp){
      visit((NilExp) exp, level);
    } else {
      indent(level);
      System.out.println("Illegal expression: " + exp.pos);
    }
  }

  public void visit(NilExp exp, int level){
      //indent(level);
      //System.out.println("NilExp");
  }

  public void visit(IntExp exp, int level){
      indent(level);
      System.out.println("IntExp: " + exp.value);
  }

  public void visit(BoolExp exp, int level){
      indent(level);
      System.out.println("BoolExp: " + exp.value);
  }

  public void visit(VarExp exp, int level){
      indent(level);
      System.out.println("VarExp: ");
      level++;
      visit(exp.variable, level);
  }

  public void visit(CallExp exp, int level){
      indent(level);
      System.out.println("CallExp: " + exp.func);
      level++;

      if(exp.args != null){
          exp.args.accept(this, level);
      }
  }

  public void visit( OpExp exp, int level ) {
      indent( level );
      System.out.print("OpExp:" ); 
      switch( exp.op ) {
        case OpExp.PLUS:
          System.out.println( " + " );
          break;
        case OpExp.MINUS:
          System.out.println( " - " );
          break;
        case OpExp.UMINUS:
          System.out.println( " - " );
          break;
        case OpExp.MUL:
          System.out.println( " * " );
          break;
        case OpExp.DIV:
          System.out.println( " / " );
          break;
        case OpExp.EQ:
          System.out.println( " = " );
          break;
        case OpExp.NE:
          System.out.println( " != " );
          break;
        case OpExp.LT:
          System.out.println( " < " );
          break;
        case OpExp.LE:
          System.out.println( " <= " );
          break;
        case OpExp.GT:
          System.out.println( " > " );
          break;
        case OpExp.GE:
          System.out.println( " <= " );
          break;
        case OpExp.NOT:
          System.out.println( "!" );
          break;
        case OpExp.AND:
          System.out.println( " && " );
          break;
        case OpExp.OR:
          System.out.println( " || " );
          break;
        case OpExp.EQEQ:
          System.out.println( " == " );
          break;
        default:
          System.out.println( "Unrecognized operator at " + exp.pos);
      }
      level++;
      if (exp.left != null){
          exp.left.accept( this, level ); }
      exp.right.accept( this, level );
    }

    public void visit( AssignExp exp, int level ) {
      indent( level );
      System.out.println( "AssignExp:" );
      level++;
      exp.lhs.accept( this, level );
      exp.rhs.accept( this, level );
    }

    public void visit( IfExp exp, int level ) {
      indent( level );
      System.out.println( "IfExp:" );
      level++;

      exp.test.accept( this, level );
      exp.thenpart.accept( this, level );
      if (exp.elsepart != null )
          exp.elsepart.accept( this, level );
    }

    public void visit(WhileExp exp, int level){
      indent(level);
      System.out.println("WhileExp: ");
      level++;
      
      visit(exp.test, level);
      visit(exp.body, level);
    }

    public void visit(ReturnExp exp, int level){
      indent(level);
      System.out.println("ReturnExp: ");
      level++;

      if(exp.exp != null){
          exp.exp.accept(this, level);
      }
    }

    public void visit(CompoundExp exp, int level){
      indent(level);
      System.out.println("CompoundExp: ");
      level++;
      /* if(exp.decList != null && exp.exp != null){
          level++;
      } */

      if(exp.decList != null){
          exp.decList.accept(this, level);
      }

      if(exp.exp != null){
          exp.exp.accept(this, level);
      }
    }

    // Miscellaneous classes
    public void visit(DecList decList, int level){
      while(decList != null){
          if(decList.head != null){
              decList.head.accept(this, level);
          }

          decList = decList.tail;
      }
    }

    public void visit(VarDecList varDecList, int level){
      while(varDecList != null){
          if(varDecList.head != null){
              varDecList.head.accept(this, level); 
              /* visit(varDecList.head, level); */
          }
          varDecList = varDecList.tail;
      }
    }

    public void visit(ExpList expList, int level){
      while(expList != null){
          if(expList.head != null){
              expList.head.accept(this, level);
          }

          expList = expList.tail;
      }
    }

    // Abstract Dec and extensions of Dec
    public void visit(Dec dec, int level){
      if(dec instanceof VarDec){
        visit((VarDec) dec, level);
      } else if(dec instanceof FunctionDec){
        visit((FunctionDec) dec, level);
      } else {
        indent(level);
        System.out.println("Illegal expression: " + dec.pos);
      }
    }

    public void visit(FunctionDec funDec, int level){
      indent(level);
      System.out.println("FunctionDec: ");
      level++;
      indent(level);
      visit(funDec.result, level);
      indent(level);
      System.out.println("Function: " + funDec.func);
      /* visit(funDec.params, level);  */
      if (funDec.params != null) {
        funDec.params.accept(this, level);
      }
      /* funDec.body.accept(this, level); */
      visit(funDec.body, level);

    }

    // VarDec extends Dec and is extended by SimpleDec and ArrayDec
    public void visit(VarDec varDec, int level){
      if(varDec instanceof SimpleDec){
        visit((SimpleDec) varDec, level);
      } else if(varDec instanceof ArrayDec){
        visit((ArrayDec) varDec, level);
      } else {
        indent(level);
        System.out.println("Illegal expression: " + varDec.pos);
      }
    }

    public void visit(SimpleDec simDec, int level){
      indent(level);
      System.out.println("Simple Dec: ");
      level++;
      indent(level);
      visit(simDec.typ, level);
      indent(level);
      System.out.println("Name: " + simDec.name);
    }

    public void visit(ArrayDec arrDec, int level){
      indent(level);
      System.out.println("ArrayDec: ");
      level++;
      indent(level);
      visit(arrDec.typ, level);
      indent(level);
      System.out.println("Name: " + arrDec.name);
      if(arrDec != null){
        indent(level);
        System.out.println("Size: " + arrDec.size);
      }
    }
}
