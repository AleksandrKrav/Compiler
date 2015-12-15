import analyzers.LexicalAnalyser;
import analyzers.Parser;
import analyzers.Simantic;
import enums.Types;

import java.util.*;

public class Main {

    public static void main(String args[]) {

        String string = "var a , b, ab: integer; \n " + "  c: real; \n" +
                "begin \n" +
                "    b := 3; \n" +
                "    ab := 2; \n" +
//                "2; \n" +
////                "  \n"+
                "for i:=1 to a do \n" +
                "  begin \n" +
                "    if (b+ 2/4 < 4) then \n" +
                "       begin  \n" +
                "       while (a < 6.7) do \n" +
                "       writeln(ab); \n" +
                "       end;  \n" +
                "    b := b +1; \n" +
                "  end; \n" +
//                "while ( i < a ) do \n" +
//                "  b:= b + 2; \n" +
//                "                \n" +
//
//                "b := a; \n" +
//                " else  \n"+
//                "c := a;  \n"+
//                "a := (b); \n" +
                "writeln(a);\n" +
                "end.";
        ArrayList<String> commands;
        ArrayList<Types> types;

        LexicalAnalyser lexicalAnalyser = new LexicalAnalyser(string);
        commands = lexicalAnalyser.analyze();
        types = lexicalAnalyser.getTypes();

        Parser parser = new Parser(commands, types);
        parser.parse();
        Simantic s = new Simantic(parser.getNodes(), parser.getNodes());
        s.intialization();
        s.simanticParse(s.getFuncBegin());
        s.printResult("file.txt");

    }
}
