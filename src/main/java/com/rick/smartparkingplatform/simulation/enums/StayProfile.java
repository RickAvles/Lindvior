package com.rick.smartparkingplatform.simulation.enums;

import lombok.Getter;

@Getter
public enum StayProfile {

    SHORT(StayCurve.SHORT),

    NORMAL(StayCurve.NORMAL),

    LONG(StayCurve.LONG),

    VERY_LONG(StayCurve.VERY_LONG);

    private final StayCurve stayCurve;

    StayProfile(StayCurve stayCurve) {
        this.stayCurve = stayCurve;
    }

}