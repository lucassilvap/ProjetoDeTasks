package main.java.com.example;
import java.util.Scanner;
/**
 * 
 *
 * Task here is to implement a function that says if a given string is
 * palindrome.
 * 
 * 
 * 
 * Definition=> A palindrome is a word, phrase, number, or other sequence of
 * characters which reads the same backward as forward, such as madam or
 * racecar.
 */
public class TASK1 {

    public static boolean isPalindrome(String phrase){
        // remove todos os espaços da frase e coloca tudo em letra maiscula
        phrase = phrase.replaceAll("\\s+", "").toUpperCase();

        // pega o tamanho da frase
        int lengthPhrase = phrase.length();

        // verifica se a frase é um palindrome
        for (int i = 0; i < lengthPhrase / 2; i++) {
            if (phrase.charAt(i) != phrase.charAt(lengthPhrase - i - 1)) {
                return false;
            }
        }
        return true;
    }
}
