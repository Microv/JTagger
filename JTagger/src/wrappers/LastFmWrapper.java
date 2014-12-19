package wrappers;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class LastFmWrapper {
	
	private static String url = "http://ws.audioscrobbler.com/2.0"
			+ "/?method=track.getInfo&api_key=db281cbe739ed74d323678c3bfda72d0";
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private XPathFactory xPathfactory;
	private XPath xpath;
	
	public LastFmWrapper() throws ParserConfigurationException {
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath();
	}
	
	public String getCover(String track, String artist) 
			throws SAXException, IOException, XPathExpressionException {
		
		Document doc = builder.parse(url+"&artist="+artist+"&track="+track);
		XPathExpression exprAlbum = xpath.compile(
				"//image[@size='extralarge']/text()"
				);
		return exprAlbum.evaluate(doc);
	}
	
	
	
}
