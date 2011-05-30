Name: org.dicr.netflow
Version: %(date +%y%m%d)
Release: %(date +%H%M)dcr
License: Dicr
Group: System Environment/Daemons
URL:	http://dicr.org/projects/%{name}
Summary: NetFlow for Java
Source0: http://dicr.org/projects/%{name}/%{name}-%{version}.tgz
BuildArch: noarch
BuildRoot: %{_tmppath}/%{name}-%{version}-build
Requires: java
Requires: org.dicr.java-libs org.dicr.util org.dicr.traffic org.dicr.sys
Requires: org.dicr.java-container
BuildRequires: java-devel ant
BuildRequires: org.dicr.java-libs org.dicr.util org.dicr.traffic org.dicr.sys

%description
Java NetFlow Server and Libraries

%prep
%setup -q -n %{name}

%build

ant bin doc

%install
[ "$RPM_BUILD_ROOT" != "/" ] && %{__rm} -rf $RPM_BUILD_ROOT

%{__install} -D target/%{name}-%{version}-bin.jar $RPM_BUILD_ROOT%{_prefix}/java/lib/%{name}-%{version}.jar
%{__install} -D src/main/contribs/%{name}.init $RPM_BUILD_ROOT%{_initrddir}/%{name}
%{__install} -D src/main/config/log4j.xml $RPM_BUILD_ROOT%{_sysconfdir}/%{name}/log4j.xml
%{__install} -D src/main/config/server.xml $RPM_BUILD_ROOT%{_sysconfdir}/%{name}/server.xml
%{__install} -d $RPM_BUILD_ROOT%{_localstatedir}/log/%{name}

%clean
[ "$RPM_BUILD_ROOT" != "/" ] && %{__rm} -rf $RPM_BUILD_ROOT

%post
if [ $1 = 1 ]; then
    /sbin/chkconfig --add %{name} >/dev/null 2>&1 || :
    /sbin/service %{name} condrestart >/dev/null 2>&1 || :
fi

%preun
if [ $1 = 0 ]; then
    /sbin/service %{name} stop >/dev/null 2>&1 || :
    /sbin/chkconfig --del %{name} >/dev/null 2>&1 || :
fi

%postun
if [ $1 = 0 ]; then
    %{__rm} -rf %{_localstatedir}/run/%{name}.pid >/dev/null 2>&1 || :
    %{__rm} -rf %{_localstatedir}/lock/subsys/%{name} >/dev/null 2>&1 || :
    %{__rm} -rf %{_localstatedir}/log/%{name}.log >/dev/null 2>&1 || :
fi

%files
%defattr(755,root,root,755)
%{_initrddir}/%{name}

%defattr(640,root,root,750)
%config(noreplace) %{_sysconfdir}/%{name}
%{_localstatedir}/log/%{name}

%defattr(644,root,root,755)
%{_prefix}/java/lib/*.jar
%doc docs target/%{name}-%{version}-doc.jar

%changelog
* Sat Jun 19 2006 Igor A Tarasov <linux@dicr.org> : 060719
- initial build
