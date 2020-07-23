package Controller;

import Model.Receipt;

public class RequestProcessor {
    private static RequestProcessor requestProcessor;
    private RequestProcessor(){}

    public static RequestProcessor getInstance(){
        if(requestProcessor==null)
            requestProcessor=new RequestProcessor();
        return requestProcessor;
    }

    public String process(String command){
        if(command.startsWith("create_account")){
            return RequestController.getInstance().createAccount(command);
        }
        else if(command.startsWith("get_token")){
            return RequestController.getInstance().getToken(command);
        }
        else if(command.startsWith("create_receipt")){
            return RequestController.getInstance().createReceipt(command);
        }
        else if(command.startsWith("get_transactions")){
            return RequestController.getInstance().getTransaction(command);
        }
        else if(command.startsWith("pay")){
            return RequestController.getInstance().payReceipt(command);
        }
        else if(command.startsWith("get_balance")){
            return RequestController.getInstance().getBalance(command);
        }
        else if(command.startsWith("exit")){
            return "buy buy Client!";
        }
        return null;
    }

}
