package com.example;

import java.util.*;

/**
 * Write a list and add an aleatory number of Strings. In the end, print out how
 * many distinct itens exists on the list.
 *
 */


public class TASK3 {

    public static void main(String[] args) {
        Random random = new Random();
        Set<String> set = new TreeSet<>();
        Scanner scann = new Scanner(System.in);
        List<String> listOfRandomNumbers = new ArrayList<>();

        System.out.println("Tecle quantos itens você quer adicionar à lista:");
        int quantityOfItems = scann.nextInt();
        for (int i = 0; i < quantityOfItems; i++) {
            listOfRandomNumbers.add("Item:" + random.nextInt(100));
        }

        set.addAll(listOfRandomNumbers);
        System.out.println("Itens na lista:");
       listOfRandomNumbers.forEach(System.out::println);
       System.out.println("Número de itens distintos: " + set.size());

    }

}
