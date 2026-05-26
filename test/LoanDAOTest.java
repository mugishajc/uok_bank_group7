import dao.AccountDAO;
import dao.LoanDAO;
import model.Loan;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for LoanDAO.
 */
public class LoanDAOTest {

    private LoanDAO    loanDao;
    private AccountDAO accDao;

    @Before
    public void setUp() throws Exception {
        TestHelper.setUp();
        loanDao = new LoanDAO();
        accDao  = new AccountDAO();
        accDao.create("0783000001", "Loan Applicant", "11111", "MOMO");
    }

    @After
    public void tearDown() {
        TestHelper.tearDown();
    }

    // ── request ────────────────────────────────────────────────────────────

    @Test
    public void request_loanCreatedWithPendingStatus() {
        boolean ok = loanDao.request("0783000001", 500_000.0);
        assertTrue("Loan request should succeed", ok);

        List<Loan> loans = loanDao.getByPhone("0783000001");
        assertEquals(1, loans.size());
        assertEquals("PENDING",   loans.get(0).getStatus());
        assertEquals(500_000.0,   loans.get(0).getAmount(), 0.001);
        assertEquals("0783000001", loans.get(0).getPhone());
    }

    @Test
    public void request_timestampIsPopulated() {
        loanDao.request("0783000001", 100_000.0);
        List<Loan> loans = loanDao.getByPhone("0783000001");
        assertNotNull(loans.get(0).getRequestedAt());
        assertFalse(loans.get(0).getRequestedAt().isEmpty());
    }

    @Test
    public void request_multipleLoansAllowed() {
        loanDao.request("0783000001", 100_000.0);
        loanDao.request("0783000001", 200_000.0);
        List<Loan> loans = loanDao.getByPhone("0783000001");
        assertEquals(2, loans.size());
    }

    // ── updateStatus ───────────────────────────────────────────────────────

    @Test
    public void updateStatus_toApproved() {
        loanDao.request("0783000001", 750_000.0);
        int loanId = loanDao.getByPhone("0783000001").get(0).getId();
        boolean ok = loanDao.updateStatus(loanId, "APPROVED");
        assertTrue(ok);
        assertEquals("APPROVED", loanDao.getByPhone("0783000001").get(0).getStatus());
    }

    @Test
    public void updateStatus_toRejected() {
        loanDao.request("0783000001", 300_000.0);
        int loanId = loanDao.getByPhone("0783000001").get(0).getId();
        loanDao.updateStatus(loanId, "REJECTED");
        assertEquals("REJECTED", loanDao.getByPhone("0783000001").get(0).getStatus());
    }

    @Test
    public void updateStatus_toRepaid() {
        loanDao.request("0783000001", 1_000_000.0);
        int loanId = loanDao.getByPhone("0783000001").get(0).getId();
        loanDao.updateStatus(loanId, "APPROVED");
        loanDao.updateStatus(loanId, "REPAID");
        assertEquals("REPAID", loanDao.getByPhone("0783000001").get(0).getStatus());
    }

    // ── getByPhone ─────────────────────────────────────────────────────────

    @Test
    public void getByPhone_noLoans_returnsEmpty() {
        accDao.create("0783000002", "No Loan User", "22222", "SAVINGS");
        assertTrue(loanDao.getByPhone("0783000002").isEmpty());
    }

    @Test
    public void getByPhone_orderedNewestFirst() throws InterruptedException {
        loanDao.request("0783000001", 100_000.0);
        Thread.sleep(1001);
        loanDao.request("0783000001", 200_000.0);
        List<Loan> loans = loanDao.getByPhone("0783000001");
        assertTrue("Newest loan should appear first",
            loans.get(0).getAmount() > loans.get(1).getAmount());
    }

    // ── getAll ─────────────────────────────────────────────────────────────

    @Test
    public void getAll_returnsAllLoansAcrossAccounts() {
        accDao.create("0783000003", "Second User", "33333", "MOMO");
        loanDao.request("0783000001", 100_000.0);
        loanDao.request("0783000003", 200_000.0);
        List<Loan> all = loanDao.getAll();
        assertEquals(2, all.size());
    }
}
