package it.unibs.ingsw.mylib.utilities;

import org.jetbrains.annotations.NotNull;

/**
 * Classe di utilità per la gestione di una frazione (e operazioni con esse) in Java.
 *
 * @see <a href="https://codippa.com/how-to-work-with-fractions-in-java/">Frazioni in Java</a>
 */
public class Fraction {
    /**
     * Numeratore.
     */
    int numerator;
    /**
     * Denominatore.
     */
    int denominator;

    /**
     * Costruttore
     *
     * @param numerator numeratore.
     * @param denominator denominatore.
     */
    public Fraction(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        reduce();
    }

    /**
     * @return {@link #numerator}
     */
    public int getNumerator() {
        return numerator;
    }

    /**
     * Metodo per settare il numeratore.
     * @param numerator il numeratore.
     */
    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    /**
     * @return {@link #denominator}
     */
    public int getDenominator() {
        return denominator;
    }

    /**
     * Metodo per settare il denominatore.
     * @param denominator il denominatore.
     */
    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }

    /**
     * Metodo per il calcolo del massimo comun divisore di due numeri.
     *
     * @param numerator numeratore.
     * @param denominator denominatore.
     * @return il massimo comun divisore.
     */
    public int calculateGCD(int numerator, int denominator) {
        if (numerator % denominator == 0)
            return denominator;

        return calculateGCD(denominator, numerator % denominator);
    }

    /**
     * Riduce la frazione ai minimi termini.
     */
    void reduce() {
        int gcd = calculateGCD(numerator, denominator);
        numerator /= gcd;
        denominator /= gcd;
    }

    /**
     * Somma di due frazioni.
     *
     * @param fractionTwo frazione da sommare.
     * @return il risultato della somma.
     */
    public Fraction add(@NotNull Fraction fractionTwo) {
        int numer = (numerator * fractionTwo.getDenominator()) + (fractionTwo.getNumerator() * denominator);
        int denr = denominator * fractionTwo.getDenominator();
        return new Fraction(numer, denr);
    }

    /**
     * Sottrazione di due frazioni.
     *
     * @param fractionTwo frazione da sottrarre.
     * @return il risultato della sottrazione.
     */
    public Fraction subtract(@NotNull Fraction fractionTwo) {
        int newNumerator = (numerator * fractionTwo.denominator) - (fractionTwo.numerator * denominator);
        int newDenominator = denominator * fractionTwo.denominator;
        Fraction result = new Fraction(newNumerator, newDenominator);
        return result;
    }

    /**
     * Moltiplicazione di due frazioni.
     *
     * @param fractionTwo frazione da sottrarre.
     * @return il risultato della moltiplicazione.
     */
    public Fraction multiply(@NotNull Fraction fractionTwo) {
        int newNumerator = numerator * fractionTwo.numerator;
        int newDenominator = denominator * fractionTwo.denominator;
        Fraction result = new Fraction(newNumerator, newDenominator);
        return result;
    }

    /**
     * Divisione di due frazioni.
     *
     * @param fractionTwo frazione da dividere.
     * @return il risultato della divisione.
     */
    public Fraction divide(@NotNull Fraction fractionTwo) {
        int newNumerator = numerator * fractionTwo.getDenominator();
        int newDenominator = denominator * fractionTwo.numerator;
        Fraction result = new Fraction(newNumerator, newDenominator);
        return result;
    }

    /**
     * Metodo che ritorna la frazione sotto forma di decimale, con due cifre significative.
     * @return La frazione convertita in decimale.
     */
    public double getTwoDecimalNumber(){
        double ris = ((double)this.getNumerator() / (double)this.getDenominator());
        return Math.floor(ris * 100)/100;
    }

    /**
     * Metodo che ritorna se una frazione è minore di un determinato valore.
     * @param i il valore con cui effettuare il confronto con la frazione.
     * @return se la frazione è effettivamente minore del decimale immesso oppure no.
     */
    public boolean less(double i) {
        return numerator / denominator < i ? true : false;
    }

    /**
     * Metodo che ritorna una rappresentazione, sotto forma di stringa, della frazione.
     */
    @Override
    public String toString() {
        return this.numerator + "/" + this.denominator;
    }
}
