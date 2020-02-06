/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 *
 * @author mirko.ravot
 */
public class Database {
    
    private String key;
    private String filename;
    private Document document;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
        if (!filename.endsWith(".pmdb"))
            this.filename += ".pmdb";
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
    
    public void save() throws DatabaseException {
        try {
            DOMSource source = new DOMSource(this.getDocument());
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(source, result);
            Encryptor enc = Encryptor.getEncryptor();
            String encString = enc.encrypt(writer.toString(), this.getKey());
            FileOutputStream  out = new FileOutputStream(this.getFilename());
            out.write(encString.getBytes());
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        
    }

    public void open() throws DatabaseException,KeyException {
        try {
            FileInputStream fis = new FileInputStream(filename);
            int i; 
            StringBuilder sb = new StringBuilder();
            while ((i = fis.read()) != -1) {
                sb.append((char) i);
            }
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();

            Encryptor enc = Encryptor.getEncryptor();
            document = docBuilder.parse(new InputSource(new StringReader(new String(enc.decrypt(sb.toString(), this.getKey())))));
        } catch (javax.crypto.BadPaddingException encex) {
            throw new KeyException("Invalid key: " + encex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
    }

    public void initNewDatabase() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = dbf.newDocumentBuilder();
        this.document = b.newDocument();
        Element root = document.createElement("root");
        root.setAttribute("createdAt", Calendar.getInstance().getTime().toString());
        root.setAttribute("nome", "workspace");

        document.appendChild(root);

    }    
    
    public Element createElement(String tagName) {
        return document.createElement(tagName);
    }
    
    
    
}
