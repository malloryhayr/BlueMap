package de.bluecolored.bluemap.core.mcr;

public class NibbleArray {

	// de-finalized
    public byte[] data;

    public NibbleArray(int i) {
        this.data = new byte[i >> 1];
    }

    public NibbleArray(byte[] abyte) {
        this.data = abyte;
    }

    public int getData(int i, int j, int k) {
        int l = i << 11 | k << 7 | j;
        int i1 = l >> 1;
        int j1 = l & 1;

        if (i1 < 0 || i1 > 16384) return 0;
        return j1 == 0 ? this.data[i1] & 15 : this.data[i1] >> 4 & 15;
    }

    public void setData(int i, int j, int k, int l) {
        int i1 = i << 11 | k << 7 | j;
        int j1 = i1 >> 1;
        int k1 = i1 & 1;

        if (k1 == 0) {
            this.data[j1] = (byte) (this.data[j1] & 240 | l & 15);
        } else {
            this.data[j1] = (byte) (this.data[j1] & 15 | (l & 15) << 4);
        }
    }
}