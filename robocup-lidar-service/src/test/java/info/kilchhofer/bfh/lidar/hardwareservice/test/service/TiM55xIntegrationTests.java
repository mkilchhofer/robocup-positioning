package info.kilchhofer.bfh.lidar.hardwareservice.test.service;

import ch.quantasy.mqtt.gateway.client.ConnectionStatus;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.message.MessageReceiver;
import info.kilchhofer.bfh.robocup.lidar.hardwaremock.HardwareMock;
import info.kilchhofer.bfh.robocup.lidar.service.TiM55xService;
import info.kilchhofer.bfh.robocup.lidar.service.binding.LidarServiceContract;
import info.kilchhofer.bfh.robocup.lidar.service.binding.LidarState;
import laser.scanner.IScanOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TiM55xIntegrationTests {
    private static final Logger LOGGER = LogManager.getLogger(TiM55xIntegrationTests.class);
    private static GatewayClient<TiM55xServiceTestContract> gatewayClient;
    private static TiM55xService tiM55xService;
    private static URI mqttURI;
    private static String mqttClientName;
    private static String instanceName;
    private static String computerName;
    ConnectionStatus resultConnectionStatus;
    LidarState resultLidarState;
    static int LIDAR_PORT = 2112;
    static String LIDAR_IP = "127.0.0.1";

    // Do not hardcode topics for autodetect service instances
    private final LidarServiceContract tempLidarServiceContract = new LidarServiceContract("+");

    static {
        LOGGER.info("Headless: " + java.awt.GraphicsEnvironment.isHeadless());
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            LOGGER.error("undefined hostname", ex);
            computerName = "undefined";
        }
    }

    @BeforeAll
    public static void setUpBeforeClass() {
        mqttURI = URI.create("tcp://127.0.0.1:11234");
        //mqttURI = URI.create(DEFAULT_BROKER_ADDRESS);
        Long timeStamp = System.currentTimeMillis();
        instanceName = (timeStamp % 10000) + "@" + computerName;
        mqttClientName = instanceName;

        try {
            // Embedded Broker
            LOGGER.info("Starting Embedded Broker...");
            // https://github.com/andsel/moquette/blob/master/
            // embedding_moquette/src/main/java/io/moquette/testembedded/EmbeddedLauncher.java
            EmbeddedBrokerLauncher embeddedBrokerLauncher = new EmbeddedBrokerLauncher();

            // Mock Hardware
            LOGGER.info("Starting Mock Hardware...");
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        new HardwareMock(LIDAR_PORT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            new Thread(r).start();
            Thread.sleep(2000);

            // GatewayClient
            LOGGER.info("Starting GatewayClient for Testing...");
            gatewayClient = new GatewayClient<>(mqttURI, mqttClientName, new TiM55xServiceTestContract(instanceName));
            gatewayClient.connect();
            Thread.sleep(1000);

        } catch (MqttException e) {
            fail("MqttException during Setup");

            LOGGER.error("Setup Exception: ", e);
        } catch (IOException e) {
            fail("IOException during Setup");
            LOGGER.error("Setup Exception: ", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Start TiM55x Service")
    public void bringTiM55xServiceUp() {
        LOGGER.info("bringTiM55xServiceUp...");
        // TiM Service
        try {
            tiM55xService = new TiM55xService(mqttURI, "SomeTestingWithTiM55x", "SomeTestingWithTiM55x",LIDAR_IP, LIDAR_PORT);
            Thread.sleep(2000);
            
        } catch (MqttException e) {
            fail("MqttException during Setup");
            LOGGER.error("Setup Exception: ", e);
        } catch (IOException e) {
            fail("IOException during Setup");
            LOGGER.error("Setup Exception: ", e);
        } catch (InterruptedException e) {
            fail("InterruptedException during Setup");
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Subscription to Connection Status Topic")
    public void subscribeStatus() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        LOGGER.info("subscribing to Lidar Service Topic '{}' ...", tempLidarServiceContract.STATUS_CONNECTION);

        MessageReceiver messageReceiver = new MessageReceiver() {
            @Override
            public void messageReceived(String topic, byte[] payload) throws Exception {
                LOGGER.trace("Payload: " + new String(payload));
                resultConnectionStatus = gatewayClient.toMessageSet(payload, ConnectionStatus.class).last();
                latch.countDown();
            }
        };

        this.gatewayClient.subscribe(tempLidarServiceContract.STATUS_CONNECTION, messageReceiver);
        latch.await(15, TimeUnit.SECONDS);
        assertEquals("online", resultConnectionStatus.value);
    }

    @Test
    @DisplayName("Subscription to Sensor State Topic")
    public void subscribeState() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        LOGGER.info("subscribing to Lidar Service Topic '{}' ...", tempLidarServiceContract.STATUS_STATE);

        MessageReceiver messageReceiver = new MessageReceiver() {
            @Override
            public void messageReceived(String topic, byte[] payload) throws Exception {
                LOGGER.trace("STATUS_STATE Payload: " + payload);
                resultLidarState = gatewayClient.toMessageSet(payload, LidarState.class).last();
                LOGGER.info("subscribeState: {}", resultLidarState);
                latch.countDown();
            }
        };

        gatewayClient.subscribe(tempLidarServiceContract.STATUS_STATE, messageReceiver);
        latch.await(15, TimeUnit.SECONDS);
        assertEquals(IScanOperator.State.STANDBY, resultLidarState.state);
    }

    @AfterAll
    public static void teardown(){
        try{
            LOGGER.trace("Starting Teardown");
            gatewayClient.disconnect();
            LOGGER.trace("gatewayClient.disconnect() done");
        } catch (Exception e){
            fail("Exception occured during Teardown.");
        }
    }
}
