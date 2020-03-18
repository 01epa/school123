package com.journal.nn.school123.rest.login;

import androidx.annotation.NonNull;

import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.util.HashMap;
import java.util.Map;

public class Auth extends AbstractPostRequest {
    private String userId;

    public Auth(@NonNull RequestParameters requestParameters,
                @NonNull String userId) {
        super("/auth",
                requestParameters,
                requestParameters.getSuccessListener());
        this.userId = userId;
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("act", "1");
        params.put("uId", userId);
        return params;
    }
}
