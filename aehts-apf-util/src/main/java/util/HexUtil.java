/**
 * Copyright 2012-2017 Functional Genomics Development Team, European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * @author Andrew Tikhonov <andrew.tikhonov@gmail.com>
 **/
package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.NoSuchObjectException;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 29/03/2012
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
public class HexUtil {

    final private static Logger log = LoggerFactory.getLogger(HexUtil.class);

    public static String bytesToHex(byte in[]) {
        byte ch = 0x00;
        int i = 0;
        if (in == null || in.length <= 0)
            return null;
        String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
        StringBuffer out = new StringBuffer(in.length * 2);
        while (i < in.length) {
            ch = (byte) (in[i] & 0xF0);
            ch = (byte) (ch >>> 4);
            ch = (byte) (ch & 0x0F);
            out.append(pseudo[(int) ch]);
            ch = (byte) (in[i] & 0x0F);
            out.append(pseudo[(int) ch]);
            i++;
        }
        String rslt = new String(out);
        return rslt;
    }

    public static final byte[] hexToBytes(String s) throws NumberFormatException, IndexOutOfBoundsException {
        int slen = s.length();
        if ((slen % 2) != 0) {
            s = '0' + s;
        }

        byte[] out = new byte[slen / 2];

        byte b1, b2;
        for (int i = 0; i < slen; i += 2) {
            b1 = (byte) Character.digit(s.charAt(i), 16);
            b2 = (byte) Character.digit(s.charAt(i + 1), 16);
            if ((b1 < 0) || (b2 < 0)) {
                throw new NumberFormatException();
            }
            out[i / 2] = (byte) (b1 << 4 | b2);
        }
        return out;
    }

    public static byte[] objectToBytes(Object obj) throws NoSuchObjectException {
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baoStream);
            oos.writeObject(obj);
            oos.flush();
        } catch (Exception e) {
            log.error("Error!", e);
        }
        return baoStream.toByteArray();
    }

    public static Object bytesToObject(byte[] b) {
        try {
            return (Object) new ObjectInputStream(new ByteArrayInputStream(b)).readObject();
        } catch (Exception e) {
            log.error("Error!", e);
            return null;
        }
    }

    public static String objectToHex(Object obj) {

        ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(baoStream).writeObject(obj);
        } catch (Exception e) {
            log.error("Error!", e);
        }
        return bytesToHex(baoStream.toByteArray());
    }

    public static Object hexToObject(String hex) {
        if (hex == null || hex.equals(""))
            return null;
        try {
            return (Object) new ObjectInputStream(new ByteArrayInputStream(hexToBytes(hex))).readObject();
        } catch (Exception e) {
            log.error("Error!", e);
            return null;
        }
    }

}
