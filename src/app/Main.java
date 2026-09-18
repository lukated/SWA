// Das app-Paket enthält den Einstiegspunkt, der die getrennten Fachpakete zur Anwendung verbindet.
package app;

// BigDecimal verarbeitet den eingegebenen Zahlungsbetrag ohne Rundungsfehler.
import java.math.BigDecimal;
// List wird für die Auswahl gespeicherter Subjects und Accounts benötigt.
import java.util.List;
// Scanner liest alle Benutzereingaben über die Konsole.
import java.util.Scanner;

// AuthenticationConsole stellt das Menü für Subjects und Authentifizierungsversuche bereit.
import authentication.AuthenticationConsole;
// AuthenticationService führt die ausgewählte Strategy aus und wird auch von Payment verwendet.
import authentication.AuthenticationService;
// AuthenticationStore hält die Subjects während der Programmlaufzeit im Hauptspeicher.
import authentication.AuthenticationStore;
// Credential enthält die beim Zahlungsversuch eingegebenen Authentifizierungsdaten.
import authentication.Credential;
// CredentialType entscheidet, welche konkrete AuthenticationStrategy benötigt wird.
import authentication.CredentialType;
// Subject bezeichnet den Auftraggeber einer Zahlung.
import authentication.Subject;
// AuthenticationStrategy ist der gemeinsame Typ der austauschbaren Prüfverfahren.
import authentication.strategy.AuthenticationStrategy;
// EyeScanStrategy prüft ein Credential vom Typ IRIS_SCAN.
import authentication.strategy.EyeScanStrategy;
// FingerPrintStrategy prüft ein Credential vom Typ FINGERPRINT.
import authentication.strategy.FingerPrintStrategy;
// UserNamePasswordStrategy prüft ein Credential vom Typ PASSWORD.
import authentication.strategy.UserNamePasswordStrategy;
// CommandManager ermöglicht Undo für Eingabe- und Löschaktionen in den Konsolen.
import command.CommandManager;
// Account stellt Sender und Empfänger einer Zahlung dar.
import payment.Account;
// CurrencyAmount verbindet den eingegebenen Betrag mit der Währung des Senderkontos.
import payment.CurrencyAmount;
// PaymentConsole verwaltet die Accounts über das geforderte Konsolenmenü.
import payment.PaymentConsole;
// PaymentService führt den Template-Method-Zahlungsablauf aus.
import payment.PaymentService;
// PaymentStore hält die Accounts während der Programmlaufzeit im Hauptspeicher.
import payment.PaymentStore;
// PaymentType wählt PayPal, Google Wallet oder Apple Pay aus.
import payment.PaymentType;

// Main ist der Startpunkt und übernimmt ausschließlich die Verbindung und Reihenfolge der vorhandenen Bausteine.
public class Main {

    // Die JVM ruft main beim Start der Java-Anwendung automatisch auf.
    public static void main(String[] args) {
        // try-with-resources schließt die Konsoleneingabe erst beim endgültigen Programmende.
        try (Scanner scanner = new Scanner(System.in)) {
            // Der AuthenticationStore bewahrt alle während des Programms angelegten Subjects auf.
            AuthenticationStore authenticationStore = new AuthenticationStore();

            // Derselbe AuthenticationService wird für direkte Authentifizierung und für Payment verwendet.
            AuthenticationService authenticationService = new AuthenticationService();

            // Ein eigener Manager hält das Undo der Authentication-Konsole in ihrem fachlichen Bereich.
            CommandManager authenticationCommandManager = new CommandManager();

            // Die AuthenticationConsole erhält ihre Abhängigkeiten von Main und erzeugt sie nicht selbst.
            AuthenticationConsole authenticationConsole = new AuthenticationConsole(
                    scanner,
                    authenticationStore,
                    authenticationService,
                    authenticationCommandManager);

            // Der PaymentStore bewahrt alle während des Programms angelegten Accounts auf.
            PaymentStore paymentStore = new PaymentStore();

            // Der PaymentService erhält Authentication direkt, weil jede Zahlung mit diesem Schritt beginnt.
            PaymentService paymentService = new PaymentService(authenticationService);

            // Ein eigener Manager verhindert, dass Payment-Undo versehentlich Authentication-Daten verändert.
            CommandManager paymentCommandManager = new CommandManager();

            // Die PaymentConsole verwaltet ihre Accounts über Store und Command-Muster.
            PaymentConsole paymentConsole = new PaymentConsole(
                    scanner,
                    paymentStore,
                    paymentCommandManager);

            // Das Hauptmenü verbindet die Teilbereiche und hält die Anwendung bis zur Auswahl 0 aktiv.
            startMainMenu(
                    scanner,
                    authenticationConsole,
                    paymentConsole,
                    authenticationStore,
                    paymentStore,
                    authenticationService,
                    paymentService);
        }
    }

