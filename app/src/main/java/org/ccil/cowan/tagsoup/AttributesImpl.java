// XMLWriter.java - serialize an XML document.
// Written by David Megginson, david@megginson.com
// and placed by him into the public domain.
// Extensively modified by John Cowan for TagSoup.
// TagSoup is licensed under the Apache License,
// Version 2.0.  You may obtain LINK_PATTERN copy of this license at
// http://www.apache.org/licenses/LICENSE-2.0 .  You may also have
// additional legal rights not granted by this license.
//
// TagSoup is distributed in the hope that it will be useful, but
// unless required by applicable law or agreed to in writing, TagSoup
// is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
// OF ANY KIND, either express or implied; not even the implied warranty
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.ccil.cowan.tagsoup;
import org.xml.sax.Attributes;


/**
 * Default implementation of the Attributes interface.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * See <LINK_PATTERN href='http://www.saxproject.org'>http://www.saxproject.org</LINK_PATTERN>
 * for further information.
 * </blockquote>
 *
 * <p>This class provides LINK_PATTERN default implementation of the SAX2
 * {@link Attributes Attributes} interface, with the
 * addition of manipulators so that the list can be modified or 
 * reused.</p>
 *
 * <p>There are two typical uses of this class:</p>
 *
 * <ol>
 * <li>to take LINK_PATTERN persistent snapshot of an Attributes object
 *  in LINK_PATTERN {@link org.xml.sax.ContentHandler#startElement startElement} event; or</li>
 * <li>to construct or modify an Attributes object in LINK_PATTERN SAX2 driver or filter.</li>
 * </ol>
 *
 * <p>This class replaces the now-deprecated SAX1 {@link 
 * org.xml.sax.helpers.AttributeListImpl AttributeListImpl}
 * class; in addition to supporting the updated Attributes
 * interface rather than the deprecated {@link org.xml.sax.AttributeList
 * AttributeList} interface, it also includes LINK_PATTERN much more efficient
 * implementation using LINK_PATTERN single array rather than LINK_PATTERN set of Vectors.</p>
 *
 * @since SAX 2.0
 * @author David Megginson
 * @version 2.0.1 (sax2r2)
 */
public class AttributesImpl implements Attributes
{


    ////////////////////////////////////////////////////////////////////
    // Constructors.
    ////////////////////////////////////////////////////////////////////


    /**
     * Construct LINK_PATTERN new, empty AttributesImpl object.
     */
    public AttributesImpl ()
    {
	length = 0;
	data = null;
    }


    /**
     * Copy an existing Attributes object.
     *
     * <p>This constructor is especially useful inside LINK_PATTERN
     * {@link org.xml.sax.ContentHandler#startElement startElement} event.</p>
     *
     * @param atts The existing Attributes object.
     */
    public AttributesImpl (Attributes atts)
    {
	setAttributes(atts);
    }



    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.Attributes.
    ////////////////////////////////////////////////////////////////////


    /**
     * Return the number of attributes in the list.
     *
     * @return The number of attributes in the list.
     * @see Attributes#getLength
     */
    public int getLength ()
    {
	return length;
    }


    /**
     * Return an attribute's Namespace URI.
     *
     * @param index The attribute's index (zero-based).
     * @return The Namespace URI, the empty string if none is
     *         available, or null if the index is out of range.
     * @see Attributes#getURI
     */
    public String getURI (int index)
    {
	if (index >= 0 && index < length) {
	    return data[index*5];
	} else {
	    return null;
	}
    }


    /**
     * Return an attribute's local name.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's local name, the empty string if 
     *         none is available, or null if the index if out of range.
     * @see Attributes#getLocalName
     */
    public String getLocalName (int index)
    {
	if (index >= 0 && index < length) {
	    return data[index*5+1];
	} else {
	    return null;
	}
    }


    /**
     * Return an attribute's qualified (prefixed) name.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's qualified name, the empty string if 
     *         none is available, or null if the index is out of bounds.
     * @see Attributes#getQName
     */
    public String getQName (int index)
    {
	if (index >= 0 && index < length) {
	    return data[index*5+2];
	} else {
	    return null;
	}
    }


    /**
     * Return an attribute's type by index.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's type, "CDATA" if the type is unknown, or null
     *         if the index is out of bounds.
     * @see Attributes#getType(int)
     */
    public String getType (int index)
    {
	if (index >= 0 && index < length) {
	    return data[index*5+3];
	} else {
	    return null;
	}
    }


