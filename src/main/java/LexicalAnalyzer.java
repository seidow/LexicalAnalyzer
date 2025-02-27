
import java.io.*;

public class LexicalAnalyzer {

    // Definition of reserved words (you can add more if needed)
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

    // Declaration of variables used
    private int state = 0;           // Initial state
    private int lookahead;           // To store the current character (EOF in Java is -1)
    private BufferedReader input;
    private BufferedWriter output;
    private StringBuilder lexeme;

    public static void main(String[] args) throws IOException {
        LexicalAnalyzer analyzer = new LexicalAnalyzer();
        analyzer.output.write("Lexemes \t Tokens\n");
        analyzer.output.newLine();
        analyzer.tokenize();
        System.out.println("Tokens have been identified successfully....!!!");
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
            // Read the first character
            lookahead = input.read();
            // Continue until end of file
            while (lookahead != -1) {
                switch (state) {
                    case 0 -> {
                        // Initial state: Skip spaces, tabs, and newline characters
                        if (lookahead == ' ' || lookahead == '\t' || lookahead == '\n' || lookahead == '\r') {
                            lookahead = input.read();
                        } else if (Character.isLetter((char) lookahead) || lookahead == '_' || lookahead == '$') {
                            // If the character is the first letter of an identifier, switch to identifier state
                            state = 1;

                        } else if (Character.isDigit((char) lookahead)) {
                            processNumber();
                            state = 0;
                        } else {
                            // Here we handle other symbols (operators, punctuation, comments, etc.)
                            // Condition for logical && and bitwise & operators
                            switch (lookahead) {
                                // For Logical AND operator and Bitwise AND
                                case '&' ->
                                    state = 3;
                                // For Logical OR operator and Bitwise OR
                                case '|' ->
                                    state = 6;
                                // For Bitwise NOT
                                case '~' ->
                                    state = 9;
                                // For Logical NOT operator (!) and Relational NOT Operator (!=)
                                case '!' ->
                                    state = 10;

                                // For Bitwise XOR (^)
                                case '^' ->
                                    state = 13;

                                // For Plus Operations: Arithmetic Assignment +=, Arithmetic +, and increment operator ++
                                case '+' ->
                                    state = 14;

                                // For Minus Operations: Arithmetic Assignment -=, Arithmetic -, and decrement operator --
                                case '-' ->
                                    state = 18;

                                // For * Operations: Arithmetic Assignment *=, and Arithmetic *
                                case '*' ->
                                    state = 22;

                                // For Module Operations: Arithmetic Assignment %=, and Arithmetic %
                                case '%' ->
                                    state = 25;

                                // For / Operations: Arithmetic Assignment /=, and Arithmetic /
                                case '/' ->
                                    state = 28;

                                // For < Operations: Relational <=, and <
                                case '<' ->
                                    state = 31;

                                // For < Operations: Relational >=, and >
                                case '>' ->
                                    state = 34;

                                // For < Operations: Relational ==, and Assignment =
                                case '=' ->
                                    state = 37;

                                // For ? ternary operator
                                case '?' ->
                                    state = 40;

                                // For : ternary operator
                                case ':' ->
                                    state = 41;

                                // For semi colon ;
                                case ';' ->
                                    state = 42;

                                // For comma
                                case ',' ->
                                    state = 43;

                                // For Left paranthesis
                                case '(' ->
                                    state = 44;

                                // For Right paranthesis
                                case ')' ->
                                    state = 45;

                                // For Left curly bracket
                                case '{' ->
                                    state = 46;

                                // For Right curly bracket
                                case '}' ->
                                    state = 47;

                                // For Left Square bracket
                                case '[' ->
                                    state = 48;

                                // For Right Square bracket
                                case ']' ->
                                    state = 49;

                                // For single quotes (Character Literal)
                                case '\'' ->
                                    state = 50;

                                // For Double quotes (String Literal)
                                case '\"' ->
                                    state = 51;

                                default -> {
                                }
                            }

                        }
                    }

                    case 1 -> {
                        // Identifier state: collect all letters and digits allowed in an identifier
                        processIdentifier();
                        state = 0; // Reset state to initial state after processing
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

                    // Handling single quotes (Character Literal)
                    case '\'' ->
                        state = 49;

                    case 50 -> {
                        // State for processing character literals
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead); // Append the opening single quote
                        lookahead = input.read(); // Read the next character

                        if (lookahead == -1) {
                            error();
                            state = 0;
                            break;
                        }

                        // Handle escape sequence or normal character
                        if (lookahead == '\\') {
                            lexeme.append((char) lookahead);
                            lookahead = input.read(); // Read the escape character

                            if (lookahead == -1) {
                                error();
                                state = 0;
                                break;
                            }

                            // Check for valid escape characters
                            if ("'\"\\btnrf".indexOf((char) lookahead) != -1) {
                                lexeme.append((char) lookahead);
                            } else {
                                error();
                                state = 0;
                                break;
                            }
                        } else {
                            lexeme.append((char) lookahead); // Append regular character
                        }

                        lookahead = input.read(); // Read the closing quote

                        if (lookahead == -1) {
                            error();
                            state = 0;
                            break;
                        }

                        if (lookahead == '\'') {
                            lexeme.append((char) lookahead);
                            writeToken(lexeme.toString(), "CHAR_LITERAL");
                        } else {
                            error();
                        }

                        lookahead = input.read(); // Move to the next character
                        state = 0;
                    }
                    case 51 -> {
                        lexeme = new StringBuilder();
                        lexeme.append((char) lookahead);  // Store the initial quote "
                        lookahead = input.read();  // Read next character
                        OUTER:
                        while (lookahead != -1) {
                            switch (lookahead) {
                                case '\"' -> {
                                    // End of string
                                    lexeme.append((char) lookahead);
                                    writeToken(lexeme.toString(), "STRING_LITERAL");
                                    lookahead = input.read();  // Move to next character
                                    state = 0;  // Reset state
                                    break OUTER;
                                }
                                case '\\' -> {
                                    // Escape sequence detected, move to State 52
                                    lexeme.append((char) lookahead);
                                    state = 52;
                                    lookahead = input.read();  // Move to next character
                                }
                                default -> {
                                    // Regular character inside string
                                    lexeme.append((char) lookahead);
                                    lookahead = input.read();  // Read next character
                                }
                            }
                        }

                        // If EOF before closing quote
                        if (lookahead == -1) {
                            error();
                            state = 0;
                        }
                    }

                    case 52 -> {
                        // Handle escape sequences
                        if (lookahead == -1) {
                            error();
                            state = 0;
                        } else if ("\"\\btnrf".indexOf((char) lookahead) != -1) {
                            // Valid escape sequence
                            lexeme.append((char) lookahead);
                            lookahead = input.read();  // Move to next character
                            state = 51;  // Return to processing the string
                        } else {
                            // Invalid escape sequence
                            error();
                            state = 0;
                        }
                    }

                    default -> {
                        // In case of an unexpected state
                        error();
                        state = 0;
                    }
                }
                // Additional cases can be added as needed (for example, for comments, string literals, etc.)
            }
            // Close files after processing
            output.close();
            input.close();
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }

