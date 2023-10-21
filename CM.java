/*
  Created by:
  Drew Mainprize
  Christian Foote
  File Name: Main.java
  To Build: 
  After the Scanner.java, tiny.flex, and tiny.cup have been processed, do:
    javac Main.java
  
  To Run: 
    java -classpath /usr/share/java/cup.jar:. Main gcd.tiny
  where gcd.tiny is an test input file for the tiny language.
*/
   
import java.io.*;
import absyn.*;
   
class CM {
  public static String fName = "";
  public static boolean SHOW_TREE = false;
  public static boolean SHOW_TABLE = false;
  public static boolean CODE_GEN = false;

  static public void main(String argv[]) {    

    for (String arg: argv) {
      if (arg.equals("-a")) {
        SHOW_TREE = true;
      }
      else if (arg.equals("-s")) {
        SHOW_TABLE = true;
      }
      else if (arg.equals("-c")) {
        CODE_GEN = true;
      }
      else if (arg.endsWith(".cm")) {
        fName = arg;
      }
      else {
        System.out.println(arg + "is an invalid argument.");
      }
    }

    /* Start the parser */
    try {
      parser p = new parser(new Lexer(new FileReader(fName)));
      p.SHOW_TREE = SHOW_TREE;
      p.SHOW_TABLE = SHOW_TABLE;
      p.CODE_GEN = CODE_GEN;
      p.fName = fName;
      
      Absyn result = (Absyn)(p.parse().value);

       if (SHOW_TREE && result != null) {
         System.out.println("The abstract syntax tree is:");
         ShowTreeVisitor visitor = new ShowTreeVisitor();
         result.accept(visitor, 0); 
      }

    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}
