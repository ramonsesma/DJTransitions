// DJTransitions quickstart
// Inspect an automation curve without booting scsynth.
(
DJTransitions.names.postln;
~frames = DJTransitions.renderFrames(\bassSwap, 4, 30);
~frames.first.postln;
~frames.last.postln;
)

// With real channel-strip Synths:
// ~routine = DJTransitions.play(\filterSweep, ~deckA, ~deckB, 8);
// ~routine.stop;