    // Function to process identifiers
    private void processIdentifier() throws IOException {
        lexeme = new StringBuilder();
        // Collect letters, digits, "_", and the '$' character allowed in identifiers
        while (lookahead != -1 && (Character.isLetterOrDigit((char) lookahead) || lookahead == '_' || lookahead == '$')) {
            lexeme.append((char) lookahead);
            lookahead = input.read();
        }
        String tokenLexeme = lexeme.toString();
        // Check if the identifier is a reserved word
        if (isReserved(tokenLexeme)) {
            writeToken(tokenLexeme, tokenLexeme);
        } else {
            writeToken(tokenLexeme, "ID");
        }
    }

    // Function to process numbers (integer, floating-point, and scientific notation)
// This version supports an optional leading sign as well as exponent parts with a sign.
    private void processNumber() throws IOException {
        lexeme = new StringBuilder();

        // Optional leading sign
        if (lookahead == '+' || lookahead == '-') {
            lexeme.append((char) lookahead);
            lookahead = input.read();
            // Ensure a digit follows the sign
            if (lookahead == -1 || !Character.isDigit((char) lookahead)) {
                error();
                state = 0;
                return;
            }
        }

        // Read the integer part (must have at least one digit)
        if (!Character.isDigit((char) lookahead)) {
            error();
            state = 0;
            return;
        }
        while (lookahead != -1 && Character.isDigit((char) lookahead)) {
            lexeme.append((char) lookahead);
            lookahead = input.read();
        }

        boolean isFloat = false; // Flag to indicate floating-point number

        // Optional decimal part
        if (lookahead == '.') {
            isFloat = true;
            lexeme.append((char) lookahead);
            lookahead = input.read();
            // There must be at least one digit after the decimal point
            if (lookahead == -1 || !Character.isDigit((char) lookahead)) {
                error();
                state = 0;
                return;
            }
            while (lookahead != -1 && Character.isDigit((char) lookahead)) {
                lexeme.append((char) lookahead);
                lookahead = input.read();
            }
        }

        // Optional exponent part for scientific notation
        if (lookahead == 'e' || lookahead == 'E') {
            isFloat = true;
            lexeme.append((char) lookahead);
            lookahead = input.read();

            // Exponent may have an optional sign
            if (lookahead == '+' || lookahead == '-') {
                lexeme.append((char) lookahead);
                lookahead = input.read();
            }

            // There must be at least one digit in the exponent
            if (lookahead == -1 || !Character.isDigit((char) lookahead)) {
                error();
                state = 0;
                return;
            }
            while (lookahead != -1 && Character.isDigit((char) lookahead)) {
                lexeme.append((char) lookahead);
                lookahead = input.read();
            }
        }

        // Optional suffix: f, F, d, or D.
        if (lookahead != -1 && (lookahead == 'f' || lookahead == 'F'
                || lookahead == 'd' || lookahead == 'D')) {
            isFloat = true;  // Suffix implies a floating-point literal
            char suffix = (char) lookahead;
            lexeme.append(suffix);
            lookahead = input.read();

            if (suffix == 'f' || suffix == 'F') {
                writeToken(lexeme.toString(), "FLOAT_LITERAL");
            } else {
                writeToken(lexeme.toString(), "DOUBLE_LITERAL");
            }
            return;
        }

        // Without a suffix, decide based on whether we encountered a decimal point or exponent.
        if (isFloat) {
            writeToken(lexeme.toString(), "DOUBLE_LITERAL");
        } else {
            writeToken(lexeme.toString(), "INT_LITERAL");
        }
    }

    // Function to write the token to the output file
    private void writeToken(String lexeme, String tokenType) throws IOException {
        output.write(lexeme + "\t\t" + tokenType);
        output.newLine();
    }

    // Function to check if the identifier is a reserved word
    private boolean isReserved(String lexeme) {
        for (String word : RESERVED_WORDS) {
            if (word.equals(lexeme)) {
                return true;
            }
        }
        return false;
    }

    // Function to display an error message when encountering an unrecognized symbol
    private void error() throws IOException {
        System.err.println("UNRECOGNIZED_TOKEN");
        // Read the next character to skip the erroneous symbol
        lookahead = input.read();
    }

}
