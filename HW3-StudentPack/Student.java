import java.util.List;

public class Student
{
    public static int STUDENTS_CORRECT_TOTAL_TOKEN = 100;
    public static boolean isAllStudentsValid = true;

    private final List<Integer> _tokens;
    private final String _id;

    public Student(List<Integer> tokenList, String id )
    {
        this._tokens = tokenList;
        this._id = id;
        _checkTokensValidity();
    }

    private void _checkTokensValidity()
    {
        if( STUDENTS_CORRECT_TOTAL_TOKEN != this._tokens.stream().reduce(0, Integer::sum)   )
        {
            isAllStudentsValid = false;
        }
    }

    public List<Integer> getStudentTokens()
    {
        return _tokens;
    }

    public Integer getStudentTokenWithIndex(int index)
    {
        return _tokens.get(index);
    }

    public String getStudentId()
    {
        return _id;
    }
}
