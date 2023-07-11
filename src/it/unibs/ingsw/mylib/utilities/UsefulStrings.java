package it.unibs.ingsw.mylib.utilities;

/**
 * Classe di utilità, permette di salvare tutte le stringhe utilizzate nel
 * codice (maggiormente in {@code Handler}, poco, o mai, in altre classi),
 * così da avere classi più pulite e ordinate.
 */
public class UsefulStrings {
    public static final String WELCOME = "\n" +
            " ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄        ▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄ \n" +
            "▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░▌      ▐░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌\n" +
            "▐░█▀▀▀▀▀▀▀█░▌ ▀▀▀▀█░█▀▀▀▀ ▐░█▀▀▀▀▀▀▀▀▀  ▀▀▀▀█░█▀▀▀▀ ▐░█▀▀▀▀▀▀▀█░▌▐░█▀▀▀▀▀▀▀█░▌▐░█▀▀▀▀▀▀▀█░▌▐░▌░▌     ▐░▌ ▀▀▀▀█░█▀▀▀▀ ▐░█▀▀▀▀▀▀▀▀▀ \n" +
            "▐░▌       ▐░▌     ▐░▌     ▐░▌               ▐░▌     ▐░▌       ▐░▌▐░▌       ▐░▌▐░▌       ▐░▌▐░▌▐░▌    ▐░▌     ▐░▌     ▐░▌          \n" +
            "▐░█▄▄▄▄▄▄▄█░▌     ▐░▌     ▐░█▄▄▄▄▄▄▄▄▄      ▐░▌     ▐░▌       ▐░▌▐░█▄▄▄▄▄▄▄█░▌▐░█▄▄▄▄▄▄▄█░▌▐░▌ ▐░▌   ▐░▌     ▐░▌     ▐░█▄▄▄▄▄▄▄▄▄ \n" +
            "▐░░░░░░░░░░░▌     ▐░▌     ▐░░░░░░░░░░░▌     ▐░▌     ▐░▌       ▐░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░▌  ▐░▌  ▐░▌     ▐░▌     ▐░░░░░░░░░░░▌\n" +
            "▐░█▀▀▀▀█░█▀▀      ▐░▌      ▀▀▀▀▀▀▀▀▀█░▌     ▐░▌     ▐░▌       ▐░▌▐░█▀▀▀▀█░█▀▀ ▐░█▀▀▀▀▀▀▀█░▌▐░▌   ▐░▌ ▐░▌     ▐░▌     ▐░█▀▀▀▀▀▀▀▀▀ \n" +
            "▐░▌     ▐░▌       ▐░▌               ▐░▌     ▐░▌     ▐░▌       ▐░▌▐░▌     ▐░▌  ▐░▌       ▐░▌▐░▌    ▐░▌▐░▌     ▐░▌     ▐░▌          \n" +
            "▐░▌      ▐░▌  ▄▄▄▄█░█▄▄▄▄  ▄▄▄▄▄▄▄▄▄█░▌     ▐░▌     ▐░█▄▄▄▄▄▄▄█░▌▐░▌      ▐░▌ ▐░▌       ▐░▌▐░▌     ▐░▐░▌     ▐░▌     ▐░█▄▄▄▄▄▄▄▄▄ \n" +
            "▐░▌       ▐░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌     ▐░▌     ▐░░░░░░░░░░░▌▐░▌       ▐░▌▐░▌       ▐░▌▐░▌      ▐░░▌     ▐░▌     ▐░░░░░░░░░░░▌\n" +
            " ▀         ▀  ▀▀▀▀▀▀▀▀▀▀▀  ▀▀▀▀▀▀▀▀▀▀▀       ▀       ▀▀▀▀▀▀▀▀▀▀▀  ▀         ▀  ▀         ▀  ▀        ▀▀       ▀       ▀▀▀▀▀▀▀▀▀▀▀ \n" +
            "                                                                                                                                  ";

    public static final String DAY = "\n" +
            " ____ _ ____ ____ __ _ ____ ___ ____   _    ____ _  _ ____ ____ ____ ___ _ _  _ ____   ___  ____ _   \n" +
            " |__, | [__] |--< | \\| |--|  |  |--|   |___ |--|  \\/  [__] |--< |--|  |  |  \\/  |--|   |__> |=== |___";

