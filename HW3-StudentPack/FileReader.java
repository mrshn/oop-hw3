import java.util.stream.Stream;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

@FunctionalInterface
public interface FileReader<T>
{
    public static <T> T readFile( String filepath, FileReader<T> processor )
    {
        try
        {
            Stream<String> lineStream =  Files.lines( Paths.get(filepath) );
            return processor.readStream(lineStream);
        } catch(IOException exp)
        {
            System.err.println("An error occurred while reading file :" + filepath  + exp);
            return (null);
        }
    }

    public T readStream(Stream<String> strings);

}
