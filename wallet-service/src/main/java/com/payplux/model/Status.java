package com.payplux.model;

public enum Status {

    ACTIVE,

    CAPTURED,   // Hold converted into actual debit

    RELEASED,   // Hold manually released/cancelled

    EXPIRED     // Hold expired automatically by scheduler
}