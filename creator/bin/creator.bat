set CURRDIR=%CD%
cd %~dp0..

set DIR=bin
set DIR3=%DIR%/thirdparty
set CP="%DIR%/com.technobear.jar;%DIR3%/xmlrpc-client-3.1.3.jar;%DIR3%/xmlrpc-common-3.1.3.jar;%DIR3%/ws-commons-util-1.0.2.jar"
java -cp %CP% com.technobear.eigenharp.creator.CreatorApp %*
cd %CURRDIR%