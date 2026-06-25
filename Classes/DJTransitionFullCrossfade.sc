// Full crossfade — A's level ramps down while B's ramps up over the whole
// window. Equal-power square-root curves keep the perceived loudness
// flat at the midpoint (vs the audible dip a linear crossfade gives).

DJTransitionFullCrossfade : DJTransition {

    *prepare { |a, b|
        b.set(\level, 0, \lo, 1, \mid, 1, \hi, 1);
        a.set(\level, 1);
    }

    *curve { |t|
        ^(
            a: ( level: sqrt(1 - t) ),
            b: ( level: sqrt(t) )
        )
    }
}
