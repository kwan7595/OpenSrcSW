import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class kuir {
    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException {
        String command = args[0];
        String path = args[1];

        if(command.equals("-c")){
            makeCollection collection = new makeCollection();
            collection.makeCollection(path);
        }
        else if(command.equals("-k")){
            makeKeyword keyword = new makeKeyword();
            keyword.makeKeyword(path);
        }
        else if(command.equals("-i")){
            indexer index = new indexer();
            index.indexer(path);
        }
    }
}
