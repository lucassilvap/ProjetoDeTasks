package main.java.com.example;

/**
 * Task here is to write a list. Each element must know the element before and
 * after it. Print out your list and them remove the element in the middle of
 * the list. Print out again.
 *
 */
class Node {
    int data;
    Node previous;
    Node next;

    public Node(int data) {
        this.data = data;
    }
}

class DoublyLinkedList {
    Node head;
    Node tail;

    public void add(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;
        }
    }

    public void printList() {
        Node current = head;
        System.out.print("List ");
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println();
    }

    public void removeMiddle() {
        if (head == null || head.next == null) {
            System.out.println("List is empty or contain a unique element");
            return;
        }
        Node slow = head;
        Node fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        if (slow == head) {
            head = head.next;
        } else {
            slow.previous.next = slow.next;
        }
        if (slow == tail) {
            tail = tail.previous;
        } else {
            slow.next.previous = slow.previous;
        }
    }
}


public class TASK2 {
    public static void main(String[] args) {
        DoublyLinkedList list = new DoublyLinkedList();
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
        list.printList();
        list.removeMiddle();
        list.printList();
    }
}