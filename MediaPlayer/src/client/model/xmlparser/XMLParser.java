package client.model.xmlparser;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import client.model.LibraryEntry;


public class XMLParser {

	
	private URL databaseLoc;
	private URL webserver;
	private String serverAddress;
	private int webServerPort;
	
	private List<LibraryEntry> library;
	
	private File database;
	
	public XMLParser(URL databaseLoc, String serverAddress, int webServerPort){
		this.databaseLoc = databaseLoc;
		this.serverAddress = serverAddress;
		this.webServerPort = webServerPort;
		
		library = new ArrayList<LibraryEntry>();
	}

	public void parseFile() {
			
		
	        BufferedReader in = null;
	        BufferedWriter out = null;
			try {
				in = new BufferedReader(
				new InputStreamReader(databaseLoc.openStream()));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

	        String inputLine;
	        database = new File("./temp/tempDatabase.xml");
	        
	        try {
	        	out = new BufferedWriter(new FileWriter(database));
				while ((inputLine = in.readLine()) != null) {
					out.write(inputLine);
				}
				in.close();
				out.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        parseXML();
	}
	
	private void parseXML(){
		try {
			library.clear();
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(database.getAbsolutePath()));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            System.out.println ("Root element of the doc is " + 
                 doc.getDocumentElement().getNodeName());


            NodeList videos = doc.getElementsByTagName("video");
            int totalVideos = videos.getLength();
            System.out.println("Total no of videos : " + totalVideos);

            for(int s=0; s<videos.getLength() ; s++){


                Node videoNode = videos.item(s);
                if(videoNode.getNodeType() == Node.ELEMENT_NODE){


                    Element videoElement = (Element)videoNode;

                    NodeList locationList = videoElement.getElementsByTagName("location");
                    Element locationElement = (Element)locationList.item(0);

                    NodeList textLocationList = locationElement.getChildNodes();
                    String location = ((Node)textLocationList.item(0)).getNodeValue().trim();
                    System.out.println("Location : " + 
                           ((Node)textLocationList.item(0)).getNodeValue().trim());

                    NodeList lengthList = videoElement.getElementsByTagName("length");
                    Element lengthElement = (Element)lengthList.item(0);

                    NodeList textLengthList = lengthElement.getChildNodes();
                    String length = ((Node)textLengthList.item(0)).getNodeValue().trim();
                   // System.out.println("Length: " + 
                     //      ((Node)textLengthList.item(0)).getNodeValue().trim());

                    NodeList periodList = videoElement.getElementsByTagName("period");
                    Element periodElement = (Element)periodList.item(0);

                    NodeList textPeriodList = periodElement.getChildNodes();
                    String period = ((Node)textPeriodList.item(0)).getNodeValue().trim();
                   // System.out.println("Period : " + 
                     //      ((Node)textPeriodList.item(0)).getNodeValue().trim());

                    NodeList typeList = videoElement.getElementsByTagName("type");
                    Element typeElement = (Element)typeList.item(0);

                    NodeList textTypeList = typeElement.getChildNodes();
                    String type = ((Node)textTypeList.item(0)).getNodeValue().trim();
                   // System.out.println("Type : " + 
                      //     ((Node)textTypeList.item(0)).getNodeValue().trim());

                    NodeList thumbnailList = videoElement.getElementsByTagName("thumbnail");
                    Element thumbnailElement = (Element)thumbnailList.item(0);

                    NodeList textThumbnailList = thumbnailElement.getChildNodes();
                    String thumbnailExt = ((Node)textThumbnailList.item(0)).getNodeValue().trim();
                    URL url = new URL("http", serverAddress, webServerPort, thumbnailExt);
                    System.out.println("Thumbnail URL" + url.toString());
                    
                    Image  thumbnail= null;
                    thumbnail = ImageIO.read(url);
                    
                    
                    LibraryEntry temp = new LibraryEntry("", "", "", location, length, period, type, thumbnail);
                    library.add(temp);
                }
                
            }
            
        }catch (SAXParseException err) {
        	System.err.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        	System.err.println(" " + err.getMessage ());

        }catch (SAXException e) {
        	Exception x = e.getException ();
        	((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        	t.printStackTrace ();
        }
	}
	
	public List<LibraryEntry> getLibrary(){
		return library;
	}
	
}
