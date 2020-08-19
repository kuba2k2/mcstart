# MCStart
Automatically start a Minecraft server whenever a whitelisted player tries to join.

[Download v0.2.1](https://github.com/kuba2k2/MCStart/releases/tag/v0.2.1)

## Config

```yaml
plugin:
  autoStopEnabled: true # whether to stop the server if all players left
  autoStopTimeout: 300 # 5 minutes
  startTimeout: true # whether to start the autoStopTimeout on server start
  startTimeoutDelay: 60 # delay the timeout on server start
standalone:
  serverPort: 25565 # should be equal to the server port
  socketTimeout: 5000 # timeout for client connections (recommended)

  protocolVersion: 1 # if equal to your MC client version, it will show player count (below)
  versionName: "Stopped" # if protocol version differs from the client, this text will be shown
  playersOnline: 0
  playersMax: 0

  motd: "The server is stopped, join to start."
  startingText: "Hi $USERNAME, the server is starting..."
  disconnectText: "You are not whitelisted to start this server."

  whitelistEnabled: true # allow only certain players to start the server
  whitelistUseServer: false # whether to use the server whitelist (plugins/../whitelist.json)
  whitelist:
   - Steve
   - Alex 
```
You can use color codes (`&`) in all string properties.
The `$USERNAME` placeholder is available in `startingText` and `disconnectText`.

## Usage

Put the .jar in `plugins` directory of your server (in order to use the server whitelist and auto stop functionality).

If used with a Bukkit/Spigot server, it will automatically shut down 
when no players are online (after a configurable timeout). MCStart will
be running a fake server again, waiting for a player to start the real server.

This behavior may be configured or disabled in the `plugin` section of the config.

### As a command
The working directory is your server directory.

`java -jar plugins/mcstart.jar <cmdline>`
- `cmdline` - a command to run when the server should start, e.g.

Examples:
- `java -jar plugins/mcstart.jar ./start-minecraft.sh`
- `java -jar plugins/mcstart.jar java -jar -Xmx1G -Xms1G spigot.jar`

### As a systemd service (example)
`/etc/systemd/system/minecraft-mcstart.service`

[Source](https://gist.github.com/nathanielc/9b98350ccbcbf21256d7)
```
[Unit]
Description=Minecraft server (MCStart)
After=network.target

[Service]
User=minecraft
Group=minecraft
KillMode=none
Restart=always
WorkingDirectory=/home/minecraft/server
ExecStart=/usr/bin/screen -DmS minecraft /usr/bin/java -jar plugins/mcstart.jar ./start.sh
ExecStop=/usr/bin/screen -p 0 -S minecraft -X eval 'stuff "say Server shuts down in 15 seconds!"\015'
ExecStop=/usr/bin/sleep 5
ExecStop=/usr/bin/screen -p 0 -S minecraft -X eval 'stuff "say Server shuts down in 10 seconds!"\015'
ExecStop=/usr/bin/sleep 5
ExecStop=/usr/bin/screen -p 0 -S minecraft -X eval 'stuff "say Server shuts down in 5 seconds!"\015'
ExecStop=/usr/bin/sleep 5
ExecStop=/usr/bin/screen -p 0 -S minecraft -X eval 'stuff "say Stopping server..."\015'
ExecStop=/usr/bin/screen -p 0 -S minecraft -X eval 'stuff "save-all"\015'
ExecStop=/usr/bin/screen -p 0 -S minecraft -X eval 'stuff "stop"\015'
ExecStop=/home/minecraft/server/wait.sh
ExecStop=/bin/kill -2 $MAINPID
ExecReload=/usr/bin/screen -p 0 -S minecraft -X eval 'stuff "reload"\015'

[Install]
WantedBy=multi-user.target
```

`/home/minecraft/server/start.sh`
```shell script
#!/bin/sh
cd /home/minecraft/server
# start the server and save its PID
/bin/sh -c 'echo $$ > ./minecraft.pid; exec /usr/bin/java -Xms1G -Xmx1G -jar spigot.jar nogui'
```

`/home/minecraft/server/wait.sh`
```shell script
#!/bin/bash
cd /home/minecraft/server
# wait for the server to terminate (when stopping the service)
/usr/bin/tail --pid=$(/usr/bin/cat ./minecraft.pid) -f /dev/null
```

#### Use the service
```shell script
systemctl start minecraft-mcstart # run MCStart
systemctl stop minecraft-mcstart # stop MC server AND MCStart

systemctl enable minecraft-mcstart # enable autostart with the system
systemctl disable minecraft-mcstart # disable autostart
```
