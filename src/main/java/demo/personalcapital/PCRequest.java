package demo.personalcapital;

public class PCRequest {
    private String planName;

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    private String sponsorName;

    public String getSponsorState() {
        return sponsorState;
    }

    public void setSponsorState(String sponsorState) {
        this.sponsorState = sponsorState;
    }

    private String sponsorState;

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    private String userToken;

    public String getUserToken(){
        return userToken;
    }

    public void setUserToken(String userToken){
        this.userToken = userToken;
    }
}
