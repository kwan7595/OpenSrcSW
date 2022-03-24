import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.snu.ids.kkma.index.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
public class makeKeyword { // generates index.html file from collection.xml file using kkma libarary.
    public  void makeKeyword(String path) {
        try{
            File XmlFile = new File(path);
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbfactory.newDocumentBuilder();
            Document doc = dBuilder.parse(XmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("doc"); // read documents form xml file
            for(int temp=0;temp<nList.getLength();temp++){
                String str = getText(nList,temp); //get body text into string
                String ExtractedKeyword = ExtractKeyword(str);
                nList.item(temp).getChildNodes().item(1).setTextContent(ExtractedKeyword); //changes body with extracted keyword strings
            }
            SaveIndex(doc); // save index.xml
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    public  String getText(NodeList nList,int temp){ // get body text into string
        return nList.item(temp).getChildNodes().item(1).getTextContent();
    }
    public  String ExtractKeyword(String str){ //function to extract keywords, using kkma library
        StringBuilder keywords = new StringBuilder();
        KeywordExtractor k = new KeywordExtractor();
        KeywordList kl = k.extractKeyword(str, true);
        for(int i=0;i<kl.size();i++){
            Keyword kwrd = kl.get(i);
            keywords.append(kwrd.getString());  //append keywords extracted into string keywords.
            keywords.append(":");
            keywords.append(kwrd.getCnt()); // noun:Term Frequency, seperator=#.
            keywords.append("#");
        }
        String ExtractedKeywords = keywords.toString();
        return ExtractedKeywords;
    }
    public  void SaveIndex(Document document) throws TransformerException, FileNotFoundException { //saves modified document into local directory
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new FileOutputStream(new File("./SimpleIR/index.xml")));
        transformer.transform(source,result);
    }
}
