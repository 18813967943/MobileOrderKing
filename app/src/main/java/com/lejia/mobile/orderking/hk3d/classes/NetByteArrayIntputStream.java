package com.lejia.mobile.orderking.hk3d.classes;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Author by HEKE
 *
 * @time 2017/3/10 16:58
 * TODO: 封装解析
 */
public class NetByteArrayIntputStream extends ByteArrayInputStream {

    public NetByteArrayIntputStream(byte[] buf) {
        super(buf);
    }

    public NetByteArrayIntputStream(byte[] buf, int offset, int length) {
        super(buf, offset, length);
    }

    /**
     * 读取对应.Net的整形数据
     */
    public int ReadInt32() {
        byte[] buffer = new byte[4];
        read(buffer, 0, buffer.length);
        int val = (((buffer[0] & 0xFF) << 24)
                | ((buffer[1] & 0xFF) << 16)
                | ((buffer[2] & 0xFF) << 8)
                | (buffer[3] & 0xFF));
        return val;
    }

    /**
     * 正常顺序读取一个整形数值
     */
    public int ReadNormalInt32() {
        byte[] buffer = new byte[4];
        read(buffer, 0, buffer.length);
        int val = (((buffer[3] & 0xFF) << 24)
                | ((buffer[2] & 0xFF) << 16)
                | ((buffer[1] & 0xFF) << 8)
                | (buffer[0] & 0xFF));
        return val;
    }

    /**
     * 读取一个正常顺序下的单精度浮点型数据
     */
    public float ReadNormalSingle() {
        return Float.intBitsToFloat(ReadNormalInt32());
    }

    /**
     * 读取对应.Net的单精度浮点型数据
     */
    public float ReadSingle() {
        return Float.intBitsToFloat(ReadInt32());
    }

    /**
     * 读取字符串
     */
    public String ReadString() {
        int count = ReadInt32();
        if (count == 0)
            return null;
        byte[] buffer = new byte[count];
        read(buffer, 0, buffer.length);
        Charset charset = Charset.forName("UTF-8");
        ByteBuffer buf = ByteBuffer.wrap(buffer);
        CharBuffer cBuf = charset.decode(buf);
        return cBuf.toString();
    }

    /**
     * 读取常序状态下的字符串
     */
    public String ReadNormalString() {
        int count = ReadNormalInt32();
        if (count == 0)
            return null;
        byte[] buffer = new byte[count];
        read(buffer, 0, buffer.length);
        Charset charset = Charset.forName("UTF-8");
        ByteBuffer buf = ByteBuffer.wrap(buffer);
        CharBuffer cBuf = charset.decode(buf);
        return cBuf.toString();
    }

    /**
     * 将字节转为String
     */
    public String bytesToString(byte[] bytes) {
        if (bytes == null)
            return null;
        read(bytes, 0, bytes.length);
        Charset charset = Charset.forName("UTF-8");
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        CharBuffer cBuf = charset.decode(buf);
        return cBuf.toString();
    }

    /**
     * 读取指定的数据长度
     *
     * @param offset
     * @param size
     */
    public byte[] ReadBytes(int offset, int size) {
        byte[] buffer = new byte[size];
        read(buffer, offset, size);
        return buffer;
    }

}
