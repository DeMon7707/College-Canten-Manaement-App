package com.example.lenovo.cafe_canteen.Model;

public class User {
    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;
    private double balance;

    public User()
    {

    }

    public User(String name,String password)
    {
        Name=name;
        Password=password;
        IsStaff="false";
        balance=00.00;
    }

    //Press Alt+Insert

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

}
