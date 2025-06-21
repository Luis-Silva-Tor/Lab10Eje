package btree;
import Registro.RegistroEstudiante;

import exceptions.ItemDuplicatedException;
import exceptions.ItemNotFoundException;

import java.io.*;
import java.util.*;

public class BTree<E extends Comparable<E>> {
    private BNode<E> root;
    private int orden;
    private boolean up;
    private BNode<E> nDes;

    public BTree(int orden) {
        this.orden = orden;
        this.root = null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void insert(E cl) {
        up = false;
        E mediana;
        BNode<E> pnew;
        mediana = push(this.root, cl);
        if (up) {
            pnew = new BNode<>(this.orden);
            pnew.count = 1;
            pnew.keys.set(0, mediana);
            pnew.childs.set(0, this.root);
            pnew.childs.set(1, nDes);
            this.root = pnew;
        }
    }

    private E push(BNode<E> current, E cl) {
        int pos[] = new int[1];
        E mediana;
        if (current == null) {
            up = true;
            nDes = null;
            return cl;
        } else {
            boolean fl;
            fl = current.searchNode(cl, pos);
            if (fl) throw new ItemDuplicatedException("Clave duplicada: " + cl);

            mediana = push(current.childs.get(pos[0]), cl);
            if (up) {
                if (current.nodeFull(this.orden - 1)) {
                    mediana = dividedNode(current, mediana, pos[0]);
                } else {
                    putNode(current, mediana, nDes, pos[0]);
                    up = false;
                }
            }
            return mediana;
        }
    }

    private void putNode(BNode<E> current, E cl, BNode<E> rd, int k) {
        int i;
        for (i = current.count - 1; i >= k; i--) {
            current.keys.set(i + 1, current.keys.get(i));
            current.childs.set(i + 2, current.childs.get(i + 1));
        }
        current.keys.set(k, cl);
        current.childs.set(k + 1, rd);
        current.count++;
    }

    private E dividedNode(BNode<E> current, E cl, int k) {
        BNode<E> rd = nDes;
        int i, posMdna = this.orden / 2;
        BNode<E> right = new BNode<>(this.orden);

        for (i = posMdna; i < this.orden - 1; i++) {
            right.keys.set(i - posMdna, current.keys.get(i));
            right.childs.set(i - posMdna + 1, current.childs.get(i + 1));
        }

        right.count = (this.orden - 1) - posMdna;
        current.count = posMdna;

        if (k < posMdna) {
            putNode(current, cl, rd, k);
        } else {
            putNode(right, cl, rd, k - posMdna);
        }

        E median = current.keys.get(current.count - 1);
        right.childs.set(0, current.childs.get(current.count));
        current.childs.set(current.count, null);
        current.count--;

        nDes = right;
        return median;
    }

    public boolean search(E cl) {
        return searchRecursive(this.root, cl);
    }

    private boolean searchRecursive(BNode<E> node, E cl) {
        if (node == null) return false;
        for (int i = 0; i < node.count; i++) {
            int cmp = cl.compareTo(node.keys.get(i));
            if (cmp == 0) {
                System.out.println(cl + " se encuentra en el nodo " + node.idNode + " en la posición " + i);
                return true;
            } else if (cmp < 0) {
                return searchRecursive(node.childs.get(i), cl);
            }
        }
        return searchRecursive(node.childs.get(node.count), cl);
    }

    public void remove(E cl) {
        // Aquí se implementaría el remove con redistribución, fusión, propagación
        // Por ahora solo se puede dejar un aviso hasta que implementes por completo:
        throw new UnsupportedOperationException("Método remove() aún no implementado");
    }

    
  // Metodo nuevo añadido e clase building_Btree()

    
    

        public static BTree<Integer> building_Btree(String filepath) throws IOException, ItemNotFoundException {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            int orden = Integer.parseInt(br.readLine());
            Map<Integer, BNode<Integer>> nodos = new HashMap<>();
            Map<Integer, Integer> niveles = new HashMap<>();

            BTree<Integer> tree = new BTree<>(orden);

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int nivel = Integer.parseInt(parts[0]);
                int idNodo = Integer.parseInt(parts[1]);
                BNode<Integer> nodo = new BNode<>(orden);
                nodo.idNode = idNodo;
                nodo.count = parts.length - 2;
                for (int i = 2; i < parts.length; i++) {
                    nodo.keys.set(i - 2, Integer.parseInt(parts[i]));
                }
                nodos.put(idNodo, nodo);
                niveles.put(idNodo, nivel);
            }
            br.close();

            // encontrar la raiz el nivel mas alto 
            int rootId = niveles.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
            tree.root = nodos.get(rootId);

            // asociar hijos
            for (Map.Entry<Integer, BNode<Integer>> entry : nodos.entrySet()) {
                int parentId = entry.getKey();
                BNode<Integer> parent = entry.getValue();
                int nivelParent = niveles.get(parentId);
                int childIndex = 0;
                for (Map.Entry<Integer, Integer> n2 : niveles.entrySet()) {
                    if (n2.getValue() == nivelParent - 1) {
                        BNode<Integer> child = nodos.get(n2.getKey());
                        if (!parent.childs.contains(child)) {
                            parent.childs.set(childIndex++, child);
                        }
                    }
                }
            }

            // validar 
            if (tree.root == null || tree.root.count == 0) {
                throw new ItemNotFoundException(".");
            }

            return tree;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            if (isEmpty()) {
                s.append("BTree is empty...\n");
            } else {
                s.append("Id.Nodo  Claves Nodo     Id.Padre  Id.Hijos\n");
                s.append("\n");
                s.append(writeTree(this.root, -1));
            }
            return s.toString();
        }

