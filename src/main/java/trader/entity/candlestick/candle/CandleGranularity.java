package trader.entity.candlestick.candle;

public enum CandleGranularity {

    S5 {
        public long toSeconds() {
            return 5L;
        }
    },
    S10 {
        public long toSeconds() {
            return 10L;
        }
    },
    S15 {
        public long toSeconds() {
            return 15L;
        }
    },
    S30 {
        public long toSeconds() {
            return 30L;
        }
    },
    M1 {
        public long toSeconds() {
            return 60L;
        }
    },
    M2 {
        public long toSeconds() {
            return 2 * MINUTE;
        }
    },
    M4 {
        public long toSeconds() {
            return 4 * MINUTE;
        }
    },
    M5 {
        public long toSeconds() {
            return 5 * MINUTE;
        }
    },
    M10 {
        public long toSeconds() {
            return 10 * MINUTE;
        }
    },
    M15 {
        public long toSeconds() {
            return 15 * MINUTE;
        }
    },
    M30 {
        public long toSeconds() {
            return 30 * MINUTE;
        }
    },
    H1 {
        public long toSeconds() {
            return HOUR;
        }
    },
    H2 {
        public long toSeconds() {
            return 2 * HOUR;
        }
    },
    H3 {
        public long toSeconds() {
            return 3 * HOUR;
        }
    },
    H4 {
        public long toSeconds() {
            return 4 * HOUR;
        }
    },
    H6 {
        public long toSeconds() {
            return 6 * HOUR;
        }
    },
    H8 {
        public long toSeconds() {
            return 8 * HOUR;
        }
    },
    H12 {
        public long toSeconds() {
            return 12 * HOUR;
        }
    },
    D {
        public long toSeconds() {
            return DAY;
        }
    },
    W {
        public long toSeconds() {
            return 7 * DAY;
        }
    },
    M {
        public long toSeconds() {
            return 30 * DAY;
        }
    };

    public abstract long toSeconds();

    private static final int DAY = 86400;
    private static final long MINUTE = 60L;
    private static final long HOUR = 3600L;
}