    // Diese Methode zeigt das übergeordnete Menü und gibt die Bedienung an die Teilbereiche weiter.
    private static void startMainMenu(
            Scanner scanner,
            AuthenticationConsole authenticationConsole,
            PaymentConsole paymentConsole,
            AuthenticationStore authenticationStore,
            PaymentStore paymentStore,
            AuthenticationService authenticationService,
            PaymentService paymentService) {

        // Die Schleifenvariable hält die Anwendung aktiv, bis der Benutzer sie bewusst beendet.
        boolean running = true;

        // Das Hauptmenü wird nach jeder abgeschlossenen Aktion erneut angezeigt.
        while (running) {
            // Die Reihenfolge führt zuerst zu Authentication-Daten, dann zu Accounts und schließlich zur Zahlung.
            System.out.println();
            System.out.println("=== Car Reservation Service ===");
            System.out.println("1 - Authentication verwalten");
            System.out.println("2 - Payment-Accounts verwalten");
            System.out.println("3 - Zahlung durchführen");
            System.out.println("0 - Programm beenden");
            System.out.print("Auswahl: ");

            // nextLine liest die vollständige Menüeingabe und hält die Scanner-Verwendung einheitlich.
            String selection = scanner.nextLine();

            // Der switch übergibt die Kontrolle an das ausgewählte Untermenü oder den Payment-Ablauf.
            switch (selection) {
                case "1":
                    authenticationConsole.start();
                    break;
                case "2":
                    paymentConsole.start();
                    break;
                case "3":
                    executePayment(
                            scanner,
                            authenticationStore,
                            paymentStore,
                            authenticationService,
                            paymentService);
                    break;
                case "0":
                    // false beendet die Schleife und anschließend den try-Block samt Scanner.
                    running = false;
                    break;
                default:
                    // Unbekannte Eingaben führen ohne Datenänderung zurück zum Hauptmenü.
                    System.out.println("Ungültige Auswahl.");
                    break;
            }
        }

        // Die Abschlussmeldung macht das reguläre Programmende für den Benutzer sichtbar.
        System.out.println("Car Reservation Service wurde beendet.");
    }

