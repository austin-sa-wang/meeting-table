package com.menu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XmlParser {
	// We don't use namespaces
	private static final String ns = null;

	// Sub-class: Entry definition
	public static class Entry {
		public final String id;
		public final String name;
		public final String attr;

		private Entry(String title, String summary, String link) {
			this.id = title;
			this.attr = summary;
			this.name = link;
		}
	}

	public List<Entry> parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readSrc(parser);
		} finally {
			in.close();
		}
	}

	private List<Entry> readSrc(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		List<Entry> entries = new ArrayList<Entry>();

		parser.require(XmlPullParser.START_TAG, ns, "category");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("entry")) {
				entries.add(readEntry(parser));
			} else {
				skip(parser);
			}
		}
		return entries;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
	
	// Parses the contents of an entry. If it encounters a title, summary, or
	// link tag, hands them off
	// to their respective "read" methods for processing. Otherwise, skips the
	// tag.
	private Entry readEntry(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "entry");
		String id = null;
		String name = null;
		String attr = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String entry_name = parser.getName();
			if (entry_name.equals("id")) {
				id = readId(parser);
			} else if (entry_name.equals("name")) {
				name = readName(parser);
			} else if (entry_name.equals("attr")) {
				attr = readAttr(parser);
			} else {
				skip(parser);
			}
		}
		return new Entry(id, name, attr);
	}

	// Processes title tags in the feed.
	private String readId(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "id");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "id");
		return title;
	}

	// Processes link tags in the feed.
	private String readAttr(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "attr");
		String attr = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "attr");
		return attr;
	}

	// Processes summary tags in the feed.
	private String readName(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "name");
		String name = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "name");
		return name;
	}

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
}
