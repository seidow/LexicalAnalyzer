


import java.io.*;

public class LexicalAnalyzer {

    private static final String[] RESERVED_WORDS = {
        "abstract", "assert", "boolean", "break", "byte",
        "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else",
        "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import",
        "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super",
        "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while"
    };

    private int state = 0;
    private int lookahead;
    private BufferedReader input;
    private BufferedWriter output;
    private StringBuilder lexeme;

    public static void main(String[] args) throws IOException {
        LexicalAnalyzer analyzer = new LexicalAnalyzer();
        analyzer.output.write("Lexemes \t Tokens\n");
        analyzer.output.newLine();
        analyzer.tokenize();
        System.out.println("Tokens have been identified successfully.");
    }

    public LexicalAnalyzer() {
        try {
            input = new BufferedReader(new FileReader("input.txt"));
            output = new BufferedWriter(new FileWriter("output.txt"));
        } catch (IOException e) {
            System.err.println("Error opening file: " + e.getMessage());
            System.exit(1);
        }
    }

    public void tokenize() {
        try {
            lookahead = input.read();
            while (lookahead != -1) {
                switch (state) {
                    case 0 -> {
                        if (Character.isWhitespace((char) lookahead)) {
                            lookahead = input.read();
                        } else if (Character.isLetter((char) lookahead) || lookahead == '_' || lookahead == '$') {
                            state = 1;
                        } else if (Character.isDigit((char) lookahead)) {
                            processNumber();
                            state = 0;
                        } else {
                            switch (lookahead) {
                                case '&' -> state = 3;
                                case '|' -> state = 6;
                                case '~' -> state = 9;
                                case '!' -> state = 10;
                                case '^' -> state = 13;
                                case '+' -> state = 14;
                                case '-' -> state = 18;
                                case '*' -> state = 22;
                                case '%' -> state = 25;
                                case '/' -> state = 28;
                                case '<' -> state = 31;
                                case '>' -> state = 34;
                                case '=' -> state = 37;
                                case '?' -> state = 40;
                                case ':' -> state = 41;
                                case ';' -> state = 42;
                                case ',' -> state = 43;
                                case '(' -> state = 44;
                                case ')' -> state = 45;
                                case '{' -> state = 46;
                                case '}' -> state = 47;
                                case '[' -> state = 48;
                                case ']' -> state = 49;
                                case '\'' -> state = 50;
                                case '\"' -> state = 51;
                                default -> error("Unrecognized symbol '" + (char) lookahead + "'");
                            }
                        }
                    }

                    case 1 -> {
                        processIdentifier();
                        state = 0;
                    }

                    case 3 -> {
                        // State for handling '&' operators: differentiate between bitwise '&' and logical '&&'
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        lookahead = input.read();
                        if (lookahead == '&') {
                            lexeme.append((char) lookahead);
                            writeToken(lexeme.toString(), "LogicAnd_Op");
                            lookahead = input.read();
                        } else {
                            writeToken(lexeme.toString(), "BitwiseAnd_Op");
                        }
                        state = 0;
                    }
             
                    case 6 -> {
                        // State for handling '&' operators: differentiate between bitwise '&' and logical '&&'
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead); //lexeme = '/'
                        lookahead = input.read(); //lookaheed = /
                        if (lookahead == '|') {
                            lexeme.append((char) lookahead); //lexeme = '//'
                            writeToken(lexeme.toString(), "LogicOr_Op");
                            lookahead = input.read();
                        } else {
                            writeToken(lexeme.toString(), "BitwiseOr_Op");
                        }
                        state = 0;
                    }
                    case 9 -> {
                        //State for handling Bitwise NOT
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "BitwiseNot_Op");
                        lookahead = input.read();
                        state = 0;
                    }
                    case 10 -> {
                        // State for handling '!' operators: differentiate between Logical '!' and relational '!='
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        lookahead = input.read();
                        if (lookahead == '=') {
                            lexeme.append((char) lookahead);
                            writeToken(lexeme.toString(), "Not_Equal");
                            lookahead = input.read();
                        } else {
                            writeToken(lexeme.toString(), "LogicalNot_Op");
                        }
                        state = 0;
                    }
                        
                    case 13 -> {
                        //State for handling Bitwise XOR
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "BitwiseXOR_Op");
                        lookahead = input.read();
                        state = 0;
                    }

                    case 14 -> {
                        // State for handling '+' operators: +=, ++, +
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        lookahead = input.read();
                        switch (lookahead) {
                            case '=' -> {
                                lexeme.append((char) lookahead);
                                writeToken(lexeme.toString(), "PlusAssign_Op");
                                lookahead = input.read();
                            }
                            case '+' -> {
                                lexeme.append((char) lookahead);
                                writeToken(lexeme.toString(), "inc_Op");
                                lookahead = input.read();
                            }
                            default ->
                                writeToken(lexeme.toString(), "Plus_Op");
                        }
                        state = 0;
                    }

                    case 18 -> {
                        // State for handling '-' operators: -=, --, -
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        lookahead = input.read();
                        switch (lookahead) {
                            case '=' -> {
                                lexeme.append((char) lookahead);
                                writeToken(lexeme.toString(), "MinusAssign_Op");
                                lookahead = input.read();
                            }
                            case '-' -> {
                                lexeme.append((char) lookahead);
                                writeToken(lexeme.toString(), "dec_Op");
                                lookahead = input.read();
                            }
                            default ->
                                writeToken(lexeme.toString(), "Minus_Op");
                        }
                        state = 0;
                    }

                    case 22 -> {
                        // State for handling '*' operators: *=, *
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        lookahead = input.read();
                        if (lookahead == '=') {
                            lexeme.append((char) lookahead);
                            writeToken(lexeme.toString(), "MultAssign_Op");
                            lookahead = input.read();
                        } else {
                            writeToken(lexeme.toString(), "Mult_Op");
                        }
                        state = 0;
                    }

                    case 25 -> {
                        // State for handling '%' operators: %=, %
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        lookahead = input.read();
                        if (lookahead == '=') {
                            lexeme.append((char) lookahead);
                            writeToken(lexeme.toString(), "ModAssign_Op");
                            lookahead = input.read();
                        } else {
                            writeToken(lexeme.toString(), "Mod_Op");
                        }
                        state = 0;
                    }
                        
                    case 28 -> {
                        // State for handling '/' operators: /=, /
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        lookahead = input.read();
                        if (lookahead == '=') {
                            lexeme.append((char) lookahead);
                            writeToken(lexeme.toString(), "DivAssign_Op");
                            lookahead = input.read();
                        } else {
                            writeToken(lexeme.toString(), "Div_Op");
                        }
                        state = 0;
                    }
                        
                    case 31 -> {
                        // State for handling '<' operators: <=, <
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        lookahead = input.read();
                        if (lookahead == '=') {
                            lexeme.append((char) lookahead);
                            writeToken(lexeme.toString(), "LessT_Equal");
                            lookahead = input.read();
                        } else {
                            writeToken(lexeme.toString(), "LessT_Op");
                        }
                        state = 0;
                    }
                        
                    case 34 -> {
                        // State for handling '>' operators: >=, >
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        lookahead = input.read();
                        if (lookahead == '=') {
                            lexeme.append((char) lookahead);
                            writeToken(lexeme.toString(), "GreaterT_Equal");
                            lookahead = input.read();
                        } else {
                            writeToken(lexeme.toString(), "GreaterT_Op");
                        }
                        state = 0;
                    }

                    case 37 -> {
                        // State for handling '=' operators: ==, =
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        lookahead = input.read();
                        if (lookahead == '=') {
                            lexeme.append((char) lookahead);
                            writeToken(lexeme.toString(), "Equality_Op");
                            lookahead = input.read();
                        } else {
                            writeToken(lexeme.toString(), "Assign_Op");
                        }
                        state = 0;
                    }

                    case 40 -> {
                        //State for handling ternary operator ?
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "Ternary_Question_Op");
                        lookahead = input.read();
                        state = 0;
                    }
 