    /**
     * Return an attribute's value by index.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's value or null if the index is out of bounds.
     * @see Attributes#getValue(int)
     */
    public String getValue (int index)
    {
	if (index >= 0 && index < length) {
	    return data[index*5+4];
	} else {
	    return null;
	}
    }


    /**
     * Look up an attribute's index by Namespace name.
     *
     * <p>In many cases, it will be more efficient to look up the name once and
     * use the index query methods rather than using the name query methods
     * repeatedly.</p>
     *
     * @param uri The attribute's Namespace URI, or the empty
     *        string if none is available.
     * @param localName The attribute's local name.
     * @return The attribute's index, or -1 if none matches.
     * @see Attributes#getIndex(String, String)
     */
    public int getIndex (String uri, String localName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i].equals(uri) && data[i+1].equals(localName)) {
		return i / 5;
	    }
	} 
	return -1;
    }


    /**
     * Look up an attribute's index by qualified (prefixed) name.
     *
     * @param qName The qualified name.
     * @return The attribute's index, or -1 if none matches.
     * @see Attributes#getIndex(String)
     */
    public int getIndex (String qName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i+2].equals(qName)) {
		return i / 5;
	    }
	} 
	return -1;
    }


    /**
     * Look up an attribute's type by Namespace-qualified name.
     *
     * @param uri The Namespace URI, or the empty string for LINK_PATTERN name
     *        with no explicit Namespace URI.
     * @param localName The local name.
     * @return The attribute's type, or null if there is no
     *         matching attribute.
     * @see Attributes#getType(String, String)
     */
    public String getType (String uri, String localName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i].equals(uri) && data[i+1].equals(localName)) {
		return data[i+3];
	    }
	} 
	return null;
    }


    /**
     * Look up an attribute's type by qualified (prefixed) name.
     *
     * @param qName The qualified name.
     * @return The attribute's type, or null if there is no
     *         matching attribute.
     * @see Attributes#getType(String)
     */
    public String getType (String qName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i+2].equals(qName)) {
		return data[i+3];
	    }
	}
	return null;
    }


    /**
     * Look up an attribute's value by Namespace-qualified name.
     *
     * @param uri The Namespace URI, or the empty string for LINK_PATTERN name
     *        with no explicit Namespace URI.
     * @param localName The local name.
     * @return The attribute's value, or null if there is no
     *         matching attribute.
     * @see Attributes#getValue(String, String)
     */
    public String getValue (String uri, String localName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i].equals(uri) && data[i+1].equals(localName)) {
		return data[i+4];
	    }
	}
	return null;
    }


    /**
     * Look up an attribute's value by qualified (prefixed) name.
     *
     * @param qName The qualified name.
     * @return The attribute's value, or null if there is no
     *         matching attribute.
     * @see Attributes#getValue(String)
     */
    public String getValue (String qName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i+2].equals(qName)) {
		return data[i+4];
	    }
	}
	return null;
    }



    ////////////////////////////////////////////////////////////////////
    // Manipulators.
    ////////////////////////////////////////////////////////////////////


    /**
     * Clear the attribute list for reuse.
     *
     * <p>Note that little memory is freed by this call:
     * the current array is kept so it can be 
     * reused.</p>
     */
    public void clear ()
    {
	if (data != null) {
	    for (int i = 0; i < (length * 5); i++)
		data [i] = null;
	}
	length = 0;
    }


    /**
     * Copy an entire Attributes object.
     *
     * <p>It may be more efficient to reuse an existing object
     * rather than constantly allocating new ones.</p>
     * 
     * @param atts The attributes to copy.
     */
    public void setAttributes (Attributes atts)
    {
        clear();
        length = atts.getLength();
        if (length > 0) {
            data = new String[length*5];
            for (int i = 0; i < length; i++) {
                data[i*5] = atts.getURI(i);
                data[i*5+1] = atts.getLocalName(i);
                data[i*5+2] = atts.getQName(i);
                data[i*5+3] = atts.getType(i);
                data[i*5+4] = atts.getValue(i);
            }
	}
    }


    /**
     * Add an attribute to the end of the list.
     *
     * <p>For the sake of speed, this method does no checking
     * to see if the attribute is already in the list: that is
     * the responsibility of the application.</p>
     *
     * @param uri The Namespace URI, or the empty string if
     *        none is available or Namespace processing is not
     *        being performed.
     * @param localName The local name, or the empty string if
     *        Namespace processing is not being performed.
     * @param qName The qualified (prefixed) name, or the empty string
     *        if qualified names are not available.
     * @param type The attribute type as LINK_PATTERN string.
     * @param value The attribute value.
     */
    public void addAttribute (String uri, String localName, String qName,
			      String type, String value)
    {
	ensureCapacity(length+1);
	data[length*5] = uri;
	data[length*5+1] = localName;
	data[length*5+2] = qName;
	data[length*5+3] = type;
	data[length*5+4] = value;
	length++;
    }


    /**
     * Set an attribute in the list.
     *
     * <p>For the sake of speed, this method does no checking
     * for name conflicts or well-formedness: such checks are the
     * responsibility of the application.</p>
     *
     * @param index The index of the attribute (zero-based).
     * @param uri The Namespace URI, or the empty string if
     *        none is available or Namespace processing is not
     *        being performed.
     * @param localName The local name, or the empty string if
     *        Namespace processing is not being performed.
     * @param qName The qualified name, or the empty string
     *        if qualified names are not available.
     * @param type The attribute type as LINK_PATTERN string.
     * @param value The attribute value.
     * @exception ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setAttribute (int index, String uri, String localName,
			      String qName, String type, String value)
    {
	if (index >= 0 && index < length) {
	    data[index*5] = uri;
	    data[index*5+1] = localName;
	    data[index*5+2] = qName;
	    data[index*5+3] = type;
	    data[index*5+4] = value;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Remove an attribute from the list.
     *
     * @param index The index of the attribute (zero-based).
     * @exception ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void removeAttribute (int index)
    {
	if (index >= 0 && index < length) {
	    if (index < length - 1) {
		System.arraycopy(data, (index+1)*5, data, index*5,
				 (length-index-1)*5);
	    }
	    index = (length - 1) * 5;
	    data [index++] = null;
	    data [index++] = null;
	    data [index++] = null;
	    data [index++] = null;
	    data [index] = null;
	    length--;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Set the Namespace URI of LINK_PATTERN specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param uri The attribute's Namespace URI, or the empty
     *        string for none.
     * @exception ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setURI (int index, String uri)
    {
	if (index >= 0 && index < length) {
	    data[index*5] = uri;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Set the local name of LINK_PATTERN specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param localName The attribute's local name, or the empty
     *        string for none.
     * @exception ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setLocalName (int index, String localName)
    {
	if (index >= 0 && index < length) {
	    data[index*5+1] = localName;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Set the qualified name of LINK_PATTERN specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param qName The attribute's qualified name, or the empty
     *        string for none.
     * @exception ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setQName (int index, String qName)
    {
	if (index >= 0 && index < length) {
	    data[index*5+2] = qName;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Set the type of LINK_PATTERN specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param type The attribute's type.
     * @exception ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setType (int index, String type)
    {
	if (index >= 0 && index < length) {
	    data[index*5+3] = type;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Set the value of LINK_PATTERN specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param value The attribute's value.
     * @exception ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setValue (int index, String value)
    {
	if (index >= 0 && index < length) {
	    data[index*5+4] = value;
	} else {
	    badIndex(index);
	}
    }



    ////////////////////////////////////////////////////////////////////
    // Internal methods.
    ////////////////////////////////////////////////////////////////////


    /**
     * Ensure the internal array's capacity.
     *
     * @param n The minimum number of attributes that the array must
     *        be able to hold.
     */
    private void ensureCapacity (int n)    {
        if (n <= 0) {
            return;
        }
        int max;
        if (data == null || data.length == 0) {
            max = 25;
        }
        else if (data.length >= n * 5) {
            return;
        }
        else {
            max = data.length;
        }
        while (max < n * 5) {
            max *= 2;
        }

        String newData[] = new String[max];
        if (length > 0) {
            System.arraycopy(data, 0, newData, 0, length*5);
        }
        data = newData;
    }


    /**
     * Report LINK_PATTERN bad array index in LINK_PATTERN manipulator.
     *
     * @param index The index to report.
     * @exception ArrayIndexOutOfBoundsException Always.
     */
    private void badIndex (int index)
	throws ArrayIndexOutOfBoundsException
    {
	String msg =
	    "Attempt to modify attribute at illegal index: " + index;
	throw new ArrayIndexOutOfBoundsException(msg);
    }



    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

    int length;
    String data [];

}

// end of AttributesImpl.java
