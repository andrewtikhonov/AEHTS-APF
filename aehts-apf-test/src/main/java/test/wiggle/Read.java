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
package test.wiggle;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 25/05/2012
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
class Read implements Comparable<Read> {
    final boolean isValid;
    int blockSize;
    int start;
    int end;

    private static int readInt32(byte[] buffer, int offset) {
        return
                (buffer[offset] & 0xFF) +
                        ((buffer[offset + 1] & 0xFF) << 8) +
                        ((buffer[offset + 2] & 0xFF) << 16) +
                        ((buffer[offset + 3] & 0xFF) << 24);
    }

    Read(byte[] buffer, int from, int to) {
        if (to - from < 24) {
            isValid = false;
            return;
        }
        blockSize = 4 + readInt32(buffer, from);
        if (blockSize < 0 || to - from < blockSize) {
            isValid = false;
            return;
        }
        start = readInt32(buffer, from + 8);
        end = start + readInt32(buffer, from + 20);
        isValid = true;
    }

    public int compareTo(Read read) {
        if (start != read.start) {
            return start - read.start;
        }
        return end - read.end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Read read = (Read) o;

        if (end != read.end) return false;
        if (start != read.start) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        return result;
    }
}

