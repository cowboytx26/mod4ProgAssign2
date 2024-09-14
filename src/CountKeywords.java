/*
Short Description:  This program will accept a file name and count the number of keywords in the file.  It ignores
                    keywords found in strings and comments.  It recognizes two types of comments - inline comments
                    and paragraph comments.
Author:  Brian Wiatrek
Date:  September 13, 2024
*/
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;
import java.io.*;

public class CountKeywords {

    //This constant represents when the beginning of a paragraph comment is identified
    static final int paragraphCommentStart = 1;
    //This constant represents when the end of a paragraph comment is identified
    static final int paragraphCommentEnd = 2;
    //This constant represents when input contains a string both the beginning and ending of the string
    static final int stringContained = 3;
    //This constant represents when input does not contain a string or a comment
    static final int noString = 0;
    //This constant represents when a string has been identified but not yet ended
    static final int stringIndicated = 5;
    //This represents when a comment has been identified
    static final int commentIndicated = 6;

    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter a Java source file: ");
        String filename = input.nextLine();

        File file = new File(filename);
        if (file.exists()) {
            System.out.println("The number of keywords in " + filename
                    + " is " + countKeywords(file));
        } else {
            System.out.println("File " + filename + " does not exist");
        }
    }

    public static int countKeywords(File file) throws Exception {
        // Array of all Java keywords + true, false and null
        String[] keywordString = {"abstract", "assert", "boolean",
                "break", "byte", "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else", "enum",
                "extends", "for", "final", "finally", "float", "goto",
                "if", "implements", "import", "instanceof", "int",
                "interface", "long", "native", "new", "package", "private",
                "protected", "public", "return", "short", "static",
                "strictfp", "super", "switch", "synchronized", "this",
                "throw", "throws", "transient", "try", "void", "volatile",
                "while", "true", "false", "null"};

        Set<String> keywordSet =
                new HashSet<>(Arrays.asList(keywordString));
        int count = 0;

        //This variable is used to receive the output of countComment method
        int stringIndicator = noString;

        //This variable is set to true if the input is inside a string
        boolean inString = false;
        //This variable is set to true if the input is inside a line comment
        boolean inLineComment = false;
        //This variable is set to true if the input is inside a paragraph comment
        boolean inParagraphComment = false;

        Scanner input = new Scanner(file);

        while (input.hasNextLine()) {
            inLineComment = false;
            String lineInput = input.nextLine();
            for (String word : lineInput.split(" ")) {
                stringIndicator = countComment(word);
                if (stringIndicator == stringIndicated) {
                    if (!inString) inString = true;
                    else {
                        if (inString) inString = false;
                    }

                }
                if (stringIndicator == commentIndicated) {
                    inLineComment = true;
                }
                if (stringIndicator == paragraphCommentStart) {
                    inParagraphComment = true;
                }
                if (stringIndicator == paragraphCommentEnd){
                    inParagraphComment = false;
                }
                System.out.printf("%s: %d\n", word, stringIndicator);
                if ((keywordSet.contains(word)) && (stringIndicator != stringContained) && (!inString)
                        && (!inLineComment) && (!inParagraphComment)) {
                    count++;
                    System.out.println(count);
                }
            }
        }

        return count;
    }

    public static int countComment(String inputWord){

        CharacterIterator inputWordIterator = new StringCharacterIterator(inputWord);
        boolean inString = false;
        boolean containsString = false;
        boolean possibleComment = false;
        boolean containsComment = false;
        boolean containsParagraphCommentStart = false;

        //If the input contains the paragraph end comment, just return the paragraph comment end indicator
        if (inputWord.contains("*/")) return paragraphCommentEnd;

        //Loop through each character of the input word until the end of the word
        while ((inputWordIterator.current() != CharacterIterator.DONE)){
            Character testChar = inputWordIterator.current();
            //when the possible comment indicator is set, it means that a forward slash was found previously
            if (possibleComment) {
                //if the next character is a forward slash, then we have a line comment
                if (testChar.equals('/')) {
                    containsComment = true;
                    possibleComment = false;
                }
                //if the next character is a forward slash, then we have the beginning of a paragraph comment
                if (testChar.equals('*')) {
                    containsParagraphCommentStart = true;
                    possibleComment = false;
                }
            }
            //if we find a foward slash, we have the beginning of a possible comment
            else if (testChar.equals('/')) {
                possibleComment = true;
            }
            //if we find a double quote, we indicate a string
            if (testChar.equals('"')) {
                //if we are not in a string already, then we are now
                if (!inString) inString = true;
                //if we were already in a string, then we found the end of the string
                else {
                    inString = false;
                    containsString = true;
                }
            }
            inputWordIterator.next();
        }

        if (containsParagraphCommentStart) return paragraphCommentStart;
        if (containsComment) return commentIndicated;
        if ((!inString) && (!containsString)) return noString;
        if ((!inString) && (containsString)) return stringContained;
        if (inString) return stringIndicated;
        return noString;
    }
}