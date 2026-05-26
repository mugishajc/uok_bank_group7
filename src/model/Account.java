package model;

public class Account {
    private final int    id;
    private final String phone;
    private final String fullName;
    private final String pin;
    private final String accountType;
    private       double balance;
    private final String role;
    private       boolean frozen;

    public Account(int id, String phone, String fullName, String pin,
                   String accountType, double balance, String role, boolean frozen) {
        this.id          = id;
        this.phone       = phone;
        this.fullName    = fullName;
        this.pin         = pin;
        this.accountType = accountType;
        this.balance     = balance;
        this.role        = role;
        this.frozen      = frozen;
    }

    public int     getId()            { return id; }
    public String  getPhone()         { return phone; }
    public String  getFullName()      { return fullName; }
    public String  getPin()           { return pin; }
    public String  getAccountType()   { return accountType; }
    public double  getBalance()       { return balance; }
    public String  getRole()          { return role; }
    public boolean isFrozen()         { return frozen; }

    public void setBalance(double b)  { this.balance = b; }
    public void setFrozen(boolean f)  { this.frozen  = f; }

    @Override public String toString() { return fullName + " (" + phone + ")"; }
}
