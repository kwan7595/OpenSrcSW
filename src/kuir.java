import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Objects;

public class kuir {
    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, ClassNotFoundException, SAXException {
        String command = args[0];
        String path = args[1];
        switch (command) {
            case "-c" -> {
                makeCollection collection = new makeCollection();
                collection.makeCollection(path);
            }
            case "-k" -> {
                makeKeyword keyword = new makeKeyword();
                keyword.makeKeyword(path);
            }
            case "-i" -> {
                indexer index = new indexer();
                index.indexer(path);
            }
            case "-s" -> {
                if(Objects.equals(args[2], "-q")) {
                    String q = args[3];
                    searcher s = new searcher();
                    s.searcher(path, q);
                }
                else{
                    System.out.println("질의어를 입력해주세요");
                }
            }
        }
    }
}
