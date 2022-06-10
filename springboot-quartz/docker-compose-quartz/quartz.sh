#!/bin/bash

docker-compose -f quartz.yml stop

docker-compose -f quartz.yml rm --force

exist=$(docker inspect --format '{{.State.Running}}' quartz)

if [ "${exist}" != "true" ]; then
  ./delete_image.sh
fi

./build.sh springboot-quartz

docker-compose -f quartz.yml up -d
