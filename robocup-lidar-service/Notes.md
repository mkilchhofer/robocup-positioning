# Tests
Um manuell den LidarService zu testen, können folgende GUIs verwendet werden:
- http://hobbyquaker.github.io/mqtt-admin/#publish
- http://www.hivemq.com/demos/websocket-client/
- MQTTLens (Chrome Extension)

## Topic:
`Robocup/Lidar/U/<instanceID>/I`

## Content:
```yaml

---
- timeStamp: 65630678285913
  command: SINGLE_MEASUREMENT
```




# Übersicht Telegramme (zum Sensor)
Kommandoarten Anfrage:
- Anfrage
  - sRN: **S**OPAS **r**ead by **n**ame
  - sEN: **S**OPAS **e**vent by **n**ame

- Antworten:
  - sRA: **S**OPAS **r**ead **a**nswer
  - sSN: **S**OPAS **s**ent event

Kommandos:
- LMDscandata: Anforderung von Daten
