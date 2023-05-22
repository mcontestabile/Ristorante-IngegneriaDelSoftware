# Ristorante

Ristorante è un sistema software di supporto alla raccolta delle prenotazioni e alla generazione dell’elenco giornaliero dei prodotti commestibili da acquistare da parte di un ristorante che lavora solo su prenotazione, la quale deve essere pervenuta con almeno un giorno feriale di anticipo, unicamente nei giorni feriali, servendo un solo pasto al giorno. Ogni prenotazione è comprensiva dell’indicazione di tutte le pietanze ordinate.
La filosofia del ristorante, e quella adottata dai progettisti, è quella di svolgere tutte le operazioni lavorative propedeutiche al giorno successivo.
Quindi, i tre operatori svolgeranno faccende il giorno _x_ per il giorno (lavorativo) seguente. Difatti come _working day_ si intende sempre la giornata lavorativa successiva. L'applicazione inoltre, non potrà essere eseguita durante un giorno festivo, informando l'utente con un relativo messaggio.

Il programma si avvia con la seguente schermata, con tanto di messaggio circa il giorno lavorativo per cui si sta inizializzando il ristorante.  

<img width="875" alt="Untitled" src="https://user-images.githubusercontent.com/76061932/227925653-4480310a-ba6a-4d52-9208-2374e33dd91f.png">



Gli utenti che possono interagire con il ristorante sono tre, ognuno avente mansioni differenti. Gli utenti sono gestore, addetto alle prenotazioni e magazziniere.

## Gestore

È uno dei tre utenti del programma. Una volta immesse le credenziali, si trova davanti questo menu

<img width="445" alt="Untitled" src="https://user-images.githubusercontent.com/76061932/227929397-36f60929-0343-4af0-a231-b1ac811c04c7.png">



Se sceglie il menu 2 al primo avvio della giornata, non visualizzerà nulla e riceverà a video un messaggio che gli indica di scegliere il menu 1.

### Inizializzazione dei dati di configurazione relativi al ristorante
Per quanto riguarda «Inizializza i dati di configurazione relativi al ristorante.», nello specifico si occupa di inizializzare i valori di:

1. Carico di lavoro per persona.
2. Numero di posti a sedere disponibili nel ristorante.
3. Insieme delle bevande.
4. Insieme dei generi (alimentari) extra.
5. Consumo pro-capite di bevande.
6. Consumo pro-capite di generi (alimentari) extra.
7. Corrispondenze piatto-ricetta.
8. Denominazione e periodo di validità di ciascun piatto.
9. Ricette e menù tematici.

Il ristorante è un sistema automatizzato che recupera le informazioni settate nella sessione precedente. Nel caso in cui il gestore si ritrovi memorizzate informazioni che cozzano con la giornata lavorativa successiva, come un menù tematico di validità invernale ad inizio primavera oppure un piatto in un menu tematico e/o alla carta, viene automaticamente eliminata quell’informazione.
Facciamo un esempio dove il sistema deve verificare autonomamente che i piatti presenti in precedenza nel menù siano validi nel giorno lavorativo successivo. Se ieri era ancora inverno ed è disponibile nel menù la ricetta di «vitello tonnato», che ha periodo di validità «inverno», il vitello tonnato è stata inserito in una data invernale, il che è corretto. Tuttavia, oggi è il primo giorno di primavera e non deve essere assolutamente presente «vitello tonnato», perché la data di validità è altamente scorretta, non corrisponde alla stagione odierna! Di conseguenza, «vitello tonnato» va eliminato sia dal menù alla carta che dall'eventuale/eventuali menù alla carta in cui è stato inserito. Inoltre, si effettueranno controlli per verificare che non ci siano menù tematici, con validità «inverno», in quanto la sua validità è stata settata a suo tempo quando era inverno, situazione che non è più valida. Questo tipo di ragionamento si fa con le stagioni, i giorni della settimana e le date.  
Una volta effettuato il controllo, vengono stampate a video le informazioni recuperate e viene chiesto se si vuole modificare qualcosa, circa qualsiasi elemento facente parte dell'esperienza culinaria del ristorante — vengono visualizzati ricettario, menù, generi alimentari (extra) e bevande recuperati per poter decidere oggettivamente se qualcosa non va bene. Un esempio di come viene mostrato a video è il seguente

<img width="735" alt="Untitled" src="https://user-images.githubusercontent.com/76061932/227929809-8e5fd949-d861-4058-8d4b-2eb9ae9c06c5.png">



Se l’utente vuole modificare qualcosa, mostra il suo assenso inserendo ‘s’, altrimenti ‘n’. In caso di necessità di modificare, viene visualizzato il seguente menu a due scelte.

