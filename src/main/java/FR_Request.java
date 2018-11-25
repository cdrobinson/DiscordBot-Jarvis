class FR_Request {

    private String requesterID;
    private String request;
    private String approvalStatus;

    FR_Request(String approvalStatus, String requesterID, String request){
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