        private String writeTree(BNode<E> current, int parentId) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-8d  (%s)", current.idNode, clavesTexto(current)));
            int pad = 16 - clavesTexto(current).length();
            sb.append(" ".repeat(Math.max(0, pad)));
            sb.append(" ");
            sb.append(parentId == -1 ? "--      " : String.format("[%-6d]", parentId));
            sb.append(" ");

            ArrayList<Integer> hijosIDs = new ArrayList<>();
            for (int i = 0; i <= current.count; i++) {
                BNode<E> hijo = current.childs.get(i);
                if (hijo != null) hijosIDs.add(hijo.idNode);
            }

            sb.append(hijosIDs.isEmpty() ? "--" : hijosIDs.toString().replace(" ", ""));
            sb.append("\n");

            for (int i = 0; i <= current.count; i++) {
                BNode<E> hijo = current.childs.get(i);
                if (hijo != null) sb.append(writeTree(hijo, current.idNode));
            }

            return sb.toString();
        }

        private String clavesTexto(BNode<E> node) {
            StringBuilder txt = new StringBuilder();
            for (int i = 0; i < node.count; i++) {
                txt.append(node.keys.get(i));
                if (i < node.count - 1) txt.append(", ");
            }
            return txt.toString();
        }

      


    
    // registro estudiante para encontrar por nombre 
    public String buscarNombre(int codigo) {
        return buscarNombreRecursivo(this.root, codigo);
    }

    private String buscarNombreRecursivo(BNode<E> node, int codigo) {
        if (node == null) return "No encontrado";

        for (int i = 0; i < node.count; i++) {
            E clave = node.keys.get(i);

            // Verifica si el objeto actual es un RegistroEstudiante
            if (clave instanceof RegistroEstudiante est) {
                if (codigo == est.getCodigo()) {
                    return est.getNombre();
                } else if (codigo < est.getCodigo()) {
                    return buscarNombreRecursivo(node.childs.get(i), codigo);
                }
            }
        }
        return buscarNombreRecursivo(node.childs.get(node.count), codigo);
    }

}
