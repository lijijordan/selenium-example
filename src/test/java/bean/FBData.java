package bean;

import java.util.Date;

public class FBData {
    Long id;
    String name;
    String password;
    String type;
    Date createTime;
    String redirectUrl;
    String message;

    public static FBData form(SourceData sourceData) {
        FBData fbData = new FBData();
        fbData.setCreateTime(new Date());
        fbData.setName(sourceData.getName());
        fbData.setPassword(sourceData.getPassword());
        return fbData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
