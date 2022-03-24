import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class kuir {
    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, ClassNotFoundException {

            FileInputStream filestream = new FileInputStream("./SimpleIR/index.post");
            ObjectInputStream o= new ObjectInputStream(filestream);

            Object object = o.readObject();
            o.close();
            HashMap hashMap = (HashMap)object;
            Iterator<String> it = hashMap.keySet().iterator();
            while(((Iterator<?>) it).hasNext()){
                String key = it.next();
                ArrayList<Double> val = (ArrayList<Double>)hashMap.get(key);
                System.out.println(key + "->"+val);
            }
    }
}
