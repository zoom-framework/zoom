package org.zoomdev.zoom.caster;

import java.io.*;
import java.sql.Clob;
import java.sql.SQLException;

public class MockClob implements Clob {

    private String content;

    public MockClob(String content){
        this.content = content;
    }

    @Override
    public long length() throws SQLException {
        return content.length();
    }

    @Override
    public String getSubString(long pos, int length) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        return new StringReader(content);
    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        return null;
    }

    @Override
    public long position(String searchstr, long start) throws SQLException {
        return 0;
    }

    @Override
    public long position(Clob searchstr, long start) throws SQLException {
        return 0;
    }

    @Override
    public int setString(long pos, String str) throws SQLException {
        return 0;
    }

    @Override
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        return 0;
    }

    @Override
    public OutputStream setAsciiStream(long pos) throws SQLException {
        return null;
    }

    @Override
    public Writer setCharacterStream(long pos) throws SQLException {
        return null;
    }

    @Override
    public void truncate(long len) throws SQLException {

    }

    @Override
    public void free() throws SQLException {

    }

    @Override
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        return null;
    }
}
