package com.example;

import java.util.*;

/**
 * Write a list and add an aleatory number of Strings. In the end, print out how
 * many distinct itens exists on the list.
 *
 */


public class TASK3 {

    public static void main(String[] args) {
        /*Usado para adicionar números aleatórios*/
        Random random = new Random();
        /* Usado para armazenar items não duplicado*/
        Set<String> set = new TreeSet<>();
        /* Usado para ler a entrada do usuário */
        Scanner scann = new Scanner(System.in);
        /*Usada para armazenar os itens aleatórios*/
        List<String> listOfRandomNumbers = new ArrayList<>();


        System.out.println("Tecle quantos itens você quer adicionar à lista:");
        /*Armazena quantos items quer adicionar na lista*/
        int quantityOfItems = scann.nextInt();

        /*Gera um numero aleatorio entre 0 e 99 e adiciona na lista
        * adicona a quantidade de itens que foi especificada*/
        for (int i = 0; i < quantityOfItems; i++) {
            listOfRandomNumbers.add("Item:" + random.nextInt(100));
        }

        /*Adiciona todos itens na lista do set não adicionando os duplicados*/
        set.addAll(listOfRandomNumbers);
        /*Imprime todos os itens da lista*/
        System.out.println("Itens na lista:");
       listOfRandomNumbers.forEach(System.out::println);
       /*Imprime a quantidade de itens distintos*/
       System.out.println("Número de itens distintos: " + set.size());

    }

}