    public static final String GOODBYE = "Arrivederci. Grazie per aver utilizzato Ristorante.\nSperiamo che la sua esperienza con noi sia stata piacevole e di rivederLa presto.";

    public static final String FIRST_FIRST_MENU_OPTION = "Accesso al Ristorante con autenticazione.";
    public static final String END_MENU_OPTION = "Uscire dal programma.";

    public static final String SECOND_FIRST_MENU_OPTION = "Scoprire chi ha scritto il programma.";

    public static final String MAIN_TASK_REQUEST = "Si scelga cosa fare";
    public static final String ERROR_MESSAGE = "An error occured. :(";
    public static final String SEE_RESTAURANT_STATUS = "Visualizza i dati di configurazione relativi al ristorante.";
    public static final String SEE_RESERVATIONS_STATUS = "Visualizza l'agenda del ristorante.";
    public static final String INITIALISE_RESTAURANT_STATUS = "Inizializza i dati di configurazione relativi al ristorante.";

    public static final String BACK_MENU_OPTION = "Handler principale.";
    public static final String BACK_MENU_OPTION2 = "Torna al menu precedente.";

    public static final String RECIPE_MENU = "Benvenuto, si sta per interfacciare con l'interfaccia di inserimento di una nuova ricetta.\n" +
            "Durante tutto l'inserimento, le chiederemo di inserire infromazioni specifiche per la ricetta in questione.\n" +
            "Iniziamo.";
    public static final String RECIPE_REMOVER = "Benvenuto, si sta per interfacciare con l'interfaccia di rimozione di una ricetta.\n" +
            "Le chiederemo di inserire infromazioni specifiche per la ricetta in questione, sulla base delle informazioni in nostro possesso.\n" +
            "Iniziamo.";
    public static final String COURSE_MENU = "Benvenuto, si sta per interfacciare con l'interfaccia di inserimento di un nuovo menù tematico.\n" +
            "Durante tutto l'inserimento, le chiederemo di inserire infromazioni specifiche per il menù tematico in questione.\n" +
            "Iniziamo.";
    public static final String COURSE_REMOVER = "Benvenuto, si sta per interfacciare con l'interfaccia di rimozione di un menù tematico.\n" +
            "Le chiederemo di inserire infromazioni specifiche per il menù tematico in questione, sulla base delle informazioni in nostro possesso.\n" +
            "Iniziamo.";
    public static final String APPETIZER_MENU = "Benvenuto, si sta per interfacciare con l'interfaccia di inserimento di un nuovo genere alimentare extra.\n" +
            "Durante tutto l'inserimento, le chiederemo di inserire infromazioni specifiche per il genere alimentare extra in questione.\n" +
            "Iniziamo.";
    public static final String APPETIZER_REMOVER = "Benvenuto, si sta per interfacciare con l'interfaccia di rimozione di un genere alimentare extra.\n" +
            "Le chiederemo di inserire infromazioni specifiche per il genere alimentare extra in questione, sulla base delle informazioni in nostro possesso.\n" +
            "Iniziamo.";
    public static final String DRINK_MENU = "Benvenuto, si sta per interfacciare con l'interfaccia di inserimento di una nuova bevanda.\n" +
            "Durante tutto l'inserimento, le chiederemo di inserire infromazioni specifiche per la bevanda in questione.\n" +
            "Iniziamo.";
    public static final String DRINK_REMOVER = "Benvenuto, si sta per interfacciare con l'interfaccia di rimozione di una bevanda.\n" +
            "Le chiederemo di inserire infromazioni specifiche per la bevanda in questione, sulla base delle informazioni in nostro possesso.\n" +
            "Iniziamo.";