                    case 41 -> {
                        //State for handling ternary operator :
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "Ternary_Colon_Op");
                        lookahead = input.read();
                        state = 0;
                    }

                    case 42 -> {
                        //State for punctuation ;
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "semi_colon");
                        lookahead = input.read();
                        state = 0;
                    }

                    case 43 -> {
                        //State for punctuation ,
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "comma");
                        lookahead = input.read();
                        state = 0;
                    }

                    case 44 -> {
                        //State for punctuation (
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "left_Paranthesis");
                        lookahead = input.read();
                        state = 0;
                    }
                        
                    case 45 -> {
                        //State for punctuation )
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "Right_Paranthesis");
                        lookahead = input.read();
                        state = 0;
                    }
                    case 46 -> {
                        //State for punctuation {
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "Left_Curly_Bracket");
                        lookahead = input.read();
                        state = 0;
                    }
                        
                    case 47 -> {
                        //State for punctuation )
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "Right_Curly_Bracket");
                        lookahead = input.read();
                        state = 0;
                    }
                        
                    case 48 -> {
                        //State for punctuation {
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "Left_Square_Bracket");
                        lookahead = input.read();
                        state = 0;
                    }
                        
                    case 49 -> {
                        //State for punctuation )
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        writeToken(lexeme.toString(), "Right_Square_Bracket");
                        lookahead = input.read();
                        state = 0;
                    }

                     case 50 -> {
                         //State for handling single quote
                         lexeme = new StringBuilder();
                         lexeme.append((char) lookahead);
                         lookahead = input.read();
                         if (lookahead == -1) {
                             error("Unclosed character literal");
                             state = 0;
                             break;
                         }
                         if (lookahead == '\\') {
                             lexeme.append((char) lookahead);
                             lookahead = input.read();
                             if (lookahead == -1) {
                                 error("Unclosed character literal after escape");
                                 state = 0;
                                 break;
                             }
                             if ("'\"\\btnrf".indexOf((char) lookahead) == -1) {
                                 error("Invalid escape sequence '\\" + (char) lookahead + "' in character literal");
                                 state = 0;
                                 break;
                             }
                             lexeme.append((char) lookahead);
                         } else {
                             lexeme.append((char) lookahead);
                         }
                         lookahead = input.read();
                         if (lookahead == '\'') {
                             lexeme.append((char) lookahead);
                             writeToken(lexeme.toString(), "CHAR_LITERAL");
                             lookahead = input.read();
                         } else {
                             error("Missing closing quote for character literal");
                         }
                         state = 0;
                    }

                    case 51 -> {
                        //State for handling string literals ""
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);
                        lookahead = input.read();
                        OUTER:
                        while (lookahead != -1) {
                            switch (lookahead) {
                                case '\"' -> {
                                    lexeme.append((char) lookahead);
                                    writeToken(lexeme.toString(), "STRING_LITERAL");
                                    lookahead = input.read();
                                    state = 0;
                                    break OUTER;
                                }
                                case '\\' -> {
                                    lexeme.append((char) lookahead);
                                    lookahead = input.read();
                                    if (lookahead == -1) {
                                        error("Unclosed string literal after escape");
                                        state = 0;
                                        break OUTER;
                                    }
                                    if ("\"\\btnrf".indexOf((char) lookahead) == -1) {
                                        error("Invalid escape sequence '\\" + (char) lookahead + "' in string literal");
                                        state = 0;
                                        break OUTER;
                                    }
                                    lexeme.append((char) lookahead);
                                    lookahead = input.read();
                                }
                                default -> {
                                    lexeme.append((char) lookahead);
                                    lookahead = input.read();
                                }
                            }
                        }
                        if (lookahead == -1) {
                            error("Unclosed string literal");
                            state = 0;
                        }
                    }


                    default -> {
                        error("Unexpected state: " + state);
                        state = 0;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        } finally {
            try {
                if (input != null) input.close();
                if (output != null) output.close();
            } catch (IOException e) {
                System.err.println("Error closing files: " + e.getMessage());
            }
        }
    }
    
    //Function for processing identifiers
    private void processIdentifier() throws IOException {
        lexeme = new StringBuilder();
        while (lookahead != -1 && (Character.isLetterOrDigit((char) lookahead) || lookahead == '_' || lookahead == '$')) {
            lexeme.append((char) lookahead);
            lookahead = input.read();
        }
        String tokenLexeme = lexeme.toString();
        writeToken(tokenLexeme, isReserved(tokenLexeme) ? tokenLexeme : "ID");
    }
    
    //Function for processing numbers: digits and floats
    private void processNumber() throws IOException {
        lexeme = new StringBuilder();
        if (!Character.isDigit((char) lookahead)) {
            error("Numeric literal must start with a digit");
            state = 0;
            return;
        }
        while (Character.isDigit((char) lookahead)) {
            lexeme.append((char) lookahead);
            lookahead = input.read();
        }
        boolean isFloat = false;
        if (lookahead == '.') {
            isFloat = true;
            lexeme.append('.');
            lookahead = input.read();
            if (!Character.isDigit((char) lookahead)) {
                error("Invalid numeric literal: decimal point must be followed by digits");
                state = 0;
                return;
            }
            while (Character.isDigit((char) lookahead)) {
                lexeme.append((char) lookahead);
                lookahead = input.read();
            }
        }
        if (lookahead == 'e' || lookahead == 'E') {
            isFloat = true;
            lexeme.append((char) lookahead);
            lookahead = input.read();
            if (lookahead == '+' || lookahead == '-') {
                lexeme.append((char) lookahead);
                lookahead = input.read();
            }
            if (!Character.isDigit((char) lookahead)) {
                error("Invalid numeric literal: exponent must be followed by digits");
                state = 0;
                return;
            }
            while (Character.isDigit((char) lookahead)) {
                lexeme.append((char) lookahead);
                lookahead = input.read();
            }
        }
        if (lookahead == 'f' || lookahead == 'F' || lookahead == 'd' || lookahead == 'D') {
            isFloat = true;
            lexeme.append((char) lookahead);
            lookahead = input.read();
            writeToken(lexeme.toString(), lookahead == 'f' || lookahead == 'F' ? "FLOAT_LITERAL" : "DOUBLE_LITERAL");
        } else {
            writeToken(lexeme.toString(), isFloat ? "DOUBLE_LITERAL" : "INT_LITERAL");
        }
    }

    //Function for writing tokens in the output file
    private void writeToken(String lexeme, String tokenType) throws IOException {
        output.write(lexeme + "\t\t" + tokenType);
        output.newLine();
    }

 
    private boolean isReserved(String lexeme) {
        for (String word : RESERVED_WORDS) {
            if (word.equals(lexeme)) {
                return true;
            }
        }
        return false;
    }

    private void error(String message) throws IOException {
        System.err.println("Error: " + message);
        lookahead = input.read();
    }
}
