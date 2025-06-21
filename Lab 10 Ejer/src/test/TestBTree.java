package test;

import btree.BTree;

import exceptions.ItemNotFoundException;

public class TestBTree {
    public static void main(String[] args) {
        try {
            // Ruta del archivo 
            String ruta = "arbolB.txt";

            // Construye el árbol a partir
            BTree<Integer> arbol = BTree.building_Btree(ruta);

            // Si se construyó correctamente
            System.out.println("Árbol B");
            System.out.println(arbol);

        } catch (ItemNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception ex) {
            System.out.println("Error inesperado: " + ex.getMessage());
        }
    }
}
