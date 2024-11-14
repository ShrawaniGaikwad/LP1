import java.io.*;
import java.util.*;

public class TwoPassMacro {
    private static List<String> mnt = new ArrayList<>();
    private static List<String> mdt = new ArrayList<>();
    private static Map<String, String> defaultValues = new LinkedHashMap<>();
    private static List<String[]> pntTable = new ArrayList<>(); 
    private static List<String> expandedCode = new ArrayList<>();
    private static List<Map<String, String>> defaultValuesList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        readFiles();
        processMacroCalls();
        printExpandedCode();
    }

    private static void readFiles() throws IOException {
    // Read MNT file
    try (BufferedReader mntReader = new BufferedReader(new FileReader("MNT2.txt"))) {
        String line;
        while ((line = mntReader.readLine()) != null) {
            mnt.add(line.trim());
        }
    }

    // Read MDT file
    try (BufferedReader mdtReader = new BufferedReader(new FileReader("MDT2.txt"))) {
        String line;
        while ((line = mdtReader.readLine()) != null) {
            mdt.add(line.trim());
        }
    }

    // Read PNT file to determine keyword parameter count for each macro
    try (BufferedReader pntReader = new BufferedReader(new FileReader("PNT2.txt"))) {
        String line;
        while ((line = pntReader.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            pntTable.add(parts);
        }
    }

    // Read KDT file and construct default values based on PNT entries
   try (BufferedReader kptabReader = new BufferedReader(new FileReader("KDT2.txt"))) {
        String line;
        List<String> kdtEntries = new ArrayList<>(); // Temporary storage for KDT entries

        while ((line = kptabReader.readLine()) != null) {
            kdtEntries.add(line.trim()); // Store each line from KDT
        }

        // Process each macro in MNT to construct default values
        for (String mntEntry : mnt) {
            String[] mntParts = mntEntry.split("\\s+");
            int numKeywordParams = Integer.parseInt(mntParts[2]); // Number of keyword params
            int kpdtpStart = Integer.parseInt(mntParts[4]) - 1;   // KPDTAB start position (0-based index)

            if (numKeywordParams == 0) {
                // Add an empty map for macros without keyword parameters
                defaultValuesList.add(new LinkedHashMap<>());
            } else {
                // Build a map of default values from KDT entries
                Map<String, String> defaultValues = new LinkedHashMap<>();
                for (int i = kpdtpStart; i < kpdtpStart + numKeywordParams; i++) {
                    String[] kdtParts = kdtEntries.get(i).split("\\s+");
                    String key = kdtParts[0];
                    String value = (kdtParts.length > 1) ? kdtParts[1] : ""; // Default value or empty
                    defaultValues.put(key, value);
                }
                defaultValuesList.add(defaultValues);
            }
        }
    }

    // Display the constructed default values list for verification
    // System.out.println("Constructed defaultValuesList:");
    // for (int i = 0; i < defaultValuesList.size(); i++) {
    //     System.out.println("Macro " + (i + 1) + " Defaults: " + defaultValuesList.get(i));
    // }
}

 private static void processMacroCalls() throws IOException {
    // Read macro calls dynamically from macrocall.txt
    try (BufferedReader macroCallReader = new BufferedReader(new FileReader("macrocall2.txt"))) {
        String macroCall;
        while ((macroCall = macroCallReader.readLine()) != null) {
            String[] parts = macroCall.split(" ");
            String macroName = parts[0];

            int mntIndex = findMNTIndex(macroName);
            if (mntIndex == -1) {
                System.out.println("Macro not found: " + macroName);
                continue;
            }

            String[] mntParts = mnt.get(mntIndex).split("\\s+");
            int numPositionalParams = Integer.parseInt(mntParts[1]);
            int numKeywordParams = Integer.parseInt(mntParts[2]);
            int kpdtpStart = Integer.parseInt(mntParts[4]);

            List<String> positionalParams = new ArrayList<>();
            Map<String, String> keywordParams = new LinkedHashMap<>();

            // Collect positional parameters
            for (int i = 1; i <= numPositionalParams; i++) {
                positionalParams.add(parts[i]);
            }

            // Collect keyword parameters
            for (int i = numPositionalParams + 1; i < parts.length; i++) {
                String[] kv = parts[i].split("=");
                keywordParams.put(kv[0], kv[1]);
            }

           System.out.println("Keyword Parameters:");
            for (Map.Entry<String, String> entry : keywordParams.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
            Map<String, String> defaultValues = defaultValuesList.get(mntIndex);

            // Call the modified printAPTAB with additional arguments, including the defaultValues for the macro
            printAPTAB(positionalParams, keywordParams, defaultValuesList.get(mntIndex), numKeywordParams, kpdtpStart - 1);

            // Expand macro using the respective PNT line
            expandMacro(macroName, positionalParams, keywordParams, pntTable.get(mntIndex), defaultValues);

        }
    }
}
    private static int findMNTIndex(String macroName) {
        for (int i = 0; i < mnt.size(); i++) {
            if (mnt.get(i).startsWith(macroName)) {
                return i;
            }
        }
        return -1; // Not found
    }

    private static void expandMacro(String macroName, List<String> positionalParams, Map<String, String> keywordParams, String[] pntArray, Map<String, String> defaultValues) {
    int mntIndex = findMNTIndex(macroName);
    if (mntIndex == -1) return;

    String[] mntParts = mnt.get(mntIndex).split("\\s+");
    int mdTPIndex = Integer.parseInt(mntParts[3])-1; // MDTP index

    for (int i = mdTPIndex; i < mdt.size(); i++) {
        String mdtLine = mdt.get(i);
        if (mdtLine.contains("MEND")) break;

        // Replace positional parameters first
        for (int j = 0; j < positionalParams.size(); j++) {
            mdtLine = mdtLine.replaceAll("\\(P," + (j + 1) + "\\)", positionalParams.get(j));
        }

        // Replace keyword parameters based on PNT
        for (int j = 0; j < pntArray.length; j++) {
            String keyword = pntArray[j];
            // Check in keywordParams first, then fallback to defaultValues if not present
            String replacementValue = keywordParams.getOrDefault(keyword, defaultValues.get(keyword));

            if (replacementValue != null) {
                mdtLine = mdtLine.replaceAll("\\(P," + (j + 1) + "\\)", replacementValue);
            }
        }

        // Add to expanded code with prefix
        expandedCode.add("+ " + mdtLine);
    }
}


    private static void printAPTAB(List<String> positionalParams, Map<String, String> keywordParams, Map<String, String> defaultValues, int numKeywordParams, int kpdtpStart) {
    System.out.println("APTAB:");
    int index = 1;

    // Step 1: Print positional parameters first
    if (!positionalParams.isEmpty()) {
        for (String param : positionalParams) {
            System.out.println(index++ + " " + param);
        }
    }

    // Step 2: Initialize APTAB with default values from KDT in the correct order
    List<String> aptabList = new ArrayList<>(Collections.nCopies(numKeywordParams, null)); // Placeholder for defaults
    List<String> defaultKeys = new ArrayList<>(defaultValues.keySet()); // Keys in the order from defaultValues

    // Debug: Print default keys
    // System.out.println("Default keys:");
    // for (String key : defaultKeys) {
    //     System.out.println(key);
    // }

    // Populate APTAB with default values from defaultValues map
    for (int i = kpdtpStart; i < numKeywordParams + kpdtpStart; i++) {
        String key = defaultKeys.get(i - kpdtpStart); // Get the key based on index
        String defaultValue = defaultValues.get(key); // Fetch the default value

        if (defaultValue != null) {
            aptabList.set(i - kpdtpStart, defaultValue); // Set default value in APTAB
        }
    }

    // Step 3: Replace default values with those provided in the macro call
     for (String key : keywordParams.keySet()) {
        if (defaultValues.containsKey(key)) {
            int position = defaultKeys.indexOf(key); // Find index in default keys
            if (position >= 0 && position < numKeywordParams) {
                aptabList.set(position, keywordParams.get(key)); // Replace with provided value
            }
        } else {
            aptabList.add(keywordParams.get(key)); // Add any additional parameters
        }
    }

    // Step 4: Print the complete APTAB
    for (String value : aptabList) {
        System.out.println(index++ + " " + (value != null ? value : ""));
    }

    System.out.println();
}


    private static void printExpandedCode() {
        System.out.println("Expanded Code:");
        
        for (String codeLine : expandedCode) {
            System.out.println(codeLine);
        }
    }
}
