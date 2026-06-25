// Abstract base for DJ transition recipes. A subclass implements
//
//   *curve { |t| ^( a: ( <ctrlName>: <value>, ... ), b: ( ... ) ) }
//
// where t ∈ [0, 1] is the normalised position within the transition window
// (0 = start, 1 = end). The base class runs the curve at a configurable
// frame rate inside a Routine and applies each frame to the two synths via
// `.set` — so any SynthDef with matching control names works.
//
// Conventional control names used by the bundled subclasses are the ones
// the Studio Sesma DJ engine speaks (\level, \lo, \mid, \hi, \cutoff,
// \filterType). Rename in your own subclass if your channel strip uses
// different argument names.
//
// Usage:
//
//   ~routine = DJTransitionBassSwap.play(deckA, deckB, durationSec: 4);
//   // ...or stop early:
//   ~routine.stop;

DJTransition {

    // Subclass interface. Must return an Event ( a: ( ... ), b: ( ... ) )
    // where each inner Event maps control Symbols to numeric values.
    *curve { |t|
        ^this.subclassResponsibility(\curve)
    }

    // Optional. Subclasses can override to set static values (e.g. enabling
    // a filter type) before the curve starts running.
    *prepare { |synthA, synthB|
        // default no-op
    }

    // Optional. Subclasses can override to finalise (e.g. silence A after
    // the transition completes).
    *finalise { |synthA, synthB|
        // default no-op
    }

    // Run the transition. Returns the Routine so the caller can stop it
    // early. `fps` is how often the curve is sampled (default 30 frames/s).
    *play { |synthA, synthB, durationSec = 4, clock, fps = 30|
        var c = clock ?? { TempoClock.default };
        var steps = (durationSec * fps).asInteger.max(2);
        var stepDur = durationSec / steps;
        var routine;

        this.prepare(synthA, synthB);

        routine = Routine({
            (steps + 1).do({ |i|
                var t = (i / steps).min(1.0);
                var frame = this.curve(t);
                if (frame.notNil) {
                    if (frame[\a].notNil) {
                        frame[\a].keysValuesDo({ |k, v|
                            synthA.set(k, v);
                        });
                    };
                    if (frame[\b].notNil) {
                        frame[\b].keysValuesDo({ |k, v|
                            synthB.set(k, v);
                        });
                    };
                };
                stepDur.wait;
            });
            this.finalise(synthA, synthB);
        });

        ^routine.play(c)
    }

    // Sample the curve into an array of frames without running anything.
    // Useful for tests and for callers that want to push automation to NRT
    // scores rather than live synths.
    *renderFrames { |durationSec = 4, fps = 30|
        var steps = (durationSec * fps).asInteger.max(2);
        var stepDur = durationSec / steps;
        ^(steps + 1).collect({ |i|
            var t = (i / steps).min(1.0);
            var frame = this.curve(t);
            ( time: i * stepDur, t: t, frame: frame )
        });
    }
}
