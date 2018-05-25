package info.kilchhofer.bfh.lidar.consoleuiservice.helper;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;

public class KeyPressHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(KeyPressHandler.class);
    private IKeyPress keyPressCallback;
    private Terminal terminal;

    public KeyPressHandler(IKeyPress keyPressCallback) throws IOException, InterruptedException {
        this.terminal = new DefaultTerminalFactory(System.out, System.in, Charset.forName("UTF8")).createTerminal();
        this.keyPressCallback = keyPressCallback;
    }

    @Override
    public void run() {
        while (true) {
            KeyStroke key = null;
            try {
                key = terminal.pollInput();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (key == null) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (key.getCharacter() == 'q'){
                LOGGER.info("'q' Pressed. Exiting...");
                System.exit(0);
            } else {
                LOGGER.info("Pressed '{}'.", key.getCharacter());
                keyPressCallback.keyPressed(key.getCharacter());
            }
        }
    }
}
