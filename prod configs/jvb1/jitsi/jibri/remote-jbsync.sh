#!/bin/bash

# Log process
exec 3>&1 4>&2
trap 'exec 2>&4 1>&3' 0 1 2 3
exec 1>/var/log/jbnode_F22G/remote_jnsync.log 2>&1

# Run sync
while true; do
  inotifywait  -t 60 -r -e modify,attrib,close_write,move,delete /var/jbrecord
  sudo su jbnode_F22G -c "rsync -Aax  --info=progress2 --remove-source-files --exclude '.*/' /var/jbrecord/ jbsync_f66a@sessions.onlinelearningsessions.com:/var/jbrecord"
  find /var/jbrecord -depth -type d -empty -not -path /var/jbrecord -delete
done
