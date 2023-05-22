package it.unibs.ingsw.mylib.utilities;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Classe di utilità per gestire l'input da CLI.
 */
public class DataInput {
	/**
	 * Scanner per captare l'input da tastiera.
	 */
	private static final Scanner reader = createScanner();

	/**
	 * Stringa rappresentante l'errore di formato.
	 */
	private static final String FORMAT_ERROR = "Attenzione: il dato inserito non è nel formato corretto";

	/**
	 * Stringa rappresentante l'errore di inserimento del valore minimo.
	 */
	private static final String MINIMUM_ERROR = "Attenzione: è richiesto un valore maggiore a ";

	/**
	 * Stringa rappresentante il mancato inserimento di (almeno) un carattere.
	 */
	private static final String EMPTY_STRING_ERROR = "Attenzione: non hai inserito alcun carattere";

	/**
	 * Stringa rappresentante l'errore di inserimento del valore massimo.
	 */
	private static final String MAXIMUM_ERROR = "Attenzione: è richiesto un valore minore o uguale a ";

	/**
	 * Stringa per segnalare quali sono i caratteri ammessi.
	 */
	private static final String ALLOWED_CHARS = "Attenzione: i caratteri ammissibili sono: ";

	/**
	 * Stringa rappresentante il sì.
	 */
	private static final char YES = 's'; // sì.

	/**
	 * Stringa rappresentante il no.
	 */
	private static final char NO = 'N';

	/**
	 * Metodo che crea lo scanner.
	 *
	 * @return lo scanner creato.
	 */
	private static @NotNull Scanner createScanner() {
		Scanner created = new Scanner(System.in);
		created.useDelimiter(System.lineSeparator() + "|\n");
		return created;
	}

	/**
	 * Legge la stringa immessa da tastiera.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @return la stringa completa.
	 */
	public static String readString(String message) {
		System.out.print(message);
		return reader.next();
	}

	/**
	 * Legge la stringa immessa da tastiera, deve essere un valore prestabilito oppure escape.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @return la stringa completa.
	 */
	public static String optionInput(String message) {
		String read = null;
		boolean ended = false;
		String unit = "g";

		do {
			read = readString(message);
			read = read.trim();
			if (read.length() == 0 || read.equals(unit)) {
				ended = true;
			} else {
				System.out.println("Attenzione: non ha inserito alcun carattere");
			}
		} while(!ended);

		return read;
	}

	/**
	 * Legge una stringa non vuota.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @return la stringa completa.
	 */
	public static String readNotEmptyString(String message) {
		boolean ended = false;
		String read = null;

		do {
			read = readString(message);
			read = read.trim();
			if (read.length() > 0) {
				ended = true;
			} else {
				System.out.println("Attenzione: non ha inserito alcun carattere");
			}
		} while(!ended);

		return read;
	}

	/**
	 * Legge un singolo carattere,
	 * @param message messaggio per richiedere l'input all'utente.
	 * @return il carattere inserito.
	 */
	public static char readChar(String message) {
		boolean ended = false;
		char readValue = 0;

		do {
			System.out.print(message);
			String reading = reader.next();
			if (reading.length() > 0) {
				readValue = reading.charAt(0);
				ended = true;
			} else {
				System.out.println("Attenzione: non ha inserito alcun carattere");
			}
		} while(!ended);

		return readValue;
	}

	/**
	 * Legge un carattere e lo trasforma in maiuscolo.
	 * @param read il carattere immesso.
	 * @param allowed i caratteri ammissibili.
	 * @return il carattere, stavolta in maiuscolo.
	 */
	public static char readUpperChar(String read, @NotNull String allowed) {
		boolean ended = false;
		boolean var3 = false;

		char readValue;
		do {
			readValue = readChar(read);
			readValue = Character.toUpperCase(readValue);
			if (allowed.indexOf(readValue) != -1) {
				ended = true;
			} else {
				System.out.println("Attenzione: i caratteri ammissibili sono: " + allowed);
			}
		} while(!ended);

		return readValue;
	}

	/**
	 * Legge un valore intero.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @return il valore intero immesso.
	 */
	public static int readInt(String message) {
		boolean ended = false;
		int readValue = 0;

		do {
			System.out.print(message);

			try {
				readValue = reader.nextInt();
				ended = true;
			} catch (InputMismatchException var5) {
				System.out.println(DataInput.FORMAT_ERROR);
				String var4 = reader.next();
			}
		} while(!ended);

		return readValue;
	}

