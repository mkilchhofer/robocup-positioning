package info.kilchhofer.bfh.lidar.consoleuiservice.event;

import ch.quantasy.mqtt.gateway.client.message.AnEvent;

public class ConsoleKeyPressEvent extends AnEvent{
    public Character character;
    public ConsoleKeyPressEvent(Character character){
        this.character = character;
    }

    public ConsoleKeyPressEvent() {
    }
}
