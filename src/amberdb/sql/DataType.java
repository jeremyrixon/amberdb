package amberdb.sql;

import java.util.Date;

public enum DataType {

    STR, INT, DBL, BLN, LNG, FLT, DTE, BAD;
    
    public static String forObject(Object obj) {
        if (obj instanceof String)  return DataType.STR.toString();
        if (obj instanceof Integer) return DataType.INT.toString();
        if (obj instanceof Double)  return DataType.DBL.toString();
        if (obj instanceof Boolean) return DataType.BLN.toString();
        if (obj instanceof Long)    return DataType.LNG.toString();
        if (obj instanceof Float)   return DataType.FLT.toString();
        if (obj instanceof Date)	return DataType.DTE.toString();
        throw new InvalidDataTypeException(
                "Cannot work with a property of class: " + obj.getClass().getName());
    }
}
