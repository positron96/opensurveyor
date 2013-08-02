package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import devedroid.opensurveyor.R;
import devedroid.opensurveyor.Utils;
import devedroid.opensurveyor.presets.PresetManager;

public class PropertyDefinition implements Serializable {
	public final String title;

	public final static String VALUE_YES = "yes" ;
	
	public final String key;
	
	public enum Type {
		String, Boolean, Choice, Number
	}
	
	public final Type type;
	
	public static class ChoiceEntry implements Serializable {
		public final String title;
		public final String value;
		public ChoiceEntry(String title, String value) {
			this.title = title;
			this.value = value;
		}
		public String toString() {return title;}
	}
	
	public final List<ChoiceEntry> choices;
	
	public PropertyDefinition(String title, String key, Type type) {
		super();
		this.title = title;
		this.key = key;
		this.type = type;
		if(type == Type.Choice) {
			choices = new ArrayList<ChoiceEntry>();
		} else choices = null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyDefinition other = (PropertyDefinition) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	private void addChoice(String title, String val) {
		choices.add( new ChoiceEntry(title, val));
	}
	
	public int findChoiceByValue(String value) {
		int i=0;
		for(ChoiceEntry c: choices) {
			if(c.value.equals(value)) return i;
			i++;
		}
		return -1;
	}
	
	public String formatValue(String value, Resources res) {
		switch(type) {
			case String: return value;
			case Choice: 
				for(ChoiceEntry e : choices)
					if(e.value.equals(value)) return e.title;
				break;
			case Boolean:
				return res.getString( value.equals(VALUE_YES) ? R.string.str_yes : R.string.str_no);
			case Number:
				return value;
		}
		return value;
	}
	
	public static PropertyDefinition stringProperty(String title, String key) {
		return new PropertyDefinition(title, key, Type.String);
	}
	
	public static PropertyDefinition readFromXml(PresetManager man, XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "property");
		String title = man.readLocalizedAttr("name", parser);
		String key = parser.getAttributeValue(null, "k");
		String cType = parser.getAttributeValue(null, "type");
		Type type = Type.String;
		if(cType==null || "text".equals(cType)) type = Type.String; else
		if("boolean".equals(cType)) type = Type.Boolean; else
		if("choice".equals(cType)) type = Type.Choice;else
		if("sequence".equals(cType) || "number".equals(cType)) type=Type.Number; else
			Utils.logd("PropertyDefinition.readFromXML", "Unknown type "+cType+"; assuming String");
		//XMLPresetLoader.skip(parser);
		PropertyDefinition res = new PropertyDefinition(title, key, type);
		if(type==Type.Choice) readChoices(man, parser, res); else man.skip(parser);
		
		return res;
	}
	
	private static void readChoices(PresetManager man, XmlPullParser parser, PropertyDefinition res) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "property");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("variant")) {
				res.addChoice( 
						man.readLocalizedAttr("name", parser), 
						parser.getAttributeValue(null, "v") );
				parser.next();
				parser.require(XmlPullParser.END_TAG, null, "variant");
			} else {
				man.skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, null, "property");

	}
}
