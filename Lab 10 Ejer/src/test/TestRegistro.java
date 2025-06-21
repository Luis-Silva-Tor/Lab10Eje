package test;


import btree.BTree;
import Registro.RegistroEstudiante;
import exceptions.ItemDuplicatedException;

public class TestRegistro {
    public static void main(String[] args) {
        BTree<RegistroEstudiante> arbol = new BTree<>(4);

        RegistroEstudiante[] estudiantes = {
            new RegistroEstudiante(103, "Ana"),
            new RegistroEstudiante(110, "Luis"),
            new RegistroEstudiante(101, "Carlos"),
            new RegistroEstudiante(120, "Lucía"),
            new RegistroEstudiante(115, "David"),
            new RegistroEstudiante(125, "Jorge"),
            new RegistroEstudiante(140, "Camila"),
            new RegistroEstudiante(108, "Rosa"),
            new RegistroEstudiante(132, "Ernesto"),
            new RegistroEstudiante(128, "Denis"),
            new RegistroEstudiante(145, "Enrique"),
            new RegistroEstudiante(122, "Karina"),
            new RegistroEstudiante(108, "Juan") // Duplicado
        };

        for (RegistroEstudiante est : estudiantes) {
        	try {
        	    arbol.insert(est);
        	} catch (ItemDuplicatedException e) {
        	    System.out.println("Error: Clave duplicada: " + est);
        	}

        }

        // Operaciones
        System.out.println(arbol.buscarNombre(115)); // David
        System.out.println(arbol.buscarNombre(132)); // Ernesto
        System.out.println(arbol.buscarNombre(999)); // No encontrado

        System.out.println("Eliminando estudiante con código 101...");
        // arbol.remove(new RegistroEstudiante(101, "Carlos")); // aún no implementado

        System.out.println("Insertando estudiante: 106 - Sara");
        arbol.insert(new RegistroEstudiante(106, "Sara"));

        System.out.println(arbol.buscarNombre(106)); // Sara

      
    }
}
