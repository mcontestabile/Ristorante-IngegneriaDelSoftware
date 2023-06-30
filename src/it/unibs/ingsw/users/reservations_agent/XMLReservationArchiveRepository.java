package it.unibs.ingsw.users.reservations_agent;

import it.unibs.ingsw.mylib.utilities.UsefulStrings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class XMLReservationArchiveRepository implements ReservationArchiveRepository{
    @Override
    public void save(String workingDay) {
        String f = UsefulStrings.LOCATION_RES_ARCHIVE + workingDay + UsefulStrings.XML_FILE_EXTENSION;

        boolean firstLineDone = true;
        try {
            Scanner myReader = new Scanner(new File(UsefulStrings.AGENDA_FILE));
            FileWriter myWriter = new FileWriter(f);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                myWriter.write(data+"\n");

                // i commenti in XML vanno dopo l'intestazione
                // ergo faccio scrivere la prima linea poi imposto la flag come false, in modo tale da non entrare più nell'if
                if(firstLineDone) {
                    myWriter.write("<!--Elenco prenotazioni del " + workingDay + "-->\n");
                    firstLineDone = false;
                }
            }
            myReader.close();
            myWriter.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
