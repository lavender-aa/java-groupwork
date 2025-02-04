import java.io.*;
//import java.lang.*;


// this is just to test that the functionality of the Word class and methods that use it are working as intended

public class TestWord
{
	public static void main(String[] args)
	{
		Word word1 = new Word("xyz");
		Word word2 = new Word("abc");
		Word word3 = new Word("hij");
		Word word4 = new Word("kl");
		Word word5 = new Word("tuv");
		Word word6 = new Word("w");
		Word word7 = new Word("xyz");
		Word word8 = new Word("lmn");
		Word word9 = new Word("qrs");
		Word word21 = new Word("xyz");
		Word word22 = new Word("abc");
		Word word31 = new Word("xyz");
		Word word32 = new Word("abc");
		
		
		Data dataIM = new Data();
		
		Word[] listIM = dataIM.get_list();
		
		checkIfInArray( listIM, word1,  dataIM);
		checkIfInArray( listIM, word2,  dataIM);
		checkIfInArray( listIM, word3,  dataIM);
		checkIfInArray( listIM, word4,  dataIM);
		checkIfInArray( listIM, word5,  dataIM);
		checkIfInArray( listIM, word6,  dataIM);
		checkIfInArray( listIM, word7,  dataIM);
		checkIfInArray( listIM, word8,  dataIM);
		checkIfInArray( listIM, word9,  dataIM);
		checkIfInArray( listIM, word21,  dataIM);
		checkIfInArray( listIM, word22,  dataIM);
		checkIfInArray( listIM, word31,  dataIM);
		checkIfInArray( listIM, word32,  dataIM);
		
		for(int i = 0; i <= dataIM.getLastIndex(); i++)
		{
			System.out.println(listIM[i].getWord());
		}
		System.out.println("First word count is " + word1.getWordCount());
		System.out.println("Second word count is " + word2.getWordCount());
		System.out.println("Last index is " + dataIM.getLastIndex());
		
		boolean isSecondSame = word1.isEqual(word2);
		boolean isThirdSame = word1.isEqual(word3);
		int wordCount1 =  word1.getWordCount();
		boolean isUnique1 = word1.getIsUnique();
		boolean isUnique2 =  word2.getIsUnique();
		boolean isUnique3 = word3.getIsUnique();
		
		/*System.out.println("First two words are equal is " + isSecondSame);
		System.out.println("First and third words are equal is " + isThirdSame);
		System.out.println("First word count is " + wordCount1);
		System.out.println("Second word count is " + word2.getWordCount());
		System.out.println("Third word count is " + word3.getWordCount());
		System.out.println("First word is unique is " + isUnique1);
		System.out.println("Second word is unique is " + isUnique2);
		System.out.println("Third word is unique is " + word3.getIsUnique());
		*/
	}
	
	// loops through passed Word array and checks if Word passed is in it
        // if not adds the string to the next open spot in the array
        static void checkIfInArray(Word[] wList, Word rightWord, Data data)
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
					data.incLastIndex();
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