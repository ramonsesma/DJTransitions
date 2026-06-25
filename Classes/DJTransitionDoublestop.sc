// Doublestop — both decks are heard together at near-full level for the
// whole window with a quick hand-off at the very end. Used when both
// tracks are harmonically locked (same / relative key) and you want the
// layered sound for a moment before B takes over alone.

DJTransitionDoublestop : DJTransition {

    *prepare { |a, b|
        a.set(\level, 1, \lo, 1, \mid, 1, \hi, 1);
        b.set(\level, 0.9, \lo, 1, \mid, 1, \hi, 1);
    }

    *curve { |t|
        // Stay near full until the last 15 %, then drop A quickly.
        var aLevel = if (t < 0.85) { 1.0 } { (1 - ((t - 0.85) / 0.15)).max(0) };
        var bLevel = 0.9 + (0.1 * t);
        ^( a: ( level: aLevel ), b: ( level: bLevel ) )
    }

    *finalise { |a, b|
        a.set(\level, 0);
        b.set(\level, 1);
    }
}
