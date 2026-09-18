// Die Klasse gehört zum payment-Paket, weil sie die textuelle Bedienung der Payment-Daten bereitstellt.
package payment;

// BigDecimal liest Geldbeträge exakt aus der Benutzereingabe ein.
import java.math.BigDecimal;
// List wird für die nummerierte Anzeige und Auswahl der gespeicherten Accounts benötigt.
import java.util.List;
// Objects prüft, ob die benötigten Abhängigkeiten beim Erstellen der Konsole vorhanden sind.
import java.util.Objects;
// Scanner liest die Eingaben des Benutzers aus der gemeinsamen Konsoleneingabe.
import java.util.Scanner;

// AddAccountCommand stellt sicher, dass das Anlegen eines Accounts rückgängig gemacht werden kann.
import command.AddAccountCommand;
// CommandManager führt die Account-Commands aus und merkt sich die letzte Aktion für Undo.
import command.CommandManager;
// DeleteAccountCommand kapselt das Löschen eines Accounts einschließlich der Gegenaktion.
import command.DeleteAccountCommand;

// Die PaymentConsole bietet das in der Aufgabe geforderte Menü für Account-Daten und Undo an.
public class PaymentConsole {
    // Derselbe Scanner kann später von Main an mehrere Konsolen weitergegeben werden und wird hier nicht geschlossen.
    private final Scanner scanner;

    // Der Store hält alle Accounts ausschließlich für die Dauer der Programmausführung im Hauptspeicher.
    private final PaymentStore store;

    // Der Manager sorgt dafür, dass Eingeben und Löschen über das Command-Muster ausgeführt werden.
    private final CommandManager commandManager;

    // Der Konstruktor erhält alle Abhängigkeiten, statt sie versteckt innerhalb der Konsole selbst zu erzeugen.
    public PaymentConsole(Scanner scanner, PaymentStore store, CommandManager commandManager) {
        // Ohne Scanner könnte das Menü keine Benutzereingaben empfangen.
        this.scanner = Objects.requireNonNull(scanner, "Der Scanner darf nicht null sein.");

        // Ohne PaymentStore könnten Accounts weder gespeichert noch angezeigt werden.
        this.store = Objects.requireNonNull(store, "Der PaymentStore darf nicht null sein.");

        // Ohne CommandManager wären die geforderten Undo-Aktionen nicht verfügbar.
        this.commandManager = Objects.requireNonNull(
                commandManager,
                "Der CommandManager darf nicht null sein.");
    }

    // start zeigt das Menü wiederholt an, bis der Benutzer bewusst zur vorherigen Ebene zurückkehrt.
    public void start() {
        // Die Schleifenvariable hält das Payment-Menü während der Bedienung aktiv.
        boolean running = true;

        // Die Schleife ermöglicht mehrere Aktionen, ohne die Anwendung nach jeder Eingabe zu beenden.
        while (running) {
            // Die folgenden Ausgaben bilden das geforderte textuelle Auswahlmenü ab.
            System.out.println();
            System.out.println("--- Payment-Menü ---");
            System.out.println("1 - Account eingeben");
            System.out.println("2 - Account löschen");
            System.out.println("3 - Accounts ausgeben");
            System.out.println("4 - Letzte Aktion rückgängig machen");
            System.out.println("0 - Zurück");
            System.out.print("Auswahl: ");

            // nextLine liest die gesamte Eingabe und vermeidet übrig gebliebene Zeilenumbrüche im Scanner.
            String selection = scanner.nextLine();

            // Der switch verbindet jede erlaubte Auswahl mit genau einer Konsolenfunktion.
            switch (selection) {
                case "1":
                    inputData();
                    break;
                case "2":
                    deleteData();
                    break;
                case "3":
                    outputData();
                    break;
                case "4":
                    undo();
                    break;
                case "0":
                    // false beendet nur dieses Menü; der gemeinsame Scanner bleibt für die Anwendung geöffnet.
                    running = false;
                    break;
                default:
                    // Unbekannte Eingaben verändern keine Daten und führen zurück zur nächsten Menüanzeige.
                    System.out.println("Ungültige Auswahl.");
                    break;
            }
        }
    }

