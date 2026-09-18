// Die Klasse liegt im strategy-Paket, weil sie eine konkrete austauschbare Authentifizierungsart darstellt.
package authentication.strategy;

// Credential enthält den Typ und den Wert des eingegebenen Fingerabdrucks.
import authentication.Credential;
// CredentialType wird benötigt, damit diese Strategie nur Fingerabdruck-Credentials akzeptiert.
import authentication.CredentialType;
// Subject enthält die bereits gespeicherten Credentials, mit denen die Eingabe verglichen wird.
import authentication.Subject;

// Diese konkrete Strategie führt die Authentifizierung mit einem Fingerabdruck durch.
// "implements" verbindet die Klasse mit dem gemeinsamen Vertrag von AuthenticationStrategy.
public class FingerPrintStrategy implements AuthenticationStrategy {

    // Override kennzeichnet die Umsetzung der im Strategy-Interface vorgegebenen Methode.
    @Override
    public boolean authenticate(Subject subject, Credential credential) {
        // Ohne Subject oder Credential fehlen die notwendigen Daten für eine Authentifizierung.
        if (subject == null || credential == null) {
            return false;
        }

        // Die Strategie lehnt alle Eingaben ab, die keinen Fingerabdruck repräsentieren.
        if (credential.getCredentialType() != CredentialType.FINGERPRINT) {
            return false;
        }

        // Das Subject vergleicht den eingegebenen Fingerabdruck mit seinen gespeicherten Credentials.
        return subject.hasMatchingCredential(credential);
    }
}
