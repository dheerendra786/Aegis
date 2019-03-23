package aegis.com.aegis.apiresponse;

public class UniversalResponse {
    private String StatusCode;

    private String Result;

    private String Status;

    public String getStatusCode ()
    {
        return StatusCode;
    }

    public void setStatusCode (String StatusCode)
    {
        this.StatusCode = StatusCode;
    }

    public String getResult ()
    {
        return Result;
    }

    public void setResult (String Result)
    {
        this.Result = Result;
    }

    public String getStatus ()
    {
        return Status;
    }

    public void setStatus (String Status)
    {
        this.Status = Status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [StatusCode = "+StatusCode+", Result = "+Result+", Status = "+Status+"]";
    }
}