    public static final String ACCESS_DENIED = "Accesso negato.\nLa reindirizziamo al menu principale.\n\n";
    public static final String ACCESS_DENIED2 = "Attenzione! Sta cercando di inizializzare il ristorante per un giorno festivo. Il ristorante non\n" +
            "è operativo durante i giorni festivi, non possiamo permetterle di proseguire. La reindirizziamo al menù principale.\n\n";
    public static final String ACCESS_DENIED3 = "Attenzione! Sta cercando di inizializzare il ristorante in un giorno festivo. Il ristorante non\n" +
            "è operativo durante i giorni festivi, non possiamo permetterle di proseguire. La reindirizziamo al menù principale.\n\n";
    public static final String ACCESS_DENIED4 = "\nAttenzione! Sta cercando di raccogliere le prenotazioni quando ancora il ristorante non è stato inizializzato.\n" +
            "Non possiamo farla procedere. La reindirizziamo al menu principale.\n\n";
    public static final String ACCESS_DENIED5 = "\nAttenzione! Sta cercando di accedere alla gestione magazzino quando ancora non è terminata la raccolta delle prenotazioni.\n" +
            "Non possiamo farla procedere. La reindirizziamo al menu principale.\n\n";

    public static final String WELCOME_USER = "\n\nUtente riconosciuto. Benvenuto ";

    public static final String AUTHOR = """        
             Autori:\n\uD83C\uDF7D\uFE0F Matteo Campanella - 731123.
            \uD83C\uDF7D\uFE0F Martina Contestabile — 731044.
            \uD83C\uDF7D\uFE0F Antonello Constantin Racsan - 731057.
             """;

    public static final String COURSE_OUTER_TAG = "courses";
    public static final String APPETIZERS_OUTER_TAG = "starters";
    public static final String RECIPES_OUTER_TAG = "recipes";
    public static final String DRINKS_OUTER_TAG = "drinks";
    public static final String DISHES_OUTER_TAG = "dishes";
    public static final String WORKLOAD_OUTER_TAG = "today_workload";

    public static final String ENTER_TO_CONTINUE = "\nSi prema invio per continuare...";

    public static final String COURSES_FILE = "src/it/unibs/ingsw/entrees/resturant_courses/courses.xml";
    public static final String COOKBOOK_FILE = "src/it/unibs/ingsw/entrees/cookbook/cookbook.xml";

    public static final String USERS_FILE = "src/it/unibs/ingsw/users/registered_users/UsersAllowed.xml";
    public static final String DRINKS_FILE = "src/it/unibs/ingsw/entrees/drinks/drinks.xml";
    public static final String AGENDA_FILE = "src/it/unibs/ingsw/users/reservations_agent/agenda.xml";
    public static final String APPETIZERS_FILE = "src/it/unibs/ingsw/entrees/appetizers/appetizers.xml";
    public static final String DISHES_FILE = "src/it/unibs/ingsw/entrees/resturant_courses/dishes.xml";
    public static final String WAREHOUSE_FILE = "src/it/unibs/ingsw/users/warehouse_worker/warehouse.xml";
    public static final String WORKLOADS_FILE = "src/it/unibs/ingsw/entrees/resturant_courses/workloads.xml";
    public static final String REMOVE_RECIPE = "Rimuovere una ricetta.";
    public static final String ADD_RECIPE = "Aggiungere una ricetta.";

    public static final String REMOVE_DRINK = "Rimuovere una bevanda.";
    public static final String ADD_DRINK = "Aggiungere una bevanda.";

    public static final String REMOVE_APPETIZER = "Rimuovere un genere alimentare extra.";
    public static final String ADD_APPETIZER = "Aggiungere un genere alimentare extra.";

    public static final String REMOVE_COURSE = "Rimuovere un menù.";
    public static final String ADD_COURSE = "Aggiungere un menù.";

