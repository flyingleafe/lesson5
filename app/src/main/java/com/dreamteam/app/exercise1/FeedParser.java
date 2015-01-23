package com.dreamteam.app.exercise1;

import android.text.Html;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by flyingleafe on 21.10.14.
 */
public class FeedParser {
    XMLReader reader;

    class FeedHandler extends DefaultHandler {
        private Feed feed;
        private FeedItem currentItem = null;
        private String curText;
        private boolean record = false;

        FeedHandler(Feed feed) {
            this.feed = feed;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if(record) {
                curText += new String(ch, start, length);
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(qName.equals("item")) {
                currentItem = new FeedItem(null, null, null);
            } else if(qName.equals("title") || qName.equals("description") || qName.equals("link")) {
                record = true;
                curText = "";
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            record = false;
            if(qName.equals("title")) {
                if(currentItem == null) {
                    feed.setTitle(curText);
                } else {
                    currentItem.setTitle(curText);
                }
            } else if(qName.equals("description")) {
                if(currentItem == null) {
                    feed.setDescription(curText);
                } else {
                    currentItem.setDescription(curText);
                }
            } else if(qName.equals("link")) {
                if(currentItem != null) {
                    currentItem.setLink(curText);
                }
            } else if (qName.equals("item")) {
                if(currentItem != null) {
                    feed.addItem(currentItem);
                    currentItem = null;
                }
            }
        }
    }

    public FeedParser() {
        try {
            reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Feed parse(InputStream stream) throws SAXException {
        try {
            Feed feed = new Feed();
            DefaultHandler handler = new FeedHandler(feed);
            reader.setContentHandler(handler);
            reader.parse(new InputSource(stream));
            return feed;
        } catch (IOException e) {
            e.printStackTrace();
            throw new SAXException(e);
        }
    }
}
