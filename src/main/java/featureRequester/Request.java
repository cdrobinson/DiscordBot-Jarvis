/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package featureRequester;

class Request {

    private String requesterID;
    private String request;
    private String approvalStatus;

    Request(String approvalStatus, String requesterID, String request){
        this.approvalStatus = approvalStatus;
        this.request = request;
        this.requesterID = requesterID;
    }

    String getApprovalStatus() {
        return approvalStatus;
    }

    String getRequesterID() {
        return requesterID;
    }

    String getRequest() {
        return request;
    }
}
