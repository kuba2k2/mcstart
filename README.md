# MCStart
Automatically start a Minecraft server whenever a whitelisted player tries to join.

[Download v1.0.2](https://github.com/kuba2k2/mcstart/releases/tag/v1.0.2)

## Usage

The project is now best suited for usage with Docker (or Docker Compose). An example is provided below.

The legacy simple command guide is attached at the end of this README.

### With Docker Compose

The MCStart Docker image is based on [itzg/minecraft-server](https://hub.docker.com/r/itzg/minecraft-server/), making most its options available.

- Create an `mcstart.env` file with your configuration options of choice. You can use the [sample file `.env.example`](https://raw.githubusercontent.com/kuba2k2/mcstart/master/.env.example).
- Configure the file according to **Configuration** section below.
- Create a `docker-compose.yml`, like the attached sample.
- Choose a Java version (`mcstart:latest-java8` or `mcstart:latest-java16`)
- Set the desired options for [itzg/minecraft-server](https://hub.docker.com/r/itzg/minecraft-server/), as described in their [README](https://github.com/itzg/docker-minecraft-server/blob/master/README.md). You can use the `environment:` section or another `.env` file.
- Run `docker-compose up` to start the container and watch its output

```yml
version: "3.9"
services:
  mc1:
    env_file:
      # configure all MCStart settings in this env file
      - mcstart.env
    environment:
      # configure all itzg/minecraft-server settings
      - EULA=TRUE
      - TYPE=VANILLA
      - VERSION=1.7.2
    # choose either java8 or java16, depending on your Minecraft version
    image: kuba2k2/mcstart:latest-java8
    # set the external server port (do not change the second number)
    ports:
      - 25565:25565
    tty: true
    stdin_open: true
    # restart the container on reboots
    restart: unless-stopped
    # attach a named volume with the server's data
    volumes:
      - mc1:/data
volumes:
  mc1: {}
```
(this sample file is available [here](https://raw.githubusercontent.com/kuba2k2/mcstart/master/docker-compose.yml))

## Configuration

Starting with v1.0.0, the configuration is done using environment variables.
The program reads `.env` and `server.properties` files automatically,
making it unnecessary to configure anything to use the program in the most basic mode. All values configurable in `.env` may be specified as environment variables. Such values are then ignored in `.env`.

The defaults (and a ready-to-use template) are provided in `.env.example`, which needs to be copied, renamed accordingly and edited.

Some values (like server port) are read from `server.properties` (if found) instead of `.env`. These are marked with `!`.
Some values are read from `server.properties` only if not provided in `.env`.

You can use color codes (`&`) in all string properties (well, except hostnames).

The `$USERNAME` placeholder is available in `MCS_MSG_STARTING` and `MCS_MSG_NOT_WHITELISTED`.

### Auto Stop

MCStart can automatically stop the server if no players are online, to save resources. It works by querying the server status periodically and running the `MCS_AUTO_STOP_COMMAND` (`stop` by default) after a configurable timeout. The feature works on all servers supporting the legacy Server List Ping protocol (tested on vanilla 1.2.5, 1.6.4, 1.7.2, 1.17.1, but should work on all versions).

- `MCS_AUTO_STOP_POLLING_DELAY` - used to start querying the server after a delay from the time it's started by a joining player
- `MCS_AUTO_STOP_POLLING_INTERVAL` - how often to query the server
- `MCS_AUTO_STOP_TIMEOUT` - how long to wait after last player leaves

### Protocol Matching

When a server is incompatible with the Minecraft client's version, the GUI shows a red version name text in the multiplayer screen. This is used by MCStart to present an informational string, such as "Server Stopped".

The string (`MCS_VERSION_NAME`) is shown only if the server's protocol version (`MCS_VERSION_PROTOCOL`) mismatches the client's one. It's set to `1` by default to occur to all clients.

Minecraft 1.3.x and older have no information about the server's protocol version, so the version name is not used at all.

However, Minecraft 1.4-1.6 won't allow a player to connect if the version is incompatible. This makes him unable to start the server. A feature, enabled by default with `MCS_MATCH_PROTOCOL_LEGACY`, is provided to allow older clients to start the server. This allows version 1.6.x and 1.5.2 clients to start the server (MC 1.4-1.5.x do not send the version during SLP).

`MCS_MATCH_PROTOCOL_MODERN` can be used to always send a matching version to 1.7+ clients. Thus, the version name is never shown. It's disabled by default.

The recommended settings, suitable for most use cases, depending on the actual server version:
```properties
# MC 1.7+
MCS_MATCH_PROTOCOL_MODERN=false
MCS_VERSION_PROTOCOL=1
MCS_VERSION_NAME=Stopped
# MC 1.6
MCS_MATCH_PROTOCOL_LEGACY=true
# MC 1.4-1.5
MCS_MATCH_PROTOCOL_LEGACY=true
MCS_VERSION_PROTOCOL=1 # set this to the correct protocol version
# MC 1.3 and older
# settings don't matter here
```

### Whitelist

You can enable (`MCS_WHITELIST`) a whitelist of players allowed to start the server.
The whitelist is enabled by default IF the server's whitelist is also enabled.

MCStart uses the server's whitelist, by default (`MCS_WHITELIST_SERVER`), and uses `whitelist.json` or `white-list.txt` files (to maintain compatibility with old versions).

You can supply a custom `whitelist.txt` file (`MCS_WHITELIST_FILE`) with a format of one nickname per line. The `MCS_WHITELIST_SERVER` has to be disabled then.

## Usage (legacy)

First, copy the `.env.example` as `.env`. Open the file and configure it, according to **Configuration** above. Put the file in your server directory.

### As a command
The working directory is your server directory.

`java -jar mcstart.jar <cmdline>`
- `cmdline` - a command to run when the server should start

Examples:
- `java -jar mcstart.jar ./start-minecraft.sh`
- `java -jar mcstart.jar java -jar -Xmx1G -Xms1G spigot.jar`

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
