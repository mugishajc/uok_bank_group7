package service;

/**
 * Pure-function business rules for UoK Bank.
 * No database access — every method is fully unit-testable.
 *
 * All monetary values are in Rwandan Francs (FRW).
 */
public final class BankingRules {

    // ── Monetary limits ────────────────────────────────────────────────────
    public static final double MIN_AMOUNT        =         1.0;   // FRW
    public static final double MAX_SINGLE_TRANSFER = 5_000_000.0; // FRW per transaction
    public static final double MAX_LOAN_AMOUNT   = 10_000_000.0; // FRW

    // ── Security ───────────────────────────────────────────────────────────
    public static final int    PIN_LENGTH         = 5;
    public static final String RWANDAN_PHONE_REGEX = "07\\d{8}";

    private BankingRules() { /* no instances */ }

    // ── Validation helpers ─────────────────────────────────────────────────

    /** Returns true if phone is a valid Rwandan mobile number (07XXXXXXXX). */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches(RWANDAN_PHONE_REGEX);
    }

    /** Returns true if pin is exactly PIN_LENGTH digits. */
    public static boolean isValidPin(String pin) {
        return pin != null && pin.length() == PIN_LENGTH && pin.matches("\\d+");
    }

    /** Returns true if amount is positive, finite, and above the minimum. */
    public static boolean isValidAmount(double amount) {
        return Double.isFinite(amount) && amount >= MIN_AMOUNT;
    }

    /** Returns true if the balance covers the requested amount. */
    public static boolean hasSufficientBalance(double balance, double required) {
        return balance >= required;
    }

    /** Returns true when sender and receiver are the same account (blocked). */
    public static boolean isSelfTransfer(String fromPhone, String toPhone) {
        return fromPhone != null && fromPhone.equals(toPhone);
    }

    /** Returns true if the transfer amount exceeds the single-transaction limit. */
    public static boolean exceedsTransferLimit(double amount) {
        return amount > MAX_SINGLE_TRANSFER;
    }

    /** Returns true if the loan amount exceeds the maximum allowed. */
    public static boolean exceedsLoanLimit(double amount) {
        return amount > MAX_LOAN_AMOUNT;
    }

    /**
     * Returns a human-readable validation error for a transfer, or null if valid.
     * Checks: amount range, balance, self-transfer.
     */
    public static String validateTransfer(String fromPhone, String toPhone,
                                          double balance, double amount) {
        if (!isValidAmount(amount))            return "Amount must be at least FRW 1.";
        if (exceedsTransferLimit(amount))      return "Single transfer limit is FRW 5,000,000.";
        if (isSelfTransfer(fromPhone, toPhone)) return "Cannot transfer to your own account.";
        if (!hasSufficientBalance(balance, amount))
            return "Insufficient balance.";
        return null; // valid
    }

    /**
     * Returns a human-readable validation error for a withdrawal, or null if valid.
     */
    public static String validateWithdrawal(double balance, double amount) {
        if (!isValidAmount(amount))                    return "Amount must be at least FRW 1.";
        if (!hasSufficientBalance(balance, amount))    return "Insufficient balance.";
        return null;
    }
}