    // executePayment sammelt die benötigten Objekte ein und ruft danach den PaymentService auf.
    private static void executePayment(
            Scanner scanner,
            AuthenticationStore authenticationStore,
            PaymentStore paymentStore,
            AuthenticationService authenticationService,
            PaymentService paymentService) {

        // Eine Kopie genügt für Auswahl und Anzeige und schützt die interne Subject-Liste des Stores.
        List<Subject> subjects = authenticationStore.getSubjects();

        // Ohne Subject fehlt der Auftraggeber, der vor der Zahlung authentifiziert werden muss.
        if (subjects.isEmpty()) {
            System.out.println("Bitte zuerst ein Subject im Authentication-Menü anlegen.");
            return;
        }

        // Eine Kopie genügt auch hier und schützt die interne Account-Liste des PaymentStores.
        List<Account> accounts = paymentStore.getAccounts();

        // Eine Zahlung benötigt zwei Konten, weil Sender und Empfänger verschieden sein müssen.
        if (accounts.size() < 2) {
            System.out.println("Bitte zuerst mindestens zwei Payment-Accounts anlegen.");
            return;
        }

        // Der Auftraggeber wird aus den bereits angelegten Subjects ausgewählt.
        Subject subject = selectSubject(scanner, subjects);

        // Eine ungültige Auswahl beendet den Zahlungsversuch ohne Änderungen an Kontoständen.
        if (subject == null) {
            return;
        }

        // Das Senderkonto wird aus den bereits angelegten Accounts ausgewählt.
        Account sender = selectAccount(scanner, accounts, "Nummer des Senderkontos: ");

        // Ohne gültiges Senderkonto kann keine Buchung durchgeführt werden.
        if (sender == null) {
            return;
        }

        // Das Empfängerkonto wird unabhängig ausgewählt; AbstractPayment prüft später, dass es verschieden ist.
        Account receiver = selectAccount(scanner, accounts, "Nummer des Empfängerkontos: ");

        // Ohne gültiges Empfängerkonto kann keine Buchung durchgeführt werden.
        if (receiver == null) {
            return;
        }

        // Der Credential-Typ bestimmt, welche AuthenticationStrategy vor der Zahlung verwendet wird.
        CredentialType credentialType = readCredentialType(scanner);

        // Eine ungültige Auswahl beendet den Versuch vor dem Erzeugen eines Credentials.
        if (credentialType == null) {
            return;
        }

        // Die Zuordnung erzeugt die zum ausgewählten Credential-Typ passende konkrete Strategie.
        AuthenticationStrategy strategy = createStrategy(credentialType);

        // Der Service arbeitet danach nur noch gegen das gemeinsame AuthenticationStrategy-Interface.
        authenticationService.setStrategy(strategy);

        // Der Benutzer gibt den aktuellen Wert ein, der mit dem gespeicherten Credential verglichen wird.
        System.out.print("Credential-Wert für die Zahlung: ");
        String credentialValue = scanner.nextLine();

        // Ein leerer Wert kann keine erfolgreiche Authentifizierung ergeben und wird früh abgelehnt.
        if (credentialValue.isBlank()) {
            System.out.println("Der Credential-Wert darf nicht leer sein.");
            return;
        }

        // Typ und Eingabewert bilden gemeinsam das Credential dieses Authentifizierungsversuchs.
        Credential credential = new Credential(credentialType, credentialValue);

        // Der PaymentType wählt die konkrete Template-Method-Variante für den zweiten Schritt aus.
        PaymentType paymentType = readPaymentType(scanner);

        // Eine ungültige Auswahl beendet den Versuch, bevor ein Betrag gebucht wird.
        if (paymentType == null) {
            return;
        }

        // Der Geldbetrag wird als Text gelesen und erst im try-Block in BigDecimal umgewandelt.
        System.out.print("Zahlungsbetrag in " + sender.getBalance().getCurrencyCode() + ": ");
        String amountInput = scanner.nextLine();

        try {
            // BigDecimal verhindert die bei double möglichen Rundungsfehler für Geldwerte.
            BigDecimal amountValue = new BigDecimal(amountInput);

            // Die Währung wird vom Senderkonto übernommen; Empfänger und Betrag werden im Template validiert.
            CurrencyAmount amount = new CurrencyAmount(
                    amountValue,
                    sender.getBalance().getCurrencyCode());

            // Der Service wählt den Prozessor und startet den festen Ablauf aus drei Schritten.
            String result = paymentService.payAmount(
                    paymentType,
                    subject,
                    credential,
                    sender,
                    receiver,
                    amount);

            // Die Rückgabe enthält entweder die Zahlungsbestätigung oder die Ablehnung der Authentifizierung.
            System.out.println(result);

            // Die anschließende Ausgabe macht sichtbar, ob und wie sich die beiden Kontostände verändert haben.
            System.out.println("Sender-Kontostand: " + sender.getBalance());
            System.out.println("Empfänger-Kontostand: " + receiver.getBalance());
        } catch (IllegalArgumentException | IllegalStateException exception) {
            // Zahlenfehler, Währungsprobleme und unzureichendes Guthaben werden verständlich abgefangen.
            System.out.println("Zahlung konnte nicht ausgeführt werden: " + exception.getMessage());
        }
    }

    // selectSubject zeigt alle Subjects und übersetzt die Benutzernummer in das gewählte Objekt.
    private static Subject selectSubject(Scanner scanner, List<Subject> subjects) {
        // Die Schleife erzeugt eine verständliche Nummerierung ab 1.
        for (int index = 0; index < subjects.size(); index++) {
            // Das aktuelle Subject liefert seine Fachdaten über die zuvor ergänzten Getter.
            Subject subject = subjects.get(index);
            System.out.println(
                    (index + 1)
                            + " - " + subject.getName()
                            + " (" + subject.getSubjectType() + ")");
        }

        // Die eingegebene Nummer bestimmt den Auftraggeber der Zahlung.
        System.out.print("Nummer des Auftraggebers: ");
        String selection = scanner.nextLine();

        try {
            // Die sichtbare Nummer ab 1 wird in den Java-Listenindex ab 0 umgerechnet.
            int subjectIndex = Integer.parseInt(selection) - 1;

            // Die Bereichsprüfung verhindert einen ungültigen Listenzugriff.
            if (subjectIndex < 0 || subjectIndex >= subjects.size()) {
                System.out.println("Diese Subject-Nummer existiert nicht.");
                return null;
            }

            // Bei einer gültigen Nummer wird das ausgewählte Subject zurückgegeben.
            return subjects.get(subjectIndex);
        } catch (NumberFormatException exception) {
            // Nicht numerische Eingaben beenden nur diesen Zahlungsversuch.
            System.out.println("Bitte eine gültige Subject-Nummer eingeben.");
            return null;
        }
    }

