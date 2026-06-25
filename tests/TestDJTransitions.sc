// TestDJTransitions.run

TestDJTransitions : UnitTest {

    test_facade_names_lists_all_styles {
        var expected = [\bassSwap, \doublestop, \filterSweep, \fullCrossfade, \scribble];
        this.assertEquals(DJTransitions.names, expected);
    }

    test_facade_unknown_style_throws {
        this.assertException({
            DJTransitions.play(\nonsense, nil, nil, 1);
        }, Error, "unknown style should throw");
    }

    test_render_frames_has_correct_length {
        // fps 10 + 4 s window → 40 steps → 41 frames (inclusive endpoint).
        var frames = DJTransitions.renderFrames(\fullCrossfade, 4, 10);
        this.assertEquals(frames.size, 41);
        this.assertFloatEquals(frames.first[\t], 0);
        this.assertFloatEquals(frames.last[\t], 1.0);
    }

    test_full_crossfade_endpoints {
        var frames = DJTransitions.renderFrames(\fullCrossfade, 2, 50);
        var first = frames.first[\frame];
        var last  = frames.last[\frame];
        this.assertFloatEquals(first[\a][\level], 1.0, "A starts at 1");
        this.assertFloatEquals(first[\b][\level], 0.0, "B starts at 0");
        this.assertFloatEquals(last[\a][\level], 0.0, "A ends at 0");
        this.assertFloatEquals(last[\b][\level], 1.0, "B ends at 1");
    }

    test_full_crossfade_equal_power_at_midpoint {
        // At t = 0.5: sqrt(0.5) ≈ 0.707 for both — equal-power crossover.
        var frames = DJTransitions.renderFrames(\fullCrossfade, 2, 100);
        var mid = frames[100][\frame];  // index 100 of 201, t = 0.5
        this.assertFloatEquals(mid[\a][\level], 0.5.sqrt, "A at √0.5", 0.01);
        this.assertFloatEquals(mid[\b][\level], 0.5.sqrt, "B at √0.5", 0.01);
    }

    test_bass_swap_only_touches_lo {
        var frames = DJTransitions.renderFrames(\bassSwap, 1, 20);
        var mid = frames[10][\frame];
        this.assert(mid[\a].keys.asArray == [\lo], "A frame only sets lo");
        this.assert(mid[\b].keys.asArray == [\lo], "B frame only sets lo");
    }

    test_bass_swap_lo_crosses_at_midpoint {
        var frames = DJTransitions.renderFrames(\bassSwap, 2, 100);
        var mid = frames[100][\frame];
        this.assertFloatEquals(mid[\a][\lo], 0.5, "A.lo at midpoint");
        this.assertFloatEquals(mid[\b][\lo], 0.5, "B.lo at midpoint");
    }

    test_filter_sweep_endpoints {
        var frames = DJTransitions.renderFrames(\filterSweep, 2, 50);
        var first = frames.first[\frame];
        var last  = frames.last[\frame];
        this.assertFloatEquals(first[\a][\cutoff], 18000, "A starts open");
        this.assertFloatEquals(last[\a][\cutoff], 400, "A ends closed", 1);
        this.assertFloatEquals(first[\b][\cutoff], 400, "B starts closed");
        this.assertFloatEquals(last[\b][\cutoff], 18000, "B ends open", 1);
    }

    test_filter_sweep_uses_log_curve {
        // At t = 0.5, log sweep through (400, 18000) → geometric mean √(400×18000) ≈ 2683
        var frames = DJTransitions.renderFrames(\filterSweep, 2, 100);
        var mid = frames[100][\frame];
        var expected = (400 * 18000).sqrt;
        this.assertFloatEquals(mid[\b][\cutoff], expected, "B midpoint = geomean", 5);
    }

    test_doublestop_holds_levels_until_late {
        var frames = DJTransitions.renderFrames(\doublestop, 1, 100);
        // At t = 0.5 both decks still near full
        this.assertFloatEquals(frames[50][\frame][\a][\level], 1.0, "A held");
        // At t = 1.0 A is silent
        this.assertFloatEquals(frames.last[\frame][\a][\level], 0.0, "A killed");
    }

    test_scribble_alternates_between_decks {
        var frames = DJTransitions.renderFrames(\scribble, 1, 200);
        var sawDifferentChoices = false;
        var lastDominant = nil;
        frames.do({ |entry|
            var f = entry[\frame];
            var dominant = if (f[\b][\level] > f[\a][\level]) { \b } { \a };
            if (lastDominant.notNil and: { lastDominant != dominant }) {
                sawDifferentChoices = true;
            };
            lastDominant = dominant;
        });
        this.assert(sawDifferentChoices, "scribble alternates between decks");
    }

    test_subclass_curve_returns_event {
        var f = DJTransitionFullCrossfade.curve(0.5);
        this.assert(f.notNil, "curve returns an event");
        this.assert(f[\a].notNil, "has A side");
        this.assert(f[\b].notNil, "has B side");
    }
}
