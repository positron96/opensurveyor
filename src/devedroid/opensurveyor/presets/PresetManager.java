package devedroid.opensurveyor.presets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Xml;
import devedroid.opensurveyor.data.PropertyDefinition;

public class PresetManager {
	private static final String ns = null;

	public static class PresetSet {
		private String name;
		private String filename=null;
		private List<BasePreset> presets;

		public PresetSet() {
			presets = new ArrayList<BasePreset>();
		}
		
		private void setFileName(String filename) {
			this.filename = filename;
		}
		public String getFileName() { return filename; }

		private void setName(String name) {
			this.name = name;
		}

		private void addPreset(BasePreset pp) {
			presets.add(pp);
		}

		public String getName() {
			return name;
		}

		public Collection<BasePreset> getPresets() {
			return presets;
		}
	}

	private String langCode;
	
	public List<PresetSet> loadPresetSets(Context ctx) {
		AssetManager aman = ctx.getResources().getAssets();
		List<PresetSet> res = new ArrayList<PresetSet>();
		
		langCode = ctx.getResources().getConfiguration().locale.getLanguage();
		try {
			String[] files = aman.list("presets");

			for (String f : files) {
				PresetSet s = loadPresetSet(aman.open("presets/" + f));
				if (s.name == null)
					s.setName(f);
				s.setFileName(f);
				res.add(s);
			}

			File external = new File(Environment.getExternalStorageDirectory(),
					"OpenSurveyor/presets");
			if (external.isDirectory()) {
				files = external.list();
				for (String f : files) {
					PresetSet s = loadPresetSet(new FileInputStream(new File(
							external, f)));
					if (s.name == null)
						s.setName(f);
					s.setFileName(f);
					res.add(s);
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public String readLocalizedAttr(String attr, XmlPullParser parser) {
		for(int i=0; i<parser.getAttributeCount(); i++) {
			if( parser.getAttributeName(i).equals(attr+":"+langCode) )
				return parser.getAttributeValue(i);
		}
		return parser.getAttributeValue(ns, attr);
	}

	public PresetSet loadPresetSet(InputStream in) throws IOException,
			XmlPullParserException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readPreset(parser);
		} finally {
			in.close();
		}

	}

	private PresetSet readPreset(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		// List<BasePreset> prs = new ArrayList<BasePreset>();
		PresetSet prs = new PresetSet();
		parser.require(XmlPullParser.START_TAG, ns, "preset");
		prs.setName( readLocalizedAttr("name",parser));
		String defLang = parser.getAttributeValue(ns, "lang");
		
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("button")) {
				prs.addPreset(readButton(parser));
			} else {
				skip(parser);
			}
		}
		return prs;
	}

	private BasePreset readButton(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		POIPreset res = null;
		parser.require(XmlPullParser.START_TAG, ns, "button");
		String title = readLocalizedAttr("label", parser);
		String sDir = parser.getAttributeValue(ns, "directional");
		String sToggle = parser.getAttributeValue(ns, "toggle");
		String icon = parser.getAttributeValue(ns, "icon");
		String type;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("poi")) {
				type = parser.getAttributeValue(ns, "type");
				res = new POIPreset(title, type, icon);
				if (sDir != null)
					res.setDirected(sDir.equals("yes"));
				if (sToggle != null)
					res.setToggleButton(sToggle.equals("yes"));
				while (parser.next() != XmlPullParser.END_TAG)
					;
			} else if (name.equals("properties")) {
				readProperties(parser, res);
			} else {
				skip(parser);
			}
		}

		return res;
	}

	private void readProperties(XmlPullParser parser, POIPreset prs)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "properties");

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("property")) {
				prs.addProperty(PropertyDefinition.readFromXml(this, parser));
				parser.require(XmlPullParser.END_TAG, ns, "property");
			} else {
				skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, "properties");

	}

	public void skip(XmlPullParser parser)
			throws XmlPullParserException, IOException {
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

}
