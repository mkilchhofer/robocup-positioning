package info.kilchhofer.bfh.robocup.lidar.service.binding;

import ch.quantasy.mqtt.gateway.client.message.AnIntent;
import ch.quantasy.mqtt.gateway.client.message.annotations.Nullable;
import info.kilchhofer.bfh.robocup.common.LoggerSettings;

public class LidarIntent extends AnIntent {
    @Nullable
    public LidarCommand command;

    @Nullable
    public LoggerSettings loggerSettings;

    public LidarIntent(LidarCommand command){
        this.command = command;
    }

    public LidarIntent(LoggerSettings loggerSettings) {
        this.loggerSettings = loggerSettings;
    }

    // needed for deserialization
    public LidarIntent(){
    }

}
