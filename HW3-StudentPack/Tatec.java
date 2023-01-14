import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.*;
import java.io.*;

public class Tatec
{
    private static final int CORRECT_TOTAL_TOKEN_PER_STUDENT = 100;
    private static final String OUT_TATEC_UNHAPPY = "unhappyOutTATEC.txt";
    private static final String OUT_TATEC_ADMISSION = "admissionOutTATEC.txt";
    private static final String OUT_RAND_UNHAPPY = "unhappyOutRANDOM.txt";
    private static final String OUT_RAND_ADMISSION = "admissionOutRANDOM.txt";

    private static List<Course> allCourses ;
    private static List<Student> allStudents;

    private static class Assigner {

        public Assigner(){}

        private static void submitTokensToCourses()
        {
            IntStream.range(0, allCourses.size())
                .forEach( courseIndex ->
                    allStudents.stream()
                        .forEach( student ->
                            allCourses.get(courseIndex).submitToken( student.getStudentId(), student.getStudentTokenWithIndex(courseIndex) )
                        )
                );
        }

        private static void enrollStudents()
        {
            allCourses.stream().forEach(
                course -> course.getSubmittedTokens().stream()
                    .filter(token -> token.getToken() > 0)
                        .sorted( Comparator.reverseOrder() )
                            .limit(course.getCurrentCourseCapacity()).forEach(token -> course.enrollStudent(token.getStudentId()))
            );

            allCourses.stream().filter(course -> course.isCourseFull())
                        .forEach(course ->
                            course.getSubmittedTokens().stream()
                                .filter( token -> (token.getToken() == course.getLastEnrolledToken() && !course.isEnrolled(token.getStudentId())) )
                                    .forEach(token ->
                                    {
                                        course.enrollStudent(token.getStudentId());
                                        course.increaseCurrentCourseCapacity();
                                    })
            );
        }

        private static void enrollStudentsRandomly()
        {
            allCourses.stream().forEach(
                    course ->
                    {
                        course.clearEnrolledStudents();
                        Collections.shuffle(course.getSubmittedTokens(), new Random());
                        course.getSubmittedTokens().stream().filter(token -> token.getToken() > 0)
                                .limit(course.getDefaultCourseCapacity()).forEach( token -> course.enrollStudent(token.getStudentId()) );
                    }
            );
        }
    }

    public static void main(String args[])
    {
        if(args.length < 4)
        {
            System.err.println("Not enough arguments!");
            return;
        }

        String courseFilePath = args[0];
        String studentIdFilePath = args[1];
        String tokenFilePath = args[2];
        double h;

        try { h = Double.parseDouble(args[3]);}
        catch (NumberFormatException ex)
        {
            System.err.println("4th argument is not a double!");
            return;
        }

        Student.STUDENTS_CORRECT_TOTAL_TOKEN = CORRECT_TOTAL_TOKEN_PER_STUDENT;

        List<String> allStudentIDs =  FileReader.readFile( studentIdFilePath, (fileStream) -> _collectToList(fileStream) ) ;
        List<List<Integer>> allTokens = FileReader.readFile( tokenFilePath, Tatec::_readAllTokens ) ;
        allCourses = FileReader.readFile( courseFilePath, Tatec::_readCourses ) ;
        allStudents = _collectToList( IntStream.range(0, allStudentIDs.size())
                .mapToObj(index -> new Student(allTokens.get(index), allStudentIDs.get(index))) );

        if(Student.isAllStudentsValid == false)
        {
            System.err.println("Error: Student did not use 100 tokens");
            return;
        }

        Assigner.submitTokensToCourses();
        Assigner.enrollStudents();

        List<Double> uTatec = _calculateUnhappiness(h);

        try
        {
            _outputAdmissions(OUT_TATEC_ADMISSION);
            _outputUnhappiness(OUT_TATEC_UNHAPPY, h , uTatec);

        } catch (FileNotFoundException exp)
        {
            throw new RuntimeException(exp);
        }

        Assigner.enrollStudentsRandomly();
        List<Double> uRandom = _calculateUnhappiness(h);

        try
        {
            _outputAdmissions(OUT_RAND_ADMISSION);
            _outputUnhappiness(OUT_RAND_UNHAPPY, h , uRandom);

        } catch (FileNotFoundException exp)
        {
            throw new RuntimeException(exp);
        }
    }

    private static void _outputHelper(String outputFileName)  throws FileNotFoundException
    {
        PrintStream out = new PrintStream( new FileOutputStream( outputFileName, false), true);
        System.setOut(out);
    }

    private static void _outputAdmissions(String outputFileName) throws FileNotFoundException
    {
        _outputHelper(outputFileName);
        allCourses.stream().forEach(System.out::println);
    }

    private static void _outputUnhappiness(String outputFileName, double h,  List<Double> unhappyList) throws FileNotFoundException
    {
        _outputHelper(outputFileName);
        System.out.println(unhappyList.stream().reduce(0.0, Double::sum));
        unhappyList.stream().forEach(System.out::println);
    }

    private static <T> List<T> _collectToList(Stream<T> stream)
    {
        return stream.collect( Collectors.toList() );
    }

    private static List<Course> _readCourses(Stream<String> fileStream)
    {
        return _collectToList( fileStream.map( (String row) -> new Course(row) ) );
    }

    private static List<List<Integer>> _readAllTokens(Stream<String> fileStream)
    {
        return _collectToList( fileStream.map( (String row) -> _collectToList( Stream.of(row.split(",")).mapToInt(Integer::parseInt).boxed() )) );
    }

    private static List<Double> _calculateUnhappiness(double h)
    {
        return _collectToList( allStudents.stream().mapToDouble( eachStudent -> IntStream.range(0, allCourses.size())
                .mapToDouble( index -> _calculateUnhappinessHelper( eachStudent, h, index ) ).sum() ).boxed()
        );
    }

    private static double _calculateUnhappinessHelper( Student student, double h, int index )
    {
        double unhappy = _isStudentUnhappy( student, index )
                ? ( -100.0 / h ) * Math.log( 1 - ( student.getStudentTokenWithIndex(index) / 100.0) )
                : 0.0;

        Course findEnrolledCourse = allCourses.stream()
                .filter(course -> course.isEnrolled(student.getStudentId()) )
                .findAny()
                .orElse(null);

        if(findEnrolledCourse == null)
        {
            unhappy = unhappy * unhappy;
        }

        return Math.min(unhappy, 100.0);
    }

    private static boolean _isStudentUnhappy(Student student, int index)
    {
        return ( ( student.getStudentTokenWithIndex(index) > 0 ) && !allCourses.get(index).isEnrolled(student.getStudentId()) );
    }

}
