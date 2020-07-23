package Controller;

import Model.Account;
import Model.Receipt;
import com.google.gson.Gson;

import java.security.SecureRandom;
import java.util.ArrayList;

public class RequestController {
    private final SecureRandom secureRandom=new SecureRandom();
    private static RequestController requestController;
    private RequestController(){}
    public static RequestController getInstance(){
        if(requestController==null)
            requestController=new RequestController();
        return requestController;
    }

    /// Account creation
    public String createAccount(String command){
        String[]token=command.split(" ");
        if(token.length!=6) return "invalid input";
        String firstName=token[1];
        String lastName=token[2];
        String username=token[3];
        String password=token[4];
        String repeatPassword=token[5];
        if(password.equals(repeatPassword)==false){
            return "passwords do not match";
        }
        if(DataBase.getInstance().isThereAccountWithUsername(username)==true){
            return "username is not available";
        }
        Account account=new Account(firstName,lastName,username,password);
        DataBase.getInstance().saveAccount(account);
        return account.getId();
    }

    /// bank Token getting
    public String getToken(String command){
        String [] token=command.split(" ");
        if(token.length!=3) return "invalid input";
        String username=token[1];
        String password=token[2];
        if(DataBase.getInstance().isThereAccountWithUsername(username)==false){
            return "invalid username or password";
        }
        Account account=DataBase.getInstance().getAccountByUsername(username);
        if(account.getPassword().equals(password)==false){
            return "invalid username or password";
        }
        String userToken=AuthTokenController.getInstance().getTokenForClient(username);
        return userToken;
    }

    //// receipt creation
    public String createReceipt(String command){
        String [] token=command.split(" ");
        if(token.length!=7 && token.length!=6) return "invalid input";
        ///if(command.contains("\\*") || command.contains("+")) return "your input contains invalid characters";
        String receiptType=token[2];
        if((receiptType.equals("deposit")==false) && (receiptType.equals("withdraw")==false) &&
                (receiptType.equals("move")==false)){
            return "invalid receipt type";
        }
        String money=token[3];
        if(money.matches("\\d+")==false) return "invalid money";
        if(Integer.parseInt(money)<0) return "invalid money";
        String bankToken=token[1];
        String username=AuthTokenController.getInstance().getUsernameByToken(bankToken);
        if(username.equals("token expired") || username.equals("token is invalid")) return username;
        String srcId=token[4];
        String desId=token[5];
        if(receiptType.equals("deposit")){
            if(srcId.equals("-1")==false) return "invalid parameters passed";
            if(desId.equals("-1")) return "invalid account id";
            if(DataBase.getInstance().isThereAccountWithId(desId)==false) return "dest account id is invalid";
        }
        else if(receiptType.equals("withdraw")){
            if(desId.equals("-1")==false) return "invalid parameters passed";
            if(srcId.equals("-1")) return "invalid account id";
            if(DataBase.getInstance().isThereAccountWithId(srcId)==false) return "source account id is invalid";
            if(DataBase.getInstance().getAccountByUsername(username).getId().equals(srcId)==false) return "invalid token";

        }
        else if(receiptType.equals("move")){
            if(srcId.equals("-1") || desId.equals("-1")) return "invalid account id";
            if(DataBase.getInstance().isThereAccountWithId(srcId)==false) return "source account id is invalid";
            if(DataBase.getInstance().isThereAccountWithId(desId)==false) return "dest account id is invalid";
            if(desId.equals(srcId)) return "equal source and dest account";
            if(DataBase.getInstance().getAccountByUsername(username).getId().equals(srcId)==false) return "invalid token";
        }
        int price=Integer.parseInt(money);
        Receipt receipt=new Receipt(bankToken,receiptType,price,srcId,desId);
        if(token.length==7){
            String description=token[6];
            receipt.setDescription(description);
        }
        DataBase.getInstance().saveReceipt(receipt);
        return receipt.getId();
    }

