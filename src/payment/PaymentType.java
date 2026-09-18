// Das Enum gehört zum payment-Paket, weil es die auswählbaren Online-Zahlungsarten beschreibt.
package payment;

// Ein Enum begrenzt die Zahlungsart auf bekannte, typsichere Werte und verhindert freie fehlerhafte Texteingaben.
public enum PaymentType {
    // PAYPAL wählt die konkrete PayPalPayment-Implementierung aus.
    PAYPAL,
    // GOOGLE_WALLET wählt die konkrete GoogleWalletPayment-Implementierung aus.
    GOOGLE_WALLET,
    // APPLE_PAY ist die von der Gruppe bestätigte dritte Zahlungsart und wählt ApplePayPayment aus.
    APPLE_PAY
}
