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
import java.util.*;

class Program2 {
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

        }

        // result: opens a new output file (includes backing up if necessary)
        //         OR sets the quit flag
        static void get_valid_output(Data data, String[] args) { // asignee: camron

        }

        // result: accumulates Words and the sum of the integers
        // OR sets the quit flag (due to possible errors)
        static void parse_input(Data data) 
        { // asignee: nicola 
            File inFile = data.get_in_file();
            Word[] wordList = data.get_list();
        }

        // method checks if a string passed to it is a valid number
        static boolean isValidNumber(String maybeNumber)
        {
            boolean isValid = false;
            boolean wasNegative = false
            
            if (maybeNumber.charAt(0) == '-') 
            {
                while(maybeNumber.charAt(0) == '-')   
                {
                    maybeNumber = maybeNumber.substring(1);
                    wasNegative = true;
                } 
            }

            if (wasNegative)
                maybeNumber = "-" + maybeNumber;

            if (maybeNumber.charAt(0) == '-' && Character.isDigit(maybeNumber.charAt(1))) 
            {
                isValid = true;
            }
            else if (Character.isDigit(maybeNumber.charAt(0)))
            {
                isValid = true;
            }

            return isValid;
        }

        // loops through passed Word array and checks if Word passed is in it
        // if not adds the string to the next open spot in the array
        static void findOrAdd(Word[] wList, Word rightWord, Data data)
        {
            boolean isNewWord = true;
            int count = 0;

            // it's a little weird to use a while loop for this, but
            // this should make it quit early if it finds a match without 
            // using break
            while(isNewWord)
            {
                if(count > data.getLastIndex())
                {
                    wList[count] = rightWord;
					isNewWord = false;
                }
                else
                {
                    Word thisWord = wList[count];
                    isNewWord = !thisWord.isEqual(rightWord);
					count++;
                }
            }
        }

        // result: prints the accumulated information to the output file
        //         OR sets the quit flag (due to possible errors)
        static void print_info(Data data) { // asignee: lavender

        }
}

/* 

word class:
-----------

data:
    string word DONE
    int number of occurrences DONE

methods:
    constructor (string) DONE
    get number of occurrences DONE
    get word DONE
    is equal to another word DONE
    is equal to another word (ignore case) Does this need to be a separate method? -Nicola
    increase number of occurrences DONE
    index of word (string) in list of words DONE

*/
class Word 
{ // assignee: nicola
    private String word;
    private int wordCount;
    private int wordIndex;
    private boolean isUnique;

    // basic constructor sets word to passed value, and 
    // intializes other variables to starting values
    public Word(String word)
    {
        wordCount = 1;
        wordIndex = 0;
        isUnique = true;
        this.word = word;
        
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
        if (isUnique) 
        {
            wordIndex = newIndex;            
        }
    }

    // returns the index this word is stored at
    public int getIndex()
    {
        return wordIndex;
    }

    // set isUnique to false, since it's deafult is true and it
    // will never become un-unique we only need to make it false
    public void setIsUnique()
    {
        isUnique = false;
    }

    // get isUnique
    public boolean getIsUnique()
    {
        return isUnique;
    }

    // returns if this word is equal to the one passed it
    // and change isUnique in the passed Word object to false
    // if it isn't unique and increment the counter
    public boolean isEqual(Word rightSide)
    {
        boolean isRightSame = false;
        String rightString = rightSide.getWord().toUpperCase();
        if (this.word.toUpperCase().equals(rightString)) 
        {
            incWordCount();
            isRightSame = true;
            rightSide.setIsUnique();
        }

        return isRightSame;
    }
}

// data class: contains all program data 
// that needs to be modified/read across methods
class Data {
    private File in_file;
    private File out_file;
    private Word[] list;
    private boolean quit;
    private int lastIndex;
    private int total;

    public Data() {
        in_file = null;
        out_file = null;
        list = new Word[100];
        for (int i = 0; i < 100; i++) 
            list[i] = null;
        quit = false;
        lastIndex = -1;
        total = 0;
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

    // no setter for list since only it's elements will be modified

    public boolean get_quit() {
        return quit;
    }
    
    public void set_quit(boolean input) {
        quit = input;
    }

    public int getLastIndex()
    {
        return lastIndex;
    }

    public int getTotal()
    {
        return total;
    }

    // so long as the program is running correctly there should
    // be no reason to alter the index pointer in any other way 
    // than adding one
    public void incLastIndex()
    {
        lastIndex++;
    }

    // total will never need to be reset completely 
    // so we only need to add values to it
    public void addToTotal(int addedValue)
    {
        total += addedValue;
    }

}