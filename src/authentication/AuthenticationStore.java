// Der Store gehört zum Authentication-Paket, weil er die dort erzeugten Subjects verwaltet.
package authentication;

// ArrayList ist die konkrete Datenstruktur, die die Subjects während der Programmlaufzeit speichert.
import java.util.ArrayList;
// List wird als allgemeiner Rückgabe- und Attributtyp verwendet, damit die Implementierung austauschbar bleibt.
import java.util.List;

// Der AuthenticationStore ersetzt für dieses Projekt eine Datenbank und hält alle Subjects im Hauptspeicher.
public class AuthenticationStore {
    // Die Liste enthält alle Subjects, die der Benutzer während der aktuellen Programmausführung anlegt.
    private final List<Subject> subjects;

    // Beim Erstellen des Stores wird eine eigene leere Liste für die Subjects vorbereitet.
    public AuthenticationStore() {
        // ArrayList eignet sich hier, weil Subjects einfach hinzugefügt, entfernt und durchlaufen werden sollen.
        this.subjects = new ArrayList<>();
    }

    // Diese Methode ist der kontrollierte Zugang zum Speichern eines neuen Subjects.
    public void addSubject(Subject subject) {
        // null wird nicht gespeichert, weil es kein verwendbares Subject repräsentiert.
        if (subject != null) {
            // Das gültige Subject bleibt bis zum Programmende oder bis zu seiner Löschung im Hauptspeicher.
            subjects.add(subject);
        }
    }

    // Diese Methode entfernt ein Subject wieder aus dem Hauptspeicher.
    public void removeSubject(Subject subject) {
        // List.remove entfernt das übergebene Objekt, wenn es in der Liste enthalten ist.
        subjects.remove(subject);
    }

    // Diese Methode stellt die gespeicherten Subjects für Ausgabe und Auswahl zur Verfügung.
    public List<Subject> getSubjects() {
        // Eine Kopie schützt die interne Liste davor, von außen ohne addSubject oder removeSubject verändert zu werden.
        return new ArrayList<>(subjects);
    }
}
