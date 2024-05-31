package main.java.com.example;

/**
 * Task here is to write a list. Each element must know the element before and
 * after it. Print out your list and them remove the element in the middle of
 * the list. Print out again.
 *
 */

/* Essa classe representa um nó na lista
int data armazena dados do nó
p referencia para o nó anterior
n referencia para o proximo nó
o construtor cria um novo nó com o dado fornecido
*/
class Node {
    int data;
    Node p;
    Node n;

    public Node(int value) {
        this.data = value;
    }
}


/*Essa classe implementa uma lista duplamente encadeada
h representa o primeiro nó da lista
l representa o ultimo nó da lista
*
* */
class DoublyList {
    Node h;
    Node l;


    /*
    Adiciona um novo nó no final da lista
    Se a lista estiver vazia o novo nó se torna a cabeca h e também o ultimo nó o l.
    Se a lista não estiver vazia o novo no é adicionado após o ultimo nó e a referencia
    n do ultimo nó é atualizada para o novo nó a referencia p e atualizada para o antigo ultimo nó
    Então o novo nó se torna a cauda(ultimo nó) da lista duplamente encadeada
    */
    public void add(int data) {
        Node newNode = new Node(data);
        if (h == null) {
            h = newNode;
            l = newNode;
        } else {
            l.n = newNode;
            newNode.p = l;
            l = newNode;
        }
    }


    /*
      Aponta pra cabeça da lista imprime o item e depois passa pro próximo item
      da lista até que a referencia seja nula
     */
    public void printList() {
        Node current = h;
        System.out.print("List: ");
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.n;
        }
        System.out.println();
    }


    /*Remove o item do meio da lista duplamente encadeada
    * Dois ponteiros são usados um s que é lento e um f que é rapido que avança 2x mais rapido que o s
    * Quando o f chega no final da lista o s estára no meio da lista
    * Se a lista estiver vazia não há motivo para remoção
    * Se o nó meio for a cabeça , a cabeça e movida para o próximo nó
    * Se o nó meio for o final da lista, ou seja, a cauda, o nó do final da lista é movido para o anterior
    * Se o nó meio estiver entre a cabeça e o nó final da lista, ou seja , a cauda, os nós anteriores e posterior
    * ao nó do meio não vão apontar para o nó do meio , assim removendo ele da lista
    *
    *  */
    public void removeMiddle() {
        if (h == null || h.n == null) {
            System.out.println("List is empty or contain a unique element");
            return;
        }
        Node s = h;
        Node f = h;
        while (f != null && f.n != null) {
            s = s.n;
            f = f.n.n;
        }
        if (s == h) {
            h = h.n;
        } else {
            s.p.n = s.n;
        }
        if (s == l) {
            l = l.p;
        } else {
            s.n.p = s.p;
        }
    }
}


public class TASK2 {
    public static void main(String[] args) {
        DoublyList list = new DoublyList();
        //Adiciona numeros na lista
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(9);
        list.add(10);
        //Printa a lista primeiro
        list.printList();
        //Remove o item do meio
        list.removeMiddle();
        //Printa a lista novamente
        list.printList();
    }
}