    // inputData liest die Daten für einen neuen Account ein und speichert ihn über ein Command.
    private void inputData() {
        // Die ID wird später für eine eindeutige und verständliche Account-Auswahl verwendet.
        System.out.print("Account-ID: ");
        String accountId = scanner.nextLine();

        // Der Eigentümername stellt die fachliche Zuordnung des Accounts her.
        System.out.print("Eigentümername: ");
        String ownerName = scanner.nextLine();

        // Der Anfangsbestand wird zunächst als Text gelesen und anschließend exakt in BigDecimal umgewandelt.
        System.out.print("Anfangsbestand: ");
        String balanceInput = scanner.nextLine();

        // Der Währungscode wird getrennt erfasst, damit CurrencyAmount Betrag und Währung verbinden kann.
        System.out.print("Währungscode, zum Beispiel EUR: ");
        String currencyCode = scanner.nextLine();

        try {
            // BigDecimal verarbeitet die Texteingabe ohne die Rundungsprobleme eines double-Werts.
            BigDecimal balanceValue = new BigDecimal(balanceInput);

            // CurrencyAmount prüft Betrag und Währung, bevor daraus ein Kontostand entsteht.
            CurrencyAmount balance = new CurrencyAmount(balanceValue, currencyCode);

            // Aus den geprüften Eingaben wird das neue Account-Fachobjekt erstellt.
            Account account = new Account(accountId, ownerName, balance);

            // Das Command kapselt die Eingabe, damit der CommandManager sie später rückgängig machen kann.
            AddAccountCommand command = new AddAccountCommand(store, account);

            // Der Manager führt das Command aus und speichert es gleichzeitig als letzte Aktion.
            commandManager.executeCommand(command);

            // Die Rückmeldung zeigt dem Benutzer, dass die Eingabe erfolgreich verarbeitet wurde.
            System.out.println("Account wurde hinzugefügt.");
        } catch (IllegalArgumentException exception) {
            // Fehlerhafte Zahlen, leere Stammdaten oder ungültige Geldwerte werden verständlich gemeldet.
            System.out.println("Account konnte nicht angelegt werden: " + exception.getMessage());
        }
    }

    // deleteData lässt einen gespeicherten Account auswählen und entfernt ihn über ein Command.
    private void deleteData() {
        // Eine lokale Kopie reicht für Anzeige und Auswahl und schützt die interne Store-Liste.
        List<Account> accounts = store.getAccounts();

        // Ohne gespeicherte Accounts gibt es kein sinnvolles Löschziel.
        if (accounts.isEmpty()) {
            System.out.println("Es sind keine Accounts zum Löschen vorhanden.");
            return;
        }

        // Vor der Auswahl werden alle Accounts mit einer für Menschen lesbaren Nummer angezeigt.
        outputData();
        System.out.print("Nummer des zu löschenden Accounts: ");
        String selection = scanner.nextLine();

        try {
            // Die sichtbare Nummer beginnt bei 1 und wird deshalb für den Listenindex um 1 reduziert.
            int accountIndex = Integer.parseInt(selection) - 1;

            // Ungültige Indizes werden abgefangen, bevor auf die Liste zugegriffen wird.
            if (accountIndex < 0 || accountIndex >= accounts.size()) {
                System.out.println("Diese Account-Nummer existiert nicht.");
                return;
            }

            // Der ausgewählte Account wird aus der zuvor angezeigten Liste entnommen.
            Account account = accounts.get(accountIndex);

            // Das Delete-Command merkt sich den Account, damit er bei Undo wieder eingefügt werden kann.
            DeleteAccountCommand command = new DeleteAccountCommand(store, account);

            // Der Manager führt die Löschung aus und speichert sie als letzte rückgängig machbare Aktion.
            commandManager.executeCommand(command);

            // Die Bestätigung informiert über die erfolgreiche Datenänderung.
            System.out.println("Account wurde gelöscht.");
        } catch (NumberFormatException exception) {
            // Nicht numerische Eingaben werden abgefangen, ohne das Menü zu beenden.
            System.out.println("Bitte eine gültige Account-Nummer eingeben.");
        }
    }

    // outputData gibt alle Accounts mit ihren wichtigsten Fachinformationen aus.
    private void outputData() {
        // Die Kopie aus dem Store verhindert unkontrollierte Änderungen an dessen interner Liste.
        List<Account> accounts = store.getAccounts();

        // Eine verständliche Meldung ersetzt eine leere, verwirrende Ausgabe.
        if (accounts.isEmpty()) {
            System.out.println("Es sind keine Accounts gespeichert.");
            return;
        }

        // Die Schleife liefert zusätzlich zur Account-Information eine fortlaufende Auswahlnummer.
        for (int index = 0; index < accounts.size(); index++) {
            // Der aktuelle Account wird einmal aus der Liste gelesen und danach für alle Ausgabefelder verwendet.
            Account account = accounts.get(index);

            // index + 1 erzeugt eine benutzerfreundliche Nummerierung ab 1 statt des Java-Index ab 0.
            System.out.println(
                    (index + 1)
                            + " - ID: " + account.getAccountId()
                            + ", Eigentümer: " + account.getOwnerName()
                            + ", Kontostand: " + account.getBalance());
        }
    }

    // undo gibt die geforderte Rückgängig-Funktion an den zentralen CommandManager weiter.
    private void undo() {
        // Der Manager kennt das zuletzt ausgeführte Command und ruft dessen konkrete Gegenaktion auf.
        commandManager.undoLastCommand();

        // Die Meldung bestätigt den Aufruf; ohne vorheriges Command verändert der Manager keine Daten.
        System.out.println("Undo wurde ausgeführt, sofern eine letzte Aktion vorhanden war.");
    }
}
