// Der Service gehört zum Authentication-Paket, weil er den Use Case authenticateSubject bereitstellt.
package authentication;

// Der Service kennt nur das gemeinsame Interface und bleibt dadurch unabhängig von einer konkreten Strategie.
import authentication.strategy.AuthenticationStrategy;

// Der AuthenticationService ist der zentrale Einstiegspunkt für eine Authentifizierung.
public class AuthenticationService {
    // Hier wird die aktuell ausgewählte Strategie gespeichert, zum Beispiel Passwort oder Fingerabdruck.
    private AuthenticationStrategy strategy;

    // Über den Setter kann die Authentifizierungsart zur Laufzeit ausgetauscht werden.
    public void setStrategy(AuthenticationStrategy strategy) {
        // Die übergebene Strategie wird für den nächsten Authentifizierungsversuch gespeichert.
        this.strategy = strategy;
    }

    // Diese Methode bildet den geforderten Use Case authenticateSubject ab.
    public boolean authenticateSubject(Subject subject, Credential credential) {
        // Ohne ausgewählte Strategie ist nicht festgelegt, auf welche Art authentifiziert werden soll.
        if (strategy == null) {
            return false;
        }

        // Der Service delegiert die Prüfung an die Strategie und kennt deren konkrete Umsetzung nicht.
        return strategy.authenticate(subject, credential);
    }
}
