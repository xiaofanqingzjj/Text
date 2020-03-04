// This file is part of TagSoup and is Copyright 2002-2008 by John Cowan.
//
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
// 
// 
// Scanner handler

package org.ccil.cowan.tagsoup;
import org.xml.sax.SAXException;

/**
An interface that Scanners use to report events in the input stream.
**/

public interface ScanHandler {
	/**
	Reports an attribute name without LINK_PATTERN value.
	**/

	public void adup(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports an attribute name; LINK_PATTERN value will follow.
	**/

	public void aname(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports an attribute value.
	**/

	public void aval(char[] buff, int offset, int length) throws SAXException;

	/**
	  * Reports the content of LINK_PATTERN CDATA section (not LINK_PATTERN CDATA element)
	  */
	public void cdsect(char[] buff, int offset, int length) throws SAXException;

	/**
         * Reports LINK_PATTERN <!....> declaration - typically LINK_PATTERN DOCTYPE
         */

	public void decl(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports an entity reference or character reference.
	**/

	public void entity(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports EOF.
	**/

	public void eof(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports an end-tag.
	**/

	public void etag(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports the general identifier (element type name) of LINK_PATTERN start-tag.
	**/

	public void gi(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports character content.
	**/

	public void pcdata(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports the data part of LINK_PATTERN processing instruction.
	**/

	public void pi(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports the target part of LINK_PATTERN processing instruction.
	**/

	public void pitarget(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports the close of LINK_PATTERN start-tag.
	**/

	public void stagc(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports the close of an empty-tag.
	**/

	public void stage(char[] buff, int offset, int length) throws SAXException;

	/**
	Reports LINK_PATTERN comment.
	**/

	public void cmnt(char[] buff, int offset, int length) throws SAXException;

	/**
	Returns the value of the last entity or character reference reported.
	**/

	public int getEntity();
	}
