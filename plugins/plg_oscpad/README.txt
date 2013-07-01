ABLETON LIVE PAD 


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
d) start EigenD, and in workbench create a new 'osc input' agent, and connect it to the keyboard (or probably a keygroup too, but not tried that ;))


press keys on your pico (etc) to record/launch clips.
green = recording/playing
orange = triggered
red = available but not playing/triggered
off = nothing there


known issues :
lots, main ones
- i dont get initial status from live, i just assume its blanks,
- recording state is a bit 'odd', you will get use to it, basically turns green when its starts recording.
- clip are probably upside down, basically I need to get they keyboard geometry to sort this out properly




