# Should follow guidelines: https://fedoraproject.org/wiki/Packaging:Java

%define __jar_repack 0
Name:		sensi-monitor
Version:	1.0
Release:	1%{?dist}
Summary:	Program to monitor Sensi thermostats for changes.

Group:		Application
License:	MIT
URL:		https://github.com/ianhattendorf/sensi-monitor
Source0:	https://github.com/ianhattendorf/sensi-monitor/archive/v%{version}.tar.gz

BuildArch:	noarch
# should require maven-local?
BuildRequires:	java-headless >= 1:1.8,javapackages-tools,systemd
Requires:	/usr/sbin/useradd,/usr/bin/getent,systemd

%description
Program to monitor Sensi thermostats for changes.

%prep
%setup -q


%build
mvn clean package

%pre
/usr/bin/getent group %{name} > /dev/null || /usr/sbin/groupadd -r %{name}
/usr/bin/getent passwd %{name} > /dev/null || /usr/sbin/useradd -r -d /opt/%{name} -s /sbin/nologin -g %{name} %{name}

%post
%systemd_post %{name}.service

%preun
%systemd_preun %{name}.service

%postun
%systemd_postun_with_restart %{name}.service

%install
install -D -p -m 644 target/%{name}-1.0-SNAPSHOT.jar %{buildroot}/opt/%{name}/bin/%{name}.jar
install -D -p -m 644 target/classes/application-secrets.properties.example %{buildroot}/opt/%{name}/config/application-secrets.properties
install -D -p -m 644 target/classes/application.properties %{buildroot}/opt/%{name}/config/application.properties
install -D -p -m 644 rpm/%{name}.service %{buildroot}%{_unitdir}/%{name}.service

%files
%doc
"/opt/%{name}/bin"
%config(noreplace) %attr(600, %{name}, %{name}) "/opt/%{name}/config/application-secrets.properties"
%config(noreplace) "/opt/%{name}/config/application.properties"
"%{_unitdir}/%{name}.service"

%changelog

