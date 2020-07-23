package Controller;

import Model.Account;
import Model.Receipt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class DataBase {
    private static DataBase dataBase;
    private DataBase(){}
    public static DataBase getInstance(){
        if(dataBase==null) dataBase=new DataBase();
        return dataBase;
    }

    public void saveReceipt(Receipt receipt){
        Gson gson=new GsonBuilder().setPrettyPrinting().create();
        String dataToSave=gson.toJson(receipt);
        String path="src/main/resources/Receipts/"+receipt.getId()+".gson";
        File file=new File(path);
        if(file.exists()==false) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter=new FileWriter(file);
            fileWriter.write(dataToSave);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String srcId=receipt.getSrcId();
        String desId=receipt.getDesId();
        if(srcId.equals("-1")==false){
            Account account=getAccountById(srcId);
            if(receipt.isPaid()==false)account.addNotPaidReceipt(receipt.getId());
            else if(receipt.isPaid()==true) account.addPaidReceipt(receipt.getId());
            if(account!=null) saveAccount(account);
        }
        if(desId.equals("-1")==false){
            Account account=getAccountById(desId);
            if(receipt.isPaid()==false)account.addNotPaidReceipt(receipt.getId());
            else if(receipt.isPaid()==true) account.addPaidReceipt(receipt.getId());
            if(account!=null) saveAccount(account);
        }
    }

    public void saveAccount(Account account){
        Gson gson=new GsonBuilder().setPrettyPrinting().create();
        String dataToSave=gson.toJson(account);
        String path="src/main/resources/Accounts/"+account.getUsername()+".gson";
        File file=new File(path);
        if(file.exists()==false) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter=new FileWriter(file);
            fileWriter.write(dataToSave);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Receipt getReceiptById(String id){
        if(isThereReceiptWithId(id)==false) return null;
        String path="src/main/resources/Receipts/"+id+".gson";
        File file=new File(path);
        if(file.exists()==false) return null;
        try {
            String content=new String(Files.readAllBytes(file.toPath()));
            Gson gson=new Gson();
            Receipt receipt=gson.fromJson(content,Receipt.class);
            return receipt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account getAccountByUsername(String username){
        if(isThereAccountWithUsername(username)==false) return null;
        String path="src/main/resources/Accounts/"+username+".gson";
        File file=new File(path);
        if(file.exists()==false) return null;
        try {
            String content=new String(Files.readAllBytes(file.toPath()));
            Gson gson=new Gson();
            Account account=gson.fromJson(content,Account.class);
            return account;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Account> getAllAccountsFromDataBase(){
        String path="src/main/resources/Accounts/";
        File file=new File(path);
        File [] allFiles=file.listFiles();
        Gson gson=new Gson();
        String content=null;
        ArrayList<Account> allAccounts=new ArrayList<>();
        for(File file1:allFiles){
            try {
                content=new String(Files.readAllBytes(file1.toPath()));
                allAccounts.add(gson.fromJson(content,Account.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return allAccounts;
    }

    public ArrayList<Receipt> getAllReceiptsFromDataBase(){
        String path="src/main/resources/Receipts/";
        File file=new File(path);
        File [] allFiles=file.listFiles();
        String content=null;
        Gson gson=new Gson();
        ArrayList<Receipt> allReceipts=new ArrayList<>();
        for(File file1:allFiles){
            try {
                content=new String(Files.readAllBytes(file1.toPath()));
                allReceipts.add(gson.fromJson(content,Receipt.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return allReceipts;
    }

    public boolean isThereAccountWithUsername(String username){
        String path="src/main/resources/Accounts/"+username+".gson";
        File file=new File(path);
        if(file.exists()) return true;
        return false;
    }

    public boolean isThereReceiptWithId(String id){
        String path="src/main/resources/Receipts/"+id+".gson";
        File file=new File(path);
        if(file.exists()) return true;
        return false;
    }

    public boolean isThereAccountWithId(String id){
        ArrayList<Account> allAccounts=getAllAccountsFromDataBase();
        for(Account account:allAccounts){
            if(account.getId().equals(id)) return true;
        }
        return false;
    }

    public Account getAccountById(String id){
        ArrayList<Account> allAccounts=getAllAccountsFromDataBase();
        for(Account account:allAccounts){
            if(account.getId().equals(id)) return account;
        }
        return null;
    }




}
