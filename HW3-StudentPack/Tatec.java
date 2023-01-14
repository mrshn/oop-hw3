import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Stream;


import java.util.stream.Collectors;
import java.util.List;
import java.io.*;


public class Tatec
{
    private static final int CORRECT_TOTAL_TOKEN_PER_STUDENT = 100;
    private static final String OUT_TATEC_UNHAPPY = "unhappyOutTATEC.txt";
    private static final String OUT_TATEC_ADMISSION = "admissionOutTATEC.txt";
    private static final String OUT_RAND_UNHAPPY = "unhappyOutRANDOM.txt";
    private static final String OUT_RAND_ADMISSION = "admissionOutRANDOM.txt";

    public static void main(String args[])
    {
        if(args.length < 4)
        {
            System.err.println("Not enough arguments!");
            return;
        }

        // File Paths
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

        List<Course> allCourses = StreamAnalyzer.analyzeFile(courseFilePath, Tatec::_readCourses) ;
        List<String> allStudentIDs =  StreamAnalyzer.analyzeFile(studentIdFilePath, (fileStream) -> _collectToList(fileStream)) ;
        List<List<Integer>> allTokens =   StreamAnalyzer.analyzeFile(tokenFilePath, Tatec::_readAllTokens) ;

        List<Student> allStudents  =  _collectToList( IntStream.range(0, allStudentIDs.size())
                .mapToObj(i -> new Student(allTokens.get(i), allStudentIDs.get(i))) );

        if(Student.isAllStudentsValid == false)
        {
            System.out.println("Error: Student did not use 100 tokens");
            return;
        }

        IntStream.range(0, allCourses.size()).forEach(index ->
                allStudents.stream().forEach(student ->
                        allCourses.get(index).addToken(student.getStudentId(), student.getStudentTokens().get(index))));

        allCourses.stream().forEach(Course::assignStudentsByTatec);

        List<Double> unhappinessByTatec = allStudents.stream().mapToDouble(student ->
                IntStream.range(0, allCourses.size())
                        .mapToDouble(index ->
                                unhappinessFunction(h, student.getStudentTokens().get(index),
                                        student.getStudentTokens().get(index) > 0 && !allCourses.get(index).isAssigned(student.getStudentId())))
                        .sum()).boxed().collect( Collectors.toList() );


        try {
            PrintStream out = new PrintStream(
                    new FileOutputStream("admissionOutTATEC.txt", false), true);
            System.setOut(out);
            allCourses.stream().forEach(System.out::println);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            PrintStream out = new PrintStream(
                    new FileOutputStream("unhappyOutTATEC.txt", false), true);
            System.setOut(out);
            System.out.println(unhappinessByTatec.stream().reduce(0.0, Double::sum));
            unhappinessByTatec.stream().forEach(System.out::println);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        allCourses.stream().forEach(Course::assignStudentsByRandom);

        List<Double> unhappinessByRandom = allStudents.stream().mapToDouble(student ->
                IntStream.range(0, allCourses.size())
                        .mapToDouble(index ->
                                unhappinessFunction(h, student.getStudentTokens().get(index),
                                        student.getStudentTokens().get(index) > 0 && !allCourses.get(index).isAssigned(student.getStudentId())))
                        .sum()).boxed().collect( Collectors.toList() );


        try {
            PrintStream out = new PrintStream(
                    new FileOutputStream("admissionOutRANDOM.txt", false), true);
            System.setOut(out);
            allCourses.stream().forEach(System.out::println);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            PrintStream out = new PrintStream(
                    new FileOutputStream("unhappyOutRANDOM.txt", false), true);
            System.setOut(out);
            System.out.println(unhappinessByRandom.stream().reduce(0.0, Double::sum));
            unhappinessByRandom.stream().forEach(System.out::println);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        /*for(Course course : allCourses) {
            System.out.println(course.toString());
        }
        for(String id : allStudentIDs) {
            System.out.println(id);
        }
        for(List<Integer> list : allTokens) {
            for(Integer number : list) {
                System.out.print(number.toString()+"--");
            }
            System.out.println();
        }*/
        for(Student st : allStudents) {
            System.out.print(st.getStudentId()+"      ===" );
            for(Integer num : st.getStudentTokens()){
                System.out.print(num.toString()+"--");
            }
            System.out.println();
        }
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

    public static double unhappinessFunction(double h, int token, boolean unhappy)
    {
        return (unhappy) ? (-100.0/h) * Math.log(1 - (token/100.0)): 0.0;
    }

}
























