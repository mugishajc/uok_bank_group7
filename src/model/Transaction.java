package model;

public class Transaction {
    private final int    id;
    private final String senderPhone;
    private final String receiverPhone;
    private final String type;
    private final double amount;
    private final String timestamp;
    private final String note;

    public Transaction(int id, String senderPhone, String receiverPhone,
                       String type, double amount, String timestamp, String note) {
        this.id            = id;
        this.senderPhone   = senderPhone;
        this.receiverPhone = receiverPhone;
        this.type          = type;
        this.amount        = amount;
        this.timestamp     = timestamp;
        this.note          = note;
    }

    public int    getId()               { return id; }
    public String getSenderPhone()      { return senderPhone; }
    public String getReceiverPhone()    { return receiverPhone; }
    public String getType()             { return type; }
    public double getAmount()           { return amount; }
    public String getTimestamp()        { return timestamp; }
    public String getNote()             { return note; }
}
