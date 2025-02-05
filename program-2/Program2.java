/*

main function:
--------------

input file:
    - contains integers (r"(-?\d+)") and words (strings; r"([A-Za-z]+['\-\d]*)")
output file:
    - contains each unique word and number of occurrences
    - ordered by first encountered
    - end of list: total number of unique words, sum of integers


notes:
    - can enter input or input and output files in command line
    - validate files; reprompt until valid or until no input
    - use StringTokenizer to parse input file
    - for each word, either add to count (if exists) or create new Word, add to list


more detailed steps:
    - check inputs from command line
    - validate files
        - if any invalid, reprompt
        - if any empty prompts, quit program
    - open PrintWriter and FileReader
    - create StringTokenizer
    - while a line exists:
        - while tokens in line:
            - determine if token is word or int
                - word: check word list, create word/add occurrence
                - int: add to sum
    - print each unique word to output, include number of occurrences
    - print total number of unique words, sum of integers

*/

import java.io.*;

public class Program2 {
        // variables used by class functions
    
        public static void main(String[] args) {

            // instantiate data object
            Data data = new Data();
    
            // open input file or quit
            getValidInput(data, args);
    
            // open output file or quit
            if(!data.getQuit()) getValidOutput(data, args);
    
            // parse input file or quit
            if(!data.getQuit()) parseInput(data);
    
            // print data to output file or quit
            if(!data.getQuit()) printInfo(data);
            
            // print appropriate exit message
            if(data.getQuit()) System.out.println("\nQuitting program.");
            else System.out.println("\nFinished processing; Output in " + data.getOutputFile().getName() + ".");
        }

        // result: opens an input file that exists 
        //         OR sets the quit flag
        static void getValidInput(Data data, String[] args) { // asignee: lavender

            // create input file name (used to open input file or set quit)
            String inName;

            // initialize reader to the keyboard
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // get first input from either command line or user
            if(args.length > 0) {
                inName = args[0];
            }
            else {
                System.out.print("Enter input file name: ");
                inName = getFileName(data, reader);
            }

            if(!data.getQuit()) {
                
                // set input file
                data.setInputFile(new File(inName));

                // keep getting input until input file exists or user quits
                while(!data.getInputFile().exists() && !data.getQuit()) {

                    // reprompt, get input again, update file
                    System.out.println("\nInput file " + inName + " does not exist.");
                    System.out.print("Enter input file name: ");
                    inName = getFileName(data, reader);
                    data.setInputFile(new File(inName));
                }
            }

            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Error closing BufferedReader.");
                data.setQuit(true);
            }
        }

        static String getFileName(Data data, BufferedReader reader) {
            String result = "";

            // read a line from the console;
            // if it succeeds, check if it's nothing and set quit accordingly
            // if it fails, inform the user and set quit
            try {
                result = reader.readLine();
                if(result.equals("")) {
                    data.setQuit(true);
                }
            } catch (IOException e) {
                System.out.println("Error reading input file name.");
                data.setQuit(true);
            }

            return result;
        }

        // result: opens a new output file (includes backing up if necessary)
        //         OR sets the quit flag
        static void getValidOutput(Data data, String[] args) { // asignee: camron

        }

        // result: accumulates Words and the sum of the integers
        // OR sets the quit flag (due to possible errors)
        static void parseInput(Data data) { // asignee: nicola 

        }

        // result: prints the accumulated information to the output file
        //         OR sets the quit flag (due to possible errors)
        static void printInfo(Data data) { // asignee: lavender

            PrintWriter writer = null;

            // open a writer to the output file
            try {
                writer = new PrintWriter(data.getOutputFile());
            } catch (FileNotFoundException e) {
                System.out.println("Error opening output file for writing.");
                data.setQuit(true);
            }

            if(!data.getQuit()) {

                // get the list of words and the writer
                Word[] list = data.getList();

                // label
                writer.println("Words:\n-----");

                // for each word: print the word and its number of occurrences
                int i;
                for(i = 0; list[i] != null; i++) {
                    Word word = list[i];
                    // TODO: uncomment, possibly correct Word method calls
                    // writer.println("\"" + word.getWord() + "\": " + word.getNumOccur() + " occurrences");
                }

                // label
                writer.println("\nData Information:\n-----------------");

                // print number of unique words, sum of all integers
                writer.println("Number of unique words: " + i);
                writer.println("Sum of integers: " + data.getSum());

                // close the printwriter
                writer.close();
            }
        }
}

/* 

word class:
-----------

data:
    string word DONE
    int number of occurrences DONE

methods:
    constructor (string) STARTED
    get number of occurrences DONE
    get word DONE
    is equal to another word
    is equal to another word (ignore case)
    increase number of occurrences DONE
    index of word (string) in list of words DONE

*/
class Word 
{ // assignee: nicola
    private String myWord;
    private int wordCount;
    private int wordIndex;

    // basic constructor validates if the word already exist
    // by calling CHANGE THIS NAME NICOLA method
    public Word(String newWord)
    {
        boolean isNewWord = isAnotherWord(newWord);
            if(isNewWord)
            {
                myWord = newWord;
            }
    }

    // accessor for wordCount
    public int getWordCount()
    {
        return wordCount;
    }

    // accessor for word
    public String getWord()
    {
        return word;
    }

    // increments word counter when word already exists
    private void incWordCount()
    {
        wordCount++;
    }

    // sets index variable to the index in the external word array
    private void setIndex(int newIndex)
    {
        wordIndex = newIndex;
    }

    public int getIndex()
    {
        return wordIndex;
    }

    
}

// data class: contains all program data 
// that needs to be modified/read across methods
class Data {
    private File inFile;
    private File outFile;
    private Word[] list;
    private boolean quit;
    private int sum;

    public Data() {
        inFile = null;
        outFile = null;
        list = new Word[100];
        quit = false;
    }

    // getters and setters for each piece of data

    public File getInputFile() {
        return inFile;
    }
    
    public void setInputFile(File input) {
        inFile = input;
    }

    public File getOutputFile() {
        return outFile;
    }
    
    public void setOutputFile(File input) {
        outFile = input;
    }

    public Word[] getList() {
        return list;
    }

    // (no setter for list since only its elements will be modified)

    public boolean getQuit() {
        return quit;
    }
    
    public void setQuit(boolean input) {
        quit = input;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int num) {
        sum = num;
    }
}