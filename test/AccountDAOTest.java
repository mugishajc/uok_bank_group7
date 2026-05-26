import dao.AccountDAO;
import model.Account;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for AccountDAO using an isolated in-memory-equivalent
 * SQLite database (temp file, cleaned up after each test).
 */
public class AccountDAOTest {

    private AccountDAO dao;

    @Before
    public void setUp() throws Exception {
        TestHelper.setUp();
        dao = new AccountDAO();
    }

    @After
    public void tearDown() {
        TestHelper.tearDown();
    }

    // ── create ─────────────────────────────────────────────────────────────

    @Test
    public void createAccount_succeeds() {
        boolean created = dao.create("0781000001", "Alice Uwase", "12345", "MOMO");
        assertTrue("Account creation should succeed", created);
    }

    @Test
    public void createAccount_duplicatePhone_fails() {
        dao.create("0781000002", "Bob Nkurunziza", "11111", "SAVINGS");
        boolean second = dao.create("0781000002", "Another Person", "22222", "MOMO");
        assertFalse("Duplicate phone must be rejected", second);
    }

    @Test
    public void createAccount_defaultBalanceIsZero() {
        dao.create("0781000003", "Claire Mukarukundo", "55555", "CURRENT");
        Account a = dao.findByPhone("0781000003");
        assertNotNull(a);
        assertEquals(0.0, a.getBalance(), 0.001);
    }

    // ── findByPhone ────────────────────────────────────────────────────────

    @Test
    public void findByPhone_existingAccount_returnsAccount() {
        dao.create("0781000004", "Denis Hakizimana", "33333", "MOMO");
        Account a = dao.findByPhone("0781000004");
        assertNotNull(a);
        assertEquals("Denis Hakizimana", a.getFullName());
        assertEquals("MOMO", a.getAccountType());
        assertFalse(a.isFrozen());
        assertEquals("USER", a.getRole());
    }

    @Test
    public void findByPhone_nonExistentPhone_returnsNull() {
        Account a = dao.findByPhone("0799999999");
        assertNull("Non-existent phone must return null", a);
    }

    // ── updateBalance ──────────────────────────────────────────────────────

    @Test
    public void updateBalance_setsNewBalance() {
        dao.create("0781000005", "Elise Ingabire", "44444", "SAVINGS");
        dao.updateBalance("0781000005", 150_000.0);
        Account a = dao.findByPhone("0781000005");
        assertNotNull(a);
        assertEquals(150_000.0, a.getBalance(), 0.001);
    }

    @Test
    public void updateBalance_canSetToZero() {
        dao.create("0781000006", "Frank Nzeyimana", "66666", "MOMO");
        dao.updateBalance("0781000006", 50_000.0);
        dao.updateBalance("0781000006", 0.0);
        Account a = dao.findByPhone("0781000006");
        assertNotNull(a);
        assertEquals(0.0, a.getBalance(), 0.001);
    }

    // ── setFrozen ──────────────────────────────────────────────────────────

    @Test
    public void setFrozen_freezesAccount() {
        dao.create("0781000007", "Grace Uwimana", "77777", "MOMO");
        dao.setFrozen("0781000007", true);
        Account a = dao.findByPhone("0781000007");
        assertNotNull(a);
        assertTrue("Account should be frozen", a.isFrozen());
    }

    @Test
    public void setFrozen_unfreezes() {
        dao.create("0781000008", "Henri Turihamwe", "88888", "SAVINGS");
        dao.setFrozen("0781000008", true);
        dao.setFrozen("0781000008", false);
        Account a = dao.findByPhone("0781000008");
        assertNotNull(a);
        assertFalse("Account should be unfrozen", a.isFrozen());
    }

    // ── getAll ─────────────────────────────────────────────────────────────

    @Test
    public void getAll_returnsAllAccounts() {
        dao.create("0781000009", "Isabelle Nyiraneza", "11111", "MOMO");
        dao.create("0781000010", "Jean-Paul Habimana", "22222", "CURRENT");
        List<Account> all = dao.getAll();
        assertEquals(2, all.size());
    }

    @Test
    public void getAll_sortedByFullName() {
        dao.create("0781000011", "Zoe Mutoni",    "11111", "MOMO");
        dao.create("0781000012", "Alice Uwimana", "22222", "MOMO");
        List<Account> all = dao.getAll();
        assertEquals(2, all.size());
        assertEquals("Alice Uwimana", all.get(0).getFullName());
        assertEquals("Zoe Mutoni",    all.get(1).getFullName());
    }
}
