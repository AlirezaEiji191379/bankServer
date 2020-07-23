package Model;

import Controller.RequestController;

import java.util.ArrayList;
import java.util.HashMap;

public class Account {
    private ArrayList<String> allPaidReceipt;
    private ArrayList<String> allNotPaidReceipt;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String id;
    private int money;

    public Account(String firstName,String lastName,String username,String password) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id= RequestController.getInstance().generateIdForAccount();
        allNotPaidReceipt=new ArrayList<>();
        allPaidReceipt=new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public void addNotPaidReceipt(String receiptId){
        allNotPaidReceipt.add(receiptId);
    }

    public void addPaidReceipt(String receiptId){
        allPaidReceipt.add(receiptId);
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public ArrayList<String> getAllPaidReceipt(){return allPaidReceipt;}

    public ArrayList<String> getAllNotPaidReceipt() {
        return allNotPaidReceipt;
    }
}
