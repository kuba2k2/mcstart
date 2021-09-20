# syntax=docker/dockerfile:1

ARG TAG=latest
FROM itzg/minecraft-server:$TAG

COPY docker-install.sh /
RUN chmod +x /docker-install.sh && /docker-install.sh
