# MCStart
Automatically start a Minecraft server whenever a whitelisted player tries to join.

[Download v1.0.1](https://github.com/kuba2k2/MCStart/releases/tag/v1.0.1)

## Configuration

Refer to `.env.example` for configuration options:
```dotenv
# - all of the configuration values are optional
# - the defaults are specified below
# - fields marked with ! are read from server.properties, if available
#   (the value in .env is then ignored)
# - fields marked with ? are read from server.properties, only
#   if not specified in .env

# server directory path (used for reading server.properties and whitelist files)
SERVER_PATH=.
# ! server port - needs to be equal to the actual server port
SERVER_PORT=25565

# timeout for client connections, in ms (recommended)
MCS_SOCKET_TIMEOUT=5000
# enable matching protocol version for modern clients
MCS_MATCH_PROTOCOL_MODERN=false
# enable matching protocol version for legacy clients
MCS_MATCH_PROTOCOL_LEGACY=true
# default protocol version - clients will show player count
MCS_VERSION_PROTOCOL=1
# version name - shown on clients with incompatible protocol version
MCS_VERSION_NAME=Stopped

# query the server periodically and shut it down when empty
MCS_AUTO_STOP=false
# the hostname/IP address of the server to query (most likely localhost)
MCS_AUTO_STOP_HOSTNAME=localhost
# delay to begin querying after server starts, in seconds
MCS_AUTO_STOP_POLLING_DELAY=60
# how often to query the server, in seconds
MCS_AUTO_STOP_POLLING_INTERVAL=10
# how long does the server have to be empty to shut it down, in seconds
MCS_AUTO_STOP_TIMEOUT=600
# the command used to shut the server down
MCS_AUTO_STOP_COMMAND=stop

# online player count - when protocol version matches
MCS_PLAYERS_ONLINE=0
# ? max player count - when protocol version matches
MCS_PLAYERS_MAX=0

# ? Message of The Day - when MCStart is running
MCS_MSG_MOTD="The server is stopped, join to start."
# the message sent to the player starting the server
MCS_MSG_STARTING="Hi $USERNAME, the server is starting..."
# the message sent to players not whitelisted to start the server
MCS_MSG_NOT_WHITELISTED="You are not whitelisted to start this server."

# ? whether the server start whitelist is enabled
MCS_WHITELIST=false
# ? whether to use the server's whitelist
MCS_WHITELIST_SERVER=true
# file containing the whitelist entries (JSON or TXT)
MCS_WHITELIST_FILE=whitelist.txt
```
You can use color codes (`&`) in all string properties.

The `$USERNAME` placeholder is available in `MCS_MSG_STARTING` and `MCS_MSG_NOT_WHITELISTED`.

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
