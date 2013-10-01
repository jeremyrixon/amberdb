package amberdb.sql;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class AmberProperty {

    private long id;
    private String name; 
    private Object value;
    private DataType type;
    
    // session constructor
    public AmberProperty(long id, String name, DataType type, Object value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
    } 
    
    public long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public DataType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("property [")
        .append("id:").append(id).append(", ").append(name)
        .append(": ").append(value).append("]");
        return sb.toString();
    }
    
    public static byte[] encodeBlob(Object value) {
        if (value instanceof String) {
            return ((String) value).getBytes(Charset.forName("UTF-8"));
        } else if (value instanceof Integer) {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putInt((int) value);
            return bb.array();
        } else if (value instanceof Long) {
            ByteBuffer bb = ByteBuffer.allocate(8);
            bb.putLong((long) value);
            return bb.array();
        } else if (value instanceof Boolean) {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putInt((boolean) value == true ? 1 : 0);
            return bb.array();
        } else if (value instanceof Float) {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putFloat((float) value);
            return bb.array();
        } else if (value instanceof Double) {
            ByteBuffer bb = ByteBuffer.allocate(8);
            bb.putDouble((double) value);
            return bb.array();
        } else if (value == null) {
            return null;
        } else {
            throw new RuntimeException("Type not supported for encoding property: " + value.getClass());
        }
    }
    
    public static Object decodeBlob(byte[] blob, DataType type) {

        if (type == DataType.STR) return new String(blob, Charset.forName("UTF-8"));

        ByteBuffer bb = ByteBuffer.wrap(blob);
        if (type == DataType.INT) return bb.asIntBuffer().get();
        if (type == DataType.BLN) return bb.asIntBuffer().get() == 1 ? true : false;
        if (type == DataType.LNG) return bb.asLongBuffer().get();
        if (type == DataType.FLT) return bb.asFloatBuffer().get();
        if (type == DataType.DBL) return bb.asDoubleBuffer().get();

        throw new RuntimeException("Type not supported for decoding property: " + type.toString());
    }
}
