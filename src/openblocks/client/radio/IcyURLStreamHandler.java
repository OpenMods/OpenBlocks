/*
** AACDecoder - Freeware Advanced Audio (AAC) Decoder for Android
** Copyright (C) 2014 Spolecne s.r.o., http://www.spoledge.com
**  
** This file is a part of AACDecoder.
**
** AACDecoder is free software; you can redistribute it and/or modify
** it under the terms of the GNU Lesser General Public License as published
** by the Free Software Foundation; either version 3 of the License,
** or (at your option) any later version.
** 
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU Lesser General Public License for more details.
** 
** You should have received a copy of the GNU Lesser General Public License
** along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package openblocks.client.radio;


import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;


/**
 * The URLStreamHandler for the ICY protocol.
 */
public class IcyURLStreamHandler extends URLStreamHandler {

    /**
     * Actually returns 80.
     */
    @Override
    protected int getDefaultPort() {
        return 80;
    }


    /**
     * Opens a connection to the object referenced by the URL argument.
     */
    @Override
    protected URLConnection openConnection( URL url ) throws IOException {
        return new IcyURLConnection( url );
    }

}