    // selectAccount zeigt alle Accounts und liefert das als Sender oder Empfänger ausgewählte Objekt.
    private static Account selectAccount(Scanner scanner, List<Account> accounts, String prompt) {
        // Die Schleife zeigt ID, Eigentümer und Kontostand für eine eindeutige Auswahl an.
        for (int index = 0; index < accounts.size(); index++) {
            // Der aktuelle Account wird einmal aus der Liste gelesen und für die vollständige Zeile verwendet.
            Account account = accounts.get(index);
            System.out.println(
                    (index + 1)
                            + " - ID: " + account.getAccountId()
                            + ", Eigentümer: " + account.getOwnerName()
                            + ", Kontostand: " + account.getBalance());
        }

        // Der unterschiedliche Prompt macht deutlich, ob gerade Sender oder Empfänger ausgewählt wird.
        System.out.print(prompt);
        String selection = scanner.nextLine();

        try {
            // Die sichtbare Nummer ab 1 wird in den Java-Listenindex ab 0 umgerechnet.
            int accountIndex = Integer.parseInt(selection) - 1;

            // Die Bereichsprüfung verhindert einen ungültigen Listenzugriff.
            if (accountIndex < 0 || accountIndex >= accounts.size()) {
                System.out.println("Diese Account-Nummer existiert nicht.");
                return null;
            }

            // Bei einer gültigen Nummer wird der ausgewählte Account zurückgegeben.
            return accounts.get(accountIndex);
        } catch (NumberFormatException exception) {
            // Nicht numerische Eingaben beenden nur diesen Zahlungsversuch.
            System.out.println("Bitte eine gültige Account-Nummer eingeben.");
            return null;
        }
    }

    // readCredentialType bietet nur die drei Credential-Arten an, für die laut UML Strategien existieren.
    private static CredentialType readCredentialType(Scanner scanner) {
        // Die Menüwerte werden anschließend in das typsichere CredentialType-Enum übersetzt.
        System.out.println("Authentifizierungsart:");
        System.out.println("1 - PASSWORD");
        System.out.println("2 - FINGERPRINT");
        System.out.println("3 - IRIS_SCAN");
        System.out.print("Auswahl: ");
        String selection = scanner.nextLine();

        // Jede gültige Auswahl entspricht genau einer vorhandenen Strategy-Implementierung.
        switch (selection) {
            case "1":
                return CredentialType.PASSWORD;
            case "2":
                return CredentialType.FINGERPRINT;
            case "3":
                return CredentialType.IRIS_SCAN;
            default:
                System.out.println("Ungültige Authentifizierungsart.");
                return null;
        }
    }

    // createStrategy kapselt die Zuordnung zwischen CredentialType und konkreter Strategy-Klasse.
    private static AuthenticationStrategy createStrategy(CredentialType credentialType) {
        // Da readCredentialType nur unterstützte Werte liefert, kann jeder Fall direkt zugeordnet werden.
        switch (credentialType) {
            case PASSWORD:
                return new UserNamePasswordStrategy();
            case FINGERPRINT:
                return new FingerPrintStrategy();
            case IRIS_SCAN:
                return new EyeScanStrategy();
            case TAN:
            default:
                // Dieser Zweig ist eine Absicherung für spätere Aufrufe außerhalb des aktuellen Menüs.
                throw new IllegalArgumentException("Für diesen Credential-Typ existiert keine Strategie.");
        }
    }

    // readPaymentType übersetzt die Benutzerauswahl in eine konkrete Payment-Variante.
    private static PaymentType readPaymentType(Scanner scanner) {
        // Die drei Werte entsprechen dem aktuellen PaymentType-Enum und dem vorhandenen UML.
        System.out.println("Zahlungsart:");
        System.out.println("1 - PAYPAL");
        System.out.println("2 - GOOGLE_WALLET");
        System.out.println("3 - APPLE_PAY");
        System.out.print("Auswahl: ");
        String selection = scanner.nextLine();

        // Der switch liefert den Enum-Wert, den PaymentService später seinem Prozessor zuordnet.
        switch (selection) {
            case "1":
                return PaymentType.PAYPAL;
            case "2":
                return PaymentType.GOOGLE_WALLET;
            case "3":
                return PaymentType.APPLE_PAY;
            default:
                System.out.println("Ungültige Zahlungsart.");
                return null;
        }
    }
}
