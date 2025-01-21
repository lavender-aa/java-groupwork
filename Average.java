/*
    Class: CMSC 3320 -- Technical Computing Using Java
    Assignment 1: Average Program
    Authors: Group 6
        - Camron Mellott (mel98378@pennwest.edu)
        - Lavender Wilson (wil81891@pennwest.edu)
        - Nicola Razumic-Rushin (raz73517@pennwest.edu)

*/

import java.io.*;

class Average {
    public static void main(String[] args) throws IOException {
        // create/instantiate variables used
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String line;
        double grade;
        double sum = 0.0;
        int numGrades = 0;
        boolean cont = true;
        
        while(cont) {

            // prompt and read a double from user
            System.out.print("Enter grade " + (numGrades + 1) + " (0.0-100.0): ");
            line = stdin.readLine();

            // convert string input into double
            grade = Double.parseDouble(line);

            // check if number entered is valid
            if(grade <= 100 && grade >= 0) {
                sum += grade;
                numGrades++;
            }
            else {
                // if not, stop the loop
                System.out.println("Invalid score; stopping loop.");
                cont = false;
            }
        }

        // calculate the average (sum is now average)
        sum /= numGrades;

        // make sure the value isn't NaN (possibly that 0 values were entered properly)
        if(Double.isNaN(sum)) {
            System.out.println("Error: average is NaN.");
        }
        else {
            System.out.println("The sum of all " + numGrades + " grades is " + sum);
        }
    }
}