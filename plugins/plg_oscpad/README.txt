ABLETON LIVE PAD 
RELEASE 0.4

THIS IS AN EXPERIMENT ONLY!!!!!
it is not finished, and has a number of shortcomings its really just a demo to play with at this stage

Ive played with it on a demo of Live 9, but should work on 8 too


TO USE:
a) first install LIVE OSC , following instructions at
http://livecontrol.q3f.org/ableton-liveapi/liveosc/
b) In ableton, setup LiveOSC as control surface in preferences
c) Install plugin into EigenD. under /usr/pi/release-2.0.74-stable/plugins/Eigenlabs/   (or you can add to your path if you know how ;))
cd /usr/pi/release-2.0.74-stable/plugins/Eigenlabs/
tar xvf ~/Downloads/plg_oscpad.tar
d) start EigenD, and in workbench create a new 'osc pad' agent, and connect it to the keyboard (or probably a keygroup too, but not tried that ;))


press keys on your pico (etc) to record/launch clips.
green = playing
orange = availble not playing
red = recording
off = nothing there


known issues :
main issues fixed in 0.2-0.4


change log
0.1 - initial release
0.2 - improved colours, and recording behaviour, fix toggle clip
0.3 - initial load state
0.4 - introduce moveable view port, fix mem leak, consistently call osc pad