<img width="567" alt="Untitled" src="https://user-images.githubusercontent.com/76061932/227930704-0596aac2-87d5-439f-bf31-a63d802f06c2.png">



Per quanto riguarda ricette, generi alimentari extra e bevande, le modifiche sono semplici, si inserisce o rimuove qualcosa e non ci sono limitazioni. Quando si aggiunge, è richiesto l'inserimento di un nome per il genere/bevanda, chiaramente un nome non presente fra quelli attualmente esistenti, e il consumo pro capite, espresso in litri. Quando si vuole rimuovere, è necessario inserire un nome presente fra quelli inseriti in precedenza e tuttora validi. Se il nome inserito non è valido, sia durante l'inserimento che durante la rimozione, viene presentato un messaggio di errore in cui è richiesto un nome corretto.

Per quanto riguarda il menù, invece, le scelte sono da ponderare. Infatti, i menu modificabili sono solo quelli tematici. Per quanto riguarda il menu alla carta, si potrà togliere/aggiungere piatti e quest’opzione si presenta nel caso in cui si sceglie di aggiungere un nuovo menu tematico, in quanto i menu tematici sono dei sotto-insiemi del menu alla carta, deve esserci una corrispondenza dei piatti, per cui

Piatto nel menù alla carta $\Rightarrow$ È nel menù tematico di nome "A"     
Piatto nel menù alla carta $\nLeftarrow$ È nel menù tematico di nome "A"    

Quindi, verrà effettuato il controllo, ad inserimento dei piatti nel menu tematico, che il piatto sia effettivamente presente nel menu alla carta, e non solo nei piatti disponibili e ricettario.  

Infine, se si vuole aggiungere una ricetta e, di conseguenza, un nuovo piatto a disposizione del ristorante, dato che la corrispondenza che questo programma associa è di tipo nome, richiede un'interazione con l'utente per cui si inserisce il nome del piatto, ovviamente con un nome non esistente nel ricettario, e il numero di ingredienti che tale ricetta ha, che poi vanno elencati, il periodo di valdità in un formato valido (potrebbe essere un giorno della settimana, una stagione, "tutto l'anno" oppure una data specifica), il numero di porzioni che tale ricetta produce e il carico di lavoro (come numeratore e denominatore) che questa ricetta comporta, in forma frazionaria. Una volta inserito il tutto, il sistema crea la corrispondenza piatto-ricetta, che si fa sulla base del nome associato alla ricetta. Il piatto che si va a creare, quindi, sarà disponibile all'inserimento fra i piatti del giorno, in quanto se esiste una ricetta, deve esistere il piatto.  

Proseguendo con le altre informazioni del Ristorante, è il momento dell'inserimenro del carico di lavoro per persona, che è un intero che rappresenta (in forma normalizzata) l’impegno richiesto per preparare il cibo tipicamente consumato da una singola persona in un singolo pasto. Il carico di lavoro per persona è fondamentale per comprendere quanto lavoro il ristorante può svolgere nel giorno lavorativo.  

Poi, si arriva a dover stabilire il numero di posti a sedere disponibili nel ristorante, cioè i coperti disponibili per quel giorno lavorativo.  

Per concludere con l'inizializzazione, sulla base dei coperti e del carico di lavoro per persona, si può calcolare il carico di lavoro sostenibile dal ristorante, che corrisponde al numero dei coperti moltiplicato per il carico di lavoro per persona, il tutto accrescuto del 20%. Questo valore serve per stabilire quante prenotazioni accettare.  

In tutte le interazioni con la linea di comando, l'immissione dei dati è smart: nel caso in cui il valore immesso non sia nel formato corretto, non ci sia corrispondenza nei periodi di validità o, nel caso di nomi di nuovi menù, bevande..., siano già presenti, il sistema segnala l'incongruenza e permette un nuovo inserimento, il tutto finché non viene immesso un dato valido.  

### Visualizzazione dei dati di configurazione relativi al ristorante
Una volta che si è stabilito di aver terminato, l’utente rivisualizza l’opzione menu 1 e 2. Il menu 1 non è più selezionabile, in quanto l’inizializzazione del ristorante è fattibile una sola volta al giorno (lavorativo) per il lavorativo successivo. Di conseguenza, l’unica opzione è «Visualizza i dati di configurazione relativi al ristorante.», che si occupa di effettuare la stampa a video di:

1. Carico di lavoro per persona.
2. Numero di posti a sedere disponibili nel ristorante.
3. Insieme delle bevande.
4. Insieme dei generi (alimentari) extra.
5. Consumo pro-capite di bevande.
6. Consumo pro-capite di generi (alimentari) extra.
7. Corrispondenze piatto-ricetta.
8. Denominazione e periodo di validità di ciascun piatto.
9. Ricette e menù tematici.

