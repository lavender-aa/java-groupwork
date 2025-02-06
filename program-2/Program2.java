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
            get_valid_input(data, args);
    
            // open output file or quit
            if(!data.get_quit()) get_valid_output(data, args);
    
            // parse input file or quit
            if(!data.get_quit()) parse_input(data);
    
            // print data to output file or quit
            if(!data.get_quit()) print_info(data);
            
            // print appropriate exit message
            if(data.get_quit()) System.out.println("\nQuitting program.");
            else System.out.println("\nFinished processing; Output in " + data.get_out_file().getName() + ".");
        }

        // result: opens an input file that exists 
        //         OR sets the quit flag
        static void get_valid_input(Data data, String[] args) { // asignee: lavender

            // create input file name (used to open input file or set quit)
            String in_name;

            // initialize reader to the keyboard
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // get first input from either command line or user
            if(args.length > 0) {
                in_name = args[0];
            }
            else {
                System.out.print("Enter input file name: ");
                in_name = get_file_name(data, reader);
            }

            if(!data.get_quit()) {
                
                // set input file
                data.set_in_file(new File(in_name));

                // keep getting input until input file exists or user quits
                while(!data.get_in_file().exists() && !data.get_quit()) {

                    // reprompt, get input again, update file
                    System.out.println("\nInput file " + in_name + " does not exist.");
                    System.out.print("Enter input file name: ");
                    in_name = get_file_name(data, reader);
                    data.set_in_file(new File(in_name));
                }
            }

            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Error closing BufferedReader.");
                data.set_quit(true);
            }
        }

        static String get_file_name(Data data, BufferedReader reader) {
            String result = "";

            // read a line from the console;
            // if it succeeds, check if it's nothing and set quit accordingly
            // if it fails, inform the user and set quit
            try {
                result = reader.readLine();
                if(result.equals("")) {
                    data.set_quit(true);
                }
            } catch (IOException e) {
                System.out.println("Error reading input file name.");
                data.set_quit(true);
            }

            return result;
        }

        // result: opens a new output file (includes backing up if necessary)
        //         OR sets the quit flag
        static void get_valid_output(Data data, String[] args) { // asignee: camron
            String out_name;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // get output file name from command line or prompt user
            if (args.length > 1) {
                out_name = args[1];
            } else {
                System.out.print("Enter output file name: ");
                out_name = get_file_name(data, reader);
            }

            if (!data.get_quit()) {
                // set output file
                data.set_out_file(new File(out_name));

                // check if output file already exists
                while (data.get_out_file().exists() && !data.get_quit()) {

                    System.out.println("\nOutput file " + out_name + " already exists.");
                    System.out.println("What would you like to do?");
                    System.out.println("1. Enter a new output file name");
                    System.out.println("2. Back up the existing output file first");
                    System.out.println("3. Overwrite the existing output file");
                    System.out.println("4. Quit the program");

                    try {

                        String choice = reader.readLine().trim();

                        switch (choice) {
                            case "1":
                                // prompt for new file name
                                System.out.print("Enter new output file name: ");
                                out_name = get_file_name(data, reader);
                                data.set_out_file(new File(out_name));
                                break;
                            case "2":
                                // backup the existing output file before proceeding
                                File backup = new File(out_name + ".bak");
                                if (data.get_out_file().renameTo(backup)) {
                                    System.out.println("Backup of existing file created as " + backup.getName());
                                    // proceed to use the original file as output
                                    System.out.print("Enter new output file name: ");
                                    out_name = get_file_name(data, reader);
                                    data.set_out_file(new File(out_name));
                                } else {
                                    System.out.println("Failed to create a backup. Proceeding with file overwrite.");
                                    // fall through to overwrite
                                }
                                break;
                            case "3":
                                // proceed to overwrite the file
                                System.out.println("Overwriting the existing output file.");
                                break;
                            case "4":
                                // set quit flag
                                data.set_quit(true);
                                break;
                            default:
                                System.out.println("Invalid option. Please choose again.");
                                break;
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading user input.");
                        data.set_quit(true);
                    }
                }
            }

            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Error closing BufferedReader.");
                data.set_quit(true);
            }
        }

        // result: accumulates Words and the sum of the integers
        // OR sets the quit flag (due to possible errors)
        static void parse_input(Data data) { // asignee: nicola 

        }

        // result: prints the accumulated information to the output file
        //         OR sets the quit flag (due to possible errors)
        static void print_info(Data data) { // asignee: lavender

            PrintWriter writer = null;

            // open a writer to the output file
            try {
                writer = new PrintWriter(data.get_out_file());
            } catch (FileNotFoundException e) {
                System.out.println("Error opening output file for writing.");
                data.set_quit(true);
            }

            if(!data.get_quit()) {

                // get the list of words and the writer
                Word[] list = data.get_list();

                // label
                writer.println("Words:\n-----");

                // for each word: print the word and its number of occurrences
                int i;
                for(i = 0; list[i] != null; i++) {
                    Word word = list[i];
                    // to be uncommented after Word class gets written
                    // writer.println("\"" + word.get_word() + "\": " + word.get_num_occur() + " occurrences");
                    writer.println("printing word data...");
                }

                // label
                writer.println("\nData Information:\n-----------------");

                // print number of unique words, sum of all integers
                writer.println("Number of unique words: " + i);
                writer.println("Sum of integers: " + data.get_sum());

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
    private File in_file;
    private File out_file;
    private Word[] list;
    private boolean quit;
    private int sum;

    public Data() {
        in_file = null;
        out_file = null;
        list = new Word[100];
        quit = false;
    }

    // getters and setters for each piece of data

    public File get_in_file() {
        return in_file;
    }
    
    public void set_in_file(File input) {
        in_file = input;
    }

    public File get_out_file() {
        return out_file;
    }
    
    public void set_out_file(File input) {
        out_file = input;
    }

    public Word[] get_list() {
        return list;
    }

    // (no setter for list since only its elements will be modified)

    public boolean get_quit() {
        return quit;
    }
    
    public void set_quit(boolean input) {
        quit = input;
    }

    public int get_sum() {
        return sum;
    }

    public void set_sum(int num) {
        sum = num;
    }
}