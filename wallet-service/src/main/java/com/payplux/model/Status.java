package com.payplux.model;

public enum Status {

    ACTIVE,
    CAPTURED,   // Means the hold has been converted into an  actual debit
    RELEASED,  // Means the hold is canceled and money is free again
}
