package info.kilchhofer.bfh.lidar.hardware.mock;

import java.io.IOException;

public interface IReadListener {
    void receivedByte(byte value) throws IOException;
}
