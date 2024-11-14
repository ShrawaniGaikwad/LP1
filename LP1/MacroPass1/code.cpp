#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <tuple>
#include <algorithm>

using namespace std;

class MacroProcessor {
private:
    vector<tuple<string, int, int, int, int>> MNTAB; // Macro Name Table
    vector<tuple<string, string>> KDTAB;             // Keyword Definition Table
    vector<vector<string>> MDT;                      // Macro Definition Table
    vector<vector<string>> PNTAB;                    // Parameter Name Table
    vector<vector<string>> myToken;                  // Tokenized input lines

    void displayToken();
    void displayAPTAB(const string& macroName, const vector<string>& APTAB);
    int deal(const string& token, int index);
    int findInMNT(const string& name);
    string getValueFromKDTAB(const string& key, int KDTP);
    int getKeywordIndex(const string& keyword, int macroIndex);
    int storeDefinition(int lineNum);

public:
    MacroProcessor();
    void displayAll();
    void pass1();
};

MacroProcessor::MacroProcessor() {
    ifstream ipFile("input1.txt");
    if (!ipFile) {
        cerr << "Error opening file" << endl;
        exit(1);
    }

    string buffer;
    while (getline(ipFile, buffer)) {
        stringstream ss(buffer);
        vector<string> token;
        string word;
        while (ss >> word) {
            if (word != ",")
                token.push_back(word);
        }
        myToken.push_back(token);
    }
}

void MacroProcessor::displayToken() {
    for (int i = 0; i < myToken.size(); i++) {
        cout << i << " :";
        for (const string& word : myToken[i]) {
            cout << word << "\t";
        }
        cout << endl;
    }
}

void MacroProcessor::displayAll() {
    cout << "\n---------MNT-----------\n";
    cout << "Name\t#PP\t#KP\t#MDTP\t#KDTP\n";
    
    ofstream mntFile("MNT.txt");
    
    for (const auto& entry : MNTAB) {
        cout << get<0>(entry) << "\t" << get<1>(entry) << "\t" 
             << get<2>(entry) << "\t" << get<3>(entry) << "\t" 
             << get<4>(entry) << "\n";
             
        mntFile << get<0>(entry) << " " 
                 << get<1>(entry) << " " 
                 << get<2>(entry) << " "
                 << get<3>(entry) << " "
                 << get<4>(entry) << "\n";
    }
    
    mntFile.close();
    
    cout << "-------------------------\n";

    cout << "\n---------MDT-----------\n";
    
    ofstream mdtFile("MDT.txt");
    
    for (size_t i = 0; i < MDT.size(); i++) {
        cout << "Line " << i + 1 << ": ";
        
        for (const auto& word : MDT[i]) {
            cout << word << " ";
            mdtFile << word << " ";
        }
        
        cout << endl;
        mdtFile << endl;
    }
    
    mdtFile.close();
    
    cout << "-------------------------\n";

    cout << "\n--------KDTAB----------\n";
    
    ofstream kdtFile("KDT.txt");
    
    for (const auto& entry : KDTAB) {
        cout << get<0>(entry) << "\t" << get<1>(entry) << endl;
        kdtFile << get<0>(entry) << " " 
                << get<1>(entry) << endl;
    }
    
    kdtFile.close();
    
    cout << "-------------------------\n";

    cout << "\n--------PNTAB----------\n";
    
    ofstream pntFile("PNT.txt");
    
    for (const auto& line : PNTAB) {
        for (const auto& para : line) {
            cout << para << "\t";
            pntFile << para << "\t";
        }
        cout << endl;
        pntFile << endl;
    }
    
    pntFile.close();
    
    cout<< "-----------------------\n";
}

int MacroProcessor::deal(const string& token, int index) {
   if (index < 0 || index >= PNTAB.size()) return -1; 

   const vector<string>& parameters = PNTAB[index];
   auto it = find(parameters.begin(), parameters.end(), token);
   if (it != parameters.end())
       return distance(parameters.begin(), it) + 1;
   return -1;
}

// int MacroProcessor::findInMNT(const string& name) {
//    for (size_t i = 0; i < MNTAB.size(); i++) {
//        if (get<0>(MNTAB[i]) == name)
//            return static_cast<int>(i);
//    }
//    return -1;
// }

// string MacroProcessor::getValueFromKDTAB(const string& key, int KDTP) {
//    if (KDTP < 0 || KDTP >= static_cast<int>(KDTAB.size())) return ""; // Prevent out-of-bounds access

//    for (int i = KDTP; i < static_cast<int>(KDTAB.size()); i++) {
//        if (get<0>(KDTAB[i]) == key)
//            return get<1>(KDTAB[i]);
//    }
//    return "";
// }

// int MacroProcessor::getKeywordIndex(const string& keyword, int macroIndex) {
//    if (macroIndex < 0 || macroIndex >= static_cast<int>(PNTAB.size())) return -1; // Prevent out-of-bounds access

//    const auto& currParameters = PNTAB[macroIndex];
//    auto it = find(currParameters.begin(), currParameters.end(), keyword);
//    if (it != currParameters.end())
//        return distance(currParameters.begin(), it);
//    return -1;
// }

int MacroProcessor::storeDefinition(int lineNum)
{
   if (lineNum < 0 || lineNum >= myToken.size()) return lineNum; // Prevent out-of-bounds access

   string name;
   vector<string> tempPNTAB;
   int PP = 0, KP = 0, KDTP, MDTP, j;
   
   KDTP = KDTAB.size()+1;
   MDTP = MDT.size()+1;
   name = myToken[lineNum][0];

   for (int i = 1; i < myToken[lineNum].size(); i++)
   {
       string currToken = myToken[lineNum][i];
       if (currToken.find('=') != string::npos)
       {
           KP++;
           int index = currToken.find('=');
           string value = currToken.substr(index + 1);
           string keyword = currToken.substr(0, index);
           KDTAB.emplace_back(keyword, value);
           tempPNTAB.push_back(keyword);
       }
       else
       {
           tempPNTAB.push_back(currToken);
       }
   }
   
   PP = tempPNTAB.size() - KP;
   PNTAB.push_back(tempPNTAB);

   for (j = lineNum + 1; j < myToken.size() && myToken[j][0] != "MEND"; j++)
   {
       vector<string> V1;
       for (string token : myToken[j])
       {
           int index = deal(token, PNTAB.size()- 1);
           if (index != -1)
           {
               V1.push_back(" (P," + to_string(index) + ")");
           }
           else
           {
               V1.push_back(token);
           }
       }
       MDT.push_back(V1);
   }
   
   MDT.push_back({"MEND"});
   MNTAB.emplace_back(name, PP, KP, MDTP, KDTP);
   
   return j;
}

void MacroProcessor::pass1()
{
   for (int i = 0; i < myToken.size(); i++)
   {
       vector<string> curr = myToken[i];
       if (find(curr.begin(), curr.end(), "MACRO") != curr.end())
       {
           i = storeDefinition(i + 1);
       }
   }
}

int main()
{
   MacroProcessor mp;
   mp.pass1();
   mp.displayAll(); // Display all tables after processing
   return 0;
}