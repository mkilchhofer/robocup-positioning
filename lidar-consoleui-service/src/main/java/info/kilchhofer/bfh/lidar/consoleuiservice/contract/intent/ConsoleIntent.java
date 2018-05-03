package info.kilchhofer.bfh.lidar.consoleuiservice.contract.intent;

import ch.quantasy.mqtt.gateway.client.message.AnIntent;

public class ConsoleIntent extends AnIntent{
    public String consoleMessage;

    public void ConsoleIntent(String consoleMessage ){
        this.consoleMessage = consoleMessage;
    }
    public void ConsoleIntent(){
    }
}
