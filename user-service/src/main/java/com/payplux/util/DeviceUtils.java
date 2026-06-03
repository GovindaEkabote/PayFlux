package com.payplux.util;

import jakarta.servlet.http.HttpServletRequest;

public class DeviceUtils {

    public static String getDeviceInfo(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
