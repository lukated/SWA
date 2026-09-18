// Die Klasse gehört zum authentication-Paket, weil sie die textuelle Bedienung dieses Bereichs bereitstellt.
package authentication;

// List wird für die nummerierte Anzeige und Auswahl der gespeicherten Subjects benötigt.
import java.util.List;
// Objects prüft, ob die benötigten Abhängigkeiten beim Erstellen der Konsole vorhanden sind.
import java.util.Objects;
// Scanner liest die Eingaben des Benutzers aus der gemeinsamen Konsoleneingabe.
import java.util.Scanner;

// AuthenticationStrategy ist der gemeinsame Typ aller auswählbaren Authentifizierungsverfahren.
import authentication.strategy.AuthenticationStrategy;
// EyeScanStrategy prüft Credentials vom Typ IRIS_SCAN.
import authentication.strategy.EyeScanStrategy;
// FingerPrintStrategy prüft Credentials vom Typ FINGERPRINT.
import authentication.strategy.FingerPrintStrategy;
// UserNamePasswordStrategy prüft Credentials vom Typ PASSWORD.
import authentication.strategy.UserNamePasswordStrategy;
// AddSubjectCommand sorgt dafür, dass das Anlegen eines Subjects rückgängig gemacht werden kann.
import command.AddSubjectCommand;
// CommandManager führt Commands aus und merkt sich die letzte Aktion für Undo.
import command.CommandManager;
// DeleteSubjectCommand kapselt das Löschen eines Subjects einschließlich der Gegenaktion.
import command.DeleteSubjectCommand;

// Die AuthenticationConsole verbindet Benutzereingaben mit Store, Service, Strategy und Command.
public class AuthenticationConsole {
    // Derselbe Scanner kann von Main an mehrere Konsolen weitergegeben werden und wird hier nicht geschlossen.
    private final Scanner scanner;

    // Der Store hält alle Subjects ausschließlich während der aktuellen Programmausführung im Hauptspeicher.
    private final AuthenticationStore store;

    // Der Service führt den Use Case authenticateSubject mit einer austauschbaren Strategie aus.
    private final AuthenticationService authenticationService;

    // Der Manager stellt Undo für das Eingeben und Löschen von Subjects bereit.
    private final CommandManager commandManager;

    // Der Konstruktor erhält alle Abhängigkeiten, damit sie später in Main gemeinsam verbunden werden können.
    public AuthenticationConsole(
            Scanner scanner,
            AuthenticationStore store,
            AuthenticationService authenticationService,
            CommandManager commandManager) {

        // Ohne Scanner könnte das Menü keine Benutzereingaben empfangen.
        this.scanner = Objects.requireNonNull(scanner, "Der Scanner darf nicht null sein.");

        // Ohne Store könnten Subjects weder gespeichert noch ausgewählt werden.
        this.store = Objects.requireNonNull(store, "Der AuthenticationStore darf nicht null sein.");

        // Ohne Service könnte das Menü keine Authentifizierung durchführen.
        this.authenticationService = Objects.requireNonNull(
                authenticationService,
                "Der AuthenticationService darf nicht null sein.");

        // Ohne CommandManager wären die geforderten Undo-Aktionen nicht verfügbar.
        this.commandManager = Objects.requireNonNull(
                commandManager,
                "Der CommandManager darf nicht null sein.");
    }

