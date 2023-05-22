package it.unibs.ingsw.mylib.utilities;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import de.vandermeer.skb.interfaces.transformers.textformat.Text_To_FormattedText;
import it.unibs.ingsw.entrees.appetizers.Appetizer;
import it.unibs.ingsw.entrees.cookbook.CookbookRecipe;
import it.unibs.ingsw.entrees.drinks.Drink;
import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.entrees.resturant_courses.Dish;
import it.unibs.ingsw.users.manager.Manager;
import it.unibs.ingsw.users.warehouse_worker.Article;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.Random;

/**
 * Classe di utilità per generare stringhe colorate.
 *
 * @see <a href="https://github.com/jankoksik/WhatDeBug">WhatTheBug Repo</a>
 */
public class AsciiArt {
    /**
     * Reset.
     */
    final static String ANSI_RESET = "\u001B[0m";
    /**
     * Nero.
     */
    final static String ANSI_BLACK = "\u001B[30m";
    /**
     * Rosso.
     */
    final static String ANSI_RED = "\u001B[31m";
    /**
     * Verde.
     */
    final static String ANSI_GREEN = "\u001B[32m";
    /**
     * Giallo.
     */
    final static String ANSI_YELLOW = "\u001B[33m";
    /**
     * Blu.
     */
    final static String ANSI_BLUE = "\u001B[34m";
    /**
     * Viola.
     */
    final static String ANSI_PURPLE = "\u001B[35m";
    /**
     * Ciano.
     */
    final static String ANSI_CYAN = "\u001B[36m";
    /**
     * Bianco.
     */
    final static String ANSI_WHITE = "\u001B[37m";

    /**
     * Genera il testo colorato.
     * @param text il testo da visualizzare.
     * @param color il colore da usare.
     * @return La stringa colorata.
     *
     */
    public static @NotNull String coloredText(String text, AsciiArt.@NotNull color color) {
        String ending = "";
        switch (color) {
            case rainbowRand:
                ArrayList<String> rainbowColors = new ArrayList<String>();
                rainbowColors.add(ANSI_RED);
                rainbowColors.add(ANSI_GREEN);
                rainbowColors.add(ANSI_YELLOW);
                rainbowColors.add(ANSI_BLUE);
                rainbowColors.add(ANSI_PURPLE);
                rainbowColors.add(ANSI_CYAN);
                Random rand = new Random();
                char[] t = text.toCharArray();
                String fin = "";
                for(char s : t){
                    int randomElementIndex =  rand.nextInt(rainbowColors.size());
                    ending = rainbowColors.get(randomElementIndex);
                    fin += ending + s ;
                }
                fin += ANSI_RESET;
                return fin;
            case rainbowSeq:
                ArrayList<String> rainbowColor = new ArrayList<String>();
                rainbowColor.add(ANSI_RED);
                rainbowColor.add(ANSI_GREEN);
                rainbowColor.add(ANSI_YELLOW);
                rainbowColor.add(ANSI_BLUE);
                rainbowColor.add(ANSI_PURPLE);
                rainbowColor.add(ANSI_CYAN);
                char[] ar = text.toCharArray();
                String res = "";
                int iter = 0;
                for(char s : ar){
                    ending = rainbowColor.get(iter);
                    iter+=1;
                    if(iter>rainbowColor.size()-1)
                        iter=0;
                    res += ending + s ;
                }
                res += ANSI_RESET;
                return res;
            case red:
                ending = ANSI_RED;
                break;
            case green:
                ending = ANSI_GREEN;
                break;
            case blue:
                ending = ANSI_BLUE;
                break;
            case yellow:
                ending = ANSI_YELLOW;
                break;
            case black:
                ending = ANSI_BLACK;
                break;
            case purple:
                ending = ANSI_PURPLE;
                break;
            case cyan:
                ending = ANSI_CYAN;
                break;
            case white:
                ending = ANSI_WHITE;
                break;
            default:
                ending = ANSI_WHITE;
                break;


        }
        return  ending + text + ANSI_RESET;
    }

	/**
	 * Colori disponibili.
	 */
	public enum color {
        /**
         * Rosso.
         */
        red,
        /**
         * Verde.
         */
        green,
        /**
         * Blu.
         */
        blue,
        /**
         * Nero.
         */
        black,
        /**
         * Giallo.
         */
        yellow,
        /**
         * Porpora.
         */
        purple,
        /**
         * Ciano.
         */
        cyan,
        /**
         * Bianco.
         */
        white,
        /**
         * Multicolor random.
         */
        rainbowRand,
        /**
         * Multicolor.
         */
        rainbowSeq;
    }

