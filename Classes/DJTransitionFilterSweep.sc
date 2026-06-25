// Filter sweep — A closes down with a low-pass that drops from 18 kHz to
// 400 Hz while its level holds, then B opens with the opposite sweep
// (LPF from 400 Hz up to 18 kHz) plus a level ramp. Both decks need a
// `\filterType` arg that switches the LPF in (1 = lowpass).

DJTransitionFilterSweep : DJTransition {

    *prepare { |a, b|
        a.set(\filterType, 1, \cutoff, 18000);
        b.set(\filterType, 1, \cutoff, 400, \level, 0);
    }

    *curve { |t|
        ^(
            a: (
                // Log sweep down — perceptually even from open to closed.
                cutoff: 18000 * pow(400 / 18000, t)
            ),
            b: (
                level: sqrt(t),
                cutoff: 400 * pow(18000 / 400, t)
            )
        )
    }

    *finalise { |a, b|
        a.set(\level, 0, \filterType, 0);
        b.set(\filterType, 0, \cutoff, 18000);
    }
}
