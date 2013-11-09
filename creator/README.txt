to use creator
=============

Mac OSX
bin/creator.sh device configfile
e.g. 
bin/creator.sh pico conf/basicdemo.conf

Windows
bin\creator.cmd device configfile
e.g. 
bin\creator.cmd pico conf/basicdemo.conf


Note: you will need to have java installed, and running from the command line.
This is normally the case but if not, then please install the latest version of java.

Documentation
=============

documentation is available on the github WIKI
the basics are covered here:
http://github.com/TheTechnobear/Eigenharp/wiki/CREATOR-GETTING-STARTED

a more in depth coverage of how to understand setups and 'modules' and developing your own is here:
http://github.com/TheTechnobear/Eigenharp/wiki/CREATOR-DEVELOPMENT

the obligatory FAQ is here:
http://github.com/TheTechnobear/Eigenharp/wiki/CREATOR-FAQ


Contents
========
bin - contains programs/scripts to execute
conf - contains config files to be used with creator.sh/creator.cmd  (device subdirectories contain configs specific to device)
templates - contains templates files to be used within config files (device subdirectories contain templates specific to device, keymaps hold keymapping files)


IMPORTANT NOTE:
Ive recently updated to creator 2.0, Ive updated the wiki but there may be some 'oddities'
things to look out for are:
* modules have now been renamed to templates
* createsetup.sh is now creator.sh/creator.cmd
* Creator v2 does work under windows (v1 did not!)


The original creator (1.0) can be found in creator/1.0, to use:
cd 1.0
bin/createsetup.sh pico conf/basicdemo.conf
However, this is now depreciated, and will be removed once 2.0 has had more user testing.
