import dao.AccountDAO;
import dao.TransactionDAO;
import model.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for TransactionDAO.
 */
public class TransactionDAOTest {

    private TransactionDAO txnDao;
    private AccountDAO     accDao;

    @Before
    public void setUp() throws Exception {
        TestHelper.setUp();
        txnDao = new TransactionDAO();
        accDao = new AccountDAO();
        // Create two accounts used across tests
        accDao.create("0782000001", "Sender Account",   "11111", "MOMO");
        accDao.create("0782000002", "Receiver Account", "22222", "MOMO");
    }

    @After
    public void tearDown() {
        TestHelper.tearDown();
    }

    // ── record ─────────────────────────────────────────────────────────────

    @Test
    public void record_deposit_persistsCorrectly() {
        txnDao.record(null, "0782000001", "DEPOSIT", 50_000.0, "Cash deposit");
        List<Transaction> list = txnDao.getByPhone("0782000001");
        assertEquals(1, list.size());
        Transaction t = list.get(0);
        assertEquals("DEPOSIT",    t.getType());
        assertEquals(50_000.0,     t.getAmount(), 0.001);
        assertNull(t.getSenderPhone());
        assertEquals("0782000001", t.getReceiverPhone());
        assertEquals("Cash deposit", t.getNote());
    }

    @Test
    public void record_transfer_persistsSenderAndReceiver() {
        txnDao.record("0782000001", "0782000002", "TRANSFER", 20_000.0, "Rent");
        List<Transaction> senderTxns   = txnDao.getByPhone("0782000001");
        List<Transaction> receiverTxns = txnDao.getByPhone("0782000002");
        assertEquals(1, senderTxns.size());
        assertEquals(1, receiverTxns.size());
        assertEquals("0782000001", senderTxns.get(0).getSenderPhone());
        assertEquals("0782000002", senderTxns.get(0).getReceiverPhone());
    }

    @Test
    public void record_withdrawal_persistsCorrectly() {
        txnDao.record("0782000001", null, "WITHDRAW", 10_000.0, "Cash withdrawal");
        List<Transaction> list = txnDao.getByPhone("0782000001");
        assertEquals(1, list.size());
        assertEquals("WITHDRAW", list.get(0).getType());
        assertNull(list.get(0).getReceiverPhone());
    }

    @Test
    public void record_timestampIsPopulated() {
        txnDao.record(null, "0782000001", "DEPOSIT", 1_000.0, null);
        List<Transaction> list = txnDao.getByPhone("0782000001");
        assertNotNull(list.get(0).getTimestamp());
        assertFalse(list.get(0).getTimestamp().isEmpty());
    }

    // ── getByPhone ─────────────────────────────────────────────────────────

    @Test
    public void getByPhone_returnsOnlyMatchingAccount() {
        txnDao.record(null,         "0782000001", "DEPOSIT",  5_000.0, null);
        txnDao.record("0782000002", null,         "WITHDRAW", 1_000.0, null);

        List<Transaction> forSender = txnDao.getByPhone("0782000001");
        assertEquals("Only one transaction belongs to account 001", 1, forSender.size());
    }

    @Test
    public void getByPhone_returnsBothSentAndReceived() {
        txnDao.record("0782000001", "0782000002", "TRANSFER", 3_000.0, null);
        txnDao.record(null,         "0782000001", "DEPOSIT",  2_000.0, null);
        List<Transaction> all = txnDao.getByPhone("0782000001");
        assertEquals(2, all.size()); // 1 as sender + 1 as receiver
    }

    @Test
    public void getByPhone_noTransactions_returnsEmpty() {
        List<Transaction> list = txnDao.getByPhone("0782000001");
        assertTrue(list.isEmpty());
    }

    @Test
    public void getByPhone_orderedNewestFirst() throws InterruptedException {
        txnDao.record(null, "0782000001", "DEPOSIT", 1_000.0, "first");
        Thread.sleep(1001); // ensure distinct timestamps (1-second precision)
        txnDao.record(null, "0782000001", "DEPOSIT", 2_000.0, "second");
        List<Transaction> list = txnDao.getByPhone("0782000001");
        assertEquals("second", list.get(0).getNote()); // newest first
        assertEquals("first",  list.get(1).getNote());
    }

    // ── getAll ─────────────────────────────────────────────────────────────

    @Test
    public void getAll_returnsAllTransactions() {
        txnDao.record(null, "0782000001", "DEPOSIT",  5_000.0, null);
        txnDao.record("0782000001", "0782000002", "TRANSFER", 1_000.0, null);
        txnDao.record("0782000002", null, "WITHDRAW", 500.0, null);
        List<Transaction> all = txnDao.getAll();
        assertEquals(3, all.size());
    }
}
