package com.example.sm_capstone;

public class Contact {
    private String name;
    private String phoneNum;
    private String time;
    private String contactId;
    private String accept;
    private String type;

    public String getType() {   return type;  }
    public void setType(String type) {   this.type = type;   }

    public String getAccept() {   return accept;   }
    public void setAccept(String accept) {   this.accept = accept;  }

    public String getName() {   return name;   }
    public void setName(String name) {   this.name = name;    }

    public String getPhoneNum() {    return phoneNum;    }
    public void setPhoneNum(String phoneNum) {    this.phoneNum = phoneNum;    }

    public String getTime() {    return time;    }
    public void setTime(String time) {    this.time = time;    }

    public String getContactId() {    return contactId;    }
    public void setContactId(String contactId) {    this.contactId = contactId;   }


    public Contact(String name, String phoneNum, String accept, String contactId){
        this.name = name;
        this.phoneNum = phoneNum;
        this.accept = accept;
        this.contactId = contactId;
    }
}
