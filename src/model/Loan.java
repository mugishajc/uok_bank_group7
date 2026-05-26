package model;

public class Loan {
    private final int    id;
    private final String phone;
    private final double amount;
    private       String status;
    private final String requestedAt;

    public Loan(int id, String phone, double amount, String status, String requestedAt) {
        this.id          = id;
        this.phone       = phone;
        this.amount      = amount;
        this.status      = status;
        this.requestedAt = requestedAt;
    }

    public int    getId()            { return id; }
    public String getPhone()         { return phone; }
    public double getAmount()        { return amount; }
    public String getStatus()        { return status; }
    public String getRequestedAt()   { return requestedAt; }
    public void   setStatus(String s){ this.status = s; }
}
