package Model;

import Controller.RequestController;

public class Receipt {
    private String id;
    private String token;
    private String type;
    private int money;
    private String srcId;
    private String desId;
    private String description;
    private boolean paid;

    public Receipt(String token, String type, int money, String srcId, String desId) {
        id= RequestController.getInstance().generateIdForReceipt();
        this.token = token;
        this.type = type;
        this.money = money;
        this.srcId = srcId;
        this.desId = desId;
        paid=false;
        description="";
    }

    public String getId(){return id;}

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public int getMoney() {
        return money;
    }

    public String getSrcId() {
        return srcId;
    }

    public String getDesId() {
        return desId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }


}
