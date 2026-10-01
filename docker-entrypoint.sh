#!/bin/sh
set -e

# Configure Tomcat to listen on the port specified by Render's PORT environment variable
if [ -n "$PORT" ]; then
  sed -i "s/port=\"8080\"/port=\"$PORT\"/g" "${CATALINA_HOME}/conf/server.xml"
fi

exec "$@"
