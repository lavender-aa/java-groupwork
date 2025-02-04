import java.io.*;
import java.lang.*;

public class TestNumber
{
	public static void main(String[] args)
	{
		String aNumber = "123";
		String aNegNumber = "-----123";
		String aWord = "abcd";
		
		System.out.println("The String aNumber is a a number: " + isValidNumber(aNumber));
		System.out.println("The String aNegNumber is a a number: " + isValidNumber(aNegNumber));
		System.out.println("The String aWord is a a number: " + isValidNumber(aWord));
		System.out.println("The value of aNegNumber is: " + aNegNumber);
	}
	
	
// method checks if a string passed to it is a valid number
        static boolean isValidNumber(String maybeNumber)
        {
            boolean isValid = false;
            boolean wasNegative = false;
            
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

}