## Addetto alle prenotazioni

È uno dei tre utenti del programma. Una volta immesse le credenziali, si trova davanti questo menu
![image](https://user-images.githubusercontent.com/48158712/229156203-d3b81dd6-76c9-4413-ac73-27a97f636de6.png)

Selezionando la voce [1], l'addetto potrà procedere con l'inserimento di una Prenotazione.\
Dopo la visualizzazione dei menu e piatti disponibili, presi dal file relativo, fornito dal manager, il sistema richiederà:
1. Nome prenotazione.
2. Cover (coperti) prenotazione.

Se tutti i dati sono corretti, si hanno coperti a sufficienza e in mancanza di ripetizioni per quanto riguardano i nomi delle prenotazioni, si proseguirà con l'inserimento dei menù/piatti (item) relativi alla prenotazione:
1. Nome del menù/piatto.
2. Cover (coperti) dell'item in questione.

Se tutti i dati sono corretti, non si eccede il carico di lavoro sostenibile dal ristorante, e si sono scelti solamente item disponibili per la giornata lavorativa,
la prenotazione verrà aggiunta nel file relativo alle prenotazioni. Verrà chiesto poi all'utente se desidera salvare le informazioni nell'archivio delle prenotazioni per visualizzazioni future. Quest'ultima operazione può essere eseguita con la voce [2] del menù.

### _Note relative all'inserimento di una prenotazione_
- Se il ristorante ha raggiunto il numero di coperti massimo e/o ha raggiunto il carico di lavoro sostenibile del ristorante, l'aggiunta di ulteriori prenotazioni non sarà permessa.
- Se si avvia l'applicazione durante un giorno non lavorativo, non si potrà interagire con il menù e quindi non si potranno effettuare prenotazioni, in quanto si dà per assunto che il ristorante resti chiuso durante i giorni festivi.
- In fase di inserimento del nome della prenotazione:
  - il sistema chiederà il nome fintantoché quest'ultimo non esisterà nell'elenco delle prenotazioni della giornata. _(Non ci potranno essere due prenotazioni con lo stesso nome)_.
- In fase di inserimento del cover (numero coperti) della prenotazione:
  - il sistema chiederà un input strettamente numerico, maggiore di 0, fintantoché quest'ultimo non sia un valore che, sommato ai coperti raggiunti fino a quel momento, non superi la capienza totale del ristorante. _(Non si potrà accettare una prenotazione di 0 coperti, ma nemmeno una di un valore che superi la capienza disponibile)_.
- In fase di inserimento del menù/piatto (item) della prenotazione:
  - il sistema chiederà il nome di un menù o di un piatto fintantoché quest'ultimo sarà valido, e quindi presente nel file sopramenzionato per quanto riguarda le entrate disponibili per la giornata relativa alla prenotazione.
- In fase di inserimento del cover dell'item in questione:
  - Il sistema chiederà un input numerico, maggiore di 0, fintantoché la somma del carico di lavoro dell'item (estesa alle relative cover) non ecceda il carico di lavoro sostenibile del ristorante. _(Ovviamente se un numero eccessivo di persone chiede un numero eccessivo di piatti, si supererà il carico sostenibile, pertanto il sistema non accetterà valori spropositati, oltre che surreali)_.
  - Inoltre, se si tratta di un menù, il sistema chiederà un input numerico, maggiore di 0, fintantoché quest'ultimo (presi in considerazione anche i valori per i menù precedentemente inseriti nella relativa prenotazione) non superi il numero coperti della prenotazione stessa. _(Si potrà quindi avere al più un menù tematico a testa, ritenuto come lo scenario più tipico)_.
- L'applicazione, a seconda del numero coperti della prenotazione, continuerà a chidere items fino al raggiungimento di tale valore. Una volta raggiunto il valore, il sistema chiederà all'utente se desidera inserire ulteriori items, il tutto governato dalle linee guida sopramenzionate.
- Una volta completato l'inserimento, il sistema chiederà all'utente se vuole aggiungere un'ulteriore prenotazione (se invece il ristorante sarà pieno, uscirà dal menù impedendo ulteriori aggiunte).
- Prima di uscire dalla fase di aggiunta, il sistema chiederà all'utente se desidera salvare l'attuale elenco, nell'archivio delle prenotazioni, un file che avrà nel nome la data lavorativa relativa alla prenotazione. Sarà possibile eseguire questa operazione anche in un secondo momento, mediante la relativa voce del menu; i progettisti hanno comunque considerato come opportuna questa soluzione in previsione di eventuali sviste dal parte del personale.

## Magazziniere
![Screenshot 2023-04-02 alle 17 07 51](https://user-images.githubusercontent.com/77293743/229361639-c7862ec6-983e-4f7a-a74c-76146f2aba68.png)

### Visualizzazione degli ingredienti disponibili in magazzino
Selezionando la voce [1], si accede alla visualizzazione degli ingredienti disponibili in magazzino, con le corrispondenti quantità. Inoltre, vengono visualizzati anche quelli attualmente in uso. 

### Vsualizzazione della lista della spesa per la giornata successiva
Selezionando la voce [2], si accede alla visualizzazione della lista della spesa per la giornata successiva. Quest'ultima è creata in base alle prenotazioni effettuate fino a tale momento. Il sistema tiene conto delle occorrenze già presenti in magazzino, le quali su di esse vengono calcolate le quantità degli ingredienti necessari per soddisfare le prenotazioni.
Dopo la visualizzazione appare un menù di scelta, che in caso di esito positivo, permette di comprare gli ingredienti presenti nella lista della spesa e quindi aggiungerli nel magazzino. Altrimenti torna al menù principale.

### Utilizzare un ingrediente e prelevarlo dal magazzino
Selezionando la voce [3], si sceglie di utilizzare un ingrediente e prelevarlo dal magazzino. Verrà quindi inserito tra gli ingredienti attualmente in uso, decrementandone le quantità presenti nel magazzino.

### Riportare un ingrediente precedentemente prelevato nel magazzino
Selezionando la voce [4], si sceglie di riportare un ingrediente precedentemente prelevato nel magazzino. Inizia con la visualizzazione della lista degli ingredienti usati in giornata. Dopodiché ne viene chiesto il nome e la quantità da riportare in magazzino. 

### Scartare un prodotto
Selezionando la voce [5], si sceglie di scartare un prodotto. Viene visualizzato il contenuto del magazzino, e quindi chiesti il nome e la quantità dell'ingrediente da scartare. Il sistema si occuperà di eliminarne le occorrenze dal magazzino.

# Stato del progetto
Il progetto Ristorante è stato terminato nel suo insieme. Tuttavia, il sistema è stato progettato iterativamente, quindi non escludiamo la possibilità, se necessario, di implementare nuove funzionalità al sistema software, come la presenza di un'interfaccia grafica o l'utilizzo di un Database Management System.

# Istruzioni d’installazione e uso
Il sistema è stato fornito sia col codice sorgente che col JAR, ossia l'eseguibile. Per l'utente che non ha basi di programmazione, suggeriamo di utilizzare il programma mediante questa funzionalità, in quanto più semplice. Per avviarlo, è sufficiente innanzitutto avere installato sul proprio PC una JDK Java, disponibile su sito di ORACLE https://www.oracle.com/java/technologies/downloads/.  
A seguire, basta aprire il proprio terminale ed eseguire il comando _java -jar nomejar.jar_ trovandosi ovviamente nella directroy dove è presente il JAR. Nella stessa directory ci sarà una cartella denominata _files_ che racchiuderà tutti i file di cui l'applicativo ha bisogno. Questi file si possono visionare e scrivere all'occorrenza. Un'implementazione futura potrà essere quella di mantenere questi file su una macchina esterna.
Ricordiamo che l'accesso agli utenti è monitorato: se il Gestore non ha ancora svolto l'inizializzazione del ristorante, agli altri utenti non è permesso di lavorare, in quanto gli mancherebbero i dati da elaborare. L'utente che, però, ha più vincoli è il Magazziniere, in quanto è colui che deve attendere anche il lavoro dell'Addetto alle Prenotazioni, altrimenti non saprebbe quanta merce acquistare.

# Tecnologie utilizzate
Ristorante è stato progettato in Java, per la stampa video fa uso della libreria https://github.com/vdmeer/asciitable, utile in quanto permette di formattare in maniera gradevole visivamente tabelle con molti dati, nel nostro caso piatti di menù ed ingredienti di ricette, che verrebbero sformattate altrimenti. Inoltre, Ristorante fa uso di file XML per il recupero e la memorizzazione di informazioni, dato che, come accennavamo in «Stato del progetto», la presenza di un vero e proprio Database Management System è un qualcosa che potrebbe avvenire in futuro, ma ora volevamo un metodo di salvataggio di dati che fosse affidabile ed estendibile facilmente.

# Informazioni su copyright e licenze
Ristorante non ha una licenza, è un progetto open source.

# Autori
Gli autori di Ristorante sono Campanella Matteo, Contestabile Martina e Racsan Antonello Costantin, studenti del terzo anno di Ingegneria Informatica, Coorte 2020, dell'Università degli Studi di Brescia. L'applicativo è stato sviluppato come progetto per l'esame di Ingegneria del Software.
