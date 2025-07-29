package Utility;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private boolean userEnable;
    private List<Object> record;
    private String owner;
    private String type;

    public Item() {
        this.record = new ArrayList<>();
    }


    public boolean isUserEnable() {
        return userEnable;
    }
    public void setUserEnable(boolean userEnable) {
        this.userEnable = userEnable;
    }
    public List<Object> getRecord() {
        return record;
    }
    public void setRecord(List<Object> record) {
        this.record = record;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
}
