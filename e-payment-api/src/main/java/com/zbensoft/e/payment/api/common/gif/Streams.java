package com.zbensoft.e.payment.api.common.gif;

import java.io.*;

public class Streams {
    /**
     * 关闭输入流
     * 
     * @param in
     *            输入流
     */
    public static void close(InputStream in) {
	if (in != null) {
	    try {
		in.close();
	    } catch (IOException ioex) {
		// ignore
	    }
	}
    }

    /**
     * 关闭输出流
     * 
     * @param out
     *            输出流
     */
    public static void close(OutputStream out) {
	if (out != null) {
	    try {
		out.flush();
	    } catch (IOException ioex) {
		// ignore
	    }
	    try {
		out.close();
	    } catch (IOException ioex) {
		// ignore
	    }
	}
    }
}