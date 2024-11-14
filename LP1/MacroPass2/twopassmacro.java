import java.util.*;
import java.io.*;

class twopassmacro {
    static List<String[]> mnt = new ArrayList<>(); // MNT with dynamic size
    static List<String[]> ala = new ArrayList<>(); // ALA with dynamic size
    static List<String> mdt = new ArrayList<>();   // MDT with dynamic size
    static int mntc = 0, mdtc = 0;

    public static void main(String args[]) {
        pass1();
        System.out.println("Macro Name Table (MNT)");
        displayArrayList(mnt); // Use modified method names
        System.out.println("Argument List Array (ALA) for Pass1");
        displayArrayList(ala);
        System.out.println("Macro Definition Table (MDT)");
        displayStringList(mdt);
        pass2();
        System.out.println("Argument List Array (ALA) for Pass2");
        displayArrayList(ala);
    }

    static void pass1() {
        String line, previousLine = "", argument;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("input1.txt"));
            BufferedWriter output = new BufferedWriter(new FileWriter("pass1_output1.txt"));

            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase("MACRO")) {
                    previousLine = line;
                    // Reading macro definition until "MEND"
                    while (!(line = reader.readLine()).equalsIgnoreCase("MEND")) {
                        if (previousLine.equalsIgnoreCase("MACRO")) {
                            // Tokenizing the macro name and arguments
                            StringTokenizer tokenizer = new StringTokenizer(line);
                            String[] macroDefinition = new String[tokenizer.countTokens()];
                            for (int i = 0; i < macroDefinition.length; i++) {
                                macroDefinition[i] = tokenizer.nextToken();
                            }
                            mnt.add(new String[]{String.valueOf(mntc + 1), macroDefinition[0], String.valueOf(mdtc + 1)});
                            mntc++;

                            // Parsing arguments
                            StringTokenizer argTokenizer = new StringTokenizer(macroDefinition[1], ",");
                            while (argTokenizer.hasMoreTokens()) {
                                argument = argTokenizer.nextToken();
                                int index = argument.indexOf("=");
                                if (index != -1) {
                                    ala.add(new String[]{String.valueOf(ala.size()), argument.substring(0, index)});
                                } else {
                                    ala.add(new String[]{String.valueOf(ala.size()), argument});
                                }
                            }
                        } else {
                            // MDT Formation: Replace arguments in the macro body with positional parameters
                            int index = line.indexOf("&");
                            if (index != -1) {
                                String substring = line.substring(index);
                                for (String[] alaEntry : ala) {
                                    if (alaEntry[1].equals(substring)) {
                                        line = line.replace(substring, "#" + alaEntry[0]);
                                    }
                                }
                            }
                        }
                        mdt.add(line);
                        mdtc++;
                        previousLine = line;
                    }
                    mdt.add(line); // Adding MEND to MDT
                    mdtc++;
                } else {
                    // Writing non-macro code to the pass1 output
                    output.write(line);
                    output.newLine();
                }
            }
            output.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Input file not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void pass2() {
    try {
        BufferedReader reader = new BufferedReader(new FileReader("pass1_output1.txt"));
        BufferedWriter output = new BufferedWriter(new FileWriter("pass2_output.txt"));
        String line;

        while ((line = reader.readLine()) != null) {
            boolean isMacroExpanded = false;
            StringTokenizer tokenizer = new StringTokenizer(line);
            String[] tokens = new String[tokenizer.countTokens()];

            // Tokenizing the current line
            for (int i = 0; i < tokens.length; i++) {
                tokens[i] = tokenizer.nextToken();
            }

            // Check if the line is a macro invocation by comparing with MNT entries
            for (String[] mntEntry : mnt) {
                if (tokens.length > 0 && tokens[0].equals(mntEntry[1])) {
                    int mdtPointer = Integer.parseInt(mntEntry[2]);
                    if (tokens.length > 1) {
                        // Split arguments and update ALA
                        StringTokenizer argTokenizer = new StringTokenizer(tokens[1], ",");
                        List<String> arguments = new ArrayList<>();
                        while (argTokenizer.hasMoreTokens()) {
                            arguments.add(argTokenizer.nextToken());
                        }

                        // Check if arguments exist to prevent ArrayIndexOutOfBoundsException
                        for (int i = 0; i < arguments.size(); i++) {
                            if (i < ala.size()) {
                                ala.get(i)[1] = arguments.get(i);
                            } else {
                                System.out.println("Error: ALA index out of bounds while updating arguments.");
                            }
                        }
                    } else {
                        System.out.println("Error: No arguments provided for macro invocation " + tokens[0]);
                    }

                    // Expand the macro by reading from MDT
                    for (int i = mdtPointer - 1; !mdt.get(i).equalsIgnoreCase("MEND"); i++) {
                        String mdtLine = mdt.get(i);
                        int index = mdtLine.indexOf("#");
                        if (index != -1) {
                            int argIndex = Integer.parseInt("" + mdtLine.charAt(index + 1));
                            // Check bounds before accessing ALA
                            if (argIndex < ala.size()) {
                                mdtLine = mdtLine.replace("#" + argIndex, ala.get(argIndex)[1]);
                            } else {
                                System.out.println("Error: ALA index out of bounds during macro expansion.");
                            }
                        }
                        output.write(mdtLine);
                        output.newLine();
                    }
                    isMacroExpanded = true;
                    break;
                }
            }

            // If not a macro invocation, write the line directly to output
            if (!isMacroExpanded) {
                output.write(line);
                output.newLine();
            }
        }
        output.close();
    } catch (FileNotFoundException ex) {
        System.out.println("Pass1 output file not found.");
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    // Display method for List<String[]>
    static void displayArrayList(List<String[]> list) {
        for (String[] row : list) {
            System.out.println(String.join(" ", row));
        }
    }

    // Display method for List<String>
    static void displayStringList(List<String> list) {
        for (String row : list) {
            System.out.println(row);
        }
    }
}
