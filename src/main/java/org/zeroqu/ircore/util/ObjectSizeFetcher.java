package org.zeroqu.ircore.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectSizeFetcher {
    public static long calculateObjectSize(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.close();
        bos.close();
        byte[] byteArr = bos.toByteArray();
        return byteArr.length / 1000;
    }
}
