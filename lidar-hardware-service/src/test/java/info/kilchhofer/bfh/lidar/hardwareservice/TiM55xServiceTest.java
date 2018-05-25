package info.kilchhofer.bfh.lidar.hardwareservice;

import ch.quantasy.mqtt.gateway.client.ConnectionStatus;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.message.MessageReceiver;
import info.kilchhofer.bfh.lidar.hardware.mock.HardwareMock;
import info.kilchhofer.bfh.lidar.hardwareservice.contract.LidarServiceContract;
import info.kilchhofer.bfh.lidar.hardwareservice.contract.status.LidarState;
import laser.scanner.IScanOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TiM55xServiceTest {
    private static final Logger LOGGER = LogManager.getLogger(TiM55xServiceTest.class);
    private LidarServiceContract lidarServiceContract;
    private static GatewayClient gatewayClient;
    private static TiM55xService tiM55xService;
    private static URI mqttURI;
    private static String mqttClientName;
    private static String instanceName;
    private static String computerName;
    ConnectionStatus resultConnectionStatus;
    LidarState resultLidarState;
    static int LIDAR_PORT = 2112;
    static String LIDAR_IP = "192.168.91.2";

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

    @BeforeClass
    public static void setUpBeforeClass() {
        mqttURI = URI.create("tcp://127.0.0.1:11234");
        //mqttURI = URI.create(DEFAULT_BROKER_ADDRESS);
        Long timeStamp = System.currentTimeMillis();
        instanceName = (timeStamp % 10000) + "@" + computerName;
        mqttClientName = instanceName;

        try {
            // Embedded Broker
            LOGGER.info("Starting Embedded Broker...");
            EmbeddedLauncher embeddedLauncher = new EmbeddedLauncher();

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
            gatewayClient = new GatewayClient<TiM55xServiceTestContract>(mqttURI, mqttClientName, new TiM55xServiceTestContract(instanceName));
            gatewayClient.connect();
            Thread.sleep(1000);

        } catch (MqttException e) {
            Assert.fail("MqttException during Setup");
            LOGGER.error("Setup Exception: ", e);
        } catch (IOException e) {
            Assert.fail("IOException during Setup");
            LOGGER.error("Setup Exception: ", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void bringTiM55xServiceUp() {
        LOGGER.info("bringTiM55xServiceUp...");
        // TiM Service
        try {
            tiM55xService = new TiM55xService(mqttURI, "SomeTestingWithTiM55x", "SomeTestingWithTiM55x",LIDAR_IP, LIDAR_PORT);
            Thread.sleep(2000);
            
        } catch (MqttException e) {
            Assert.fail("MqttException during Setup");
            LOGGER.error("Setup Exception: ", e);
        } catch (IOException e) {
            Assert.fail("IOException during Setup");
            LOGGER.error("Setup Exception: ", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void subscribeStatus() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        LOGGER.info("subscribing to Lidar Service Topic '{}' ...", tempLidarServiceContract.STATUS_CONNECTION);

        MessageReceiver messageReceiver = new MessageReceiver() {
            @Override
            public void messageReceived(String topic, byte[] payload) throws Exception {
                LOGGER.trace("Payload: " + new String(payload));
                resultConnectionStatus = (ConnectionStatus) gatewayClient.toMessageSet(payload, ConnectionStatus.class).last();
                latch.countDown();
            }
        };

        this.gatewayClient.subscribe(tempLidarServiceContract.STATUS_CONNECTION, messageReceiver);
        latch.await(100, TimeUnit.SECONDS);
        Assert.assertEquals("online", resultConnectionStatus.value);
    }

    @Test
    public void subscribeState() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        // Subscribe to hardware status
        gatewayClient.subscribe(tempLidarServiceContract.STATUS_STATE, (statusTopic, statusPayload) -> {
            LOGGER.trace("STATUS_STATE Payload: " + statusPayload);
            resultLidarState = (LidarState) gatewayClient.toMessageSet(statusPayload, LidarState.class).last();
            LOGGER.info("subscribeState: {}", resultLidarState);
            latch.countDown();
        });
        latch.await(100, TimeUnit.SECONDS);
        Assert.assertEquals(IScanOperator.State.STANDBY, resultLidarState.state);
    }

}
