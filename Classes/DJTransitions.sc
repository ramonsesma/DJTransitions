// Facade: pick a transition style by Symbol and run it. Keeps the
// caller decoupled from the specific subclass names.
//
//   DJTransitions.play(\bassSwap, deckA, deckB, 4);

DJTransitions {
    classvar <styles;

    *initClass {
        styles = IdentityDictionary[
            \bassSwap      -> DJTransitionBassSwap,
            \fullCrossfade -> DJTransitionFullCrossfade,
            \filterSweep   -> DJTransitionFilterSweep,
            \doublestop    -> DJTransitionDoublestop,
            \scribble      -> DJTransitionScribble
        ];
    }

    *names {
        ^styles.keys.asArray.sort
    }

    *classFor { |style|
        ^styles[style.asSymbol]
    }

    *play { |style, synthA, synthB, durationSec = 4, clock, fps = 30|
        var cls = this.classFor(style);
        if (cls.isNil) {
            Error("Unknown DJ transition style: " ++ style).throw;
        };
        ^cls.play(synthA, synthB, durationSec, clock, fps)
    }

    *renderFrames { |style, durationSec = 4, fps = 30|
        var cls = this.classFor(style);
        if (cls.isNil) {
            Error("Unknown DJ transition style: " ++ style).throw;
        };
        ^cls.renderFrames(durationSec, fps)
    }
}
