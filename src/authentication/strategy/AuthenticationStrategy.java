// Das Unterpaket strategy bündelt den gemeinsamen Vertrag und alle austauschbaren Strategien.
package authentication.strategy;

// Credential wird als Eingabe benötigt, weil es das zu prüfende Authentifizierungsmerkmal enthält.
import authentication.Credential;
// Subject wird benötigt, weil die Strategie das eingegebene Credential diesem Subject zuordnen muss.
import authentication.Subject;

// Das Interface definiert den gemeinsamen Vertrag für alle Arten der Authentifizierung.
public interface AuthenticationStrategy {
    // Jede konkrete Strategie muss entscheiden, ob das Credential für das angegebene Subject gültig ist.
    // boolean liefert true bei erfolgreicher Authentifizierung und false bei einer Ablehnung zurück.
    boolean authenticate(Subject subject, Credential credential);
}