    /**
     * Metodo per rallentare la stampa a video delle stringe.
     * @param output la stringa da stampare.
     */
	public static void slowPrint(@NotNull String output) {
		for (int i = 0; i<output.length(); i++) {
			char c = output.charAt(i);
			System.out.print(c);
			try {
				TimeUnit.MILLISECONDS.sleep(30);
			}
			catch (Exception e) {
			}
		}
	}

    /**
     * Metodo per la stampa sotto forma di tabella del ricettario.
     * @param cookbookRecipe il ricettario da visualizzare.
     */
    public static void printCookbook(@NotNull ArrayList<CookbookRecipe> cookbookRecipe) {
        AsciiArt.slowPrint(UsefulStrings.SEE_COOKBOOK_CHOICE);
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Nome", "Porzioni", "Carico di lavoro per porzione", "Ingredienti").setTextAlignment(TextAlignment.CENTER);
        for (CookbookRecipe c : cookbookRecipe) {
            table.addRule();
            table.addRow(Text_To_FormattedText.right(c.getName(), 30), Text_To_FormattedText.right(c.getPortion(), 30),
                    Text_To_FormattedText.right(c.getWorkload(), 30), Text_To_FormattedText.right(c.getIngredientsToString()
                            .replaceAll("\\[|\\]", ""), 30)).setTextAlignment(TextAlignment.LEFT);
        }
        table.addRule();
        System.out.println(table.render());
        System.out.println();
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }

    /**
     * Metodo per la stampa sotto forma di tabella delle bevande.
     * @param drinks le bevande da visualizzare.
     */
    public static void printBeverages(@NotNull ArrayList<Drink> drinks) {
        AsciiArt.slowPrint(UsefulStrings.BEVERAGES);
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Nome", "Consumo pro capite (l)").setTextAlignment(TextAlignment.CENTER);
        for (Drink d : drinks) {
            table.addRule();
            table.addRow((d.getName()), Text_To_FormattedText.right(d.getQuantity(), 30)).setTextAlignment(TextAlignment.LEFT);
        }
        table.addRule();
        System.out.println(table.render());
        System.out.println();
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }

    /**
     * Metodo per la stampa sotto forma di tabella dei generi alimentari extra.
     * @param appetizers i generi alimentari (extra) da visualizzare.
     */
    public static void printAppetizers(@NotNull ArrayList<Appetizer> appetizers) {
        AsciiArt.slowPrint(UsefulStrings.APPETIZERS);
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Nome", "Consumo pro capite (hg)").setTextAlignment(TextAlignment.CENTER);
        for (Appetizer a : appetizers) {
            table.addRule();
            table.addRow((a.getGenre()), Text_To_FormattedText.right(a.getQuantity(), 30)).setTextAlignment(TextAlignment.LEFT);
        }
        table.addRule();
        System.out.println(table.render());
        System.out.println();
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }

    /**
     * Metodo per la stampa sotto forma di tabella della
     * corrispondenza piatto-ricetta.
     *
     * @param manager il gestore, proprietario dei dati.
     * @param dishes i piatti di cui trovare il match con la ricetta.
     */
    public static void printMatch(Manager manager, @NotNull ArrayList<Dish> dishes) {
        AsciiArt.slowPrint(UsefulStrings.MATCH);
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Piatto", "Ricetta").setTextAlignment(TextAlignment.CENTER);
        for (Dish d : dishes) {
            if (manager.dishRecipeMatch(d)) {
                table.addRule();
                table.addRow((d.getName()), Text_To_FormattedText.right(manager.retriveRecipefromDish(d)
                                .replaceAll("\\[|\\]", "").replaceAll("Recipe", "").replaceAll("\\{", "").replaceAll("\\}", ""), 30))
                        .setTextAlignment(TextAlignment.LEFT);
            }
        }
        table.addRule();
        System.out.println(table.render());
        System.out.println();
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }

