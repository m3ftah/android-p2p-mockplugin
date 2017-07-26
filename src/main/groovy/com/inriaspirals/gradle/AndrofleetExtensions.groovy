package com.inriaspirals.gradle


class AndrofleetExtensions {

    Integer nodes
    Integer androidVersion
    String androfleetPath
    Integer dataExchangePort
    String displayType

    AndrofleetExtensions() {
        nodes = -1
        androidVersion = -1
        androfleetPath = null
        dataExchangePort = -1
        displayType = "pretty"
    }
}
