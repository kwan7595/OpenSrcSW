import java.io.*;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
public class makeCollection {
    public void makeCollection(String datapath) throws ParserConfigurationException, IOException, TransformerException {
        //creating docfactKry, docbuilder
        javax.xml.parsers.DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        File[] filelist; // get filelists
        String path = datapath;
        filelist = makeFileList(path);
        int n = filelist.length;
        //making new xml files with DOM

        Document document = docBuilder.newDocument();
        org.w3c.dom.Element docs = document.createElement("docs");
        document.appendChild(docs);
        for(int i=0;i<n;i++){ // reading all html files and append it to xml files
            org.w3c.dom.Element doc = document.createElement("doc"); //creating doc id=i
            docs.appendChild(doc);
            doc.setAttribute("id",Integer.toString(i));

            org.w3c.dom.Element title = document.createElement("title"); //create text
            title.appendChild(document.createTextNode(readTitle(filelist[i].getPath())));
            doc.appendChild(title);

            org.w3c.dom.Element body = document.createElement("body");  //create body
            body.appendChild(document.createTextNode(readBody(filelist[i].getPath())));
            doc.appendChild(body);
        }
        //writing docs in xml file with TransformerFactory.
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new FileOutputStream(new File("./output/collection.xml")));

        transformer.transform(source,result);
    }
    public File[] makeFileList(String path){ // reading html files from given path
        File dir = new File(path);
        return dir.listFiles();
    }
    public String readTitle(String file) throws IOException {
        File dir = new File(file);
        org.jsoup.nodes.Document html= Jsoup.parse(dir, "UTF-8"); // read title from html file
        String titleData = html.title();
        return titleData;
    }
    public String readBody(String file) throws IOException {
        File dir = new File(file);
        org.jsoup.nodes.Document html = Jsoup.parse(dir, "UTF-8"); // read body from html file
        String bodyData = html.body().text();
        return bodyData;
    }
}
