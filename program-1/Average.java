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
        double average;
        int numGrades = 0;
        boolean cont = true;
        
        while(cont) {

            // prompt and read a double from user
            System.out.print("Enter grade " + (numGrades + 1) + " (0.0-100.0): ");
            line = stdin.readLine();

            // convert string input into double
            try {
                grade = Double.parseDouble(line);
            }
            catch (NumberFormatException e) {
                System.out.println("Input cannot be interpreted as a double.\n");
                continue;
            }

            // check if number entered is valid
            if(grade <= 100 && grade >= 0) {
                sum += grade;
                numGrades++;
            }
            else {
                // if not, stop the loop
                System.out.println("Score outside range; stopping loop.\n");
                cont = false;
            }
        }

        // calculate the average
        average = sum / numGrades;

        // make sure the value isn't NaN (possibly that 0 values were entered properly)s
        if(Double.isNaN(average)) {
            System.out.println("Error: cannot calculate average of 0 grades (NaN).");
        }
        else {
            System.out.println("The average of all " + numGrades + " grades is " + average);
        }
    }
}