import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Course {

    private final String id;

    private int capacity;

    private class Token implements Comparable<Token>{

        private final String studentId;
        private final int token;

        public Token(String studentId, int token){
            this.studentId = studentId;
            this.token = token;
        }

        public String getStudentId(){ return studentId; }
        public int getToken() { return token; }

        @Override
        public int compareTo(Token o) {
            return this.getToken() - o.getToken();
        }
    }

    private final List<Token> tokens = new ArrayList<>();

    private final List<Token> sortedTokens = new ArrayList<>();

    public Course(String row)
    {
        this.id = row.split(",")[0];
        this.capacity = Integer.parseInt(row.split(", ")[1]);
        System.out.print(id+"      ===  "+capacity+"\n" );
    }

    public void addToken(String studentId, int token)
    {
        tokens.add(new Token(studentId, token));
    }

    public void assignStudentsByTatec(){
        // Get first n student to sorted list
        tokens.stream().filter(token -> token.getToken() > 0).sorted(Comparator.reverseOrder()).limit(capacity).forEach(sortedTokens::add);

        if(capacity == sortedTokens.size() ) addMinimumTokenStudents();
    }

    public void assignStudentsByRandom(){
        // Get first n student to sorted list
        sortedTokens.clear();
        Collections.shuffle(tokens);
        tokens.stream().filter(token -> token.getToken() > 0).limit(capacity).forEach(sortedTokens::add);

    }

    public void addMinimumTokenStudents(){
        // Admit minumum token students
        Token lastToken = sortedTokens.get(sortedTokens.size() - 1);
        tokens.stream().filter(token -> token.getToken() == lastToken.getToken())
                .filter(token -> !token.getStudentId().equals(lastToken.getStudentId()))
                .forEach(sortedTokens::add);

        // Increase capacity
        capacity = sortedTokens.size();
    }

    public boolean isAssigned(String studentId){
        return sortedTokens.stream().anyMatch(token -> token.getStudentId().equals(studentId));
    }

    @Override
    public String toString(){
        return id + sortedTokens.stream().map(token -> ", " + token.getStudentId()).reduce("", String::concat);
    }



}
