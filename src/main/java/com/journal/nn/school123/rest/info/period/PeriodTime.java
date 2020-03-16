package com.journal.nn.school123.rest.info.period;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.journal.nn.school123.pojo.Data;
import com.journal.nn.school123.pojo.Period;
import com.journal.nn.school123.rest.AbstractPostRequest;
import com.journal.nn.school123.rest.RequestParameters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Map;

import static com.journal.nn.school123.util.VersionUtil.getJsonSize;

public class PeriodTime extends AbstractPostRequest {
    public PeriodTime(@NonNull RequestParameters requestParameters) {
        super("/act/GET_JOURNAL_PERIODS_INFO",
                requestParameters,
                response -> {
                    response = response.replace("new Date(", "");
                    response = response.replace(")", "");
                    JsonArray jsonArray = requestParameters.getGson().fromJson(response, JsonArray.class);
                    Map<String, Period> periods = requestParameters.getData().getPeriods();
                    for (JsonElement jsonElement : jsonArray) {
                        JsonArray periodData = jsonElement.getAsJsonArray();
                        if (periodData.size() == getJsonSize(requestParameters, PeriodTime.class)) {
                            Period period = periods.get(periodData.get(0).getAsString());
                            if (period != null) {
                                Calendar from = Calendar.getInstance();
                                from.set(periodData.get(1).getAsInt(),
                                        periodData.get(2).getAsInt(),
                                        periodData.get(3).getAsInt());
                                from.set(Calendar.HOUR_OF_DAY, 0);
                                from.set(Calendar.MINUTE, 0);
                                from.set(Calendar.SECOND, 0);
                                from.set(Calendar.MILLISECOND, 0);
                                period.setFrom(from.getTime());
                                Calendar to = Calendar.getInstance();
                                to.set(periodData.get(8).getAsInt(),
                                        periodData.get(9).getAsInt(),
                                        periodData.get(10).getAsInt(),
                                        periodData.get(11).getAsInt(),
                                        periodData.get(12).getAsInt(),
                                        periodData.get(13).getAsInt());
                                to.set(Calendar.HOUR_OF_DAY, 0);
                                to.set(Calendar.MINUTE, 0);
                                to.set(Calendar.SECOND, 0);
                                to.set(Calendar.MILLISECOND, 0);
                                period.setTo(to.getTime());
                            }
                        } else {
                            System.out.println("Too many data in period time=" + periodData);
                        }
                    }
                    ClassPeriod classPeriod = new ClassPeriod(requestParameters);
                    requestParameters.getRequestQueue().add(classPeriod);
                }
        );
    }

    public byte[] getBody() {
        String paramsEncoding = getParamsEncoding();
        StringBuilder encodedParams = new StringBuilder();
        try {
            Data data = requestParameters.getData();
            for (String id : data.getPeriods().keySet()) {
                addParam(paramsEncoding, encodedParams, "ids", id);
            }
            addParam(paramsEncoding, encodedParams, "uchYear", data.getCurrentYear());
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (Exception e) {
            throw new RuntimeException("Got exception during params encoding", e);
        }
    }

    private void addParam(String paramsEncoding,
                          StringBuilder encodedParams,
                          String key,
                          String value) throws UnsupportedEncodingException {
        encodedParams.append(URLEncoder.encode(key, paramsEncoding));
        encodedParams.append('=');
        encodedParams.append(URLEncoder.encode(value, paramsEncoding));
        encodedParams.append('&');
    }
}
