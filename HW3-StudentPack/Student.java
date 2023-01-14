import java.util.List;

public class Student
{
    private final List<Integer> _tokens;
    private final String _id;
    public static boolean isAllStudentsValid = true;

    public Student(List<Integer> tokenList, String id )
    {
        this._tokens = tokenList;
        this._id = id;
        _checkTokensValidity();
    }

    private void _checkTokensValidity()
    {
        if( 100 != this._tokens.stream().reduce(0, Integer::sum)   )
        {
            System.err.println("aaaaaaaaaaaaaaa"+this._tokens.stream().reduce(0, Integer::sum).toString() );
            isAllStudentsValid = false;
        }
    }

    public List<Integer> getStudentTokens()
    {
        return _tokens;
    }

    public String getStudentId()
    {
        return _id;
    }
}