    public static final String RESTAURANT_NOT_INITIALIZED = "Attenzione! Poiché ha appena avviato il programma, è pregato di\nsettare le informazioni relative al ristorante, selezionando il menù 1.\n\n";
    public static final String RESTAURANT_STATUS = "Si è scelta l'opzione «visualizza lo stato del ristorante»,\nquindi le forniamo tutte le informazioni a riguardo.\nIniziamo.\n\n";
    public static final String UPDATE_AGENDA = "Gentile utente, ha scelto l'opzione «aggiorna agenda».\nStiamo per permetterLe di interagire con il sistema che si occupa di ciò. Iniziamo.\n\n";
    public static final String SHOPPING_LIST = "Ecco la lista della spesa aggiornata alle prenotazioni attuali.\n\n";
    public static final String BEVERAGES = "\n\n\nInsieme delle bevande\n\n";
    public static final String APPETIZERS = "\n\n\nInsieme dei generi alimentari extra\n\n";
    public static final String MATCH = "\n\n\nCorrispondenze piatto-ricetta\n\n";
    public static final String DISH_AND_VALIDITY = "\n\n\nDenominazione e periodo di validità di ciascun piatto\n\n";
    public static final String WAREHOUSE_STATUS = "\n\n\nVisualizzazione del magazzino\n\n";
    public static final String KITCHEN_STATUS = "\n\n\nAttualmente in uso (in cucina o ai tavoli)\n\n";
    public static final String COOKBOOK = "\n\n\nRicettario attualmente in uso\n\n";
    public static final String A_LA_CARTE_MENU_PRINT = "\n\n\nMenu alla carta\n\n";
    public static final String THEMED_MENU_PRINT = "\nMenu tematici\n\n";
    public static final String WORKLOAD_PER_PERSON = "Carico di lavoro per persona ";
    public static final String COVERED = "Numero di posti a sedere disponibili nel ristorante ";
    public static final String SET_COVERED = "\n\n\nSi imposti ora il numero di posti a sedere disponibili nel ristorante:\n";

    public static final String USERS_SIGNIN = "Gentile utente, per proseguire inserire le credenziali:\n";
    public static final String RESTAURANT_SETUP = "Gentile utente, ha scelto l'opzione «inizializza ristorante».\nStiamo per permetterLe di interagire con il sistema che si occupa di ciò. Iniziamo.\n\n";
    public static final String RESTAURANT_SETUP_NOT_ALLOWED = "Gentile utente, ha scelto l'opzione «inizializza ristorante».\nTuttavia, non possiamo consentirLe di proseguire, perché ha già scelto\n" +
            "quest'opzione per la corrente giornata lavorativa. La reindirizziamo al menù precedente.\n\n";
    public static final String RESTAURANT_RETRIVE_INFOS = "Recupero le informazioni riguardanti il ristorante, quali menu,\nricettario, generi alimentari extra e drink validi in precedenza.\n" +
            "La avvisiamo che stiamo per fare un controllo sui piatti disponibili in preceddenza,\n" +
            "se il nostro sistema rileverà che un determinato piatto nei menù non è compatibile col\n" +
            "giorno lavorativo successivo, lo elimineremo automaticamente.\n\n";
    public static final String RESTAURANT_RETRIVE_INFOS_COMPLETED = "Il recupero delle informazioni è avvenuto con successo.";
    public static final String CHANGE_SOMETHING = "\nPer caso, vuole modificare qualcosa? Risponda con";
    public static final String CHANGE_WORKLOAD_PER_PERSON = "\nPer caso, vuole modificare il carico di lavoro per persona? Risponda con";
    public static final String CHANGE_COVERED = "\nPer caso, vuole modificare il numero di coperti? Risponda con";
    public static final String RECIPE_REMOVER_WARNING = "\nAttenzione! Sta cercando di rimuovere una ricetta che si trova in un menù\n" +
            "tematico e/o alla carta, ciò implica che, se rimuove la ricetta, va rimossa anche\n" +
            "dal/dai menù in questione. Vuole procedere con la rimozione? Risponda con";
    public static final String CHANGE_WORKLOAD_COVERED = "\nPer caso, vuole modificare il numero di coperti? Risponda con";
    public static final String CHANGE_COOKBOOK = "Modificare le ricette.";
    public static final String CHANGE_COURSES = "Mdificare il menu. ";
    public static final String INSERT_IN_A_LA_CARTE_MENU_OPTION = "Inserire un piatto.";
    public static final String REMOVE_IN_A_LA_CARTE_MENU_OPTION = "Rimuovere un piatto.";
    public static final String CHANGE_APPETIZERS = "Modificare i generi alimentari extra.";
    public static final String CHANGE_DRINKS = "Modificare le bevande.";
    public static final String SEE_MENU_CHOICE = "Menù attualmente disponibili ai clienti.\n\n";
    public static final String SEE_COOKBOOK_CHOICE = "Ricettario attualmente in uso in Ristorante.\n\n";
    public static final String COOKBOOK_UPDATED= "Il ricettario è stato correttamente aggiornato.\n\n";
    public static final String COURSES_UPDATED= "Il menù è stato correttamente aggiornato.\n\n";
    public static final String DRINKS_UPDATED= "I drink a disposizione dei clienti sono stati correttamente aggiornati.\n\n";
    public static final String APPETIZERS_UPDATED= "L'insieme dei generi extra è stato correttamente aggiornato.\n\n";