	/**
	 * Legge un intero positivo maggiore di 0.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @return il valore intero positivo maggiore di 0 immesso.
	 */
	public static int readPositiveInt(String message) {
		return readIntWithMinimum(message, 1);
	}

	/**
	 * Legge un intero positivo.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @return il valore intero immesso.
	 */
	public static int readNonNegativeInt(String message) {
		return readIntWithMinimum(message, 0);
	}

	/**
	 * Legge un intero maggiore di un valore minimo prestabilito.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @param minimum il valore minimo richiesto.
	 * @return il valore intero immesso.
	 */
	public static int readIntWithMinimum(String message, int minimum) {
		boolean ended = false;
		boolean var3 = false;

		int readValue;
		do {
			readValue = readInt(message);
			if (readValue >= minimum) {
				ended = true;
			} else {
				System.out.println("Attenzione: è richiesto un valore maggiore a " + minimum);
			}
		} while(!ended);

		return readValue;
	}

	/**
	 * Legge un intero maggiore di un minimo e minore di un massimo prestabiliti.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @param minimum il valore minimo richiesto.
	 * @param maximum il valore massimo richiesto.
	 * @return il valore intero immesso.
	 */
	public static int readIntWIthMaxAndMin(String message, int minimum, int maximum) {
		boolean ended = false;
		boolean var4 = false;

		int readValue;
		do {
			readValue = readInt(message);
			if (readValue >= minimum && readValue <= maximum) {
				ended = true;
			} else if (readValue < minimum) {
				System.out.println("Attenzione: è richiesto un valore maggiore a " + minimum);
			} else {
				System.out.println("Attenzione: è richiesto un valore minore o uguale a " + maximum);
			}
		} while(!ended);

		return readValue;
	}

	/**
	 * Legge un valore decimale.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @return il valore decimale immesso.
	 */
	public static double readDouble(String message) {
		boolean ended = false;
		double readValue = 0.0D;

		do {
			System.out.print(message);

			try {
				readValue = reader.nextDouble();
				ended = true;
			} catch (InputMismatchException var6) {
				System.out.println(DataInput.FORMAT_ERROR);
				String var5 = reader.next();
			}
		} while(!ended);

		return readValue;
	}

	/**
	 * Legge un decimale positivo maggiore di 0.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @return il valore decimale positivo maggiore di 0 immesso.
	 */
	public static double readPositiveDouble(String message) {
		return readDoubleWithMinimum(message, 0.0D);
	}

	/**
	 * Legge un decimale maggiore di un valore minimo prestabilito.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @param minimum il valore minimo richiesto.
	 * @return il valore decimale immesso.
	 */
	public static double readDoubleWithMinimum(String message, double minimum) {
		boolean finito = false;
		double readValue = 0.0D;

		do {
			readValue = readDouble(message);
			if (readValue > minimum) {
				finito = true;
			} else {
				System.out.println("Attenzione: è richiesto un valore maggiore a " + minimum);
			}
		} while(!finito);

		return readValue;
	}

	/**
	 * Legge una stringa che può essere S, sì, oppure N, no.
	 * @param message messaggio per richiedere l'input all'utente.
	 * @return true se si è inserito S, false altrimenti.
	 */
	public static boolean yesOrNo(String message) {
		String myMessage = message + "(" + 'S' + "/" + 'N' + ")\n» ";
		char readValue = readUpperChar(myMessage, String.valueOf('S') + String.valueOf('N'));
		return readValue == 'S';
	}

	/**
	 * Traduce sì o no dall'Inglese.
	 * @param trueOrFalse il valore del booleano.
	 * @return se il booleano trueOrFalse è yes oppure no.
	 */
	@Contract(pure = true)
	public static @NotNull String translate(boolean trueOrFalse) {
		return trueOrFalse ? "yes" : "no";
	}

	/**
	 * @see <a href="https://www.javatpoint.com/how-to-take-multiple-string-input-in-java-using-scanner">Input di più stringhe</a>
	 *
	 * @param message messaggio per richiedere l'input all'utente.
	 * @return le stringhe immesse.
	 */
	public static @NotNull ArrayList<String> readMultipleString(String message) {
		Scanner sc = new Scanner(System.in);
		System.out.print(message);

		int howMany = sc.nextInt();
		ArrayList<String> strings = new ArrayList<>();

		System.out.println("\nInserire le stringhe:");
		sc.nextLine();
		for (int i = 0; i < howMany; i++) {
			System.out.print("\n» ");
			strings.add(sc.nextLine());
		}

		return strings;
	}
}
