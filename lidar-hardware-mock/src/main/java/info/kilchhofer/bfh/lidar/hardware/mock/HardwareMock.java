package info.kilchhofer.bfh.lidar.hardware.mock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HardwareMock {
    private static final Logger LOGGER = LogManager.getLogger(HardwareMock.class);
    private static Socket connectionSocket;
    private static BufferedReader bufferedReader;
    private static DataOutputStream outputStream;
    private String foobar;
    private final static byte DATA_START = 2;
    private final static byte DATA_END = 3;

    private String singleAnswer = "sRA LMDscandata 1 1 107375C 0 0 2AD8 2ADD 7907F6FD 79080660 0 0 1 0 0 5DC 36 0 1 DIST1 3F800000 00000000 FFF92230 2710 10F 116 120 123 11D 120 122 124 123 123 126 128 127 12D 137 13C 14C 159 168 17B 18D 19F 1A8 1B2 1C0 1CA 1CA 1CA 1C9 1CA 1CC 1CF 1CD 1CA 1CA 1BD 19F 198 198 198 19A 197 1A1 1BE 1C1 1BD 1BD 1C0 1BC 1BB 1B5 1AA 1A3 19B 192 18E 17C 16B 153 132 112 101 FC F6 F2 F0 ED E7 E5 E5 DF DB DA D6 D5 D1 CC CD C9 C7 C9 C6 C9 BE C4 C1 C0 C1 C5 C3 C5 A8 22 2 62A 619 60B 604 5FC 5F2 5E0 5D9 5CE 5C3 5B8 5B5 5A9 59D 5A7 59D 596 596 596 58B 586 58A 581 581 57B 575 587 57A 581 57D 57D 57D 57B 583 588 590 585 313 2FF 2FF 303 307 30F 30F 312 314 317 320 324 327 32F 330 33D 33F 34B 353 0 66E 673 684 69F 6A9 6B9 6D1 6E1 6FF 6FE 6D9 6EF 712 72E 736 720 6F2 6D6 6BB 6A5 68F 676 660 648 634 0 0 65 A4 A5 9F 9D 9E A0 99 9B 9A 9A 99 98 97 99 95 98 92 91 9E 97 9C A1 97 93 9A A0 99 96 95 9A 98 95 95 97 97 9C 99 9A 9A 9D 9C A1 9F 9E 9E A1 A0 A5 A6 AA A9 A8 AC AD AD B3 BF 0 0 0 56D 53E 512 506 4FF 527 4F1 4F2 54E 0 188 17E 17E 18F 1A5 19F 173 177 120E 51F 50E 508 4F0 4D8 4CC 4B4 4A7 49B 490 47D 476 46A 458 1 RSSI1 3F800000 00000000 FFF92230 2710 10F 9A 9D 9D 97 93 8D 8D 87 87 7D 80 7D 84 87 80 80 87 87 87 8D 8A 80 87 84 8A 8A 87 87 8A 90 90 8D 8A 8A 8A 8A 97 A3 C6 B0 97 8A 84 93 9A 93 8A 8D 8A 8A 84 87 80 7D 80 77 77 71 6D 67 67 67 6A 64 64 67 6D 67 6D 71 6D 6D 71 74 77 74 7A 7A 7D 87 80 87 87 8D 93 90 90 87 77 6D 61 97 0 AD B3 B3 B9 B9 B9 B9 BD B9 BD B9 BD B9 B9 C0 C0 C0 C6 C9 C3 C3 C9 C6 CC C9 CF E6 DF E6 DF D6 CF C6 C6 C9 C9 C0 CF D9 D9 D9 D9 DC D9 D9 D3 CF D6 D6 CF D3 CC D3 CF CF CC 0 B0 AD AA AD A6 A3 A6 A0 A6 A0 9A 9A A0 A0 A3 A3 9D 9D A0 A3 A6 AA AA A6 A3 0 0 8A 93 93 93 93 93 97 93 97 97 97 97 93 97 9A 97 9A 9A 9A A0 A0 A3 A6 A3 A3 A6 A6 A0 9D 9A 9D 9A 93 93 97 93 97 93 97 93 97 93 97 93 90 90 90 90 90 90 97 8D 90 8D 8A 8A 8A 80 0 0 0 93 9A 9A A3 9A A3 9A 9A 9A 0 87 97 9A 7D 64 4B 41 3B 31 90 9A A6 AA AA AA AA AD B0 B3 B0 B3 B6 B3 0 1 B not defined 0 0 0";

    private String contAnswer1 = "sEA LMDscandata 1";
    private String contAnswer2 = "sSN LMDscandata 1 1 107375C 0 0 E784 E789 3D611E4A 3D612D1F 0 0 1 0 0 5DC 36 0 1 DIST1 3F800000 00000000 FFF92230 2710 10F 1B8 1B3 1B9 1BD 1F9 22B 218 209 1F9 1FB 1F4 1EB 1E0 1D7 1CE 1C6 1BE 1B8 1BB 1B6 1B1 193 1A6 201 2B7 2 671 5CE 4FE 0 15A 156 14D 142 149 151 157 15B 1EF 254 6B5 621 5E9 C93 5AE 561 536 506 0 F0 176 191 18C 18B 189 18A 18F 18A 18F 191 18C 18B 187 17E 178 160 143 12F 11B 104 EA CA A9 A6 A2 9F A0 9D 9E 9E 9C 9C 9A 98 98 97 93 93 91 8C 8B 8B 88 8A 83 8B 91 8C 89 8C 8D 88 87 8E 5A 0 0 610 5FE 5ED 5DE 5D6 5D1 5C4 5B6 5B6 5AF 5A1 596 595 591 592 589 584 57D 57B 579 571 571 56F 56B 577 573 576 56B 56B 56C 56E 577 579 57C 57C 586 58A 58E 595 593 59B 0 301 30A 30A 30E 316 31A 31F 323 32D 32F 335 33E 345 350 358 362 369 36F 6A0 6B6 6D1 723 225 206 22C 235 21C 1E9 1D2 1C0 1B8 16E 10C 6F9 6E3 6D0 6B0 69D 684 678 65F 652 63B 628 617 607 5FC 5EF 5DB 5D2 5C6 5C0 58D 575 0 1A E5 E5 E6 E8 E6 E7 E6 EB E2 E3 EC E7 E2 E7 F0 EA E8 E8 E5 E8 EB EE EF EE F1 F0 F4 F4 F0 F4 F5 F9 F9 F8 FC 101 103 106 17D 333 34C 0 5CD 5DC 5E0 5AE 57B 550 54E 58E 192 1C6 1F3 1FC 1F2 1A1 1AA 2 AA6 419 418 562 56A 562 548 535 1 RSSI1 3F800000 00000000 FFF92230 2710 10F AA AA AA A3 9A 97 87 8A 8A 8A 8D 8D 8D 90 8D 8D 90 8A 90 8D 93 8D 7D 6A 5E 0 A0 6D 74 0 80 87 9A B9 AA 8A 84 74 67 80 5B 74 31 25 57 64 67 80 0 44 7A 8A 8A 87 87 80 80 7A 7A 77 7A 74 74 77 74 77 6D 74 71 6D 6D 71 6D 6D 77 77 77 7D 80 84 87 8A 8A 90 93 8D 93 93 90 93 93 97 9A A0 A0 A6 A3 9A 93 90 80 74 71 64 61 0 0 B3 B6 BD BD BD BD C0 BD C3 C3 C3 C0 C3 C6 C6 C6 C6 C6 C6 CC CC D3 D3 D9 E6 E6 E6 D9 D9 CF CC CC C9 C9 C3 C9 C9 C6 C6 C0 BD 0 D6 D6 D3 CF D3 CF D3 CF CF C9 CC CC CC CC CC C9 C6 B6 A3 A0 8D 71 71 84 8D 87 84 87 87 8A 87 90 9A 9D A3 A6 A3 A3 A0 A6 AA AD AD AD AD AD B0 B0 AD B3 B0 B0 A0 A0 0 80 93 9A 97 9A 97 9A 9A 9D 97 97 A0 9D 97 9D A0 9D 9A 93 97 93 93 97 93 93 97 97 97 90 90 93 93 97 97 90 8D 8D 8D 87 57 7A 84 0 AA AD A3 90 8D 9D 90 87 80 93 8A 8D 74 44 48 0 48 87 7D 93 A0 A3 A3 A3 0 1 B not defined 0 0 0";

    private String contAnswerStop = "sEA LMDscandata 0";

    public HardwareMock () throws Exception {
        this.foobar = "";

        this.connectionSocket = null;
        ServerSocket serverSocket = new ServerSocket(2112, 0);
        while (true) {
            if (this.connectionSocket == null) {
                LOGGER.info("this.connectionSocket == null");
                this.connectionSocket = serverSocket.accept();
            }
            if (this.bufferedReader == null) {
                LOGGER.info("this.bufferedReader == null");
                this.bufferedReader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                this.outputStream = new DataOutputStream(connectionSocket.getOutputStream());
            }
            byte temp=0;
            while((temp = (byte)this.bufferedReader.read()) != -1){
                receivedByte(temp);
                LOGGER.trace("receivedByte ended");
            }
            this.bufferedReader.close();
            this.bufferedReader = null;
            LOGGER.info("bufferedReader reset");

            this.connectionSocket.close();
            this.connectionSocket = null;
            LOGGER.info("connectionSocket reset");
        }

    }

    public void receivedByte(byte value) throws IOException {
        LOGGER.trace("receivedByte '{}'", (char)value);

        switch (value){
            case 2:
                this.foobar = "<STX>";
                LOGGER.info("STX received, clearing String.");
                break;
            case 3:
                this.foobar = this.foobar + "<ETX>";
                LOGGER.info("ETX received. String is now: {}", this.foobar);

                if (this.foobar.contains("sRN LMDscandata")) {
                    LOGGER.info("Single Scan requested");

                    this.outputStream.write(DATA_START);
                    this.outputStream.write(singleAnswer.getBytes());
                    this.outputStream.write(DATA_END);
                    this.outputStream.flush();
                }

                if (this.foobar.contains("sEN LMDscandata 1")) {
                    LOGGER.info("cont Scan START requested");

                    // ACK request
                    this.outputStream.write(DATA_START);
                    this.outputStream.write(contAnswer1.getBytes());
                    this.outputStream.write(DATA_END);
                    this.outputStream.flush();

                    // Measurement data
                    this.outputStream.write(DATA_START);
                    this.outputStream.write(contAnswer2.getBytes());
                    this.outputStream.write(DATA_END);
                    this.outputStream.flush();
                }

                if (this.foobar.contains("sEN LMDscandata 0")) {
                    LOGGER.info("cont Scan STOP requested");

                    // ACK request
                    this.outputStream.write(DATA_START);
                    this.outputStream.write(contAnswerStop.getBytes());
                    this.outputStream.write(DATA_END);
                    this.outputStream.flush();
                }
                break;
            default:
                this.foobar = this.foobar + (char)value;
        }
    }

    public static void main(String[] args) throws Exception {
        HardwareMock hw = new HardwareMock();
    }
}
