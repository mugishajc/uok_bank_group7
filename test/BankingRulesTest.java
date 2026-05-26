import org.junit.Test;
import service.BankingRules;

import static org.junit.Assert.*;

/**
 * Pure unit tests for BankingRules — no database, no I/O.
 * Every test runs in microseconds.
 */
public class BankingRulesTest {

    // ── isValidPhone ────────────────────────────────────────────────────────

    @Test public void validPhone_standard()        { assertTrue(BankingRules.isValidPhone("0781234567")); }
    @Test public void validPhone_allProviders()    { assertTrue(BankingRules.isValidPhone("0721234567")); }
    @Test public void invalidPhone_wrongPrefix()   { assertFalse(BankingRules.isValidPhone("0881234567")); }
    @Test public void invalidPhone_tooShort()      { assertFalse(BankingRules.isValidPhone("078123456")); }
    @Test public void invalidPhone_tooLong()       { assertFalse(BankingRules.isValidPhone("07812345678")); }
    @Test public void invalidPhone_containsLetters(){ assertFalse(BankingRules.isValidPhone("078123456A")); }
    @Test public void invalidPhone_null()           { assertFalse(BankingRules.isValidPhone(null)); }
    @Test public void invalidPhone_empty()          { assertFalse(BankingRules.isValidPhone("")); }

    // ── isValidPin ──────────────────────────────────────────────────────────

    @Test public void validPin_fiveDigits()        { assertTrue(BankingRules.isValidPin("12345")); }
    @Test public void validPin_allZeros()          { assertTrue(BankingRules.isValidPin("00000")); }
    @Test public void invalidPin_fourDigits()      { assertFalse(BankingRules.isValidPin("1234")); }
    @Test public void invalidPin_sixDigits()       { assertFalse(BankingRules.isValidPin("123456")); }
    @Test public void invalidPin_hasLetters()      { assertFalse(BankingRules.isValidPin("1234A")); }
    @Test public void invalidPin_null()            { assertFalse(BankingRules.isValidPin(null)); }
    @Test public void invalidPin_empty()           { assertFalse(BankingRules.isValidPin("")); }

    // ── isValidAmount ───────────────────────────────────────────────────────

    @Test public void validAmount_one()            { assertTrue(BankingRules.isValidAmount(1.0)); }
    @Test public void validAmount_large()          { assertTrue(BankingRules.isValidAmount(4_999_999.0)); }
    @Test public void invalidAmount_zero()         { assertFalse(BankingRules.isValidAmount(0.0)); }
    @Test public void invalidAmount_negative()     { assertFalse(BankingRules.isValidAmount(-100.0)); }
    @Test public void invalidAmount_nan()          { assertFalse(BankingRules.isValidAmount(Double.NaN)); }
    @Test public void invalidAmount_infinity()     { assertFalse(BankingRules.isValidAmount(Double.POSITIVE_INFINITY)); }

    // ── hasSufficientBalance ────────────────────────────────────────────────

    @Test public void sufficientBalance_exact()    { assertTrue(BankingRules.hasSufficientBalance(100.0, 100.0)); }
    @Test public void sufficientBalance_more()     { assertTrue(BankingRules.hasSufficientBalance(200.0, 100.0)); }
    @Test public void insufficientBalance()        { assertFalse(BankingRules.hasSufficientBalance(99.0, 100.0)); }
    @Test public void insufficientBalance_zero()   { assertFalse(BankingRules.hasSufficientBalance(0.0, 1.0)); }

    // ── isSelfTransfer ──────────────────────────────────────────────────────

    @Test public void selfTransfer_samePhone()     { assertTrue(BankingRules.isSelfTransfer("0781234567", "0781234567")); }
    @Test public void notSelfTransfer_diffPhone()  { assertFalse(BankingRules.isSelfTransfer("0781234567", "0787654321")); }
    @Test public void notSelfTransfer_nullFrom()   { assertFalse(BankingRules.isSelfTransfer(null, "0781234567")); }

    // ── exceedsTransferLimit ────────────────────────────────────────────────

    @Test public void withinTransferLimit()        { assertFalse(BankingRules.exceedsTransferLimit(5_000_000.0)); }
    @Test public void exceedsTransferLimit()       { assertTrue(BankingRules.exceedsTransferLimit(5_000_001.0)); }

    // ── exceedsLoanLimit ────────────────────────────────────────────────────

    @Test public void withinLoanLimit()            { assertFalse(BankingRules.exceedsLoanLimit(10_000_000.0)); }
    @Test public void exceedsLoanLimit()           { assertTrue(BankingRules.exceedsLoanLimit(10_000_001.0)); }

    // ── validateTransfer ────────────────────────────────────────────────────

    @Test
    public void validateTransfer_valid_returnsNull() {
        assertNull(BankingRules.validateTransfer(
            "0781000001", "0781000002", 100_000.0, 50_000.0));
    }

    @Test
    public void validateTransfer_selfTransfer_returnsError() {
        String err = BankingRules.validateTransfer(
            "0781000001", "0781000001", 100_000.0, 50_000.0);
        assertNotNull(err);
        assertTrue(err.toLowerCase().contains("own"));
    }

    @Test
    public void validateTransfer_insufficientBalance_returnsError() {
        String err = BankingRules.validateTransfer(
            "0781000001", "0781000002", 10_000.0, 50_000.0);
        assertNotNull(err);
        assertTrue(err.toLowerCase().contains("insufficient"));
    }

    @Test
    public void validateTransfer_zeroAmount_returnsError() {
        String err = BankingRules.validateTransfer(
            "0781000001", "0781000002", 100_000.0, 0.0);
        assertNotNull(err);
    }

    @Test
    public void validateTransfer_exceedsLimit_returnsError() {
        String err = BankingRules.validateTransfer(
            "0781000001", "0781000002", 10_000_000.0, 6_000_000.0);
        assertNotNull(err);
        assertTrue(err.toLowerCase().contains("limit"));
    }

    // ── validateWithdrawal ──────────────────────────────────────────────────

    @Test
    public void validateWithdrawal_valid_returnsNull() {
        assertNull(BankingRules.validateWithdrawal(50_000.0, 20_000.0));
    }

    @Test
    public void validateWithdrawal_insufficient_returnsError() {
        String err = BankingRules.validateWithdrawal(10_000.0, 20_000.0);
        assertNotNull(err);
        assertTrue(err.toLowerCase().contains("insufficient"));
    }

    @Test
    public void validateWithdrawal_zeroAmount_returnsError() {
        assertNotNull(BankingRules.validateWithdrawal(50_000.0, 0.0));
    }
}
