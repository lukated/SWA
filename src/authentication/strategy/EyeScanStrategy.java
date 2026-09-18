// Die Klasse liegt im strategy-Paket, weil sie eine konkrete austauschbare Authentifizierungsart darstellt.
package authentication.strategy;

// Credential enthält den Typ und den Wert des eingegebenen Iris-Scans.
import authentication.Credential;
// CredentialType wird benötigt, damit diese Strategie nur Iris-Scan-Credentials akzeptiert.
import authentication.CredentialType;
// Subject enthält die bereits gespeicherten Credentials, mit denen die Eingabe verglichen wird.
import authentication.Subject;

// Diese konkrete Strategie führt die Authentifizierung mit einem Iris- beziehungsweise Augen-Scan durch.
// "implements" verbindet die Klasse mit dem gemeinsamen Vertrag von AuthenticationStrategy.
public class EyeScanStrategy implements AuthenticationStrategy {

    // Override kennzeichnet die Umsetzung der im Strategy-Interface vorgegebenen Methode.
    @Override
    public boolean authenticate(Subject subject, Credential credential) {
        // Ohne Subject oder Credential fehlen die notwendigen Daten für eine Authentifizierung.
        if (subject == null || credential == null) {
            return false;
        }

        // Die Strategie lehnt alle Eingaben ab, die keinen Iris-Scan repräsentieren.
        if (credential.getCredentialType() != CredentialType.IRIS_SCAN) {
            return false;
        }

        // Das Subject vergleicht den eingegebenen Iris-Scan mit seinen gespeicherten Credentials.
        return subject.hasMatchingCredential(credential);
    }
}