    /**
     * Metodo per la stampa sotto forma di tabella dei piatti disponibili.
     * @param dishes i piatti di cui stampare la disponibilità.
     */
    public static void printDishAvailability(@NotNull ArrayList<Dish> dishes) {
        AsciiArt.slowPrint(UsefulStrings.DISH_AND_VALIDITY);
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Denominazione piatto", "Periodo di validità").setTextAlignment(TextAlignment.CENTER);
        for (Dish d : dishes) {
            table.addRule();
            table.addRow((d.getName()), Text_To_FormattedText.right(d.getAvailability(), 30)).setTextAlignment(TextAlignment.LEFT);
        }
        table.addRule();
        System.out.println(table.render());
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }

    /**
     * Metodo per la stampa sotto forma di tabella del menù tematico.
     * @param menu il menù da cui recuperare i menù tematici da stampare.
     */
    public static void printThemedMenu(@NotNull ArrayList<Course> menu) {
        AsciiArt.slowPrint(UsefulStrings.THEMED_MENU_PRINT);
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Menu", "Durata", "Piatti").setTextAlignment(TextAlignment.CENTER);
        for (Course c : menu) {
            if (c.getType().equalsIgnoreCase(UsefulStrings.THEMED_COURSE)) {
                table.addRule();
                table.addRow(c.getName(), Text_To_FormattedText.right(c.getValidation(), 30), Text_To_FormattedText.right(c.getDishes()
                        .replaceAll("\\[|\\]", ""), 30)).setTextAlignment(TextAlignment.LEFT);
            } else continue;
        }

        table.addRule();
        System.out.println(table.render());
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
        System.out.println();
    }

    /**
     * Metodo per la stampa sotto forma di tabella del menù tematico.
     * @param menu il menù da cui recuperare il menù alla carta.
     */
    public static void printALaCarteMenu(@NotNull ArrayList<Course> menu) {
        AsciiArt.slowPrint(UsefulStrings.A_LA_CARTE_MENU_PRINT);
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Menu", "Durata", "Piatti").setTextAlignment(TextAlignment.CENTER);
        for (Course c : menu) {
            if (c.getType().equalsIgnoreCase(UsefulStrings.A_LA_CARTE_COURSE)) {
                table.addRule();
                table.addRow(c.getName(), Text_To_FormattedText.right(c.getValidation(), 30), Text_To_FormattedText.right(c.getDishes()
                        .replaceAll("\\[|\\]", ""), 30)).setTextAlignment(TextAlignment.LEFT);
            } else continue;
        }

        table.addRule();
        System.out.println(table.render());
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
        System.out.println();
    }

    /**
     * Metodo per la stampa sotto forma di tabella del menù magazzino.
     * @param articles gli articoli a magazzino.
     */
    public static void printWareHouse(@NotNull ArrayList<Article> articles) {
        AsciiArt.slowPrint(UsefulStrings.WAREHOUSE_STATUS);
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Articolo", "Quantità").setTextAlignment(TextAlignment.CENTER);
        for (Article a : articles) {
            table.addRule();
            table.addRow((a.getName()), Text_To_FormattedText.right(a.getQuantityString() + a.getMeasure(), 30)).setTextAlignment(TextAlignment.LEFT);
        }
        table.addRule();
        System.out.println(table.render());
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }

    /**
     * Stampa degli ingredienti necessari in cucina.
     * @param articles gli articoli necessari.
     */
    public static void printKitchen(@NotNull ArrayList<Article> articles) {
        AsciiArt.slowPrint(UsefulStrings.KITCHEN_STATUS);
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Articolo", "Quantità").setTextAlignment(TextAlignment.CENTER);
        for (Article a : articles) {
            table.addRule();
            table.addRow((a.getName()), Text_To_FormattedText.right(a.getQuantityString() + a.getMeasure(), 30)).setTextAlignment(TextAlignment.LEFT);
        }
        table.addRule();
        System.out.println(table.render());
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }

    /**
     * Metodo per la stampa sotto forma di tabella della lista della spesa.
     * @param articles gli articoli della lista della spesa.
     */
    public static void printShoppingList(@NotNull ArrayList<Article> articles) {
        AsciiArt.slowPrint(UsefulStrings.SHOPPING_LIST);
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Articolo", "Quantità da comprare").setTextAlignment(TextAlignment.CENTER);
        for (Article a : articles) {
            table.addRule();
            table.addRow((a.getName()), Text_To_FormattedText.right(a.getQuantityString() + a.getMeasure(), 30)).setTextAlignment(TextAlignment.LEFT);
        }
        table.addRule();
        System.out.println(table.render());
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }
}
