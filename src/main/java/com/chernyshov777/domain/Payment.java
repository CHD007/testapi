package com.chernyshov777.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @NotNull
    private String intent;
    @NotNull
    @JsonProperty("notification_url")
    private String notificationUrl;
    @Valid
    private Payer payer;
    @Valid
    private Transaction transaction;
}
