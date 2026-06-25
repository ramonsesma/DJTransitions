// Bass swap — A and B both play at full level, but A's low band falls
// while B's low band rises. The single low-frequency source moves cleanly
// from one deck to the other; no level dip in the mids/highs. Classic
// transition for genres with continuous kick (house, techno).

DJTransitionBassSwap : DJTransition {

    *prepare { |a, b|
        // B comes in instantly at full level but with its bass killed.
        b.set(\level, 1, \lo, 0, \mid, 1, \hi, 1);
        // A holds its level; only its low band moves during the curve.
        a.set(\lo, 1);
    }

    *curve { |t|
        ^( a: ( lo: 1 - t ), b: ( lo: t ) )
    }

    *finalise { |a, b|
        // Cut A entirely at the end so the next transition starts clean.
        a.set(\level, 0);
    }
}