    // start zeigt das Authentication-Menü wiederholt an, bis der Benutzer zur vorherigen Ebene zurückkehrt.
    public void start() {
        // Die Schleifenvariable hält das Menü während der Bedienung aktiv.
        boolean running = true;

        // Die Schleife ermöglicht mehrere Aktionen, ohne die Anwendung nach jeder Eingabe zu beenden.
        while (running) {
            // Die Ausgaben bilden das geforderte textuelle Auswahlmenü und den Authentication-Use-Case ab.
            System.out.println();
            System.out.println("--- Authentication-Menü ---");
            System.out.println("1 - Subject eingeben");
            System.out.println("2 - Subject löschen");
            System.out.println("3 - Subjects ausgeben");
            System.out.println("4 - Subject authentifizieren");
            System.out.println("5 - Letzte Aktion rückgängig machen");
            System.out.println("0 - Zurück");
            System.out.print("Auswahl: ");

            // nextLine liest die vollständige Eingabe und vermeidet Probleme mit übrigen Zeilenumbrüchen.
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
                    authenticateSubject();
                    break;
                case "5":
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

    // inputData liest ein Subject und mindestens ein Credential ein und speichert es über ein Command.
    private void inputData() {
        // Der Name wird später für eine verständliche Auswahl und Ausgabe benötigt.
        System.out.print("Name des Subjects: ");
        String name = scanner.nextLine();

        // Ein leerer Name würde das Subject in der Konsole nicht sinnvoll identifizierbar machen.
        if (name.isBlank()) {
            System.out.println("Der Name darf nicht leer sein.");
            return;
        }

        // Die Auswahl wird in einen typsicheren SubjectType übersetzt.
        SubjectType subjectType = readSubjectType();

        // Bei einer ungültigen Auswahl wird kein unvollständiges Subject erstellt.
        if (subjectType == null) {
            return;
        }

        // Das Subject wird zunächst mit seinen festen Stammdaten und einer leeren Credential-Liste erzeugt.
        Subject subject = new Subject(name, subjectType);

        // Mindestens ein Credential wird benötigt, damit das Subject später authentifiziert werden kann.
        Credential firstCredential = readCredential();

        // Bei einer ungültigen Credential-Eingabe wird das gesamte Anlegen abgebrochen.
        if (firstCredential == null) {
            return;
        }

        // Das erste gültige Credential wird mit dem neuen Subject verknüpft.
        subject.addCredential(firstCredential);

        // Die Schleife erlaubt weitere Authentifizierungsarten für dasselbe Subject.
        boolean addMoreCredentials = true;
        while (addMoreCredentials) {
            // Der Benutzer entscheidet bewusst, ob die Credential-Liste erweitert werden soll.
            System.out.print("Weiteres Credential hinzufügen? (j/n): ");
            String answer = scanner.nextLine();

            // Nur ein eindeutiges j führt zu einer weiteren Credential-Eingabe.
            if (answer.equalsIgnoreCase("j")) {
                // Das zusätzliche Credential wird mit derselben geprüften Hilfsmethode eingelesen.
                Credential additionalCredential = readCredential();

                // Eine ungültige weitere Eingabe beendet nur die Erweiterung; das erste Credential bleibt erhalten.
                if (additionalCredential == null) {
                    addMoreCredentials = false;
                } else {
                    // Das gültige zusätzliche Credential wird in der internen Liste des Subjects gespeichert.
                    subject.addCredential(additionalCredential);
                }
            } else {
                // Jede andere Antwort beendet die Credential-Eingabe und führt mit dem Speichern fort.
                addMoreCredentials = false;
            }
        }

        // Das Command kapselt das Speichern, damit der CommandManager es rückgängig machen kann.
        AddSubjectCommand command = new AddSubjectCommand(store, subject);

        // Der Manager führt die Eingabe aus und merkt sie sich gleichzeitig als letzte Aktion.
        commandManager.executeCommand(command);

        // Die Rückmeldung bestätigt, dass das Subject jetzt im Hauptspeicher liegt.
        System.out.println("Subject wurde hinzugefügt.");
    }

    // deleteData lässt ein gespeichertes Subject auswählen und entfernt es über ein Command.
    private void deleteData() {
        // Die gemeinsame Auswahlmethode liefert das gewählte Subject oder null bei einer ungültigen Auswahl.
        Subject subject = selectSubject("Nummer des zu löschenden Subjects: ");

        // Ohne gültiges Subject darf keine Löschaktion erzeugt werden.
        if (subject == null) {
            return;
        }

        // Das Delete-Command behält das Subject für ein mögliches Undo im Speicher.
        DeleteSubjectCommand command = new DeleteSubjectCommand(store, subject);

        // Der Manager führt die Löschung aus und speichert sie als letzte rückgängig machbare Aktion.
        commandManager.executeCommand(command);

        // Die Rückmeldung bestätigt die erfolgreiche Datenänderung.
        System.out.println("Subject wurde gelöscht.");
    }

    // outputData gibt alle Subjects mit Name und SubjectType nummeriert aus.
    private void outputData() {
        // Der Store liefert eine Kopie, damit die Konsole seine interne Liste nicht verändern kann.
        List<Subject> subjects = store.getSubjects();

        // Eine verständliche Meldung ersetzt eine leere und dadurch verwirrende Ausgabe.
        if (subjects.isEmpty()) {
            System.out.println("Es sind keine Subjects gespeichert.");
            return;
        }

        // Die Schleife erzeugt zusätzlich zu den Fachdaten eine Auswahlnummer ab 1.
        for (int index = 0; index < subjects.size(); index++) {
            // Das aktuelle Subject wird einmal gelesen und danach für alle Ausgabefelder verwendet.
            Subject subject = subjects.get(index);

            // Name und Typ reichen aus, um das Subject bei späteren Menüaktionen wiederzuerkennen.
            System.out.println(
                    (index + 1)
                            + " - Name: " + subject.getName()
                            + ", Typ: " + subject.getSubjectType());
        }
    }

    // authenticateSubject verbindet Subject-Auswahl, Credential-Eingabe und Strategy-Auswahl zum Use Case.
    private void authenticateSubject() {
        // Zuerst wird festgelegt, welches bereits gespeicherte Subject authentifiziert werden soll.
        Subject subject = selectSubject("Nummer des zu authentifizierenden Subjects: ");

        // Ohne gültiges Subject kann keine Authentifizierung gestartet werden.
        if (subject == null) {
            return;
        }

        // Das eingegebene Credential stellt den aktuellen Authentifizierungsversuch dar.
        Credential credential = readCredential();

        // Eine ungültige Credential-Eingabe beendet den Versuch ohne Service-Aufruf.
        if (credential == null) {
            return;
        }

        // Die Credential-Art entscheidet, welche konkrete Strategie für den Versuch benötigt wird.
        AuthenticationStrategy strategy = createStrategy(credential.getCredentialType());

        // Für TAN sieht das aktuelle UML keine konkrete Strategy-Klasse vor.
        if (strategy == null) {
            System.out.println("Für TAN ist im aktuellen UML keine AuthenticationStrategy definiert.");
            return;
        }

        // Der Service erhält die passende Strategie, ohne selbst deren konkrete Klasse kennen zu müssen.
        authenticationService.setStrategy(strategy);

        // Der Service delegiert den Vergleich an die ausgewählte Strategie.
        boolean authenticated = authenticationService.authenticateSubject(subject, credential);

        // Die Ausgabe übersetzt das boolean-Ergebnis in eine verständliche Rückmeldung.
        if (authenticated) {
            System.out.println("Authentifizierung erfolgreich.");
        } else {
            System.out.println("Authentifizierung fehlgeschlagen.");
        }
    }

    // readSubjectType übersetzt eine Texteingabe in einen der zwei erlaubten SubjectType-Werte.
    private SubjectType readSubjectType() {
        // Die Nummern machen die Eingabe einfacher als die exakte Schreibweise eines Enum-Werts.
        System.out.println("Subject-Typ:");
        System.out.println("1 - NATURAL_PERSON");
        System.out.println("2 - SOFTWARE_SYSTEM");
        System.out.print("Auswahl: ");
        String selection = scanner.nextLine();

        // Jede gültige Auswahl wird genau einem typsicheren Enum-Wert zugeordnet.
        switch (selection) {
            case "1":
                return SubjectType.NATURAL_PERSON;
            case "2":
                return SubjectType.SOFTWARE_SYSTEM;
            default:
                System.out.println("Ungültiger Subject-Typ.");
                return null;
        }
    }

    // readCredential liest Typ und Wert gemeinsam ein, damit immer ein vollständiges Credential entsteht.
    private Credential readCredential() {
        // Zuerst wird die gewünschte Credential-Art ausgewählt.
        CredentialType credentialType = readCredentialType();

        // Ein ungültiger Typ verhindert die Erzeugung eines unvollständigen Credentials.
        if (credentialType == null) {
            return null;
        }

        // Der Wert wird als String gespeichert; die Strategie vergleicht später denselben Objekttyp.
        System.out.print("Credential-Wert: ");
        String credentialValue = scanner.nextLine();

        // Ein leerer Wert könnte keine sinnvolle Identitätsprüfung ermöglichen.
        if (credentialValue.isBlank()) {
            System.out.println("Der Credential-Wert darf nicht leer sein.");
            return null;
        }

        // Typ und Wert werden zu dem im UML vorgesehenen Credential-Fachobjekt verbunden.
        return new Credential(credentialType, credentialValue);
    }

    // readCredentialType übersetzt eine Menüauswahl in den passenden CredentialType.
    private CredentialType readCredentialType() {
        // Die vier Werte entsprechen vollständig dem vorhandenen CredentialType-Enum.
        System.out.println("Credential-Typ:");
        System.out.println("1 - PASSWORD");
        System.out.println("2 - FINGERPRINT");
        System.out.println("3 - IRIS_SCAN");
        System.out.println("4 - TAN");
        System.out.print("Auswahl: ");
        String selection = scanner.nextLine();

        // Der switch liefert für jede erlaubte Zahl den passenden Enum-Wert.
        switch (selection) {
            case "1":
                return CredentialType.PASSWORD;
            case "2":
                return CredentialType.FINGERPRINT;
            case "3":
                return CredentialType.IRIS_SCAN;
            case "4":
                return CredentialType.TAN;
            default:
                System.out.println("Ungültiger Credential-Typ.");
                return null;
        }
    }

    // createStrategy kapselt die Zuordnung zwischen CredentialType und konkreter Strategy-Klasse.
    private AuthenticationStrategy createStrategy(CredentialType credentialType) {
        // Jede unterstützte Credential-Art erhält ihre im UML vorgesehene Strategie.
        switch (credentialType) {
            case PASSWORD:
                return new UserNamePasswordStrategy();
            case FINGERPRINT:
                return new FingerPrintStrategy();
            case IRIS_SCAN:
                return new EyeScanStrategy();
            case TAN:
            default:
                // Für TAN existiert bewusst keine erfundene Klasse außerhalb des vorgegebenen UML.
                return null;
        }
    }

    // selectSubject bündelt Anzeige, Eingabe und Indexprüfung für Löschen und Authentifizieren.
    private Subject selectSubject(String prompt) {
        // Die Store-Kopie schützt die interne Liste vor Veränderungen durch die Konsole.
        List<Subject> subjects = store.getSubjects();

        // Ohne gespeicherte Subjects gibt es kein gültiges Auswahlziel.
        if (subjects.isEmpty()) {
            System.out.println("Es sind keine Subjects gespeichert.");
            return null;
        }

        // Die Ausgabe zeigt dieselbe Reihenfolge, die anschließend für die Auswahl verwendet wird.
        outputData();
        System.out.print(prompt);
        String selection = scanner.nextLine();

        try {
            // Die sichtbare Nummer beginnt bei 1 und wird deshalb für den Listenindex um 1 reduziert.
            int subjectIndex = Integer.parseInt(selection) - 1;

            // Die Bereichsprüfung verhindert einen ungültigen Zugriff auf die Liste.
            if (subjectIndex < 0 || subjectIndex >= subjects.size()) {
                System.out.println("Diese Subject-Nummer existiert nicht.");
                return null;
            }

            // Bei einer gültigen Nummer wird das ausgewählte Subject zurückgegeben.
            return subjects.get(subjectIndex);
        } catch (NumberFormatException exception) {
            // Nicht numerische Eingaben werden abgefangen, ohne das Menü zu beenden.
            System.out.println("Bitte eine gültige Subject-Nummer eingeben.");
            return null;
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
