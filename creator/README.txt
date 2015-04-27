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


Iterators:

Lets say we have a config lines like:
midi/linkmidirig.bc:percussion keygroup:midi rig 1:1:11:1:1:
midi/linkmidirig.bc:percussion keygroup:midi rig 2:2:11:1:2:
midi/linkmidirig.bc:percussion keygroup:midi rig 3:3:11:1:3:

we can now replace this with:
Iterate:%X%:midi rig 1,midi rig 2,midi rig 3:midi/linkmidirig.bc:percussion keygroup:%X%:%IDX%:11:1:%IDX:

so whats going on here...
generally the format is:
Iterate:VAR(=offset):LIST:existing config line to iterate 
VAR is the variable name %X% in my example above
optionally = offset can be used to create an offset index value, see below.
LIST is the list of values to use (comma separated)
in the existing config lines, you then just use VAR where you would have used the value before.
there are two special variables:
%IDX% which runs from 1 to list length.
%IDX-OFFSET%, this is an offset index value
e.g. 
Iterate:%X%=9:midi rig 1,midi rig 2,midi rig 3:midi/linkmidirig.bc:percussion keygroup:%X%%IDX%:11:1:%IDX:

linkmidirig.bc will be called three times, substituting as follows:  
first time %X% with midi rig 1 %IDX% with 1 %IDX-OFFSET% with 9
next time %X% with midi rig 2 %IDX% with 2 %IDX-OFFSET% with 10
next time %X% with midi rig 3 %IDX% with 3 %IDX-OFFSET% with 11

(IDX-OFFSET is used mainly if you have to split iterations over multiple lines due needing to call different templates)




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
