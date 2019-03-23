package aegis.com.aegis.apiresponse;

import java.util.ArrayList;
import aegis.com.aegis.model.Result;
public class LoginResponse {
    private String StatusCode;

    private ArrayList<Result> Result;

    private String Status;

    public String getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(String StatusCode) {
        this.StatusCode = StatusCode;
    }

    public ArrayList<Result> getResult() {
        return Result;
    }

    public void setResult(ArrayList<Result> Result) {
        this.Result = Result;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

}
