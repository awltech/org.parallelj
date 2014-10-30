/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010, 2011, 2012 Atos Worldline or third-party contributors as
 *     indicated by the @author tags or express copyright attribution
 *     statements applied by the authors.
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.parallelj.launching.transport.tcp;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;

/**
 * This is a copy of org.apache.mina.filter.codec.textline.TextLineEncoder
 * with a few modification in the method encode(...).
 * See: http://mina.apache.org
 * 
 * A {@link ProtocolEncoder} which encodes a string into a text line
 * which ends with the delimiter.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class TcpIpTextLineEncoder extends ProtocolEncoderAdapter {
    private final AttributeKey ENCODER = new AttributeKey(getClass(), "encoder");

    private final Charset charset;

    //private final LineDelimiter delimiter;

    private int maxLineLength = Integer.MAX_VALUE;

    /**
     * Creates a new instance with the current default {@link Charset}
     * and {@link LineDelimiter#UNIX} delimiter.
     */
    public TcpIpTextLineEncoder() {
        this(Charset.defaultCharset(), LineDelimiter.UNIX);
    }

    /**
     * Creates a new instance with the current default {@link Charset}
     * and the specified <tt>delimiter</tt>.
     */
    public TcpIpTextLineEncoder(final String delimiter) {
        this(new LineDelimiter(delimiter));
    }

    /**
     * Creates a new instance with the current default {@link Charset}
     * and the specified <tt>delimiter</tt>.
     */
    public TcpIpTextLineEncoder(final LineDelimiter delimiter) {
        this(Charset.defaultCharset(), delimiter);
    }

    /**
     * Creates a new instance with the spcified <tt>charset</tt>
     * and {@link LineDelimiter#UNIX} delimiter.
     */
    public TcpIpTextLineEncoder(final Charset charset) {
        this(charset, LineDelimiter.UNIX);
    }

    /**
     * Creates a new instance with the spcified <tt>charset</tt>
     * and the specified <tt>delimiter</tt>.
     */
    public TcpIpTextLineEncoder(final Charset charset, final String delimiter) {
        this(charset, new LineDelimiter(delimiter));
    }

    /**
     * Creates a new instance with the spcified <tt>charset</tt>
     * and the specified <tt>delimiter</tt>.
     */
    public TcpIpTextLineEncoder(final Charset charset, final LineDelimiter delimiter) {
        if (charset == null) {
            throw new IllegalArgumentException("charset");
        }
        if (delimiter == null) {
            throw new IllegalArgumentException("delimiter");
        }
        if (LineDelimiter.AUTO.equals(delimiter)) {
            throw new IllegalArgumentException(
                    "AUTO delimiter is not allowed for encoder.");
        }

        this.charset = charset;
        //this.delimiter = delimiter;
    }

    /**
     * Returns the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     */
    public int getMaxLineLength() {
        return maxLineLength;
    }

    /**
     * Sets the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     */
    public void setMaxLineLength(final int maxLineLength) {
        if (maxLineLength <= 0) {
            throw new IllegalArgumentException("maxLineLength: "
                    + maxLineLength);
        }

        this.maxLineLength = maxLineLength;
    }

    public void encode(final IoSession session, final Object message,
    		final ProtocolEncoderOutput out) throws Exception {
        CharsetEncoder encoder = (CharsetEncoder) session.getAttribute(ENCODER);

        if (encoder == null) {
            encoder = charset.newEncoder();
            session.setAttribute(ENCODER, encoder);
        }

        final String value = (message == null ? "" : message.toString());
        final IoBuffer buf = IoBuffer.allocate(value.length())
                .setAutoExpand(true);
        buf.putString(value, encoder);

        if (buf.position() > maxLineLength) {
            throw new IllegalArgumentException("Line length: " + buf.position());
        }

        // The next line has been removed for ParalleJ
        //buf.putString(delimiter.getValue(), encoder);
        if (buf.capacity()>0) {
	        buf.flip();
	        out.write(buf);
        }
    }

    public void dispose() throws Exception {
        // Do nothing
    }
}