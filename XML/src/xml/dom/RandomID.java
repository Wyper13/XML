/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Thibault
 */
public class RandomID
{
    private Map<Integer, Integer> H;
    private List<Integer> A;
    
    public RandomID()
    {
        H = new HashMap<>();
        A = new ArrayList<>();
    }
    
    public void insert(int v)
    {
        A.add(v);
        H.put(v, A.size()-1);
    }
    
    public boolean contains(int v)
    {
        return H.containsKey(v);
    }
    
    public int getRandomElement()
    {
        Double rand = Math.random(); // [0;1]
        int r = (int)(rand * A.size()-1); // [0;A-1]
        return A.get(r);
    }
    
    public void remove(int v)
    {
        int m = A.size()-1; // index du dernier élément de A
        int d = A.get(m);   // dernier élément de A
        int i = H.get(v);   // index dans A de v (valeur à bouger)
        A.set(i, d);        // remplacer la valeur à enlever par le dernière valeur
        A.remove(m);        // décroit la taille de A de 1
        H.remove(v);        // supprimer la valeur v de H
        H.put(d, i);        // ajouter la valeur d avec le bon indice
    }
}
