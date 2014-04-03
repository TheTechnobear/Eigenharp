Eigenharp
=========

Eigenharp related setups, and script, and agents

at the top of github webpage you will see a drop down of BRANCHES, this is important!!!

General guide lines for branches... (see below for specifics)
master, always commit changes to this branch
master, use this for everything EXCEPT agent/binaries 
numbered branches, always pickup agent binaries from here, from the EigenD version you are using... see below



master : dev branch, this is where ALL changes are made, if you make changes in other branches, then will NOT be propagated to other versions  
2.0 : release branch, this contains agents build scripts for 2.0 built under Mac OSX 10.8 ONLY, it is compatible with Eigenlabs 2.0.74 release
2.0-m : release branch, this contains agents build scripts for 2.0 built under Mac OSX 10.9+ (Mavericks, hence the -m) , it is NOT compatible with Eigenlabs 2.0.74 release
2.1 : release branch, this contains agents build scripts for 2.1 built under Mac OSX 10.9+ , it is NOT compatible with Eigenlabs 2.0.74 release


master, will always follow the latest development version of EigenD, so at present this is 2.1/Mac OSX 10.9, it is therefore the same as 2.1
However, to avoid confusion, the binaries of the agents, will only ever be put on the numbered builds
from time to time, master may contain development/testing code - thay may not have been released





If you would like to contribute to this repository, just contact me on G+ Eigenharp Community - TheTechnobear

If you would like to redistribute any of the contents here, that is fine, my only request is include a reference back to this repository as the original source.
(This allows users to come back to here, to see if there are any updates etc)
http://github.com/TheTechnobear/Eigenharp

Tree contents:
Creator - creator tool, change into this directory for README about creator
Bin - useful mac osx shell scripts
Vi - useful additions for VI
Setups - contain setups for Eigenharps, these are binary files
Scripts - contains scripts to do various things, they all have .bc extensions
Agents - binaries and source code for my agents