    public static final String WAREHOUSE_WORKER = "MAGAZZINIERE";
    public static final String RESERVATIONS_AGENTI = "ADDETTO ALLE VENDITE";
    public static final String MANAGER = "GESTORE";

    public static final String RECIPE_NAME = "Inserire il nome della ricetta\n:» ";
    public static final String RECIPE_VALIDITY = "Inserire la validità della ricetta, cioè quando questa è usabile nel ristorante.\n" +
            "Il formato può essere «tutto l'anno», gg-mm-aaaa, un giorno della settimana oppure una stagione.\n:» ";
    public static final String COURSE_VALIDITY = "Inserire la validità del menù, cioè quando questa è usabile nel ristorante.\n" +
            "Il formato può essere gg-mm-aaaa, un giorno della settimana oppure una stagione.\n:» ";
    public static final String RECIPE_INGREDIENTS_NUMBER = "È il momento di scrivere gli ingredienti della ricetta.\nPer prima cosa, quanti sono? ";
    public static final String RECIPE_INGREDIENTS_NAME = "\nNome dell'ingrediente » ";
    public static final String RECIPE_INGREDIENTS_WHEIGHT = "\nQuantità dell'ingrediente (solo valore numerico)  » ";
    public static final String RECIPE_INGREDIENTS_UNIT = "\nUnità di misura dell'ingrediente (g oppure premere invio)  » ";
    public static final String RECIPE_PORTIONS = "Inserire le porzioni (valore intero)\n» ";
    public static final String WORKLOAD_PER_PORTION = "Inserire il carico di lavoro per porzione.\nIl carico di lavoro per porzione è una frazione, minore dell’unità, del carico di lavoro per persona.\n";
    public static final String NUMERATOR = "Inserire il numeratore della frazione» ";
    public static final String DENOMINATOR = "Inserire il denominatore della frazione» ";
    public static final String WORKLOAD_PER_PERSON_SETUP = "\n\nSi imposti il carico di lavoro per persona.\n" +
            "Tale valore è un intero che rappresenta (in forma normalizzata) l’impegno richiesto per preparare\nil cibo tipicamente consumato da una singola persona in un singolo pasto.\n";
    public static final String INSERT = "\nInserire il valore » ";
    public static final String WORKLOAD_RESTAURANT  = "Il carico di lavoro del ristorante è pari a ";

