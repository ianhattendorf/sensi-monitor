#!/bin/sh

set -e

/usr/bin/getent group %{name} > /dev/null || /usr/sbin/groupadd -r %{name}
/usr/bin/getent passwd %{name} > /dev/null || /usr/sbin/useradd -r -d /opt/%{name} -s /sbin/nologin -g %{name} %{name}
