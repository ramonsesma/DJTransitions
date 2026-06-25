// Scribble — quick alternating chops between A and B over the window,
// each chop slightly longer than the last, ending with B fully in. A
// crowd-pleaser when both tracks share tempo + key.
//
// The chop count defaults to 8; override via the class variable `chops`
// (subclass + set if you need a different feel).

DJTransitionScribble : DJTransition {
    classvar <>chops = 8;

    *prepare { |a, b|
        a.set(\level, 1);
        b.set(\level, 0);
    }

    *curve { |t|
        // Square wave with rising-density edges. Use the chop count to
        // decide which deck is "on" at this t.
        var phase = (t * chops).floor;
        var onB = (phase % 2) > 0;
        // Tighten the duty cycle as t grows so B wins out by the end.
        var bWeight = (t * 1.2).min(1);
        ^if (onB) {
            ( a: ( level: (1 - bWeight) * 0.6 ), b: ( level: bWeight ) )
        } {
            ( a: ( level: 1 - (bWeight * 0.5) ), b: ( level: bWeight * 0.4 ) )
        }
    }

    *finalise { |a, b|
        a.set(\level, 0);
        b.set(\level, 1);
    }
}
