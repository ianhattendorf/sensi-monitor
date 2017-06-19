#!/bin/sh

set -e

/usr/bin/getent group sensi-monitor > /dev/null || /usr/sbin/groupadd -r sensi-monitor
/usr/bin/getent passwd sensi-monitor > /dev/null || /usr/sbin/useradd -r -d /opt/sensi-monitor -s /sbin/nologin -g sensi-monitor sensi-monitor
