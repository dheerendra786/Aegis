package aegis.com.aegis.model;

public class Result {
    private String SesionID;

    private String EnableLocation;

    private String RecordCalls;

    private String Breaktime;

    public String getSesionID ()
    {
        return SesionID;
    }

    public void setSesionID (String SesionID)
    {
        this.SesionID = SesionID;
    }

    public String getEnableLocation ()
    {
        return EnableLocation;
    }

    public void setEnableLocation (String EnableLocation)
    {
        this.EnableLocation = EnableLocation;
    }

    public String getRecordCalls ()
    {
        return RecordCalls;
    }

    public void setRecordCalls (String RecordCalls)
    {
        this.RecordCalls = RecordCalls;
    }

    public String getBreaktime ()
    {
        return Breaktime;
    }

    public void setBreaktime (String Breaktime)
    {
        this.Breaktime = Breaktime;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [SesionID = "+SesionID+", EnableLocation = "+EnableLocation+", RecordCalls = "+RecordCalls+", Breaktime = "+Breaktime+"]";
    }
}