    public String payReceipt(String command){
        String [] token=command.split(" ");
        if(token.length!=2) return "invalid input";
        String receiptId=token[1];
        if(DataBase.getInstance().isThereReceiptWithId(receiptId)==false) return "invalid receipt id";
        Receipt receipt=DataBase.getInstance().getReceiptById(receiptId);
        String srcId=receipt.getSrcId();
        String desId=receipt.getDesId();
        if(!srcId.equals("-1") && DataBase.getInstance().isThereAccountWithId(srcId)==false) return "invalid account id";
        if(!desId.equals("-1") && DataBase.getInstance().isThereAccountWithId(desId)==false) return "invalid account id";
        if(receipt.isPaid()==true) return "receipt is paid before";
        if(srcId.equals("-1")==false){
            Account account=DataBase.getInstance().getAccountById(srcId);
            if(account.getMoney() < receipt.getMoney()) return "source account does not have enough money";
            account.setMoney(account.getMoney()-receipt.getMoney());
            if(desId.equals("-1")==false){
                Account account1=DataBase.getInstance().getAccountById(desId);
                account1.setMoney(account1.getMoney()+receipt.getMoney());
                DataBase.getInstance().saveAccount(account1);
            }
            DataBase.getInstance().saveAccount(account);
            receipt.setPaid(true);
            DataBase.getInstance().saveReceipt(receipt);
            return "done successfully";
        }
        else if(srcId.equals("-1")){
            Account account=DataBase.getInstance().getAccountById(desId);
            account.setMoney(account.getMoney()+receipt.getMoney());
            receipt.setPaid(true);
            DataBase.getInstance().saveAccount(account);
            DataBase.getInstance().saveReceipt(receipt);
            return "done successfully";
        }
        return null;
    }

    public String getBalance(String command){
        String [] token=command.split(" ");
        if(token.length!=2) return "invalid input";
        String bankToken=token[1];
        String username=AuthTokenController.getInstance().getUsernameByToken(bankToken);
        if(username.equals("token expired") || username.equals("token is invalid")) return username;
        Account account=DataBase.getInstance().getAccountByUsername(username);
        return String.valueOf(account.getMoney());
    }

    public String getTransaction(String command){
        String [] token=command.split(" ");
        if(token.length!=3) return "invalid input";
        String bankToken=token[1];
        String type=token[2];
        String username=AuthTokenController.getInstance().getUsernameByToken(bankToken);
        if(username.equals("token expired") || username.equals("token is invalid")) return username;
        Account account=DataBase.getInstance().getAccountByUsername(username);
        Gson gson=new Gson();
        ArrayList<String> receiptIds=account.getAllPaidReceipt();
        if(type.equals("-")==false && type.equals("+")==false && type.equals("*")==false){
            if(receiptIds.contains(type)==false) return "invalid receipt id";
            Receipt receipt=DataBase.getInstance().getReceiptById(type);
            return gson.toJson(receipt);
        }
        ArrayList<Receipt> validReceipts=new ArrayList<>();
        if(type.equals("*")){
            for(String id:receiptIds){
                validReceipts.add(DataBase.getInstance().getReceiptById(id));
            }
            return gson.toJson(validReceipts);
        }
        else if(type.equals("+")){
            for(String id:receiptIds){
                Receipt receipt=DataBase.getInstance().getReceiptById(id);
                if(receipt.getDesId().equals(account.getId())) validReceipts.add(receipt);
            }
            return gson.toJson(validReceipts);
        }
        else if(type.equals("-")){
            for(String id:receiptIds){
                Receipt receipt=DataBase.getInstance().getReceiptById(id);
                if(receipt.getSrcId().equals(account.getId())) validReceipts.add(receipt);
            }
            return gson.toJson(validReceipts);
        }
        return null;
    }

    public String generateIdForReceipt(){
        int id=0;
        while (true) {
            id=secureRandom.nextInt(90000)+10000;
            if (DataBase.getInstance().getReceiptById(String.valueOf(id)) == null) {
                return String.valueOf(id);
            }
        }
    }

    public String generateIdForAccount(){
        int id=0;
        ArrayList<Account> allAccounts=DataBase.getInstance().getAllAccountsFromDataBase();
        if(allAccounts.isEmpty()==true) return "10001";
        for(Account account:allAccounts){
            id=secureRandom.nextInt(90000)+10000;
            if(account.getId().equals(String.valueOf(id))) generateIdForAccount();
        }
        return String.valueOf(id);
    }

}
