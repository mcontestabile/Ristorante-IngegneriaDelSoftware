package it.unibs.ingsw.mylib.xml_utils;

import java.util.*;

/**
 * Classe necessaria per salvare le informazioni dei getter e setter delle
 * classi che implementano Parsable e Writable.
 *
 * @author TheTrinity - Progetto Arnaldo 2021
 * @author Baresi Marco
 * @author Contestabile Martina
 * @author Iannella Simone
 *
 * @param <K> chiave
 * @param <V> valore
 *
 * @see <a href="https://github.com/GithubboAncheIo/TheTrinity_Arnaldo2021">TheTrinity GitHub</a>
 * @see Parsable
 * @see Writable
 */
public class MultiMap<K, V> {

    private Map<K, Collection<V>> map = new HashMap<>();

    /**
     * Aggiunta del valore specificato con la chiave specificata.
     *
     * @param key   la chiave.
     * @param value il valore associato alla chiave.
     */
    public void put(K key, V value) {
        map.computeIfAbsent(key, k -> new ArrayList<V>());
        map.get(key).add(value);
    }

    /**
     * Associa la chiave specificata con il dato valore se non
     * già associato al valore.
     *
     * @param key   la chiave associata.
     * @param value il valore associato alla chiave.
     */
    public void putIfAbsent(K key, V value) {
        map.computeIfAbsent(key, k -> new ArrayList<>());

        // se il valore è assente, inserirlo.
        if (!map.get(key).contains(value)) {
            map.get(key).add(value);
        }
    }

    /**
     * Ritorna la Collezione di valori a cui la specifica chiave è mappata,
     * altrimenti null se la multimap non contiene alcun mapping per la chiave.
     *
     * @param key la chiave.
     * @return la collezione di valori associata alla chiave.
     */
    public Collection<V> get(Object key) {
        return map.get(key);
    }

    /**
     * Ritorna un set delle chiave contenute nella multimap.
     *
     * @return il set di chiavi nella multimap.
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Ritorna un set del mapping contenuto nella multimap.
     *
     * @return il Set del mapping.
     */
    public Set<Map.Entry<K, Collection<V>>> entrySet() {
        return map.entrySet();
    }

    /**
     * Ritorna una Collezione di Collezione di valori contenuti nella multimap
     *
     * @return la Collection.
     */
    public Collection<Collection<V>> values() {
        return map.values();
    }

    /**
     * Ritorna true se questa multimap contiene un mapping for per la chiave specificata.
     *
     * @param key la chiave immessa.
     * @return se c'è un valore per key immessa o meno.
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * Rimuove il mapping per la chiave specificata da questo multimap se presente e
     * ritorna una Collezione dei valori precedentemente associati a tale chiave, o
     * null se in precedenza non c'erano.
     *
     * @param key la chiave immessa.
     * @return la collection privata del key e il value a essa associata.
     */
    public Collection<V> remove(Object key) {
        return map.remove(key);
    }

    /**
     * Ritorna il numero totale di key-value mappati nella multimap.
     *
     * @return la dimensione della multimap.
     */
    public int size() {
        int size = 0;
        for (Collection<V> value : map.values()) {
            size += value.size();
        }
        return size;
    }

    /**
     * Ritorna se nella multimap non ci sono key-value, ossia se è vuota.
     * @return se nella multimap non ci key-value, ossia se è vuota.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Metodo che rende vuota la multimap, svuotandola di tutti i valori salvati.
     */
    public void clear() {
        map.clear();
    }


    /**
     * Rimuove le entry per la chiave specificate solo se attualmente mappa
     * il valore specificato, ossia messo in ingresso al metodo, deve esserci
     * una corrispondenza key-value, e ritorna true se rimosso, false altrimenti.
     *
     * @param key    la chiave.
     * @param value  il valore associato a key.
     * @return se il dato è presente ed è stato tolto con successo o meno.
     */
    public boolean remove(K key, V value) {
        if (map.get(key) != null) // se la chiave esiste, prosegue.
            return map.get(key).remove(value);

        return false;
    }

    /**
     * Rimpiazza le entry per la chiave specificata solo se
     * mappano il valore immesso. Sostanzialmente, è il metodo che,
     * come si deduce dal nome, rimpiazza il valore vecchio con
     * quello nuovo, ma effettua un controllo per evitare errori.
     *
     * @param key       la chiave.
     * @param oldValue  il valore associato a key.
     * @param newValue  il nuovo valore da associare a key.
     * @return se la sostituzione può essere effettuata.
     */
    public boolean replace(K key, V oldValue, V newValue) {
        if (map.get(key) != null) {
            if (map.get(key).remove(oldValue)) {
                return map.get(key).add(newValue);
            }
        }
        return false;
    }
}