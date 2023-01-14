import java.util.ArrayList;
import java.util.List;

public class Course {

    private final String _id;
    private final int _defaultCapacity;
    private int _currentCapacity;
    private final List<String> _enrolledStudentsIDs = new ArrayList<>();
    private final List<Token> _submittedTokens = new ArrayList<>();

    public Course(String row)
    {
        this._id = row.split(",")[0];
        this._defaultCapacity = Integer.parseInt(row.split(",")[1]);
        this._currentCapacity = this._defaultCapacity;
    }

    @Override
    public String toString()
    {
        return _id + _enrolledStudentsIDs.stream()
            .map(sID -> "," + sID )
                .reduce("", String::concat);
    }

    public int getCurrentCourseCapacity()
    {
        return _currentCapacity;
    }

    public int getDefaultCourseCapacity()
    {
        return _currentCapacity;
    }

    public String getCourseID()
    {
        return _id;
    }

    public List<String> getEnrolledStudents()
    {
        return _enrolledStudentsIDs;
    }

    public List<Token> getSubmittedTokens()
    {
        return _submittedTokens;
    }

    public void enrollStudent(String winnerStudentId)
    {
        _enrolledStudentsIDs.add(winnerStudentId);
    }

    public void clearEnrolledStudents()
    {
        _enrolledStudentsIDs.clear();
    }

    public void submitToken(String studentId, int token)
    {
        _submittedTokens.add( new Token(this._id, studentId, token) );
    }

    public void increaseCurrentCourseCapacity()
    {
        this._currentCapacity =  this._currentCapacity +1 ;
    }

    public boolean isCourseFull()
    {
        return ( (_currentCapacity > 0 ) && ( _enrolledStudentsIDs.size() == _currentCapacity ) );
    }

    public int getLastEnrolledToken()
    {
        if( _enrolledStudentsIDs.size() > 0 )
        {
            String lastEnrolledId = _enrolledStudentsIDs.get( _enrolledStudentsIDs.size() - 1 );
            Token findToken = _submittedTokens.stream()
                    .filter(token -> token.getStudentId().equals(lastEnrolledId) )
                    .findAny()
                    .orElse(null);
            if(findToken != null)
            {
                return findToken.getToken();
            }
            System.out.println("aaaaaaaaaaaaaaa");
        }
        return -1;
    }

    public boolean isEnrolled(String searchedStudentID)
    {
        return _enrolledStudentsIDs.stream()
                .anyMatch( enrolledStudentID -> enrolledStudentID.equals(searchedStudentID) );
    }

}