    public static final String INSERT_IN_A_LA_CARTE_MENU = "Come ha avuto modo di vedere, i menu tematici al momento disponibili sono formati da piatti che\n" +
            "sono presenti anche nel menu alla carta. Questa corrispondenza è essenziale per poter formare\n" +
            "un menù tematico. Le chiediamo, quindi, se per caso vuole aggiungere/togliere al menù alla carta qualche piatto\n" +
            "che è presente nel ricettario, ma non nel menù stesso, in modo tale da ampliare le opzioni ai clienti.\n" +
            "Risponda con";
    public static final String AVAILABLE_DISHES = "Piatti disponibili";
    public static final String DISH_NAME = "Inserire il nome del piatto\n:» ";
    public static final String COURSE_NAME = "Inserire il nome del menù tematico\n:» ";
    public static final String THEMED_COURSE = "menu tematico";
    public static final String A_LA_CARTE_COURSE = "menu alla carta";
    public static final String COURSE_VALIDATION = "Inserire il periodo di validità del menu tematico.\n" +
            "\nSi richiede che, se è un giorno specifico, il formato sia AAAA-MM-GG, altrimenti un giorno della settimana come «domenica».\n:» ";
    public static final String RECIPE_VALIDATION = "Inserire il periodo di validità del piatto a cui la ricetta corrisponde.\n" +
            "\nSi richiede che, se è un giorno specifico, il formato sia AAAA-MM-GG, altrimenti un giorno della settimana come «domenica».\n:» ";
    public static final String COURSES_DISHES_NUMBER = "È il momento di scrivere i piatti del menù tematico.\nPer prima cosa, quanti sono? ";
    public static final String COURSES_DISHES = "Inserire il nome di un piatto (nel caso in cui non esista/sia di un periodo non valido per il menù, l'input non verrà considerato)\n» ";
    public static final String INCORRECT_COURSE_WORKLOAD = "Non è possibile accettare questo menu tematico: il carico di lavoro del menu è\nsuperiore a quello per persona, in quanto ";
    public static final String APPETIZER_NAME = "Inserire il nome del genere alimentare extra \n:» ";
    public static final String APPETIZER_CONSUMPTION = "Inserire il consumo pro capite (in hg)\n:» ";
    public static final String DRINK_NAME = "Inserire il nome della bevanda\n:» ";
    public static final String UPDATE_AGENDA_MENU_VOICE = "Aggiorna agenda.";
    public static final String SAVE_IN_RES_ARCHIVE_MENU_VOICE = "Salva in archivio prenotazioni.";
    public static final String NO_MORE_RESERVATION_MESSAGE = "Non sono più ammesse prenotazioni!";
    public static final String RESERVATION_NAME = "Inserire il nome della prenotazione\n:» ";
    public static final String MENU_DISH_NAME = "Inserire il nome del menu/piatto\n:» ";
    public static final String MENU_DISH_COVER = "Inserire il numero di coperti per il menu/piatto\n:» ";
    public static final String ONE_MENU_PER_PERSON = "Un menu tematico per persona!";
    public static final String QUE_ADD_ANOTHER_RESERVATION = "Aggiungere un'altra prenotazione? ";
    public static final String QUE_SAVE_IN_RES_ARCHIVE = "Desidera salvare le prenotazioni attuali nell'archivio delle prenotazioni? ";
    public static final String MORE_ITEMS = "Aggiungere ulteriori items? ";
    public static final String NO_MORE_RES_COVER = "Capienza massima del ristorante raggiunta. Non sono più ammesse prenotazioni.\n\n";
    public static final String NO_MORE_RES_WORKLOAD = "Carico di lavoro massimo del ristorante raggiunto. Non sono più ammesse prenotazioni.\n\n";
    public static final String WORKLOAD_EXCEEDED_AVAILABLE = "Così si supera il carico di lavoro del ristorante!\nCarico rimanente: ";
    public static final String COVER_EXCEEDED_AVAILABLE = "Così si supera la capienza massima!\nPosti rimanenti: ";
    public static final String ACTUAL_COVER_MESSAGE = "\nCoperti attuali raggiunti: ";
    public static final String ACTUAL_COVER_AVAILABLE_MESSAGE = "Coperti attuali disponibili: ";
    public static final String ACTUAL_WORKLOAD_MESSAGE = "\nCarico di lavoro raggiunto: ";
    public static final String ACTUAL_WORKLOAD_AVAILABLE_MESSAGE = "Carico di lavoro rimanente: ";
    public static final String DRINKS_CONSUMPTION = "Inserire il consumo pro capite (in cl)\n:» ";
    public static final String RES_COVER = "Inserire il numero di coperti\n:» ";
    public static final String AGENDA_OUTER_TAG = "agenda";
    public static final String XML_FILE_EXTENSION = ".xml";
    public static final String DAILY_MENU_NAME = "menu del giorno";
    public static final String LOCATION_RES_ARCHIVE = "src/it/unibs/ingsw/users/reservations_agent/archivio_prenotazioni/prenotazioni_";
    public static final String INVALID_MENU_DISH = "Il menu/piatto inserito non è disponibile, la preghiamo di inserire un nome valido.";
    public static final String INVALID_DAILY_MENU = "La singola voce 'menu del giorno' non è ordinabile! Tutti i piatti del giorno ovviamente sì! La preghiamo quindi di inserire un nome valido.";
    public static final String OK_FILE_SAVED_MESSAGE = "File salvato correttamente.";
    public static final String ELEMENT_ALREADY_IN = "Elemento già presente!";
}
