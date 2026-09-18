// Die Klasse gehört zum Authentication-Paket, weil ein Subject authentifiziert werden soll.
package authentication;

// ArrayList ist die konkrete Listenklasse, in der die Credentials im Hauptspeicher abgelegt werden.
import java.util.ArrayList;
// List wird als allgemeiner Datentyp verwendet, damit die Klasse nicht unnötig an ArrayList gebunden ist.
import java.util.List;
// Objects stellt einen Vergleich bereit, der auch mit null-Werten sicher umgehen kann.
import java.util.Objects;

// Ein Subject beschreibt den Auftraggeber einer Authentifizierung, also eine Person oder ein Softwaresystem.
public class Subject {
    // Der Name identifiziert das Subject für die spätere Auswahl und Ausgabe in der Konsole.
    private final String name;

    // Der SubjectType unterscheidet zwischen einer natürlichen Person und einem Softwaresystem.
    private final SubjectType subjectType;

    // Ein Subject kann mehrere Credentials besitzen, zum Beispiel Passwort und Fingerabdruck.
    private final List<Credential> credentialList;

    // Beim Erstellen werden die festen Stammdaten gesetzt; Credentials können anschließend ergänzt werden.
    public Subject(String name, SubjectType subjectType) {
        // Der übergebene Name wird dauerhaft diesem Subject zugeordnet.
        this.name = name;

        // Der übergebene Typ legt fest, um welche Art von Subject es sich handelt.
        this.subjectType = subjectType;

        // Jedes Subject erhält eine eigene leere Liste, damit seine Credentials getrennt gespeichert werden.
        this.credentialList = new ArrayList<>();
    }

    // Der Getter stellt den Namen für Konsolenausgabe und Auswahl bereit, ohne das private Attribut veränderbar zu machen.
    public String getName() {
        // Zurückgegeben wird der feste Name dieses konkreten Subjects.
        return name;
    }

    // Der Getter ermöglicht der Konsole anzuzeigen, ob eine Person oder ein Softwaresystem ausgewählt wurde.
    public SubjectType getSubjectType() {
        // Zurückgegeben wird der beim Erstellen festgelegte und danach unveränderliche Subject-Typ.
        return subjectType;
    }

    // Diese Methode verknüpft ein zusätzliches Credential mit dem Subject.
    public void addCredential(Credential credential) {
        // null wird nicht gespeichert, weil ein fehlendes Objekt später nicht authentifiziert werden kann.
        if (credential != null) {
            // Das gültige Credential wird in der Liste dieses Subjects abgelegt.
            credentialList.add(credential);
        }
    }

    // Diese Methode löst die Verknüpfung zu einem nicht mehr benötigten Credential.
    public void removeCredential(Credential credential) {
        // List.remove entfernt das übergebene Objekt, falls es in der Liste enthalten ist.
        credentialList.remove(credential);
    }

    // Die Strategien verwenden diese Methode, um ein eingegebenes Credential mit den gespeicherten zu vergleichen.
    public boolean hasMatchingCredential(Credential credential) {
        // Ohne ein eingegebenes Credential kann keine erfolgreiche Übereinstimmung vorliegen.
        if (credential == null) {
            return false;
        }

        // Die Schleife prüft nacheinander alle Credentials, die zu diesem Subject gehören.
        for (Credential storedCredential : credentialList) {
            // Enum-Werte werden mit == verglichen, weil jeder Enum-Wert nur einmal existiert.
            boolean sameType = storedCredential.getCredentialType() == credential.getCredentialType();

            // Objects.equals vergleicht die Werte und verhindert dabei Fehler, falls ein Wert null ist.
            boolean sameValue = Objects.equals(
                    storedCredential.getCredentialValue(),
                    credential.getCredentialValue());

            // Nur wenn Typ und Wert übereinstimmen, handelt es sich um dasselbe Authentifizierungsmerkmal.
            if (sameType && sameValue) {
                return true;
            }
        }

        // Wurde in der gesamten Liste keine Übereinstimmung gefunden, schlägt die Prüfung fehl.
        return false;
    }
}
