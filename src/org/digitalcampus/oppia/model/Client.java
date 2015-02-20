package org.digitalcampus.oppia.model;

import java.io.Serializable;

/**
 * Created by ronak on 30/1/15.
 */
public class Client implements Serializable{

    private long clientId;
    private String clientName;
    private String clientMobileNumber;
    private String clientGender;
    private String clientMaritalStatus;
    private String clientAge;
    private String clientParity;
    private String clientLifeStage;
    private String healthWorker; // User
    private long clientServerId;
    private long lastModifiedDate;

    public long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public long getClientServerId() {
        return clientServerId;
    }

    public void setClientServerId(long clientServerId) {
        this.clientServerId = clientServerId;
    }

    public String getHealthWorker() {
        return healthWorker;
    }

    public void setHealthWorker(String healthWorker) {
        this.healthWorker = healthWorker;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientMobileNumber() {
        return clientMobileNumber;
    }

    public void setClientMobileNumber(String mobileNumber) {
        this.clientMobileNumber = mobileNumber;
    }

    public String getClientGender() {
        return clientGender;
    }

    public void setClientGender(String clientGender) {
        this.clientGender = clientGender;
    }

    public String getClientMaritalStatus() {
        return clientMaritalStatus;
    }

    public void setClientMaritalStatus(String clientMaritalStatus) {
        this.clientMaritalStatus = clientMaritalStatus;
    }

    public String getClientAge() {
        return clientAge;
    }

    public void setClientAge(String clientAge) {
        this.clientAge = clientAge;
    }

    public String getClientParity() {
        return clientParity;
    }

    public void setClientParity(String clientParity) {
        this.clientParity = clientParity;
    }

    public String getClientLifeStage() {
        return clientLifeStage;
    }

    public void setClientLifeStage(String clientLifeStage) {
        this.clientLifeStage = clientLifeStage;
    }
}