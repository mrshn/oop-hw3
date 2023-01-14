
public class Token implements Comparable<Token>{

    private final String _courseID;
    private final String _studentID;
    private final int _token;

    public Token(String cID, String sID, int tokens)
    {
        this._courseID = cID;
        this._studentID = sID;
        this._token = tokens;
    }

    public String getCourseId()
    {
        return _courseID;
    }

    public String getStudentId()
    {
        return _studentID;
    }

    public int getToken()
    {
        return _token;
    }

    @Override
    public int compareTo(Token o)
    {
        return this.getToken() - o.getToken();
    }
}