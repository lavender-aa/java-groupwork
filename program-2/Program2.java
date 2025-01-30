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
    - validate files; reprompt until valid or until nothing (quit)
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

class Program2 {
        // variables used by class functions
        private static String in_name, out_name;
        private static boolean quit = false;
    
        public static void main(String[] args) {
    
            // open input file or quit
            get_valid_input(args);
    
            // open output file or quit
            if(!quit) get_valid_output(args);
    
            // parse input file or quit
            if(!quit) parse_input();
    
            // print data to output file or quit
            if(!quit) print_info();
            
            // print appropriate exit message
            if(quit) System.out.println("\nQuitting program.");
            else System.out.println("\nFinished processing; Output in " + out_name + ".");
        }

        // result: opens an input file that exists 
        //         OR sets the quit flag
        static void get_valid_input(String[] args) {

        }

        // result: opens a new output file (includes backing up if necessary)
        //         OR sets the quit flag
        static void get_valid_output(String[] args) {

        }

        // result: accumulates Words and the sum of the integers
        // OR sets the quit flag (due to possible errors)
        static void parse_input() {

        }

        // result: prints the accumulated information to the output file
        //         OR sets the quit flag (due to possible errors)
        static void print_info() {

        }
}

/* 

word class:
-----------

data:
    string word
    int number of occurrences

methods:
    constructor (string)
    get number of occurrences
    get word
    is equal to another word
    is equal to another word (ignore case)
    increase number of occurrences
    index of word (string) in list of words

*/
class Word {
    
}