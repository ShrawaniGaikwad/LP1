#include <iostream>
#include <map>
#include <vector>
#include <sstream>
#include <fstream>
#include <string>

using namespace std;

// Structure to hold the macro definitions
struct MacroDefinition {
    int pp; // Number of positional parameters
    int kpd; // Number of keyword parameters
    vector<string> mdt; // Macro Definition Table
};

// Function to read MNT from a file
void readMNT(ifstream& file, map<string, MacroDefinition>& mnt) {
    int index;
    string name;
    while (file >> index >> name) {
        MacroDefinition macroDef;
        file >> macroDef.pp >> macroDef.kpd;
        macroDef.mdt.clear();
        string line;
        while (true) {
            getline(file, line);
            if (line == "MEND") {
                macroDef.mdt.push_back(line);
                break;
            }
            macroDef.mdt.push_back(line);
        }
        mnt[name] = macroDef;
    }
}

// Function to read MDT from a file
void readMDT(ifstream& file, map<int, string>& mdt) {
    int index = 1;
    string line;
    while (getline(file, line)) {
        mdt[index++] = line;
    }
}

// Function to read KPDTAB from a file
void readKPDTAB(ifstream& file, map<int, string>& kpdTab) {
    int index;
    string name;
    while (file >> index >> name) {
        kpdTab[index] = name;
    }
}

// Function to read macro calls from a file
void readMacroCalls(ifstream& file, vector<pair<string, vector<string>>>& macroCalls) {
    string line;
    while (getline(file, line)) {
        stringstream ss(line);
        string macroName;
        ss >> macroName;
        string param;
        vector<string> parameters;
        while (ss >> param) {
            parameters.push_back(param);
        }
        macroCalls.push_back({macroName, parameters});
    }
}

// Function to expand macros
void expandMacro(const string& macroName, const vector<string>& parameters, const map<string, MacroDefinition>& mnt, const map<int, string>& mdt) {
    if (mnt.find(macroName) == mnt.end()) {
        cout << "Macro " << macroName << " not found!" << endl;
        return;
    }

    const MacroDefinition& macroDef = mnt.at(macroName);

    // Display expanded code
    cout << "Expanded Code for " << macroName << ":\n";
    for (const auto& line : macroDef.mdt) {
        string expandedLine = line;

        // Replace parameters in the macro definition with actual values
        for (size_t i = 0; i < parameters.size(); ++i) {
            string placeholder = "P" + to_string(i + 1); // Assuming parameters are named P1, P2, ...
            size_t pos = expandedLine.find(placeholder);
            while (pos != string::npos) {
                expandedLine.replace(pos, placeholder.length(), parameters[i]);
                pos = expandedLine.find(placeholder, pos + parameters[i].length());
            }
        }

        cout << expandedLine << endl;
    }
}

int main() {
    // Define MNT, MDT, and KPDTAB
    map<string, MacroDefinition> mnt;
    map<int, string> mdt;
    map<int, string> kpdTab;

    // Read MNT
    ifstream mntFile("MNT.txt");
    if (!mntFile.is_open()) {
        cerr << "Error opening MNT file!" << endl;
        return 1;
    }
    readMNT(mntFile, mnt);
    mntFile.close();

    // Read MDT
    ifstream mdtFile("MDT.txt ");
    if (!mdtFile.is_open()) {
        cerr << "Error opening MDT file!" << endl;
        return 1;
    }
    readMDT(mdtFile, mdt);
    mdtFile.close();

    // Read KPDTAB
    ifstream kpdTabFile("KPDTAB.txt");
    if (!kpdTabFile.is_open()) {
        cerr << "Error opening KPDTAB file!" << endl;
        return 1;
    }
    readKPDTAB(kpdTabFile, kpdTab);
    kpdTabFile.close();

    // Read macro calls
    ifstream macroCallsFile("macrocall.txt");
    if (!macroCallsFile.is_open()) {
        cerr << "Error opening macro call file!" << endl;
        return 1;
    }
    vector<pair<string, vector<string>>> macroCalls;
    readMacroCalls(macroCallsFile, macroCalls);
    macroCallsFile.close();

    // Process each macro call
    for (const auto& call : macroCalls) {
        expandMacro(call.first, call.second, mnt, mdt);
        cout << endl; // Separate each macro call output
    }

    return 0;
}